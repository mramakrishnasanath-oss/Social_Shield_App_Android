import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../core/utils/validators.dart';
import '../../../core/utils/extensions.dart';
import '../../../providers/auth_provider.dart';
import '../../widgets/common/glass_card.dart';
import '../../widgets/common/neon_button.dart';

class ForgotPasswordScreen extends ConsumerWidget {
  const ForgotPasswordScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final formKey = GlobalKey<FormState>();
    final emailController = TextEditingController();
    final authState = ref.watch(authNotifierProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Reset Password')),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            const Text(
              'Enter your email address and we will send you a link to reset your password.',
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 32),
            GlassCard(
              child: Form(
                key: formKey,
                child: Column(
                  children: [
                    TextFormField(
                      controller: emailController,
                      decoration: const InputDecoration(labelText: 'Email', prefixIcon: Icon(Icons.email_outlined)),
                      validator: Validators.email,
                    ),
                    const SizedBox(height: 24),
                    NeonButton(
                      text: 'Send Reset Link',
                      isLoading: authState.isLoading,
                      onPressed: () async {
                        if (formKey.currentState!.validate()) {
                          final success = await ref.read(authNotifierProvider.notifier).resetPassword(emailController.text.trim());
                          if (success && context.mounted) {
                            context.showSnackBar('Password reset link sent! Check your email.');
                            context.pop();
                          } else if (context.mounted) {
                            context.showSnackBar(ref.read(authNotifierProvider).error ?? 'Error', isError: true);
                          }
                        }
                      },
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
