import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:dartz/dartz.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/errors/failures.dart';
import '../models/scan_result_model.dart';
import '../models/user_model.dart';

class FirestoreService {
  final FirebaseFirestore _firestore;

  FirestoreService(this._firestore);

  // User Profile Methods
  Future<Either<Failure, void>> saveUserProfile(UserModel user) async {
    try {
      await _firestore.collection('users').doc(user.uid).set(user.toJson());
      return const Right(null);
    } catch (e) {
      return Left(ServerFailure('Failed to save profile: \$e'));
    }
  }

  Future<Either<Failure, UserModel>> getUserProfile(String uid) async {
    try {
      final doc = await _firestore.collection('users').doc(uid).get();
      if (doc.exists && doc.data() != null) {
        return Right(UserModel.fromJson(doc.data()!));
      }
      return const Left(ServerFailure('User profile not found'));
    } catch (e) {
      return Left(ServerFailure('Failed to fetch profile: \$e'));
    }
  }

  Future<Either<Failure, void>> updateSecurityScore(String uid, int score) async {
    try {
      await _firestore.collection('users').doc(uid).update({'securityScore': score});
      return const Right(null);
    } catch (e) {
      return Left(ServerFailure('Failed to update score: \$e'));
    }
  }

  // Scan History Methods
  Future<Either<Failure, void>> saveScanResult(ScanResultModel scanResult) async {
    try {
      await _firestore
          .collection('users')
          .doc(scanResult.userId)
          .collection('scans')
          .doc(scanResult.scanId)
          .set(scanResult.toJson());
          
      // Increment counters
      await _firestore.collection('users').doc(scanResult.userId).update({
        'scansCount': FieldValue.increment(1),
        if (scanResult.isFake || scanResult.isSuspicious) 
          'threatsDetected': FieldValue.increment(1),
      });
      
      return const Right(null);
    } catch (e) {
      return Left(ServerFailure('Failed to save scan result: \$e'));
    }
  }

  Future<Either<Failure, List<ScanResultModel>>> getScanHistory(String uid) async {
    try {
      final snapshot = await _firestore
          .collection('users')
          .doc(uid)
          .collection('scans')
          .orderBy('timestamp', descending: true)
          .limit(50)
          .get();
          
      final scans = snapshot.docs.map((doc) => ScanResultModel.fromJson(doc.data())).toList();
      return Right(scans);
    } catch (e) {
      return Left(ServerFailure('Failed to fetch scan history: \$e'));
    }
  }
}

final firestoreServiceProvider = Provider<FirestoreService>((ref) {
  return FirestoreService(FirebaseFirestore.instance);
});
