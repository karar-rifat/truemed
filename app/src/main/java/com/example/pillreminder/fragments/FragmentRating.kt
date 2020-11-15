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
import androidx.fragment.app.Fragment
import com.example.pillreminder.R
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.review.testing.FakeReviewManager
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel
import kotlinx.android.synthetic.main.view_rating_alert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FragmentRating : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val manager = ReviewManagerFactory.create(context!!)
        val manager = FakeReviewManager(context)

        rate()

        val reviewRequest = manager.requestReviewFlow()
        reviewRequest.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                // We got the ReviewInfo object
                Log.e("rating", "We got the ReviewInfo object")
                val reviewInfo = request.result
                val flow = manager.launchReviewFlow(activity!!, reviewInfo)
                if (reviewInfo != null) {
                    GlobalScope.launch(Dispatchers.Main) {
                        manager.launchReview(requireActivity(), reviewInfo)
                    }
                }
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } else {
                // There was some problem, continue regardless of the result.
                Log.e("rating", "There was some problem, continue regardless of the result.")
            }
        }
    }

    fun rate() {

        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.view_rating_alert, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)

        val btnRate=view.findViewById<TextView>(R.id.btnRate)
        val btnLater=view.findViewById<TextView>(R.id.btnLater)

        btnRate.setOnClickListener {

            val packageName=context!!.packageName
            Log.e("rating",packageName)
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
        }
        btnLater.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialog.show()
    }
}