package com.example.librarylink


class FilterCategory(
    private var filterList: ArrayList<ModelCategory>,
    private var adapterCategory: AdapterCategory
) : android.widget.Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()

        // value should be neither null nor empty
        if (!constraint.isNullOrBlank()) {
            // searched value is neither null nor empty

            // change to upper case for case-insensitive comparison
            val constraintUpperCase = constraint.toString().uppercase()

            val filteredModel = filterList.filter { it.category.uppercase().contains(constraintUpperCase) }

            result.count = filteredModel.size
            result.values = filteredModel
        } else {
            // search value is either null or empty
            result.count = filterList.size
            result.values = filterList
        }
        return result
    }

    override fun publishResults(constraint: CharSequence?, result: FilterResults) {
        // apply filter changes
        adapterCategory.categoryArrayList = result.values as ArrayList<ModelCategory>

        // notify changes
        adapterCategory.notifyDataSetChanged()
    }
}
