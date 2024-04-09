package com.example.librarylink

import android.app.AlertDialog
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Instrumentation.ActivityResult
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.librarylink.databinding.ActivityMainScreeenBinding
import com.example.librarylink.databinding.ActivityPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    //arraylist
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    //uri of pickes pdf
    private var pdfUri: Uri? = null

    //tag
    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //intialize firebase
        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategory()

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backBtn.setOnClickListener{
            val intent = Intent(this,DashBoardAdmin::class.java)

            startActivity(intent)

             finish()
        }

        //handle click, show category pick dialog

        binding.categoryTV.setOnClickListener {
            categoryPickDialog()
        }
        //handle click, pick pdf Intent
        binding.attachBTn.setOnClickListener {
            pdfPickIntent()
        }
        //handle click, start uploading pdf/book
        binding.submitBTN.setOnClickListener {
            //STEP1: Validate Data
            //STEP2: Upload pdf to firebase storage
            //STEP3: Get url of upload pdf
            //STEP4: Upload pdf info to firebase db
            validateData()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
        //STEP1: Validate Data
        Log.d(TAG, "validateData: validating data")

        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTV.text.toString().trim()

        //validate data
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()){

            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty()){

            Toast.makeText(this, "Enter Category...", Toast.LENGTH_SHORT).show()
        }
        else if (pdfUri == null){
            Toast.makeText(this, "Picking PDF...", Toast.LENGTH_SHORT).show()
        }
        else{
            //data validates, begin upload
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        //STEP2: Upload pdf to firebase storage
        Log.d(TAG, "uploadPdfToStorage: uploading to storage..")
        //show progress dialog
        progressDialog.setMessage("Uploading PDF..")
        progressDialog.show()

        //timestamp
        val timestamp =System.currentTimeMillis()

        // path of pdf in firebase storage
        val filePathAndName ="Books$timestamp"
        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {taskSnapshot ->
                Log.d(TAG, "uploadPdfToStorage: PDF uploaded no getting url...")

                //STEP3: Get url of upload pdf
                val uriTask: Task<Uri> =taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadPdfUrl = "${uriTask.result}"

                uploadPdfInfoToDb(uploadPdfUrl,timestamp)


            }
            .addOnFailureListener{e ->
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadPdfInfoToDb(uploadPdfUrl: String, timestamp: Long) {
        //STEP4: Upload pdf info to firebase db
        Log.d(TAG, "uploadPdfInfoToDb: upload to db")
        progressDialog.setMessage("Uploading pdf info...")

        //uid of current user
        val uid= firebaseAuth.uid

            //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategotyId"
        hashMap["url"] = "$uploadPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewcount"] = 0
        hashMap["downloadsCount"] = 0

        //db reference DB> Book> BookId> (Book Info)
        val ref =FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb: uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded...", Toast.LENGTH_SHORT).show()
                pdfUri = null

            }
            .addOnFailureListener {e ->
                Log.d(TAG, "uploadPdfInfoToDb: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPdfCategory() {
        Log.d(TAG, "loadPdfCategoriries: Loading pdf categories")
        //init arraylist
        categoryArrayList = ArrayList()

        //d reference to load categories pf pdf
        val ref = FirebaseDatabase.getInstance().getReference("categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add arraylist
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var selectedCategotyId = ""
    private var selectedCategotyTitle = ""

    private fun categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: show Pdf category pick dialog")

        //get string array of categories from analyst
        val categoryArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices) {
            categoryArray[i] = categoryArrayList[i].category
        }

        // alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoryArray) { dialog, which ->
                //handle item click

                //get clicked items
                selectedCategotyTitle = categoryArrayList[which].category
                selectedCategotyId = categoryArrayList[which].id

                //sert categiory to textview
                binding.categoryTV.text = selectedCategotyTitle

                Log.d(TAG, "categoryPickDialog: Selected Category ID: $selectedCategotyId")
                Log.d(TAG, "categoryPickDialog: Selected Category Title: $selectedCategotyTitle")

            }
            .show()
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pic intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<androidx.activity.result.ActivityResult> { result ->

            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "PDF Picked")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "PDF Picked cancelled ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

}