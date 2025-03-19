//package com.example.myapplication.viewModel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.myapplication.storage.db.AppDatabase
//
//class UserInfoViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return UserInfoViewModel(database) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}