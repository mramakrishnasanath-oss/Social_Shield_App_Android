import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/theme/app_colors.dart';

class HomeScreen extends StatefulWidget {
  final Widget child;
  const HomeScreen({super.key, required this.child});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
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

  @override
  Widget build(BuildContext context) {
    // Update index based on current location
    final String location = GoRouterState.of(context).uri.toString();
    if (location.startsWith('/home/dashboard')) _currentIndex = 0;
    else if (location.startsWith('/home/scan')) _currentIndex = 1;
    else if (location.startsWith('/home/analytics')) _currentIndex = 2;
    else if (location.startsWith('/home/profile')) _currentIndex = 3;

    return Scaffold(
      body: widget.child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (idx) => _onTap(idx, context),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.dashboard_outlined),
            selectedIcon: Icon(Icons.dashboard_rounded),
            label: 'Dashboard',
          ),
          NavigationDestination(
            icon: Icon(Icons.document_scanner_outlined),
            selectedIcon: Icon(Icons.document_scanner_rounded),
            label: 'Scan',
          ),
          NavigationDestination(
            icon: Icon(Icons.analytics_outlined),
            selectedIcon: Icon(Icons.analytics_rounded),
            label: 'Analytics',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person_rounded),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}
