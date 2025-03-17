package com.example.myapplication.account.userProfile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.account.service.UserService
import com.example.myapplication.storage.db.entity.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userService: UserService
): ViewModel() {

    private val _userProfileLiveData = MutableLiveData<Account>()
    val userProfile: MutableLiveData<Account> get() = _userProfileLiveData

    private val _updateStatusLiveData = MutableLiveData<Boolean>()
    val updateStatus: MutableLiveData<Boolean> get() = _updateStatusLiveData

    fun getPhone(): String? {
        return userService.getPhone()
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

    fun updateUserProfile(account: Account) {
        viewModelScope.launch {
            try {
                val isUpdated = userService.updateUserProfile(account)
                _updateStatusLiveData.postValue(isUpdated)
            } catch (e: Exception) {
                Log.e("更新用户",e.message.toString())
            }
        }
    }

}