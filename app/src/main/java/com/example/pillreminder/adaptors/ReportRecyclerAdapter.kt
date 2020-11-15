package com.example.pillreminder.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.models.Report
import kotlinx.android.synthetic.main.report_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReportRecyclerAdapter(
    internal var context: Context,
    private var reportList: List<Report>,
    internal var supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<ReportRecyclerAdapter.ReportViewHolder>() {

    var timeSection: String? = null
    var dateSection: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_row, parent, false)
        return ReportViewHolder(itemView, supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

//    prevent item shuffle
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)

        if (reportList[position].date.toString() != dateSection) {
            holder.tvDate.visibility = View.VISIBLE
            holder.tvDate.text = dateFormat.format(reportList[position].date)
            dateSection = reportList[position].date.toString()
        }
        else {
            holder.tvDate.visibility = View.GONE
        }

        val report = reportList[position]
        Log.e("report row date", "${report.date}")


        Log.e("report row", "${report.name} ${report.time}")

        if (getTimeSection(report.time!!) != timeSection) {
            holder.tvTimeSection.visibility = View.VISIBLE
            holder.tvTimeSection.text = getTimeSection(report.time!!)
            timeSection = getTimeSection(report.time!!)
        }
        else {
            holder.tvTimeSection.visibility = View.GONE
        }

        holder.tvTitle.text = report.name
        holder.tvTime.text = report.time
        holder.tvStatus.text = report.status

        when {
            report.type == "pill" -> {
                holder.ivIcon.setImageResource(R.drawable.pill_selected)
            }
            report.type == "appointment" -> {
                holder.ivIcon.setImageResource(R.drawable.appointment_selected)
            }
            report.type == "vaccine" -> {
                holder.ivIcon.setImageResource(R.drawable.vaccine_selected)
            }
        }

//        view.setOnClickListener {
//            Toast.makeText(holder.linearView.context, "clicked", Toast.LENGTH_SHORT).show()
//        }

        holder.setItemPos(position)

    }

    fun getTimeSection(time: String): String {
        var parts = time.split(":")
        val hour = parts[0]
        parts = parts[1].split(" ")
        val am_pm = parts[1]
        Log.e("report ", "hour $hour am/pm $am_pm")

        if (hour.toInt() in 6..11 && am_pm.toLowerCase() == "am") {
            return "Morning"
        }
        if ((hour.toInt() == 12 && am_pm.toLowerCase() == "pm") || (hour.toInt() in 1..3 && am_pm.toLowerCase() == "pm")) {
            return "Noon"
        }
        if (hour.toInt() in 4..7 && am_pm.toLowerCase() == "pm") {
            return "Evening"
        }
        if (hour.toInt() in 8..11 && am_pm.toLowerCase() == "pm") {
            return "Night"
        }

        return "Night"
    }

    class ReportViewHolder(
        itemView: View,
        supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.tvDateSection
        var tvTimeSection: TextView = itemView.findViewById(R.id.tvTimeSection)
        var tvTitle: TextView = itemView.findViewById(R.id.med_name_report)
        var tvTime: TextView = itemView.findViewById(R.id.tvReportTime)
        var tvStatus: TextView = itemView.findViewById(R.id.med_status_report)
        var ivIcon: ImageView = itemView.findViewById(R.id.imageView23)
        var pos = 0
        fun setItemPos(position: Int) {
            pos = position
        }

        init {
//            add.setOnClickListener {
//                Toast.makeText(itemView.context, pos.toString(), Toast.LENGTH_SHORT).show()
//            }
        }
    }

}