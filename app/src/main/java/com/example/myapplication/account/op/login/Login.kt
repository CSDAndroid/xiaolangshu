package com.example.myapplication.account.op.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.account.bean.LoginRequest
import com.example.myapplication.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class Login : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.logToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.logToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.logButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val telephone = binding.logPhone.text.toString()
        val pwd = binding.logPassword.text.toString()

        if (telephone.isEmpty()) {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show()
        } else if (pwd.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
        } else {
            binding.logButton.isEnabled = false // 禁止重复点击
            lifecycleScope.launch {
                try {
                    val loginRequest = LoginRequest(telephone, pwd)
                    val result = withContext(Dispatchers.IO) {
                        viewModel.login(loginRequest)
                    }
                    if (result) {
                        Toast.makeText(this@Login, "登录成功", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "登录失败，请检查账号密码是否正确", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Login, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.logButton.isEnabled = true // 登录结束后重新启用按钮
                }
            }
        }
    }
}