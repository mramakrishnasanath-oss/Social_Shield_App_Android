import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';

class VerdictBadge extends StatelessWidget {
  final String verdict;
  final bool isLarge;

  const VerdictBadge({
    super.key,
    required this.verdict,
    this.isLarge = false,
  });

  @override
  Widget build(BuildContext context) {
    final v = verdict.toUpperCase();
    
    Color color;
    IconData icon;
    String label;

    if (v == 'FAKE') {
      color = AppColors.riskHigh;
      icon = Icons.warning_amber_rounded;
      label = 'FAKE';
    } else if (v == 'SUSPICIOUS') {
      color = AppColors.riskMedium;
      icon = Icons.error_outline_rounded;
      label = 'SUSPICIOUS';
    } else {
      color = AppColors.riskLow;
      icon = Icons.check_circle_outline_rounded;
      label = 'REAL';
    }

    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: isLarge ? 16 : 10,
        vertical: isLarge ? 8 : 4,
      ),
      decoration: BoxDecoration(
        color: color.withOpacity(0.15),
        border: Border.all(color: color.withOpacity(0.3)),
        borderRadius: BorderRadius.circular(50),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            icon,
            color: color,
            size: isLarge ? 20 : 14,
          ),
          SizedBox(width: isLarge ? 8 : 6),
          Text(
            label,
            style: TextStyle(
              color: color,
              fontWeight: FontWeight.bold,
              fontSize: isLarge ? 14 : 11,
              letterSpacing: 1,
            ),
          ),
        ],
      ),
    );
  }
}
