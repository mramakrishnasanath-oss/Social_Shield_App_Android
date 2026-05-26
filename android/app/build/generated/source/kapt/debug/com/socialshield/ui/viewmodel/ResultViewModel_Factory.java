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
public final class ResultViewModel_Factory implements Factory<ResultViewModel> {
  private final Provider<ScanRepository> repoProvider;

  public ResultViewModel_Factory(Provider<ScanRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public ResultViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static ResultViewModel_Factory create(Provider<ScanRepository> repoProvider) {
    return new ResultViewModel_Factory(repoProvider);
  }

  public static ResultViewModel newInstance(ScanRepository repo) {
    return new ResultViewModel(repo);
  }
}
