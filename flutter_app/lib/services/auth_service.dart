import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:dartz/dartz.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../core/constants/app_constants.dart';
import '../core/errors/failures.dart';

class AuthService {
  final FirebaseAuth _firebaseAuth;
  final GoogleSignIn _googleSignIn;

  AuthService(this._firebaseAuth, this._googleSignIn);

  Stream<User?> get authStateChanges => _firebaseAuth.authStateChanges();
  User? get currentUser => _firebaseAuth.currentUser;

  Future<Either<Failure, User>> signInWithEmail(String email, String password) async {
    try {
      final userCredential = await _firebaseAuth.signInWithEmailAndPassword(
        email: email,
        password: password,
      );
      if (userCredential.user != null) {
        await _saveToken(userCredential.user!);
        return Right(userCredential.user!);
      }
      return const Left(AuthFailure('Login failed. Please try again.'));
    } on FirebaseAuthException catch (e) {
      return Left(AuthFailure(_getAuthExceptionMessage(e)));
    } catch (e) {
      return Left(AuthFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<Either<Failure, User>> signUpWithEmail(String email, String password, String name) async {
    try {
      final userCredential = await _firebaseAuth.createUserWithEmailAndPassword(
        email: email,
        password: password,
      );
      if (userCredential.user != null) {
        await userCredential.user!.updateDisplayName(name);
        await _saveToken(userCredential.user!);
        return Right(userCredential.user!);
      }
      return const Left(AuthFailure('Registration failed. Please try again.'));
    } on FirebaseAuthException catch (e) {
      return Left(AuthFailure(_getAuthExceptionMessage(e)));
    } catch (e) {
      return Left(AuthFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<Either<Failure, User>> signInWithGoogle() async {
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        return const Left(AuthFailure('Google Sign-In was cancelled.'));
      }

      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      
      if (googleAuth.idToken == null && googleAuth.accessToken == null) {
        return const Left(AuthFailure(
          'Google authentication failed: Missing ID or Access Token. '
          'Please ensure that your Android SHA-1 fingerprint is registered in the Firebase Console.'
        ));
      }

      final AuthCredential credential = GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );

      final userCredential = await _firebaseAuth.signInWithCredential(credential);
      if (userCredential.user != null) {
        await _saveToken(userCredential.user!);
        return Right(userCredential.user!);
      }
      return const Left(AuthFailure('Google sign in failed.'));
    } on FirebaseAuthException catch (e) {
      if (e.code == 'wrong-credentials' || e.code == 'invalid-credential') {
        return const Left(AuthFailure(
          'Firebase Auth credential error. '
          'Please verify that Google Sign-In is enabled in the Firebase Console and your SHA-1 is correct.'
        ));
      }
      return Left(AuthFailure(_getAuthExceptionMessage(e)));
    } catch (e) {
      return Left(AuthFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<Either<Failure, void>> signOut() async {
    try {
      await _googleSignIn.signOut();
      await _firebaseAuth.signOut();
      final prefs = await SharedPreferences.getInstance();
      await prefs.remove(AppConstants.keyAuthToken);
      return const Right(null);
    } catch (e) {
      return Left(AuthFailure('Error signing out: \$e'));
    }
  }

  Future<Either<Failure, void>> resetPassword(String email) async {
    try {
      await _firebaseAuth.sendPasswordResetEmail(email: email);
      return const Right(null);
    } on FirebaseAuthException catch (e) {
      return Left(AuthFailure(_getAuthExceptionMessage(e)));
    } catch (e) {
      return Left(AuthFailure('An unexpected error occurred: \$e'));
    }
  }

  Future<void> _saveToken(User user) async {
    try {
      final token = await user.getIdToken();
      if (token != null) {
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString(AppConstants.keyAuthToken, token);
      }
    } catch (_) {}
  }

  String _getAuthExceptionMessage(FirebaseAuthException e) {
    switch (e.code) {
      case 'user-not-found':
        return 'No user found for that email.';
      case 'wrong-password':
        return 'Wrong password provided.';
      case 'email-already-in-use':
        return 'The account already exists for that email.';
      case 'invalid-email':
        return 'The email address is invalid.';
      case 'weak-password':
        return 'The password provided is too weak.';
      case 'network-request-failed':
        return 'Network error. Please check your connection.';
      default:
        return 'Authentication error: \${e.message}';
    }
  }
}

final authServiceProvider = Provider<AuthService>((ref) {
  return AuthService(FirebaseAuth.instance, GoogleSignIn());
});
