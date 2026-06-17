import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../core/constants/app_constants.dart';
import '../models/scan_result_model.dart';
import '../providers/auth_provider.dart';

// Import Screens (Placeholders for now, will implement in Phase 6)
import '../presentation/screens/splash_screen.dart';
import '../presentation/screens/onboarding_screen.dart';
import '../presentation/screens/auth/login_screen.dart';
import '../presentation/screens/auth/register_screen.dart';
import '../presentation/screens/auth/forgot_password_screen.dart';
import '../presentation/screens/home/home_screen.dart';
import '../presentation/screens/home/dashboard_screen.dart';
import '../presentation/screens/scan/scan_screen.dart';
import '../presentation/screens/scan/result_screen.dart';
import '../presentation/screens/analytics/analytics_screen.dart';
import '../presentation/screens/profile/profile_screen.dart';
import '../presentation/screens/settings/settings_screen.dart';

final goRouterProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: '/splash',
    routes: [
      GoRoute(
        path: '/splash',
        builder: (context, state) => const SplashScreen(),
      ),
      GoRoute(
        path: '/onboarding',
        builder: (context, state) => const OnboardingScreen(),
      ),
      GoRoute(
        path: '/auth/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/auth/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/auth/forgot-password',
        builder: (context, state) => const ForgotPasswordScreen(),
      ),
      ShellRoute(
        builder: (context, state, child) => HomeScreen(child: child),
        routes: [
          GoRoute(
            path: '/home/dashboard',
            builder: (context, state) => const DashboardScreen(),
          ),
          GoRoute(
            path: '/home/scan',
            builder: (context, state) => const ScanScreen(),
          ),
          GoRoute(
            path: '/home/analytics',
            builder: (context, state) => const AnalyticsScreen(),
          ),
          GoRoute(
            path: '/home/profile',
            builder: (context, state) => const ProfileScreen(),
          ),
        ],
      ),
      GoRoute(
        path: '/result',
        builder: (context, state) {
          final result = state.extra as ScanResultModel?;
          return ResultScreen(result: result);
        },
      ),
      GoRoute(
        path: '/settings',
        builder: (context, state) => const SettingsScreen(),
      ),
    ],
    redirect: (context, state) async {
      // If we are still loading auth state, don't redirect yet
      if (authState.isLoading) return null;

      final user = authState.value;
      final bool isAuthenticated = user != null;
      final bool isAuthRoute = state.matchedLocation.startsWith('/auth');
      final bool isSplashRoute = state.matchedLocation == '/splash';
      
      final prefs = await SharedPreferences.getInstance();
      final bool hasSeenOnboarding = prefs.getBool(AppConstants.keyOnboardingComplete) ?? false;
      final bool isOnboardingRoute = state.matchedLocation == '/onboarding';

      // Let splash screen handle its own logic initially
      if (isSplashRoute) return null;

      // If not seen onboarding, force them there
      if (!hasSeenOnboarding && !isOnboardingRoute) {
        return '/onboarding';
      }

      // If seen onboarding, but not authenticated and trying to access protected route
      if (hasSeenOnboarding && !isAuthenticated && !isAuthRoute && !isOnboardingRoute) {
        return '/auth/login';
      }

      // If authenticated and trying to access auth or onboarding screens
      if (isAuthenticated && (isAuthRoute || isOnboardingRoute)) {
        return '/home/dashboard';
      }

      return null;
    },
  );
});
