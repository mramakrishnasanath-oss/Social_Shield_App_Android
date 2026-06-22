import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';

class ScanResultModel {
  final String scanId;
  final String userId;
  final String mediaType;
  final String verdict;
  final double confidence;
  final double fakeProbability;
  final double realProbability;
  final String riskLevel;
  final List<String> explanations;
  final Map<String, dynamic>? metadata;
  final DateTime timestamp;

  ScanResultModel({
    required this.scanId,
    required this.userId,
    required this.mediaType,
    required this.verdict,
    required this.confidence,
    required this.fakeProbability,
    required this.realProbability,
    required this.riskLevel,
    required this.explanations,
    this.metadata,
    required this.timestamp,
  });

  factory ScanResultModel.fromJson(Map<String, dynamic> json) {
    double fakeProb = (json['fake_probability'] as num?)?.toDouble() ?? 0.0;
    double realProb = (json['real_probability'] as num?)?.toDouble() ?? 0.0;

    // Auto-normalize fractions <= 1.0 to percentage (0-100%)
    if (fakeProb <= 1.0 && realProb <= 1.0 && (fakeProb > 0.0 || realProb > 0.0)) {
      fakeProb *= 100.0;
      realProb *= 100.0;
    }

    return ScanResultModel(
      scanId: json['scan_id'] as String? ?? '',
      userId: json['user_id'] as String? ?? '',
      mediaType: json['media_type'] as String? ?? 'UNKNOWN',
      verdict: json['verdict'] as String? ?? 'UNKNOWN',
      confidence: (json['confidence'] as num?)?.toDouble() ?? 0.0,
      fakeProbability: fakeProb,
      realProbability: realProb,
      riskLevel: json['risk_level'] as String? ?? 'LOW',
      explanations: (json['explanations'] as List<dynamic>?)?.map((e) => e as String).toList() ?? [],
      metadata: json['metadata'] as Map<String, dynamic>?,
      timestamp: json['timestamp'] != null 
          ? DateTime.parse(json['timestamp'] as String) 
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'scan_id': scanId,
      'user_id': userId,
      'media_type': mediaType,
      'verdict': verdict,
      'confidence': confidence,
      'fake_probability': fakeProbability,
      'real_probability': realProbability,
      'risk_level': riskLevel,
      'explanations': explanations,
      'metadata': metadata,
      'timestamp': timestamp.toIso8601String(),
    };
  }

  bool get isFake => verdict.toUpperCase() == 'FAKE';
  bool get isSuspicious => verdict.toUpperCase() == 'SUSPICIOUS';
  bool get isReal => verdict.toUpperCase() == 'REAL' || verdict.toUpperCase() == 'SAFE';
  bool get isSafe => isReal;

  Color get verdictColor {
    if (isFake) return AppColors.riskHigh;
    if (isSuspicious) return AppColors.riskMedium;
    if (isReal) return AppColors.riskLow;
    return Colors.grey;
  }

  IconData get verdictIcon {
    if (isFake) return Icons.warning_amber_rounded;
    if (isSuspicious) return Icons.error_outline_rounded;
    if (isReal) return Icons.check_circle_outline_rounded;
    return Icons.help_outline_rounded;
  }
}
