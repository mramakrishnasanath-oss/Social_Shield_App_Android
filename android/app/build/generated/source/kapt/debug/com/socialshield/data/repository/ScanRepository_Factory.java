package com.socialshield.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.socialshield.data.api.SocialShieldApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ScanRepository_Factory implements Factory<ScanRepository> {
  private final Provider<SocialShieldApi> apiProvider;

  private final Provider<FirebaseAuth> authProvider;

  public ScanRepository_Factory(Provider<SocialShieldApi> apiProvider,
      Provider<FirebaseAuth> authProvider) {
    this.apiProvider = apiProvider;
    this.authProvider = authProvider;
  }

  @Override
  public ScanRepository get() {
    return newInstance(apiProvider.get(), authProvider.get());
  }

  public static ScanRepository_Factory create(Provider<SocialShieldApi> apiProvider,
      Provider<FirebaseAuth> authProvider) {
    return new ScanRepository_Factory(apiProvider, authProvider);
  }

  public static ScanRepository newInstance(SocialShieldApi api, FirebaseAuth auth) {
    return new ScanRepository(api, auth);
  }
}
