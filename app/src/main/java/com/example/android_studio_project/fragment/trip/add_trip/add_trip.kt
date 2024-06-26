package com.example.android_studio_project.fragment.trip.add_trip

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripLocationModel
import com.example.android_studio_project.data.retrofit.models.TripModelCreate
import com.example.android_studio_project.data.retrofit.models.UserTripModel
import com.example.android_studio_project.data.retrofit.services.TripService
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class add_trip(private val userUUID: String) : Fragment() {

    private lateinit var tripNameEditText: EditText
    private lateinit var tripDateEditText: EditText
    private lateinit var tripDescriptionEditText: EditText
    private lateinit var tripRatingBar: RatingBar

    private lateinit var saveTripButton: Button
    private lateinit var tripService: TripService

    private var tripStartDate: String? = null
    private var tripEndDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_trip, container, false)

        tripNameEditText = view.findViewById(R.id.trip_name)
        tripDateEditText = view.findViewById(R.id.trip_date)
        tripDateEditText.setOnClickListener {
            showDatePicker()
        }
        tripDescriptionEditText = view.findViewById(R.id.trip_description)
        tripRatingBar = view.findViewById(R.id.trip_rating)
        saveTripButton = view.findViewById(R.id.save_btn)
        saveTripButton.setOnClickListener {
            saveTrip()
        }

        tripService = TripService(requireContext())

        val backButton: ImageView = view.findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val cancelButton: Button = view.findViewById(R.id.cancel_btn)
        cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun saveTrip() {
        val tripName = tripNameEditText.text.toString()
        val tripDate = tripDateEditText.text.toString()
        val tripDescription = tripDescriptionEditText.text.toString()
        val tripRating = tripRatingBar.rating

        if (tripName.isNotEmpty() && tripDate.isNotEmpty() && tripStartDate != null && tripEndDate != null) {
            val trip = TripModelCreate(
                uuid = UUID.randomUUID(),
                name = tripName,
                description = tripDescription,
                startDate = tripStartDate,
                endDate = tripEndDate,
                rating = tripRating
            )

            tripService.createTrip(trip, onResponse = { responseMessage, tripUUID ->
                requireActivity().runOnUiThread {
                    if (responseMessage == "success" && tripUUID != null) {
                        val userTrip = UserTripModel(userId = userUUID, tripId = tripUUID.toString())
                        tripService.createUserTrip(userTrip, onResponse = { userTripResponse ->
                            requireActivity().runOnUiThread {
                                if (userTripResponse == "success") {
                                    showConfirmationDialog()
                                    clearFields()
                                } else {
                                    Toast.makeText(context, getString(R.string.save_error), Toast.LENGTH_LONG).show()
                                }
                            }
                        }, onFailure = { userTripThrowable ->
                            requireActivity().runOnUiThread {
                                Toast.makeText(context, getString(R.string.save_error), Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Toast.makeText(context, getString(R.string.save_error), Toast.LENGTH_LONG).show()
                    }
                }
            }, onFailure = { throwable ->
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(context, getString(R.string.fill_fields), Toast.LENGTH_LONG).show()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date range")
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            tripStartDate = isoDateFormat.format(Date(selection.first ?: 0))
            tripEndDate = isoDateFormat.format(Date(selection.second ?: 0))

            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDisplay = displayFormat.format(Date(selection.first ?: 0))
            val endDisplay = displayFormat.format(Date(selection.second ?: 0))
            val dateRange = "$startDisplay - $endDisplay"
            tripDateEditText.setText(dateRange)
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun clearFields(){
        tripNameEditText.text.clear()
        tripDateEditText.text.clear()
        tripDescriptionEditText.text.clear()
        tripRatingBar.rating = 0.0f
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.succe))
        builder.setMessage(getString(R.string.save_succe))
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun getLoggedUserId(): Int {
        val sharedPreferences = requireActivity().getSharedPreferences("UserLoggedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1)
    }

}

