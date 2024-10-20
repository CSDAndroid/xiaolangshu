package com.example.myapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class CustomNotification(private val context: Context) {

    private lateinit var notificationView: LinearLayout
    private var handler: Handler = Handler()

    @SuppressLint("InflateParams")
    fun showNotification(message: String, duration: Long = 20000, onClose: (() -> Unit)? = null) {
        notificationView =
            LayoutInflater.from(context).inflate(R.layout.custom_notification, null) as LinearLayout

        val messageTextView =
            notificationView.findViewById<TextView>(R.id.notification_verificationCode)
        val closeButton = notificationView.findViewById<Button>(R.id.notification_close)

        messageTextView.text = message
        closeButton.setOnClickListener {
            dismissNotification(onClose)
        }

        // 添加到根布局
        (context as? AppCompatActivity)?.addContentView(
            notificationView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // 显示通知
        notificationView.visibility = View.VISIBLE
        slideIn()

        // 设置自动关闭
        handler.postDelayed({
            dismissNotification(onClose)
        }, duration)
    }

    private fun slideIn() {
        val animateSlideIn = TranslateAnimation(0f, 0f, -notificationView.height.toFloat(), 0f)
        animateSlideIn.duration = 500
        notificationView.startAnimation(animateSlideIn)
    }

    private fun dismissNotification(onClose: (() -> Unit)?) {
        val animateSlideOut = TranslateAnimation(0f, 0f, 0f, -notificationView.height.toFloat())
        animateSlideOut.duration = 500
        notificationView.startAnimation(animateSlideOut)

        animateSlideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                notificationView.visibility = View.GONE
                onClose?.invoke() // 调用关闭回调
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}