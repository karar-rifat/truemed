package com.example.pillreminder.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.database.AppDb
import com.facebook.accountkit.internal.AccountKitController
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AlertDialog
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Table
import com.example.pillreminder.service.DataPushService
import kotlinx.android.synthetic.main.fragment_table_edit.*
import kotlinx.android.synthetic.main.fragment_table_edit.view.*


class FragmentTableEdit : DialogFragment() , Validator.ValidationListener {

    private var position : Int? = null
    private var table_id : Int? = null
    private var title : String? = null
    private var user_id : String? = null

    @NotEmpty
    private var tableNameET: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(getActivity());
        root.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        val dialog = Dialog(getActivity()!!,R.style.MyCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE));
        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_table_edit, container)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getInt("position")
        table_id = arguments?.getInt("table_id")
        title = arguments?.getString("table_title")
        user_id = arguments?.getString("user_id")
        Log.e("table id in on attach",table_id.toString())
        Log.e("table title",title!!)
        Log.e("table position",position.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView45.setOnClickListener{
            dialog?.dismiss()
        }
        tableNameET = view.editText14

        editText14.setText(title)

        val validator = Validator(this)
        validator.setValidationListener(this)

        button34.setOnClickListener {
            validator.validate()
        }

        imageButton16.visibility=View.INVISIBLE

        imageButton16.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this table ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        _, _ -> deleteTable()
                    (activity as DashboardActivity).showAddPill()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, _ -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("AlertDialogExample")
            // show alert dialog
            alert.show()
//            deletePill()
        }

//        Log.e("table id from arguments",arguments?.getInt("table_id").toString())

    }

    private fun deleteTable() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        Log.e("position for array",position.toString())
        Thread{
            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val temp = Table(table_id!!,title!!,user_id!!,currentDateTime,false)
            database.tableDao().delete(temp)
            //delete pill of this table and timepivot
            val pills=database.pillDao().getTableSpecificPills(table_id!!)
            pills.forEach { pill ->
                Log.e("delete table","pill row ${pill.localId}")
                val timePivot=database.timePivotDao().getData(pill.title!!,"pill")
                timePivot.forEach {
                    database.timePivotDao().deleteRows(it.localId,"pill")
                    Log.e("delete table","deleted timePivot row ${it.idToMatch}")
                }
            }
            database.pillDao().deleteTableSpecificPills(table_id!!)
            Log.e("delete table","deleted table row $table_id")

            //update data to server with background service
            if (Helper.isConnected(context!!)) {

                val token=Helper.token
                Log.e("table update ", "starting push service table")
                val service = Intent(context, DataPushService::class.java)
                service.putExtra("type", "table")
                service.putExtra("token", token)
                context!!.startService(service)
            }else{
                AlarmHelper().startAlarmForSync(context!!,"table")
                Log.e("table update ", "alarm for sync table")
            }

//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentPillList.tables as ArrayList).removeAt(position!!)
                (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Table Deleted!!", Toast.LENGTH_LONG).show();
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error in errors!!) {
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
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()

            val newTable = Table(table_id!!,editText14.text.toString(),user_id!!,currentDateTime,false)
            database.tableDao().update(newTable)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
//            Log.e("table id which is added",table_id.toString())
            (activity as DashboardActivity).runOnUiThread {
                ((activity as DashboardActivity).fragmentPillList.tables as ArrayList).set(position!!,newTable)
                (activity as DashboardActivity).fragmentPillList.pillRecyclerAdapter?.notifyDataSetChanged()
                database.close()
                Toast.makeText(context, "Table Updated Successfully", Toast.LENGTH_LONG).show();
                dialog?.dismiss()
            }
        }.start()
    }
}
