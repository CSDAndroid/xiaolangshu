package com.example.myapplication.mine.home

import androidx.lifecycle.ViewModel
import com.example.myapplication.account.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val userService: UserService
): ViewModel() {

    fun getPhone(): String? {
        return userService.getPhone()
    }

}