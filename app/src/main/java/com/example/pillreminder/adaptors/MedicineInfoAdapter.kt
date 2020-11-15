package com.example.pillreminder.adaptors

import android.content.Context
import android.provider.Contacts.People
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.pillreminder.R
import com.example.pillreminder.network.response.MedicineResponse
import java.util.*
import kotlin.collections.ArrayList


class MedicineInfoAdapter(
    internal var context: Context,
    var textViewResourceId: Int,
    var medicineList: List<MedicineResponse>
) :
    ArrayAdapter<MedicineResponse?>(context, textViewResourceId, medicineList) {
    var tempItems: List<MedicineResponse> = ArrayList(medicineList)
    var suggestions: MutableList<MedicineResponse> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val medicine = medicineList[position]
        var view=convertView
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_info_row, parent, false);
        }
        val name = view!!.findViewById<View>(R.id.tvMedicine) as TextView
        name.text = "${medicine.productname}"
        return view
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    private var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as MedicineResponse).productname!!
        }

         override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                for (medicine in tempItems) {
                    if (medicine.productname!!.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                        suggestions.add(medicine)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            try {
                val filterList: List<MedicineResponse> = results?.values as List<MedicineResponse>

                if (results.count > 0) {
                    clear()
                    for (medicine in filterList) {
                        add(medicine)
                        notifyDataSetChanged()
                    }
                }
            }catch (e: TypeCastException){}
        }
    }

    init {
        // this makes the difference.
    }
}