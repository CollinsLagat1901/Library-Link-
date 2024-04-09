package com.example.librarylink

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.librarylink.DashBoardUser
import com.example.librarylink.SignInActivity
import com.example.librarylink.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccountTv.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.signinBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.emailEt.text.toString()
        val pass = binding.passET.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        } else if (pass.isEmpty()) {
            Toast.makeText(this, "Enter Password..", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In..")
        progressDialog.show()

        val email = binding.emailEt.text.toString()
        val pass = binding.passET.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                // Handle successful login
                checkUser()
            }
            .addOnFailureListener { e ->
                // Handle failed login
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User info..")
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    // Get user type e.g., user or admin
                    val userType = snapshot.child("userType").value

                    if (userType == "Users") {
                        // It's a simple user
                        val intent = Intent(this@SignInActivity, DashBoardAdmin::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Handle unexpected user types, or redirect to a default activity
                        Toast.makeText(
                            this@SignInActivity,
                            "Unexpected user type: $userType",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SignInActivity,
                        "Error checking user info: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}


