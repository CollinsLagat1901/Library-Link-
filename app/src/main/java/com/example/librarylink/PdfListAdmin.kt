package com.example.librarylink
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.librarylink.databinding.ActivityPdfListAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PdfListAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityPdfListAdminBinding

    private var categoryId = ""
    private var category = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    private companion object {
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId") ?: ""
        category = intent.getStringExtra("category") ?: ""

        // Get the current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserID = currentUser?.uid ?: ""

        binding.subTitleTXT.text = category
        loadPdfList()

        binding.searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterPdfAdmin.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.backBtn.setOnClickListener {
            val intent = Intent(this@PdfListAdmin, DashBoardAdmin::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadPdfList() {
        pdfArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()

                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)

                        // Check if the PDF belongs to the current user
                        if (model != null && model.uid == currentUserID) {
                            pdfArrayList.add(model)
                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                        }
                    }

                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdmin, pdfArrayList)
                    binding.bookRV.adapter = adapterPdfAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${error.message}")
                }
            })
    }
}
