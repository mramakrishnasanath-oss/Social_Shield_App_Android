import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/user_model.dart';
import '../repositories/auth_repository.dart';

final authStateProvider = StreamProvider<User?>((ref) {
  return ref.watch(authRepositoryProvider).authStateChanges;
});

final userProfileProvider = FutureProvider<UserModel?>((ref) async {
  final user = ref.watch(authStateProvider).value;
  if (user == null) return null;
  
  final result = await ref.watch(authRepositoryProvider).getUserProfile(user.uid);
  return result.fold(
    (failure) => null, // Handle error appropriately in UI
    (profile) => profile,
  );
});

class AuthState {
  final bool isLoading;
  final String? error;

  AuthState({this.isLoading = false, this.error});

  AuthState copyWith({bool? isLoading, String? error}) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      error: error ?? this.error,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthRepository _repository;

  AuthNotifier(this._repository) : super(AuthState());

  Future<bool> signIn(String email, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    final result = await _repository.signInWithEmail(email, password);
    state = result.fold(
      (failure) => state.copyWith(isLoading: false, error: failure.message),
      (_) => state.copyWith(isLoading: false),
    );
    return result.isRight();
  }

  Future<bool> signUp(String email, String password, String name) async {
    state = state.copyWith(isLoading: true, error: null);
    final result = await _repository.signUpWithEmail(email, password, name);
    state = result.fold(
      (failure) => state.copyWith(isLoading: false, error: failure.message),
      (_) => state.copyWith(isLoading: false),
    );
    return result.isRight();
  }

  Future<bool> signInWithGoogle() async {
    state = state.copyWith(isLoading: true, error: null);
    final result = await _repository.signInWithGoogle();
    state = result.fold(
      (failure) => state.copyWith(isLoading: false, error: failure.message),
      (_) => state.copyWith(isLoading: false),
    );
    return result.isRight();
  }

  Future<void> signOut() async {
    state = state.copyWith(isLoading: true);
    await _repository.signOut();
    state = state.copyWith(isLoading: false);
  }

  Future<bool> resetPassword(String email) async {
    state = state.copyWith(isLoading: true, error: null);
    final result = await _repository.resetPassword(email);
    state = result.fold(
      (failure) => state.copyWith(isLoading: false, error: failure.message),
      (_) => state.copyWith(isLoading: false),
    );
    return result.isRight();
  }
}

final authNotifierProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier(ref.watch(authRepositoryProvider));
});
