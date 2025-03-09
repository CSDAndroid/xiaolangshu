package com.example.myapplication.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.storage.db.AppDatabase

class VideoInfoViewModelFactory(private val context: Context, private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoInfoViewModel(context,database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}