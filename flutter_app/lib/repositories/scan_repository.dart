import 'dart:io';
import 'package:dartz/dartz.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/errors/failures.dart';
import '../models/scan_result_model.dart';
import '../services/api_service.dart';
import '../services/firestore_service.dart';

abstract class ScanRepository {
  Future<Either<Failure, ScanResultModel>> scanImage(File file, String userId);
  Future<Either<Failure, ScanResultModel>> scanUrl(String url, String userId);
  Future<Either<Failure, ScanResultModel>> scanText(String text, String userId);
  Future<Either<Failure, List<ScanResultModel>>> getScanHistory(String userId);
}

class ScanRepositoryImpl implements ScanRepository {
  final ApiService _apiService;
  final FirestoreService _firestoreService;

  ScanRepositoryImpl(this._apiService, this._firestoreService);

  @override
  Future<Either<Failure, ScanResultModel>> scanImage(File file, String userId) async {
    final result = await _apiService.scanImage(file);
    return _handleScanResult(result, userId);
  }

  @override
  Future<Either<Failure, ScanResultModel>> scanUrl(String url, String userId) async {
    final result = await _apiService.scanUrl(url);
    return _handleScanResult(result, userId);
  }

  @override
  Future<Either<Failure, ScanResultModel>> scanText(String text, String userId) async {
    final result = await _apiService.scanText(text);
    return _handleScanResult(result, userId);
  }

  @override
  Future<Either<Failure, List<ScanResultModel>>> getScanHistory(String userId) async {
    // Try to get from Firestore first for the specific user
    final firestoreResult = await _firestoreService.getScanHistory(userId);
    
    return firestoreResult.fold(
      (failure) async {
        // Fallback to API if firestore fails (e.g. not initialized)
        return await _apiService.getHistory();
      },
      (history) => Right(history),
    );
  }

  Future<Either<Failure, ScanResultModel>> _handleScanResult(
    Either<Failure, ScanResultModel> apiResult, 
    String userId
  ) async {
    return apiResult.fold(
      (failure) => Left(failure),
      (scan) async {
        // Attach user ID and save to Firestore
        final modelWithUser = ScanResultModel(
          scanId: scan.scanId,
          userId: userId,
          mediaType: scan.mediaType,
          verdict: scan.verdict,
          confidence: scan.confidence,
          fakeProbability: scan.fakeProbability,
          realProbability: scan.realProbability,
          riskLevel: scan.riskLevel,
          explanations: scan.explanations,
          metadata: scan.metadata,
          timestamp: scan.timestamp,
        );
        
        // Save to firestore asynchronously without waiting
        _firestoreService.saveScanResult(modelWithUser);
        
        return Right(modelWithUser);
      },
    );
  }
}

final scanRepositoryProvider = Provider<ScanRepository>((ref) {
  return ScanRepositoryImpl(
    ref.watch(apiServiceProvider),
    ref.watch(firestoreServiceProvider),
  );
});
