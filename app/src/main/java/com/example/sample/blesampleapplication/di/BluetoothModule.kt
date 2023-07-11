package com.example.sample.blesampleapplication.di

import android.content.Context
import com.softnet.module.blemodule.amoband.AmoOmega
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {

    @Singleton
    @Provides
    fun provideAmoBand(@ApplicationContext context: Context): AmoOmega {
        return AmoOmega(context)
    }
}