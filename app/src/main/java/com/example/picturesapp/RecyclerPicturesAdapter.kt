package com.example.picturesapp

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picturesapp.models.PictureHit


class RecyclerPicturesAdapter(val context: Context, var data: ArrayList<PictureHit>, var maxHeight: Int) : RecyclerView.Adapter<RecyclerPicturesAdapter.MyViewHolder>() {

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

        private val mainLayout: ConstraintLayout = itemView.findViewById(R.id.main_layout)
        private val ivSmallImage: ImageView = itemView.findViewById(R.id.small_image)

        fun onBind(item: PictureHit) {

            val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxHeight.toFloat(), context.resources.displayMetrics).toInt()

            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height)
            mainLayout.layoutParams = params


                Glide.with(context)
                    .load(item.smallImageUrl)
                    .into(ivSmallImage)

                if (item.previewHeight < maxHeight) {
                    val scale: Float = (maxHeight.toFloat() / item.previewHeight.toFloat())
                    ivSmallImage.scaleY = scale
                    ivSmallImage.scaleX = scale
                    itemView.postDelayed({
                        mainLayout.requestLayout()
                        ivSmallImage.requestLayout()
                    }, 50)
                }

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