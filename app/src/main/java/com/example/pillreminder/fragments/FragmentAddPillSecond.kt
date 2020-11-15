package com.example.pillreminder.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.Helper.actionOnService
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_add_pill_first.imageView33
import kotlinx.android.synthetic.main.fragment_add_pill_second.*
import kotlinx.android.synthetic.main.fragment_add_pill_second.tvEndAt
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentAddPillSecond : DialogFragment() {

    private var table_id : Int? = null
    private var pillName : String? = null
    private var stock : Int? = 0
    private var noOfDays : Int? = 0
    private var noOfDose : Int? = 0
    private var frequency : Int = 1
    private var periodically : Int = 0
    private var isDaily : Boolean = true
    private var reminder : Boolean? = true
    private var beforeOrAfterMeal : String? = null
    private var morningAlarm : String = ""
    private var noonAlarm : String = ""
    private var eveningAlarm : String = ""
    private var nightAlarm : String = ""

    private var lowestStock : Int? = 0
    private var stockReminder : Boolean? = false

    var startDate:String?=null
    var endDate:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
//        dialog.window.attributes.windowAnimations = R.style.dialog_animation
    }
//
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         val root = RelativeLayout(getActivity());
         root.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

         // creating the fullscreen dialog
         val dialog = Dialog(getActivity()!!,R.style.MyCustomTheme);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.setContentView(root);
         dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE));
         dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

         return dialog;
       // return  AlertDialog.Builder( (activity as DashboardActivity), R.style.FullScreenDialogStyle ).setView(R.layout.fragment_add_pill_first).create()
//        val context = ContextThemeWrapper(activity, R.style.FullScreenDialogStyle)
//        val builder = AlertDialog.Builder(context)
//        return builder.create()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_pill_second, container)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        table_id = arguments?.getInt("tableId")
        pillName = arguments?.getString("pillName")
        noOfDays = arguments?.getInt("noOfDay")
        noOfDose = arguments?.getInt("noOfDose")
        stock = arguments?.getInt("stock")
        reminder = arguments?.getBoolean("reminder")
        startDate = arguments?.getString("startFrom")
        endDate = arguments?.getString("endAt")
        Log.e("table id in on attach",table_id.toString())

        stockReminder = arguments?.getBoolean("stockReminder")
        if (stockReminder!!)
            lowestStock = arguments?.getString("lowestStock")!!.toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView33.setOnClickListener{
            dialog?.dismiss()
        }
//        Log.e("table id default",table_id.toString())
        Log.e("table id from arguments",arguments?.getInt("table_id").toString())

        setSwitchListeners()
        setEditTextListeners()
        setFrequencyListeners()
        setPeriodicallyListeners()
        btnSave.setOnClickListener {
           savePill()
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
//        val frequency=arrayOf("Daily","Periodically")
//        val spFreqAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, frequency)
        val spFreqAdapter = ArrayAdapter.createFromResource(context!!,R.array.array_sp_frequency, android.R.layout.simple_spinner_item)
        spFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFrequency.adapter=spFreqAdapter

        val spPeriAdapter = ArrayAdapter.createFromResource(context!!,R.array.array_sp_periodically, android.R.layout.simple_spinner_item)
        spPeriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPeriodically.adapter=spPeriAdapter

    }

    private fun setEditTextListeners() {
        etMorningAdd.setOnClickListener {
            setTime("morning")
        }
        etNoonAdd.setOnClickListener {
            setTime("noon")
        }
        etEveningAdd.setOnClickListener {
            setTime("evening")
        }
        etNightAdd.setOnClickListener {
            setTime("night")
        }
    }

    private fun setFrequencyListeners() {
        spFrequency.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
               if (parent?.getItemAtPosition(pos)=="Daily") {
                   isDaily=true
                   frequency=1
                   tvPeriodically.visibility=View.INVISIBLE
                   spPeriodically.visibility=View.INVISIBLE

                   updateEndDate()

                   Log.e("pill add", "onItemSelected: daily $isDaily frequency $frequency")

               }
                else{
                   isDaily=false
                   frequency=periodically
                   tvPeriodically.visibility=View.VISIBLE
                   spPeriodically.visibility=View.VISIBLE

                   updateEndDate()

                   Log.e("pill add", "onItemSelected: daily $isDaily frequency $frequency")
               }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setPeriodicallyListeners() {
        spPeriodically.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {

                val selected: String =spPeriodically.selectedItem.toString()
                val parts=selected.split(" ")
                periodically=parts[1].toInt()
                if (!isDaily)
                frequency=periodically

                updateEndDate()

                Log.e("pill add", "onItemSelected: daily $isDaily frequency $frequency")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }


    private fun setSwitchListeners() {

        switch12.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etMorningAdd.visibility = View.VISIBLE
                etMorningAdd.setText("09:00 AM")
                morningAlarm="09:00"
            }else{
                etMorningAdd.visibility = View.GONE
                etMorningAdd.setText("")
                morningAlarm=""
            }
        }
        switch13.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etNoonAdd.visibility = View.VISIBLE
                etNoonAdd.setText("1:00 PM")
                noonAlarm="13:00"
            }else{
                etNoonAdd.visibility = View.GONE
                etNoonAdd.setText("")
                noonAlarm=""
            }
        }
        switch14.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etEveningAdd.visibility = View.VISIBLE
                etEveningAdd.setText("5:00 PM")
                eveningAlarm="17:00"
            }else{
                etEveningAdd.visibility = View.GONE
                etEveningAdd.setText("")
                eveningAlarm=""
            }
        }
        switch15.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etNightAdd.visibility = View.VISIBLE
                etNightAdd.setText("9:00 PM")
                nightAlarm="21:00"
            }else{
                etNightAdd.visibility = View.GONE
                etNightAdd.setText("")
                nightAlarm=""
            }
        }

        switch16.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                switch17.isChecked = false
                beforeOrAfterMeal="before"
            }
        }
        switch17.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                switch16.isChecked = false
                beforeOrAfterMeal="after"
            }
        }
    }

    private fun setTime(type : String){
        val cal = Calendar.getInstance()
        if (type=="morning"){
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
        }
        if (type=="noon"){
            cal.set(Calendar.HOUR_OF_DAY, 13)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
        }
        if (type=="evening"){
            cal.set(Calendar.HOUR_OF_DAY, 17)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
        }
        if (type=="night"){
            cal.set(Calendar.HOUR_OF_DAY, 21)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
        }

        var time : String? = ""
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            Log.e("hour variable", hour.toString())
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            val format = SimpleDateFormat("HH:mm")
            time = format.format(cal.time)
            Log.e("add pill", "setTime: $time")
            if(type == "morning" && hour>=6 && hour<12){
                morningAlarm = time!!
                etMorningAdd.setText(formatTime(time!!))
            }else if(type == "noon" && hour>=12 && hour<16){
                noonAlarm = time!!
                etNoonAdd.setText(formatTime(time!!))
            }else if(type == "evening" && hour>=16 && hour<20){
                eveningAlarm = time!!
                etEveningAdd.setText(formatTime(time!!))
            }else if(type == "night" && (hour>=20 || hour<6)){
                nightAlarm = time!!
                etNightAdd.setText(formatTime(time!!))
            }else{
                Toast.makeText(context, "Please maintain the time frame", Toast.LENGTH_LONG).show()
                if(type == "morning"){
                    etMorningAdd.setText("")
                    morningAlarm = ""
                }else if(type == "noon"){
                    etNoonAdd.setText("")
                    noonAlarm = ""
                }else if(type == "evening"){
                    etEveningAdd.setText("")
                    eveningAlarm = ""
                }else if(type == "night"){
                    etNightAdd.setText("")
                    nightAlarm = ""
                }
            }
//            (activity as DashboardActivity).startAlarm(cal)
        }
//        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        var timePicker = TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
        timePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePicker.show()
    }

    //formate time with AM,PM for button
    fun formatTime(time:String): String? {
        val timeParts = time.split(":")
        var hour=timeParts[0].toInt()
        val minute=timeParts[1].toInt()
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

     fun savePill() {
         Helper.pillList.clear()
         Helper.timePivotList.clear()

        if (!switch16.isChecked&&!switch17.isChecked){
            Log.e("add pill", "onValidationSucceeded: ${switch16.isChecked} ${switch17.isChecked}")
            Toast.makeText(AccountKitController.getApplicationContext(),"Please select before or after meal",Toast.LENGTH_SHORT).show()

            return
        }
        if (etNightAdd.text.isEmpty()&&etMorningAdd.text.isEmpty()&&etNoonAdd.text.isEmpty()&&etEveningAdd.text.isEmpty()){
            Toast.makeText(AccountKitController.getApplicationContext(),"Please select at least one time",Toast.LENGTH_SHORT).show()

            return
        }


        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
//        setAlarms()
        val task_id = System.currentTimeMillis()

        Thread{
            val token: String? = SessionManager(context!!).fetchAuthToken()

            val c = Calendar.getInstance()
            val current = DateFormat.getDateInstance().format(c.time)
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
//            val simpleDateFormat= SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()
//            val currentDate=simpleDateFormat.parse(current).toString()

            Log.i("date update: ",currentDateTime)

            val newPill = Pill(table_id!!,pillName!!,morningAlarm,noonAlarm,eveningAlarm,nightAlarm,beforeOrAfterMeal!!,isDaily,frequency,noOfDays!!,noOfDose!!,stock!!,"N/A",reminder!!,startDate!!,currentDateTime,task_id+1,task_id+2,task_id+3,task_id+4,(activity as DashboardActivity).user_id!!,false,lowestStock!!,stockReminder!!)
            val id = database.pillDao().savePill(newPill)


            if (etMorningAdd.text.toString().isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,pillName!!,"pill",morningAlarm,task_id+1,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved morning pivot")
            }
            if (etNoonAdd.text.toString().isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,pillName!!,"pill",noonAlarm,task_id+2,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved noon pivot")
            }
            if (etEveningAdd.text.toString().isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,pillName!!,"pill",eveningAlarm,task_id+3,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved evening pivot")
            }
            if (etNightAdd.text.toString().isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,pillName!!,"pill",nightAlarm,task_id+4,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved night pivot")
            }

            Log.e("pill title", pillName!!)

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
//            Helper.pillList.add(newPill)
                Log.e("pill add ", "starting push service "+pillName!!)
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", "pill")
                service.putExtra("token", token)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"pill")
                Log.e("pill add ", "alarm for sync "+pillName!!)
            }


            if (reminder!!)
            setAlarms(id.toInt(), "pill", pillName!!,task_id)

            newPill.localId = id.toInt()
            Log.e("temp id",newPill.localId.toString())
            (activity as DashboardActivity).runOnUiThread {
                 ((activity as DashboardActivity).fragmentPillList.pills as ArrayList?)?.add(newPill)
                (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                database.close()

                //dismiss first fragment
                val firstFragment= fragmentManager!!.findFragmentByTag("addPill")
                (activity as DashboardActivity).manager.beginTransaction().remove(firstFragment!!).commit()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()

                //start foreground service
//        actionOnService(context!!,Helper.Actions.START)
            }
        }.start()
        Toast.makeText(context, "Medicine added Successfully", Toast.LENGTH_LONG).show();
    }

    private fun setAlarms(id: Int, reminderType: String, title: String, task_id:Long) {
        val cal = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy  HH:mm")
        val currentDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))

        if(morningAlarm == null || morningAlarm == ""){
            Log.e("morning alarm time null", "null given")
        }else{
            val temp = morningAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title, morningAlarm.toString(),task_id+1)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("morning alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title, morningAlarm.toString(),task_id+1)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("morning alarm time set", "$alarmTime $currentDateTime")
            }

        }
        if(noonAlarm == null || noonAlarm == ""){
            Log.e("noon alarm time null", "null given")
        }else{
            //Log.e("check noon alarm", noonAlarm)
            val temp = noonAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title,noonAlarm.toString(),task_id+2)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("noon alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title,noonAlarm.toString(),task_id+2)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("noon alarm time set", "$alarmTime $currentDateTime")
            }
         }
        if(eveningAlarm == null || eveningAlarm == ""){
            Log.e("evening alarm time null", "null given")
        }else{
            val temp = eveningAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title,eveningAlarm.toString(),task_id+3)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("evening alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title,eveningAlarm.toString(),task_id+3)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("evening alarm time set", "$alarmTime $currentDateTime")
            }
        }
        if(nightAlarm == null || nightAlarm == ""){
            Log.e("night alarm time null", "null given")
        }else{
            val temp = nightAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title,nightAlarm.toString(),task_id+4)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("night alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, reminderType,isDaily,frequency, title,nightAlarm.toString(),task_id+4)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("night alarm time set", "$alarmTime $currentDateTime")
            }
        }
    }

    fun updateEndDate(){
        val cal = Calendar.getInstance()
        val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        cal.setTime(simpleDateFormat.parse(startDate))
        if (noOfDays in 1..364 ){
            if (isDaily)
                cal.add(Calendar.DATE, noOfDays!!)
            else{
                cal.add(Calendar.DATE, noOfDays!!*(frequency+1))
            }

            endDate=simpleDateFormat.format(cal.time).toString()
            tvEndAt.setText(endDate)
            cal.setTime(simpleDateFormat.parse(startDate))
        }else{
            tvEndAt.setText("Continue")
        }
    }
}
