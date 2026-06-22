import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:socialshield/presentation/widgets/common/verdict_badge.dart';
import 'package:socialshield/core/theme/app_colors.dart';

void main() {
  group('VerdictBadge Widget Tests', () {
    testWidgets('renders FAKE verdict badge correctly', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: VerdictBadge(verdict: 'FAKE'),
          ),
        ),
      );

      expect(find.text('FAKE'), findsOneWidget);
      expect(find.text('REAL'), findsNothing);
      expect(find.byIcon(Icons.warning_amber_rounded), findsOneWidget);
    });

    testWidgets('renders SUSPICIOUS verdict badge correctly', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: VerdictBadge(verdict: 'SUSPICIOUS'),
          ),
        ),
      );

      expect(find.text('SUSPICIOUS'), findsOneWidget);
      expect(find.byIcon(Icons.error_outline_rounded), findsOneWidget);
    });

    testWidgets('renders REAL/SAFE verdict badge correctly', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: VerdictBadge(verdict: 'SAFE'),
          ),
        ),
      );

      expect(find.text('REAL'), findsOneWidget);
      expect(find.byIcon(Icons.check_circle_outline_rounded), findsOneWidget);
    });
  });
}
