package com.example.pillreminder.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.pillreminder.adaptors.MedicineInfoAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.helper.Validate
import com.example.pillreminder.models.Pill
import com.example.pillreminder.models.TimePivot
import com.example.pillreminder.service.DataDeleteService
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_add_pill_first.*
import kotlinx.android.synthetic.main.fragment_appointment_details.*
import kotlinx.android.synthetic.main.fragment_pill_details_first.*
import kotlinx.android.synthetic.main.fragment_pill_details_first.btnDuplicate
import kotlinx.android.synthetic.main.fragment_pill_details_first.btnNext
import kotlinx.android.synthetic.main.fragment_pill_details_first.imageView33
import kotlinx.android.synthetic.main.fragment_pill_details_first.spNumOfDayAdd
import kotlinx.android.synthetic.main.fragment_pill_details_first.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class FragmentPillDetailsFirst : DialogFragment(), Validator.ValidationListener {

    private var position: Int? = null
    private var id: Int? = null
    private var table_id: Int? = null
    private var title: String? = null
    private var morning: String? = null
    private var noon: String? = null
    private var evening: String? = null
    private var night: String? = null
    private var isDaily: Boolean? = null
    private var frequency: Int? = 0
    private var periodically: Int? = 0
    private var beforeOrAfterMeal: String? = null
    private var numOfDay: Int? = null
    private var numOfDose: Int? = null
    private var stock: Int? = null
    private var lastPillStatus: String? = null
    private var reminder: Boolean? = null
    private var startFrom: String? = null
    private var task_id_m: Long? = null
    private var task_id_no: Long? = null
    private var task_id_e: Long? = null
    private var task_id_ni: Long? = null
    private var user_id: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var remoteId: Int? = null
    private var lowestStock: Int? = 0
    private var stockReminder: Boolean? = false

    var spNumOfDayAdapter: ArrayAdapter<CharSequence>? = null


    @NotEmpty
    private var medNameET: EditText? = null

    //    @NotEmpty
//    private var noOfDayET: EditText? = null
    @NotEmpty
    private var noOfDoseET: EditText? = null

    @NotEmpty
    private var stockET: EditText? = null
    @NotEmpty
    private var lowestStockET: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
//        dialog.window.attributes.windowAnimations = R.style.dialog_animation
    }

    //
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(getActivity());
        root.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        );

        // creating the fullscreen dialog
        val dialog = Dialog(getActivity()!!, R.style.MyCustomTheme);
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pill_details_first, container)
        return rootView
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
            lowestStock = arguments?.getInt("lowestStock")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView33.setOnClickListener {
            dialog?.dismiss()
        }
//        Log.e("table id default",table_id.toString())
        Log.e("table id from arguments", arguments?.getInt("table_id").toString())
        medNameET = view.etPillNameDetails
//        noOfDayET = view.etNumOfDayDetails
        noOfDoseET = view.etNumOfDoseDetails
        stockET = view.etStockDetails
        lowestStockET = view.etLowestStockDetails

        val validator = Validator(this)
        validator.setValidationListener(this)

        val c = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val currentDate = simpleDateFormat.format(c.time).toString()
        startDate = currentDate
        etStartFromDetails.setText(currentDate)
        Log.i("date start: ", currentDate)

        spNumOfDayAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.number_of_days,
            android.R.layout.simple_spinner_item
        )
        spNumOfDayAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spNumOfDayAdd.adapter = spNumOfDayAdapter

        numberOfDaysListener()

        setInitialData()

        etStartFromDetails.setOnClickListener {
            callDatePicker()
        }

        val medicines = Helper.medicinesList
        Log.i("medicine: ", Helper.medicinesList.size.toString())
        val adapter = MedicineInfoAdapter(context!!, R.layout.medicine_info_row, medicines)
        etPillNameDetails.threshold = 1 //will start working from first character
        etPillNameDetails.setAdapter(adapter)


        etPillNameDetails.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

//        etNumOfDayDetails.addTextChangedListener(object :TextWatcher{
//            override fun afterTextChanged(p0: Editable?) {
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (text!!.isNotEmpty()) {
//                    numOfDay = text.toString().toInt()
//                    updateEndDate()
//                }else{
//                    numOfDay=0
//                    tvEndAt.setText("Select no. of days first")
//                }
//            }
//
//        })

        setSwitchListeners()

        btnNext.setOnClickListener {
            validator.validate()
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

        btnDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this appointment ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                    deletePill()
                } )
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Delete Pill")
            // show alert dialog
            alert.show()
            //deleteAppointment()
        }

    }

    private fun setInitialData() {
        etPillNameDetails.setText(title)
//        etNumOfDayDetails.setText(numOfDay.toString())
        etNumOfDoseDetails.setText(numOfDose.toString())
        etStockDetails.setText(stock.toString())
        swPillReminderDetails.isChecked = reminder!!
        etStartFromDetails.setText(startFrom)

        if (numOfDay == 365) {
            spNumOfDayAdd.setSelection(0)
        } else {
            val numberOfDayPos: Int = spNumOfDayAdapter!!.getPosition(numOfDay.toString())
            spNumOfDayAdd.setSelection(numberOfDayPos)
        }

        if (stockReminder!!){
            swStockReminderDetails.isChecked=stockReminder!!
            etLowestStockDetails.visibility=View.VISIBLE
            etLowestStockDetails.setText(lowestStock!!.toString())
        }

//        updateEndDate()
    }

    private fun numberOfDaysListener() {
        spNumOfDayAdd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val selected: String = spNumOfDayAdd.selectedItem.toString()

                numOfDay = if (selected == "Continue")
                    365
                else
                    selected.toInt()

//                updateEndDate()

                Log.e("pill add", "onItemSelected: $selected")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setSwitchListeners() {
        swPillReminderDetails.setOnCheckedChangeListener { buttonView, isChecked ->
            reminder = isChecked
        }

        swStockReminderDetails.setOnCheckedChangeListener { _, isChecked ->
            stockReminder = isChecked
            if (stockReminder!!){
                etLowestStockDetails.visibility=View.VISIBLE
            }else{
                etLowestStockDetails.visibility=View.GONE
            }
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
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

        if (Validate().isNumeric(etPillNameDetails.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Title can't be numeric",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
//        if (!Validate().isInt(etNumOfDayDetails.text.toString())){
//            Toast.makeText(AccountKitController.getApplicationContext(),"Invalid number",Toast.LENGTH_SHORT).show()
//            return
//        }

        if (!Validate().isInt(etNumOfDoseDetails.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Invalid number",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!Validate().isInt(etStockDetails.text.toString())) {
            Toast.makeText(
                AccountKitController.getApplicationContext(),
                "Invalid number",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (stockReminder!!&&!Validate().isInt(etLowestStockDetails.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Stock reminder can't be empty",Toast.LENGTH_SHORT).show()
            return
        }

        val pop = FragmentPillDetailsSecond()
        val bundle = Bundle()

        bundle.putInt("position", position!!)
        bundle.putInt("id", id!!)
        bundle.putInt("table_id", table_id!!)
        bundle.putString("pillName", etPillNameDetails.text.toString())
        bundle.putString("morning", morning)
        bundle.putString("noon", noon)
        bundle.putString("evening", evening)
        bundle.putString("night", night)
        bundle.putString("beforeOrAfterMeal", beforeOrAfterMeal!!)
        bundle.putBoolean("daily", isDaily!!)
        bundle.putInt("frequency", frequency!!)
        bundle.putInt("noOfDay", numOfDay!!)
        bundle.putInt("noOfDose", etNumOfDoseDetails.text.toString().toInt())
        bundle.putInt("stock", etStockDetails.text.toString().toInt())
        bundle.putBoolean("reminder", swPillReminderDetails.isChecked)
        bundle.putString("lowestStock", etLowestStockDetails.text.toString())
        bundle.putBoolean("stockReminder", stockReminder!!)
        bundle.putString("lastPillStatus", lastPillStatus)
        bundle.putString("startFrom", etStartFromDetails.text.toString())
        bundle.putString("endAt", endDate)
        bundle.putLong("task_id_m", task_id_m!!)
        bundle.putLong("task_id_no", task_id_no!!)
        bundle.putLong("task_id_e", task_id_e!!)
        bundle.putLong("task_id_ni", task_id_ni!!)
        bundle.putString("user_id", user_id!!)
        bundle.putInt("remoteId", remoteId!!)

        pop.arguments = bundle
        val fm = fragmentManager
        pop.show(fm!!, "name")

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

            val newPill = Pill(table_id!!,"Copy of $title",morning!!,noon!!,evening!!,night!!,beforeOrAfterMeal!!,isDaily!!,frequency!!,numOfDay!!,numOfDose!!,stock!!,"N/A",false,startFrom!!,currentDateTime,task_id_m!!+1,task_id_no!!+2,task_id_e!!+3,task_id_ni!!+4,(activity as DashboardActivity).user_id!!,false,lowestStock!!,stockReminder!!)
            val id = database.pillDao().savePill(newPill)

            if (morning!!.isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",morning!!,task_id+1,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved morning pivot")
            }
            if (noon!!.isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",noon!!,task_id+2,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved noon pivot")
            }
            if (evening!!.isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",evening!!,task_id+3,currentDateTime,false)
                database.timePivotDao().saveTime(newTimePivot)
//                Helper.timePivotList.add(newTimePivot)
                Log.i("time pivot: ","saved evening pivot")
            }
            if (night!!.isNotEmpty()){
                val newTimePivot = TimePivot(id.toInt(),(activity as DashboardActivity).user_id!!,"Copy of $title","pill",night!!,task_id+4,currentDateTime,false)
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

    fun callDatePicker() {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)

            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            val current = simpleDateFormat.format(cal.time).toString()

            startDate = current
            etStartFromDetails.setText(current)

//            updateEndDate()
        }

        var datePicker = DatePickerDialog(
            context!!,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePicker.show()
    }

//    fun updateEndDate() {
//        val cal = Calendar.getInstance()
//        val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
//        cal.setTime(simpleDateFormat.parse(etStartFromDetails.text.toString()))
//        if (numOfDay in 1..364) {
//            cal.add(Calendar.DATE, numOfDay!!)
//            endDate = simpleDateFormat.format(cal.time).toString()
//            tvEndAt.setText(endDate)
//            cal.setTime(simpleDateFormat.parse(etStartFromDetails.text.toString()))
//        } else {
//            tvEndAt.setText("Continue")
//        }
//    }
}
