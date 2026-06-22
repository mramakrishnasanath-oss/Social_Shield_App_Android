import 'dart:io';
import 'package:dio/dio.dart';
import 'package:dartz/dartz.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/errors/failures.dart';
import '../core/network/dio_client.dart';
import '../config/app_config.dart';
import '../models/scan_result_model.dart';
import '../models/user_model.dart';

class ApiService {
  final DioClient _dioClient;

  ApiService(this._dioClient);

  Future<Either<Failure, ScanResultModel>> scanImage(File file) async {
    try {
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(file.path, filename: file.path.split(RegExp(r'[/\\]')).last),
      });

      final response = await _dioClient.postFormData(AppConfig.endpointScanImage, formData);
      return Right(ScanResultModel.fromJson(response.data));
    } on DioException catch (e) {
      return Left(ServerFailure(e.error.toString()));
    } catch (e) {
      return Left(ServerFailure('An unexpected error occurred: $e'));
    }
  }

  Future<Either<Failure, ScanResultModel>> scanVideo(File file) async {
    try {
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(file.path, filename: file.path.split(RegExp(r'[/\\]')).last),
      });

      final response = await _dioClient.postFormData(AppConfig.endpointScanVideo, formData);
      return Right(ScanResultModel.fromJson(response.data));
    } on DioException catch (e) {
      return Left(ServerFailure(e.error.toString()));
    } catch (e) {
      return Left(ServerFailure('An unexpected error occurred: $e'));
    }
  }

  Future<Either<Failure, ScanResultModel>> scanUrl(String url) async {
    try {
      final response = await _dioClient.post(AppConfig.endpointScanUrl, data: {'url': url});
      return Right(ScanResultModel.fromJson(response.data));
    } on DioException catch (e) {
      return Left(ServerFailure(e.error.toString()));
    } catch (e) {
      return Left(ServerFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<Either<Failure, ScanResultModel>> scanText(String text) async {
    try {
      final response = await _dioClient.post(AppConfig.endpointScanText, data: {'text': text});
      return Right(ScanResultModel.fromJson(response.data));
    } on DioException catch (e) {
      return Left(ServerFailure(e.error.toString()));
    } catch (e) {
      return Left(ServerFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<Either<Failure, List<ScanResultModel>>> getHistory() async {
    try {
      final response = await _dioClient.get(AppConfig.endpointHistory);
      final List<dynamic> data = response.data['history'] ?? [];
      final history = data.map((json) => ScanResultModel.fromJson(json)).toList();
      return Right(history);
    } on DioException catch (e) {
      return Left(ServerFailure(e.error.toString()));
    } catch (e) {
      return Left(ServerFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<Either<Failure, UserModel>> getProfile() async {
    try {
      final response = await _dioClient.get(AppConfig.endpointProfile);
      return Right(UserModel.fromJson(response.data));
    } on DioException catch (e) {
      return Left(ServerFailure(e.error.toString()));
    } catch (e) {
      return Left(ServerFailure('An unexpected error occurred: \$e'));
    }
  }
}

final apiServiceProvider = Provider<ApiService>((ref) {
  return ApiService(ref.watch(dioClientProvider));
});
