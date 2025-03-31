package ai.elimu.vitabu.di

import ai.elimu.common.utils.data.repository.language.LanguageProvider
import ai.elimu.vitabu.repository.LanguageProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun providesLanguageProvider(): LanguageProvider = LanguageProviderImpl()
}