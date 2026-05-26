package com.socialshield;

import com.google.firebase.auth.FirebaseAuth;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<FirebaseAuth> authProvider;

  public MainActivity_MembersInjector(Provider<FirebaseAuth> authProvider) {
    this.authProvider = authProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<FirebaseAuth> authProvider) {
    return new MainActivity_MembersInjector(authProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectAuth(instance, authProvider.get());
  }

  @InjectedFieldSignature("com.socialshield.MainActivity.auth")
  public static void injectAuth(MainActivity instance, FirebaseAuth auth) {
    instance.auth = auth;
  }
}
