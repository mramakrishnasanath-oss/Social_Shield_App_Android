import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/theme/app_colors.dart';
import '../../../core/utils/validators.dart';
import '../../../core/utils/extensions.dart';
import '../../../providers/auth_provider.dart';
import '../../../services/localization_service.dart';
import '../../widgets/common/glass_card.dart';
import '../../widgets/common/neon_button.dart';

class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  void _login() async {
    if (_formKey.currentState!.validate()) {
      final success = await ref.read(authNotifierProvider.notifier).signIn(
        _emailController.text.trim(),
        _passwordController.text,
      );
      
      if (success && mounted) {
        context.go('/home/dashboard');
      } else if (mounted) {
        final error = ref.read(authNotifierProvider).error;
        if (error != null) {
          context.showSnackBar(error, isError: true);
        }
      }
    }
  }

  void _loginWithGoogle() async {
    final success = await ref.read(authNotifierProvider.notifier).signInWithGoogle();
    if (success && mounted) {
      context.go('/home/dashboard');
    } else if (mounted) {
      final error = ref.read(authNotifierProvider).error;
      if (error != null) {
        context.showSnackBar(error, isError: true);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authNotifierProvider);
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Premium Brand Logo Asset
                Image.asset(
                  'assets/images/app_logo.png',
                  height: 100,
                  width: 100,
                  errorBuilder: (context, error, stackTrace) {
                    return Icon(
                      Icons.shield,
                      size: 80,
                      color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                    );
                  },
                ),
                const SizedBox(height: 16),
                Text(
                  Trans.of(context, 'login'),
                  style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  'Sign in to continue protecting your digital life',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 32),

                // Login Form
                GlassCard(
                  child: Form(
                    key: _formKey,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        TextFormField(
                          controller: _emailController,
                          keyboardType: TextInputType.emailAddress,
                          decoration: InputDecoration(
                            labelText: Trans.of(context, 'email'),
                            prefixIcon: const Icon(Icons.email_outlined),
                          ),
                          validator: Validators.email,
                        ),
                        const SizedBox(height: 16),
                        TextFormField(
                          controller: _passwordController,
                          obscureText: _obscurePassword,
                          decoration: InputDecoration(
                            labelText: Trans.of(context, 'password'),
                            prefixIcon: const Icon(Icons.lock_outline),
                            suffixIcon: IconButton(
                              icon: Icon(
                                _obscurePassword ? Icons.visibility_off : Icons.visibility,
                                color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary,
                              ),
                              onPressed: () {
                                setState(() {
                                  _obscurePassword = !_obscurePassword;
                                });
                              },
                            ),
                          ),
                          validator: Validators.password,
                        ),
                        const SizedBox(height: 8),
                        Align(
                          alignment: Alignment.centerRight,
                          child: TextButton(
                            onPressed: () => context.push('/auth/forgot-password'),
                            child: Text(
                              Trans.of(context, 'forgot_password'),
                              style: TextStyle(
                                color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                                fontWeight: FontWeight.w600
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(height: 16),
                        NeonButton(
                          text: Trans.of(context, 'login'),
                          isLoading: authState.isLoading,
                          onPressed: _login,
                        ),
                        const SizedBox(height: 24),
                        Row(
                          children: [
                            const Expanded(child: Divider(color: AppColors.glassBorder)),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 16),
                              child: Text('OR', style: Theme.of(context).textTheme.bodySmall),
                            ),
                            const Expanded(child: Divider(color: AppColors.glassBorder)),
                          ],
                        ),
                        const SizedBox(height: 24),
                        NeonButton(
                          text: Trans.of(context, 'google_signin'),
                          isSecondary: true,
                          icon: Icons.g_mobiledata,
                          isLoading: authState.isLoading,
                          onPressed: _loginWithGoogle,
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      "Don't have an account?", 
                      style: TextStyle(
                        color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary
                      )
                    ),
                    TextButton(
                      onPressed: () => context.push('/auth/register'),
                      child: Text(
                        Trans.of(context, 'signup'),
                        style: TextStyle(
                          color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary, 
                          fontWeight: FontWeight.bold
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
