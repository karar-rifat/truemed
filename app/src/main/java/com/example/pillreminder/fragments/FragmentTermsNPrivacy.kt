package com.example.pillreminder.fragments

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ActivityNotFoundException
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.testing.FakeReviewManager
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import kotlinx.android.synthetic.main.fragment_terms_n_privacy.*
import kotlinx.android.synthetic.main.view_rating_alert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FragmentTermsNPrivacy : Fragment() {

    var type:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type=it.getString("type")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_n_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type=="terms"){
            showTerms()
        }
        if (type=="policy"){
            showPolicy()
        }

        ivBack.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

//        btnClose.setOnClickListener {
//            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
//        }

    }

    fun showTerms(){
        val header="Terms Of Service"
        val title=resources.getString(R.string.terms_of_service_title)
        val body=resources.getString(R.string.terms_of_service_body)

        tvHeader.text=header

//        tvTitle.text=title
//        tvBody.text=body

        webView.loadUrl("file:///android_asset/terms_of_service.html")
    }

    fun showPolicy(){
        val header="Privacy Policy"
        val title=resources.getString(R.string.privacy_policy_title)
        val body=resources.getString(R.string.privacy_policy_body)

        tvHeader.text=header

//        tvTitle.text=title
//        tvBody.text=body

        webView.loadUrl("file:///android_asset/privacy_policy.html")
    }
}