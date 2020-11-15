package com.example.pillreminder.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pillreminder.MainActivity
import com.example.pillreminder.R
import com.example.pillreminder.network.request.ForgotPasswordRequest
import com.example.pillreminder.network.request.ResetPasswordRequest
import com.example.pillreminder.network.repository.ForgotPasswordRepo
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Email
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentForgotPassword : Fragment(), Validator.ValidationListener{

    private var flag:String="email"
    var email:String?=null
    lateinit var progressDialog:ProgressDialog

    @NotEmpty
    @Email
    private var etEmail: EditText? = null

//    @NotEmpty
//    private var etCode: EditText? = null
//
//    @NotEmpty
//    @Length(min = 6, max = 20)
//    private var etPass: EditText? = null
//
//    @NotEmpty
//    @Length(min = 6, max = 20)
//    private var etConfirmPass: EditText? = null

    private var tvMessage : TextView? = null
    private var tvPasswordRecovery : TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forgot_password,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog=ProgressDialog(context)
        progressDialog.setMessage("Please wait!")

        etEmail = view.etEmail
        tvMessage = view.tv_message
        tvPasswordRecovery = view.passwordRecoveryText
//            etCode = view.etCode
//            etPass = view.etPass
//            etConfirmPass = view.etConfirmPass

            val validator = Validator(this)
            validator.setValidationListener(this)

            btnSubmitEmail.setOnClickListener {
                validator.validate()
            }

//            btnResetPassword.setOnClickListener {
//                validator.validate()
//            }

        passResetBack.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStackImmediate()
        }

    }

    fun responseHandler(response:String){
        progressDialog.dismiss()
        if (response.isNotEmpty()){
            flag="password"
            containerEmail.visibility=View.GONE
//            containerPassword.visibility=View.VISIBLE
        }
    }

    fun messageHandler(message:String){
        progressDialog.dismiss()
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        if (errors != null) {
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
    }

    override fun onValidationSucceeded() {

//        val tempPassword=etCode?.text.toString()

        if (flag=="email"){
            email=etEmail?.text.toString()
            if (email!=null){
                progressDialog.show()
                GlobalScope.launch(Dispatchers.IO) {
                    ForgotPasswordRepo().forgotPassword(
                        ForgotPasswordRequest(email!!,email!!),
                        ::responseHandler,
                        ::messageHandler
                    )
                }
            }else{
                Toast.makeText(context,"Please enter email",Toast.LENGTH_SHORT).show()
            }



//            Thread.sleep(3000)
//            passResetBack.performClick()
        }
        tvPasswordRecovery?.visibility = View.INVISIBLE
        tvMessage?.visibility = View.VISIBLE

//        else{
//            val code=etCode?.text.toString()
//            val password=etPass?.text.toString()
//            val confirmPassword=etConfirmPass?.text.toString()
//
//            progressDialog.show()
//                if (password.isNotEmpty()&&password==confirmPassword){
//                    GlobalScope.launch(Dispatchers.IO) {
//                        ForgotPasswordRepo().resetPassword(
//                            ResetPasswordRequest(email!!, password, code),
//                            ::messageHandler
//                        )
//
//                        progressDialog.dismiss()
//                    }
//                }else{
//                    Toast.makeText(context,"Passwords do not match",Toast.LENGTH_SHORT).show()
//                }
//        }

    }
}