import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../core/theme/app_colors.dart';
import '../../../providers/scan_provider.dart';
import '../../../services/localization_service.dart';
import '../../widgets/common/glass_card.dart';

class AnalyticsScreen extends ConsumerStatefulWidget {
  const AnalyticsScreen({super.key});

  @override
  ConsumerState<AnalyticsScreen> createState() => _AnalyticsScreenState();
}

class _AnalyticsScreenState extends ConsumerState<AnalyticsScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      appBar: AppBar(
        title: Text(Trans.of(context, 'analytics')),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
          labelColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
          unselectedLabelColor: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted,
          tabs: [
            Tab(icon: const Icon(Icons.show_chart_rounded), text: 'Threat Trends'),
            Tab(icon: const Icon(Icons.warning_rounded), text: Trans.of(context, 'scam_alerts')),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildTrendsTab(context),
          _buildAlertsTab(context),
        ],
      ),
    );
  }

  Widget _buildTrendsTab(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Threat Analysis Trends', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          GlassCard(
            child: SizedBox(
              height: 250,
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: LineChart(
                  LineChartData(
                    gridData: const FlGridData(show: false),
                    titlesData: const FlTitlesData(show: false),
                    borderData: FlBorderData(show: false),
                    lineBarsData: [
                      LineChartBarData(
                        spots: const [
                          FlSpot(0, 3), FlSpot(1, 1), FlSpot(2, 4),
                          FlSpot(3, 2), FlSpot(4, 5), FlSpot(5, 1), FlSpot(6, 4),
                        ],
                        isCurved: true,
                        color: AppColors.riskHigh,
                        barWidth: 4,
                        isStrokeCapRound: true,
                        belowBarData: BarAreaData(
                          show: true,
                          color: AppColors.riskHigh.withOpacity(0.2),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ).animate().fadeIn(duration: 500.ms),
          
          const SizedBox(height: 32),
          const Text('Scan Distribution', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          const SizedBox(height: 16),
          GlassCard(
            child: SizedBox(
              height: 200,
              child: PieChart(
                PieChartData(
                  sectionsSpace: 4,
                  centerSpaceRadius: 40,
                  sections: [
                    PieChartSectionData(
                      value: 40, 
                      color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary, 
                      title: 'Images', 
                      radius: 50,
                      titleStyle: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white)
                    ),
                    PieChartSectionData(
                      value: 30, 
                      color: isDark ? AppColors.darkSecondary : AppColors.lightSecondary, 
                      title: 'URLs', 
                      radius: 50,
                      titleStyle: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white)
                    ),
                    PieChartSectionData(
                      value: 30, 
                      color: isDark ? AppColors.darkAccent : AppColors.lightAccent, 
                      title: 'Text', 
                      radius: 50,
                      titleStyle: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white)
                    ),
                  ],
                ),
              ),
            ),
          ).animate().fadeIn(delay: 200.ms, duration: 500.ms),
        ],
      ),
    );
  }

  Widget _buildAlertsTab(BuildContext context) {
    final alertsAsync = ref.watch(scamAlertsStreamProvider);
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return alertsAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (err, _) => Center(child: Text('Error loading alerts: $err')),
      data: (alerts) {
        if (alerts.isEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.shield_rounded, 
                  size: 64, 
                  color: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted
                ),
                const SizedBox(height: 16),
                const Text(
                  'All Quiet in the Community',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                ),
                const SizedBox(height: 8),
                const Text('No community scam warnings reported yet.', style: TextStyle(color: Colors.grey)),
              ],
            ),
          );
        }

        return ListView.builder(
          padding: const EdgeInsets.all(24.0),
          itemCount: alerts.length,
          itemBuilder: (context, index) {
            final alert = alerts[index];
            final String severity = alert['severity'] ?? 'LOW';
            
            // Map severity colors
            Color alertColor = AppColors.riskLow;
            String severityLabel = 'Low Risk';
            if (severity.toUpperCase() == 'HIGH' || severity.toUpperCase() == 'CRITICAL') {
              alertColor = AppColors.riskHigh;
              severityLabel = 'Critical Scam';
            } else if (severity.toUpperCase() == 'MEDIUM') {
              alertColor = AppColors.riskMedium;
              severityLabel = 'Medium Risk';
            }

            return Card(
              margin: const EdgeInsets.only(bottom: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
                side: BorderSide(color: alertColor.withOpacity(0.4), width: 1.5),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Icon(Icons.warning_amber_rounded, color: alertColor, size: 20),
                            const SizedBox(width: 8),
                            Text(
                              alert['title'] ?? 'Scam Alert',
                              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                            ),
                          ],
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: alertColor.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            severityLabel,
                            style: TextStyle(color: alertColor, fontWeight: FontWeight.bold, fontSize: 11),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      alert['body'] ?? 'Suspicious activity has been reported.',
                      style: TextStyle(color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary),
                    ),
                    const SizedBox(height: 12),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'Type: ${alert['type'] ?? 'General'}',
                          style: const TextStyle(fontSize: 12, color: Colors.grey, fontWeight: FontWeight.w500),
                        ),
                        Text(
                          alert['timestamp'] != null 
                              ? alert['timestamp'].toString().substring(0, 16).replaceAll('T', ' ')
                              : '',
                          style: const TextStyle(fontSize: 11, color: Colors.grey),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ).animate().fadeIn(duration: 400.ms).slideY(begin: 0.1, end: 0);
          },
        );
      },
    );
  }
}
