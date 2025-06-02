package ai.elimu.vitabu.data.repository

import ai.elimu.model.v2.gson.content.StoryBookGson

interface StoryBookRepository {
    suspend fun getStoryBooks(): List<StoryBookGson>
}