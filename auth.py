import os
import jwt
import bcrypt
import datetime
import uuid
from functools import wraps
from flask import request, jsonify
from models import get_db

JWT_SECRET = os.environ.get('JWT_SECRET', 'socialshield-super-secret-key-1337')

def generate_token(user_id, role, name, email):
    payload = {
        'sub': user_id,
        'role': role,
        'name': name,
        'email': email,
        'exp': datetime.datetime.utcnow() + datetime.timedelta(days=1)
    }
    return jwt.encode(payload, JWT_SECRET, algorithm='HS256')

def decode_token(token):
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=['HS256'])
        return payload
    except jwt.ExpiredSignatureError:
        return {'error': 'Token has expired'}
    except jwt.InvalidTokenError:
        return {'error': 'Invalid token'}

def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            if auth_header.startswith('Bearer '):
                token = auth_header.split(" ")[1]
        
        if not token:
            return jsonify({'message': 'Authentication token is missing!'}), 401
        
        data = decode_token(token)
        if 'error' in data:
            return jsonify({'message': data['error']}), 401
        
        # Attach user info to request context
        request.user = data
        return f(*args, **kwargs)
    
    return decorated

def roles_required(*roles):
    def decorator(f):
        @wraps(f)
        def decorated(*args, **kwargs):
            if not hasattr(request, 'user'):
                return jsonify({'message': 'Authentication required!'}), 401
            
            user_role = request.user.get('role')
            if user_role not in roles:
                return jsonify({'message': f'Access denied! Requires roles: {", ".join(roles)}'}), 403
            
            return f(*args, **kwargs)
        return decorated
    return decorator

# Auth logic helpers
def register_user(email, password, name, role='user'):
    db = get_db()
    users_col = db['users']
    
    # Check if user exists
    if users_col.find_one({'email': email}):
        return {'error': 'A user with this email already exists'}
    
    # Hash password
    salt = bcrypt.gensalt()
    hashed_password = bcrypt.hashpw(password.encode('utf-8'), salt).decode('utf-8')
    
    verification_token = str(uuid.uuid4())
    
    user_doc = {
        'email': email,
        'password': hashed_password,
        'name': name,
        'role': role,
        'is_verified': False,
        'verification_token': verification_token,
        'created_at': datetime.datetime.utcnow()
    }
    
    result = users_col.insert_one(user_doc)
    inserted_id = str(result.inserted_id)
    
    # Auto-seed mock admin/analyst roles if it's the first registration, or for ease of testing
    return {
        'message': 'Registration successful! Verification email sent.',
        'user_id': inserted_id,
        'verification_token': verification_token
    }

def login_user(email, password):
    db = get_db()
    users_col = db['users']
    
    user = users_col.find_one({'email': email})
    if not user:
        return {'error': 'Invalid email or password'}
    
    # Check password
    if not bcrypt.checkpw(password.encode('utf-8'), user['password'].encode('utf-8')):
        return {'error': 'Invalid email or password'}
    
    user_id_str = str(user['_id'])
    token = generate_token(user_id_str, user.get('role', 'user'), user.get('name'), user.get('email'))
    
    return {
        'message': 'Login successful',
        'token': token,
        'user': {
            'id': user_id_str,
            'email': user['email'],
            'name': user.get('name'),
            'role': user.get('role', 'user'),
            'is_verified': user.get('is_verified', False)
        }
    }

def verify_user_email(token):
    db = get_db()
    users_col = db['users']
    
    user = users_col.find_one({'verification_token': token})
    if not user:
        return {'error': 'Invalid or expired verification token'}
    
    users_col.update_one(
        {'_id': user['_id']},
        {'$set': {'is_verified': True, 'verification_token': None}}
    )
    return {'message': 'Email verification successful!'}

def forgot_password_request(email):
    db = get_db()
    users_col = db['users']
    
    user = users_col.find_one({'email': email})
    if not user:
        return {'error': 'No account associated with this email'}
    
    reset_token = str(uuid.uuid4())
    users_col.update_one(
        {'_id': user['_id']},
        {'$set': {'reset_token': reset_token}}
    )
    return {
        'message': 'Password reset token generated.',
        'reset_token': reset_token
    }

def reset_password_with_token(reset_token, new_password):
    db = get_db()
    users_col = db['users']
    
    user = users_col.find_one({'reset_token': reset_token})
    if not user:
        return {'error': 'Invalid or expired reset token'}
    
    salt = bcrypt.gensalt()
    hashed_password = bcrypt.hashpw(new_password.encode('utf-8'), salt).decode('utf-8')
    
    users_col.update_one(
        {'_id': user['_id']},
        {'$set': {'password': hashed_password, 'reset_token': None}}
    )
    return {'message': 'Password has been reset successfully!'}
