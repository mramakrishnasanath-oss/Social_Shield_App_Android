import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/scan_result_model.dart';
import '../models/threat_model.dart';
import 'auth_provider.dart';
import 'scan_provider.dart';

class DashboardData {
  final int securityScore;
  final int totalScans;
  final int threatCount;
  final List<ScanResultModel> recentScans;
  final List<ThreatModel> recentThreats;

  DashboardData({
    required this.securityScore,
    required this.totalScans,
    required this.threatCount,
    required this.recentScans,
    required this.recentThreats,
  });
}

// Provider used to trigger a refresh of the dashboard
final dashboardRefreshProvider = StateProvider<int>((ref) => 0);

final dashboardProvider = FutureProvider<DashboardData>((ref) async {
  // Watch the refresh provider so we can force re-fetch
  ref.watch(dashboardRefreshProvider);
  
  final userProfile = ref.watch(userProfileProvider).value;
  final scanHistory = ref.watch(scanHistoryProvider).value ?? [];
  
  // Calculate threats from history
  final threats = scanHistory.where((scan) => scan.isFake || scan.isSuspicious).toList();
  
  // Map recent threats to ThreatModel
  final recentThreats = threats.take(5).map((scan) => ThreatModel(
    id: scan.scanId,
    type: scan.mediaType,
    title: scan.isFake ? 'Deepfake Detected' : 'Suspicious Content',
    description: scan.explanations.isNotEmpty ? scan.explanations.first : 'Potential fraud detected',
    severity: scan.riskLevel,
    timestamp: scan.timestamp,
  )).toList();

  return DashboardData(
    securityScore: userProfile?.securityScore ?? 100,
    totalScans: userProfile?.scansCount ?? scanHistory.length,
    threatCount: userProfile?.threatsDetected ?? threats.length,
    recentScans: scanHistory.take(5).toList(),
    recentThreats: recentThreats,
  );
});
