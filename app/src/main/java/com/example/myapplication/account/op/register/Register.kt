package com.example.myapplication.account.op.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.account.bean.RegisterRequest
import com.example.myapplication.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Register : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    private var standVerificationCode: String = ""

//    private val address: String by lazy {
//        "http://8.138.41.189:8085/"
//    }
//
//    private val service: HttpInterface by lazy {
//        HttpService.sendHttp(address, HttpInterface::class.java)
//    }
//
//    private val customNotification: CustomNotification by lazy {
//        CustomNotification(this)
//    }
//
//    private val sharedPreferences: SharedPreferences by lazy {
//        getSharedPreferences("IsLogin", Context.MODE_PRIVATE)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.resToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.resToolbar.setNavigationOnClickListener {
            finish()
        }

        //获取验证码
        binding.resRequestVerificationCode.setOnClickListener {
            if (binding.resNo.text.toString().isEmpty()) {
                Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show()
            } else if (binding.resNickname.text.toString().isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show()
            } else {
                // 禁用按钮
                val button: Button = binding.resRequestVerificationCode
                button.isEnabled = false

                // 启动倒计时
                object : CountDownTimer(60000, 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        button.text = "重新发送(${millisUntilFinished / 1000})"
                    }

                    override fun onFinish() {
                        button.text = "获取验证码"
                        button.isEnabled = true
                    }
                }.start()

                lifecycleScope.launch {
                    try {
                        standVerificationCode = viewModel.sendVerificationCode(
                            binding.resNo.text.toString(),
                            binding.resNickname.text.toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(this@Register, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show()
                    }

//                    try {
//                        //验证码请求
//                        val response = service.sendVerificationCode(
//                            binding.resNo.text.toString(),
//                            binding.resNickname.text.toString()
//                        )
//
//                        //成功响应处理
//                        if (response.isSuccessful) {
//                            val myResponseData = response.body() as MyResponse1
//                            when (myResponseData.msg.toString()) {
//                                "该手机号已使用" -> {
//                                    withContext(Dispatchers.Main) {
//                                        Toast.makeText(
//                                            this@Register, "该手机号已被注册", Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//
//                                "该昵称已使用" -> {
//                                    withContext(Dispatchers.Main) {
//                                        Toast.makeText(
//                                            this@Register, "该昵称已被使用", Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//
//                                else -> {
//                                    standVerificationCode = myResponseData.data
//                                    withContext(Dispatchers.Main) {
//                                        customNotification.showNotification(
//                                            message = standVerificationCode,
//                                            duration = 20000,
//                                        )
//                                    }
//                                }
//                            }
//                        } else {
//                            // 不成功响应处理
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(
//                                    this@Register, "请求响应失败: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } catch (e: Exception) {
//                        //错误处理
//                        withContext(Dispatchers.Main) {
//                            Log.d("Register.error1", "请求错误: ${e.message}")
//                            Toast.makeText(
//                                this@Register, "请求错误: ${e.message}", Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
                }
            }
        }

        binding.register.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val nickname = binding.resNickname.text.toString()
        val telephone = binding.resNo.text.toString()
        val pwd = binding.resPwd.text.toString()
        val pwd1 = binding.resPwd1.text.toString()
        val verificationCode = binding.resVerificationCode.text.toString()

        if (nickname.isEmpty()) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show()
        } else if (telephone.isEmpty()) {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show()
        } else if (pwd.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
        } else if (pwd1.isEmpty()) {
            Toast.makeText(this, "请再次输入密码", Toast.LENGTH_SHORT).show()
        } else if (verificationCode.isEmpty()) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show()
        } else if (pwd != pwd1) {
            Toast.makeText(this, "密码不匹配", Toast.LENGTH_SHORT).show()
        } else if (verificationCode != standVerificationCode) {
            Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show()
        } else {
            binding.register.isEnabled = false
            lifecycleScope.launch {
                try {
                    val registerRequest = RegisterRequest(nickname, telephone, pwd, verificationCode)
                    if (viewModel.register(registerRequest)) {
                        Toast.makeText(this@Register, "注册成功", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Register, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Register, "注册失败，该手机号已注册", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Register, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.register.isEnabled = true
                }
            }
        }
    }
}