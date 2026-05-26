package com.socialshield.ui.viewmodel;

import android.content.Context;
import com.socialshield.data.repository.ScanRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ScanViewModel_Factory implements Factory<ScanViewModel> {
  private final Provider<ScanRepository> repoProvider;

  private final Provider<Context> contextProvider;

  public ScanViewModel_Factory(Provider<ScanRepository> repoProvider,
      Provider<Context> contextProvider) {
    this.repoProvider = repoProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ScanViewModel get() {
    return newInstance(repoProvider.get(), contextProvider.get());
  }

  public static ScanViewModel_Factory create(Provider<ScanRepository> repoProvider,
      Provider<Context> contextProvider) {
    return new ScanViewModel_Factory(repoProvider, contextProvider);
  }

  public static ScanViewModel newInstance(ScanRepository repo, Context context) {
    return new ScanViewModel(repo, context);
  }
}
