package com.example.myapplication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.database.UserInfoDatabase

class UserInfoViewModelFactory(private val database: UserInfoDatabase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserInfoViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}