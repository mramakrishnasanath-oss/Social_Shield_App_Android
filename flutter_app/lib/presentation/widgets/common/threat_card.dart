import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../common/verdict_badge.dart';
import '../common/glass_card.dart';

/// Severity levels for a detected threat.
enum ThreatSeverity { critical, high, medium, low }

extension ThreatSeverityExt on ThreatSeverity {
  String get label {
    switch (this) {
      case ThreatSeverity.critical:
        return 'CRITICAL';
      case ThreatSeverity.high:
        return 'HIGH';
      case ThreatSeverity.medium:
        return 'MEDIUM';
      case ThreatSeverity.low:
        return 'LOW';
    }
  }

  Color get color {
    switch (this) {
      case ThreatSeverity.critical:
        return const Color(0xFFFF3CAC);
      case ThreatSeverity.high:
        return const Color(0xFFFF4D4D);
      case ThreatSeverity.medium:
        return const Color(0xFFFF8C42);
      case ThreatSeverity.low:
        return const Color(0xFFFFD166);
    }
  }

  IconData get icon {
    switch (this) {
      case ThreatSeverity.critical:
        return Icons.crisis_alert_rounded;
      case ThreatSeverity.high:
        return Icons.gpp_bad_rounded;
      case ThreatSeverity.medium:
        return Icons.warning_amber_rounded;
      case ThreatSeverity.low:
        return Icons.info_outline_rounded;
    }
  }
}

/// A dismissible card that presents a single detected threat.
class ThreatCard extends StatefulWidget {
  const ThreatCard({
    super.key,
    required this.title,
    required this.description,
    required this.severity,
    required this.timestamp,
    this.verdict,
    this.onDismiss,
    this.onTap,
    this.margin = const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
  });

  final String title;
  final String description;
  final ThreatSeverity severity;
  final DateTime timestamp;
  final Verdict? verdict;
  final VoidCallback? onDismiss;
  final VoidCallback? onTap;
  final EdgeInsetsGeometry margin;

  @override
  State<ThreatCard> createState() => _ThreatCardState();
}

class _ThreatCardState extends State<ThreatCard>
    with SingleTickerProviderStateMixin {
  late AnimationController _entryController;
  late Animation<double> _slideAnim;
  late Animation<double> _fadeAnim;

  @override
  void initState() {
    super.initState();
    _entryController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 450),
    );
    _slideAnim = Tween<double>(begin: 40, end: 0).animate(
      CurvedAnimation(parent: _entryController, curve: Curves.easeOutCubic),
    );
    _fadeAnim = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _entryController, curve: Curves.easeIn),
    );
    _entryController.forward();
  }

  @override
  void dispose() {
    _entryController.dispose();
    super.dispose();
  }

  String _formatTime(DateTime dt) {
    final now = DateTime.now();
    final diff = now.difference(dt);
    if (diff.inMinutes < 1) return 'Just now';
    if (diff.inHours < 1) return '${diff.inMinutes}m ago';
    if (diff.inDays < 1) return '${diff.inHours}h ago';
    return DateFormat('MMM d, h:mm a').format(dt);
  }

  @override
  Widget build(BuildContext context) {
    final sev = widget.severity;

    return AnimatedBuilder(
      animation: _entryController,
      builder: (_, child) => Transform.translate(
        offset: Offset(0, _slideAnim.value),
        child: Opacity(opacity: _fadeAnim.value, child: child),
      ),
      child: widget.onDismiss != null
          ? Dismissible(
              key: ValueKey(widget.title + widget.timestamp.toString()),
              direction: DismissDirection.endToStart,
              background: _dismissBackground(),
              onDismissed: (_) => widget.onDismiss?.call(),
              child: _cardBody(sev),
            )
          : _cardBody(sev),
    );
  }

  Widget _dismissBackground() {
    return Container(
      alignment: Alignment.centerRight,
      padding: const EdgeInsets.only(right: 20),
      decoration: BoxDecoration(
        color: const Color(0x22FF3CAC),
        borderRadius: BorderRadius.circular(16),
      ),
      child: const Icon(Icons.delete_outline_rounded,
          color: Color(0xFFFF3CAC), size: 28),
    );
  }

  Widget _cardBody(ThreatSeverity sev) {
    return GlassCard(
      margin: widget.margin,
      borderRadius: 16,
      borderColor: sev.color.withAlpha(60),
      onTap: widget.onTap,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // ── Severity icon ────────────────────────────────────────────────
          Container(
            width: 44,
            height: 44,
            decoration: BoxDecoration(
              color: sev.color.withAlpha(30),
              shape: BoxShape.circle,
              boxShadow: [
                BoxShadow(
                  color: sev.color.withAlpha(60),
                  blurRadius: 12,
                )
              ],
            ),
            child: Icon(sev.icon, color: sev.color, size: 22),
          ),

          const SizedBox(width: 14),

          // ── Content ──────────────────────────────────────────────────────
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Title + severity chip
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        widget.title,
                        style: const TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w700,
                          fontSize: 14,
                          fontFamily: 'Inter',
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                    const SizedBox(width: 8),
                    _SeverityChip(severity: sev),
                  ],
                ),

                const SizedBox(height: 5),

                // Description
                Text(
                  widget.description,
                  style: const TextStyle(
                    color: Colors.white54,
                    fontSize: 12.5,
                    fontFamily: 'Inter',
                    height: 1.5,
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),

                const SizedBox(height: 8),

                // Footer: timestamp + optional verdict badge
                Row(
                  children: [
                    Icon(Icons.access_time_rounded,
                        size: 12, color: Colors.white38),
                    const SizedBox(width: 4),
                    Text(
                      _formatTime(widget.timestamp),
                      style: const TextStyle(
                        color: Colors.white38,
                        fontSize: 11,
                        fontFamily: 'Inter',
                      ),
                    ),
                    if (widget.verdict != null) ...[
                      const Spacer(),
                      VerdictBadge(
                          verdict: widget.verdict!, animate: false),
                    ],
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _SeverityChip extends StatelessWidget {
  const _SeverityChip({required this.severity});

  final ThreatSeverity severity;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 3),
      decoration: BoxDecoration(
        color: severity.color.withAlpha(30),
        borderRadius: BorderRadius.circular(6),
        border: Border.all(color: severity.color.withAlpha(80), width: 0.8),
      ),
      child: Text(
        severity.label,
        style: TextStyle(
          color: severity.color,
          fontSize: 9,
          fontWeight: FontWeight.w800,
          letterSpacing: 0.6,
          fontFamily: 'Inter',
        ),
      ),
    );
  }
}
