package com.example.pillreminder.fragments

import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pillreminder.R
import kotlinx.android.synthetic.main.view_pager_fragment.*

class ViewPagerFragment : Fragment() {
    companion object {
        fun newInstance(message: String): ViewPagerFragment {

            val f = ViewPagerFragment()

            val bdl = Bundle(1)

            bdl.putString(EXTRA_MESSAGE, message)

            f.setArguments(bdl)

            return f

        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = inflater.inflate(R.layout.view_pager_fragment, container, false);

        val message = arguments!!.getString(EXTRA_MESSAGE)
//        text.text = "First one"
        var image: ImageView = view!!.findViewById(R.id.imageHolder)
//        textView!!.text = message
        if(message=="first"){
            image.setImageResource(R.drawable.slider_1)
        }else if(message=="second"){
            image.setImageResource(R.drawable.slider_2)
        }else if(message=="third"){
            image.setImageResource(R.drawable.slider_3)
        }else if(message=="fourth"){
            image.setImageResource(R.drawable.slider_4)
        }

        return view
    }


}