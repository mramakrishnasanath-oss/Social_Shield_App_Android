import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../core/theme/app_colors.dart';
import '../../../providers/auth_provider.dart';

class SplashScreen extends ConsumerStatefulWidget {
  const SplashScreen({super.key});

  @override
  ConsumerState<SplashScreen> createState() => _SplashScreenState();
}


class _SplashScreenState extends ConsumerState<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    await Future.delayed(const Duration(seconds: 3));
    if (!mounted) return;
    
    final user = ref.read(authStateProvider).value;
    if (user != null) {
      context.go('/home/dashboard');
    } else {
      context.go('/onboarding');
    }
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      backgroundColor: isDark ? AppColors.darkBg : AppColors.lightBg,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Premium Brand Logo Replacement
            Container(
              width: 120,
              height: 120,
              decoration: BoxDecoration(
                color: isDark ? AppColors.glassWhite : Colors.white,
                borderRadius: BorderRadius.circular(30),
                border: Border.all(
                  color: (isDark ? AppColors.darkPrimary : AppColors.lightPrimary).withOpacity(0.3)
                ),
                boxShadow: [
                  BoxShadow(
                    color: (isDark ? AppColors.darkPrimary : AppColors.lightPrimary).withOpacity(0.2),
                    blurRadius: 40,
                  )
                ],
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(30),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Image.asset(
                    'assets/images/splash_logo.png',
                    fit: BoxFit.contain,
                    errorBuilder: (context, error, stackTrace) {
                      return Icon(
                        Icons.shield,
                        size: 60,
                        color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                      );
                    },
                  ),
                ),
              ),
            ).animate()
              .scale(duration: 600.ms, curve: Curves.easeOutBack)
              .then()
              .shimmer(duration: 1200.ms, color: Colors.white24),
              
            const SizedBox(height: 24),
            
            // Title
            Text(
              'SocialShield',
              style: Theme.of(context).textTheme.displayMedium?.copyWith(
                fontWeight: FontWeight.bold,
                foreground: Paint()
                  ..shader = (isDark ? AppColors.darkPrimaryGradient : AppColors.lightPrimaryGradient).createShader(
                    const Rect.fromLTWH(0, 0, 200, 70),
                  ),
              ),
            ).animate()
              .fadeIn(delay: 400.ms, duration: 600.ms)
              .slideY(begin: 0.2, end: 0),
              
            const SizedBox(height: 12),
            
            // Subtitle
            Text(
              'AI-Powered Mobile Protection',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                letterSpacing: 2,
                fontWeight: FontWeight.w500,
              ),
            ).animate()
              .fadeIn(delay: 800.ms, duration: 600.ms),
          ],
        ),
      ),
    );
  }
}
