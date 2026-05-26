package com.socialshield.ui.viewmodel;

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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<ScanRepository> repoProvider;

  public HistoryViewModel_Factory(Provider<ScanRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<ScanRepository> repoProvider) {
    return new HistoryViewModel_Factory(repoProvider);
  }

  public static HistoryViewModel newInstance(ScanRepository repo) {
    return new HistoryViewModel(repo);
  }
}
