import 'package:flutter/material.dart';

/// Dark-theme shimmer placeholder widget for the SocialShield app.
///
/// Usage:
/// ```dart
/// ShimmerLoading(shape: ShimmerShape.box, width: double.infinity, height: 80)
/// ShimmerLoading(shape: ShimmerShape.circle, width: 56, height: 56)
/// ShimmerLoading(shape: ShimmerShape.line, width: 120)
/// ```
enum ShimmerShape { box, circle, line }

class ShimmerLoading extends StatefulWidget {
  const ShimmerLoading({
    super.key,
    this.shape = ShimmerShape.box,
    this.width,
    this.height,
    this.borderRadius = 12,
    this.baseColor = const Color(0xFF1A1A2E),
    this.highlightColor = const Color(0xFF252545),
    this.duration = const Duration(milliseconds: 1400),
  });

  final ShimmerShape shape;
  final double? width;
  final double? height;
  final double borderRadius;

  /// Dark background — keep aligned to the app's surface colour.
  final Color baseColor;

  /// Shimmer sweep colour — slightly lighter than base.
  final Color highlightColor;
  final Duration duration;

  @override
  State<ShimmerLoading> createState() => _ShimmerLoadingState();
}

class _ShimmerLoadingState extends State<ShimmerLoading>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: widget.duration)
      ..repeat();
    _animation = Tween<double>(begin: -1.5, end: 1.5).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOutSine),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final defaultHeight = widget.shape == ShimmerShape.line ? 14.0 : 80.0;
    final effectiveHeight = widget.height ?? defaultHeight;
    final effectiveWidth = widget.width ?? double.infinity;

    final radius = widget.shape == ShimmerShape.circle
        ? BorderRadius.circular(effectiveHeight / 2)
        : BorderRadius.circular(widget.borderRadius);

    return AnimatedBuilder(
      animation: _animation,
      builder: (context, _) {
        return Container(
          width: effectiveWidth,
          height: effectiveHeight,
          decoration: BoxDecoration(
            borderRadius: radius,
            gradient: LinearGradient(
              begin: Alignment(_animation.value - 1, 0),
              end: Alignment(_animation.value, 0),
              colors: [
                widget.baseColor,
                widget.highlightColor,
                widget.baseColor,
              ],
              stops: const [0.0, 0.5, 1.0],
            ),
          ),
        );
      },
    );
  }
}

// ─── Convenience composites ────────────────────────────────────────────────

/// Shimmer placeholder that mimics a scan-history list tile.
class ShimmerListTile extends StatelessWidget {
  const ShimmerListTile({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          const ShimmerLoading(
            shape: ShimmerShape.circle,
            width: 48,
            height: 48,
          ),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const ShimmerLoading(shape: ShimmerShape.line, height: 14),
                const SizedBox(height: 8),
                ShimmerLoading(
                  shape: ShimmerShape.line,
                  width: MediaQuery.of(context).size.width * 0.45,
                  height: 11,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

/// Shimmer placeholder for a stat/analytics card.
class ShimmerCard extends StatelessWidget {
  const ShimmerCard({
    super.key,
    this.height = 120,
  });

  final double height;

  @override
  Widget build(BuildContext context) {
    return ShimmerLoading(
      shape: ShimmerShape.box,
      width: double.infinity,
      height: height,
      borderRadius: 16,
    );
  }
}

/// Builds a list of [count] shimmer list-tile placeholders.
class ShimmerList extends StatelessWidget {
  const ShimmerList({super.key, this.count = 5});

  final int count;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: count,
      itemBuilder: (_, __) => const ShimmerListTile(),
    );
  }
}
