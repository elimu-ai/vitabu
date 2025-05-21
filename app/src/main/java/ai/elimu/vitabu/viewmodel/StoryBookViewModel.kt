package ai.elimu.vitabu.viewmodel

import ai.elimu.model.v2.gson.content.StoryBookGson
import kotlinx.coroutines.flow.StateFlow

/**
 * A sealed hierarchy describing the state of the feed of storybooks resources.
 */
sealed interface LoadStoryBooksUiState {
    /**
     * The storybooks are still loading.
     */
    data object Loading : LoadStoryBooksUiState

    /**
     * The storybooks are loaded with the given list of storybooks resources.
     */
    data class Success(
        /**
         * The list of storybooks resources
         */
        val storyBooks: List<StoryBookGson>,
    ) : LoadStoryBooksUiState
}

interface StoryBookViewModel {
    val uiState: StateFlow<LoadStoryBooksUiState>
    fun getAllStoryBooks()
}