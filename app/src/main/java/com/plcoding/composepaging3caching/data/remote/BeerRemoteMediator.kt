package com.plcoding.composepaging3caching.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.plcoding.composepaging3caching.data.local.entity.BeerDatabase
import com.plcoding.composepaging3caching.data.local.entity.BeerEntity
@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator(
    private val beerDb : BeerDatabase,
    private val beerApi: BeerApi
) : RemoteMediator<Int, BeerEntity>(){

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
        TODO("Not yet implemented")
    }


}