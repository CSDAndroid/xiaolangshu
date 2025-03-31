package com.example.myapplication.util

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R

class DialogUtil : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // 设置透明背景
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 加载布局
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_loading, null, false)
        dialog.setContentView(view)

        // 设置全屏
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        // 添加旋转加载图像
        val loadingImage: ImageView = view.findViewById(R.id.loading_img)

        // 添加旋转动画
        val rotateAnimation = ObjectAnimator.ofFloat(loadingImage, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        rotateAnimation.start()

        return dialog
    }

    override fun onCancel(dialog: DialogInterface) {
        // 禁用对话框取消
    }

    override fun onDismiss(dialog: DialogInterface) {
        // 可选处理对话框消失
    }

}