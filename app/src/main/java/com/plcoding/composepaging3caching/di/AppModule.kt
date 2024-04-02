package com.plcoding.composepaging3caching.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.room.RoomDatabase
import com.plcoding.composepaging3caching.data.local.entity.BeerDatabase
import com.plcoding.composepaging3caching.data.local.entity.BeerEntity
import com.plcoding.composepaging3caching.data.remote.BeerApi
import com.plcoding.composepaging3caching.data.remote.BeerRemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideBeerDatabase(
        @ApplicationContext context: Context
    ): BeerDatabase {
        return Room.databaseBuilder(
            context,
            BeerDatabase::class.java,
            "beers.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBeerApi(
        @ApplicationContext context: Context
    ): BeerApi {
        return Retrofit.Builder()
            .baseUrl(BeerApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideBeerPager(
        beerDb : BeerDatabase,beerApi: BeerApi
    ) : Pager<Int,BeerEntity>{
         return Pager(
             config = PagingConfig(pageSize = 20),
             remoteMediator = BeerRemoteMediator(
                 beerDb = beerDb,
                 beerApi = beerApi
             ),
             pagingSourceFactory = {
                 beerDb.dao.pagingSource()
             }
         )
    }
}