package com.example.pillreminder.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.pillreminder.R
import com.example.pillreminder.adaptors.ViewPagerAdapter
import com.example.pillreminder.fragments.ViewPagerFragment
import kotlinx.android.synthetic.main.activity_tutorial.*
import me.relex.circleindicator.CircleIndicator

class TutorialActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var adapter:ViewPagerAdapter
    private lateinit var indicator: CircleIndicator
    var user_id : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        user_id = intent.getStringExtra("user_id")
        Log.e("tutorial user id",user_id.toString())
        viewPager = findViewById(R.id.viewPager)
        indicator = findViewById(R.id.indicator)
        adapter = ViewPagerAdapter(supportFragmentManager)
        setupViewPager()

        tvStart.setOnClickListener {
            showDashboard()
        }

        tvSKip.setOnClickListener {
            showDashboard()
        }
        
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position==adapter.count-1){
                    tvSKip.visibility=View.INVISIBLE
                    tvStart.visibility= View.VISIBLE
                }
                else{
                    tvStart.visibility= View.INVISIBLE
                }
            }
            override fun onPageSelected(position: Int) {

            }

        })

    }
    private fun setupViewPager() {

        var firstFragmet: ViewPagerFragment = ViewPagerFragment.newInstance("first")
        var secondFragmet: ViewPagerFragment = ViewPagerFragment.newInstance("second")
        var thirdFragmet: ViewPagerFragment = ViewPagerFragment.newInstance("third")
        var fourthFragmet: ViewPagerFragment = ViewPagerFragment.newInstance("fourth")

        adapter.addFragment(firstFragmet, "ONE")
        adapter.addFragment(secondFragmet, "TWO")
        adapter.addFragment(thirdFragmet, "THREE")
        adapter.addFragment(fourthFragmet, "FOUR")

        viewPager.adapter = adapter
        indicator.setViewPager(viewPager)
//        adapter.unregisterDataSetObserver(indicator.dataSetObserver)

        //tabs!!.setupWithViewPager(viewpager)

    }

    override fun onBackPressed() {
            val dialogBuilder = AlertDialog.Builder(this);
            dialogBuilder.setMessage("Do you want to skip tutorial ?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        _, _ -> showDashboard()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, _ -> dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Skip Tutorial")
            // show alert dialog
            alert.show()

    }

    private fun showDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("user_id",user_id)
        startActivity(intent)
        finish()
//        finishAffinity()
    }
}
