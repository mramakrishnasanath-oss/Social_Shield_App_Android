import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:image_picker/image_picker.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/utils/extensions.dart';
import '../../../providers/scan_provider.dart';
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
  final _urlController = TextEditingController();
  final _textController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
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

  void _handleScanState(ScanState state) {
    if (state is ScanSuccess) {
      context.push('/result', extra: state.result);
      ref.read(scanNotifierProvider.notifier).reset();
    } else if (state is ScanError) {
      context.showSnackBar(state.message, isError: true);
      ref.read(scanNotifierProvider.notifier).reset();
    }
  }

  @override
  Widget build(BuildContext context) {
    final scanState = ref.watch(scanNotifierProvider);
    
    // Listen for state changes to navigate
    ref.listen(scanNotifierProvider, (previous, next) {
      _handleScanState(next);
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('New Scan'),
        bottom: TabBar(
          controller: _tabController,
          indicatorColor: AppColors.neonBlue,
          labelColor: AppColors.neonBlue,
          unselectedLabelColor: AppColors.textMuted,
          tabs: const [
            Tab(icon: Icon(Icons.image), text: 'Image'),
            Tab(icon: Icon(Icons.link), text: 'URL'),
            Tab(icon: Icon(Icons.text_snippet), text: 'Text'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildImageScanner(scanState),
          _buildUrlScanner(scanState),
          _buildTextScanner(scanState),
        ],
      ),
    );
  }

  Widget _buildImageScanner(ScanState state) {
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
                color: AppColors.glassWhite,
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: AppColors.neonBlue, width: 2, style: BorderStyle.solid),
              ),
              child: _selectedImage != null
                  ? ClipRRect(
                      borderRadius: BorderRadius.circular(22),
                      child: Image.file(_selectedImage!, fit: BoxFit.cover),
                    )
                  : const Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.cloud_upload_outlined, size: 64, color: AppColors.neonBlue),
                        SizedBox(height: 16),
                        Text('Tap to select image', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        SizedBox(height: 8),
                        Text('JPG, PNG up to 20MB', style: TextStyle(color: AppColors.textMuted)),
                      ],
                    ),
            ),
          ),
          const SizedBox(height: 32),
          NeonButton(
            text: 'Scan Image',
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
                const Icon(Icons.link_rounded, size: 64, color: AppColors.neonPurple),
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
}
