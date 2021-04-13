package com.example.picturesapp.models

import com.google.gson.annotations.SerializedName

data class PicturesResponse(@SerializedName("total") val totalResults:Int,
                            @SerializedName("totalHits") val totalHits:Int,
                            @SerializedName("hits")val picturesList:ArrayList<PictureHit>)
