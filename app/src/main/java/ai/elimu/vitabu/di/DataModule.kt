package ai.elimu.vitabu.di

import ai.elimu.common.utils.data.repository.language.LanguageProvider
import ai.elimu.common.utils.di.StringKey.LANGUAGE_CUSTOM
import ai.elimu.vitabu.repository.LanguageProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import java.util.Optional

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @StringKey(LANGUAGE_CUSTOM)
    @IntoMap
    @Provides
    fun providesLanguageProvider(): Optional<LanguageProvider> = Optional.of(LanguageProviderImpl())
}