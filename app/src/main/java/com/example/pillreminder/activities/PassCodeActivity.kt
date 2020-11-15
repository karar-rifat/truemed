package com.example.pillreminder.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pillreminder.R
import com.example.pillreminder.helper.SharedPrefHelper
import kotlinx.android.synthetic.main.activity_pass_code.*

class PassCodeActivity: AppCompatActivity() {

    var passCode:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code)

        val userId = intent.getStringExtra("user_id")

        passCode=SharedPrefHelper(this).getInstance().getPassCode()

        btnEnter.setOnClickListener {
            if (etPassCode.text.isNullOrEmpty()){
                Toast.makeText(this,"please enter code.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etPassCode.text.toString()!=passCode){
                Toast.makeText(this,"Wrong code!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passCode==etPassCode.text.toString()){
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("user_id", userId)
                intent.putExtra("from_session", true)
                startActivity(intent)
                this.finish()
            }
        }
    }
}