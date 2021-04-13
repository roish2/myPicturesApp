package com.example.picturesapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object{
        private var retrofit: Retrofit?= null
//        https://pixabay.com/api/?q=small+kittens&pnj;key=6814610-cd083c066ad38bb511337fb2b
//        &q=yellow+flowers&image_type=photo
        const val baseUrl:String = "https://pixabay.com/"

        fun createClient(): Retrofit?{

            if (retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit
        }
    }
}