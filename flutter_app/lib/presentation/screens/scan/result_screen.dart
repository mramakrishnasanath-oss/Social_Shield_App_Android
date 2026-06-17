import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/app_colors.dart';
import '../../../models/scan_result_model.dart';
import '../../widgets/common/glass_card.dart';
import '../../widgets/common/verdict_badge.dart';
import '../../widgets/common/neon_button.dart';

class ResultScreen extends StatelessWidget {
  final ScanResultModel? result;

  const ResultScreen({super.key, required this.result});

  @override
  Widget build(BuildContext context) {
    if (result == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Result')),
        body: const Center(child: Text('No result found')),
      );
    }

    final Color color = result!.verdictColor;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Scan Result'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/home/dashboard'),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            // Verdict Header
            GlassCard(
              border: Border.all(color: color.withOpacity(0.5), width: 2),
              child: Column(
                children: [
                  Container(
                    padding: const EdgeInsets.all(24),
                    decoration: BoxDecoration(
                      color: color.withOpacity(0.1),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(result!.verdictIcon, size: 64, color: color),
                  ),
                  const SizedBox(height: 16),
                  VerdictBadge(verdict: result!.verdict, isLarge: true),
                  const SizedBox(height: 8),
                  Text(
                    'Confidence: \${result!.confidence}%',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: color,
                    ),
                  ),
                ],
              ),
            ),
            
            const SizedBox(height: 24),
            
            // Detailed Analysis
            GlassCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Detailed Analysis', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const Divider(color: AppColors.glassBorder, height: 32),
                  
                  // Progress Bars
                  _buildProgressBar('AI / Fake Probability', result!.fakeProbability, AppColors.riskHigh),
                  const SizedBox(height: 16),
                  _buildProgressBar('Authentic Probability', result!.realProbability, AppColors.riskLow),
                  
                  const Divider(color: AppColors.glassBorder, height: 32),
                  const Text('AI Explanation', style: TextStyle(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 12),
                  
                  ...result!.explanations.map((exp) => Padding(
                    padding: const EdgeInsets.only(bottom: 8.0),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Icon(Icons.info_outline, size: 16, color: AppColors.neonBlue),
                        const SizedBox(width: 8),
                        Expanded(child: Text(exp, style: const TextStyle(color: AppColors.textSecondary))),
                      ],
                    ),
                  )).toList(),
                ],
              ),
            ),
            
            const SizedBox(height: 32),
            NeonButton(
              text: 'Scan Another',
              onPressed: () => context.go('/home/scan'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProgressBar(String label, double value, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(label, style: const TextStyle(fontSize: 14)),
            Text('\${value.toStringAsFixed(1)}%', style: TextStyle(fontWeight: FontWeight.bold, color: color)),
          ],
        ),
        const SizedBox(height: 8),
        LinearProgressIndicator(
          value: value / 100,
          backgroundColor: AppColors.glassBorder,
          valueColor: AlwaysStoppedAnimation<Color>(color),
          minHeight: 8,
          borderRadius: BorderRadius.circular(4),
        ),
      ],
    );
  }
}
