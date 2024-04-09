package com.example.librarylink


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.librarylink.databinding.ActivityMainScreeenBinding

class MainScreen : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreeenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreeenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //handle login click
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))

        }

        binding.skipBtn.setOnClickListener {

            startActivity(Intent(this, DashBoardAdmin::class.java))
        }
    }

}