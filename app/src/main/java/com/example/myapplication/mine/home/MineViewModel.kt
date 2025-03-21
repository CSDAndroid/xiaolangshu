package com.example.myapplication.mine.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.account.service.UserService
import com.example.myapplication.storage.db.AppDatabase
import com.example.myapplication.storage.db.entity.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val userService: UserService,
    private val appDatabase: AppDatabase
): ViewModel() {

    private val _userProfileLiveData = MutableLiveData<Account>()
    val userProfile: MutableLiveData<Account> get() = _userProfileLiveData

    fun getPhone(): String? {
        return userService.getPhone()
    }

    fun logout(account: Account) {
        viewModelScope.launch {
            userService.logout(account)
        }
    }

    fun getUserProfile(phone: String) {
        viewModelScope.launch {
            try {
                userService.getUserProfile(phone).let {
                    _userProfileLiveData.postValue(it)
                }
            } catch (e: Exception) {
                Log.e("用户信息",e.message.toString())
            }
        }
    }

    fun updateUserAvatar(avatar: String, phone: String) {
        viewModelScope.launch {
            appDatabase.accountDao().updateAvatar(avatar, phone)
        }
    }

    fun updateUserBackground(background: String, phone: String) {
        viewModelScope.launch {
            appDatabase.accountDao().updateBackground(background, phone)
        }
    }

}