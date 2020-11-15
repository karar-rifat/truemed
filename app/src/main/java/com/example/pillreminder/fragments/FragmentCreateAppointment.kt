package com.example.pillreminder.fragments

//import androidx.core.app.Fragment
import android.app.Dialog
import android.app.TimePickerDialog
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
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.adaptors.VaccineTimeRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.Validate
import com.example.pillreminder.models.Appointment
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController.getApplicationContext
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.appointment_add.*
import kotlinx.android.synthetic.main.appointment_add.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class FragmentCreateAppointment : DialogFragment() , Validator.ValidationListener {
    var date: String? = ""
    var dateTime: String? = ""
    val finalCal : Calendar? = Calendar.getInstance()

    lateinit var vacTimeRecyclerAdapter: VaccineTimeRecyclerAdapter
    var times: MutableList<String> = mutableListOf<String>()
    var task_ids: MutableList<Long> = mutableListOf<Long>()
    var status:Boolean=true

    @NotEmpty
    private var appNameET: EditText? = null
    @NotEmpty
    private var appPlaceET: EditText? = null
    @NotEmpty
    private var appMobileET: EditText? = null
//    @NotEmpty
    private var appTimeET: EditText? = null
//    @NotEmpty
    private var appNoteET: EditText? = null

    fun delete(position:Int){
        Log.e("del clicked",times[position])
        times.removeAt(position)
        vacTimeRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.appointment_add,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appNameET = view.editText
        appPlaceET = view.editText2
        appMobileET = view.editText3
        appTimeET = view.etAppoTimeAdd
        appNoteET = view.editText7

        val validator = Validator(this)
        validator.setValidationListener(this)

        etAppoTimeAdd.setOnClickListener {
            (activity as DashboardActivity).callDatePicker("appointment")
            setTime("time")

        }

        button2.setOnClickListener {
//            Log.i("appointment","""1557370200097 -- ${finalCal.timeInMillis}""")

//            Toast.makeText(context, switchReminder.isChecked.toString(), Toast.LENGTH_LONG).show()
            validator.validate()
        }

        imageButton13.setOnClickListener {
            dialog?.dismiss()
        }

        swRemAddApp.setOnCheckedChangeListener{ _, isChecked ->
            status=isChecked
        }

        val simpleDateFormat  = SimpleDateFormat("MMM dd,yyyy  HH:mm")
        val currentDate: Date = simpleDateFormat.parse(simpleDateFormat.format(Date()))
        imageButtonP3.setOnClickListener {
            if(etAppoTimeAdd.text.toString().equals("")){
                Toast.makeText(context, "Select date & time first!", Toast.LENGTH_LONG).show();
            }else{
                //compare selected time with current time
                val selectedDate: Date = simpleDateFormat.parse(dateTime)
                if (selectedDate.compareTo(currentDate) > 0){
                    times.add(dateTime!!)
                    etAppoTimeAdd.setText("")
                }
                else
                    Toast.makeText(context,"Previous time can't be selected!",Toast.LENGTH_SHORT).show()
            }
            vacTimeRecyclerAdapter.notifyDataSetChanged()
        }

        recyclerAppTime.setHasFixedSize(true)
        recyclerAppTime.layoutManager = LinearLayoutManager(context)
        vacTimeRecyclerAdapter = VaccineTimeRecyclerAdapter(context!!, times!!,::delete)
        System.out.println(vacTimeRecyclerAdapter)
        recyclerAppTime.adapter = vacTimeRecyclerAdapter

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

        if (Validate().isNumeric(editText.text.toString())){
            Toast.makeText(getApplicationContext(),"Title can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }
        if (Validate().isNumeric(editText2.text.toString())){
            Toast.makeText(getApplicationContext(),"Place can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validate().validatePhoneNumber(editText3.text.toString())){
            Toast.makeText(getApplicationContext(),"Invalid phone number",Toast.LENGTH_SHORT).show()
            return
        }
        if (Validate().isNumeric(editText7.text.toString())){
            Toast.makeText(getApplicationContext(),"Note can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }

        if (times.size==0||etAppoTimeAdd.text.isNotEmpty()){
            Toast.makeText(getApplicationContext(),"Please add time (click +)",Toast.LENGTH_SHORT).show()
            return
        }


        val database = Room.databaseBuilder(getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        val custom = activity as DashboardActivity
        times.forEachIndexed() { index, it ->
            val randomInteger = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ThreadLocalRandom.current().nextInt(10000, 100000)
            } else {
                index*1000
            }
            Log.e("random generator", randomInteger.toString())
            val task_id = System.currentTimeMillis()+randomInteger
            task_ids.add(task_id)
        }
        val task_id = System.currentTimeMillis()
        Thread{

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newAppo = Appointment(editText.text.toString(),editText2.text.toString(),editText3.text.toString(),dateTime!!,status,editText7.text.toString(),task_id,custom.user_id!!,currentDateTime,false)

            val id = database.appointmentDao().saveAppointment(newAppo).toInt()
            newAppo.localId = id
            times.forEachIndexed { index, s ->
                val newTimePivot = TimePivot(id,(activity as DashboardActivity).user_id!!,editText.text.toString(),"appointment",s,task_ids[index],currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
            }
            custom.runOnUiThread {
//                var sdf  = SimpleDateFormat("MMM dd,yyyy  HH:mm")
                var sdf  = SimpleDateFormat("MMM dd,yyyy  HH:mm")
                times.forEachIndexed { index, s ->
                    val cal: Calendar = Calendar.getInstance()
                    val date: Date = sdf.parse(s)
                    val currentDate: Date = sdf.parse(sdf.format(Date()))
                    cal.setTime(date)
                    Log.e("print date", "date $s")
                    System.out.println(cal)

                    //check if the date is previous before starting alarm
                    if (date.compareTo(currentDate) > 0) {
                        if (status)
                            (activity as DashboardActivity).startAlarm(
                                cal!!,
                                "once",
                                id.toInt(),
                                "appointment",
                                true,
                                0,
                                editText.text.toString(),
                                s,
                                task_ids[index]
                            )
                    }
                }

                //update data to server with background service
                if (Helper.isConnected(context!!)) {
                    val token=Helper.token
                    Log.e("add appointment", "starting push service appointment")
                    val service = Intent(context, DataPushService::class.java)
                    service.putExtra("type", "appointment")
                    service.putExtra("token", token)
                    context!!.startService(service)
                }else{
                    AlarmHelper().startAlarmForSync(context!!,"appointment")
                    Log.e("add appointment ", "alarm for sync appointment")
                }

//                custom.startAlarm(finalCal!!,"once", id, "Appointment", editText.text.toString(), editText4.text.toString(),task_id)
//                (custom.fragmentAppointmentList.appointments as ArrayList).add(newAppo)
//                custom.fragmentAppointmentList.appointmentRecyclerAdapter?.notifyDataSetChanged()

                database.close()

                //local broadcast for update dashboard
                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Appointment Created Successfully", Toast.LENGTH_LONG).show();

    }


    private fun setTime(type : String){
//        val date = editText4.text.toString()
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)

            val sdf = SimpleDateFormat("HH:mm")
            if(type == "time"){
                if(date==""){
                    Toast.makeText(context, "Select date first!", Toast.LENGTH_LONG).show();
                }else {
                    finalCal?.set(Calendar.HOUR_OF_DAY, hour)
                    finalCal?.set(Calendar.MINUTE, minute)
                    finalCal?.set(Calendar.SECOND, 0)
                    dateTime=date + "  " +sdf.format(cal.time)
                    etAppoTimeAdd.setText(date + "  " +formatTime(sdf.format(cal.time)))
                    Log.e("add appo", "setTime: $dateTime" )
//                    Log.i("appointment","""${cal.timeInMillis} -- ${DateFormat.getDateInstance().format(finalCal.getTime())}""")
                }
            }else if(type == "timeIn"){
                etAppoTimeAdd.setText(formatTime(sdf.format(cal.time)))
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
