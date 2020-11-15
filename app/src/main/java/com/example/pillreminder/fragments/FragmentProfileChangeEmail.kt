package com.example.pillreminder.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.facebook.accountkit.internal.AccountKitController
import com.facebook.accountkit.internal.AccountKitController.getApplicationContext
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Email
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_profile_change_email.*
import kotlinx.android.synthetic.main.fragment_profile_document_add.*
import java.io.File

class FragmentProfileChangeEmail : Fragment(), Validator.ValidationListener{
    @NotEmpty
    @Email
    private var emailEditText: EditText? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_change_email,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = view.findViewById(R.id.editText50)

        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
            (activity as ProfileActivity).runOnUiThread {

                editText50.setText(user.email)

                database.close()
            }
        }.start()

        imageView35.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

        val validator = Validator(this);
        validator.setValidationListener(this);

        button29.setOnClickListener {
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
        val database = databaseBuilder(getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
            user.email = editText50.text.toString()
            database.userDao().update(user)

            (activity as ProfileActivity).runOnUiThread {
                database.close()
                (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
            }
        }.start()
        Toast.makeText(context, "Email Updated Successfully", Toast.LENGTH_LONG).show();
    }
}