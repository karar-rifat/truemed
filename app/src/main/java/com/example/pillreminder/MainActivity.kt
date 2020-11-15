package com.example.pillreminder

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.activities.PassCodeActivity
import com.example.pillreminder.fragments.DatePickerFragment
import com.example.pillreminder.fragments.FragmentLogin
import com.example.pillreminder.helper.SharedPrefHelper
import kotlinx.android.synthetic.main.fragment_register.*
import java.text.DateFormat
import java.util.*


class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    val manager = supportFragmentManager
    private var sharedPrefHelper: SharedPrefHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //fullscreen with status bar
        val mWindow = window
        mWindow.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        //white status bar background and black text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

        sharedPrefHelper = SharedPrefHelper(this)

        if (sharedPrefHelper!!.getInstance().isLoggedIn()) {
            val userId = sharedPrefHelper!!.getInstance().getUserId()
            val passCode=SharedPrefHelper(this).getInstance().getPassCode()
            if (passCode.isNullOrEmpty()) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("from_session", true)
                startActivity(intent)
                this.finish()
            }else{
                val intent = Intent(this, PassCodeActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("from_session", true)
                startActivity(intent)
                this.finish()
            }

//            showLoginFragment()
        } else
            showLoginFragment()

//        showRegisterFragment()
//        registerDob.setOnClickListener {
//            val datePicker = DatePickerFragment()
//            datePicker.show(supportFragmentManager, "date picker")
//        }
//        btnSignup.setOnClickListener {
//            Log.e("main activity","pressed signup")
//            val intent = Intent(this,ViewPostsActivity::class.java)
//            startActivity(intent)
//        }
    }

    //    private fun showRegisterFragment(){
//        val transaction = manager.beginTransaction()
//        val fragment = FragmentRegister()
//        transaction.replace(R.id.fragment_holder,fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }
     fun showLoginFragment() {
        val transaction = manager.beginTransaction()
        val fragment = FragmentLogin()
        transaction.replace(R.id.fragment_holder, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    fun callDatePicker() {
        val datePicker = DatePickerFragment()
        datePicker.show(supportFragmentManager, "date picker")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//        val name: String =  registerName.text.toString()
//        Log.e("date set",name)
        var current = DateFormat.getDateInstance().format(c.getTime())
//        var date = findViewById<EditText>(R.id.registerDob)
//        registerDob.text = Editable.Factory.getInstance().newEditable(current)
    }
}

