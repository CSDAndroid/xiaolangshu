package com.example.myapplication.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.UserInfoDatabase

class PictureInfoViewModelFactory(
    private val context: Context,
    private val database: UserInfoDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PictureInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PictureInfoViewModel(context, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}