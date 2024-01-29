package com.raj.marvelcompose.di

import com.raj.marvelcompose.model.APIService
import com.raj.marvelcompose.model.MarvelAPiRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {

    @Provides
    fun provideAPIRepo() = MarvelAPiRepo(APIService.api)
}