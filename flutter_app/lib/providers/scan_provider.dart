import 'dart:io';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/scan_result_model.dart';
import '../repositories/scan_repository.dart';
import 'auth_provider.dart';

abstract class ScanState {}
class ScanInitial extends ScanState {}
class ScanLoading extends ScanState {}
class ScanSuccess extends ScanState {
  final ScanResultModel result;
  ScanSuccess(this.result);
}
class ScanError extends ScanState {
  final String message;
  ScanError(this.message);
}

class ScanNotifier extends StateNotifier<ScanState> {
  final ScanRepository _repository;
  final String _userId;

  ScanNotifier(this._repository, this._userId) : super(ScanInitial());

  void reset() {
    state = ScanInitial();
  }

  Future<void> scanImage(File file) async {
    state = ScanLoading();
    final result = await _repository.scanImage(file, _userId);
    state = result.fold(
      (failure) => ScanError(failure.message),
      (scanResult) => ScanSuccess(scanResult),
    );
  }

  Future<void> scanUrl(String url) async {
    state = ScanLoading();
    final result = await _repository.scanUrl(url, _userId);
    state = result.fold(
      (failure) => ScanError(failure.message),
      (scanResult) => ScanSuccess(scanResult),
    );
  }

  Future<void> scanText(String text) async {
    state = ScanLoading();
    final result = await _repository.scanText(text, _userId);
    state = result.fold(
      (failure) => ScanError(failure.message),
      (scanResult) => ScanSuccess(scanResult),
    );
  }
}

final scanNotifierProvider = StateNotifierProvider<ScanNotifier, ScanState>((ref) {
  final user = ref.watch(authStateProvider).value;
  return ScanNotifier(
    ref.watch(scanRepositoryProvider), 
    user?.uid ?? 'dev_user',
  );
});

final scanHistoryProvider = FutureProvider<List<ScanResultModel>>((ref) async {
  final user = ref.watch(authStateProvider).value;
  if (user == null) return [];
  
  final result = await ref.watch(scanRepositoryProvider).getScanHistory(user.uid);
  return result.fold(
    (failure) => [],
    (history) => history,
  );
});
