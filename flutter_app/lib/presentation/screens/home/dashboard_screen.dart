import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../core/theme/app_colors.dart';
import '../../../providers/dashboard_provider.dart';
import '../../../providers/auth_provider.dart';
import '../../../services/localization_service.dart';
import '../../widgets/common/glass_card.dart';
import '../../widgets/common/verdict_badge.dart';

class DashboardScreen extends ConsumerWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final dashboardAsyncValue = ref.watch(dashboardProvider);
    final user = ref.watch(userProfileProvider).value;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      body: CustomScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        slivers: [
          // Custom App Bar Header
          SliverAppBar(
            expandedHeight: 120.0,
            floating: true,
            pinned: true,
            backgroundColor: isDark ? AppColors.darkBg.withOpacity(0.9) : AppColors.lightBg.withOpacity(0.9),
            flexibleSpace: FlexibleSpaceBar(
              titlePadding: const EdgeInsets.only(left: 24, bottom: 16),
              title: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '${Trans.of(context, 'hello')}, ${user?.displayName ?? 'Guardian'}',
                    style: TextStyle(
                      fontSize: 12, 
                      color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary
                    ),
                  ),
                  Text(
                    Trans.of(context, 'security_dashboard'),
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                  ),
                ],
              ),
            ),
            actions: [
              IconButton(
                icon: Icon(
                  Icons.settings_rounded, 
                  color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary
                ),
                onPressed: () => context.push('/settings'),
              ),
              const SizedBox(width: 12),
            ],
          ),
          
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
              child: dashboardAsyncValue.when(
                loading: () => const Center(
                  child: Padding(
                    padding: EdgeInsets.all(40.0),
                    child: CircularProgressIndicator(),
                  ),
                ),
                error: (err, _) => Center(
                  child: Padding(
                    padding: const EdgeInsets.all(24.0),
                    child: Text('Error: $err'),
                  ),
                ),
                data: (data) => Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Security Score Card
                    GlassCard(
                      padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 24),
                      color: isDark ? AppColors.darkCard.withOpacity(0.8) : Colors.white.withOpacity(0.8),
                      border: Border.all(
                        color: (isDark ? AppColors.darkPrimary : AppColors.lightPrimary).withOpacity(0.3)
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                Trans.of(context, 'device_security_score'),
                                style: TextStyle(
                                  color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                                  fontSize: 14,
                                  fontWeight: FontWeight.w500
                                ),
                              ),
                              const SizedBox(height: 8),
                              Text(
                                data.securityScore > 80 ? 'EXCELLENT' : data.securityScore > 55 ? 'SECURE' : 'ACTION REQUIRED',
                                style: TextStyle(
                                  fontSize: 16,
                                  fontWeight: FontWeight.bold,
                                  color: data.securityScore > 80 ? AppColors.riskLow : 
                                         data.securityScore > 50 ? AppColors.riskMedium : AppColors.riskHigh
                                ),
                              ),
                            ],
                          ),
                          Stack(
                            alignment: Alignment.center,
                            children: [
                              SizedBox(
                                height: 80,
                                width: 80,
                                child: CircularProgressIndicator(
                                  value: data.securityScore / 100,
                                  strokeWidth: 8,
                                  backgroundColor: isDark ? Colors.white10 : Colors.black.withOpacity(0.1),
                                  valueColor: AlwaysStoppedAnimation<Color>(
                                    data.securityScore > 80 ? AppColors.riskLow : 
                                    data.securityScore > 50 ? AppColors.riskMedium : AppColors.riskHigh
                                  ),
                                ),
                              ),
                              Text(
                                '${data.securityScore}',
                                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ).animate().fadeIn(duration: 500.ms).slideY(begin: 0.1, end: 0),
                    
                    const SizedBox(height: 24),
                    
                    // Main Grid Statistics
                    Row(
                      children: [
                        Expanded(
                          child: _StatCard(
                            title: Trans.of(context, 'total_scans'),
                            value: '${data.totalScans}',
                            icon: Icons.document_scanner,
                            color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: _StatCard(
                            title: Trans.of(context, 'safe_profiles'),
                            value: '${data.totalSafeProfiles}',
                            icon: Icons.check_circle_outline_rounded,
                            color: AppColors.riskLow,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: _StatCard(
                            title: Trans.of(context, 'fake_profiles'),
                            value: '${data.totalFakeProfiles}',
                            icon: Icons.dangerous_outlined,
                            color: AppColors.riskHigh,
                          ),
                        ),
                      ],
                    ).animate().fadeIn(delay: 150.ms, duration: 500.ms).slideY(begin: 0.1, end: 0),
                    
                    const SizedBox(height: 12),

                    // Media Breakdown Statistics Row
                    Row(
                      children: [
                        Expanded(
                          child: _StatCard(
                            title: 'Images',
                            value: '${data.totalImageScans}',
                            icon: Icons.image,
                            color: Colors.blueAccent,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: _StatCard(
                            title: 'URLs',
                            value: '${data.totalUrlScans}',
                            icon: Icons.link,
                            color: Colors.orangeAccent,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: _StatCard(
                            title: 'Texts',
                            value: '${data.totalTextScans}',
                            icon: Icons.text_snippet,
                            color: Colors.greenAccent,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: _StatCard(
                            title: 'Videos',
                            value: '${data.totalVideoScans}',
                            icon: Icons.videocam,
                            color: Colors.purpleAccent,
                          ),
                        ),
                      ],
                    ).animate().fadeIn(delay: 200.ms, duration: 500.ms).slideY(begin: 0.1, end: 0),

                    const SizedBox(height: 24),

                    // FAKE PROFILES (Red Warning Indicators)
                    if (data.recentFakeProfiles.isNotEmpty) ...[
                      Row(
                        children: [
                          const Icon(Icons.warning_amber_rounded, color: AppColors.riskHigh),
                          const SizedBox(width: 8),
                          Text(
                            Trans.of(context, 'fake_profiles'),
                            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                              color: AppColors.riskHigh,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      ListView.separated(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: data.recentFakeProfiles.length,
                        separatorBuilder: (context, index) => const SizedBox(height: 10),
                        itemBuilder: (context, index) {
                          final scan = data.recentFakeProfiles[index];
                          return _buildScanRow(context, scan, AppColors.riskHigh);
                        },
                      ).animate().fadeIn(duration: 400.ms),
                      const SizedBox(height: 24),
                    ],

                    // SAFE PROFILES (Green Status Indicators)
                    if (data.recentSafeProfiles.isNotEmpty) ...[
                      Row(
                        children: [
                          const Icon(Icons.check_circle_outline_rounded, color: AppColors.riskLow),
                          const SizedBox(width: 8),
                          Text(
                            Trans.of(context, 'safe_profiles'),
                            style: Theme.of(context).textTheme.titleMedium?.copyWith(
                              fontWeight: FontWeight.bold,
                              color: AppColors.riskLow,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      ListView.separated(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: data.recentSafeProfiles.length,
                        separatorBuilder: (context, index) => const SizedBox(height: 10),
                        itemBuilder: (context, index) {
                          final scan = data.recentSafeProfiles[index];
                          return _buildScanRow(context, scan, AppColors.riskLow);
                        },
                      ).animate().fadeIn(duration: 400.ms),
                      const SizedBox(height: 24),
                    ],
                    
                    // GENERAL RECENT ACTIVITY
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          Trans.of(context, 'recent_activity'),
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
                        ),
                        TextButton(
                          onPressed: () => context.go('/home/analytics'),
                          child: Text(
                            Trans.of(context, 'view_all'), 
                            style: TextStyle(
                              color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary
                            )
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    
                    if (data.recentScans.isEmpty)
                      Center(
                        child: Padding(
                          padding: const EdgeInsets.all(32.0),
                          child: Text(
                            Trans.of(context, 'no_recent_scans'), 
                            style: TextStyle(
                              color: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted
                            )
                          ),
                        ),
                      )
                    else
                      ListView.separated(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: data.recentScans.length,
                        separatorBuilder: (context, index) => const SizedBox(height: 12),
                        itemBuilder: (context, index) {
                          final scan = data.recentScans[index];
                          final isDarkTheme = Theme.of(context).brightness == Brightness.dark;
                          final statusColor = scan.isSafe ? AppColors.riskLow : 
                                              scan.isSuspicious ? AppColors.riskMedium : AppColors.riskHigh;
                          return _buildScanRow(context, scan, statusColor);
                        },
                      ).animate().fadeIn(delay: 300.ms, duration: 500.ms),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildScanRow(BuildContext context, dynamic scan, Color statusColor) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return GlassCard(
      onTap: () => context.push('/result', extra: scan),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: statusColor.withOpacity(0.1),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Icon(
              scan.mediaType == 'IMAGE' ? Icons.image_rounded : 
              scan.mediaType == 'URL' ? Icons.link_rounded : 
              scan.mediaType == 'VIDEO' ? Icons.videocam_rounded : Icons.text_snippet_rounded,
              color: statusColor,
              size: 20,
            ),
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '${Trans.of(context, 'analyzed')} ${scan.mediaType}',
                  style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
                ),
                const SizedBox(height: 2),
                Text(
                  '${Trans.of(context, 'confidence')}: ${scan.confidence}% • ${scan.timestamp.toString().substring(0, 10)}',
                  style: TextStyle(
                    fontSize: 11, 
                    color: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted
                  ),
                ),
              ],
            ),
          ),
          VerdictBadge(verdict: scan.verdict),
        ],
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const _StatCard({required this.title, required this.value, required this.icon, required this.color});

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return GlassCard(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: color, size: 18),
              const SizedBox(width: 6),
              Expanded(
                child: Text(
                  title,
                  style: TextStyle(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary, 
                    fontSize: 10,
                    fontWeight: FontWeight.w500
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            value,
            style: TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.bold,
              color: color
            ),
          ),
        ],
      ),
    );
  }
}
