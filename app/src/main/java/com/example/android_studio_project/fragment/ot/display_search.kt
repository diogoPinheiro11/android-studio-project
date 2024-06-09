package com.example.android_studio_project.fragment.ot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_studio_project.R
import com.example.android_studio_project.data.retrofit.models.TripModel
import com.example.android_studio_project.data.retrofit.services.TripService
import com.example.android_studio_project.fragment.trip.edit_trip.edit_trip
import java.util.UUID
import android.app.AlertDialog
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup

class display_search(private val userEmail: String, private val userUUID: String) : Fragment() {

    private lateinit var tripService: TripService
    private lateinit var displayHomeAdapter: display_home_adapter
    private var allTrips: List<TripModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_search, container, false)
        val searchBar: EditText = view.findViewById(R.id.search_bar)
        val filterButton: ImageButton = view.findViewById(R.id.filter_button)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        displayHomeAdapter = display_home_adapter(emptyList()) { clickedTrip ->
            clickedTrip.uuid?.let { openEditTripFragment(it, userUUID) }
        }
        recyclerView.adapter = displayHomeAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tripService = TripService(requireContext())
        getTrips()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTrips(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        filterButton.setOnClickListener {
            showFilterPopup()
        }

        return view
    }

    private fun showFilterPopup() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filter, null)
        val dateRadioGroup: RadioGroup = dialogView.findViewById(R.id.date_radio_group)
        val ratingRadioGroup: RadioGroup = dialogView.findViewById(R.id.rating_radio_group)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Filter Options")
            .setPositiveButton("Apply") { dialog, _ ->
                val selectedDateOption = dateRadioGroup.checkedRadioButtonId
                val selectedRatingOption = ratingRadioGroup.checkedRadioButtonId

                when (selectedDateOption) {
                    R.id.date_asc -> sortTripsByDate(ascending = true)
                    R.id.date_desc -> sortTripsByDate(ascending = false)
                }

                when (selectedRatingOption) {
                    R.id.rating_asc -> sortTripsByRating(ascending = true)
                    R.id.rating_desc -> sortTripsByRating(ascending = false)
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun sortTripsByDate(ascending: Boolean) {
        val sortedTrips = if (ascending) {
            allTrips.sortedBy { it.endDate }
        } else {
            allTrips.sortedByDescending { it.endDate }
        }
        displayHomeAdapter.updateData(sortedTrips)
    }

    private fun sortTripsByRating(ascending: Boolean) {
        val sortedTrips = if (ascending) {
            allTrips.sortedBy { it.rating }
        } else {
            allTrips.sortedByDescending { it.rating }
        }
        displayHomeAdapter.updateData(sortedTrips)
    }

    private fun openEditTripFragment(tripUuid: UUID, userUUID: String?) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, edit_trip.newInstance(tripUuid, userUUID))
            .addToBackStack(null)
            .commit()
    }

    private fun getTrips() {
        tripService.getUserTrips(userUUID,
            onResponse = { trips ->
                if (trips != null) {
                    allTrips = trips
                    displayHomeAdapter.updateData(trips)
                } else {
                }
            }
        ) {
        }
    }

    private fun filterTrips(query: String) {
        val filteredTrips = allTrips.filter { it.name?.contains(query, ignoreCase = true) == true }
        displayHomeAdapter.updateData(filteredTrips)
    }

    companion object {
        fun newInstance(userEmail: String, userUUID: String): display_search {
            return display_search(userEmail, userUUID)
        }
    }
}
