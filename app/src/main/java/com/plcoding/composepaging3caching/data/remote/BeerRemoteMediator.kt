package com.plcoding.composepaging3caching.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.plcoding.composepaging3caching.data.local.entity.BeerDatabase
import com.plcoding.composepaging3caching.data.local.entity.BeerEntity
import com.plcoding.composepaging3caching.data.mappers.toBeerEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator(
    private val beerDb : BeerDatabase,
    private val beerApi: BeerApi
) : RemoteMediator<Int, BeerEntity>(){

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
      return try{
         val loadKey = when(loadType){
             LoadType.REFRESH -> 1
             LoadType.PREPEND -> return MediatorResult.Success(
                 endOfPaginationReached = true
             )
             LoadType.APPEND -> {
                 val lastItem = state.lastItemOrNull()
                 if (lastItem == null){
                     1
                 }else{
                     // If the page size  = 20 and last item is 80 then
                     //80/20 = 4 and add +1 for append next page
                     (lastItem.id / state.config.pageSize )+ 1
                 }
             }
         }

          val beers = beerApi.getBeers(
              page = loadKey,
              pageCount = state.config.pageSize
          )

          beerDb.withTransaction {
              if (loadType == LoadType.REFRESH){
                  beerDb.dao.clearAll()
              }

              val beerEntity = beers.map { it.toBeerEntity() }
              beerDb.dao.upsertAll(beerEntity)
          }

          MediatorResult.Success(
              endOfPaginationReached = beers.isEmpty()
          )

      }catch(e : IOException){
         MediatorResult.Error(e)
      }catch(e : HttpException){
          MediatorResult.Error(e)
      }
    }


}