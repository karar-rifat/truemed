package com.example.pillreminder.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.helper.SharedPrefHelper
import kotlinx.android.synthetic.main.fragment_pass_code.view.*
import kotlinx.android.synthetic.main.fragment_profile_support_contact.*

class FragmentPassCode: Fragment() {


    var passCode:String?=null
    var userId:String?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userId = arguments?.getString("user_id")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pass_code,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passCode=SharedPrefHelper(context!!).getInstance().getPassCode()

        if (!passCode.isNullOrEmpty()){
            view.btnDisable.visibility=View.VISIBLE
            view.btnSave.visibility= View.INVISIBLE //"Change Pass Code"
            view.etPassCodeVerify.visibility = View.INVISIBLE

        }

        view.btnSave.setOnClickListener {
            if (view.etPassCode.text.isNullOrEmpty()||view.etPassCodeVerify.text.isNullOrEmpty()){
                Toast.makeText(context,"please enter code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (view.etPassCode.text.toString()!=view.etPassCodeVerify.text.toString()){
                Toast.makeText(context,"Code doesn't match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SharedPrefHelper(context!!).getInstance().setPassCode(view.etPassCode.text.toString())
            Toast.makeText(context,"Pass code has successfully saved!", Toast.LENGTH_SHORT).show()
//            val fragmentSettings = FragmentProfileSettings()
//            val manager = fragmentManager
//            manager?.beginTransaction()
//                    ?.replace(R.id.fragment_container, fragmentSettings, "settings")
//                    ?.commit()
            view.btnBack.performClick()

        }

        view.btnDisable.setOnClickListener {

            if (passCode.isNullOrEmpty()){
                Toast.makeText(context,"You don't have pass code set yet.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passCode != view.etPassCode.text.toString()){
                Toast.makeText(context,"Passcode does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            if (view.etPassCode.text.isNullOrEmpty()||view.etPassCodeVerify.text.isNullOrEmpty()){
//                Toast.makeText(context,"please enter code.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

//            if (view.etPassCode.text.toString()!=view.etPassCodeVerify.text.toString()){
//                Toast.makeText(context,"Code doesn't match!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            if(passCode == view.etPassCode.text.toString()){
                Toast.makeText(context,"Pass code has successfully disabled!", Toast.LENGTH_SHORT).show()
                SharedPrefHelper(context!!).getInstance().clearPassCode()
                view.btnBack.performClick()
            }



        }

        view.btnBack.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }
    }
}