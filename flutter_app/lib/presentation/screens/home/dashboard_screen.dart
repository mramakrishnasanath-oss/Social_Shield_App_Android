import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../core/theme/app_colors.dart';
import '../../../providers/dashboard_provider.dart';
import '../../../providers/auth_provider.dart';
import '../../widgets/common/glass_card.dart';
import '../../widgets/common/verdict_badge.dart';

class DashboardScreen extends ConsumerWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final dashboardState = ref.watch(dashboardProvider);
    final user = ref.watch(userProfileProvider).value;

    return Scaffold(
      body: RefreshIndicator(
        color: AppColors.neonBlue,
        backgroundColor: AppColors.darkCard,
        onRefresh: () async {
          ref.read(dashboardRefreshProvider.notifier).state++;
          await ref.read(dashboardProvider.future);
        },
        child: CustomScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          slivers: [
            // Custom App Bar Header
            SliverAppBar(
              expandedHeight: 120.0,
              floating: true,
              pinned: true,
              backgroundColor: AppColors.deepBlack.withOpacity(0.9),
              flexibleSpace: FlexibleSpaceBar(
                titlePadding: const EdgeInsets.only(left: 24, bottom: 16),
                title: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Hello, \${user?.displayName ?? 'Guardian'}',
                      style: const TextStyle(fontSize: 14, color: AppColors.textSecondary),
                    ),
                    const Text(
                      'Security Dashboard',
                      style: TextStyle(fontWeight: FontWeight.bold, fontSize: 20),
                    ),
                  ],
                ),
              ),
              actions: [
                IconButton(
                  icon: const Icon(Icons.notifications_none_rounded, color: AppColors.neonBlue),
                  onPressed: () {},
                ),
                const SizedBox(width: 12),
              ],
            ),
            
            SliverToBoxAdapter(
              child: Padding(
                padding: const EdgeInsets.all(24.0),
                child: dashboardState.when(
                  loading: () => const Center(child: CircularProgressIndicator()),
                  error: (err, _) => Center(child: Text('Error: \$err')),
                  data: (data) => Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Security Score Card
                      GlassCard(
                        padding: const EdgeInsets.symmetric(vertical: 32, horizontal: 24),
                        color: AppColors.darkCard.withOpacity(0.8),
                        border: Border.all(color: AppColors.neonBlue.withOpacity(0.3)),
                        child: Column(
                          children: [
                            const Text(
                              'Device Security Score',
                              style: TextStyle(color: AppColors.textSecondary, fontSize: 14),
                            ),
                            const SizedBox(height: 16),
                            Stack(
                              alignment: Alignment.center,
                              children: [
                                SizedBox(
                                  height: 150,
                                  width: 150,
                                  child: CircularProgressIndicator(
                                    value: data.securityScore / 100,
                                    strokeWidth: 12,
                                    backgroundColor: AppColors.glassWhite,
                                    valueColor: AlwaysStoppedAnimation<Color>(
                                      data.securityScore > 80 ? AppColors.riskLow : 
                                      data.securityScore > 50 ? AppColors.riskMedium : AppColors.riskHigh
                                    ),
                                  ),
                                ),
                                Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    Text(
                                      '\${data.securityScore}',
                                      style: Theme.of(context).textTheme.displayLarge?.copyWith(
                                        color: data.securityScore > 80 ? AppColors.riskLow : 
                                               data.securityScore > 50 ? AppColors.riskMedium : AppColors.riskHigh,
                                      ),
                                    ),
                                    const Text('/100', style: TextStyle(color: AppColors.textMuted)),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        ),
                      ).animate().fadeIn(duration: 600.ms).slideY(begin: 0.1, end: 0),
                      
                      const SizedBox(height: 24),
                      
                      // Stats Row
                      Row(
                        children: [
                          Expanded(
                            child: _StatCard(
                              title: 'Total Scans',
                              value: '\${data.totalScans}',
                              icon: Icons.document_scanner,
                              color: AppColors.neonBlue,
                            ),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: _StatCard(
                              title: 'Threats Blocked',
                              value: '\${data.threatCount}',
                              icon: Icons.shield_outlined,
                              color: data.threatCount > 0 ? AppColors.riskMedium : AppColors.riskLow,
                            ),
                          ),
                        ],
                      ).animate().fadeIn(delay: 200.ms, duration: 600.ms).slideY(begin: 0.1, end: 0),
                      
                      const SizedBox(height: 32),
                      
                      // Recent Activity
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            'Recent Activity',
                            style: Theme.of(context).textTheme.titleLarge,
                          ),
                          TextButton(
                            onPressed: () => context.go('/home/analytics'),
                            child: const Text('View All', style: TextStyle(color: AppColors.neonBlue)),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      
                      if (data.recentScans.isEmpty)
                        const Center(
                          child: Padding(
                            padding: EdgeInsets.all(32.0),
                            child: Text('No recent scans found.', style: TextStyle(color: AppColors.textMuted)),
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
                            return GlassCard(
                              onTap: () => context.push('/result', extra: scan),
                              child: Row(
                                children: [
                                  Container(
                                    padding: const EdgeInsets.all(12),
                                    decoration: BoxDecoration(
                                      color: AppColors.glassWhite,
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                    child: Icon(
                                      scan.mediaType == 'IMAGE' ? Icons.image : 
                                      scan.mediaType == 'URL' ? Icons.link : Icons.text_snippet,
                                      color: AppColors.neonBlue,
                                    ),
                                  ),
                                  const SizedBox(width: 16),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          'Analyzed \${scan.mediaType}',
                                          style: const TextStyle(fontWeight: FontWeight.w600),
                                        ),
                                        Text(
                                          'Conf: \${scan.confidence}% • \${scan.timestamp.toString().substring(0,10)}',
                                          style: const TextStyle(fontSize: 12, color: AppColors.textMuted),
                                        ),
                                      ],
                                    ),
                                  ),
                                  VerdictBadge(verdict: scan.verdict),
                                ],
                              ),
                            );
                          },
                        ).animate().fadeIn(delay: 400.ms, duration: 600.ms),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
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
    return GlassCard(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, color: color, size: 20),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  title,
                  style: const TextStyle(color: AppColors.textSecondary, fontSize: 12),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            value,
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(color: color),
          ),
        ],
      ),
    );
  }
}
