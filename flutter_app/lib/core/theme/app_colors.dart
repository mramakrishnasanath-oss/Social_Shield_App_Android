import 'package:flutter/material.dart';

class AppColors {
  AppColors._();

  // Cyberpunk Neon Palette (Dark Theme)
  static const Color neonBlue = Color(0xFF00D4FF);
  static const Color neonPurple = Color(0xFF8B5CF6);
  static const Color neonCyan = Color(0xFF06FFA5);
  static const Color neonPink = Color(0xFFFF3CAC);
  
  static const Color deepBlack = Color(0xFF050510);
  static const Color darkSurface = Color(0xFF0D0D2B);
  static const Color darkCard = Color(0xFF12123A);
  static const Color darkElevated = Color(0xFF1A1A4E);
  
  static const Color glassWhite = Color(0x0FFFFFFF); // ~6% opacity white
  static const Color glassBorder = Color(0x1EFFFFFF); // ~12% opacity white

  // Risk Levels
  static const Color riskHigh = Color(0xFFFF3B3B);
  static const Color riskMedium = Color(0xFFFFB800);
  static const Color riskLow = Color(0xFF06FFA5);

  // Text Colors (Dark Theme)
  static const Color textPrimary = Color(0xFFFFFFFF);
  static const Color textSecondary = Color(0xA6FFFFFF); // 65% white
  static const Color textMuted = Color(0x59FFFFFF); // 35% white

  // Light Theme Palette
  static const Color lightBg = Color(0xFFF8FAFC);
  static const Color lightSurface = Color(0xFFFFFFFF);
  static const Color lightCard = Color(0xFFF1F5F9);
  
  // Text Colors (Light Theme)
  static const Color lightTextPrimary = Color(0xFF0F172A);
  static const Color lightTextSecondary = Color(0xFF475569);
  static const Color lightTextMuted = Color(0xFF94A3B8);

  // Gradients
  static const LinearGradient primaryGradient = LinearGradient(
    colors: [neonBlue, neonPurple],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
  
  static const LinearGradient dangerGradient = LinearGradient(
    colors: [riskHigh, Color(0xFFFF6B6B)],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );

  static const LinearGradient successGradient = LinearGradient(
    colors: [riskLow, neonBlue],
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
  );
}
