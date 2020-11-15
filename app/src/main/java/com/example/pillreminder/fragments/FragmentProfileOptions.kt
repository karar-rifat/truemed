package com.example.pillreminder.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.BuildConfig
import com.example.pillreminder.MainActivity
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.activities.TutorialActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SharedPrefHelper
import com.facebook.accountkit.internal.AccountKitController
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dashboard2.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_options.*
import kotlinx.android.synthetic.main.fragment_profile_options.tvEmail
import java.io.File


class FragmentProfileOptions : Fragment(){
    var fragmentProfileInformation: FragmentProfileInformation = FragmentProfileInformation()
    var fragmentProfileDependantList: FragmentProfileDependantList = FragmentProfileDependantList()
    var fragmentProfileDocumentList: FragmentProfileDocumentList = FragmentProfileDocumentList()
    var fragmentProfileReport: FragmentProfileReport = FragmentProfileReport()

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_options,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("835479418396-rf3tpffi28sufik8k4rctl8oo5tjsids.apps.googleusercontent.com")
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)

        setData()
        imageBack.setOnClickListener {
            (activity as ProfileActivity).finish()
        }
        rlInfo.setOnClickListener {
            showProfileInformationFragment()
        }
        rlDependent.setOnClickListener {
            showProfileDependantFragment()
        }
        rlSettings.setOnClickListener {
            showProfileSettingsFragment()
        }
        rlDoc.setOnClickListener {
            showProfileDocumentListFragment()
        }
        rlReport.setOnClickListener {
            showProfileReportFragment()
        }
        rlTutorial.setOnClickListener {
            val intent = Intent(context, TutorialActivity::class.java)
            intent.putExtra("user_id",Helper.user_id)
            startActivity(intent)
        }
        rlRating.setOnClickListener {
            showRatingFragment()
        }
        rlShare.setOnClickListener {
            shareApp()
        }
        rlSupport.setOnClickListener {
            showProfileSupportFragment()
        }
        rlLogout.setOnClickListener{

            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to logout ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Yes", DialogInterface.OnClickListener {
                        _, _ -> logout()
                })
                // negative button text and action
                .setNegativeButton("No", DialogInterface.OnClickListener {
                        dialog, _ -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Log Out")
            // show alert dialog
            alert.show()
        }
    }

    fun logout(){

        firebaseAuth.signOut()
        mGoogleSignInClient.signOut()
        LoginManager.getInstance().logOut()

        SharedPrefHelper((activity as ProfileActivity).applicationContext).getInstance().logout()
        startActivity(Intent(context,MainActivity::class.java))
        (activity as ProfileActivity).finishAffinity()
    }

    private fun setData() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            var user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as ProfileActivity).runOnUiThread {
                //(activity as ProfileActivity).fragmentProfileOptions.fragmentProfileInformation.textView47.text = user.dob
                try {
                    tvName.text = user.name
                    tvEmail.text = user.email
                    if (!user.photo.isNullOrEmpty()) {
                        Log.e("doc", "file from base64")
                        //decode base64 string to image
                        val imageBytes = Base64.decode(user.photo, Base64.DEFAULT)
                        val decodedImage: Bitmap =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        ivProfile.setImageBitmap(decodedImage)
                    }
                    database.close()
                }catch (ex:Exception){}
            }
        }.start()
    }

    private fun showProfileInformationFragment(){
        Log.e("view24","clicked")
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        //val fragment = FragmentProfileInformation()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragmentProfileInformation,"FragmentProfileInformation")
        transaction.addToBackStack("FragmentProfileInformation")
        transaction.commit()
    }
    private fun showProfileDependantFragment(){
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
//        val fragmentProfileDependantList = FragmentProfileDependantList()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragmentProfileDependantList,"FragmentProfileDependantList")
        transaction.addToBackStack("FragmentProfileDependantList")
        transaction.commit()
    }
    private fun showProfileSettingsFragment(){
        Log.e("view24","clicked")
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        val fragment = FragmentProfileSettings()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragment,"FragmentProfileSettings")
        transaction.addToBackStack("FragmentProfileSettings")
        transaction.commit()
    }
       private fun showProfileDocumentListFragment(){
        Log.e("view24","clicked")
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragmentProfileDocumentList,"FragmentProfileDocumentList")
        transaction.addToBackStack("FragmentProfileDocumentList")
        transaction.commit()
    }
    private fun showProfileReportFragment() {
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
//        val fragmentProfileDependantList = FragmentProfileDependantList()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragmentProfileReport,"FragmentProfileDependantList")
        transaction.addToBackStack("FragmentProfileDependantList")
        transaction.commit()
    }

    private fun shareApp(){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check TrueMed : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun showProfileSupportFragment(){
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
//        val fragment = FragmentProfileSupport()
        val fragment = FragmentProfileSupportContact()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragment,"FragmentProfileSupport")
        transaction.addToBackStack("FragmentProfileSupport")
        transaction.commit()
    }

    private fun showRatingFragment() {

        val dialog=RatingDialogFragment.newInstance("", "")
        dialog.isCancelable=false
        dialog.show(fragmentManager!!, RatingDialogFragment.TAG)

    }
}