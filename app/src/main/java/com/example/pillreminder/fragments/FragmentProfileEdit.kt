package com.example.pillreminder.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.PermissionChecker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.esafirm.imagepicker.features.ImagePicker
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.KeyboardUtils
import com.example.pillreminder.models.User
import com.example.pillreminder.network.repository.UserRepository
import com.example.pillreminder.network.request.RegisterRequest
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.facebook.accountkit.internal.AccountKitController.getApplicationContext
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_add_pill_first.*
import kotlinx.android.synthetic.main.fragment_add_pill_first.spNumOfDayAdd
import kotlinx.android.synthetic.main.fragment_pill_details_first.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import kotlinx.android.synthetic.main.fragment_profile_edit.proPicEdit
import kotlinx.android.synthetic.main.fragment_profile_edit.tvEmail
import kotlinx.android.synthetic.main.fragment_profile_information.*
import kotlinx.android.synthetic.main.fragment_profile_information.tvName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.text.DateFormat
import java.util.*

class FragmentProfileEdit : DialogFragment(), Validator.ValidationListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE_GALLERY = 1001
    var image_path: String? = null
    var base64:String?=null

    var bloodGroup:String?=null
    var spBGAdapter: ArrayAdapter<CharSequence>? = null

    lateinit var mSelected : List<Uri> ;

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        genderEditText = gender[position]
        Log.e("gender",genderEditText!!)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

    lateinit var spinner1 : Spinner
    lateinit var user : User
    var gender = arrayOf("Male","Female")
    @NotEmpty
    private var nameEditText: EditText? = null
    @NotEmpty
    private var dobEditText: EditText? = null
    @NotEmpty
    private var genderEditText: String? = null
    @NotEmpty
    private var heightEditText: EditText? = null
    @NotEmpty
    private var weightEditText: EditText? = null
    @NotEmpty
    private var bpEditText: EditText? = null
    private var proPicEditText: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container,false)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameEditText = view.findViewById(R.id.editText42)
        dobEditText = view.findViewById(R.id.editText43)
//        genderEditText = view.findViewById(R.id.editText44)
        heightEditText = view.findViewById(R.id.editText45)
        weightEditText = view.findViewById(R.id.editText46)
        bpEditText = view.findViewById(R.id.editText48)
//        proPicEditText = view.findViewById(R.id.registerPassword)

        imageView27.setOnClickListener {
            dialog?.dismiss()
        }
        editText43.setOnClickListener {
            callDatePicker()
        }
        setData()
        val validator = Validator(this);
        validator.setValidationListener(this);

        button20.setOnClickListener {
            validator.validate()
        }
        proPicEdit.setOnClickListener {
            checkAndOpenGallery()
        }

        btnAddImage.setOnClickListener {
            checkAndOpenGallery()
        }

        spBGAdapter = ArrayAdapter.createFromResource(context!!,R.array.array_sp_blood_group, android.R.layout.simple_spinner_item)
        spBGAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spBloodGroup.adapter=spBGAdapter

        bloodGroupListener()

        KeyboardUtils.addKeyboardToggleListener(activity!!, object : KeyboardUtils.SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                Log.e("keyboard", "keyboard visible: $isVisible")

                if (isVisible) {
                    if (scrollViewEditProfile != null)
//                        scrollViewEditProfile.scrollTo(0, scrollViewEditProfile.bottom)
                        scrollViewEditProfile.fullScroll(ScrollView.FOCUS_DOWN)
                    setFocus()
                }
            }
        })

        hideKeyBoard()
    }

    fun Fragment.setFocus(){
        view.let {
            it?.requestFocus()
        }
    }

    fun Fragment.hideKeyBoard(){
        view.let {
            activity?.hideKeyBoard(it)
        }
    }
    fun Activity.hideKeyBoard(){
        hideKeyBoard(currentFocus?:View(this))
    }
    fun Context.hideKeyBoard(view:View?){
        val inputMethodManager= getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken,0)
    }

    private fun bloodGroupListener() {
        spBloodGroup.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val selected: String =spBloodGroup.selectedItem.toString()

                bloodGroup=selected
                Log.e("blood Group", "onItemSelected: $selected")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setData() {
        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        Thread{
            user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
//            database.appointmentDao().appointments().forEach {
//                Log.i("appointment",""" Title - ${it.title}""")
//            }
            (activity as ProfileActivity).runOnUiThread {
                //(activity as ProfileActivity).fragmentProfileOptions.fragmentProfileInformation.textView47.text = user.dob
                tvNameEdit.text=user.name
                tvEmail.text = user.email
                editText42.setText(user.name)
                editText43.setText(user.dateOfBirth)
                editText45.setText(user.height)
                editText46.setText(user.weight)
                editText48.setText(user.bloodPressure)
                if(!user.photo.isNullOrEmpty()){
//                    proPicEdit.setImageURI(Uri.parse(user.photo))

                    Log.e("doc", "file from base64")
                    //decode base64 string to image
                    val imageBytes = Base64.decode(user.photo, Base64.DEFAULT)
                    val decodedImage: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    proPicEdit.setImageBitmap(decodedImage)
                }
                if(user.gender=="Female"){
                    gender = arrayOf("Female","Male")
                }

                val bloodGroupPos: Int = spBGAdapter!!.getPosition(user.bloodGroup)
                spBloodGroup.setSelection(bloodGroupPos)

                setSpinner()
                database.close()
//                ((activity as DashboardActivity).fragmentVaccineList.vaccines as ArrayList).removeAt(position!!)
//                (activity as DashboardActivity).fragmentVaccineList.vaccineRecyclerAdapter.notifyDataSetChanged()
//                dialog.dismiss()
            }
        }.start()
    }

    private fun setSpinner() {
        spinner1 = this.spinner3
        spinner1!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, gender)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner3!!.setAdapter(aa)
    }

    private fun checkAndOpenGallery(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (PermissionChecker.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE_GALLERY);
            }
            else{
                //permission already granted
                pickImageFromGallery();
            }
        }
        else{
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
    }

    private fun pickImageFromGallery(){
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, IMAGE_PICK_CODE)

        ImagePicker.create(this) // Activity or Fragment
            .single()
            .start();

//        Matisse.from(this)
//        .choose(MimeType.ofAll())
//        .countable(true)
//        .maxSelectable(9)
////        .addFilter( GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//        .thumbnailScale(0.85f)
//        .imageEngine( GlideEngine())
//        .forResult(IMAGE_PICK_CODE);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    //openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE_GALLERY -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);


        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
//            System.out.println(data)
            //var images = ImagePicker.getImages(data)
            // or get a single image only
            var image = ImagePicker.getFirstImageOrNull(data)
            Log.e("image path",image.path)
            var imgFile = File(image.path);

            if(imgFile.exists())
            {
                image_path = image.path
                proPicEdit.setImageURI(Uri.fromFile(imgFile))

                var inputStream: InputStream? = null //You can get an inputStream using any IO API
                inputStream = FileInputStream(imgFile)
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
                Log.e("documents","base64 : "+base64)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            //set image captured to image view
//            proPic.setImageURI(image_uri)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
//            mSelected = Matisse.obtainResult(data);
//            Log.d("Matisse", "mSelected: " + mSelected);

            //image_uri = data?.data
            //proPic.setImageURI(data?.data)
        }

//         var cursor : Cursor? = null;
//            try {
//                var proj = { MediaStore.Images.Media.DATA };
//                cursor = context!!.getContentResolver().query(image_uri, (proj as Array<String>)!!, null, null, null);
//                var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                Log.e("new",cursor.getString(column_index))
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
    }

    fun callDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)
        Log.e("call Date Picker","Entered")
        val datePicker = DatePickerDialog(context!!,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var c = Calendar.getInstance()
                c.set(Calendar.YEAR,year)
                c.set(Calendar.MONTH,month)
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                var current = DateFormat.getDateInstance().format(c.getTime())
                Log.e("on Date Set",current)
                editText43.text = Editable.Factory.getInstance().newEditable(current)
            },year,month,date)
        datePicker.show()
//        datePicker.show(fragmentManager!!.beginTransaction(), "date picker")
    }
    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        for (error in errors) {
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
        val database = databaseBuilder(getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()
//        Log.i("appointment","""${finalCal.timeInMillis}""")
//        val task_id = System.currentTimeMillis().toInt()
        val custom = activity as ProfileActivity


        user.name = editText42.text.toString()
        user.dateOfBirth = editText43.text.toString()
        user.gender = genderEditText
        user.bloodGroup = bloodGroup
        user.height = editText45.text.toString()
        user.weight = editText46.text.toString()
        user.bloodPressure = editText48.text.toString()
        user.synced=false
        if (base64!=null) {
            user.photo = base64
        }

//        if(image_path!=null){
//            var imgFile = File(image_path);
//            if(imgFile.exists())
//            {
//                Log.e("user edit ", "image file doesnot exists")
//                custom.fragmentProfileOptions.fragmentProfileInformation.proPic.setImageURI(Uri.fromFile(imgFile))
//
//                var inputStream: InputStream? = null //You can get an inputStream using any IO API
//                inputStream = FileInputStream(imgFile)
//                val buffer = ByteArray(8192)
//                var bytesRead: Int
//                val output = ByteArrayOutputStream()
//                val output64 = Base64OutputStream(output, Base64.NO_WRAP)
//                try {
//                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                        output64.write(buffer, 0, bytesRead)
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                output64.close()
//                base64 = output.toString()
//                Log.e("user edit base64 ", base64)
//                user.photo = base64
//            }
            //setting data in information fragment
//                    custom.fragmentProfileOptions.fragmentProfileInformation.imageView26.invalidate()
//        }


//        if(image_path!=null){
//            user.photo = image_path
//        }

        Thread{
            custom.runOnUiThread {
                //setting data in information fragment

                val intentFetch=Intent("fetch-data")
                intentFetch.putExtra("isDataFetched",true)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intentFetch)

//                custom.fragmentProfileOptions.fragmentProfileInformation.tvName.text = user.name
//                custom.fragmentProfileOptions.fragmentProfileInformation.textView47.text = user.dateOfBirth
//                custom.fragmentProfileOptions.fragmentProfileInformation.textView49.text = user.gender
//                custom.fragmentProfileOptions.fragmentProfileInformation.tvBloodGroup.text = user.bloodGroup
//                custom.fragmentProfileOptions.fragmentProfileInformation.textView51.text = "${user.height} cm"
//                custom.fragmentProfileOptions.fragmentProfileInformation.textView53.text = "${user.weight} kg"
//                custom.fragmentProfileOptions.fragmentProfileInformation.textView55.text = user.bloodPressure
//                if(image_path!=null){
//                    val imgFile = File(image_path);
//                    if(imgFile.exists())
//                    {
//                        Log.e("user edit ", "image file doesnot exists")
//                        custom.fragmentProfileOptions.fragmentProfileInformation.proPic.setImageURI(Uri.fromFile(imgFile))
//
//                    }
//                    //setting data in information fragment
////                    custom.fragmentProfileOptions.fragmentProfileInformation.imageView26.invalidate()
//                }

            }
            val id = database.userDao().update(user)
            database.close()

            GlobalScope.launch(Dispatchers.IO) {
//                        DataPushRepo().pushUser(token!!, listOf(user))
                UserRepository().updateUser(Helper.token!!, RegisterRequest(user.email!!,user.name!!,user.email!!,user.password!!,user.phoneNumber,"api"))
            }

            updateUserInService()

            dialog?.dismiss()

        }.start()
        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
    }

    fun updateUserInService(){
        //update data to server with background service
        if (Helper.isConnected(context!!)) {
            val token= Helper.token

            Log.e("user add ", "starting push service user")
            val service = Intent(context, DataPushService::class.java)
            service.putExtra("type", "user")
            service.putExtra("token", token)
            context!!.startService(service)
        }else{
            AlarmHelper().startAlarmForSync(context!!,"user")
            Log.e("user add ", "alarm for sync user")
        }
    }
}