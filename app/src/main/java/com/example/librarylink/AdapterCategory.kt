package com.example.librarylink

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.librarylink.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory(
    private val context: Context,
    public var categoryArrayList: ArrayList<ModelCategory>,
    private var filter: FilterCategory? = null
) : RecyclerView.Adapter<AdapterCategory.HolderCategory>(), Filterable {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        val binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val model = categoryArrayList[position]
        val id = model.id
        val category =model.category
        val uid = model.uid
        val timestamp =model.timestamp

        holder.categoryTv.text = model.category

        holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Delete")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Confirm") { _, _ ->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show() // Show the dialog
        }


    // handle click, start pdf list main activity, also pas pdf id, title

        holder.itemView.setOnClickListener{
            val intent = Intent(context, PdfListAdmin::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
}
    private fun deleteCategory(model: ModelCategory) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Unable To delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryTv: TextView = itemView.findViewById(R.id.categoryTv)
        var deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
    }

    override fun getFilter(): Filter? {
        if (filter == null) {
            filter = FilterCategory(ArrayList(categoryArrayList), this)
        }
        return filter as FilterCategory
    }
}
