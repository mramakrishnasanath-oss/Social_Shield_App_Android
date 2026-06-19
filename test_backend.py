import os
import unittest
import json
import tempfile
import numpy as np
import cv2

# Set env vars for testing
os.environ['FLASK_ENV'] = 'testing'
os.environ['JWT_SECRET'] = 'test-secret'
os.environ['MONGO_URI'] = '' # Force mock database fallback

from app import app
from models import get_db, is_mock

class SocialShieldBackendTests(unittest.TestCase):
    def setUp(self):
        self.app = app.test_client()
        self.app.testing = True
        
        # Clear database records
        db = get_db()
        db['users'].data = {}
        db['scans'].data = {}
        db['threats'].data = {}
        db['notifications'].data = {}

    def test_health_check(self):
        response = self.app.get('/health')
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.data)
        self.assertEqual(data['status'], 'active')
        self.assertTrue(is_mock()) # Verify mock fallback activated

    def test_auth_flow(self):
        # 1. Register User
        reg_payload = {
            'email': 'test@socialshield.ai',
            'password': 'Password123!',
            'name': 'Test User',
            'role': 'user'
        }
        reg_res = self.app.post('/api/auth/register', 
                                 data=json.dumps(reg_payload),
                                 content_type='application/json')
        self.assertEqual(reg_res.status_code, 201)
        reg_data = json.loads(reg_res.data)
        self.assertIn('user_id', reg_data)
        
        # Verify the user is registered in the DB
        db = get_db()
        self.assertEqual(db['users'].count_documents({'email': 'test@socialshield.ai'}), 1)
        
        # Set verified flag manually for testing
        db['users'].update_one({'email': 'test@socialshield.ai'}, {'$set': {'is_verified': True}})
        
        # 2. Login User
        login_payload = {
            'email': 'test@socialshield.ai',
            'password': 'Password123!'
        }
        login_res = self.app.post('/api/auth/login',
                                  data=json.dumps(login_payload),
                                  content_type='application/json')
        self.assertEqual(login_res.status_code, 200)
        login_data = json.loads(login_res.data)
        self.assertIn('token', login_data)
        token = login_data['token']

        # 3. Access Protected Route (Auth Profile Check)
        headers = {'Authorization': f'Bearer {token}'}
        me_res = self.app.get('/api/auth/me', headers=headers)
        self.assertEqual(me_res.status_code, 200)
        me_data = json.loads(me_res.data)
        self.assertEqual(me_data['user']['email'], 'test@socialshield.ai')

    def test_scanners(self):
        # Set up an authenticated session
        db = get_db()
        db['users'].insert_one({
            '_id': '123',
            'email': 'scanner_test@socialshield.ai',
            'password': 'hashed_password',
            'name': 'Scanner Test',
            'role': 'user',
            'is_verified': True
        })
        
        import auth
        token = auth.generate_token('123', 'user', 'Scanner Test', 'scanner_test@socialshield.ai')
        headers = {'Authorization': f'Bearer {token}'}
        
        # 1. Test Text Scam Scan
        text_payload = {'text': 'URGENT: Win a free million dollars now! Click here to redeem: http://free-scam-reward.xyz'}
        text_res = self.app.post('/api/scans/text',
                                 data=json.dumps(text_payload),
                                 content_type='application/json',
                                 headers=headers)
        self.assertEqual(text_res.status_code, 200)
        text_data = json.loads(text_res.data)
        self.assertEqual(text_data['risk_level'], 'High Risk')
        self.assertGreaterEqual(text_data['score'], 71)
        
        # 2. Test URL Phishing Scan
        url_payload = {'url': 'http://secure-update-facebook-credentials.net'}
        url_res = self.app.post('/api/scans/url',
                                data=json.dumps(url_payload),
                                content_type='application/json',
                                headers=headers)
        self.assertEqual(url_res.status_code, 200)
        url_data = json.loads(url_res.data)
        self.assertEqual(url_data['status'], 'Dangerous')
        self.assertGreaterEqual(url_data['score'], 71)

        # 3. Test Image Scan (creating a dummy image in memory)
        dummy_img = np.zeros((100, 100, 3), dtype=np.uint8)
        # Add some dummy structures/lines to avoid flat zero array
        cv2.line(dummy_img, (0, 0), (100, 100), (255, 255, 255), 5)
        
        with tempfile.NamedTemporaryFile(suffix='.jpg', delete=False) as f:
            cv2.imwrite(f.name, dummy_img)
            temp_path = f.name
            
        try:
            with open(temp_path, 'rb') as img_file:
                image_res = self.app.post('/api/scans/image',
                                          data={'image': img_file},
                                          content_type='multipart/form-data',
                                          headers=headers)
            self.assertEqual(image_res.status_code, 200)
            img_data = json.loads(image_res.data)
            self.assertIn('classification', img_data)
            self.assertIn('score', img_data)
        finally:
            if os.path.exists(temp_path):
                os.remove(temp_path)

if __name__ == '__main__':
    unittest.main()
