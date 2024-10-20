package com.example.myapplication.op

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.data.requestData.LoginRequest
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.http.HttpInterface
import com.example.myapplication.http.HttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    private val address: String by lazy {
        "http://8.138.41.189:8085/"
    }
    private val service: HttpInterface by lazy {
        HttpUtil.sendHttp(address, HttpInterface::class.java)
    }
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logBack.setOnClickListener {
            finish()
        }

        binding.login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val telephone = binding.logNo.text.toString()
        val pwd = binding.logPwd.text.toString()

        if (telephone.isEmpty()) {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show()
        } else if (pwd.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    //登录请求
                    val loginRequest = LoginRequest(telephone, pwd)
                    val response = service.login(loginRequest)

                    //成功响应处理
                    if (response.isSuccessful) {
                        if (response.body()?.code == 1) {
                            withContext(Dispatchers.Main) {
                                val edit = sharedPreferences.edit()
                                edit.putBoolean("isLogin", true)
                                edit.putString("phone", telephone)
                                edit.apply()

                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@Login, "登录失败，请检查账号密码是否正确",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        //不成功响应处理
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@Login, "登录响应失败: ${response.errorBody()?.string()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    //错误处理
                    withContext(Dispatchers.Main) {
                        Log.d("login.error", "请求错误: ${e.message}")
                        Toast.makeText(
                            this@Login, "请求错误: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}