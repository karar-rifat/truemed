package com.example.pillreminder.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.*
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.fragments.*
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.network.repository.LoginRepo
import com.example.pillreminder.network.repository.MedicineRepo
import com.example.pillreminder.network.request.LoginRequest
import com.example.pillreminder.network.response.MedicineResponse
import com.example.pillreminder.receivers.AlarmReceiver
import com.example.pillreminder.service.DataFetchService
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.activity_dashboard2.*
import kotlinx.android.synthetic.main.content_dashboard2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class DashboardActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    val manager = supportFragmentManager
    private lateinit var flag: String
    var temp: String = ""
    var user_id: String? = null
    var from_session: Boolean? = false
    lateinit var activeFragment: Fragment
    var fragmentPillList: FragmentPillList = FragmentPillList()

    var fragmentCreateAppointment: FragmentCreateAppointment = FragmentCreateAppointment()
    var fragmentCreateVaccine: FragmentCreateVaccine = FragmentCreateVaccine()

    var fragmentVaccineList: FragmentVaccineList = FragmentVaccineList()
    var fragmentAppointmentList: FragmentAppointmentList = FragmentAppointmentList()
    var fragmentDashboard: FragmentDashboard = FragmentDashboard()
    var fragmentDashboardMedList: FragmentDashboardMedList = FragmentDashboardMedList()
    var fragmentRating: FragmentRating = FragmentRating()

    var fragmentAddPillFirst: FragmentAddPillFirst = FragmentAddPillFirst()
    var fragmentPillDetailsSecond: FragmentPillDetailsSecond = FragmentPillDetailsSecond()

    //        lateinit var toolbar: ActionBar
    private val inst: DashboardActivity? = null
    fun instance(): DashboardActivity {
        return inst!!
    }

    val database = Room.databaseBuilder(
        AccountKitController.getApplicationContext(),
        AppDb::class.java,
        "pill_reminder"
    ).fallbackToDestructiveMigration().build()
    lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Helper.isAppAlive = true

        //register network state change
//        registerReceiver(NetworkStateChecker(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        Helper.token = SessionManager(this).fetchAuthToken()
        val password = SharedPrefHelper(this).getInstance().getPassword()
        user_id = intent.getStringExtra("user_id")
        from_session = intent.getBooleanExtra("from_session", false)
        Helper.user_id = user_id
        Log.e("Dashboard user id", user_id.toString())
        setContentView(R.layout.activity_dashboard2)
//        setSupportActionBar(toolbar)
//        showDashboardFragment()
        invisReminderOptions()
        invisInformationOptions()

        Thread {
            if (from_session!!) {
                GlobalScope.launch(Dispatchers.IO) {
                    LoginRepo().login(LoginRequest(user_id!!, password!!), ::loginHandler)
                }
            } else {
                fetchData(Helper.token!!)
            }
        }.start()

        //starting data fetch service
//        if (Helper.isConnected(this)) {
//            Thread {
//                val token=Helper.token
////            val token: String? = SessionManager(this).fetchAuthToken()
//                MedicineRepo().getMedicine(token!!) as ArrayList<MedicineResponse>
//                database.appointmentDao().clearAppointment()
//                database.vaccineDao().clearVaccine()
//                database.pillDao().clearPill()
//                database.reportDao().clearReport()
//                database.timePivotDao().clearTimePivot()
//                database.documentDao().clearDocument()
//                database.tableDao().clearTable()
//
//                //start service to fetch data from server
//                val service = Intent(applicationContext, DataFetchService::class.java)
//                service.putExtra("token", Helper.token)
//                applicationContext.startService(service)
//
////                runOnUiThread {
////                    Log.e("Dashboard","calling dashboard")
////                    if (Helper.isDataFetched)
////                    showDashboardFragment()
////                }
//            }.start()
//        }

        userProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", user_id)
            startActivity(intent)

            handleBtnClick(it)
        }

//        Helper.user=User("atik","a","1234","123","qqd","wq","dqd","qq","qsq","qs","qs",false)
//        val service = Intent(this, DataSyncService::class.java)
//        service.putExtra("type", "user")
//        this.startService(service)

//        //test service
//        val service = Intent(this, AppService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            startForegroundService(service);
//        else
//            startService(service);

        addIconAppointment.setOnClickListener {
//            addIconAppointment.visibility = View.INVISIBLE
            showCreateAppointmentFragment()
//            val intent = Intent(this, AppointmentActivity::class.java)
//            startActivityForResult(intent,100)
//            addIconAppointment.isEnabled=false
            handleBtnClick(it)
        }

        addIconVaccine.setOnClickListener {
//            addIconVaccine.visibility = View.INVISIBLE
            showCreateVaccineFragment()
//            val intent = Intent(this, VaccineActivity::class.java)
//            startActivityForResult(intent,200)
//            addIconVaccine.isEnabled=false
            handleBtnClick(it)
        }

        backDashData.setOnClickListener {
            supportFragmentManager.popBackStackImmediate()
            showProPic()
        }

        addIconPill.setOnClickListener {
//                showCreatePillTableFragment()
            showAddPillFragment()
//            addIconPill.isEnabled=false
            handleBtnClick(it)
        }

        ivInformation.setOnClickListener {
//            showRatingFragment()

            txtHeader.text = "Information"
            val fragment = FragmentInformation()
            val bundle = Bundle()
            bundle.putString("category", "instruction")
            fragment.arguments = bundle
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction.replace(R.id.fragment_container, fragment, "FragmentInformation")
            transaction.addToBackStack("FragmentInformation")
            transaction.commit()
            activeFragment = fragment
            hideAddall()
            markInformationButton()
            invisReminderOptions()
            showInformationOptions()
        }

        btnInstructions.setOnClickListener {
            showInformationWithCat("instruction")
        }

        btnFactors.setOnClickListener {
            showInformationWithCat("factor")
        }
        btnMistake.setOnClickListener {
            showInformationWithCat("mistake")
        }

        imageButton3.setOnClickListener {
            invisInformationOptions()
            showReminderOptions()
            markReminderButton()
        }
        imageButton4.setOnClickListener {
            showDashboardFragment()
        }
        btnPill.setOnClickListener {
            showPillListFragment()
        }
        btnVaccine.setOnClickListener {
            if (activeFragment != fragmentVaccineList) {
                showVaccineListFragment()
            }
        }
        btnAppointment.setOnClickListener {
            if (activeFragment != fragmentAppointmentList) {
                showAppointmentListFragment()
            }
        }

        addIconAll.setOnClickListener {
            val popUpMenu = PopupMenu(applicationContext, addIconAll)
            popUpMenu.menuInflater.inflate(R.menu.menu_add_all_reminder, popUpMenu.menu)

            popUpMenu.setOnMenuItemClickListener { menu ->
                when (menu!!.itemId) {
                    R.id.pill -> {
                        showAddPillFragment()
                    }
                    R.id.appointment -> {
                        showCreateAppointmentFragment()
                    }
                    R.id.vaccine -> {
                        showCreateVaccineFragment()
                    }
                }
                true
            }
            popUpMenu.show()
        }

        showDashboardFragment()

        popUpPermission()

//        handleBatteryOptimization()

        btnNotifications.setOnClickListener {
            val fragment = FragmentNotification()
            val transaction = manager.beginTransaction()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction.replace(R.id.fragment_container, fragment, "FragmentNotification")
            transaction.addToBackStack("FragmentNotification")
            transaction.commit()
        }
    }

    fun handleBtnClick(view: View) {
        view.isEnabled = false
        val secondsDelayed = 2
        Handler().postDelayed(Runnable {
            view.isEnabled = true
        }, ((secondsDelayed * 1000).toLong()))
    }

    private fun loginHandler(token: String, error: Boolean) {
        if (!error) {
            Helper.token = token
            SessionManager(this).saveAuthToken(token)
            Log.e("dashboard activity", " Token - $token")

            fetchData(token)
        }
    }

    fun fetchData(token: String) {
        if (Helper.isConnected(this)) {
            Thread {

                MedicineRepo().getMedicine(token) as ArrayList<MedicineResponse>
                database.appointmentDao().clearAppointment()
                database.vaccineDao().clearVaccine()
                database.pillDao().clearPill()
                database.reportDao().clearReport()
                database.timePivotDao().clearTimePivot()
                database.documentDao().clearDocument()
                database.tableDao().clearTable()
                database.informationDao().clearInformations()

                //start service to fetch data from server
                val service = Intent(applicationContext, DataFetchService::class.java)
                service.putExtra("token", Helper.token)
                applicationContext.startService(service)

            }.start()

        }
    }

    override fun onPause() {
        super.onPause()

//        unregisterReceiver(NetworkStateChecker())
    }

    fun handleBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("battery saver:", "entered fun")
            val intent = Intent()
            val packageName = packageName
            val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

            if (isXiaomi()) {
                    try {
                        intent.component = ComponentName(
                            "com.miui.powerkeeper",
                            "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
                        )
                        intent.putExtra("package_name", getPackageName())
                        intent.putExtra("package_label", getText(R.string.app_name))
                        startActivity(intent)
                    } catch (anfe: ActivityNotFoundException) {
                    }
            }else if (Build.MANUFACTURER == "samsung") {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    intent.component = ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    intent.component = ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")
                }

                try {
                    startActivity(intent);
                } catch (ex: ActivityNotFoundException) {
                    // Fallback to global settings
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }else if (Build.MANUFACTURER == "Honor"||Build.MANUFACTURER == "huwawei") {

                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
                try {
                    startActivity(intent);
                } catch (ex: ActivityNotFoundException) {
                    // Fallback to global settings
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
            else {

                startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
            }
        }
    }

    fun handleAutoStartPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("battery saver:", "entered fun")
            val intent = Intent()
            val packageName = packageName
            val manufacturer = Build.MANUFACTURER
            val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
                startActivity(intent)
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
                startActivity(intent)
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
                startActivity(intent)
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
                startActivity(intent)
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
                startActivity(intent)
            } else {
                try {
                    val intentAS = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    intentAS.addCategory(Intent.CATEGORY_DEFAULT)
                    intentAS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentAS)
                } catch (anfe: ActivityNotFoundException) {
                }
            }
        }
    }

    fun permissionDialog() {

        val layoutInflater = LayoutInflater.from(this)
        val view: View = layoutInflater.inflate(R.layout.view_permission_dialog, null)
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setView(view)
        val alertDialog: android.app.AlertDialog = builder.create()
        alertDialog.setCancelable(false)

        val btnPermissions = view.findViewById<TextView>(R.id.btnPermissions)
        val btnBatterySaver = view.findViewById<TextView>(R.id.btnBatterySaver)
        val btnAutoStart = view.findViewById<TextView>(R.id.btnAutoStart)
        val btnCancel = view.findViewById<TextView>(R.id.btnCancel)

        btnPermissions.setOnClickListener {
            onDisplayPopupPermission()
        }

        btnBatterySaver.setOnClickListener {
            handleBatteryOptimization()
        }

        btnAutoStart.setOnClickListener {
            handleAutoStartPermission()
        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun popUpPermission() {
        Log.e("popup", isXiaomi().toString());
//        if (!canDrawOverlayViews() && isXiaomi()) {
        if (!canDrawOverlayViews()) {
            //permission not granted
            Log.e("canDrawOverlayViews", "-No-");

            permissionDialog()
        }
    }

    fun isXiaomi(): Boolean {
        return "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    }

    fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            /** check if we already  have permission to draw over other apps  */
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission  */
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                /** request permission via start activity for result  */
                startActivityForResult(intent, 999)
            }
        }
    }

    fun canDrawOverlayViews(): Boolean {
        val con: Context = this
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("canDrawOverlayViews", Settings.canDrawOverlays(con).toString());
                Settings.canDrawOverlays(con)
            } else {
                true
            }
        } catch (e: NoSuchMethodError) {
//            canDrawOverlaysUsingReflection(con)
            return false
        }
    }


    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Settings.canDrawOverlays(context)
        } else {
            if (Settings.canDrawOverlays(context)) return true
            try {
                val mgr: WindowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        ?: return false
                //getSystemService might return null
                val viewToAdd = View(context)
                val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
                    0,
                    0,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
                )
                viewToAdd.layoutParams = params
                mgr.addView(viewToAdd, params)
                mgr.removeView(viewToAdd)
                return true
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            false
        }
    }
//    fun canDrawOverlaysUsingReflection(context: Context): Boolean {
//        return try {
//            val manager: AppOpsManager =
//                context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//            val clazz: Class<*> = AppOpsManager::class.java
//            val dispatchMethod: Method = clazz.getMethod(
//                "checkOp", *arrayOf<Class<*>?>(
//                    Int::class.javaPrimitiveType,
//                    Int::class.javaPrimitiveType,
//                    String::class.java
//                )
//            )
//            //AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
//            val mode = dispatchMethod.invoke(
//                manager,
//                arrayOf<Any>(
//                    24,
//                    Binder.getCallingUid(),
//                    context.applicationContext.packageName
//                )
//            ) as Int
//            AppOpsManager.MODE_ALLOWED === mode
//        } catch (e: Exception) {
//            false
//        }
//    }

    private fun onDisplayPopupPermission() {
        if (!isXiaomi()) {
            // Otherwise jump to application details
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }else {
            try {
                // MIUI 8
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity"
                )
                localIntent.putExtra("extra_pkgname", packageName)
                startActivity(localIntent)
                return
            } catch (ignore: java.lang.Exception) {
            }
            try {
                // MIUI 5/6/7
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
                localIntent.putExtra("extra_pkgname", packageName)
                startActivity(localIntent)
                return
            } catch (ignore: java.lang.Exception) {
            }
            // Otherwise jump to application details
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }


//    private fun isMIUI(): Boolean {
//        val device = Build.MANUFACTURER
//        if (device == "Xiaomi") {
//            try {
//                val prop = Properties()
//                prop.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
//                return prop.getProperty(
//                    "ro.miui.ui.version.code",
//                    null
//                ) != null || prop.getProperty(
//                    "ro.miui.ui.version.name",
//                    null
//                ) != null || prop.getProperty("ro.miui.internal.storage", null) != null
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        return false
//    }

    private fun showRatingFragment() {
        hideAddall()
        invisReminderOptions()
//        markFollowButton()
        txtHeader.text = "Rating"

        val dialog = RatingDialogFragment.newInstance("", "")
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, RatingDialogFragment.TAG)

//        val transaction = manager.beginTransaction()
//        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//        transaction.replace(R.id.fragment_container,fragmentRating,"FragmentRating")
//        transaction.addToBackStack("FragmentRating")
//        transaction.commit()
//        activeFragment = fragmentRating
    }

    fun showInformationWithCat(category: String) {

        txtHeader.text = "Information"
        val fragment = FragmentInformation()
        val bundle = Bundle()
        bundle.putString("category", category)
        fragment.arguments = bundle
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container, fragment, "FragmentInformation")
        transaction.addToBackStack("FragmentInformation")
        transaction.commit()
        activeFragment = fragment

//            hideAddall()
//            markInformationButton()
//            invisReminderOptions()
//            showInformationOptions()

    }

    private fun markPillButton() {
        btnPill.setIconResource(R.drawable.pill_selected)
        btnVaccine.setIconResource(R.drawable.vaccine_normal)
        btnAppointment.setIconResource(R.drawable.appointment_normal)
    }

    private fun markVaccineButton() {
        btnPill.setIconResource(R.drawable.pill_normal)
        btnVaccine.setIconResource(R.drawable.vaccine_selected)
        btnAppointment.setIconResource(R.drawable.appointment_normal)
    }

    private fun markAppointmentButton() {
        btnPill.setIconResource(R.drawable.pill_normal)
        btnVaccine.setIconResource(R.drawable.vaccine_normal)
        btnAppointment.setIconResource(R.drawable.appointment_selected)
    }

    private fun markDashboardButton() {
        ivInformation.isEnabled = true
        ivInformation.setImageResource(R.drawable.info)
        imageButton3.setImageResource(R.drawable.reminder_icon)
        imageButton4.setImageResource(R.drawable.home_icon_selected)
    }

    private fun markReminderButton() {
        ivInformation.isEnabled = true
        ivInformation.setImageResource(R.drawable.info)
        imageButton3.setImageResource(R.drawable.reminder_icon_selected)
        imageButton4.setImageResource(R.drawable.home_icon)
    }

    private fun markInformationButton() {
        ivInformation.isEnabled = false
        ivInformation.setImageResource(R.drawable.info)
        imageButton3.setImageResource(R.drawable.reminder_icon)
        imageButton4.setImageResource(R.drawable.home_icon)
    }

    fun showAddAppointment() {
        addIconAppointment.visibility = View.VISIBLE
        addIconAppointment.isEnabled = true
        addIconVaccine.visibility = View.INVISIBLE
        addIconPill.visibility = View.INVISIBLE
        addIconAll.visibility = View.INVISIBLE
    }

    fun showAddVaccine() {
        addIconAppointment.visibility = View.INVISIBLE
        addIconVaccine.visibility = View.VISIBLE
        addIconVaccine.isEnabled = true
        addIconPill.visibility = View.INVISIBLE
        addIconAll.visibility = View.INVISIBLE
    }

    fun showAddPill() {
        addIconAppointment.visibility = View.INVISIBLE
        addIconVaccine.visibility = View.INVISIBLE
        addIconPill.visibility = View.VISIBLE
        addIconPill.isEnabled = true
        addIconAll.visibility = View.INVISIBLE
    }

    fun hideAddall() {
        addIconAppointment.visibility = View.INVISIBLE
        addIconVaccine.visibility = View.INVISIBLE
        addIconPill.visibility = View.INVISIBLE
        addIconAll.visibility = View.VISIBLE
    }

    private fun showReminderOptions() {
        appBar.setBackgroundColor(resources.getColor(R.color.blue_header))
        groupReminder.visibility = View.VISIBLE
        groupLines.visibility = View.VISIBLE
        frameLayout.layoutParams.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            200.toFloat(),
            getResources().getDisplayMetrics()
        ).toInt()
        showPillListFragment()
    }

    private fun showInformationOptions() {
        appBar.setBackgroundColor(resources.getColor(R.color.blue_header))
        groupInformation.visibility = View.VISIBLE
        groupLines.visibility = View.VISIBLE
        frameLayout.layoutParams.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            200.toFloat(),
            getResources().getDisplayMetrics()
        ).toInt()
    }

    private fun invisReminderOptions() {
        appBar.setBackgroundColor(resources.getColor(R.color.dashboardAppBarColor))
        groupReminder.visibility = View.GONE
        groupLines.visibility = View.GONE
        frameLayout.layoutParams.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100.toFloat(),
            getResources().getDisplayMetrics()
        ).toInt()
        btnPill.setIconResource(R.drawable.pill_normal)
        btnVaccine.setIconResource(R.drawable.vaccine_normal)
        btnAppointment.setIconResource(R.drawable.appointment_normal)
    }

    private fun invisInformationOptions() {
        appBar.setBackgroundColor(resources.getColor(R.color.dashboardAppBarColor))
        groupInformation.visibility = View.GONE
        groupLines.visibility = View.GONE
        frameLayout.layoutParams.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100.toFloat(),
            getResources().getDisplayMetrics()
        ).toInt()
        btnInstructions.setIconResource(R.drawable.info)
        btnFactors.setIconResource(R.drawable.info)
        btnMistake.setIconResource(R.drawable.info)
    }


    private fun showDashboardFragment() {
        hideAddall()
        invisReminderOptions()
        invisInformationOptions()
        markDashboardButton()
        txtHeader.text = "Dashboard"
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container, fragmentDashboard, "FragmentDashboard")
//        transaction.addToBackStack(null)
        transaction.commit()
        activeFragment = fragmentDashboard
    }

    private fun showAppointmentListFragment() {
        markAppointmentButton()
        txtHeader.text = "Appointment"
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(
            R.id.fragment_container,
            fragmentAppointmentList,
            "FragmentAppointmentList"
        )
        transaction.addToBackStack("FragmentAppointmentList")
        transaction.commit()
        activeFragment = fragmentAppointmentList
    }

    private fun showVaccineListFragment() {
        markVaccineButton()
        txtHeader.text = "Vaccine"
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container, fragmentVaccineList, "FragmentVaccineList")
        transaction.addToBackStack("FragmentVaccineList")
        transaction.commit()
        activeFragment = fragmentVaccineList

    }

    private fun showPillListFragment() {
//        hideAddall()
        showAddPill()

        markPillButton()
        txtHeader.text = "Pill"
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container, fragmentPillList, "FragmentPillList")
        transaction.addToBackStack("FragmentPillList")
        transaction.commit()
        activeFragment = fragmentPillList

    }

    private fun showCreateAppointmentFragment() {
//        val transaction = manager.beginTransaction()
//        val fragment = FragmentCreateAppointment()
//        transaction.replace(R.id.fragment_container,fragment,"FragmentCreateAppointment")
//        transaction.addToBackStack("FragmentCreateAppointment")
//        transaction.commit()
        val fm = supportFragmentManager
        fragmentCreateAppointment = FragmentCreateAppointment()

        fragmentCreateAppointment.show(fm, "FragmentCreateAppointment")
    }


    private fun showCreateVaccineFragment() {
//        Log.e("milisecond print",System.currentTimeMillis().toString())
//        val transaction = manager.beginTransaction()
//        transaction.replace(R.id.fragment_container,fragmentCreateVaccine,"FragmentCreateVaccine")
//        transaction.addToBackStack("FragmentCreateVaccine")
//        transaction.commit()
//        activeFragment = fragmentCreateVaccine
        val fm = supportFragmentManager
        fragmentCreateVaccine = FragmentCreateVaccine()

        fragmentCreateVaccine.show(fm, "FragmentCreateAppointment")
    }

    private fun showCreatePillTableFragment() {
        val fm = supportFragmentManager
        val fragmentAddPillTable = FragmentAddPillTable()

        fragmentAddPillTable.show(fm, "FragmentAddPillTable")
    }

    private fun showAddPillFragment() {
        Thread {
            val tables = database.tableDao().tables()
            if (tables.isNotEmpty()) {
                val pop = FragmentAddPillFirst()
                val bundle = Bundle()
                bundle.putInt("table_id", tables[0].localId)
                Log.e("table Id Send", tables[0].localId.toString())
                pop.arguments = bundle
                val fm = supportFragmentManager
//                Log.e("table_id",tableList[pos].id.toString())
//                Log.e("pos",pos.toString())
                pop.show(fm, "addPill")
            }
        }.start()
    }

    fun showPillList(code: String, currentDate: String, currentDay: String) {
        showBack()
        val transaction = manager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("code", code)
        bundle.putString("date", currentDate)
        bundle.putString("day", currentDay)
        fragmentDashboardMedList.arguments = bundle
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(
            R.id.fragment_container,
            fragmentDashboardMedList,
            "FragmentDashboardMedList"
        )
        transaction.addToBackStack("FragmentDashboardMedList")
        transaction.commit()
        activeFragment = fragmentDashboardMedList
    }

    private fun showBack() {
        Log.e("showback", "clicked")
        backDashData.visibility = View.VISIBLE
        userProfile.visibility = View.GONE
    }

    fun showProPic() {
        backDashData.visibility = View.INVISIBLE
        userProfile.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
//        inst = this;
    }

    fun startAlarm(
        c: Calendar,
        type: String,
        id: Int,
        reminderType: String,
        isDaily: Boolean,
        frequency: Int,
        title: String,
        time: String,
        task_id: Long
    ) {

        Log.e("startAlram", c.time.toString())
        Log.e("startAlram", "$type $time")
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("type", reminderType)
        intent.putExtra("title", title)
        intent.putExtra("time", time)
        val pendingIntent = PendingIntent.getBroadcast(this, task_id.toInt(), intent, 0)

//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1)
//        }

        if (type == "repeating") {
//            if (isDaily) {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,AlarmManager.INTERVAL_DAY, pendingIntent);
//            }
//            else{
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,AlarmManager.INTERVAL_DAY*(frequency+1), pendingIntent);
//            }
//            alarmManager.setExact(AlarmManager.RTC, c.timeInMillis, pendingIntent);

            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.timeInMillis,
                    pendingIntent
                );

//                val i = Intent(this, IsolatedActivity::class.java)
//                val pi = PendingIntent.getBroadcast(this, task_id.toInt(), i, 0)
//                val aci=  AlarmClockInfo(c.timeInMillis, pi)
//                alarmManager.setAlarmClock(aci,pendingIntent)

            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            }


        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.timeInMillis,
                    pendingIntent
                );

//                val i = Intent(this, IsolatedActivity::class.java)
//                val pi = PendingIntent.getBroadcast(this, task_id.toInt(), i, 0)
//                val aci=  AlarmClockInfo(c.timeInMillis, pi)
//                alarmManager.setAlarmClock(aci,pendingIntent)

            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent);
            }
        }

//        alarmManager!!.setExact(AlarmManager.RTC, c.timeInMillis, pendingIntent)
    }

    fun cancelAlarm(task_id: Long) {
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, task_id.toInt(), intent, 0)
        alarmManager.cancel(pendingIntent)
        Log.e("cancel alarm", "cancelAlarm: task_id $task_id")
    }

    fun callDatePicker(type: String) {
        flag = type
        val datePicker = DatePickerFragment()
        datePicker.show(supportFragmentManager, "date picker")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//        val name: String =  registerName.text.toString()
//        Log.e("date set",name)
//        var current = DateFormat.getDateInstance().format(c.getTime())
        val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val current = simpleDateFormat.format(c.time).toString()
//        var date = findViewById<EditText>(R.id.registerDob)
//        return c
        if (flag == "appointment") {
//            editText4.text = Editable.Factory.getInstance().newEditable(current)
            fragmentCreateAppointment.date = current
            fragmentCreateAppointment.finalCal?.set(Calendar.YEAR, year)
            fragmentCreateAppointment.finalCal?.set(Calendar.MONTH, month)
            fragmentCreateAppointment.finalCal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        } else if (flag == "vaccine") {
            fragmentCreateVaccine.date = current

        } else if (flag == "vacDetails") {
//            FragmentVaccineDetails. = current
            temp = current
        } else if (flag == "appDetails") {
            temp = current
        }
    }

    override fun onBackPressed() {
        if (activeFragment != fragmentDashboard) {
            supportFragmentManager.beginTransaction().remove(activeFragment)
            showDashboardFragment()
            showProPic()
        } else {
            val dialogBuilder = AlertDialog.Builder(this);
            dialogBuilder.setMessage("Do you want to quit ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { _, _ ->
                    finalBack()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Exit App")
            // show alert dialog
            alert.show()

        }
    }

    private fun finalBack() {
        showProPic()
        finish()
        finishAffinity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            fragmentAppointmentList.fetchData()
        } else if (requestCode == 200) {
            fragmentVaccineList.fetchData()
        }
    }
}