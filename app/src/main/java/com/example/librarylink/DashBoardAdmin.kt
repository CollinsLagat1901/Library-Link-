package com.example.librarylink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.librarylink.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashBoardAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var adapterCategory: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()

        // search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterCategory.filter?.filter(s)
                } catch (e: Exception) {
                    // Log the exception or print a message for debugging
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // handle click, logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this, CategoryAddActivity::class.java))
            finish()
        }
        //handle click, pdf
        binding.addpdf.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java))
            finish()
        }
    }

    private fun loadCategories() {
        // init arrayList
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list before starting to add data into it
                categoryArrayList.clear()

                for (ds in snapshot.children) {
                    // get data as model
                    val model = ds.getValue(ModelCategory::class.java)
                    // add to arrayList
                    model?.let {
                        categoryArrayList.add(it)
                    }
                }
                // setup adapter
                adapterCategory = AdapterCategory(this@DashBoardAdmin, categoryArrayList)
                // set adapter to recyclerview
                binding.categoryRV.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors, e.g., log the error
                // Log.e("DashBoardAdmin", "Error loading categories: ${error.message}")
            }
        })
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // Not logged in, go to the main screen
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        } else {
            // Logged in, get and show user info
            val email = firebaseUser.email
            // Set to the TextView in the toolbar
            binding.subTitleTv.text = email
        }
    }
}
