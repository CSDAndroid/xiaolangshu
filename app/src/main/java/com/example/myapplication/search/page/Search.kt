package com.example.myapplication.search.page

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBack.setOnClickListener { finish() }

        binding.searchSearch.setOnClickListener {
            val intent = Intent(this, AfterSearch::class.java)
            intent.putExtra("keyword", binding.searchComment.text.toString())
            startActivity(intent)
        }
    }
}