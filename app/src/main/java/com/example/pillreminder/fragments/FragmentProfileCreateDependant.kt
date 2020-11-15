package com.example.pillreminder.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.room.Room.*
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.KeyboardUtils
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Dependant
import com.facebook.accountkit.internal.AccountKitController.getApplicationContext
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_profile_dependant_add.*
import kotlinx.android.synthetic.main.fragment_profile_dependant_add.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import java.text.DateFormat
import java.util.*

class FragmentProfileCreateDependant : DialogFragment() , Validator.ValidationListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        depGenderET = gender[position]
        Log.e("spinner item",depGenderET!!)
    }

    @NotEmpty
    private var depNameET: EditText? = null
    @NotEmpty
    private var depGenderET: String? = null
    @NotEmpty
    private var depDobET: EditText? = null
    @NotEmpty
    private var depRelET: EditText? = null

    lateinit var spinner1 : Spinner
    var gender = arrayOf("Male","Female")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_dependant_add,container,false)
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
        depNameET = view.editText36
//        depGenderET = view.editText14
        depDobET = view.editText38
        depRelET = view.editText39
        imageCross.setOnClickListener {
            dialog?.dismiss()
        }
        btnCreateDependant.setOnClickListener {
            dialog?.dismiss()
        }

        editText38.setOnClickListener {
            Log.e("call Date Picker","Entered first")
            callDatePicker()
        }

        setSpinner()

        val validator = Validator(this)
        validator.setValidationListener(this)
        btnCreateDependant.setOnClickListener {
            validator.validate()
        }

        KeyboardUtils.addKeyboardToggleListener(activity!!, object : KeyboardUtils.SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                Log.e("keyboard", "keyboard visible: $isVisible")

                if (isVisible) {
                    if (scrollViewAddDependant != null)
//                        scrollViewAddDependant.scrollTo(0, scrollViewAddDependant.bottom)
                        scrollViewAddDependant.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        })

    }

    private fun setSpinner() {
        spinner1 = this.spinner
        spinner1!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, gender)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)
    }

    fun callDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)
        Log.e("call Date Picker","Entered")
        val datePicker = DatePickerDialog(context!!,object: DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                var c = Calendar.getInstance()
                c.set(Calendar.YEAR,year)
                c.set(Calendar.MONTH,month)
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                var current = DateFormat.getDateInstance().format(c.getTime())
                Log.e("on Date Set",current)
                editText38.text = Editable.Factory.getInstance().newEditable(current)
            }
        },year,month,date)
        datePicker.show()
//        datePicker.show(fragmentManager!!.beginTransaction(), "date picker")
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR,year)
        c.set(Calendar.MONTH,month)
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
//        val name: String =  registerName.text.toString()
//        Log.e("date set",name)
        var current = DateFormat.getDateInstance().format(c.getTime())
//        var date = findViewById<EditText>(R.id.registerDob)
        Log.e("on Date Set",current)

        editText38.text = Editable.Factory.getInstance().newEditable(current)
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
        val database = databaseBuilder(getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
//        Log.i("appointment","""${finalCal.timeInMillis}""")
//        val task_id = System.currentTimeMillis().toInt()
        val custom = activity as ProfileActivity

        Thread{
            val user_id= SharedPrefHelper(context!!).getInstance().getUserId()

            var dependant = Dependant(user_id!!,editText36.text.toString(),depGenderET!!,editText38.text.toString(),editText39.text.toString(),"")
            val id = database.dependantDao().saveDependant(dependant)
            dependant.id = id
            custom.runOnUiThread {
                (custom.fragmentProfileOptions.fragmentProfileDependantList.dependants as ArrayList).add(dependant)
                custom.fragmentProfileOptions.fragmentProfileDependantList.dependantRecyclerAdapter.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Dependant Created Successfully", Toast.LENGTH_LONG).show();

    }
}