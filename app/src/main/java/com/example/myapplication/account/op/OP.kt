package com.example.myapplication.account.op

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.account.op.login.Login
import com.example.myapplication.account.op.register.Register
import com.example.myapplication.databinding.ActivityOpBinding

class OP : AppCompatActivity() {

    private lateinit var binding: ActivityOpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.opLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.opRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}