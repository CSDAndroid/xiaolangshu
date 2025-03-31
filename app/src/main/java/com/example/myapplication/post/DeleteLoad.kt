package com.example.myapplication.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class DeleteLoad : AppCompatActivity() {

    private lateinit var back: ImageButton
    private lateinit var delete: ImageView
    private lateinit var image: ImageView
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        back = findViewById(R.id.delete_back)
        delete = findViewById(R.id.delete)
        image = findViewById(R.id.delete_image)

        uri = Uri.parse(intent.getStringExtra("uri"))

        image.setImageURI(uri)

        back.setOnClickListener {
            finish()
        }

        delete.setOnClickListener {
            val intent = Intent()
            intent.putExtra("isDelete", "delete")
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}