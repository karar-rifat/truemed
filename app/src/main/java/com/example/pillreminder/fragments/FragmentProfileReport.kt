package com.example.pillreminder.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.adaptors.ReportRecyclerAdapter
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.models.Report
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.fragment_profile_report.*
import kotlinx.android.synthetic.main.view_report_name_dialog.view.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentProfileReport : Fragment() {
    lateinit var reportRecyclerAdapter: ReportRecyclerAdapter
    var reports: List<Report>? = null
    var reportsCustom: List<Report>? = arrayListOf()
    private var bitmap: Bitmap? = null
    private var noOfDays: Int? = null
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var dateNow: Date? = null

    private val TAG = "PdfCreatorActivity"
    private val REQUEST_CODE_ASK_PERMISSIONS = 111
    private var pdfFile: File? = null

    //    var connectionClass: ConnectionClass? = null
    var giftitemPOJO: Report? = null
    var name: String? = null
    var price: Report? = null
    var url: Report? = null
    var type: Report? = null
    var date: Report? = null

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)

    var user_id: String? = null
    val database = Room.databaseBuilder(
        AccountKitController.getApplicationContext(),
        AppDb::class.java,
        "pill_reminder"
    ).fallbackToDestructiveMigration().build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageBackReport.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }

        recyclerReport.setHasFixedSize(true)
        recyclerReport.layoutManager = LinearLayoutManager(context)

        user_id = Helper.user_id

        dateNow = dateFormat.parse(dateFormat.format(Date()))
        endDate = dateNow

        fetchData()

        reportExport.setOnClickListener {

            if (reports!!.isNotEmpty()) {
                try {
                    createPdfWrapper()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: DocumentException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(context, "No report available!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun fetchData() {

        Thread {

            reports = database.reportDao().reports()
//            reports = database.reportDao().reportsByDays(dateThen,dateNow)
            if (reports == null) {
                reports = arrayListOf()
            }


            (activity as ProfileActivity).runOnUiThread {
                reportRecyclerAdapter = ReportRecyclerAdapter(
                    context!!,
                    reports!!,
                    (activity as ProfileActivity).supportFragmentManager
                )
                recyclerReport.adapter = reportRecyclerAdapter
            }
            database.close()

        }.start()
    }

    private fun numberOfDaysListener(view: View) {
        view.spDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val selected: String = view.spDays.selectedItem.toString()

                if (selected == "Custom Date") {
                    view.etStartDate.visibility = View.VISIBLE
                    view.etEndDate.visibility = View.VISIBLE
                } else {
                    view.etStartDate.visibility = View.GONE
                    view.etEndDate.visibility = View.GONE
                    val parts = selected.split(" ")
                    noOfDays = parts[0].toInt()

                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -noOfDays!!)
                    startDate = dateFormat.parse(dateFormat.format(calendar.time))
                    calendar.add(Calendar.DATE, noOfDays!!)

                }

                Log.e("report days", "onItemSelected: $selected")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun callDatePicker(view: View, flag: String) {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)

            val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            val date = simpleDateFormat.format(cal.time).toString()

            if (flag == "start") {
                startDate = dateFormat.parse(date)
                view.etStartDate.setText(date)
            }
            if (flag == "end") {
                endDate = dateFormat.parse(date)
                view.etEndDate.setText(date)
            }

        }

        val datePicker = DatePickerDialog(
            context!!,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePicker.datePicker.maxDate = cal.timeInMillis
        datePicker.show()

    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    private fun createPdfWrapper() {
        val hasWriteStoragePermission: Int =
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                        DialogInterface.OnClickListener { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    REQUEST_CODE_ASK_PERMISSIONS
                                )
                            }
                        })
                    return
                }
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_ASK_PERMISSIONS
                )
            }
            return
        } else {
//            createPdf()
            documentNameDialog()
        }
    }

    private fun showMessageOKCancel(
        message: String, okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    documentNameDialog()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    @Throws(FileNotFoundException::class, DocumentException::class)
    private fun createPdf() {
        val docsFolder = File(Environment.getExternalStorageDirectory().toString() + "/TrueMed")

        if (!docsFolder.exists()) {
            docsFolder.mkdir()
            Log.i(TAG, "Created a new directory for PDF")
        }

        try {
            val pdfName = "$name.pdf"
            pdfFile = File(docsFolder.absolutePath, pdfName)
            val output: OutputStream = FileOutputStream(pdfFile)
            val document = Document(PageSize.A4)
            val table = PdfPTable(floatArrayOf(3f, 3f, 3f, 3f))
            table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
            table.defaultCell.minimumHeight = 40F
            table.totalWidth = PageSize.A4.width
            table.widthPercentage = 100F
            table.defaultCell.verticalAlignment = Element.ALIGN_MIDDLE
            table.addCell("TITLE")
            table.addCell("STATUS")
            table.addCell("SCHEDULED ON")
            table.addCell("TAKEN ON")
            table.headerRows = 1

            val cells: Array<PdfPCell> = table.getRow(0).cells
            for (j in cells.indices) {
                cells[j].backgroundColor = BaseColor.GRAY
            }

            var timeSection: String? = null
            var dateSection: String? = null

            for (i in reportsCustom!!.indices) {
                val report = reportsCustom!![i]

                val name: String = report.name!!
                val status: String? = report.status
                val date: String = dateFormat.format(report.date)
                val time: String = report.time!!

                val scheduleTime = when(report.type){
                    "pill"->{
                        formatTime(report.scheduleTime!!)
                    }
                    else->{
                        formatTime(report.scheduleTime!!.split("  ")[1])
                    }
                }

                Log.e(TAG, "schedule time: ${report.scheduleTime}")

                if (date != dateSection) {
                    val cell = PdfPCell(Phrase(date))
                    cell.colspan = 4
                    cell.minimumHeight = 40F
                    cell.verticalAlignment = Element.ALIGN_MIDDLE
                    cell.paddingLeft = 8F
                    table.addCell(cell)

                    dateSection = date
                }

                if (getTimeSection(time) != timeSection) {

                    val cell = PdfPCell(Phrase(getTimeSection(time)))
                    cell.colspan = 4
                    cell.minimumHeight = 40F
                    cell.verticalAlignment = Element.ALIGN_MIDDLE
                    cell.paddingLeft = 8F
                    table.addCell(cell)

                    timeSection = getTimeSection(time)
                }

                table.addCell(name)
                table.addCell(status)
                table.addCell(scheduleTime)
                table.addCell(time)
            }

//        System.out.println("Done");
            PdfWriter.getInstance(document, output)
            document.open()
            val f = Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE)
            val g = Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE)
            document.add(Paragraph("TrueMed \n\n", f))
            document.add(Paragraph("Medication Report\n\n", g))
            document.add(table)

//        for (int i = 0; i < MyList1.size(); i++) {
//            document.add(new Paragraph(String.valueOf(MyList1.get(i))));
//        }

            document.close()
            Log.e("report", "Report created!")
//        Toast.makeText(context,"Report created! Please find in /TrueMed folder!",Toast.LENGTH_SHORT).show()

//        previewPdf()
//        documentNameDialog()
            validateAndUpload()

        }catch (ex:Exception){}
    }

    //formate time with AM,PM for button
    fun formatTime(time: String): String? {
        val timeParts = time.split(":")
        var hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        val format: String
        val formattedTime: String
        when {
            hour == 0 -> {
                hour += 12
                format = "am"
            }
            hour == 12 -> {
                format = "pm"
            }
            hour > 12 -> {
                hour -= 12
                format = "pm"
            }
            else -> {
                format = "am"
            }
        }
        val newMinute = if (minute < 10) "0$minute" else minute.toString()
        formattedTime = "$hour:$newMinute $format"
        return formattedTime
    }

    fun getTimeSection(time: String): String {
        var parts = time.split(":")
        val hour = parts[0]
        parts = parts[1].split(" ")
        val am_pm = parts[1]
        Log.e("report ", "hour $hour am/pm $am_pm")

        if (hour.toInt() in 6..11 && am_pm == "AM") {
            return "Morning"
        }
        if ((hour.toInt() == 12 && am_pm == "PM") || (hour.toInt() in 1..3 && am_pm == "PM")) {
            return "Noon"
        }
        if (hour.toInt() in 4..7 && am_pm == "PM") {
            return "Evening"
        }
        if (hour.toInt() in 8..11 && am_pm == "PM") {
            return "Night"
        }

        return ""
    }

    private fun previewPdf() {
        val packageManager: PackageManager = context!!.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list =
            packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.size > 0) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
//            val uri = Uri.fromFile(pdfFile)
            val uri = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".fileprovider",
                pdfFile!!
            )
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context!!.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "Download a PDF Viewer to see the generated PDF",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun documentNameDialog() {

        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.view_report_name_dialog, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
//        alertDialog.setCancelable(false)

        val etName = view.findViewById<TextView>(R.id.btnPermissions)
        val btnSave = view.findViewById<TextView>(R.id.btnSave)
        val btnCancel = view.findViewById<TextView>(R.id.btnCancel)

        val spDaysAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.array_sp_report_days,
            android.R.layout.simple_spinner_item
        )
        spDaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spDays.adapter = spDaysAdapter

        numberOfDaysListener(view)

        view.etStartDate.setOnClickListener {
            callDatePicker(view, "start")
        }

        view.etEndDate.setOnClickListener {
            callDatePicker(view, "end")
        }

        btnSave.setOnClickListener {

            name = etName.text.toString()
            if (name!!.isNotEmpty()) {
//                validateAndUpload(name)

                if (startDate!! > endDate!!) {
                    Toast.makeText(
                        context,
                        "End date must be after start date.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                Thread {
                    reportsCustom = database.reportDao().reportsByDays(startDate!!, endDate!!)
                    database.close()
                }.start()

                alertDialog.dismiss()
                createPdf()
            } else {
                etName.error = "Can't be empty"
            }

        }
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun validateAndUpload() {
        val uri = FileProvider.getUriForFile(
            context!!,
            context!!.applicationContext.packageName + ".fileprovider",
            pdfFile!!
        )

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        if (name == "" || uri == null) {
            Toast.makeText(context, "Validation Error!", Toast.LENGTH_LONG).show()
        } else {
            Thread {
                //get current datetime
                val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
                val currentDateTime = simpleDateFormat.format(Date()).toString()
                var base64: String? = null

                if (pdfFile!!.exists()) {

//                    base64 = encodeFileToBase64Binary(pdfFile!!)

                    try {
                        var inputStream: InputStream? =
                            null //You can get an inputStream using any IO API
                        inputStream = FileInputStream(pdfFile)
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        val output = ByteArrayOutputStream()
                        val output64 = Base64OutputStream(output, Base64.NO_WRAP)
                        try {
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                output64.write(buffer, 0, bytesRead)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        output64.close()
                        base64 = output.toString()

                    }catch (ex:Exception){}

                }

                Log.e("documents", "base64 : " + base64)

                val doc = com.example.pillreminder.models.Document(
                    name!!,
                    uri.toString(),
                    base64!!,
                    "pdf",
                    user_id!!,
                    currentDateTime,
                    false,
                    user_id!!,
                    user_id!!
                )

                database.documentDao().saveDocument(doc)
                database.documentDao().documents().forEach {
                    Log.i("documents", """ Title - ${it.title}""")
                }

                //update data to server with background service
                if (Helper.isConnected(context!!)) {
                    val token = Helper.token

                    Log.e("doc add ", "starting push service doc")
                    val service = Intent(context, DataPushService::class.java)
                    service.putExtra("type", "document")
                    service.putExtra("token", token)
                    context!!.startService(service)
                } else {
                    AlarmHelper().startAlarmForSync(context!!, "document")
                    Log.e("document add ", "alarm for sync document")
                }

                (activity as ProfileActivity).runOnUiThread {
                    database.close()
//                    (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
                }
            }.start()
            Toast.makeText(context, "Report added in Documents section", Toast.LENGTH_LONG).show()
        }


    }

    fun updateReportDocInService() {
        //update data to server with background service
        if (Helper.isConnected(context!!)) {
            val token = Helper.token

            Log.e("report doc add ", "starting push service report doc")
            val service = Intent(context, DataPushService::class.java)
            service.putExtra("type", "document")
            service.putExtra("token", token)
            context!!.startService(service)
        } else {
            AlarmHelper().startAlarmForSync(context!!, "document")
            Log.e("report doc add ", "alarm for sync report doc")
        }
    }

    private fun encodeFileToBase64Binary(yourFile: File): String {
        val size = yourFile.length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(yourFile))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}