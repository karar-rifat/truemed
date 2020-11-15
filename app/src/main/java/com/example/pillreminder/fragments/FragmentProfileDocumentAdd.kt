package com.example.pillreminder.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.esafirm.imagepicker.features.ImagePicker
import com.example.pillreminder.R
import com.example.pillreminder.activities.ProfileActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.AlarmHelper
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.KeyboardUtils
import com.example.pillreminder.models.Document
import com.example.pillreminder.service.DataPushService
import com.facebook.accountkit.internal.AccountKitController
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_profile_document_add.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentProfileDocumentAdd : Fragment() {
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE_GALLERY = 1001
    var image_path: String? = null
    var user_id: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_document_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        btnDocUploadCamera.setOnClickListener {
//            checkAndOpenCamera()
//        }

        user_id = Helper.user_id

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        Thread {
            val user = database.userDao().getUserByEmail((activity as ProfileActivity).user_id!!)
            (activity as ProfileActivity).runOnUiThread {
                tvName.text = user.name
                tvEmail.text = user.email

                if (user.photo != "") {
//                    var imgFile = File(user.photo);
//                    if(imgFile.exists())
//                    {
//                        proPicEdit.setImageURI(Uri.fromFile(imgFile))
//                    }
                    Log.e("doc", "file from base64")
                    //decode base64 string to image
                    val imageBytes = Base64.decode(user.photo, Base64.DEFAULT)
                    val decodedImage: Bitmap =
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    proPicEdit.setImageBitmap(decodedImage)
                }

                database.close()
            }
        }.start()

        imageButton5.setOnClickListener {
            (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
        }
        btnDocUploadGallery.setOnClickListener {
            checkAndOpenGallery()
        }
        imageView21.setOnClickListener {
            try {
                validateAndUpload()
            } catch (ex: Exception) {
            }
        }

        KeyboardUtils.addKeyboardToggleListener(
            activity!!,
            object : KeyboardUtils.SoftKeyboardToggleListener {
                override fun onToggleSoftKeyboard(isVisible: Boolean) {
                    Log.e("keyboard", "keyboard visible: $isVisible")
//                btnDocUploadGallery.requestFocus()
                    if (isVisible) {
                        if (scrollViewAddDoc != null)
                            try {
//                        scrollViewAddDoc.scrollTo(0, scrollViewAddDoc.bottom)
//                        scrollViewAddDoc.fullScroll(ScrollView.FOCUS_DOWN)
                                editText40.requestFocus()
                            }catch (ex:Exception){}
                    }

                }
            })

//        layoutUserInfo.viewTreeObserver.addOnGlobalLayoutListener(OnGlobalLayoutListener {
//            val heightDiff: Int = layoutUserInfo.rootView.height - layoutUserInfo.height
//            if (heightDiff > 200) {
//                Log.e("MyActivity", "keyboard opened")
//            } else {
//                Log.e("MyActivity", "keyboard closed")
//            }
//        })

    }

    fun Fragment.setFocus() {
        view.let {
            it?.requestFocus()
        }
    }

    private fun validateAndUpload() {

        var title = editText40.text.toString()
        val database = Room.databaseBuilder(
            AccountKitController.getApplicationContext(),
            AppDb::class.java!!,
            "pill_reminder"
        ).fallbackToDestructiveMigration().build()

        if (title == "" || image_path == null) {
            Toast.makeText(context, "Validation Error!", Toast.LENGTH_LONG).show()
        } else {
            Thread {
                //get current datetime
                val simpleDateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
                val currentDateTime = simpleDateFormat.format(Date()).toString()

                var base64: String? = null
                val imgFile = File(Uri.parse(image_path).path)

                if (imgFile.exists()) {
//                    var inputStream: InputStream? =
//                        null //You can get an inputStream using any IO API
//                    inputStream = FileInputStream(imgFile)
//                    val buffer = ByteArray(8192)
//                    var bytesRead: Int
//                    val output = ByteArrayOutputStream()
//                    val output64 = Base64OutputStream(output, Base64.NO_WRAP)
//                    try {
//                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                            output64.write(buffer, 0, bytesRead)
//                        }
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                    output64.close()
//                    base64 = output.toString()

                    try {
                        val baos = ByteArrayOutputStream()
                        val bitmap = BitmapFactory.decodeFile(Uri.parse(image_path).path)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                        val imageBytes = baos.toByteArray()
                        base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                    }catch (ex:Exception){}

                }
                Log.e("documents", "base64 : " + base64)

                val doc = Document(
                    title,
                    image_path.toString(),
                    base64!!,
                    "image",
                    user_id!!,
                    currentDateTime,
                    false,
                    user_id!!,
                    user_id!!
                )

                try {
                    database.documentDao().saveDocument(doc)
                }catch (ex:Exception){}
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
//                    (activity as ProfileActivity).fragmentProfileDocumentList.getDocuments()
//                    (activity as ProfileActivity).fragmentProfileDocumentList.documentAdapter.notifyDataSetChanged()

                    (activity as ProfileActivity).supportFragmentManager.popBackStackImmediate()
                }
            }.start()
            Toast.makeText(context, "Document Added successfully", Toast.LENGTH_LONG).show()
        }


    }

    private fun checkAndOpenCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(context!!, Manifest.permission.CAMERA)
                == PermissionChecker.PERMISSION_DENIED ||
                checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PermissionChecker.PERMISSION_DENIED
            ) {
                //permission was not enabled
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granted
                openCamera()
            }
        } else {
            //system os is < marshmallow
            openCamera()
        }
    }

    private fun checkAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PermissionChecker.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE_GALLERY);
            } else {
                //permission already granted
//                pickImageFromGallery();

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .start(context!!, this)
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(context!!, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                image_path = result.uri.toString()
                Log.e("Image", "uri =" + result.uri)
//              Glide.with(this).load(result.uri).into(capturedImage);
                capturedImage.setImageURI(Uri.parse(image_path))

            }
        }
    }

    private fun pickImageFromGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, IMAGE_PICK_CODE)
        ImagePicker.create(this) // Activity or Fragment
            .single()
            .start();
    }

    private fun openCamera() {
//        ImagePicker.create(this) // Activity or Fragment
//            .returnMode(ReturnMode.CAMERA_ONLY)
//            .start();

        ImagePicker
            .cameraOnly()
            .start(activity)

//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, "New Picture")
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
//        image_uri = (activity as ProfileActivity).contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        //camera intent
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
//        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
//            // Get a list of picked images
////            System.out.println(data)
//            //var images = ImagePicker.getImages(data)
//            // or get a single image only
//            var image = ImagePicker.getFirstImageOrNull(data)
//            Log.e("image path",image.path)
//            var imgFile = File(image.path);
//            if(imgFile.exists())
//            {
//                image_path = image.path
//                capturedImage.setImageURI(Uri.fromFile(imgFile))
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
//            //set image captured to image view
//            capturedImage.setImageURI(data?.data)
//        }
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
//            //image_uri = data?.data
//            capturedImage.setImageURI(data?.data)
//        }
//    }
}