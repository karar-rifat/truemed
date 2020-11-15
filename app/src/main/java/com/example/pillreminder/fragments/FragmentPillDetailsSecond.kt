package com.example.pillreminder.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.service.DataDeleteService
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_pill_details_second.*
import kotlinx.android.synthetic.main.fragment_pill_details_second.btnSave
import kotlinx.android.synthetic.main.fragment_pill_details_second.switch12
import kotlinx.android.synthetic.main.fragment_pill_details_second.switch13
import kotlinx.android.synthetic.main.fragment_pill_details_second.switch14
import kotlinx.android.synthetic.main.fragment_pill_details_second.switch15
import kotlinx.android.synthetic.main.fragment_pill_details_second.switch16
import kotlinx.android.synthetic.main.fragment_pill_details_second.switch17
import kotlinx.android.synthetic.main.fragment_pill_details_second.tvPeriodically
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentPillDetailsSecond : DialogFragment() {


    private var position : Int? = null
    private var id : Int? = null
    private var table_id : Int? = null
    private var title : String? = null
    private var morning : String? = null
    private var noon : String? = null
    private var evening : String? = null
    private var night : String? = null
    private var isDaily : Boolean? = null
    private var frequency : Int? = 0
    private var periodically : Int? = 0
    private var beforeOrAfterMeal : String? = null
    private var numOfDay : Int? = null
    private var numOfDose : Int? = null
    private var stock : Int? = null
    private var lastPillStatus : String? = null
    private var reminder : Boolean? = null
    private var startFrom : String? = null
    private var task_id_m : Long? = null
    private var task_id_no : Long? = null
    private var task_id_e : Long? = null
    private var task_id_ni : Long? = null
    private var user_id : String? = null
    private var endDate : String? = null

    private var remoteId: Int? = null

    private var lowestStock : Int? = 0
    private var stockReminder : Boolean? = false

    var spFreqAdapter:ArrayAdapter<CharSequence>?=null
    var spPeriAdapter:ArrayAdapter<CharSequence>?=null

    @NotEmpty
    private var medNameET: EditText? = null
    @NotEmpty
    private var noOfDayET: EditText? = null
    @NotEmpty
    private var noOfDoseET: EditText? = null
    @NotEmpty
    private var stockET: EditText? = null

    private var morningAlarm : String = ""
    private var noonAlarm : String = ""
    private var eveningAlarm : String = ""
    private var nightAlarm : String = ""

    fun updatePill() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        val task_id = System.currentTimeMillis()

        if (!switch16.isChecked&&!switch17.isChecked){
            Log.e("add pill", "onValidationSucceeded: ${switch16.isChecked} ${switch17.isChecked}")
            Toast.makeText(AccountKitController.getApplicationContext(),"Please select before or after meal",Toast.LENGTH_SHORT).show()

            return
        }
        if (etNightDetails.text.isEmpty()&&etMorningDetails.text.isEmpty()&&etNoonDetails.text.isEmpty()&&etEveningDetails.text.isEmpty()){
            Toast.makeText(AccountKitController.getApplicationContext(),"Please select at least one time",Toast.LENGTH_SHORT).show()

            return
        }

//         morningAlarm = etMorningDetails.text.toString()
//         noonAlarm  = etNoonDetails.text.toString()
//         eveningAlarm  = etEveningDetails.text.toString()
//         nightAlarm  = etNightDetails.text.toString()

        //cancel previous alarm if any segment had alarm and updated
//        if (morning!=null&&morning!=morningAlarm){
        if (morning!=null){
            (activity as DashboardActivity).cancelAlarm(task_id_m!!)
            Log.e("pill details", "cancel alarm: morning time not equal $task_id_m")
        }
        if (noon!=null){
            (activity as DashboardActivity).cancelAlarm(task_id_no!!)
            Log.e("pill details", "cancel alarm: noon time not equal $task_id_no")
        }
        if (evening!=null){
            (activity as DashboardActivity).cancelAlarm(task_id_e!!)
            Log.e("pill details", "cancel alarm: evening time not equal $task_id_e")
        }
        if (night!=null){
//        if (night!=null&&night!=nightAlarm){
            (activity as DashboardActivity).cancelAlarm(task_id_ni!!)
            Log.e("pill details", "cancel alarm: night time not equal $task_id_ni")
        }

        if (!switch16.isChecked&&!switch17.isChecked){
            Toast.makeText(AccountKitController.getApplicationContext(),"Please select before or after meal",Toast.LENGTH_SHORT).show()

            return
        }
        if (etMorningDetails.text.isEmpty()&&etNoonDetails.text.isEmpty()&&etEveningDetails.text.isEmpty()&&etNightDetails.text.isEmpty()){
            Toast.makeText(AccountKitController.getApplicationContext(),"Please select at least one time",Toast.LENGTH_SHORT).show()

            return
        }

        Thread {

            //set alarm for each segment if it is updated
            if (reminder!!){
//            if (morning!=morningAlarm){
                if (morningAlarm != null && morningAlarm.isNotEmpty()) {
                    setAlarms(id!!, "morning", title!!, task_id)
                    Log.e("pill details", "onValidationSucceeded: morning alarm set ${task_id + 1}")
                }
            if (noonAlarm != null && noonAlarm.isNotEmpty()) {
                setAlarms(id!!, "noon",  title!!, task_id)
                Log.e("pill details", "onValidationSucceeded: noon alarm set ${task_id + 2}")
            }
            if (eveningAlarm != null && eveningAlarm.isNotEmpty()) {
                setAlarms(id!!, "evening",  title!!, task_id)
                Log.e("pill details", "onValidationSucceeded: evening alarm set ${task_id + 3}")
            }
            if (nightAlarm != null && nightAlarm.isNotEmpty()) {
                setAlarms(id!!, "night",  title!!, task_id)
                Log.e("pill details", "onValidationSucceeded: night alarm set ${task_id + 4}")
            }
        }
           //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newPill = Pill(id!!,remoteId!!,table_id!!, title!!,morningAlarm!!,noonAlarm!!,eveningAlarm!!,nightAlarm!!,beforeOrAfterMeal!!,isDaily!!,frequency!!,numOfDay!!,numOfDose!!,stock!!,lastPillStatus!!,reminder!!,startFrom!!,currentDateTime,task_id_m!!,task_id_no!!,task_id_e!!,task_id_ni!!,user_id!!,false,lowestStock!!,stockReminder!!)
            database.pillDao().update(newPill)

            database.timePivotDao().deleteRows(id!!,"pill")

            if (morningAlarm!=null&& morningAlarm!!.isNotEmpty()){
                val newTimePivot = TimePivot(id!!,(activity as DashboardActivity).user_id!!,title!!,"pill",morningAlarm!!,task_id+1,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved morning pivot")
            }
            if (noonAlarm!=null&& noonAlarm!!.isNotEmpty()){
                val newTimePivot = TimePivot(id!!.toInt(),(activity as DashboardActivity).user_id!!,title!!,"pill",noonAlarm!!,task_id+2,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved noon pivot")
            }
            if (eveningAlarm!=null&& eveningAlarm!!.isNotEmpty()){
                val newTimePivot = TimePivot(id!!.toInt(),(activity as DashboardActivity).user_id!!,title!!,"pill",eveningAlarm!!,task_id+3,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved evening pivot")
            }
            if (nightAlarm!=null&& nightAlarm!!.isNotEmpty()){
                val newTimePivot = TimePivot(id!!.toInt(),(activity as DashboardActivity).user_id!!,title!!,"pill",nightAlarm!!,task_id+4,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved night pivot")
            }
            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                Log.e("pill timePivot delete ", "starting delete service pill")
                val service = Intent(context, DataDeleteService::class.java)
                service.putExtra("type", "timePivot")
                service.putExtra("title", title)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"pill")
                Log.e("pill delete ", "alarm for sync pill")
            }

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                val token=Helper.token
                Log.e("details pill", "starting push service pill")
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", "pill")
                service.putExtra("token", token)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"pill")
                Log.e("details pill ", "alarm for sync pill")
            }

            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentPillList.pills as ArrayList).set(position!!,newPill)
                (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                database.close()

                //dismiss first fragment
                val firstFragment= fragmentManager!!.findFragmentByTag("pillDetails")
                (activity as DashboardActivity).manager.beginTransaction().remove(firstFragment!!).commit()

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Pill Updated Successfully", Toast.LENGTH_LONG).show();
    }

    private fun setAlarms(id: Int, timeSegment: String, title: String, task_id:Long) {
        val cal = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy  HH:mm")
        val currentDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))

        if(timeSegment=="morning"){
            val temp = morningAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title, morningAlarm.toString(),task_id+1)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("morning alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title, morningAlarm.toString(),task_id+1)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("morning alarm time set", "$alarmTime $currentDateTime")
            }
        }
        if(timeSegment=="noon"){
            //Log.e("check noon alarm", noonAlarm)
            val temp = noonAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title,noonAlarm.toString(),task_id+2)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("noon alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title,noonAlarm.toString(),task_id+2)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("noon alarm time set", "$alarmTime $currentDateTime")
            }
        }
        if(timeSegment=="evening"){
            val temp = eveningAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title,eveningAlarm.toString(),task_id+3)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("evening alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title,eveningAlarm.toString(),task_id+3)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("evening alarm time set", "$alarmTime $currentDateTime")
            }
        }
        if(timeSegment=="night"){
            val temp = nightAlarm!!.split(":")
            cal.set(Calendar.HOUR_OF_DAY, temp[0].toInt())
            cal.set(Calendar.MINUTE, temp[1].toInt())
            cal.set(Calendar.SECOND, 0)

            //compare selected date with current date
            val selectedDateTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
            if (selectedDateTime.compareTo(currentDateTime) < 0) {
                cal.add(Calendar.DATE, 1)
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title,nightAlarm.toString(),task_id+4)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("night alarm time set", "$alarmTime $currentDateTime")
                cal.add(Calendar.DATE, -1)
            }
            else{
                (activity as DashboardActivity).startAlarm(cal,"repeating", id, "pill",isDaily!!,frequency!!, title,nightAlarm.toString(),task_id+4)
                val alarmTime: Date = simpleDateFormat.parse(simpleDateFormat.format(cal.time))
                Log.e("night alarm time set", "$alarmTime $currentDateTime")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getInt("position")
        id = arguments?.getInt("id")
        table_id = arguments?.getInt("table_id")
        title = arguments?.getString("pillName")
        morning = arguments?.getString("morning")
        noon = arguments?.getString("noon")
        evening = arguments?.getString("evening")
        night = arguments?.getString("night")
        isDaily = arguments?.getBoolean("daily")
        frequency = arguments?.getInt("frequency")
        beforeOrAfterMeal = arguments?.getString("beforeOrAfterMeal")
        numOfDay = arguments?.getInt("noOfDay")
        numOfDose = arguments?.getInt("noOfDose")
        stock = arguments?.getInt("stock")
        lastPillStatus = arguments?.getString("lastPillStatus")
        reminder = arguments?.getBoolean("reminder")
        startFrom = arguments?.getString("startFrom")
        task_id_m = arguments?.getLong("task_id_m")
        task_id_no = arguments?.getLong("task_id_no")
        task_id_e = arguments?.getLong("task_id_e")
        task_id_ni = arguments?.getLong("task_id_ni")
        user_id = arguments?.getString("user_id")

        remoteId = arguments?.getInt("remoteId")

        stockReminder = arguments?.getBoolean("stockReminder")
        if (stockReminder!!)
        lowestStock = arguments?.getString("lowestStock")!!.toInt()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_pill_details_second, container)
        return rootView
    }

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create an ArrayAdapter using the string array and a default spinner layout
//        val frequency=arrayOf("Daily","Periodically")
//        val spFreqAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, frequency)
        spFreqAdapter = ArrayAdapter.createFromResource(context!!,R.array.array_sp_frequency, android.R.layout.simple_spinner_item)
        (spFreqAdapter as ArrayAdapter<CharSequence>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFrequencyUpdate.adapter=spFreqAdapter

        spPeriAdapter = ArrayAdapter.createFromResource(context!!,R.array.array_sp_periodically, android.R.layout.simple_spinner_item)
        (spPeriAdapter as ArrayAdapter<CharSequence>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPeriodicallyUpdate.adapter=spPeriAdapter

        setInitialData()

        setSwitchListeners()

        setEditTextListeners()

        setFrequencyListeners()
        setPeriodicallyListeners()

        imageView330.setOnClickListener{
            dialog?.dismiss()
        }
        btnSave.setOnClickListener {
            updatePill()
        }

        btnDuplicate.setOnClickListener {

            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to duplicate this Pill ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                    duplicatePill()
                } )
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Duplicate Pill")
            // show alert dialog
            alert.show()
            //duplicateAppointment()
        }

        imageButton15.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this medication ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        _, _ -> deletePill()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, _ -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Delete Pill")
            // show alert dialog
            alert.show()
//            deletePill()
        }

    }

    private fun setFrequencyListeners() {
        spFrequencyUpdate.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (parent?.getItemAtPosition(pos)=="Daily") {
                    isDaily=true
                    frequency=1
                    tvPeriodically.visibility=View.INVISIBLE
                    spPeriodicallyUpdate.visibility=View.INVISIBLE

                    updateEndDate()

                    Log.e("pill details", "onItemSelected: daily $isDaily frequency $frequency")

                }
                else{
                    isDaily=false
                    frequency=periodically
                    tvPeriodically.visibility=View.VISIBLE
                    spPeriodicallyUpdate.visibility=View.VISIBLE

                    updateEndDate()

                    Log.e("pill details", "onItemSelected: daily $isDaily frequency $frequency")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setPeriodicallyListeners() {
        spPeriodicallyUpdate.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {

                    val selected: String =spPeriodicallyUpdate.selectedItem.toString()
                    val parts=selected.split(" ")
                    periodically=parts[1].toInt()
                if (!isDaily!!)
                frequency=periodically

                updateEndDate()

                Log.e("pill details", "onItemSelected: daily $isDaily frequency $frequency")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun deletePill() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        Log.e("position for array",position.toString())
        Thread{
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                Log.e("pill delete ", "starting delete service pill $remoteId")
                val service = Intent(context, DataDeleteService::class.java)
                service.putExtra("type", "pill")
                service.putExtra("title", title)
                service.putExtra("remoteId", remoteId!!)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"pill")
                Log.e("pill delete ", "alarm for sync pill")
            }

            val temp = Pill(id!!,remoteId!!,table_id!!,title!!,morning!!,noon!!,evening!!,night!!,beforeOrAfterMeal!!,isDaily!!,frequency!!,numOfDay!!.toInt(),numOfDose!!.toInt(),stock!!.toInt(),lastPillStatus!!,reminder!!, startFrom!!,currentDateTime,task_id_m!!,task_id_no!!,task_id_e!!,task_id_ni!!,user_id!!,false,lowestStock!!,stockReminder!!)
            database.pillDao().delete(temp)
            //delete timePivot for pill

//            val timePivot=database.timePivotDao().getData(title!!,"pill")
//            timePivot.forEach {
//                database.timePivotDao().deleteRows(it.localId,"pill")
//                Log.e("delete pill","deleted timePivot row ${it.idToMatch}")
//            }


//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentPillList.pills as ArrayList).removeAt(position!!)
                (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                database.close()

                //dismiss first fragment
                val firstFragment= fragmentManager!!.findFragmentByTag("pillDetails")
                (activity as DashboardActivity).manager.beginTransaction().remove(firstFragment!!).commit()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Pill Deleted!!", Toast.LENGTH_LONG).show()
        database.close()
    }

    private fun duplicatePill(){

            Helper.pillList.clear()
            Helper.timePivotList.clear()

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

                val newPill = Pill(table_id!!,"Copy of $title",morningAlarm,noonAlarm,eveningAlarm,nightAlarm,beforeOrAfterMeal!!,isDaily!!,frequency!!,numOfDay!!,numOfDose!!,stock!!,"N/A",false,startFrom!!,currentDateTime,task_id_m!!+1,task_id_no!!+2,task_id_e!!+3,task_id_ni!!+4,(activity as DashboardActivity).user_id!!,false,lowestStock!!,stockReminder!!)
                val id = database.pillDao().savePill(newPill)

                if (morningAlarm.isNotEmpty()){
                    val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",morningAlarm,task_id+1,currentDateTime,false)
                    database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                    Log.i("time pivot: ","saved morning pivot")
                }
                if (noonAlarm.isNotEmpty()){
                    val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",noonAlarm,task_id+2,currentDateTime,false)
                    database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                    Log.i("time pivot: ","saved noon pivot")
                }
                if (eveningAlarm.isNotEmpty()){
                    val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",eveningAlarm,task_id+3,currentDateTime,false)
                    database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                    Log.i("time pivot: ","saved evening pivot")
                }
                if (nightAlarm.isNotEmpty()){
                    val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",nightAlarm,task_id+4,currentDateTime,false)
                    database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                    Log.i("time pivot: ","saved night pivot")
                }

                Log.e("pill title", "Copy of $title")

                //update data to server with background service
                if (Helper.isConnected(context!!)) {
//            Helper.pillList.add(newPill)
                    Log.e("pill add ", "starting push service Copy of $title")
                    val service = Intent(context, DataPushService::class.java)
                    service.putExtra("type", "pill")
                    service.putExtra("token", token)
                    context!!.startService(service)
                }else{
                    AlarmHelper().startAlarmForSync(context!!,"pill")
                    Log.e("pill add ", "alarm for sync Copy of $title")
                }

//                if (reminder!!)
//                    setAlarms(id.toInt(), "pill", title!!,task_id_m!!)

//                newPill.localId = id.toInt()
                Log.e("temp id",id.toString())
                (activity as DashboardActivity).runOnUiThread {
                    ((activity as DashboardActivity).fragmentPillList.pills as ArrayList?)?.add(newPill)
                    (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                    database.close()

                    //dismiss first fragment
                    val firstFragment= fragmentManager!!.findFragmentByTag("pillDetails")
                    (activity as DashboardActivity).manager.beginTransaction().remove(firstFragment!!).commit()

                    //local broadcast for update dashboard
                    val intentFetch=Intent("fetch-data")
                    intentFetch.putExtra("isDataFetched",true)
                    LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                    dialog?.dismiss()

                }
            }.start()
            Toast.makeText(context, "Medicine Duplicated Successfully", Toast.LENGTH_LONG).show();
    }

    private fun setSwitchListeners() {

        switch12.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etMorningDetails.visibility = View.VISIBLE
                if (morning.isNullOrEmpty()){
                    etMorningDetails.setText("09:00 AM")
                    morningAlarm="09:00"
                }else{
                    etMorningDetails.setText(formatTime(morning!!))
                    morningAlarm=morning!!
                }

            }else{
                etMorningDetails.visibility = View.GONE
                etMorningDetails.setText("")
                morningAlarm=""
            }
        }
        switch13.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etNoonDetails.visibility = View.VISIBLE
                if (noon.isNullOrEmpty()){
                    etNoonDetails.setText("1:00 PM")
                    noonAlarm="13:00"
                }else{
                    etNoonDetails.setText(formatTime(noon!!))
                    noonAlarm=noon!!
                }
            }else{
                etNoonDetails.visibility = View.GONE
                etNoonDetails.setText("")
                noonAlarm=""
            }
        }
        switch14.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etEveningDetails.visibility = View.VISIBLE
                if (evening.isNullOrEmpty()){
                    etEveningDetails.setText("5:00 PM")
                    eveningAlarm="17:00"
                }else{
                    etEveningDetails.setText(formatTime(evening!!))
                    eveningAlarm=evening!!
                }
            }else{
                etEveningDetails.visibility = View.GONE
                etEveningDetails.setText("")
                eveningAlarm=""
            }
        }
        switch15.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                etNightDetails.visibility = View.VISIBLE
                if (night.isNullOrEmpty()){
                    etNightDetails.setText("9:00 PM")
                    nightAlarm="21:00"
                }else{
                    etNightDetails.setText(formatTime(night!!))
                    nightAlarm=night!!
                }
            }else{
                etNightDetails.visibility = View.GONE
                etNightDetails.setText("")
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

    private fun setEditTextListeners() {
        etMorningDetails.setOnClickListener {
            setTime("morning")
        }
        etNoonDetails.setOnClickListener {
            setTime("noon")
        }
        etEveningDetails.setOnClickListener {
            setTime("evening")
        }
        etNightDetails.setOnClickListener {
            setTime("night")
        }
    }

    private fun setInitialData() {

        if(beforeOrAfterMeal=="before") {
            switch16.isChecked = true
            switch17.isChecked = false
        }else {
            switch16.isChecked = false
            switch17.isChecked = true
        }

        updateEndDate()

        if (!isDaily!!){
            spFrequencyUpdate.setSelection(1)
            val periodicallyPos:Int=spPeriAdapter!!.getPosition("Every $frequency Days")
            spPeriodicallyUpdate.setSelection(periodicallyPos)
            tvPeriodically.visibility=View.VISIBLE
            spPeriodicallyUpdate.visibility=View.VISIBLE
        }

        if(morning==""){
            switch12.isChecked = false
            etMorningDetails.visibility = View.GONE
        }else{
            switch12.isChecked = true
            morningAlarm=morning!!
            etMorningDetails.visibility = View.VISIBLE
            etMorningDetails.setText(formatTime(morning!!))
        }
        if(noon==""){
            switch13.isChecked = false
            etNoonDetails.visibility = View.GONE
        }else{
            switch13.isChecked = true
            noonAlarm=noon!!
            etNoonDetails.visibility = View.VISIBLE
            etNoonDetails.setText(formatTime(noon!!))
        }
        if(evening==""){
            switch14.isChecked = false
            etEveningDetails.visibility = View.GONE
        }else{
            switch14.isChecked = true
            eveningAlarm=evening!!
            etEveningDetails.visibility = View.VISIBLE
            etEveningDetails.setText(formatTime(evening!!))
        }
        if(night==""){
            switch15.isChecked = false
            etNightDetails.visibility = View.GONE
        }else{
            switch15.isChecked = true
            nightAlarm=night!!
            etNightDetails.visibility = View.VISIBLE
            etNightDetails.setText(formatTime(night!!))
        }
    }

    private fun setTime(type : String){
        val cal = Calendar.getInstance()

        if (type=="morning"){
            if (morningAlarm.isNotEmpty()) {
                val timeParts = morningAlarm.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()

                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
            }
            else{
                cal.set(Calendar.HOUR_OF_DAY, 9)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
        }
        if (type=="noon"){
            if (noonAlarm.isNotEmpty()) {
                val timeParts = noonAlarm.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()

                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
            }
            else{
                cal.set(Calendar.HOUR_OF_DAY, 13)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
        }
        if (type=="evening"){
            if (eveningAlarm.isNotEmpty()) {
                val timeParts = eveningAlarm.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()

                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
            }
            else{
                cal.set(Calendar.HOUR_OF_DAY, 17)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
        }
        if (type=="night"){
            if (nightAlarm.isNotEmpty()) {
                val timeParts = nightAlarm.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()

                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
            }
            else{
                cal.set(Calendar.HOUR_OF_DAY, 21)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
            }
        }

        var time : String? = ""
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            Log.e("hour variable", hour.toString())
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            val format = SimpleDateFormat("HH:mm")
            time = format.format(cal.time)
            if(type == "morning" && hour>=6 && hour<12){
                morningAlarm=time!!
                etMorningDetails.setText(formatTime(time!!))
            }else if(type == "noon" && hour>=12 && hour<16){
                noonAlarm=time!!
                etNoonDetails.setText(formatTime(time!!))
            }else if(type == "evening" && hour>=16 && hour<20){
                eveningAlarm=time!!
                etEveningDetails.setText(formatTime(time!!))
            }else if(type == "night" && (hour>=20 || hour<6)){
                nightAlarm=time!!
                etNightDetails.setText(formatTime(time!!))
            }else{
                Toast.makeText(context, "Please maintain the time frame", Toast.LENGTH_LONG).show()
//                if(type == "morning"){
//                    etMorningDetails.setText(formatTime(morningAlarm))
//                }else if(type == "noon"){
//                    etNoonDetails.setText(formatTime(noonAlarm))
//                }else if(type == "evening"){
//                    etEveningDetails.setText(formatTime(eveningAlarm))
//                }else if(type == "night"){
//                    etNightDetails.setText(formatTime(nightAlarm))
//                }
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

    fun updateEndDate(){
        val cal = Calendar.getInstance()
        val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        cal.setTime(simpleDateFormat.parse(startFrom))
        if (numOfDay!!.toInt() in 1..364){
            if (isDaily!!)
                cal.add(Calendar.DATE, numOfDay!!.toInt())
            else{
                cal.add(Calendar.DATE, numOfDay!!.toInt()*(frequency!!+1))
            }

            endDate=simpleDateFormat.format(cal.time).toString()
            tvEndAtDetails.setText(endDate)
            cal.setTime(simpleDateFormat.parse(startFrom))
        }else{
            tvEndAtDetails.setText("Continue")
        }
    }
}
