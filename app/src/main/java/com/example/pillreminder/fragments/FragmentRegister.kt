/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.pillreminder.fragments

import android.content.Intent
import android.os.Bundle
//import android.support.design.widget.TextInputEditText
//import androidx.core.app.DialogFragment
//import androidx.core.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.pillreminder.MainActivity
import com.example.pillreminder.R
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.activities.TutorialActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Settings
import com.example.pillreminder.models.User
import com.example.pillreminder.network.repository.LoginRepo
import com.example.pillreminder.network.repository.SignUpRepo
import com.example.pillreminder.network.request.LoginRequest
import com.example.pillreminder.network.request.RegisterRequest
import com.mobsandgeeks.saripaar.annotation.Email
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Password

//facebook account kit
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.internal.AccountKitController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class FragmentRegister : Fragment() , Validator.ValidationListener {

    companion object {
        val APP_REQUEST_CODE : Int = 99;
    }

    var user_id:String?=null

    @NotEmpty
    private var nameEditText: TextInputEditText? = null

    @NotEmpty
    @Email
    private var emailEditText: TextInputEditText? = null

    @Password
    private var passwordEditText: TextInputEditText? = null

//    @NotEmpty
//    private var dobEditText: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("register fragment","entered on view created")
//        validator = new Validator(this);
//        validator.setValidationListener(this);

        nameEditText = view.findViewById(R.id.registerName)
        emailEditText = view.findViewById(R.id.registerEmail)
        passwordEditText = view.findViewById(R.id.registerPassword)
//        dobEditText = view.findViewById(R.id.registerDob)

        //checking signup
//        SignUpRepo().signUp(SignUpRequest("amatik","atik","aa@aaqcvcv.com","1234","123456","1/1/1","user"))

        val validator = Validator(this);
        validator.setValidationListener(this);

        btnSignup.setOnClickListener {
//            (activity as MainActivity).finish()
//            val intent = Intent(activity, TutorialActivity::class.java)
//            intent.putExtra("user_id",2)
//            startActivity(intent)
            var pass: String =  passwordEditText?.text.toString()
            Log.e("val",pass)
            validator.validate();
        }
        signupBack.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStackImmediate()
        }
//        registerDob.setOnClickListener {
//            Log.e("register fragment","on click dob")
//            (activity as MainActivity).callDatePicker()
////            val datePicker = DatePickerFragment()
////            datePicker.show(fragmentManager,"date picker")
//        }

    }

    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        for (error in errors) {
            val view = error.getView()
            val message = error.getCollatedErrorMessage(context)

            // Display error messages ;)
            if (view is TextInputEditText) {
                (view as EditText).error = message
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onValidationSucceeded() {
        //Toast.makeText(context, "Signed Up successfully", Toast.LENGTH_LONG).show();
//        phoneLogin(view!!)
        saveInDb()
    }

    fun phoneLogin(view: View) {
        login(LoginType.PHONE);
    }

    fun login(loginType: LoginType){
        val intent = Intent(activity,AccountKitActivity::class.java)
        val configurationBuilder =
                    AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            var loginResult = data?.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            var toastMessage: String? = null
            var cancelCheck : Boolean? = loginResult?.wasCancelled()
            if (loginResult?.error != null) {
                toastMessage = loginResult?.error?.errorType?.message
//                showErrorActivity(loginResult.error);
            } else if (cancelCheck==true) {
                toastMessage = "Login Cancelled";
                Toast.makeText(context,toastMessage,Toast.LENGTH_LONG).show();

            } else {
                if (loginResult?.getAccessToken() != null) {
                    saveInDb()
                } else {
                    toastMessage = String.format(
                        "Success:%s...",
                        loginResult?.getAuthorizationCode()?.substring(0,10));
                }
            }
        }
    }

    private fun saveInDb() {
            GlobalScope.launch(Dispatchers.IO) {
                val userRequest= RegisterRequest(emailEditText!!.text.toString(),nameEditText!!.text.toString(),emailEditText!!.text.toString(),passwordEditText!!.text.toString(),"api")
//                    SignUpRepo().signUp(newUser)
                val isSuccess=SignUpRepo().signUp(userRequest,::signUpHandler)
                Log.e("Checking registration", "success "+Helper.user_id)
            }
    }

    fun signUpHandler(isSuccess:Boolean){

        if (isSuccess) {
            user_id = emailEditText!!.text.toString()
            val password = passwordEditText!!.text.toString()
            Helper.user_id = emailEditText!!.text.toString()

            Log.e("Check register result", "handler "+Helper.user_id)

            GlobalScope.launch(Dispatchers.IO) {
                LoginRepo().login(LoginRequest(user_id!!, password), ::loginHandler)
            }

            }else {
                Log.e("Checking registration", "failed " + Helper.user_id)
                (activity as MainActivity).runOnUiThread {
                    Toast.makeText(context, "Registration Failed!, please check your details.", Toast.LENGTH_LONG).show();
                }
            }
    }

    private fun loginHandler(tokenOrMessage:String, error:Boolean){
            if (!error){
                val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
                //get current datetime
                val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
                val currentDateTime=simpleDateFormat.format(Date()).toString()

                SessionManager(context!!).saveAuthToken(tokenOrMessage)
                Helper.token=tokenOrMessage
                SharedPrefHelper((activity as MainActivity).applicationContext).getInstance().loginUser(user_id!!,passwordEditText!!.text.toString())
                Log.e("login token"," token -  $tokenOrMessage")
//
                Thread {
                    Log.e("Check register result", "success " + Helper.user_id)
                    database.userDao().clearUser()
                    val users = database.userDao().checkIfExist(user_id!!)
//            Log.i("size",users.size.toString())
                    if (users.isEmpty()) {
                        val newUser = User(
                            nameEditText!!.text.toString(),
                            emailEditText!!.text.toString(),
                            passwordEditText!!.text.toString(),
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            currentDateTime,
                            false
                        )
                        database.settingsDao().settings().forEach {
                            Log.e("settings=====>", """ Title - ${it.title}""")
                        }

                        val id = database.userDao().saveUser(newUser).toInt()
                        val alarm = database.settingsDao().getSpecificSettings("alarm")
                        val notification_message =
                            database.settingsDao().getSpecificSettings("notification_message")
                        val snooze_time = database.settingsDao().getSpecificSettings("snooze_time")
                        if (alarm == null) {
                            val forAlarm = Settings("alarm", "", true, user_id!!)
                            database.settingsDao().saveSettings(forAlarm)
                            Log.e("Checking alarm tune", "null")
                        } else if (alarm != null) {
                            Log.e("Checking alarm tune", "not null")
                        }
                        if (notification_message == null) {
                            val forNotification = Settings("notification_message", "", true, user_id!!)
                            database.settingsDao().saveSettings(forNotification)
                            Log.e("Checking notification", "null")
                        } else if (notification_message != null) {
                            Log.e("Checking notification", "not null")
                        }
                        if (snooze_time == null) {
                            val forSnooze = Settings("snooze_time", "0", false, user_id!!)
                            database.settingsDao().saveSettings(forSnooze)
                            Log.e("Checking snooze", "null")
                        } else if (snooze_time != null) {
                            Log.e("Checking snooze", "not null")
                        }


                        (activity as MainActivity).runOnUiThread {
                            Log.e("Checking success", "success " + Helper.user_id)
                            Toast.makeText(context, "Successfully Registered!!", Toast.LENGTH_LONG)
                                .show();
                            val intent = Intent(activity, TutorialActivity::class.java)
                            intent.putExtra("user_id", Helper.user_id)
                            database.close()
                            startActivity(intent)
                            (activity as MainActivity).finish()
                        }
                    } else {
                        Log.e("Checking user", "Exists " + Helper.user_id)
                        (activity as MainActivity).runOnUiThread {
//                            Toast.makeText(context, "Email Already Exists!!", Toast.LENGTH_LONG).show();
                            Log.e("Checking success", "success " + Helper.user_id)
                            Toast.makeText(context, "Successfully Registered!!", Toast.LENGTH_LONG)
                                .show();
                            val intent = Intent(activity, TutorialActivity::class.java)
                            intent.putExtra("user_id", Helper.user_id)
                            database.close()
                            startActivity(intent)
                            (activity as MainActivity).finish()
                        }
                    }
                }.start()
            }
    }

//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//        Log.e("register fragment","on click dob")
//
//        var c = Calendar.getInstance()
//        c.set(Calendar.YEAR,year)
//        c.set(Calendar.MONTH,month)
//        c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
////        val name: String =  registerName.text.toString()
////        Log.e("date set",name)
//        var current = DateFormat.getDateInstance().format(c.getTime())
////        var date = findViewById<EditText>(R.id.registerDob)
//        registerDob.text = Editable.Factory.getInstance().newEditable(current)
//    }

}



