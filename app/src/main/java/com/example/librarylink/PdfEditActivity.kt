package com.example.librarylink

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.librarylink.databinding.ActivityPdfEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfEditBinding

    private companion object{
        private const val TAG ="PDF_EDIT_TAG"
    }

    // book id get from intent started from AdapterAdmin
    private var bookId= ""

    //progress Dialog
    private lateinit var progressDialog: ProgressDialog

    //array list to hold categort titles
    private lateinit var categoryTitleArrayList: ArrayList<String>

        //array list to hold category ids
    private lateinit var categoryIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_edit)
        binding = ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id to edit the book info
        bookId =intent.getStringExtra("bookId")!!

        //set up progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadBookInfo()

        //handle go back
        binding.backBtn.setOnClickListener {
            val intent = Intent(this@PdfEditActivity, DashBoardAdmin::class.java)
            startActivity(intent)
            finish()
        }
        //handel click, pick category
        binding.categoryTV.setOnClickListener{
            categoryDialog()
        }
        //handle click , begin update
        binding.submitBTN.setOnClickListener {
            validateData()
        }
    }

    private fun loadBookInfo() {
        Log.d(TAG, "loadBookInfo: Loading book Info")

        val ref =FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book info
                    selectedCategoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title =snapshot.child("title").value.toString()

                    //set to view
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)


                    //load book category info using categoryId
                    Log.d(TAG, "onDataChange: Loading book category info")
                    val refBookCategory =FirebaseDatabase.getInstance().getReference("categories")
                    refBookCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //get category
                                val category =snapshot.child("category").value
                                    //set to textview
                                binding.categoryTV.text =category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private var title=""
    private var description=""

    private fun validateData() {
        //get data
        title =binding.titleEt.text.toString().trim()
        description =binding.descriptionEt.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show()
        }
        else if (selectedCategoryId.isEmpty()){
            Toast.makeText(this, "Pick Category", Toast.LENGTH_SHORT).show()
        }
        else{
            updatePdf()
        }

    }

    private fun updatePdf() {
        Log.d(TAG, "updatePdf: Starting updating pdf info...")

        //show progress
        progressDialog.setMessage("Updating book info")
        progressDialog.show()

        //setup data to update to db, spelling of keys must be same as in firebase
        val hashMap = HashMap<String, Any>()
        hashMap["title"] ="$title"
        hashMap["description"] ="$description"
        hashMap["categoryId"] ="$selectedCategoryId"

        //start updating
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updatePdf: Updated Successfully...")
                Toast.makeText(this, "Updated Successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "updatePdf: Failed to update due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId=""
    private var selectedCategoryTitle=""


    private fun categoryDialog() {

        //make string array from arraylist of string
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for (i in categoryTitleArrayList.indices){
            categoriesArray[i] =categoryTitleArrayList[i]
        }
        //alert dialog

        val builder =AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog, position ->
                //handle click, save clicked category id and title
                selectedCategoryId =categoryIdArrayList[position]
                selectedCategoryTitle =categoryTitleArrayList[position]

                //set to textview
                binding.categoryTV.text =selectedCategoryTitle
            }
            .show() //show dialog

    }

    private fun loadCategories() {
        Log.d(TAG, "loadCategories: loading categories..")

        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref =FirebaseDatabase.getInstance().getReference("categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before strting adding data into them
                categoryTitleArrayList.clear()
                categoryIdArrayList.clear()

                for (ds in snapshot.children){
                    val id = "${ds.child("id").value}"
                    val category ="${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG, "onDataChange: Category ID $id")
                    Log.d(TAG, "onDataChange: Category  $category")
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }

        })
    }
}