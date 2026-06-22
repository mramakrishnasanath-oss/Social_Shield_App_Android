import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/theme/app_colors.dart';
import '../../../services/localization_service.dart';

class HomeScreen extends ConsumerStatefulWidget {
  final Widget child;
  const HomeScreen({super.key, required this.child});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  int _currentIndex = 0;

  void _onTap(int index, BuildContext context) {
    if (_currentIndex == index) return;
    setState(() => _currentIndex = index);
    
    switch (index) {
      case 0:
        context.go('/home/dashboard');
        break;
      case 1:
        context.go('/home/scan');
        break;
      case 2:
        context.go('/home/analytics');
        break;
      case 3:
        context.go('/home/profile');
        break;
    }
  }

  Future<void> _showEmergencyDialog(BuildContext context) async {
    showDialog(
      context: context,
      builder: (BuildContext ctx) {
        final isDark = Theme.of(context).brightness == Brightness.dark;
        return AlertDialog(
          title: Row(
            children: [
              const Icon(Icons.warning_amber_rounded, color: AppColors.riskHigh),
              const SizedBox(width: 8),
              Text(Trans.of(context, 'emergency_call')),
            ],
          ),
          content: Text(Trans.of(context, 'call_police_confirm')),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(ctx).pop(),
              child: Text(
                Trans.of(context, 'cancel'),
                style: TextStyle(color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary),
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                Navigator.of(ctx).pop();
                final Uri telUri = Uri.parse('tel:100');
                try {
                  if (await canLaunchUrl(telUri)) {
                    await launchUrl(telUri);
                  }
                } catch (e) {
                  debugPrint('Could not launch dialer: $e');
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.riskHigh,
                foregroundColor: Colors.white,
              ),
              child: Text(Trans.of(context, 'call')),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    // Update index based on current location
    final String location = GoRouterState.of(context).uri.toString();
    if (location.startsWith('/home/dashboard')) _currentIndex = 0;
    else if (location.startsWith('/home/scan')) _currentIndex = 1;
    else if (location.startsWith('/home/analytics')) _currentIndex = 2;
    else if (location.startsWith('/home/profile')) _currentIndex = 3;

    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      body: widget.child,
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _showEmergencyDialog(context),
        label: Text(Trans.of(context, 'emergency_call')),
        icon: const Icon(Icons.phone_in_talk_rounded),
        backgroundColor: AppColors.riskHigh,
        foregroundColor: Colors.white,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (idx) => _onTap(idx, context),
        destinations: [
          NavigationDestination(
            icon: const Icon(Icons.dashboard_outlined),
            selectedIcon: const Icon(Icons.dashboard_rounded),
            label: Trans.of(context, 'dashboard'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.document_scanner_outlined),
            selectedIcon: const Icon(Icons.document_scanner_rounded),
            label: Trans.of(context, 'scan'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.analytics_outlined),
            selectedIcon: const Icon(Icons.analytics_rounded),
            label: Trans.of(context, 'analytics'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.person_outline),
            selectedIcon: const Icon(Icons.person_rounded),
            label: Trans.of(context, 'profile'),
          ),
        ],
      ),
    );
  }
}
