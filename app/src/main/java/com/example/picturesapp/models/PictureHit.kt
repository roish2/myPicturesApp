package com.example.picturesapp.models

import com.google.gson.annotations.SerializedName

data class PictureHit(@SerializedName("id")val id:Int,
                      @SerializedName("tags") val tags:String,
                      @SerializedName("previewURL") val smallImageUrl:String,
                      @SerializedName("previewHeight") val previewHeight:Int,
                      @SerializedName("webformatURL") val largeImage:String
                      )