package com.example.pillreminder.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
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
import androidx.fragment.app.DialogFragment
import com.example.pillreminder.R
import com.example.pillreminder.adaptors.MedicineInfoAdapter
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.Validate
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_add_pill_first.*
import kotlinx.android.synthetic.main.fragment_add_pill_first.view.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentAddPillFirst : DialogFragment() , Validator.ValidationListener {

    private var table_id : Int? = null
    private var noOfDays : Int = 0
    private var reminder : Boolean = true
    private var stockReminder : Boolean = false

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
        val rootView = inflater.inflate(R.layout.fragment_add_pill_first, container)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        table_id = arguments?.getInt("table_id")
        Log.e("table id in on attach",table_id.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView33.setOnClickListener{
            dialog?.dismiss()
        }
//        Log.e("table id default",table_id.toString())
        Log.e("table id from arguments",arguments?.getInt("table_id").toString())
        medNameET = view.etPillNameAdd
//        noOfDayET = view.etNumOfDayAdd
        noOfDoseET = view.etNumOfDoseAdd
        stockET = view.etStockAdd
        lowestStockET = view.etLowestStock

        val validator = Validator(this)
        validator.setValidationListener(this)

        val c = Calendar.getInstance()
        val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val currentDate=simpleDateFormat.format(c.time).toString()
        startDate=currentDate
        etStartFrom.setText(currentDate)
        Log.i("date start: ",currentDate)

        val spDaysAdapter = ArrayAdapter.createFromResource(context!!,R.array.number_of_days, android.R.layout.simple_spinner_item)
        spDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spNumOfDayAdd.adapter=spDaysAdapter

        numberOfDaysListener()

        etStartFrom.setOnClickListener {
            callDatePicker()
        }

        val medicines=Helper.medicinesList
        Log.i("medicine: ",Helper.medicinesList.size.toString())
       val adapter = MedicineInfoAdapter(context!!,R.layout.medicine_info_row, medicines)
        etPillNameAdd.threshold = 1 //will start working from first character
        etPillNameAdd.setAdapter(adapter)


        etPillNameAdd.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

//        etNumOfDayAdd.addTextChangedListener(object :TextWatcher{
//            override fun afterTextChanged(p0: Editable?) {
//            }
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (text!!.isNotEmpty()) {
//                    noOfDays = text.toString().toInt()
//                    updateEndDate()
//                }else{
//                    noOfDays=0
//                    tvEndAt.setText("Select no. of days first")
//                }
//            }
//        })

        setSwitchListeners()
        btnNext.setOnClickListener {
            validator.validate()
        }

    }

    private fun setSwitchListeners() {
        swPillAddReminder.setOnCheckedChangeListener { _, isChecked ->
            reminder = isChecked
        }

        swStockReminder.setOnCheckedChangeListener { _, isChecked ->
            stockReminder = isChecked
            if (stockReminder){
                etLowestStock.visibility=View.VISIBLE
            }else{
                etLowestStock.visibility=View.GONE
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

    private fun numberOfDaysListener() {
        spNumOfDayAdd.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val selected: String =spNumOfDayAdd.selectedItem.toString()

                noOfDays = if (selected=="Continue")
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

    override fun onValidationSucceeded() {

        if (Validate().isNumeric(etPillNameAdd.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Title can't be numeric",Toast.LENGTH_SHORT).show()
            return
        }
//        if (!Validate().isInt(etNumOfDayAdd.text.toString())){
//            Toast.makeText(AccountKitController.getApplicationContext(),"Invalid number",Toast.LENGTH_SHORT).show()
//            return
//        }

        if (!Validate().isInt(etNumOfDoseAdd.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Invalid number",Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validate().isInt(etStockAdd.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Invalid number",Toast.LENGTH_SHORT).show()
            return
        }

        if (stockReminder&&!Validate().isInt(etLowestStock.text.toString())){
            Toast.makeText(AccountKitController.getApplicationContext(),"Invalid Lowest Stock",Toast.LENGTH_SHORT).show()
            return
        }

        val pop = FragmentAddPillSecond()
        val bundle = Bundle()
        bundle.putInt("tableId", table_id!!)
        bundle.putString("pillName", etPillNameAdd.text.toString())
        bundle.putInt("noOfDay", noOfDays)
        bundle.putInt("noOfDose", etNumOfDoseAdd.text.toString().toInt())
        bundle.putInt("stock", etStockAdd.text.toString().toInt())
        bundle.putBoolean("reminder", swPillAddReminder.isChecked)
        bundle.putString("lowestStock", etLowestStock.text.toString())
        bundle.putBoolean("stockReminder", swStockReminder.isChecked)
        bundle.putString("startFrom", etStartFrom.text.toString())
        bundle.putString("endAt", endDate)
        pop.arguments = bundle
        val fm = fragmentManager
        pop.show(fm!!, "name")

    }

    fun callDatePicker(){
        val cal = Calendar.getInstance()
        val dateSetListener=DatePickerDialog.OnDateSetListener{datePicker, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)

            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            val current=simpleDateFormat.format(cal.time).toString()

            startDate=current
            etStartFrom.setText(current)

//            updateEndDate()
        }

        var datePicker = DatePickerDialog(context!!, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        datePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePicker.show()

    }

//    fun updateEndDate(){
//        val cal = Calendar.getInstance()
//        val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
//        cal.setTime(simpleDateFormat.parse(etStartFrom.text.toString()))
//        if (noOfDays in 1..364) {
//            cal.add(Calendar.DATE, noOfDays)
//            endDate = simpleDateFormat.format(cal.time).toString()
//            tvEndAt.setText(endDate)
//            cal.setTime(simpleDateFormat.parse(etStartFrom.text.toString()))
//        }else{
//            tvEndAt.setText("Continue")
//        }
//    }
}
