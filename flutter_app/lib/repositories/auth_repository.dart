import 'package:dartz/dartz.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/errors/failures.dart';
import '../models/user_model.dart';
import '../services/auth_service.dart';
import '../services/firestore_service.dart';

abstract class AuthRepository {
  Stream<User?> get authStateChanges;
  User? get currentUser;
  
  Future<Either<Failure, UserModel>> signInWithEmail(String email, String password);
  Future<Either<Failure, UserModel>> signUpWithEmail(String email, String password, String name);
  Future<Either<Failure, UserModel>> signInWithGoogle();
  Future<Either<Failure, void>> signOut();
  Future<Either<Failure, void>> resetPassword(String email);
  Future<Either<Failure, UserModel>> getUserProfile(String uid);
}

class AuthRepositoryImpl implements AuthRepository {
  final AuthService _authService;
  final FirestoreService _firestoreService;

  AuthRepositoryImpl(this._authService, this._firestoreService);

  @override
  Stream<User?> get authStateChanges => _authService.authStateChanges;

  @override
  User? get currentUser => _authService.currentUser;

  @override
  Future<Either<Failure, UserModel>> signInWithEmail(String email, String password) async {
    final result = await _authService.signInWithEmail(email, password);
    return result.fold(
      (failure) => Left(failure),
      (user) => _getOrCreateUserProfile(user),
    );
  }

  @override
  Future<Either<Failure, UserModel>> signUpWithEmail(String email, String password, String name) async {
    final result = await _authService.signUpWithEmail(email, password, name);
    return result.fold(
      (failure) => Left(failure),
      (user) => _getOrCreateUserProfile(user),
    );
  }

  @override
  Future<Either<Failure, UserModel>> signInWithGoogle() async {
    final result = await _authService.signInWithGoogle();
    return result.fold(
      (failure) => Left(failure),
      (user) => _getOrCreateUserProfile(user),
    );
  }

  @override
  Future<Either<Failure, void>> signOut() {
    return _authService.signOut();
  }

  @override
  Future<Either<Failure, void>> resetPassword(String email) {
    return _authService.resetPassword(email);
  }

  @override
  Future<Either<Failure, UserModel>> getUserProfile(String uid) {
    return _firestoreService.getUserProfile(uid);
  }

  Future<Either<Failure, UserModel>> _getOrCreateUserProfile(User user) async {
    final profileResult = await _firestoreService.getUserProfile(user.uid);
    
    return profileResult.fold(
      (failure) async {
        // If profile doesn't exist, create it
        final newUser = UserModel(
          uid: user.uid,
          email: user.email ?? '',
          displayName: user.displayName,
          photoUrl: user.photoURL,
          createdAt: DateTime.now(),
        );
        
        final saveResult = await _firestoreService.saveUserProfile(newUser);
        return saveResult.fold(
          (f) => Left(f),
          (_) => Right(newUser),
        );
      },
      (userModel) => Right(userModel),
    );
  }
}

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepositoryImpl(
    ref.watch(authServiceProvider),
    ref.watch(firestoreServiceProvider),
  );
});
