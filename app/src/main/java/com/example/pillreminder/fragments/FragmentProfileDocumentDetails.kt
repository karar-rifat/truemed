package com.example.pillreminder.fragments

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Document
import com.example.pillreminder.service.DataDeleteService
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_profile_document_details.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class FragmentProfileDocumentDetails : DialogFragment() {

    private var position: Int? = null
    private var id: Long? = null
    private var remoteId: Int? = null
    private var title: String? = null
    private var uri: String? = null
    private var file: String? = null
    private var type: String? = null
    lateinit var imageFile: File
    lateinit var reportFile: File
    var user_id: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        position = arguments?.getInt("position")
        id = arguments?.getLong("id")
        remoteId = arguments?.getInt("remoteId")
        title = arguments?.getString("title")
        uri = arguments?.getString("uri")
        file = arguments?.getString("file")
        type = arguments?.getString("type")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile_document_details, container)
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(getActivity());
        root.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        );

        // creating the fullscreen dialog
        val dialog = Dialog(getActivity()!!, R.style.MyCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE));
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_id = Helper.user_id

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        editText41.setText(title)

        if (uri != "" || file != null) {

            if (type == "image") {

                Log.e("doc", "file from base64")
                try {
                    //decode base64 string to image
                    val imageBytes = Base64.decode(file, Base64.DEFAULT)
                    val decodedImage: Bitmap =
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageView24.setImageBitmap(decodedImage)

                    // Find the SD Card path
                    val dir =
                        File(Environment.getExternalStorageDirectory().toString() + "/TrueMed")
                    if (!dir.exists()) {
                        dir.mkdir()
                        Log.e(TAG, "Created a new directory for File")
                    }
                    // Create a name for the saved image
                    imageFile = File(dir, "${title}.png")

                    if (!imageFile.exists()) {
                        Log.e("doc", "file from base64")
                        val decoded: ByteArray = Base64.decode(file, Base64.DEFAULT);
                        val fos = FileOutputStream(imageFile)
                        fos.write(decoded)
                        fos.flush()
                        fos.close()
                    }

                }catch (ex:Exception){}

            }
            if (type == "pdf") {
                imageView24.visibility = View.GONE
                pdfView.visibility = View.VISIBLE

                try {
                    // Find the SD Card path
                    val dir =
                        File(Environment.getExternalStorageDirectory().toString() + "/TrueMed")
                    if (!dir.exists()) {
                        dir.mkdir()
                        Log.e(TAG, "Created a new directory for PDF")
                    }
                    // Create a name for the saved image
                    reportFile = File(dir, "${title}.pdf")

                    if (!reportFile.exists()) {
                        Log.e("doc", "file from base64")
                        val decoded: ByteArray = Base64.decode(file, Base64.DEFAULT);
                        val fos = FileOutputStream(reportFile)
                        fos.write(decoded)
                        fos.flush()
                        fos.close()
                    }

                    pdfView
                        .fromFile(reportFile)
//                    .fromFile(File(uri))
                        .spacing(10) // in dp
                        .load();

//                }else {
//                    Log.e("doc", "file from uri")
//                    pdfView
//                        .fromUri(Uri.parse(uri))
////                    .fromFile(File(uri))
//                        .spacing(10) // in dp
//                        .load();
//                }

                }catch (ex:Exception){}
            }
        }

        imageView22.setOnClickListener {
            dialog?.dismiss()
        }

//        button18.setOnClickListener {
//            dialog?.dismiss()
//        }

        ibShare.setOnClickListener {

            if (type == "image") {
                shareImage()
            }
            if (type == "pdf") {
                sharePDF()
            }
        }

        btnDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context!!);
            dialogBuilder.setMessage("Do you want to delete this document ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    deleteDoc()
                })
                // negative button text and action
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Delete Document")
            // show alert dialog
            alert.show()
        }
    }

    fun shareImage() {

        val share: Intent

//        // Find the SD Card path
//        val dir = File(Environment.getExternalStorageDirectory().toString() + "/TrueMed")
//        if (!dir.exists()) {
//            dir.mkdir()
//            Log.i(TAG, "Created a new directory for PDF")
//        }
//        // Create a name for the saved image
//        val file = File(dir, "${title}.png")
////            val file = File(uri)

        try {
            share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"

            // Locate the image to Share
            val uri = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".fileprovider",
                imageFile
            )
            Log.e("document", "image uri $uri")
            // Captures the share image
            share.putExtra(Intent.EXTRA_STREAM, uri)
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Start the share dialog
            startActivity(Intent.createChooser(share, "Share Image"))
        } // Catch exceptions
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sharePDF() {
        val share: Intent
        try {
            share = Intent(Intent.ACTION_SEND)
            share.type = "application/pdf"
//            val uri = Uri.parse(uri)
            val uri = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".fileprovider",
                reportFile
            )
            Log.e("document", "pdf uri $uri")
            // Captures the share image
            share.putExtra(Intent.EXTRA_STREAM, uri)
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Start the share dialog
            startActivity(Intent.createChooser(share, "Share PDF"))
        } // Catch exceptions
        catch (e: Exception) {
            e.printStackTrace()
        }

//        val packageManager: PackageManager = context!!.packageManager
//        val testIntent = Intent(Intent.ACTION_VIEW)
//        testIntent.type = "application/pdf"
//        val list =
//            packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
//        if (list.size > 0) {
//            val intent = Intent()
//            intent.action = Intent.ACTION_VIEW
//            val uri = Uri.parse(uri)
//            intent.setDataAndType(uri, "application/pdf")
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            context!!.startActivity(intent)
//        } else {
//            Toast.makeText(
//                context,
//                "Download a PDF Viewer to see the generated PDF",
//                Toast.LENGTH_SHORT
//            ).show()
//        }

    }

    private fun deleteDoc() {
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        Thread {

            //update data to server with background service
            if (Helper.isConnected(context!!)) {
                Log.e("document delete ", "starting delete service document $remoteId")
                val service = Intent(context, DataDeleteService::class.java)
                service.putExtra("type", "document")
                service.putExtra("title", title)
                service.putExtra("remoteId", remoteId!!)
                context!!.startService(service)
            } else {
                AlarmHelper().startAlarmForSync(context!!, "document")
                Log.e("document delete ", "alarm for sync document")
            }

            //get current datetime
            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime = simpleDateFormat.format(Date()).toString()

            val doc = Document(
                id!!,
                title!!,
                uri,
                file!!,
                type!!,
                user_id!!,
                currentDateTime,
                false,
                user_id!!,
                user_id!!
            )
            database.documentDao().delete(doc)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as ProfileActivity).runOnUiThread {
                ((activity as ProfileActivity).fragmentProfileOptions.fragmentProfileDocumentList.documents as ArrayList).removeAt(
                    position!!
                )
                (activity as ProfileActivity).fragmentProfileOptions.fragmentProfileDocumentList.documentAdapter.notifyDataSetChanged()
                database.close()
                dialog?.dismiss()
            }
        }.start()
        Toast.makeText(context, "Document Deleted!!", Toast.LENGTH_LONG).show();
    }
}