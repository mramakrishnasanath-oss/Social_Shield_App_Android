import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter_animate/flutter_animate.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/utils/extensions.dart';
import '../../../providers/scan_provider.dart';
import '../../../services/localization_service.dart';
import '../../widgets/common/glass_card.dart';
import '../../widgets/common/neon_button.dart';

class ScanScreen extends ConsumerStatefulWidget {
  const ScanScreen({super.key});

  @override
  ConsumerState<ScanScreen> createState() => _ScanScreenState();
}

class _ScanScreenState extends ConsumerState<ScanScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  File? _selectedImage;
  File? _selectedVideo;
  final _urlController = TextEditingController();
  final _textController = TextEditingController();
  String _currentStep = 'Initializing scan engines...';

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _urlController.dispose();
    _textController.dispose();
    super.dispose();
  }

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);
    if (pickedFile != null) {
      setState(() {
        _selectedImage = File(pickedFile.path);
      });
    }
  }

  Future<void> _pickVideo(ImageSource source) async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickVideo(source: source);
    if (pickedFile != null) {
      setState(() {
        _selectedVideo = File(pickedFile.path);
      });
    }
  }

  void _simulateProgressSteps() async {
    final steps = [
      'Extracting package metadata...',
      'Validating registry reputation...',
      'Running local neural classifiers...',
      'Finalizing risk rating score...',
    ];

    for (var step in steps) {
      if (!mounted || ref.read(scanNotifierProvider) is! ScanLoading) break;
      setState(() {
        _currentStep = step;
      });
      await Future.delayed(const Duration(milliseconds: 200));
    }
  }

  void _handleScanState(ScanState state) {
    if (state is ScanSuccess) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        context.push('/result', extra: state.result);
        ref.read(scanNotifierProvider.notifier).reset();
      });
    } else if (state is ScanError) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        context.showSnackBar(state.message, isError: true);
        ref.read(scanNotifierProvider.notifier).reset();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final scanState = ref.watch(scanNotifierProvider);
    final isDark = Theme.of(context).brightness == Brightness.dark;

    // Listen for state changes to navigate
    ref.listen(scanNotifierProvider, (previous, next) {
      if (next is ScanLoading) {
        _simulateProgressSteps();
      }
      _handleScanState(next);
    });

    if (scanState is ScanLoading) {
      return Scaffold(
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(32.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Stack(
                  alignment: Alignment.center,
                  children: [
                    SizedBox(
                      height: 120,
                      width: 120,
                      child: CircularProgressIndicator(
                        strokeWidth: 6,
                        valueColor: AlwaysStoppedAnimation<Color>(
                          isDark ? AppColors.darkPrimary : AppColors.lightPrimary
                        ),
                      ),
                    ),
                    Icon(
                      Icons.security_rounded,
                      size: 48,
                      color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                    ).animate(onPlay: (controller) => controller.repeat())
                     .shimmer(duration: 1200.ms, color: Colors.white24),
                  ],
                ),
                const SizedBox(height: 32),
                Text(
                  'SCANNING IN PROGRESS',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    letterSpacing: 1.2,
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  _currentStep,
                  style: const TextStyle(color: Colors.grey, fontSize: 14),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(Trans.of(context, 'scan')),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
          labelColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
          unselectedLabelColor: isDark ? AppColors.darkTextMuted : AppColors.lightTextMuted,
          tabs: [
            Tab(icon: const Icon(Icons.image), text: Trans.of(context, 'image') != 'image' ? Trans.of(context, 'image') : 'Image'),
            Tab(icon: const Icon(Icons.link), text: Trans.of(context, 'link') != 'link' ? Trans.of(context, 'link') : 'URL'),
            Tab(icon: const Icon(Icons.text_snippet), text: Trans.of(context, 'text') != 'text' ? Trans.of(context, 'text') : 'Text'),
            Tab(icon: const Icon(Icons.videocam), text: 'Video'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildImageScanner(scanState),
          _buildUrlScanner(scanState),
          _buildTextScanner(scanState),
          _buildVideoScanner(scanState),
        ],
      ),
    );
  }

  Widget _buildImageScanner(ScanState state) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          GestureDetector(
            onTap: _pickImage,
            child: Container(
              height: 250,
              width: double.infinity,
              decoration: BoxDecoration(
                color: isDark ? AppColors.glassWhite : Colors.black.withOpacity(0.02),
                borderRadius: BorderRadius.circular(24),
                border: Border.all(
                  color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary, 
                  width: 2, 
                  style: BorderStyle.solid
                ),
              ),
              child: _selectedImage != null
                  ? ClipRRect(
                      borderRadius: BorderRadius.circular(22),
                      child: Image.file(_selectedImage!, fit: BoxFit.cover),
                    )
                  : Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.cloud_upload_outlined, 
                          size: 64, 
                          color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary
                        ),
                        const SizedBox(height: 16),
                        Text(
                          Trans.of(context, 'tap_to_select') != 'tap_to_select' ? Trans.of(context, 'tap_to_select') : 'Tap to select image', 
                          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)
                        ),
                        const SizedBox(height: 8),
                        const Text('JPG, PNG up to 20MB', style: TextStyle(color: Colors.grey)),
                      ],
                    ),
            ),
          ),
          const SizedBox(height: 32),
          NeonButton(
            text: '${Trans.of(context, 'scan')} Image',
            icon: Icons.search,
            isLoading: state is ScanLoading,
            onPressed: _selectedImage == null
                ? null
                : () {
                    ref.read(scanNotifierProvider.notifier).scanImage(_selectedImage!);
                  },
          ),
        ],
      ),
    );
  }

  Widget _buildUrlScanner(ScanState state) {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          GlassCard(
            child: Column(
              children: [
                const Icon(Icons.link_rounded, size: 64, color: AppColors.riskMedium),
                const SizedBox(height: 24),
                const Text(
                  'Phishing & Fraud URL Detector',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 24),
                TextField(
                  controller: _urlController,
                  decoration: const InputDecoration(
                    labelText: 'Enter suspicious URL',
                    prefixIcon: Icon(Icons.public),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),
          NeonButton(
            text: 'Analyze URL',
            icon: Icons.search,
            isLoading: state is ScanLoading,
            onPressed: () {
              if (_urlController.text.isNotEmpty) {
                ref.read(scanNotifierProvider.notifier).scanUrl(_urlController.text);
              }
            },
          ),
        ],
      ),
    );
  }

  Widget _buildTextScanner(ScanState state) {
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        children: [
          const SizedBox(height: 24),
          Expanded(
            child: GlassCard(
              child: Column(
                children: [
                  const Text(
                    'Scam Message Detector',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 16),
                  Expanded(
                    child: TextField(
                      controller: _textController,
                      maxLines: null,
                      expands: true,
                      textAlignVertical: TextAlignVertical.top,
                      decoration: const InputDecoration(
                        hintText: 'Paste suspicious SMS, email, or social media text here...',
                        alignLabelWithHint: true,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 24),
          NeonButton(
            text: 'Analyze Text',
            icon: Icons.search,
            isLoading: state is ScanLoading,
            onPressed: () {
              if (_textController.text.isNotEmpty) {
                ref.read(scanNotifierProvider.notifier).scanText(_textController.text);
              }
            },
          ),
        ],
      ),
    );
  }

  Widget _buildVideoScanner(ScanState state) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Padding(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          GestureDetector(
            onTap: () {
              showModalBottomSheet(
                context: context,
                builder: (context) => SafeArea(
                  child: Wrap(
                    children: [
                      ListTile(
                        leading: const Icon(Icons.video_library),
                        title: const Text('Pick from Gallery'),
                        onTap: () {
                          Navigator.pop(context);
                          _pickVideo(ImageSource.gallery);
                        },
                      ),
                      ListTile(
                        leading: const Icon(Icons.videocam),
                        title: const Text('Record with Camera'),
                        onTap: () {
                          Navigator.pop(context);
                          _pickVideo(ImageSource.camera);
                        },
                      ),
                    ],
                  ),
                ),
              );
            },
            child: Container(
              height: 250,
              width: double.infinity,
              decoration: BoxDecoration(
                color: isDark ? AppColors.glassWhite : Colors.black.withOpacity(0.02),
                borderRadius: BorderRadius.circular(24),
                border: Border.all(
                  color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary, 
                  width: 2, 
                  style: BorderStyle.solid
                ),
              ),
              child: _selectedVideo != null
                  ? Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.video_file_rounded, 
                          size: 64, 
                          color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary
                        ),
                        const SizedBox(height: 16),
                        Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 16.0),
                          child: Text(
                            _selectedVideo!.path.split(RegExp(r'[/\\]')).last, 
                            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                            textAlign: TextAlign.center,
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Size: ${(_selectedVideo!.lengthSync() / (1024 * 1024)).toStringAsFixed(2)} MB',
                          style: const TextStyle(color: Colors.grey),
                        ),
                      ],
                    )
                  : Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.cloud_upload_outlined, 
                          size: 64, 
                          color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary
                        ),
                        const SizedBox(height: 16),
                        const Text(
                          'Tap to select video', 
                          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)
                        ),
                        const SizedBox(height: 8),
                        const Text('MP4, MOV, AVI, MKV up to 50MB', style: TextStyle(color: Colors.grey)),
                      ],
                    ),
            ),
          ),
          const SizedBox(height: 32),
          NeonButton(
            text: 'Analyze Video',
            icon: Icons.search,
            isLoading: state is ScanLoading,
            onPressed: _selectedVideo == null
                ? null
                : () {
                    ref.read(scanNotifierProvider.notifier).scanVideo(_selectedVideo!);
                  },
          ),
        ],
      ),
    );
  }
}
