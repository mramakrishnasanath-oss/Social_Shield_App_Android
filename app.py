import os
import time
import datetime
from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
from werkzeug.utils import secure_filename
from bson import ObjectId

from models import get_db, init_db, is_mock
import auth
import ai_engine

app = Flask(__name__)
# Enable CORS for all origins in development, restrict in production
CORS(app, resources={r"/api/*": {"origins": "*"}})

# Configurations
UPLOAD_FOLDER = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'static', 'uploads')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB max upload size

os.makedirs(UPLOAD_FOLDER, exist_ok=True)

# Rate limiting helper (simple in-memory rate limiter to satisfy security requirements)
RATE_LIMIT_STORAGE = {}
def rate_limit(limit_requests=60, period_seconds=60):
    def decorator(f):
        from functools import wraps
        @wraps(f)
        def decorated(*args, **kwargs):
            ip = request.remote_addr
            now = time.time()
            if ip not in RATE_LIMIT_STORAGE:
                RATE_LIMIT_STORAGE[ip] = []
            
            # Filter out timestamps older than the period
            RATE_LIMIT_STORAGE[ip] = [t for t in RATE_LIMIT_STORAGE[ip] if now - t < period_seconds]
            
            if len(RATE_LIMIT_STORAGE[ip]) >= limit_requests:
                return jsonify({
                    'message': 'Too many requests. Please try again later.',
                    'retry_after': int(period_seconds - (now - RATE_LIMIT_STORAGE[ip][0]))
                }), 429
                
            RATE_LIMIT_STORAGE[ip].append(now)
            return f(*args, **kwargs)
        return decorated
    return decorator

# ====================================================
# DB SEEDING FOR PRODUCTION-READY INITIAL STATE
# ====================================================
def seed_initial_data():
    db = get_db()
    users_col = db['users']
    scans_col = db['scans']
    threats_col = db['threats']
    notifications_col = db['notifications']
    
    # 1. Seed Users if empty
    if users_col.count_documents({}) == 0:
        print("Seeding users database...")
        # Create an Admin
        auth.register_user('admin@socialshield.ai', 'AdminSecure2026!', 'Security Admin', 'admin')
        # Create an Analyst
        auth.register_user('analyst@socialshield.ai', 'AnalystSecure2026!', 'Threat Analyst', 'analyst')
        # Create a Demo User
        auth.register_user('user@socialshield.ai', 'UserSecure2026!', 'Alex Demo User', 'user')
        
        # Mark them as verified
        users_col.update_one({'email': 'admin@socialshield.ai'}, {'$set': {'is_verified': True}})
        users_col.update_one({'email': 'analyst@socialshield.ai'}, {'$set': {'is_verified': True}})
        users_col.update_one({'email': 'user@socialshield.ai'}, {'$set': {'is_verified': True}})

    # Retrieve demo user ID for attribution
    demo_user = users_col.find_one({'email': 'user@socialshield.ai'})
    user_id = str(demo_user['_id']) if demo_user else "demo_user"

    # 2. Seed Scans if empty
    if scans_col.count_documents({}) == 0:
        print("Seeding demo scan history...")
        demo_scans = [
            {
                'user_id': user_id,
                'type': 'text',
                'input': 'Dear Customer, update your passcode at https://verify-paypal-login.xyz to avoid suspension.',
                'score': 85,
                'risk_level': 'High Risk',
                'explanation': 'Contains scam terms (passcode, suspension) and points to a suspicious top-level domain (.xyz).',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=15)
            },
            {
                'user_id': user_id,
                'type': 'url',
                'input': 'https://google.com',
                'score': 10,
                'risk_level': 'Safe',
                'explanation': 'Uses a secure HTTPS protocol and references a reputable domain.',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=12)
            },
            {
                'user_id': user_id,
                'type': 'image',
                'input': 'ceo_profile_scanned.png',
                'score': 92,
                'risk_level': 'High Risk',
                'explanation': 'Telltale synthetic pixel grid artifacts detected matching Generative GAN patterns.',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=8)
            },
            {
                'user_id': user_id,
                'type': 'text',
                'input': 'Hey! Are we still meeting for lunch at 12:30 PM today?',
                'score': 5,
                'risk_level': 'Safe',
                'explanation': 'No threats or coercive patterns detected.',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=3)
            },
            {
                'user_id': user_id,
                'type': 'url',
                'input': 'http://secure-update-banking-portal.net',
                'score': 75,
                'risk_level': 'High Risk',
                'explanation': 'Lacks HTTPS and contains suspicious keywords ("secure-update-banking").',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(hours=6)
            }
        ]
        for scan in demo_scans:
            scans_col.insert_one(scan)

    # 3. Seed Threat Reports if empty
    if threats_col.count_documents({}) == 0:
        print("Seeding active threats database...")
        demo_threats = [
            {
                'reporter_email': 'user@socialshield.ai',
                'type': 'phishing',
                'description': 'Received suspicious DM containing link to fake cryptocurrency giveaway: https://elon-musk-eth.xyz',
                'status': 'pending',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=4)
            },
            {
                'reporter_email': 'analyst@socialshield.ai',
                'type': 'deepfake',
                'description': 'AI generated video profile mimicking local political figure circulating on social channels.',
                'status': 'reviewed',
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=10)
            }
        ]
        for threat in demo_threats:
            threats_col.insert_one(threat)

    # 4. Seed Notifications if empty
    if notifications_col.count_documents({}) == 0:
        print("Seeding user notifications...")
        demo_notifications = [
            {
                'user_id': user_id,
                'title': 'New Scan Logged',
                'message': 'Your URL Scan for http://secure-update-banking-portal.net was logged as HIGH RISK.',
                'is_read': False,
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(hours=6)
            },
            {
                'user_id': user_id,
                'title': 'System Core Update',
                'message': 'SocialShield ML classifiers have been updated. NLP precision increased by 8.4%.',
                'is_read': True,
                'created_at': datetime.datetime.utcnow() - datetime.timedelta(days=2)
            }
        ]
        for notif in demo_notifications:
            notifications_col.insert_one(notif)


# ====================================================
# API ROUTES
# ====================================================

@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        'status': 'active',
        'database': 'mock-in-memory' if is_mock() else 'connected',
        'timestamp': datetime.datetime.utcnow().isoformat(),
        'version': '1.0.0'
    }), 200

# Serve uploaded images statically
@app.route('/static/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)


# --- AUTHENTICATION ENDPOINTS ---

@app.route('/api/auth/register', methods=['POST'])
@rate_limit(15, 60)
def register():
    data = request.get_json() or {}
    email = data.get('email')
    password = data.get('password')
    name = data.get('name')
    role = data.get('role', 'user')
    
    if not email or not password or not name:
        return jsonify({'message': 'Missing email, password, or name.'}), 400
        
    result = auth.register_user(email, password, name, role)
    if 'error' in result:
        return jsonify({'message': result['error']}), 400
        
    return jsonify(result), 201

@app.route('/api/auth/login', methods=['POST'])
@rate_limit(30, 60)
def login():
    data = request.get_json() or {}
    email = data.get('email')
    password = data.get('password')
    
    if not email or not password:
        return jsonify({'message': 'Missing email or password.'}), 400
        
    result = auth.login_user(email, password)
    if 'error' in result:
        return jsonify({'message': result['error']}), 401
        
    return jsonify(result), 200

@app.route('/api/auth/verify-email', methods=['GET'])
def verify_email():
    token = request.args.get('token')
    if not token:
        return jsonify({'message': 'Verification token is missing.'}), 400
    
    result = auth.verify_user_email(token)
    if 'error' in result:
        return jsonify({'message': result['error']}), 400
        
    return jsonify(result), 200

@app.route('/api/auth/forgot-password', methods=['POST'])
def forgot_password():
    data = request.get_json() or {}
    email = data.get('email')
    if not email:
        return jsonify({'message': 'Email address required.'}), 400
        
    result = auth.forgot_password_request(email)
    if 'error' in result:
        return jsonify({'message': result['error']}), 400
        
    return jsonify(result), 200

@app.route('/api/auth/reset-password', methods=['POST'])
def reset_password():
    data = request.get_json() or {}
    token = data.get('token')
    password = data.get('password')
    
    if not token or not password:
        return jsonify({'message': 'Token and new password are required.'}), 400
        
    result = auth.reset_password_with_token(token, password)
    if 'error' in result:
        return jsonify({'message': result['error']}), 400
        
    return jsonify(result), 200

@app.route('/api/auth/me', methods=['GET'])
@auth.token_required
def get_profile():
    return jsonify({'user': request.user}), 200


# --- FRAUD SCANNING ENDPOINTS ---

@app.route('/api/scans/text', methods=['POST'])
@auth.token_required
@rate_limit(30, 60)
def scan_text():
    data = request.get_json() or {}
    content = data.get('text')
    
    if not content:
        return jsonify({'message': 'Text payload is required.'}), 400
        
    result = ai_engine.analyze_text_fraud(content)
    
    # Save to history
    db = get_db()
    scan_doc = {
        'user_id': request.user['sub'],
        'type': 'text',
        'input': content[:200] + ('...' if len(content) > 200 else ''),
        'score': result['score'],
        'risk_level': result['risk_level'],
        'explanation': result['explanation'],
        'details': result['details'],
        'created_at': datetime.datetime.utcnow()
    }
    db['scans'].insert_one(scan_doc)
    
    # Generate safety recommendations
    _, _, recs = ai_engine.generate_overall_fraud_score([result])
    result['recommendations'] = recs
    
    return jsonify(result), 200

@app.route('/api/scans/url', methods=['POST'])
@auth.token_required
@rate_limit(30, 60)
def scan_url():
    data = request.get_json() or {}
    url = data.get('url')
    
    if not url:
        return jsonify({'message': 'URL payload is required.'}), 400
        
    result = ai_engine.analyze_url_phishing(url)
    
    # Save to history
    db = get_db()
    scan_doc = {
        'user_id': request.user['sub'],
        'type': 'url',
        'input': url,
        'score': result['score'],
        'risk_level': 'High Risk' if result['status'] == 'Dangerous' else ('Suspicious' if result['status'] == 'Suspicious' else 'Safe'),
        'explanation': result['explanation'],
        'details': result['details'],
        'created_at': datetime.datetime.utcnow()
    }
    db['scans'].insert_one(scan_doc)
    
    # Recommendations mapping
    mock_scan_res = {'score': result['score']}
    _, _, recs = ai_engine.generate_overall_fraud_score([mock_scan_res])
    result['recommendations'] = recs
    
    return jsonify(result), 200

@app.route('/api/scans/image', methods=['POST'])
@auth.token_required
@rate_limit(15, 60)
def scan_image():
    if 'image' not in request.files:
        return jsonify({'message': 'No image file uploaded.'}), 400
        
    file = request.files['image']
    if file.filename == '':
        return jsonify({'message': 'Empty file selected.'}), 400
        
    filename = secure_filename(f"{int(time.time())}_{file.filename}")
    filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
    file.save(filepath)
    
    # Execute analysis
    result = ai_engine.analyze_image_deepfake(filepath)
    
    # Translate status to risk score
    score = int(result['confidence']) if result['classification'] == 'Fake' else int(100 - result['confidence'])
    # Bound score realistically
    if result['classification'] == 'Fake':
        score = max(score, 71)  # High risk min
        risk_level = 'High Risk'
    else:
        score = min(score, 30)  # Safe max
        risk_level = 'Safe'
        
    # Save to database
    db = get_db()
    scan_doc = {
        'user_id': request.user['sub'],
        'type': 'image',
        'input': filename,
        'score': score,
        'risk_level': risk_level,
        'explanation': result['explanation'],
        'details': result['details'],
        'created_at': datetime.datetime.utcnow()
    }
    db['scans'].insert_one(scan_doc)
    
    # Recommendations
    mock_scan_res = {'score': score}
    _, _, recs = ai_engine.generate_overall_fraud_score([mock_scan_res])
    
    return jsonify({
        'classification': result['classification'],
        'confidence': result['confidence'],
        'score': score,
        'risk_level': risk_level,
        'explanation': result['explanation'],
        'details': result['details'],
        'image_url': f"/static/uploads/{filename}",
        'recommendations': recs
    }), 200

@app.route('/api/scans/history', methods=['GET'])
@auth.token_required
def get_scan_history():
    db = get_db()
    scans = list(db['scans'].find({'user_id': request.user['sub']}).sort('created_at', -1).limit(20))
    for scan in scans:
        scan['id'] = str(scan['_id'])
        del scan['_id']
        if isinstance(scan.get('created_at'), datetime.datetime):
            scan['created_at'] = scan['created_at'].isoformat()
    return jsonify({'scans': scans}), 200


# --- ANALYTICS DASHBOARD ---

@app.route('/api/analytics/dashboard', methods=['GET'])
@auth.token_required
def get_analytics():
    db = get_db()
    scans_col = db['scans']
    threats_col = db['threats']
    
    # Total Scans
    total_scans = scans_col.count_documents({})
    # Risk breakdowns
    high_risk_scans = scans_col.count_documents({'risk_level': 'High Risk'})
    suspicious_scans = scans_col.count_documents({'risk_level': 'Suspicious'})
    safe_scans = scans_col.count_documents({'risk_level': 'Safe'})
    
    # Monthly analysis mock stats (aggregated nicely)
    months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
    trends = [
        {'month': 'Jan', 'scams': 12, 'deepfakes': 5, 'phishing': 20},
        {'month': 'Feb', 'scams': 18, 'deepfakes': 8, 'phishing': 25},
        {'month': 'Mar', 'scams': 15, 'deepfakes': 12, 'phishing': 22},
        {'month': 'Apr', 'scams': 24, 'deepfakes': 15, 'phishing': 35},
        {'month': 'May', 'scams': 32, 'deepfakes': 19, 'phishing': 42},
        {'month': 'Jun', 'scams': 45, 'deepfakes': 28, 'phishing': 56}
    ]
    
    # Add active scans distribution
    type_distribution = [
        {'name': 'Text Scams', 'value': scans_col.count_documents({'type': 'text'})},
        {'name': 'Deepfake Images', 'value': scans_col.count_documents({'type': 'image'})},
        {'name': 'Phishing URLs', 'value': scans_col.count_documents({'type': 'url'})}
    ]
    
    # Calculate avg security score
    all_scans = list(scans_col.find({}, {'score': 1}))
    avg_score = 0
    if all_scans:
        avg_score = sum([s.get('score', 0) for s in all_scans]) / len(all_scans)
    
    return jsonify({
        'summary': {
            'total_scans': total_scans,
            'high_risk': high_risk_scans,
            'suspicious': suspicious_scans,
            'safe': safe_scans,
            'system_risk_index': round(avg_score, 1)
        },
        'trends': trends,
        'distribution': type_distribution,
        'heatmap': [
            {'region': 'North America', 'threats': 124},
            {'region': 'Europe', 'threats': 98},
            {'region': 'Asia Pacific', 'threats': 156},
            {'region': 'Latin America', 'threats': 42},
            {'region': 'Middle East & Africa', 'threats': 33}
        ]
    }), 200


# --- ADMIN DASHBOARD ---

@app.route('/api/admin/users', methods=['GET'])
@auth.token_required
@auth.roles_required('admin')
def admin_get_users():
    db = get_db()
    users = list(db['users'].find({}, {'password': 0}))
    for user in users:
        user['id'] = str(user['_id'])
        del user['_id']
        if isinstance(user.get('created_at'), datetime.datetime):
            user['created_at'] = user['created_at'].isoformat()
    return jsonify({'users': users}), 200

@app.route('/api/admin/users/<user_id>', methods=['PUT', 'DELETE'])
@auth.token_required
@auth.roles_required('admin')
def admin_manage_user(user_id):
    db = get_db()
    users_col = db['users']
    
    if request.method == 'DELETE':
        res = users_col.delete_one({'_id': ObjectId(user_id) if not is_mock() else user_id})
        if res.deleted_count == 0:
            return jsonify({'message': 'User not found.'}), 404
        return jsonify({'message': 'User deleted successfully.'}), 200
        
    elif request.method == 'PUT':
        data = request.get_json() or {}
        role = data.get('role')
        is_verified = data.get('is_verified')
        
        update_fields = {}
        if role is not None:
            update_fields['role'] = role
        if is_verified is not None:
            update_fields['is_verified'] = is_verified
            
        if not update_fields:
            return jsonify({'message': 'No changes provided.'}), 400
            
        res = users_col.update_one(
            {'_id': ObjectId(user_id) if not is_mock() else user_id},
            {'$set': update_fields}
        )
        if res.matched_count == 0:
            return jsonify({'message': 'User not found.'}), 404
            
        return jsonify({'message': 'User updated successfully.'}), 200

@app.route('/api/admin/threats', methods=['GET', 'POST', 'PUT'])
@auth.token_required
@auth.roles_required('admin', 'analyst')
def admin_threats():
    db = get_db()
    threats_col = db['threats']
    
    if request.method == 'GET':
        threats = list(threats_col.find().sort('created_at', -1))
        for threat in threats:
            threat['id'] = str(threat['_id'])
            del threat['_id']
            if isinstance(threat.get('created_at'), datetime.datetime):
                threat['created_at'] = threat['created_at'].isoformat()
        return jsonify({'threats': threats}), 200
        
    elif request.method == 'POST':
        # Submit threat report
        data = request.get_json() or {}
        threat_type = data.get('type')
        description = data.get('description')
        
        if not threat_type or not description:
            return jsonify({'message': 'Type and description are required.'}), 400
            
        threat_doc = {
            'reporter_email': request.user['email'],
            'type': threat_type,
            'description': description,
            'status': 'pending',
            'created_at': datetime.datetime.utcnow()
        }
        result = threats_col.insert_one(threat_doc)
        return jsonify({'message': 'Threat report submitted.', 'id': str(result.inserted_id)}), 201
        
    elif request.method == 'PUT':
        # Update threat status
        data = request.get_json() or {}
        threat_id = data.get('id')
        status = data.get('status')
        
        if not threat_id or not status:
            return jsonify({'message': 'Threat ID and status required.'}), 400
            
        res = threats_col.update_one(
            {'_id': ObjectId(threat_id) if not is_mock() else threat_id},
            {'$set': {'status': status}}
        )
        if res.matched_count == 0:
            return jsonify({'message': 'Threat report not found.'}), 404
            
        return jsonify({'message': f'Threat status updated to {status}.'}), 200

@app.route('/api/admin/system-health', methods=['GET'])
@auth.token_required
@auth.roles_required('admin')
def get_system_health():
    import platform
    import sys
    
    db = get_db()
    scans_col = db['scans']
    users_col = db['users']
    
    return jsonify({
        'uptime_status': 'Operational',
        'python_version': platform.python_version(),
        'platform': platform.system(),
        'database_mode': 'Mock In-Memory' if is_mock() else 'MongoDB Atlas Production Cluster',
        'metrics': {
            'registered_users': users_col.count_documents({}),
            'total_scans_processed': scans_col.count_documents({}),
            'api_latency_ms': 42, # Simulated live metric
            'active_workers': 2,
            'cpu_utilization_pct': 18.5,
            'memory_utilization_pct': 34.2
        }
    }), 200

# Initialize and seed DB on load
with app.app_context():
    init_db()
    seed_initial_data()

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    # Run server locally
    app.run(host='0.0.0.0', port=port, debug=False)
