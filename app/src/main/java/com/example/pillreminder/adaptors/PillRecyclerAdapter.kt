package com.example.pillreminder.adaptors

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.Table
import kotlinx.android.synthetic.main.table_title.view.*
import com.example.pillreminder.fragments.FragmentPillDetailsFirst
import com.example.pillreminder.fragments.FragmentTableEdit
import com.example.pillreminder.models.DashboardData
import kotlinx.android.synthetic.main.pill_item_row.view.*
import java.util.*


class PillRecyclerAdapter(
    internal var context: Context,
    internal var tableList: List<Table>,
    internal var pillList: List<Pill>,
    internal var supportFragmentManager: FragmentManager
) :
    RecyclerView.Adapter<PillRecyclerAdapter.PillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_title, parent, false)
        return PillViewHolder(tableList, itemView, supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return tableList.size
    }

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        holder.tl.removeAllViews()

        holder.table_title.text = tableList[position].title?.toString()
        Log.e("inside loop", tableList[position].localId.toString())
        var i: Int = 0
        for (pill in pillList) {
            //check table id for pill
//            if(pill.tableId==tableList[position].localId){
            var index = i
//                Log.e("inside loop",pill.table_id.toString())
            var view1 = LayoutInflater.from(holder.tl.context)
                .inflate(R.layout.pill_item_row, holder.tl, false)

            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            Log.e("pill list :", "hour of day: $hour")
            if (hour in 6..11) {
                val temp = pill.morning?.split(":")
                if (!temp.isNullOrEmpty()) {
                    try {
                        val hourMorning = temp?.get(0)?.toInt() ?: 0
                        if (hour <= hourMorning)
                            view1.tvTime.text = formatTime(pill.morning!!)
                        else
                            view1.tvTime.text = pill.noon
                    } catch (ex: Exception) {
                    }
                }
            } else if (hour >= 12 && hour < 16) {
                val temp = pill.noon?.split(":")
                if (!temp.isNullOrEmpty()) {
                    try {
                        val hourNoon = temp?.get(0)?.toInt() ?: 0
                        if (hour <= hourNoon)
                            view1.tvTime.text = formatTime(pill.noon!!)
                        else
                            view1.tvTime.text = formatTime(pill.evening!!)
                    } catch (ex: Exception) {
                    }
                }
            } else if (hour >= 16 && hour < 20) {
                val temp = pill.evening?.split(":")
                if (!temp.isNullOrEmpty()) {
                    try {
                        val hourEvening = temp?.get(0)?.toInt() ?: 0
                        if (hour <= hourEvening)
                            view1.tvTime.text = formatTime(pill.evening!!)
                        else
                            view1.tvTime.text = formatTime(pill.night!!)
                    } catch (ex: Exception) {
                    }
                }
            } else if (hour >= 20 || hour < 6) {
                val temp = pill.evening?.split(":")
                if (!temp.isNullOrEmpty()) {
                    try {
                        val hourNight = temp.get(0).toInt() ?: 0
                        if (hour <= hourNight)
                            view1.tvTime.text = formatTime(pill.night!!)
                        else
                            view1.tvTime.text = formatTime(pill.morning!!)
                    } catch (ex: Exception) {
                    }
                }
            }

            view1.tvPillName.text = pill.title

            if (pill.beforeOrAfterMeal == "before") {
                view1.tvBeforeOrAfter.text = "Before Meal"
            } else {
                view1.tvBeforeOrAfter.text = "After Meal"
            }

            val dose = "${pill.noOfDose} pill(s)"

            view1.tvDose.text = dose
            view1.tvStock.text = pill.stock.toString()

            if (pill.reminder!!)
                view1.tvReminder.text = "Active"
            else
                view1.tvReminder.text = "Inactive"

            if (pill.morning!!.isEmpty()) {
                view1.tvMorning.text = " - "
            }
            if (pill.noon!!.isEmpty()) {
                view1.tvNoon.text = " - "
            }
            if (pill.evening!!.isEmpty()) {
                view1.tvEvening.text = " - "
            }
            if (pill.night!!.isEmpty()) {
                view1.tvNight.text = " - "
            }

//            if(pill.morning!!.isNotEmpty()){
//                view1.tvTime.text = "M - "
//            }else{
//                view1.tvTime.text = " - "
//            }
//            if(pill.noon!!.isNotEmpty()){
//                view1.tvTime.text = view1.tvTime.text.toString() + "N - "
//            }else{
//                view1.tvTime.text = view1.tvTime.text.toString() + "- "
//            }
//            if(pill.evening!!.isNotEmpty()){
//                view1.tvTime.text = view1.tvTime.text.toString() + "E - "
//            }else{
//                view1.tvTime.text = view1.tvTime.text.toString() + "- "
//            }
//            if(pill.night!!.isNotEmpty()){
//                view1.tvTime.text = view1.tvTime.text.toString() + "N"
//            }else{
//                view1.tvTime.text = view1.tvTime.text.toString() + " "
//            }


            /*
            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            val c = Calendar.getInstance()
            val current = simpleDateFormat.format(c.time).toString()
            c.setTime(simpleDateFormat.parse(pill.created_at))
            c.add(Calendar.DATE, pill.noOfDay!!.toInt())
            val dateToEnd: Date = simpleDateFormat.parse(simpleDateFormat.format(c.time))
            val currentDate: Date = simpleDateFormat.parse(current)

            Log.e("check date table ", dateToEnd.toString() + " " + current)
            if(currentDate.compareTo(dateToEnd) < 0){
                view1.pillStatus.text = "Course Incomplete"
            }
            else{
                view1.pillStatus.text = "Course Completed"
            }

             */

            holder.tl.addView(view1)
//                holder.table_title.text = tableList[position].title?.toString()
            view1.setOnClickListener {
                handleBtnClick(it)

//                    val pop = FragmentPillDetails()
                val pop = FragmentPillDetailsFirst()
                val bundle = Bundle()
                bundle.putInt("position", index)
                bundle.putInt("id", pill.localId)
                bundle.putInt("table_id", pill.tableId!!)
                bundle.putString("pillName", pill.title)
                bundle.putString("morning", pill.morning)
                bundle.putString("noon", pill.noon)
                bundle.putString("evening", pill.evening)
                bundle.putString("night", pill.night)
                bundle.putString("beforeOrAfterMeal", pill.beforeOrAfterMeal!!)
                bundle.putBoolean("daily", pill.everyday!!)
                bundle.putInt("frequency", pill.frequency!!)
                bundle.putInt("noOfDay", pill.noOfDay!!)
                bundle.putInt("noOfDose", pill.noOfDose!!)
                bundle.putInt("stock", pill.stock!!)
                bundle.putInt("lowestStock", pill.lowestStock!!)
                bundle.putBoolean("stockReminder", pill.stockReminder!!)
                bundle.putString("lastPillStatus", pill.lastPillStatus)
                bundle.putBoolean("reminder", pill.reminder!!)
                bundle.putString("startFrom", pill.startFrom)
                bundle.putLong("task_id_m", pill.taskIdMorning!!)
                bundle.putLong("task_id_no", pill.taskIdNoon!!)
                bundle.putLong("task_id_e", pill.taskIdEvening!!)
                bundle.putLong("task_id_ni", pill.taskIdNight!!)
                bundle.putString("user_id", pill.userId!!)
                bundle.putInt("remoteId", pill.remoteId!!)

                pop.arguments = bundle
                val fm = supportFragmentManager
                pop.show(fm, "pillDetails")
//                    Toast.makeText(holder.tl.context, "clicked", Toast.LENGTH_SHORT).show()
            }
            i++
//            }
        }
        holder.setItemPos(position)
    }

    fun handleBtnClick(view: View) {
        view.isEnabled = false
        val secondsDelayed = 2
        Handler().postDelayed(Runnable {
            view.isEnabled = true
        }, ((secondsDelayed * 1000).toLong()))
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

    class PillViewHolder(
        tableList: List<Table>,
        itemView: View,
        supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView) {

        var table_title = itemView.tableTitle
        var tl = itemView.table

        //        var add = itemView.add
        var settings = itemView.tableSettings
        var scrollerForward = itemView.scrollButtonForward
        var scrollerBackward = itemView.scrollButtonBackward

        //        var scroller = itemView.hScroll
        var pos = 0
        fun setItemPos(position: Int) {
            pos = position
        }

        init {
//            add.setOnClickListener {
//                val pop = FragmentAddPillTableRow()
//                val bundle = Bundle()
//                bundle.putInt("table_id", tableList[pos].id)
//                Log.e("table Id Send",tableList[pos].id.toString())
//                pop.arguments = bundle
//                val fm = supportFragmentManager
////                Log.e("table_id",tableList[pos].id.toString())
////                Log.e("pos",pos.toString())
//                pop.show(fm, "name")
//            }
            settings.setOnClickListener {
                val pop = FragmentTableEdit()
                val bundle = Bundle()
                bundle.putInt("position", pos)
                bundle.putInt("table_id", tableList[pos].localId)
                bundle.putString("table_title", tableList[pos].title)
                bundle.putString("user_id", tableList[pos].userId!!)
                Log.e("table Id Send", tableList[pos].localId.toString())
                Log.e("table title Send", tableList[pos].title!!)
                Log.e("table pos Send", pos.toString())
                pop.arguments = bundle
                val fm = supportFragmentManager
                pop.show(fm, "name")
            }
//            scrollerForward.setOnClickListener {
//                Log.e("scroller","clicking")
//                scroller.post(Runnable { scroller.fullScroll(HorizontalScrollView.FOCUS_RIGHT) })
//                //itemView.hScroll.scrollTo(2000,0)
//                scrollerBackward.visibility = View.VISIBLE
//                scrollerForward.visibility = View.GONE
//            }
//            scrollerBackward.setOnClickListener {
//                Log.e("scroller","clicking")
//                //itemView.hScroll.scrollTo(0,0)
//                scroller.post(Runnable { scroller.fullScroll(HorizontalScrollView.FOCUS_LEFT) })
//                scrollerBackward.visibility = View.GONE
//                scrollerForward.visibility = View.VISIBLE
//            }
        }
    }

}