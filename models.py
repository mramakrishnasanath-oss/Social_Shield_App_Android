import os
import datetime
from bson import ObjectId
from pymongo import MongoClient
import logging

logger = logging.getLogger(__name__)

# Mock database for fallback
class MockCollection:
    def __init__(self, name):
        self.name = name
        self.data = {}
        self.counter = 0

    def insert_one(self, document):
        doc = document.copy()
        if '_id' not in doc:
            self.counter += 1
            doc['_id'] = str(self.counter)
        elif isinstance(doc['_id'], ObjectId):
            doc['_id'] = str(doc['_id'])
        
        self.data[doc['_id']] = doc
        return type('InsertResult', (), {'inserted_id': doc['_id']})()

    def find_one(self, filter_query):
        for doc in self.data.values():
            match = True
            for k, v in filter_query.items():
                if k == '_id' and isinstance(v, ObjectId):
                    v = str(v)
                if doc.get(k) != v:
                    match = False
                    break
            if match:
                return doc
        return None

    def find(self, filter_query=None):
        filter_query = filter_query or {}
        results = []
        for doc in self.data.values():
            match = True
            for k, v in filter_query.items():
                if k == '_id' and isinstance(v, ObjectId):
                    v = str(v)
                if doc.get(k) != v:
                    match = False
                    break
            if match:
                results.append(doc)
        
        # Mock cursor behavior
        class MockCursor:
            def __init__(self, items):
                self.items = items
            def __iter__(self):
                return iter(self.items)
            def sort(self, key, direction=-1):
                reverse = True if direction == -1 else False
                self.items.sort(key=lambda x: x.get(key, ''), reverse=reverse)
                return self
            def limit(self, count):
                self.items = self.items[:count]
                return self
        return MockCursor(results)

    def update_one(self, filter_query, update_data):
        doc = self.find_one(filter_query)
        if not doc:
            return type('UpdateResult', (), {'modified_count': 0, 'matched_count': 0})()
        
        # Handle $set, $push, etc.
        for op, data in update_data.items():
            if op == '$set':
                for k, v in data.items():
                    doc[k] = v
            elif op == '$push':
                for k, v in data.items():
                    if k not in doc:
                        doc[k] = []
                    doc[k].append(v)
        
        self.data[doc['_id']] = doc
        return type('UpdateResult', (), {'modified_count': 1, 'matched_count': 1})()

    def delete_one(self, filter_query):
        doc = self.find_one(filter_query)
        if doc:
            del self.data[doc['_id']]
            return type('DeleteResult', (), {'deleted_count': 1})()
        return type('DeleteResult', (), {'deleted_count': 0})()

    def count_documents(self, filter_query=None):
        filter_query = filter_query or {}
        count = 0
        for doc in self.data.values():
            match = True
            for k, v in filter_query.items():
                if doc.get(k) != v:
                    match = False
                    break
            if match:
                count += 1
        return count


class MockDatabase:
    def __init__(self):
        self.collections = {}

    def __getitem__(self, name):
        if name not in self.collections:
            self.collections[name] = MockCollection(name)
        return self.collections[name]


# Connection setup
db_instance = None
use_mock = False

def init_db():
    global db_instance, use_mock
    mongo_uri = os.environ.get('MONGO_URI') or os.environ.get('MONGODB_URI')
    
    if not mongo_uri:
        logger.warning("MONGO_URI not configured. Falling back to in-memory database simulation.")
        db_instance = MockDatabase()
        use_mock = True
        return db_instance

    try:
        # Set a short connection timeout so it doesn't hang the app if server is unreachable
        client = MongoClient(mongo_uri, serverSelectionTimeoutMS=4000)
        # Force a connection check
        client.server_info()
        db_instance = client.get_default_database() or client['socialshield']
        logger.info("Successfully connected to MongoDB Atlas!")
        use_mock = False
    except Exception as e:
        logger.error(f"Failed to connect to MongoDB Atlas: {e}. Falling back to in-memory database simulation.")
        db_instance = MockDatabase()
        use_mock = True
    
    return db_instance

def get_db():
    global db_instance
    if db_instance is None:
        return init_db()
    return db_instance

def is_mock():
    return use_mock
