package com.example.pillreminder.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_profile_information.*
import kotlinx.android.synthetic.main.fragment_profile_information.tvEmail
import kotlinx.android.synthetic.main.fragment_profile_information.tvName

class FragmentProfileInformation : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_profile_edit_test,container,false)
        return inflater.inflate(R.layout.fragment_profile_information,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.e("pro info user id",(activity as ProfileActivity).user_id.toString())

        imageView25.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }
        button19.setOnClickListener {
//            (activity as ProfileActivity).showProfileEditFragment()
//            val fragmentManager = (activity as ProfileActivity).supportFragmentManager
//            val transaction = fragmentManager.beginTransaction()
//            val fragment = FragmentProfileEdit()
//            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//            transaction?.replace(R.id.fragment_container_profile,fragment)
//            transaction?.addToBackStack("FragmentProfileEdit")
//            transaction?.commit()
            val pop = FragmentProfileEdit()
            val fm = (activity as ProfileActivity).supportFragmentManager
            pop.show(fm, "FragmentProfileEdit")
        }

        tvTerms.setOnClickListener {

            val transaction =  (activity as ProfileActivity).manager.beginTransaction()
            val fragment = FragmentTermsNPrivacy()
            val bundle = Bundle()
            bundle.putString("type", "terms")
            fragment.arguments = bundle
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction.replace(R.id.fragment_container_profile,fragment,"fragmentTerms")
            transaction.addToBackStack("fragmentTerms")
            transaction.commit()
        }
        tvPolicy.setOnClickListener {
            val transaction =  (activity as ProfileActivity).manager.beginTransaction()
            val fragment = FragmentTermsNPrivacy()
            val bundle = Bundle()
            bundle.putString("type", "policy")
            fragment.arguments = bundle
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction.replace(R.id.fragment_container_profile,fragment,"fragmentPolicy")
            transaction.addToBackStack("fragmentPolicy")
            transaction.commit()
        }

        setData()
    }

    fun setData(){
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val user = database.userDao().getUserByEmail(Helper.user_id!!)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            try {
                activity?.runOnUiThread {
                    //(activity as ProfileActivity).fragmentProfileOptions.fragmentProfileInformation.textView47.text = user.dob
                    tvName.text = user.name
                    tvEmail.text = user.email
                    textView47.text = user.dateOfBirth
                    textView49.text = user.gender
                    tvBloodGroup.text = user.bloodGroup
                    textView51.text = "${user.height} cm"
                    textView53.text = "${user.weight} kg"
                    textView55.text = user.bloodPressure
                    if (!user.photo.isNullOrEmpty()) {
//                    var imgFile = File(user.photo);
//                    if(imgFile.exists())
//                    {
//                        proPic.setImageURI(Uri.fromFile(imgFile))
//                    }
//                    imageView26.setImageURI(Uri.parse(user.pro_pic))
                        Log.e("doc", "file from base64")
                        //decode base64 string to image
                        val imageBytes = Base64.decode(user.photo, Base64.DEFAULT)
                        val decodedImage: Bitmap =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        proPic.setImageBitmap(decodedImage)
                    }
                    database.close()
//                ((activity as DashboardActivity).fragmentVaccineList.vaccines as ArrayList).removeAt(position!!)
//                (activity as DashboardActivity).fragmentVaccineList.vaccineRecyclerAdapter.notifyDataSetChanged()
//                dialog.dismiss()
                }
            }catch (ex:Exception){}
        }.start()
    }

    override fun onResume() {
        super.onResume()

        // Register Receiver to receive messages.
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcastReceiver, IntentFilter("fetch-data"))

    }

    // handler for received Intents for the "my-event" event
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Extract data included in the Intent
            val isDataFetched = intent.getBooleanExtra("isDataFetched",false)
            if (isDataFetched){
               setData()
            }
            Log.d("receiver", "Got message: $isDataFetched")
        }
    }

    override fun onDestroyView() {
//        if (view != null) {
//            val parentViewGroup = view?.parent as ViewGroup?
//            parentViewGroup?.removeAllViews();
//        }
        super.onDestroyView()
    }
}