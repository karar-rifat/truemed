package com.example.pillreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.network.repository.DataPushRepo
import com.example.pillreminder.network.repository.UserRepository
import com.example.pillreminder.network.request.RegisterRequest
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Password
import kotlinx.android.synthetic.main.fragment_profile_change_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentProfileChangePassword : Fragment(), Validator.ValidationListener{
    @NotEmpty
    private var prevPassEditText: EditText? = null

    @Password
    private var passEditText1: EditText? = null

    @Password
    private var passEditText2: EditText? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_change_password,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prevPassEditText = view.findViewById(R.id.editText52)
        passEditText1 = view.findViewById(R.id.editText56)
        passEditText2 = view.findViewById(R.id.editText57)

        imageView36.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

        val validator = Validator(this);
        validator.setValidationListener(this);

        button30.setOnClickListener {
            validator.validate()
        }
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
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        Thread{
            val user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
            if(user.password==prevPassEditText?.text.toString()){
                if(passEditText1?.text.toString()==passEditText2?.text.toString()){
                    user.password = passEditText1?.text.toString()
                    database.userDao().update(user)

                   val token =Helper.token
                    GlobalScope.launch(Dispatchers.IO) {
//                        DataPushRepo().pushUser(token!!, listOf(user))
                        UserRepository().updateUser(token!!, RegisterRequest(user.email!!,user.name!!,user.email!!,user.password!!,user.phoneNumber,"api"))
                    }

                    (activity as ProfileActivity).runOnUiThread {
                        Toast.makeText(context, "Password Updated Successfully", Toast.LENGTH_LONG).show();
                        database.close()
                        (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
                    }
                }else{
                    (activity as ProfileActivity).runOnUiThread {
                        Toast.makeText(context, "Confirm Password didn't match!!!", Toast.LENGTH_LONG).show();
                        database.close()
                    }
                }
            }else{
                (activity as ProfileActivity).runOnUiThread {
                    Toast.makeText(context, "Current Password didn't match!!!", Toast.LENGTH_LONG).show();
                    database.close()
                }
            }
        }.start()
    }
}