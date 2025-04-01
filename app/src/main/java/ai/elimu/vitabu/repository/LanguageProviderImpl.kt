package ai.elimu.vitabu.repository

import ai.elimu.common.utils.data.repository.language.LanguageProvider
import ai.elimu.vitabu.BuildConfig
import javax.inject.Inject

class LanguageProviderImpl @Inject constructor(): LanguageProvider {
    override fun getLanguage(): String {
        return ""
    }

    override fun getContentProviderId(): String {
        return BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
    }
}