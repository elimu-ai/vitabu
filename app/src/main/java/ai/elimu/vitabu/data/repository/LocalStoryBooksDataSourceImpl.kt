package ai.elimu.vitabu.data.repository

import ai.elimu.content_provider.utils.ContentProviderUtil
import ai.elimu.model.v2.gson.content.StoryBookGson
import ai.elimu.vitabu.BuildConfig
import android.app.Application
import javax.inject.Inject

class LocalStoryBooksDataSourceImpl @Inject constructor(private val context: Application) :
    LocalStoryBooksDataSource {
    override suspend fun getStoryBooks(): List<StoryBookGson> {
        return ContentProviderUtil.getAllStoryBookGsons(
            context,
            BuildConfig.CONTENT_PROVIDER_APPLICATION_ID
        )
    }
}