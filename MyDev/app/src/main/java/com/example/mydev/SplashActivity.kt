package com.example.mydev

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 필요한 경우 여기서 초기화 작업을 수행할 수 있습니다
        splashScreen.setKeepOnScreenCondition { false }

        // MainActivity로 전환
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}