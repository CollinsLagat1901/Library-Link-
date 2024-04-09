package com.example.librarylink

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.librarylink.databinding.ActivityCategoryAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_add)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
            setContentView(binding.root)

        firebaseAuth =FirebaseAuth.getInstance()
        //configure progress Bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backBtn.setOnClickListener{
            val intent = Intent(this,DashBoardAdmin::class.java)

            startActivity(intent)

            finish()
        }

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }
    private var category=""

    private fun validateData() {
        //validate Data
        //get data
        category = binding.categoryEt.text.toString().trim()

        //validate data
        if (category.isEmpty()){
            Toast.makeText(this, "Enter Category...", Toast.LENGTH_SHORT).show()
        }
        else{
            addCategoryFireBase()
        }
    }

    private fun addCategoryFireBase() {
        //show progress
        progressDialog.show()

        //get timestap
        val timestamp =System.currentTimeMillis()

        //setup data to ass in firebase db
        val hashMap =HashMap<String, Any>()
        hashMap["id"]="$timestamp"
        hashMap["category"]=category
        hashMap["timestamp"]=timestamp
        hashMap["uid"]="${firebaseAuth.uid}"

        //add to firebadbase: DataBase root> categories > categoryID>categoryInfo
        val ref =FirebaseDatabase.getInstance().getReference("categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Added successful...", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failure to add due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}