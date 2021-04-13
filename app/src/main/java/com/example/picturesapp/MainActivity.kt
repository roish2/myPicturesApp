package com.example.picturesapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturesapp.models.PictureHit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {

    private val gson: Gson = Gson()
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerPicturesAdapter: RecyclerPicturesAdapter
    private lateinit var recyclerView: RecyclerView
    private var dataPicturesList: ArrayList<PictureHit> = ArrayList()
    private var dpHeight: Int = 0
    private var lastMaxHeight = -1
    private lateinit var prefs:SharedPreferences
    private var isRefreshOnScrolling:Boolean = false

    companion object {
        private const val PREFERENCE_NAME: String = "com.example.picturesapp"
        private const val PREFERENCE_DATA_NAME: String = "my_array"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.setContext(this)
        viewModel.steps.observe(this, nameObserver)

        recyclerView = findViewById(R.id.my_recyclerView)
        val button: Button = findViewById(R.id.button)
        val searchText: EditText = findViewById(R.id.search_text)

        getScreenHeightDisplay(button)

        if (!prefs.getString(PREFERENCE_DATA_NAME,"").isNullOrEmpty()){
            val listOfTestObject: Type = object : TypeToken<List<PictureHit?>?>() {}.type
            dataPicturesList = gson.fromJson(prefs.getString(PREFERENCE_DATA_NAME,""), listOfTestObject)
        }


        val manager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = manager
        recyclerPicturesAdapter = RecyclerPicturesAdapter(this, dataPicturesList, 0)
        recyclerView.adapter = recyclerPicturesAdapter

        button.setOnClickListener {
            if (searchText.text.toString().trim().isNotEmpty()){
                isRefreshOnScrolling = true
                dataPicturesList.clear()
                recyclerPicturesAdapter.notifyDataSetChanged()
                viewModel.searchPictures(searchText.text.toString())
            }else{
                viewModel.steps.postValue(MainViewModel.Steps.OnError(getString(R.string.error_no_query)))
            }
        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(10) && isRefreshOnScrolling) {
                    viewModel.getPictures()
                }
            }
        })

        if (dataPicturesList.size > 0){
            viewModel.steps.postValue(MainViewModel.Steps.DataReady(dataPicturesList))
        }

    }

    private fun getScreenHeightDisplay(button: Button) {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        dpHeight = ((outMetrics.heightPixels - button.height) / density).toInt()
    }

    private val nameObserver = Observer<MainViewModel.Steps> { step ->
        when (step) {
            is MainViewModel.Steps.DataReady -> {
//                dataPicturesList.addAll(step.data.picturesList)
                step.data.maxBy {
                    it.previewHeight
                }?.let {
                    val maxHeightPicture: PictureHit = it
                    if (lastMaxHeight < it.previewHeight) {
                        lastMaxHeight = it.previewHeight
                        recyclerPicturesAdapter.refreshHeight(maxHeightPicture.previewHeight)
                        val numOfColumn: Int = dpHeight / (maxHeightPicture.previewHeight)
                        val manager = GridLayoutManager(this, numOfColumn - 1, GridLayoutManager.HORIZONTAL, false)
                        recyclerView.layoutManager = manager

                    }
                } ?: kotlin.run {
                    isRefreshOnScrolling = false
                    Toast.makeText(this, getString(R.string.error_no_results), Toast.LENGTH_LONG).show()
                }

                if (!step.data.isNullOrEmpty()) {
                    recyclerPicturesAdapter.refreshData(step.data)
                }


            }

            is MainViewModel.Steps.OnError -> {
                Toast.makeText(this, step.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        if (dataPicturesList.size > 0) {
            val listOfTestObject: Type = object : TypeToken<List<PictureHit?>?>() {}.type
            val jsonData = gson.toJson(dataPicturesList, listOfTestObject)
            prefs.edit().putString(PREFERENCE_DATA_NAME, jsonData).apply()
        }
        super.onPause()
    }
}