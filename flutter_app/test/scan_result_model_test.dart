import 'package:flutter_test/flutter_test.dart';
import 'package:socialshield/models/scan_result_model.dart';

void main() {
  group('ScanResultModel Tests', () {
    test('fromJson auto-normalizes fractional probabilities to percentage scale', () {
      final json = {
        'scan_id': 'test-123',
        'user_id': 'user-456',
        'media_type': 'IMAGE',
        'verdict': 'FAKE',
        'confidence': 88.0,
        'fake_probability': 0.88,
        'real_probability': 0.12,
        'risk_level': 'HIGH',
        'explanations': ['Deepfake detected in eyes', 'Mismatch color space'],
        'timestamp': '2026-06-21T09:00:00.000Z'
      };

      final model = ScanResultModel.fromJson(json);

      expect(model.scanId, 'test-123');
      expect(model.fakeProbability, 88.0);
      expect(model.realProbability, 12.0);
      expect(model.isFake, true);
      expect(model.isSuspicious, false);
      expect(model.isSafe, false);
    });

    test('fromJson keeps percentage probabilities intact', () {
      final json = {
        'scan_id': 'test-124',
        'user_id': 'user-456',
        'media_type': 'TEXT',
        'verdict': 'SAFE',
        'confidence': 95.0,
        'fake_probability': 5.0,
        'real_probability': 95.0,
        'risk_level': 'LOW',
        'explanations': [],
        'timestamp': '2026-06-21T09:00:00.000Z'
      };

      final model = ScanResultModel.fromJson(json);

      expect(model.fakeProbability, 5.0);
      expect(model.realProbability, 95.0);
      expect(model.isSafe, true);
    });

    test('toJson returns correct map structure', () {
      final model = ScanResultModel(
        scanId: 'test-125',
        userId: 'user-456',
        mediaType: 'VIDEO',
        verdict: 'SUSPICIOUS',
        confidence: 65.0,
        fakeProbability: 65.0,
        realProbability: 35.0,
        riskLevel: 'MEDIUM',
        explanations: ['Video compression anomalies'],
        timestamp: DateTime.parse('2026-06-21T09:00:00.000Z'),
      );

      final json = model.toJson();

      expect(json['scan_id'], 'test-125');
      expect(json['fake_probability'], 65.0);
      expect(json['real_probability'], 35.0);
      expect(json['verdict'], 'SUSPICIOUS');
    });
  });
}
