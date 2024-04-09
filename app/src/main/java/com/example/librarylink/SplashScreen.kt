package com.example.librarylink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreen : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            checkUser()
        }, 2000) // 2 seconds delay
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // User not logged in, go to the main screen
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        } else {
            // User logged in, go to DashboardUser
            startActivity(Intent(this@SplashScreen, DashBoardAdmin::class.java))
            finish()
        }
    }
}
