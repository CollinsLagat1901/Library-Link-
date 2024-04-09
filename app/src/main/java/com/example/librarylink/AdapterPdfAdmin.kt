package com.example.librarylink

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.librarylink.databinding.RowPdfAdminBinding
class AdapterPdfAdmin(
    private val context: Context,
    var pdfArrayList: ArrayList<ModelPdf>
) : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>(), Filterable {

    private var filterList: ArrayList<ModelPdf> = pdfArrayList
    private var filter: FilterPdfAdmin? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        val binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfAdmin(binding)
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        holder.binding.titleTv.text = title
        holder.binding.descriptionTv.text = description
        holder.binding.dateTv.text = formattedDate

        MyApplication.loadCategory(categoryId = categoryId, holder.binding.categoryTv)
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, holder.binding.pdfView, holder.binding.progressBar, null)
        MyApplication.loadPdfSize(pdfUrl, title, holder.binding.sizeTv)

        holder.binding.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetails::class.java)
            intent.putExtra("bookId", pdfId)
            context.startActivity(intent)
        }
    }

    private fun moreOptionsDialog(model: ModelPdf, holder: HolderPdfAdmin) {
        val bookId = model.id
        val bookUrl = model.url
        val bookTitle = model.title
        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) { _, position ->
                when (position) {
                    0 -> {
                        val intent = Intent(context, PdfEditActivity::class.java)
                        intent.putExtra("bookId", bookId)
                        context.startActivity(intent)
                    }
                    1 -> MyApplication.deleteBook(context, bookId, bookUrl, bookTitle)
                }
            }.show()
    }

    override fun getItemCount(): Int = pdfArrayList.size

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterPdfAdmin(filterList, this)
        }
        return filter!!
    }

    inner class HolderPdfAdmin(val binding: RowPdfAdminBinding) : RecyclerView.ViewHolder(binding.root)
}
