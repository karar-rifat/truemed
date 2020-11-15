package com.example.pillreminder.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.models.Notification
import kotlinx.android.synthetic.main.notification_row.view.*
import kotlinx.android.synthetic.main.notification_row.view.tvDateSection
import java.text.SimpleDateFormat
import java.util.*

class NotificationRecyclerAdapter(
    internal var context: Context,
    private var notificationList: List<Notification>,
    internal var supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<NotificationRecyclerAdapter.NotificationViewHolder>() {

    var timeSection: String? = null
    var dateSection: String? = null

    class NotificationViewHolder(
        notificationList: List<Notification>,
        itemView: View,
        supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView) {
        val tvDateSection = itemView.tvDateSection
        var tvTimeSection: TextView = itemView.tvTimeSection
        val tvTitle = itemView.tvTitle
        val tvTime = itemView.tvTime
        val ivIcon = itemView.ivIcon

        var itemPos: Int = 0
        fun setItemPosition(itemPos: Int) {
            this.itemPos = itemPos
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_row, parent, false)
        return NotificationViewHolder(notificationList, itemView, supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)

        if (notificationList[position].date.toString() != dateSection) {
            holder.tvDateSection.visibility = View.VISIBLE
            holder.tvDateSection.text = dateFormat.format(dateFormat.parse(notificationList[position].date))
            dateSection = notificationList[position].date.toString()
        } else {
            holder.tvDateSection.visibility = View.GONE
        }

        val notification = notificationList[position]
        Log.e("notification row date", "${notification.date}")


        Log.e("notification row", "${notification.title} ${notification.time}")

        if (getTimeSection(notification.time!!) != timeSection) {
            holder.tvTimeSection.visibility = View.VISIBLE
            holder.tvTimeSection.text = getTimeSection(notification.time!!)
            timeSection = getTimeSection(notification.time!!)
        }
        else {
            holder.tvTimeSection.visibility = View.GONE
        }

//        holder.txtNumber.text = (position+1).toString()
    holder.tvTitle.text = notificationList[position].title
    holder.tvTime.text = notificationList[position].time

    if (notificationList[position].type=="pill")
    {
        holder.ivIcon.setImageDrawable(context.resources.getDrawable(R.drawable.pill_selected))
    }
    if (notificationList[position].type=="vaccine")
    {
        holder.ivIcon.setImageDrawable(context.resources.getDrawable(R.drawable.vaccine_selected))
    }
    if (notificationList[position].type=="appointment")
    {
        holder.ivIcon.setImageDrawable(context.resources.getDrawable(R.drawable.appointment_selected))
    }

}

//formate time with AM,PM for button
fun formatTime(time: String): String? {
    val timeParts = time.split(":")
    var hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()
    val format: String
    val formattedTime: String
    when {
        hour == 0 -> {
            hour += 12
            format = "AM"
        }
        hour == 12 -> {
            format = "PM"
        }
        hour > 12 -> {
            hour -= 12
            format = "PM"
        }
        else -> {
            format = "AM"
        }
    }
    val newMinute = if (minute < 10) "0$minute" else minute.toString()
    formattedTime = "$hour:$newMinute $format"
    return formattedTime
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


}