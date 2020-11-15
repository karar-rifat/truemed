/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.pillreminder.fragments
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.pillreminder.MainActivity
import com.example.pillreminder.R
import com.example.pillreminder.activities.DashboardActivity
import com.example.pillreminder.activities.TutorialActivity
import com.example.pillreminder.database.AppDb
import com.example.pillreminder.helper.Helper
import com.example.pillreminder.helper.MockRegister
import com.example.pillreminder.helper.SessionManager
import com.example.pillreminder.helper.SharedPrefHelper
import com.example.pillreminder.models.Settings
import com.example.pillreminder.models.User
import com.example.pillreminder.network.repository.LoginRepo
import com.example.pillreminder.network.repository.MedicineRepo
import com.example.pillreminder.network.repository.SignUpRepo
import com.example.pillreminder.network.repository.UserRepository
import com.example.pillreminder.network.request.LoginRequest
import com.example.pillreminder.network.request.RegisterRequest
import com.example.pillreminder.network.response.MedicineResponse
import com.example.pillreminder.network.response.UserResponse
import com.example.pillreminder.service.DataFetchService
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.accountkit.internal.AccountKitController
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class FragmentLogin : Fragment() , Validator.ValidationListener {
    val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

    var token:String?=null

    @NotEmpty
    private var nameEditText: TextInputEditText? = null

//    @Password
    private var passwordEditText: TextInputEditText? = null

//    var user:User?=null
    var user:UserResponse?=null
    var type:String?=null
    var userId:String?=null
    var password:String?=null

    private lateinit var firebaseAuth: FirebaseAuth
    private  var firebaseUser: FirebaseUser?=null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    var isFirstLogin:Boolean=false
    //Facebook Callback manager
    var callbackManager: CallbackManager? = null

    lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progressDialog=ProgressDialog(context)
        progressDialog.setMessage("Please wait!")

        firebaseAuth = Firebase.auth
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("835479418396-rf3tpffi28sufik8k4rctl8oo5tjsids.apps.googleusercontent.com")
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)

        callbackManager = CallbackManager.Factory.create();

        ivFB.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email","public_profile"))
        }

        ivGoogle.setOnClickListener {
            signIn()
        }
        //facebook Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, object :FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })

        nameEditText = view.findViewById<TextInputEditText>(R.id.loginUser)
        passwordEditText = view.findViewById<TextInputEditText>(R.id.loginPassword)

        val validator = Validator(this);
        validator.setValidationListener(this);

        btnLogin.setOnClickListener {
//           mockLogin()
            validator.validate()
//            UserRepository().activeMQTest()
        }
        txtSignup.setOnClickListener {
//            Fragment fragment = new TaskStackBuilder()
            Log.e("sign up text","clicked")
            val fragmentManager = (activity as MainActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val fragment = FragmentRegister()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction?.replace(R.id.fragment_holder,fragment)
            transaction?.addToBackStack("FragmentForgotPassword")
            transaction?.commit()
        }
        txtForgotPass.setOnClickListener {
            val fragmentManager = (activity as MainActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val fragment = FragmentForgotPassword()
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction?.replace(R.id.fragment_holder,fragment)
            transaction?.addToBackStack("FragmentForgotPassword")
            transaction?.commit()
        }
    }

    fun mockLogin(){
//        GlobalScope.launch(Dispatchers.IO) {
////        LoginRepo().login(LoginRequest("sajibcse42@gmail.com","1234"),::messageHandler)
////          LoginRepo().login(LoginRequest("amatik2015@gmail.com", "123456"), ::responseHandler)
//        }
        val userId=MockRegister().saveInDb()
        Helper.user_id=userId
        SharedPrefHelper((activity as MainActivity).applicationContext).getInstance().loginUser(userId!!,"123456")
        val intent = Intent(activity,DashboardActivity::class.java)
        intent.putExtra("user_id",userId)
        startActivity(intent)
        (activity as MainActivity).finish()
        Log.e("mock login", "userId= ${userId}, done successfully!")
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
        progressDialog.show()
        Thread{
            userId=nameEditText!!.text.toString()
            password=passwordEditText!!.text.toString()
            val user = database.userDao().checkUser(userId!!,password!!)

//            if (user.email!!.isNotEmpty()) {
//                Helper.user_id = userId
//                SharedPrefHelper((activity as MainActivity).applicationContext).getInstance()
//                    .loginUser(userId!!)
//
//                val intent = Intent(activity,DashboardActivity::class.java)
//                intent.putExtra("user_id",userId)
//                startActivity(intent)
//                (activity as MainActivity).finish()
//            }else{
//                Toast.makeText(context, "Email or Password didn't matched! Please try again", Toast.LENGTH_LONG).show();
//            }

            GlobalScope.launch(Dispatchers.IO) {
//               LoginRepo().login(LoginRequest("sajibcse42@gmail.com", "1234"), ::messageHandler)
//                token=LoginRepo().login(LoginRequest("amatik2015@gmail.com", "123456"), ::responseHandler)
                LoginRepo().login(LoginRequest(userId!!, password!!), ::loginHandler)
            }


        }.start()
    }


    private fun loginHandler(tokenOrMessage:String, error:Boolean){
        (activity as MainActivity).runOnUiThread {
            progressDialog.dismiss()
            if (!error){
                type="api"
                SessionManager(context!!).saveAuthToken(tokenOrMessage)
//                val userId=MockRegister().saveInDb()
                getUserDetails()
//                saveUserInDB(UserResponse())
                Helper.user_id = userId
                SharedPrefHelper((activity as MainActivity).applicationContext).getInstance().loginUser(userId!!,password!!)
                Log.e("user"," Title - ${userId} $tokenOrMessage")
//                    Toast.makeText(context, "Logged In Successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Email or Password didn't matched! Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener{
                // Update your UI here
            }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener {
                // Update your UI here
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //google

        try {
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            }
            //facebook
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }catch (e:Exception){
            Toast.makeText(context,"Something wrong!, Please try again later.",Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        // [START_EXCLUDE silent]
//        showProgressBar()
        progressDialog.show()
        // [END_EXCLUDE]
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    firebaseUser = firebaseAuth.currentUser
                    type="google"
                    //do something

                    if (firebaseUser!=null) {
//                        performLogin(user)
                        saveInDb()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // [START_EXCLUDE]

                }

                // [START_EXCLUDE]
//                hideProgressBar()
                progressDialog.dismiss()
                // [END_EXCLUDE]
            }
//        progressDialog.dismiss()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            Toast.makeText(context,"Sign In Successful",Toast.LENGTH_SHORT).show()

            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken: $token")
        progressDialog.show()

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    firebaseUser = firebaseAuth.currentUser
                    type="facebook"
                    if (firebaseUser!=null) {
//                        performLogin(user)
                        saveInDb()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException())
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        progressDialog.hide()
    }

    private fun performRegistration(){
        Toast.makeText(context, "Login Successful, ${firebaseUser?.displayName}!",
            Toast.LENGTH_SHORT).show()

        //TO DO:check if user is already registered in service if not then sign up
        var isSuccess:Boolean=false

        GlobalScope.launch(Dispatchers.IO){
            isSuccess=SignUpRepo().signUp(RegisterRequest(firebaseUser?.email!!,firebaseUser?.displayName!!
                ,firebaseUser?.email!!,firebaseUser?.uid!!,firebaseUser?.phoneNumber,type!!),::signUpHandler)
        }
    }

    fun signUpHandler(isSuccess:Boolean){
        Log.e("repo", "sign up:success  $isSuccess")
        if (isSuccess) {
//            getUserDetails()
            isFirstLogin=true
            saveInDb()
//            SharedPrefHelper((activity as MainActivity).applicationContext).getInstance().loginUser(firebaseUser?.email!!)
        }
    }

    private fun saveInDb() {

        GlobalScope.launch(Dispatchers.IO) {
            LoginRepo().login(LoginRequest(firebaseUser?.email!!, firebaseUser?.uid!!), ::responseHandlerForSocial)
        }

    }

    private fun responseHandlerForSocial(tokenOrMessage:String,error:Boolean){
        (activity as MainActivity).runOnUiThread {
            if (!error){
                SessionManager(context!!).saveAuthToken(tokenOrMessage)
//                val userId=MockRegister().saveInDb()
                getUserDetails()
                userId=firebaseUser?.email!!
                Helper.user_id = userId
                SharedPrefHelper((activity as MainActivity).applicationContext).getInstance().loginUser(userId!!,firebaseUser?.uid!!)
                Log.e("user"," Title - ${userId} $tokenOrMessage")
//                    Toast.makeText(context, "Logged In Successfully", Toast.LENGTH_SHORT).show();
//                val intent = Intent(activity,DashboardActivity::class.java)
//                intent.putExtra("user_id",userId)
//                database.close()
//                startActivity(intent)
//                (activity as MainActivity).finish()
            }else{
                performRegistration()
            }
        }
    }

    fun getUserDetails(){
        GlobalScope.launch(Dispatchers.IO) {
            user = UserRepository().getUserDetails(Helper.token!!, userId!!,::saveUserInDB)
        }
    }

    private fun saveUserInDB(user:UserResponse) {
//        user.name="Atik"
//        user.email=userId

        val database = Room.databaseBuilder(AccountKitController.getApplicationContext(), AppDb::class.java!!, "pill_reminder").fallbackToDestructiveMigration().build()

        var dbUuser:User?=null

            //get current datetime
            val simpleDateFormat= SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH)
            val currentDateTime=simpleDateFormat.format(Date()).toString()
            dbUuser = if (type=="api") {
                User(
                    user.name!!, user.email!!,password!!, user.phone_number,"", ""
                    , "", "", "", "", "", currentDateTime, false
                )
            }else{
                User(
                    firebaseUser!!.displayName!!, userId!!,firebaseUser!!.uid,user.phone_number, "", ""
                    , "", "", "", "", "", currentDateTime, false
                )
            }

        Thread{

            database.userDao().clearUser()
            val users = database.userDao().checkIfExist(userId!!)

//            Log.i("size",users.size.toString())
            if(users.isEmpty()){

                database.settingsDao().settings().forEach {
                    Log.i("settings=====>",""" Title - ${it.title}""")
                }
                val user_id = database.userDao().saveUser(dbUuser!!).toInt()
                val alarm = database.settingsDao().getSpecificSettings("alarm")
                val notification_message = database.settingsDao().getSpecificSettings("notification_message")
                val snooze_time = database.settingsDao().getSpecificSettings("snooze_time")
                if(alarm==null){
                    val forAlarm = Settings("alarm","",true,user.email!!)
                    database.settingsDao().saveSettings(forAlarm)
                    Log.e("Checking alarm tune","null")
                }else if(alarm!=null){
                    Log.e("Checking alarm tune","not null")
                }
                if(notification_message==null){
                    val forNotification = Settings("notification_message","",true,user.email!!)
                    database.settingsDao().saveSettings(forNotification)
                    Log.e("Checking notification","null")
                }else if(notification_message!=null){
                    Log.e("Checking notification","not null")
                }
                if(snooze_time==null){
                    val forSnooze = Settings("snooze_time","0",false,user.email!!)
                    database.settingsDao().saveSettings(forSnooze)
                    Log.e("Checking snooze","null")
                }else if(snooze_time!=null){
                    Log.e("Checking snooze","not null")
                }

//                //starting data fetch service
//                if (Helper.isConnected(context!!)) {
//                    Thread {
//                        val token = Helper.token
////            val token: String? = SessionManager(this).fetchAuthToken()
//                        MedicineRepo().getMedicine(token!!) as ArrayList<MedicineResponse>
//                        database.appointmentDao().clearAppointment()
//                        database.vaccineDao().clearVaccine()
//                        database.pillDao().clearPill()
//                        database.reportDao().clearReport()
//                        database.timePivotDao().clearTimePivot()
//                        database.documentDao().clearDocument()
//                        database.tableDao().clearTable()
//
//                        //start service to fetch data from server
//                        val service = Intent(context, DataFetchService::class.java)
//                        service.putExtra("token", Helper.token)
//                        context!!.startService(service)
////                runOnUiThread {
////                    Log.e("Dashboard","calling dashboard")
////                    showDashboardFragment()
////                }
//                    }.start()
//                }

                (activity as MainActivity).runOnUiThread {
                    if (!isFirstLogin) {
//                    Toast.makeText(context,"Successfully Registered!!",Toast.LENGTH_LONG).show();
                        val intent = Intent(activity, DashboardActivity::class.java)
                        intent.putExtra("user_id", userId)
                        database.close()
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    }
                    else{
                        val intent = Intent(activity, TutorialActivity::class.java)
                        intent.putExtra("user_id", userId)
                        database.close()
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    }
                }
            }else{
                (activity as MainActivity).runOnUiThread {
                   if (!isFirstLogin) {
//                    Toast.makeText(context,"Successfully Registered!!",Toast.LENGTH_LONG).show();
                        val intent = Intent(activity, DashboardActivity::class.java)
                        intent.putExtra("user_id", userId)
                        database.close()
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    }
                    else{
                        val intent = Intent(activity, TutorialActivity::class.java)
                        intent.putExtra("user_id", userId)
                        database.close()
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    }
                }
            }

        }.start()
    }
}