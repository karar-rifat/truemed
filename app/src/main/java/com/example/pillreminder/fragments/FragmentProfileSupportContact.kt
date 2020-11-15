package com.example.pillreminder.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.network.repository.UserRepository
import com.example.pillreminder.network.request.FeedBackRequest
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Email
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_profile_support_contact.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentProfileSupportContact : Fragment() , Validator.ValidationListener{

//    @NotEmpty
//    private var nameET: EditText? = null
//
//    @NotEmpty
//    @Email
//    private var emailET: EditText? = null

    @NotEmpty
    private var subjectET: EditText? = null

    @NotEmpty
    private var messageET: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_support_contact,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        nameET = view.findViewById<EditText>(R.id.etName)
//        emailET = view.findViewById<EditText>(R.id.etEmail)
        subjectET = view.findViewById<EditText>(R.id.etSubject)
        messageET = view.findViewById<EditText>(R.id.etMessage)

        val validator = Validator(this);
        validator.setValidationListener(this);

        btnBack.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

        btnSend.setOnClickListener {
            validator.validate()
        }

        ivFB.setOnClickListener {
            val facebook_page_url=resources.getString(R.string.facebook_page_url)
            val facebook_page_url_for_app=resources.getString(R.string.facebook_page_url_for_app)
            val intentApp=Intent(Intent.ACTION_VIEW, Uri.parse(facebook_page_url_for_app))
            val intentBrowser=Intent(Intent.ACTION_VIEW, Uri.parse(facebook_page_url))
//            try {
//                intentApp.setPackage("com.facebook.katana")
//                startActivity(intentApp)
//            }catch (ex:ActivityNotFoundException){
//                startActivity(intentBrowser)
//            }

            val intent=Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(facebook_page_url)
            try {
                intent.setPackage("com.facebook.katana")
                startActivity(intent)
            }catch (exception:ActivityNotFoundException){
                startActivity(intentBrowser)
            }

        }

        ivGoogle.setOnClickListener {
            val youtube_channel_id=resources.getString(R.string.youtube_channel_id)
            val youtube_channel_url=resources.getString(R.string.youtube_channel_url)
            val intentApp=Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:channel/"+youtube_channel_id))
            val intentBrowser=Intent(Intent.ACTION_VIEW, Uri.parse(youtube_channel_url))
            try {
                startActivity(intentApp)
            }catch (ex:ActivityNotFoundException){
                startActivity(intentBrowser)
            }
        }
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
    }

    override fun onValidationSucceeded() {
//        val name=etName.text.toString()
//        val email=etEmail.text.toString()
        val subject=etSubject.text.toString()
        val feedback=etMessage.text.toString()
        val feedBackRequest=FeedBackRequest(subject,feedback)

        val token= Helper.token
        GlobalScope.launch(Dispatchers.IO){
            val response=UserRepository().sendFeedBack(token!!,feedBackRequest)
            (activity as ProfileActivity).runOnUiThread {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
            }
        }
    }
}