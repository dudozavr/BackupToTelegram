package com.mindorks.framework.backuptotelegram.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.mindorks.framework.backuptotelegram.data.network.auth.AuthValidator
import com.mindorks.framework.backuptotelegram.data.network.auth.AuthValidatorImpl
import com.mindorks.framework.backuptotelegram.data.network.telegram.TelegramNetworkInterceptor
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.DeleteService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.MessageService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.PhotoService
import com.mindorks.framework.backuptotelegram.data.network.telegram.services.VideoService
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManager
import com.mindorks.framework.backuptotelegram.data.storage.media_store.BackupManagerImpl
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferences
import com.mindorks.framework.backuptotelegram.data.storage.preferences.AppPreferencesImpl
import com.mindorks.framework.backuptotelegram.data.storage.room.AppDataBase
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileDao
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepository
import com.mindorks.framework.backuptotelegram.data.storage.room.MediaFileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkProviderModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(TelegramNetworkInterceptor()).build()


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.telegram.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideMessageService(retrofit: Retrofit): MessageService =
        retrofit.create(MessageService::class.java)

    @Provides
    @Singleton
    fun providePhotoService(retrofit: Retrofit): PhotoService =
        retrofit.create(PhotoService::class.java)

    @Provides
    @Singleton
    fun provideVideoService(retrofit: Retrofit): VideoService =
        retrofit.create(VideoService::class.java)

    @Provides
    @Singleton
    fun provideDeleteService(retrofit: Retrofit): DeleteService =
        retrofit.create(DeleteService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
class WorkManagerProviderModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}

@Module
@InstallIn(SingletonComponent::class)
class StorageProviderModule {

    @Provides
    @Singleton
    fun provideMediaFileDao(appDataBase: AppDataBase): MediaFileDao = appDataBase.getMediaFileDao()

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "ReservedMediaFiles").build()

    @Provides
    @Singleton
    fun provideMediaFileRepository(mediaFileDao: MediaFileDao): MediaFileRepository =
        MediaFileRepositoryImpl(mediaFileDao)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageBinderModule {

    @Binds
    @Singleton
    abstract fun bindPreferences(appPreferencesImpl: AppPreferencesImpl): AppPreferences

    @Binds
    @Singleton
    abstract fun bindBackupManager(mediaStoreConnectorImpl: BackupManagerImpl): BackupManager
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthBinderModule {

    @Binds
    abstract fun bindAuthValidator(authValidatorImpl: AuthValidatorImpl): AuthValidator
}