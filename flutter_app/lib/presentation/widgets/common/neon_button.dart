import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';

class NeonButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final bool isLoading;
  final bool isSecondary;
  final IconData? icon;

  const NeonButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.isLoading = false,
    this.isSecondary = false,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    final bool isDark = Theme.of(context).brightness == Brightness.dark;
    
    final gradient = isSecondary
        ? null
        : (isDark ? AppColors.primaryGradient : null);
        
    final color = isSecondary 
        ? Colors.transparent 
        : (isDark ? null : AppColors.neonBlue);
        
    final textColor = isSecondary
        ? (isDark ? AppColors.textPrimary : AppColors.lightTextPrimary)
        : (isDark ? AppColors.deepBlack : Colors.white);
        
    final border = isSecondary
        ? Border.all(color: isDark ? AppColors.glassBorder : AppColors.lightTextMuted)
        : null;
        
    final boxShadow = !isSecondary && isDark && onPressed != null
        ? [
            BoxShadow(
              color: AppColors.neonBlue.withOpacity(0.3),
              blurRadius: 16,
              offset: const Offset(0, 4),
            )
          ]
        : null;

    return Container(
      width: double.infinity,
      height: 56,
      decoration: BoxDecoration(
        gradient: gradient,
        color: color,
        borderRadius: BorderRadius.circular(50),
        border: border,
        boxShadow: boxShadow,
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: isLoading ? null : onPressed,
          borderRadius: BorderRadius.circular(50),
          child: Center(
            child: isLoading
                ? SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(
                      strokeWidth: 3,
                      valueColor: AlwaysStoppedAnimation<Color>(textColor),
                    ),
                  )
                : Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      if (icon != null) ...[
                        Icon(icon, color: textColor, size: 20),
                        const SizedBox(width: 8),
                      ],
                      Text(
                        text,
                        style: TextStyle(
                          color: textColor,
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
          ),
        ),
      ),
    );
  }
}
