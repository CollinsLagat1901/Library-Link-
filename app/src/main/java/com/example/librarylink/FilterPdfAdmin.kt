package com.example.librarylink

import java.util.logging.Filter

class FilterPdfAdmin: android.widget.Filter {

    var filterList: ArrayList<ModelPdf>

    var adapterPdfAdmin: AdapterPdfAdmin

    //constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfAdmin: AdapterPdfAdmin) {
        this.filterList = filterList
        this.adapterPdfAdmin = adapterPdfAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filterString = constraint?.toString()?.lowercase() ?: ""

        val results = FilterResults()

        if (filterString.isNotEmpty()) {
            val filterModel = ArrayList<ModelPdf>()
            for (model in filterList) {
                if (model.title.lowercase().contains(filterString)) {
                    filterModel.add(model)
                }
            }
            results.count = filterModel.size
            results.values = filterModel
        } else {
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }



    override fun publishResults(constraint: CharSequence, results: FilterResults){
        //apply filter changes
        adapterPdfAdmin.pdfArrayList= results.values as ArrayList<ModelPdf>

        //notify changes
        adapterPdfAdmin.notifyDataSetChanged()
    }

}