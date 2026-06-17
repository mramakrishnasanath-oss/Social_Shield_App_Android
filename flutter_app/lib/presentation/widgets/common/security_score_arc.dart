import 'dart:math' as math;

import 'package:flutter/material.dart';

/// A circular arc widget that visually represents a security score (0–100).
///
/// The arc sweeps 270° from the bottom-left to the bottom-right, filling
/// progressively from 0 to [score] with an animated gradient that transitions:
/// red (danger) → orange (medium) → green (safe).
class SecurityScoreArc extends StatefulWidget {
  const SecurityScoreArc({
    super.key,
    required this.score,
    this.size = 180,
    this.strokeWidth = 14,
    this.animationDuration = const Duration(milliseconds: 1400),
    this.animationCurve = Curves.easeOutCubic,
    this.label = 'Security Score',
    this.showLabel = true,
  });

  /// Score from 0 to 100.
  final double score;
  final double size;
  final double strokeWidth;
  final Duration animationDuration;
  final Curve animationCurve;
  final String label;
  final bool showLabel;

  @override
  State<SecurityScoreArc> createState() => _SecurityScoreArcState();
}

class _SecurityScoreArcState extends State<SecurityScoreArc>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _progressAnim;
  late Animation<double> _fadeAnim;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: widget.animationDuration,
    );
    _progressAnim = Tween<double>(begin: 0, end: widget.score / 100).animate(
      CurvedAnimation(parent: _controller, curve: widget.animationCurve),
    );
    _fadeAnim = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.6, 1.0, curve: Curves.easeIn),
      ),
    );
    _controller.forward();
  }

  @override
  void didUpdateWidget(SecurityScoreArc old) {
    super.didUpdateWidget(old);
    if (old.score != widget.score) {
      _progressAnim =
          Tween<double>(begin: _progressAnim.value, end: widget.score / 100)
              .animate(
        CurvedAnimation(parent: _controller, curve: widget.animationCurve),
      );
      _controller
        ..reset()
        ..forward();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  /// Interpolate colour based on score progress (0→1).
  Color _scoreColor(double progress) {
    if (progress < 0.4) {
      return Color.lerp(
          const Color(0xFFFF3CAC), const Color(0xFFFF8C42), progress / 0.4)!;
    } else if (progress < 0.7) {
      return Color.lerp(const Color(0xFFFF8C42), const Color(0xFFFFD166),
          (progress - 0.4) / 0.3)!;
    } else {
      return Color.lerp(const Color(0xFFFFD166), const Color(0xFF06FFA5),
          (progress - 0.7) / 0.3)!;
    }
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, _) {
        final progress = _progressAnim.value;
        final color = _scoreColor(progress);
        final scoreInt = (progress * widget.score.abs()).round();

        return SizedBox(
          width: widget.size,
          height: widget.size,
          child: Stack(
            alignment: Alignment.center,
            children: [
              // ── Arc painter ────────────────────────────────────────────────
              CustomPaint(
                size: Size(widget.size, widget.size),
                painter: _ArcPainter(
                  progress: progress,
                  strokeWidth: widget.strokeWidth,
                  scoreColor: color,
                ),
              ),

              // ── Centre text ────────────────────────────────────────────────
              FadeTransition(
                opacity: _fadeAnim,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      '$scoreInt',
                      style: TextStyle(
                        color: color,
                        fontSize: widget.size * 0.26,
                        fontWeight: FontWeight.w800,
                        fontFamily: 'Inter',
                        height: 1,
                      ),
                    ),
                    Text(
                      '/100',
                      style: TextStyle(
                        color: Colors.white38,
                        fontSize: widget.size * 0.09,
                        fontWeight: FontWeight.w500,
                        fontFamily: 'Inter',
                      ),
                    ),
                  ],
                ),
              ),

              // ── Score label below arc ──────────────────────────────────────
              if (widget.showLabel)
                Positioned(
                  bottom: widget.size * 0.03,
                  child: FadeTransition(
                    opacity: _fadeAnim,
                    child: Text(
                      widget.label,
                      style: TextStyle(
                        color: Colors.white54,
                        fontSize: widget.size * 0.075,
                        fontWeight: FontWeight.w500,
                        fontFamily: 'Inter',
                        letterSpacing: 0.3,
                      ),
                    ),
                  ),
                ),
            ],
          ),
        );
      },
    );
  }
}

// ─── Custom painter ────────────────────────────────────────────────────────

class _ArcPainter extends CustomPainter {
  _ArcPainter({
    required this.progress,
    required this.strokeWidth,
    required this.scoreColor,
  });

  final double progress;
  final double strokeWidth;
  final Color scoreColor;

  // Arc sweeps 270° starting at 135° (bottom-left) clockwise.
  static const double _startAngle = 135 * math.pi / 180;
  static const double _totalSweep = 270 * math.pi / 180;

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = (size.width / 2) - strokeWidth / 2;
    final rect = Rect.fromCircle(center: center, radius: radius);

    // ── Track (background ring) ────────────────────────────────────────────
    final trackPaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..color = const Color(0xFF1E1E3A);

    canvas.drawArc(rect, _startAngle, _totalSweep, false, trackPaint);

    if (progress <= 0) return;

    // ── Filled arc with shader ────────────────────────────────────────────
    final sweepAngle = _totalSweep * progress;

    final gradientPaint = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round
      ..shader = SweepGradient(
        center: Alignment.center,
        startAngle: _startAngle,
        endAngle: _startAngle + sweepAngle,
        colors: [
          scoreColor.withAlpha(180),
          scoreColor,
        ],
      ).createShader(rect);

    canvas.drawArc(rect, _startAngle, sweepAngle, false, gradientPaint);

    // ── Glowing dot at the arc tip ────────────────────────────────────────
    final tipAngle = _startAngle + sweepAngle;
    final tipOffset = Offset(
      center.dx + radius * math.cos(tipAngle),
      center.dy + radius * math.sin(tipAngle),
    );

    canvas.drawCircle(
      tipOffset,
      strokeWidth / 2,
      Paint()
        ..color = scoreColor
        ..maskFilter = const MaskFilter.blur(BlurStyle.normal, 6),
    );
    canvas.drawCircle(
      tipOffset,
      strokeWidth / 2.5,
      Paint()..color = Colors.white,
    );
  }

  @override
  bool shouldRepaint(_ArcPainter old) =>
      old.progress != progress || old.scoreColor != scoreColor;
}
