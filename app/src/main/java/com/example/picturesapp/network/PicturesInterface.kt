package com.example.picturesapp.network

import com.example.picturesapp.models.PicturesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PicturesInterface {

    @GET("api/")
    fun getPictures(@Query("q")query:String, @Query("page")page:Int, @Query("image_type")imageType:String, @Query("key")key:String, @Query("per_page")perPage:String): Call<PicturesResponse>?
}