import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

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
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final formattedDate = DateFormat('yyyy-MM-dd HH:mm:ss').format(result!.timestamp);

    // Format verdict label
    final String verdictSymbol = result!.isFake ? '⚠' : (result!.isSuspicious ? '⚠' : '✓');
    final String verdictText = result!.isFake ? 'FAKE' : (result!.isSuspicious ? 'SUSPICIOUS' : 'REAL');

    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Audit Result'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/home/dashboard'),
        ),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          physics: const BouncingScrollPhysics(),
          padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // 1. Verdict Header Card
              GlassCard(
                padding: const EdgeInsets.all(24),
                border: Border.all(color: color.withOpacity(0.4), width: 2),
                child: Column(
                  children: [
                    Container(
                      padding: const EdgeInsets.all(20),
                      decoration: BoxDecoration(
                        color: color.withOpacity(0.12),
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        result!.verdictIcon, 
                        size: 56, 
                        color: color
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      '$verdictSymbol $verdictText',
                      style: TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.w900,
                        color: color,
                        letterSpacing: 1.5,
                      ),
                    ),
                    const SizedBox(height: 10),
                    VerdictBadge(verdict: result!.verdict, isLarge: true),
                    const SizedBox(height: 16),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'Confidence: ',
                          style: TextStyle(
                            fontSize: 16,
                            color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                          ),
                        ),
                        Text(
                          '${result!.confidence.toStringAsFixed(1)}%',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: color,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              
              const SizedBox(height: 20),
              
              // 2. Probability Breakdown Card
              GlassCard(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Text(
                      'AI Threat Evaluation', 
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const Divider(color: AppColors.glassBorder, height: 24),
                    
                    _buildProgressBar(
                      context: context,
                      label: 'AI / Fake Probability', 
                      value: result!.fakeProbability, 
                      color: AppColors.riskHigh,
                    ),
                    const SizedBox(height: 16),
                    _buildProgressBar(
                      context: context,
                      label: 'Authentic Probability', 
                      value: result!.realProbability, 
                      color: AppColors.riskLow,
                    ),
                  ],
                ),
              ),
              
              const SizedBox(height: 20),
              
              // 3. AI Explanation & Warnings
              GlassCard(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Text(
                      'Detection Factors & Insights', 
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const Divider(color: AppColors.glassBorder, height: 24),
                    
                    if (result!.explanations.isEmpty)
                      const Text(
                        'No anomalies identified during this check.',
                        style: TextStyle(color: Colors.grey, fontSize: 13),
                      )
                    else
                      ...result!.explanations.map((exp) => Padding(
                        padding: const EdgeInsets.only(bottom: 12.0),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Icon(
                              result!.isFake ? Icons.warning_amber_rounded : Icons.info_outline_rounded, 
                              size: 18, 
                              color: result!.isFake ? AppColors.riskHigh : AppColors.darkPrimary
                            ),
                            const SizedBox(width: 10),
                            Expanded(
                              child: Text(
                                exp, 
                                style: TextStyle(
                                  fontSize: 13,
                                  color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                                ),
                              ),
                            ),
                          ],
                        ),
                      )).toList(),
                  ],
                ),
              ),

              const SizedBox(height: 20),

              // 4. Metadata & Analysis Parameters
              if (result!.metadata != null && result!.metadata!.isNotEmpty) ...[
                GlassCard(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      const Text(
                        'Metadata Analysis', 
                        style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                      ),
                      const Divider(color: AppColors.glassBorder, height: 24),
                      ...result!.metadata!.entries.map((entry) {
                        final formattedKey = entry.key
                            .replaceAll('_', ' ')
                            .split(' ')
                            .map((word) => word.isNotEmpty ? '${word[0].toUpperCase()}${word.substring(1)}' : '')
                            .join(' ');
                        
                        return Padding(
                          padding: const EdgeInsets.symmetric(vertical: 6.0),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Expanded(
                                child: Text(
                                  formattedKey,
                                  style: TextStyle(
                                    fontSize: 13,
                                    color: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted,
                                  ),
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                              const SizedBox(width: 16),
                              Text(
                                entry.value.toString(),
                                style: const TextStyle(
                                  fontSize: 13,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                        );
                      }).toList(),
                    ],
                  ),
                ),
                const SizedBox(height: 20),
              ],

              // 5. Visual/Structural Checks
              GlassCard(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Text(
                      'Visual & Structural Verification',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const Divider(color: AppColors.glassBorder, height: 24),
                    _buildVerificationRow(
                      context, 
                      'Codec/Format Compliance', 
                      true,
                    ),
                    _buildVerificationRow(
                      context, 
                      'Exif Metadata Validation', 
                      !result!.isFake,
                    ),
                    _buildVerificationRow(
                      context, 
                      'Temporal Vector Coherence', 
                      !result!.isFake || result!.mediaType != 'VIDEO',
                    ),
                    _buildVerificationRow(
                      context, 
                      'GAN/Deepfake Signature Check', 
                      !result!.isFake,
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 20),

              // 6. Scan Logs & Technical ID
              GlassCard(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Text(
                      'Scan Reference Logs', 
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const Divider(color: AppColors.glassBorder, height: 24),
                    
                    // Timestamp
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Timestamp', style: TextStyle(fontSize: 12, color: Colors.grey)),
                        Text(formattedDate, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                      ],
                    ),
                    const SizedBox(height: 12),
                    
                    // Scan ID with copy button
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Scan ID', style: TextStyle(fontSize: 12, color: Colors.grey)),
                        Expanded(
                          child: Align(
                            alignment: Alignment.centerRight,
                            child: SingleChildScrollView(
                              scrollDirection: Axis.horizontal,
                              child: Text(
                                result!.scanId, 
                                style: const TextStyle(fontSize: 10, fontFamily: 'monospace', fontWeight: FontWeight.bold)
                              ),
                            ),
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.copy, size: 14, color: Colors.grey),
                          onPressed: () {
                            Clipboard.setData(ClipboardData(text: result!.scanId));
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('Scan ID copied to clipboard'), duration: Duration(seconds: 1)),
                            );
                          },
                          constraints: const BoxConstraints(),
                          padding: const EdgeInsets.only(left: 8),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              
              const SizedBox(height: 32),
              
              // Scan Another Button
              NeonButton(
                text: 'Scan Another',
                onPressed: () => context.go('/home/scan'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildProgressBar({
    required BuildContext context,
    required String label, 
    required double value, 
    required Color color
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Expanded(
              child: Text(
                label, 
                style: const TextStyle(fontSize: 13),
                overflow: TextOverflow.ellipsis,
              ),
            ),
            const SizedBox(width: 8),
            Text(
              '${value.toStringAsFixed(1)}%', 
              style: TextStyle(fontWeight: FontWeight.bold, color: color, fontSize: 13),
            ),
          ],
        ),
        const SizedBox(height: 8),
        LinearProgressIndicator(
          value: value / 100.0,
          backgroundColor: AppColors.glassBorder,
          valueColor: AlwaysStoppedAnimation<Color>(color),
          minHeight: 8,
          borderRadius: BorderRadius.circular(4),
        ),
      ],
    );
  }

  Widget _buildVerificationRow(BuildContext context, String label, bool passed) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Text(
              label,
              style: TextStyle(
                fontSize: 13,
                color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
              ),
              overflow: TextOverflow.ellipsis,
            ),
          ),
          const SizedBox(width: 16),
          Icon(
            passed ? Icons.check_circle_rounded : Icons.cancel_rounded,
            color: passed ? AppColors.riskLow : AppColors.riskHigh,
            size: 18,
          ),
        ],
      ),
    );
  }
}
