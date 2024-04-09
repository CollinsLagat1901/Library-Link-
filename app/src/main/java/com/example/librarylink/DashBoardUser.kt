package com.example.librarylink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.librarylink.databinding.ActivityDashBoardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashBoardUser : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_user)

        binding = ActivityDashBoardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // Not logged in, go to the main screen
            binding.subTitleTv.text= "Not Logged In"
        } else {
            // Logged in, get and show user info
            val email = firebaseUser.email
            // Set to the TextView in the toolbar
            binding.subTitleTv.text = email
        }
    }
}