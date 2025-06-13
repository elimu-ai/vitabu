package ai.elimu.vitabu.viewmodel

import ai.elimu.common.utils.di.IoScope
import ai.elimu.vitabu.data.repository.StoryBookRepository
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryBookViewModelImpl @Inject constructor(
    @IoScope private val ioScope: CoroutineScope,
    private val storyBookRepository: StoryBookRepository
) : ViewModel(), StoryBookViewModel {

    private val _uiState = MutableStateFlow<LoadStoryBooksUiState>(LoadStoryBooksUiState.Loading)
    override val uiState: StateFlow<LoadStoryBooksUiState> = _uiState.asStateFlow()

    override fun getAllStoryBooks() {
        ioScope.launch {
            _uiState.emit(LoadStoryBooksUiState.Loading)
            val storyBooks = storyBookRepository.getStoryBooks()
            _uiState.emit(LoadStoryBooksUiState.Success(storyBooks))
        }
    }
}