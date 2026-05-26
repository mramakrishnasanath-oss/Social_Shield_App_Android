package com.socialshield.ui.viewmodel;

import com.google.firebase.auth.FirebaseAuth;
import com.socialshield.data.repository.ScanRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ScanRepository> repoProvider;

  private final Provider<FirebaseAuth> authProvider;

  public HomeViewModel_Factory(Provider<ScanRepository> repoProvider,
      Provider<FirebaseAuth> authProvider) {
    this.repoProvider = repoProvider;
    this.authProvider = authProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repoProvider.get(), authProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ScanRepository> repoProvider,
      Provider<FirebaseAuth> authProvider) {
    return new HomeViewModel_Factory(repoProvider, authProvider);
  }

  public static HomeViewModel newInstance(ScanRepository repo, FirebaseAuth auth) {
    return new HomeViewModel(repo, auth);
  }
}
