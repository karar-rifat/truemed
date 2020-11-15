package com.example.pillreminder.fragments

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Settings
import com.facebook.accountkit.internal.AccountKitController
import kotlinx.android.synthetic.main.fragment_profile_settings.*
import kotlin.properties.Delegates

class FragmentProfileSettings : Fragment(){

    var alarmStatus:Boolean?=null
    var isToggleSet:Boolean=false
    var fragmentProfileInformation: FragmentProfileInformation = FragmentProfileInformation()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_settings,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAlarmButton()
        setSnoozeTime()

        imageView28.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }
        button8.setOnClickListener {
            showProfileInformationFragment()
        }
        button14.setOnClickListener {
            showProfileManageAcc()
        }

        swAlarmOnOff.setOnCheckedChangeListener { _, isChecked ->
            Log.e("settings","toggle isToggleSet $isToggleSet isChecked $isChecked")
            if (!isToggleSet)
                toggleAlarm()
            isToggleSet=false
        }

        btnNotifications.setOnClickListener {
            val fragment = FragmentNotification()
            val transaction = (activity as ProfileActivity).manager.beginTransaction()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction.replace(R.id.fragment_container_profile,fragment,"FragmentNotification")
            transaction.addToBackStack("FragmentNotification")
            transaction.commit()
        }

        btnPermissions.setOnClickListener {
            popUpPermission()
//            onDisplayPopupPermission()
        }

        btnSnooze.setOnClickListener {
            var database:AppDb?=null
            var snooze:Settings?=null
            Thread {
                database = Room.databaseBuilder(
                    AccountKitController.getApplicationContext(),
                    AppDb::class.java!!,
                    "pill_reminder"
                ).fallbackToDestructiveMigration().build()
                snooze = database!!.settingsDao().getSpecificSettings("snooze_time")
            }.start()

                val popUpMenu = PopupMenu(context, btnSnooze)
                popUpMenu.menuInflater.inflate(R.menu.snooze_time_list, popUpMenu.menu)

                popUpMenu.setOnMenuItemClickListener { menu ->
                    when (menu!!.itemId) {
                        R.id.snooze_off -> {
                            btnSnooze.text = "SNOOZE TIME Off"
                            snooze!!.status = false
                            snooze!!.description = "0"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()

                        }
                        R.id.snooze_5m -> {
                            btnSnooze.text = "SNOOZE TIME 5 Minutes"
                            snooze!!.status = true
                            snooze!!.description = "5"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()
                        }
                        R.id.snooze_10m -> {
                            btnSnooze.text = "SNOOZE TIME 10 Minutes"
                            snooze!!.status = true
                            snooze!!.description = "10"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()
                        }
                        R.id.snooze_15m -> {
                            btnSnooze.text = "SNOOZE TIME 15 Minutes"
                            snooze!!.status = true
                            snooze!!.description = "15"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()
                        }
                        R.id.snooze_20m -> {
                            btnSnooze.text = "SNOOZE TIME 20 Minutes"
                            snooze!!.status = true
                            snooze!!.description = "20"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()
                        }
                        R.id.snooze_25m -> {
                            btnSnooze.text = "SNOOZE TIME 25 Minutes"
                            snooze!!.status = true
                            snooze!!.description = "25"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()
                        }
                        R.id.snooze_30m -> {
                            btnSnooze.text = "SNOOZE TIME 30 Minutes"
                            snooze!!.status = true
                            snooze!!.description = "30"
                            Thread{
                                database!!.settingsDao().update(snooze!!)
                            }.start()
                            Toast.makeText(context,"Snooze Time Updated",Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                popUpMenu.show()
                database!!.close()
        }

        val passCode= SharedPrefHelper(context!!).getInstance().getPassCode()
        if(!passCode.isNullOrEmpty()){
            btnPassCode.text="Disable Passcode"
        }
        btnPassCode.setOnClickListener {
            val transaction =  (activity as ProfileActivity).manager.beginTransaction()
            val fragment = FragmentPassCode()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction.replace(R.id.fragment_container_profile,fragment,"FragmentPassCode")
            transaction.addToBackStack("FragmentPassCode")
            transaction.commit()
        }

        btnPrivacy.setOnClickListener {
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

        btnTerms.setOnClickListener {
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
    }

    private fun toggleAlarm() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            val alarm = database.settingsDao().getSpecificSettings("alarm")
            System.out.println(alarm)
            alarm.status = alarm.status != true
            database.settingsDao().update(alarm)
            if(alarm!=null && alarm.status==true){
                (activity as ProfileActivity).runOnUiThread {
                }
            }
            else{
                (activity as ProfileActivity).runOnUiThread {
                }
            }
        }.start()
        database.close()
        Toast.makeText(context,"Alarm status changed",Toast.LENGTH_SHORT).show()
    }

    private fun setAlarmButton() {

        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        Thread{
            val alarm = database.settingsDao().getSpecificSettings("alarm")
//            System.out.println(alarm)
            if(alarm!=null && alarm.status==true){
                (activity as ProfileActivity).runOnUiThread {
                    if (!swAlarmOnOff.isChecked)
                        isToggleSet=true
                    swAlarmOnOff.isChecked=true
                    database.close()
                }
            }else{
                (activity as ProfileActivity).runOnUiThread {
                    if (swAlarmOnOff.isChecked)
                        isToggleSet=true
                    swAlarmOnOff.isChecked=false
                    database.close()
                }
            }
        }.start()
    }

    private fun setSnoozeTime() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
        Thread{
            val snooze = database.settingsDao().getSpecificSettings("snooze_time")
//            System.out.println(alarm)
            if(snooze!=null && snooze.status==true){
                (activity as ProfileActivity).runOnUiThread {
                    btnSnooze.text = "SNOOZE TIME ${snooze.description} Minutes"
                }
            }else{
                (activity as ProfileActivity).runOnUiThread {
                    btnSnooze.text = "SNOOZE TIME OFF"
                }
            }
            database.close()
        }.start()
    }

    private fun showProfileInformationFragment(){
        Log.e("view24","clicked")
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        val fragment = FragmentProfileInformation()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragmentProfileInformation,"FragmentProfileInformation")
        transaction.addToBackStack("FragmentProfileInformation")
        transaction.commit()
    }
    private fun showProfileManageAcc(){
        Log.e("view24","clicked")
        val transaction =  (activity as ProfileActivity).manager.beginTransaction()
        val fragment = FragmentProfileManageAccount()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.fragment_container_profile,fragment,"FragmentProfileManageAccount")
        transaction.addToBackStack("FragmentProfileManageAccount")
        transaction.commit()
    }

    fun popUpPermission() {
            permissionDialog()
    }

    fun permissionDialog() {

        val layoutInflater = LayoutInflater.from(context!!)
        val view: View = layoutInflater.inflate(R.layout.view_permission_dialog, null)
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context!!)
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

    fun handleBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("battery saver:", "entered fun")
            val intent = Intent()
            val packageName = context!!.packageName
            val pm: PowerManager = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager

            if (isXiaomi()) {
                try {
                    intent.component = ComponentName(
                        "com.miui.powerkeeper",
                        "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
                    )
                    intent.putExtra("package_name", context!!.getPackageName())
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
                    startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
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
                    startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                }
            }
            else {

                startActivity(Intent(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS))
            }
        }
    }

    fun handleAutoStartPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e("battery saver:", "entered fun")
            val intent = Intent()
            val packageName = context!!.packageName
            val manufacturer = Build.MANUFACTURER
            val pm: PowerManager =  context!!.getSystemService(Context.POWER_SERVICE) as PowerManager

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
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
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

    fun isXiaomi(): Boolean {
        return "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    }
    fun canDrawOverlayViews(): Boolean {
        val con: Context = context!!
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("canDrawOverlayViews", android.provider.Settings.canDrawOverlays(con).toString());
                android.provider.Settings.canDrawOverlays(con)
            } else {
                true
            }
        } catch (e: NoSuchMethodError) {
//            canDrawOverlaysUsingReflection(con)
            return false
        }
    }
    private fun onDisplayPopupPermission() {
        if (!isXiaomi()) {
            // Otherwise jump to application details
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context!!.packageName, null)
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
                localIntent.putExtra("extra_pkgname", context!!.packageName)
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
                localIntent.putExtra("extra_pkgname", context!!.packageName)
                startActivity(localIntent)
                return
            } catch (ignore: java.lang.Exception) {
            }
            // Otherwise jump to application details
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context!!.packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }
}