package com.example.pillreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_profile_change_email.*
import kotlinx.android.synthetic.main.fragment_profile_manage_acc.*

class FragmentProfileManageAccount : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_manage_acc,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
            (activity as ProfileActivity).runOnUiThread {

                tvEmail.text=user.email

                database.close()
            }
        }.start()

        imageView34.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

//        button27.setOnClickListener {
//            showChangeEmailFragment()
//        }

        button28.setOnClickListener {
            showChangePasswordFragment()
        }
    }


    private fun showChangeEmailFragment() {
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        val fragment = FragmentProfileChangeEmail()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragment,"FragmentProfileChangeEmail")
        transaction.addToBackStack("FragmentProfileChangeEmail")
        transaction.commit()
    }

    private fun showChangePasswordFragment() {
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        val fragment = FragmentProfileChangePassword()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragment,"FragmentProfileChangePassword")
        transaction.addToBackStack("FragmentProfileChangePassword")
        transaction.commit()
    }

}
