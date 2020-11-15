package com.example.pillreminder.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.adaptors.VaccineTimeRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.*
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.models.Vaccine
import com.example.pillreminder.service.DataDeleteService
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_vaccine_details.*
import kotlinx.android.synthetic.main.fragment_vaccine_details.view.*
import kotlinx.android.synthetic.main.vaccine_add.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class FragmentVaccineDetails : DialogFragment() , Validator.ValidationListener{
    var dateTime: String? = ""

    private var position : Int? = null
    private var id : Int? = null
    private var title : String? = null
    private var place : String? = null
    private var mobile : String? = null
    private var time : String? = null
    private var reminder : Boolean? = null
    private var note : String? = null
    private var task_id : Long? = null
    private var user_id : String? = null
    private var remoteId: Int? = null

    var times: MutableList<String> = mutableListOf<String>()
    var newTask_ids: MutableList<Long> = mutableListOf<Long>()
    var oldTask_ids: MutableList<Long> = mutableListOf<Long>()
    lateinit var vacTimeRecyclerAdapter: VaccineTimeRecyclerAdapter

    @NotEmpty
    private var vNameET: EditText? = null
    @NotEmpty
    private var vPlaceET: EditText? = null
    @NotEmpty
    private var vMobileET: EditText? = null
//    @NotEmpty
    private var vTimeET: EditText? = null
//    @NotEmpty
    private var vNoteET: EditText? = null

    fun delete(position: Int) {
        Log.e("del clicked", times[position])
        times.removeAt(position)
        vacTimeRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getInt("position")
        id = arguments?.getInt("id")
        title = arguments?.getString("title")
        place = arguments?.getString("place")
        mobile = arguments?.getString("mobile")
        time = arguments?.getString("time")
        reminder = arguments?.getBoolean("reminder")
        note = arguments?.getString("note")
        task_id = arguments?.getLong("task_id")
        user_id = arguments?.getString("user_id")
        remoteId = arguments?.getInt("remoteId",0)
//        Log.e("id from dialog Attach", id.toString())
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val data = database.timePivotDao().getData(title!!, "vaccine")
            data.forEach {
                times.add(it.time!!)
                oldTask_ids.add(it.taskId!!)
            }
        }.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_vaccine_details, container)
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
        // return  AlertDialog.Builder( (activity as DashboardActivity), R.style.FullScreenDialogStyle ).setView(R.layout.fragment_add_pill_first).create()
//        val context = ContextThemeWrapper(activity, R.style.FullScreenDialogStyle)
//        val builder = AlertDialog.Builder(context)
//        return builder.create()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vNameET = view.editText30
        vPlaceET = view.editText31
        vMobileET = view.editText32
        vTimeET = view.etTimeVacDetails
        vNoteET = view.editText35

        editText30.setText(title)
        editText31.setText(place)
        editText32.setText(mobile)
//        editText33.setText(time)
        swRemUpdateVac.isChecked = reminder!!
        editText35.setText(note)

        swRemUpdateVac.setOnCheckedChangeListener { buttonView, isChecked ->
            reminder = isChecked
        }

        val validator = Validator(this)
        validator.setValidationListener(this)

        etTimeVacDetails.setOnClickListener {
            (activity as DashboardActivity).callDatePicker("vacDetails")
//            callDatePicker()
            setTime("time")
        }

        button16.setOnClickListener {
            validator.validate()
        }

        btnDuplicate.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to duplicate this vaccine ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                    duplicate()
                } )
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Duplicate Vaccine")
            // show alert dialog
            alert.show()
            //duplicateAppointment()
        }

        imageButton9.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this vaccine ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        _, _ -> deleteVaccine()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, _ -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Delete Vaccine")
            // show alert dialog
            alert.show()
//            deleteVaccine()
        }

        imageView18.setOnClickListener{
            dialog?.dismiss()
        }

        val simpleDateFormat  = SimpleDateFormat("MMM dd,yyyy  HH:mm")
        val currentDate: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))

        imageButtonP2.setOnClickListener {
            if(etTimeVacDetails.text.toString().equals("")){
                Toast.makeText(context, "Select date & time first!", Toast.LENGTH_LONG).show();
            }else{
                //compare selected time with current time
                val selectedDate: Date = simpleDateFormat.parse(time)
                if (selectedDate.compareTo(currentDate) > 0){
                    times.add(time!!)
                    etTimeVacDetails.setText("")
                }
                else
                    Toast.makeText(context,"Previous time can't be selected!",Toast.LENGTH_SHORT).show()
            }
            vacTimeRecyclerAdapter.notifyDataSetChanged()
        }

        recyclerVaccineTime2.setHasFixedSize(true)
        recyclerVaccineTime2.layoutManager = LinearLayoutManager(context)
        vacTimeRecyclerAdapter = VaccineTimeRecyclerAdapter(context!!, times!!,::delete)

        System.out.println(vacTimeRecyclerAdapter)
        recyclerVaccineTime2.adapter = vacTimeRecyclerAdapter


    }

    private fun deleteVaccine() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                Log.e("vaccine delete ", "starting delete service vaccine $remoteId")
                val service = Intent(context, DataDeleteService::class.java)
                service.putExtra("type", "vaccine")
                service.putExtra("title", title)
                service.putExtra("remoteId", remoteId!!)
                context!!.startService(service)

            }else{
                AlarmHelper().startAlarmForSync(context!!,"vaccine")
                Log.e("vaccine delete ", "alarm for sync vaccine")
            }

            val newVaccine = Vaccine(id!!,remoteId!!,title!!,place!!,mobile!!,time!!,reminder!!,note!!,task_id!!,user_id!!,currentDateTime,false)
            database.vaccineDao().delete(newVaccine)

//            //delete timePivot for vaccine
//            val timePivot=database.timePivotDao().getData(title!!,time!!,"vaccine")
//            timePivot.forEach {
//                database.timePivotDao().deleteRows(it.localId,"vaccine")
//                Log.e("delete vaccine","deleted timePivot row ${it.idToMatch}")
//            }


//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentVaccineList.vaccines as ArrayList).removeAt(position!!)
                (activity as DashboardActivity).fragmentVaccineList.vaccineRecyclerAdapter?.notifyDataSetChanged()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Vaccine Deleted!!", Toast.LENGTH_LONG).show();
        database.close()
    }

    fun duplicate() {

        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newVaccine = Vaccine("Copy of $title",place!!,mobile!!,time!!,false,note!!,task_id!!,(activity as DashboardActivity).user_id!!,currentDateTime,false)
            val id = database.vaccineDao().saveVaccine(newVaccine)
            newVaccine.localId = id.toInt()

            times.forEachIndexed { index, s ->
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","vaccine",s,oldTask_ids[index],currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
            }

            (activity as DashboardActivity).runOnUiThread {

                //update data to server with background service
                if (Helper.isConnected(context!!)) {
                    val token=Helper.token
                    Log.e("add vaccine", "starting push service vaccine")
                    val service = Intent(context, DataPushService::class.java)
                    service.putExtra("type", "vaccine")
                    service.putExtra("token", token)
                    context!!.startService(service)
                }else{
                    AlarmHelper().startAlarmForSync(context!!,"vaccine")
                    Log.e("add vaccine ", "alarm for sync vaccine")
                }

                ((activity as DashboardActivity).fragmentVaccineList.vaccines as ArrayList).add(newVaccine)
                (activity as DashboardActivity).fragmentVaccineList.vaccineRecyclerAdapter?.notifyDataSetChanged()
                database.close()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Vaccine Duplicated Successfully", Toast.LENGTH_LONG).show();

    }

    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        for (error in errors) {
            val view = error.getView()
            val message = error.getCollatedErrorMessage(context)

            // Display error messages ;)
            if (view is EditText) {
                (view as EditText).error = message
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onValidationSucceeded() {

        if (Validate().isNumeric(editText30.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Title can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }
        if (Validate().isNumeric(editText31.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Place can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validate().validatePhoneNumber(editText32.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Invalid phone number",Toast.LENGTH_SHORT).show()
            return
        }
        if (Validate().isNumeric(editText35.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Note can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }

        if (times.size==0||etTimeVacDetails.text.isNotEmpty()){
            Toast.makeText(AccountKitController.getApplicationContext(),"Please add time and click +",Toast.LENGTH_SHORT).show()
            return
        }

        //update data to server with background service
        if (Helper.isConnected(context!!)) {
            Log.e("vaccine time delete", "starting delete service vaccine")
            val service = Intent(context, DataDeleteService::class.java)
            service.putExtra("type", "timePivot")
            service.putExtra("title", title)
            context!!.startService(service)
        }else{
            AlarmHelper().startAlarmForSync(context!!,"vaccine")
            Log.e("vaccine delete ", "alarm for sync vaccine")
        }

        oldTask_ids.forEachIndexed { index, i ->
            Log.e("vaccine details:", "cancelling alarm")
            (activity as DashboardActivity).cancelAlarm(oldTask_ids[index])
        }

        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        times.forEachIndexed { index, it ->
            val randomInteger = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ThreadLocalRandom.current().nextInt(10000, 100000)
            } else {
                index*1000
            }
            Log.e("random generator", randomInteger.toString())
            var task = System.currentTimeMillis()
            if(task<0)
                task = task*(-1)+randomInteger
            else
                task += randomInteger

            newTask_ids.add(task)
        }
        println(newTask_ids)
        Thread{

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newVaccine = Vaccine(id!!,remoteId!!,editText30.text.toString(),editText31.text.toString(),editText32.text.toString(),time!!,reminder!!,editText35.text.toString(),task_id!!,user_id!!,currentDateTime,false)
            database.vaccineDao().update(newVaccine)
            database.timePivotDao().deleteRows(id!!,"vaccine")
            var sdf  = SimpleDateFormat("MMM dd,yyyy  HH:mm")

            times.forEachIndexed { index, s ->
                val newTimePivot = TimePivot(id!!,(activity as DashboardActivity).user_id!!,editText30.text.toString(),"vaccine",s,newTask_ids[index],currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)


                val cal : Calendar = Calendar.getInstance()
                val date : Date = sdf.parse(s)
                val currentDate: Date = sdf.parse(sdf.format(Date()))
                cal.setTime(date)
                Log.e("print date","date $s")
                System.out.println(cal)

                //check if the date is previous before starting alarm
                if (date.compareTo(currentDate) > 0) {
                    if (reminder!!)
                    (activity as DashboardActivity).startAlarm(
                        cal!!,
                        "once",
                        id!!,
                        "vaccine",
                        true,
                        0,
                        editText30.text.toString(),
                        s,
                        newTask_ids[index]
                    )
                }
            }

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                val token=Helper.token
                Log.e("details vaccine", "starting push service vaccine")
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", "vaccine")
                service.putExtra("token", token)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"vaccine")
                Log.e("details vaccine ", "alarm for sync vaccine")
            }

            println(newVaccine)
            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentVaccineList.vaccines as ArrayList).set(position!!,newVaccine)
                (activity as DashboardActivity).fragmentVaccineList.vaccineRecyclerAdapter?.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Vaccine Updated Successfully", Toast.LENGTH_LONG).show()

    }

    fun callDatePicker(){
        val datePicker = DatePickerFragment()
//        datePi
        val date = datePicker.show(fragmentManager!!, "date picker")
        Log.e("date",date.toString())
    }

    private fun setTime(type : String){
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            val sdf = SimpleDateFormat("HH:mm")
            if(type == "time"){
                if((activity as DashboardActivity).temp==""){
                    etTimeVacDetails.setText("")
                    Toast.makeText(context, "Select date first!", Toast.LENGTH_LONG).show();
                }else {
                    time=(activity as DashboardActivity).temp + "  " + sdf.format(cal.time)
                    etTimeVacDetails.setText((activity as DashboardActivity).temp + "  " + formatTime(sdf.format(cal.time)))
                    (activity as DashboardActivity).temp = ""
                }
            }else if(type == "timeIn"){
                etTimeVacDetails.setText(sdf.format(cal.time))
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
}