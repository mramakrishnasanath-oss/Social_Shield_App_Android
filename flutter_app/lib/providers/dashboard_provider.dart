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
  
  // Safe / Fake Result Dashboard extensions
  final int totalSafeProfiles;
  final List<ScanResultModel> recentSafeProfiles;
  final int totalFakeProfiles;
  final List<ScanResultModel> recentFakeProfiles;

  // Breakdown of counts by media type
  final int totalImageScans;
  final int totalUrlScans;
  final int totalTextScans;
  final int totalVideoScans;

  DashboardData({
    required this.securityScore,
    required this.totalScans,
    required this.threatCount,
    required this.recentScans,
    required this.recentThreats,
    required this.totalSafeProfiles,
    required this.recentSafeProfiles,
    required this.totalFakeProfiles,
    required this.recentFakeProfiles,
    required this.totalImageScans,
    required this.totalUrlScans,
    required this.totalTextScans,
    required this.totalVideoScans,
  });
}

// Provider used to trigger a refresh of the dashboard (no-op since it is real-time now, kept for backward compatibility)
final dashboardRefreshProvider = StateProvider<int>((ref) => 0);

final dashboardProvider = Provider<AsyncValue<DashboardData>>((ref) {
  final userProfileAsync = ref.watch(userProfileProvider);
  final scanHistoryAsync = ref.watch(scanHistoryProvider);

  if (userProfileAsync.isLoading || scanHistoryAsync.isLoading) {
    return const AsyncValue.loading();
  }

  if (userProfileAsync.hasError) {
    return AsyncValue.error(userProfileAsync.error!, userProfileAsync.stackTrace!);
  }
  if (scanHistoryAsync.hasError) {
    return AsyncValue.error(scanHistoryAsync.error!, scanHistoryAsync.stackTrace!);
  }

  final userProfile = userProfileAsync.value;
  final scanHistory = scanHistoryAsync.value ?? [];

  // Calculate threats and breakdowns from history stream in real-time
  final threats = scanHistory.where((scan) => scan.isFake || scan.isSuspicious).toList();
  final safeProfiles = scanHistory.where((scan) => scan.isSafe).toList();

  final imageScans = scanHistory.where((scan) => scan.mediaType == 'IMAGE').length;
  final urlScans = scanHistory.where((scan) => scan.mediaType == 'URL').length;
  final textScans = scanHistory.where((scan) => scan.mediaType == 'TEXT').length;
  final videoScans = scanHistory.where((scan) => scan.mediaType == 'VIDEO').length;
  
  // Map recent threats to ThreatModel
  final recentThreats = threats.take(5).map((scan) => ThreatModel(
    id: scan.scanId,
    type: scan.mediaType,
    title: scan.isFake ? 'Deepfake Detected' : 'Suspicious Content',
    description: scan.explanations.isNotEmpty ? scan.explanations.first : 'Potential fraud detected',
    severity: scan.riskLevel,
    timestamp: scan.timestamp,
  )).toList();

  // Dynamically calculate security score based on threat ratio (with 100 as default/max)
  final int calculatedSecurityScore = scanHistory.isEmpty 
      ? 100 
      : (100 - (threats.length / scanHistory.length * 100)).round().clamp(0, 100);

  return AsyncValue.data(DashboardData(
    securityScore: userProfile?.securityScore ?? calculatedSecurityScore,
    totalScans: scanHistory.length,
    threatCount: threats.length,
    recentScans: scanHistory.take(5).toList(),
    recentThreats: recentThreats,
    totalSafeProfiles: safeProfiles.length,
    recentSafeProfiles: safeProfiles.take(5).toList(),
    totalFakeProfiles: threats.length,
    recentFakeProfiles: threats.take(5).toList(),
    totalImageScans: imageScans,
    totalUrlScans: urlScans,
    totalTextScans: textScans,
    totalVideoScans: videoScans,
  ));
});
