package com.m3.weathervue.alerts.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.m3.weathervue.R
import com.m3.weathervue.databinding.AlertDialogBinding
import com.m3.weathervue.databinding.FragmentAlertsBinding
import com.m3.weathervue.databinding.FragmentFavoritesBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class AlertsFragment : Fragment() {
    lateinit var fromDate: Date
    lateinit var toDate: Date
    var fromDateInM: Long = 0
    var toDateInM: Long = 0


    lateinit var binding: FragmentAlertsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toDate = Date()
        fromDate = Date()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.floatingBtn.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
            val bindingAlert = AlertDialogBinding.inflate(LayoutInflater.from(requireContext()))
            alertDialogBuilder.setView(bindingAlert.root)
            alertDialogBuilder.show()
            bindingAlert.fromCard.setOnClickListener {
                showDateTimePicker(bindingAlert.fromDate, bindingAlert.fromTime, true)


            }
            bindingAlert.toCard.setOnClickListener {
                showDateTimePicker(bindingAlert.toDate, bindingAlert.toTime, false)


            }
            bindingAlert.saveButton.setOnClickListener {

            }


        }
    }

    private fun showDateTimePicker(
        dateTextView: TextView,
        timeTextView: TextView,
        isFromDate: Boolean
    ) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    if (isFromDate) {
                        fromDate = calendar.time
                        val dateFormat = SimpleDateFormat("E، d MMM", Locale.getDefault())
                        val formattedDate = dateFormat.format(fromDate)
                        val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
                        val formattedTime = timeFormat.format(fromDate)


                        timeTextView.text = formattedTime
                        dateTextView.text = formattedDate
                        fromDateInM = calculateTime(fromDate.time)
                        Log.e("date fromDateInM", "$fromDateInM")
                    } else {
                        toDate = calendar.time
                        val dateFormat = SimpleDateFormat("E، d MMM", Locale.getDefault())
                        val formattedDate = dateFormat.format(toDate)
                        val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
                        val formattedTime = timeFormat.format(toDate)


                        timeTextView.text = formattedTime
                        dateTextView.text = formattedDate
                        toDateInM = calculateTime(toDate.time)
                        Log.e("date toDateInM", "$toDateInM")

                    }


                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                timePickerDialog.setTitle("Select Time")
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.setTitle("Select Date")
        datePickerDialog.show()

    }

    private fun calculateTime(dateMillis: Long): Long {
        val currentTimeMillis = System.currentTimeMillis()
        return dateMillis - currentTimeMillis
    }
}
