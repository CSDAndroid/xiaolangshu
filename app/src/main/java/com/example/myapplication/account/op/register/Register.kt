package com.example.myapplication.account.op.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
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
        binding.resRequestVerificationCodeButton.setOnClickListener {
            if (binding.resPhone.text.toString().isEmpty()) {
                Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show()
            } else if (binding.resNickname.text.toString().isEmpty()) {
                Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show()
            } else {
                // 禁用按钮
                binding.resRequestVerificationCodeButton.isEnabled = false

                // 启动倒计时
                object : CountDownTimer(60000, 1000) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        binding.resRequestVerificationCodeButton.text =
                            "重新发送(${millisUntilFinished / 1000})"
                    }

                    override fun onFinish() {
                        binding.resRequestVerificationCodeButton.text = "获取验证码"
                        binding.resRequestVerificationCodeButton.isEnabled = true
                    }
                }.start()

                lifecycleScope.launch {
                    try {
                        standVerificationCode = viewModel.sendVerificationCode(
                            binding.resPhone.text.toString(),
                            binding.resNickname.text.toString()
                        )
                    } catch (e: Exception) {
                        Toast.makeText(this@Register, "网络错误，请稍后再试", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.resButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val nickname = binding.resNickname.text.toString()
        val telephone = binding.resPhone.text.toString()
        val pwd = binding.resPassword.text.toString()
        val pwd1 = binding.resPassword1.text.toString()
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
            binding.resButton.isEnabled = false
            lifecycleScope.launch {
                try {
                    val registerRequest =
                        RegisterRequest(nickname, telephone, pwd, verificationCode)
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
                    binding.resButton.isEnabled = true
                }
            }
        }
    }
}