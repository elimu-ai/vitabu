package ai.elimu.vitabu.di

import ai.elimu.vitabu.data.repository.LocalStoryBooksDataSource
import ai.elimu.vitabu.data.repository.LocalStoryBooksDataSourceImpl
import ai.elimu.vitabu.data.repository.StoryBookRepository
import ai.elimu.vitabu.data.repository.StoryBooksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

// Tells Dagger this is a Dagger module
@Module
@InstallIn(ViewModelComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindStoryBookRepository(repo: StoryBooksRepositoryImpl)
    : StoryBookRepository

    @Binds
    abstract fun bindLocalStoryBookDataSource(dataSource: LocalStoryBooksDataSourceImpl)
    : LocalStoryBooksDataSource

}
