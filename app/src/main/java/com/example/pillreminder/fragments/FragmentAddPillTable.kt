package com.example.pillreminder.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Table
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_add_pill_table.*
import kotlinx.android.synthetic.main.fragment_add_pill_table.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentAddPillTable : DialogFragment() , Validator.ValidationListener{
    @NotEmpty
    private var tableTitleET: EditText? = null
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_add_pill_table, container)
            return rootView
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            tableTitleET = view.editText47

            val validator = Validator(this)
            validator.setValidationListener(this)

            button25.setOnClickListener {
                validator.validate()
            }

            imageView32.setOnClickListener{
                dialog?.dismiss()
            }
        }


    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
            val view = error.getView()
            val message = error.getCollatedErrorMessage(context)

            // Display error messages ;)
            if (view is EditText) {
                view.error = message
            } else {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onValidationSucceeded() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            var newTable = Table(editText47.text.toString(),(activity as DashboardActivity).user_id!!,currentDateTime,false)
            var id  = database.tableDao().saveTable(newTable)
            newTable.localId = id.toInt()
            Log.e("new table details",newTable.localId.toString()+" "+newTable.title)
//            database.tableDao().tables().forEach {
//                Log.i("appointment",""" Title - ${it.id}""")
//            }


            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                val token=Helper.token

                Log.e("table add ", "starting push service table")
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", "table")
                service.putExtra("token", token)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"table")
                Log.e("table add ", "alarm for sync table")
            }

            System.out.println("First one ===>"+activity)
           // System.out.println("second ==========>"+activity as DashboardActivity)
            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentPillList.tables as ArrayList?)?.add(newTable)
                (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Table Created Successfully", Toast.LENGTH_LONG).show();
        (activity as DashboardActivity).hideAddall()
    }


    override fun onStart() {
            super.onStart()
            //dialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
}