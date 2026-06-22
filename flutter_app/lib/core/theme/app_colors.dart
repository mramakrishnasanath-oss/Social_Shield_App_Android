import 'package:flutter/material.dart';

class AppColors {
  AppColors._();

  // Light Theme Palette
  static const Color lightPrimary = Color(0xFF0066FF);
  static const Color lightSecondary = Color(0xFF00C2FF);
  static const Color lightAccent = Color(0xFF00E5A8);
  static const Color lightBg = Color(0xFFF7F9FC);
  static const Color lightCard = Color(0xFFFFFFFF);
  
  static const Color lightTextPrimary = Color(0xFF1E293B);
  static const Color lightTextSecondary = Color(0xFF475569);
  static const Color lightTextMuted = Color(0xFF94A3B8);

  // Dark Theme Palette
  static const Color darkPrimary = Color(0xFF4D8DFF);
  static const Color darkSecondary = Color(0xFF00D4FF);
  static const Color darkAccent = Color(0xFF00F5B4);
  static const Color darkBg = Color(0xFF0E1117);
  static const Color darkCard = Color(0xFF1A1F2E);
  
  static const Color darkTextPrimary = Color(0xFFF8FAFC);
  static const Color darkTextSecondary = Color(0xFFCBD5E1);
  static const Color darkTextMuted = Color(0xFF64748B);

  // Status & Risk Levels
  static const Color riskHigh = Color(0xFFEF4444); // Red
  static const Color riskMedium = Color(0xFFF59E0B); // Amber
  static const Color riskLow = Color(0xFF10B981); // Green

  static const Color glassWhite = Color(0x0FFFFFFF);
  static const Color glassBorder = Color(0x1EFFFFFF);

  // Gradients
  static const LinearGradient lightPrimaryGradient = LinearGradient(
    colors: [lightPrimary, lightSecondary],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );

  static const LinearGradient darkPrimaryGradient = LinearGradient(
    colors: [darkPrimary, darkSecondary],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
  
  static const LinearGradient dangerGradient = LinearGradient(
    colors: [riskHigh, Color(0xFFF87171)],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );

  static const LinearGradient successGradient = LinearGradient(
    colors: [riskLow, Color(0xFF34D399)],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );

  // Compatibility colors and gradients used in screens/widgets
  static const Color neonBlue = Color(0xFF0066FF);
  static const Color textMuted = Color(0xFF94A3B8);
  static const Color textSecondary = Color(0xFF475569);
  static const Color textPrimary = Color(0xFF1E293B);
  static const Color deepBlack = Color(0xFF0E1117);
  static const LinearGradient primaryGradient = lightPrimaryGradient;
}
