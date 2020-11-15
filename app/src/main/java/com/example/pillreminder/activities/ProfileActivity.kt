package com.example.pillreminder.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.pillreminder.R
import com.example.pillreminder.fragments.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import java.text.DateFormat
import java.util.*

class ProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    val manager = supportFragmentManager
    var user_id : String? = null
    val fragmentProfileOptions : FragmentProfileOptions = FragmentProfileOptions()
    val fragmentProfileEdit : FragmentProfileEdit = FragmentProfileEdit()
    val fragmentProfileDocumentList : FragmentProfileDocumentList = FragmentProfileDocumentList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = intent.getStringExtra("user_id")
        setContentView(R.layout.activity_profile)
        showProfileOptionsFragment()
    }

    private fun showProfileOptionsFragment(){
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragmentProfileOptions,"FragmentProfileOptions")
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun showProfileEditFragment(){
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.add(R.id.fragment_container_profile,fragmentProfileEdit,"FragmentProfileEdit")
//        transaction.addToBackStack("FragmentProfileEdit")
        transaction.commit()
    }

    fun callDatePicker(){
        val datePicker = DatePickerFragment()
        datePicker.show(supportFragmentManager, "date picker")
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR,year)
        c.set(Calendar.MONTH,month)
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
//        val name: String =  registerName.text.toString()
//        Log.e("date set",name)
        var current = DateFormat.getDateInstance().format(c.getTime())
        Log.e("Date Set activity",current)

//        var date = findViewById<EditText>(R.id.registerDob)
//        editText43.text = Editable.Factory.getInstance().newEditable(current)
    }
}