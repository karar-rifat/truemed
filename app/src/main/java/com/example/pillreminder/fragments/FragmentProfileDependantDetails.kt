package com.example.pillreminder.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Dependant
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_appointment_details.*
import kotlinx.android.synthetic.main.fragment_profile_dependant_details.*
import kotlinx.android.synthetic.main.fragment_profile_dependant_details.view.*
import java.text.DateFormat
import java.util.*

class FragmentProfileDependantDetails : DialogFragment(), Validator.ValidationListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener{

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        depGenderET = genderArray[position]
        Log.e("spinner item",depGenderET!!)
    }
    private var position : Int? = null
    private var dep_id : Long? = null
    private var name : String? = null
    private var gender : String? = null
    private var dob : String? = null
    private var relationship : String? = null

    lateinit var spinner1 : Spinner
    var genderArray = arrayOf("Male","Female")

    @NotEmpty
    private var depNameET: EditText? = null
    @NotEmpty
    private var depGenderET: String? = null
    @NotEmpty
    private var depDobET: EditText? = null
    @NotEmpty
    private var depRelET: EditText? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getInt("position")
        dep_id = arguments?.getLong("id")
        name = arguments?.getString("name")
        gender = arguments?.getString("gender")
        if(gender=="Female"){
            genderArray = arrayOf("Female","Male")
        }
        dob = arguments?.getString("dob")
        relationship = arguments?.getString("relationship")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile_dependant_details, container)
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
        setDatasAndValidation(view)
        imageView19.setOnClickListener{
            dialog?.dismiss()
        }
        setSpinner()
        editText38.setOnClickListener {
            callDatePicker()
        }
        val validator = Validator(this)
        validator.setValidationListener(this)

        button17.setOnClickListener {
            validator.validate()
        }
        imageButton12.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this dependant ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        _, _ -> deleteDependant()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, _ -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Confirm Delete")
            // show alert dialog
            alert.show()
            //deleteAppointment()
        }
    }

    private fun deleteDependant() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val user_id=SharedPrefHelper(context!!).getInstance().getUserId()
            val dependant = Dependant(dep_id!!,user_id!!,name!!,gender!!,dob!!,relationship!!,"")
            database.dependantDao().delete(dependant)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as ProfileActivity).runOnUiThread {
                ((activity as ProfileActivity).fragmentProfileOptions.fragmentProfileDependantList.dependants as ArrayList).removeAt(position!!)
                (activity as ProfileActivity).fragmentProfileOptions.fragmentProfileDependantList.dependantRecyclerAdapter.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Dependant Deleted!!", Toast.LENGTH_LONG).show();
    }

    private fun setDatasAndValidation(view: View) {
        depNameET = view.editText36
        depDobET = view.editText38
        depRelET = view.editText39

        editText36.setText(name)
        editText38.setText(dob)
        editText39.setText(relationship)
    }

    private fun setSpinner() {
        spinner1 = this.spinner2
        spinner1!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, genderArray)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner2!!.setAdapter(aa)
    }

    fun callDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)
        Log.e("call Date Picker","Entered")
        val datePicker = DatePickerDialog(context!!,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var c = Calendar.getInstance()
                c.set(Calendar.YEAR,year)
                c.set(Calendar.MONTH,month)
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                var current = DateFormat.getDateInstance().format(c.getTime())
                Log.e("on Date Set",current)
                editText38.text = Editable.Factory.getInstance().newEditable(current)
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
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val user_id=SharedPrefHelper(context!!).getInstance().getUserId()
            val dependant = Dependant(dep_id!!,user_id!!,editText36.text.toString(),depGenderET!!,editText38.text.toString(),editText39.text.toString(),"")
            database.dependantDao().update(dependant)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as ProfileActivity).runOnUiThread {
                ((activity as ProfileActivity).fragmentProfileOptions.fragmentProfileDependantList.dependants as ArrayList).set(position!!,dependant)
                (activity as ProfileActivity).fragmentProfileOptions.fragmentProfileDependantList.dependantRecyclerAdapter.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Dependant Updated Successfully", Toast.LENGTH_LONG).show();

    }
}