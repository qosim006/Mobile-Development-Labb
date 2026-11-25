package com.example.hiltpractice.di

import com.example.hiltpractice.core.LogcatLogger
import com.example.hiltpractice.core.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {

    // Q11: change only this line to switch to FileLogger
    @Binds
    abstract fun bindLogger(impl: LogcatLogger): Logger
}
