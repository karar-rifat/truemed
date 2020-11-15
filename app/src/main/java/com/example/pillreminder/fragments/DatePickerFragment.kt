package com.example.pillreminder.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
//import androidx.core.app.DialogFragment
//import androidx.core.app.FragmentActivity
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(activity!!, android.R.style.Theme_Holo_Light_Dialog_MinWidth, activity as DatePickerDialog.OnDateSetListener, year, month, date)
        datePicker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return datePicker

    }

//    @override
//    public Dialog onCreateDialog(Bundle savedInstanceState){
//        Calender c = Calender.getInstance();
//        int year = c.get(Calender.YEAR);
//        int month = c.get(Calender.MONTH);
//        int date = c.get(Calender.DAY_OF_MONTH);
//
//        return new DatePickerDialog(getActivity(),(DatePickerDialog.onDateSetListener)getActivity(),year,month,day);
//    }

}