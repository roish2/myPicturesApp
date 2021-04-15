package com.example.picturesapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.picturesapp.models.PictureHit


class RecyclerPicturesAdapter(val context: Context, var data: ArrayList<PictureHit>, var maxHeight: Int, val screenDensity:Float) : RecyclerView.Adapter<RecyclerPicturesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.picture_adapter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivSmallImage: ImageView = itemView.findViewById(R.id.small_image)

        fun onBind(item: PictureHit) {

                Glide.with(context)
                    .asBitmap()
                    .load(item.smallImageUrl)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            if ((item.previewHeight)/screenDensity < maxHeight) {
                                val scale: Float = (maxHeight.toFloat() / (item.previewHeight.toFloat()/screenDensity))
                                val resized = Bitmap.createScaledBitmap(resource,(resource.width *scale).toInt(), (resource.height *scale).toInt(), true)
                                ivSmallImage.setImageBitmap(resized)
                                ivSmallImage.requestLayout()

                            }else{
                                ivSmallImage.setImageBitmap(resource)
                            }

                        }
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })

        }
    }

    fun refreshHeight(newHeight: Int) {
        maxHeight = newHeight
    }

    fun refreshData(newData: ArrayList<PictureHit>) {
        data.addAll(newData)
        notifyDataSetChanged()
    }


}