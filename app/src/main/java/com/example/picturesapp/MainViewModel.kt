package com.example.picturesapp

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.picturesapp.models.PictureHit
import com.example.picturesapp.models.PicturesResponse
import com.example.picturesapp.network.PicturesInterface
import com.example.picturesapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainViewModel: ViewModel() {

    val retrofitService: PicturesInterface?
    var steps = MutableLiveData<Steps>()
    private lateinit var context: Context
    private var page = 1
    private var query:String = ""
    companion object{
        private const val API_KEY = "6814610-cd083c066ad38bb511337fb2b"
        private const val IMAGE_TYPE = "photo"
        private const val PER_PAGE = "100"
    }

    init {
        val retrofitClient: Retrofit? = RetrofitClient.createClient()
        retrofitService = retrofitClient?.create(PicturesInterface::class.java)
    }

    fun searchPictures(query:String){
        this.query = query
        page = 1
        getPictures()
    }

    fun setContext(context: Context){
        this.context = context
    }

     fun getPictures(){
         if (query.trim().isNotEmpty()){
             val picturesCall: Call<PicturesResponse>? = retrofitService?.getPictures(query,page, IMAGE_TYPE, API_KEY, PER_PAGE)
             picturesCall?.enqueue(object : Callback<PicturesResponse> {
                 override fun onResponse(
                     call: Call<PicturesResponse>, response: Response<PicturesResponse>) {
                     response.body()?.let {
                         page++
                         steps.postValue(Steps.DataReady(it.picturesList))
                     }
                 }

                 override fun onFailure(call: Call<PicturesResponse>, t: Throwable) {
                     steps.postValue(Steps.OnError(context.getString(R.string.general_error)))
                 }

             })
         }else{
             steps.postValue(Steps.OnError(context.getString(R.string.error_no_query)))
         }
    }



    sealed class Steps{
        class DataReady(val data: ArrayList<PictureHit>) : Steps()
        class OnError(val errorMessage:String) : Steps()
    }
}