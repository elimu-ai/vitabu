package ai.elimu.vitabu.data.repository

import ai.elimu.model.v2.gson.content.StoryBookGson
import javax.inject.Inject

class StoryBooksRepositoryImpl @Inject constructor(
    private val localDataSource: LocalStoryBooksDataSource,
) : StoryBookRepository {
    override suspend fun getStoryBooks(): List<StoryBookGson> {
        return localDataSource.getStoryBooks()
    }
}