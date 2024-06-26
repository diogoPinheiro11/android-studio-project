package com.example.android_studio_project.data.retrofit.services

import android.content.Context
import com.example.android_studio_project.data.retrofit.core.API
import com.example.android_studio_project.data.retrofit.interfaces.TripInterface
import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.models.TripModel
import com.example.android_studio_project.data.retrofit.models.TripModelCreate
import com.example.android_studio_project.data.retrofit.models.TripModelEdit
import com.example.android_studio_project.data.retrofit.models.UserModel
import com.example.android_studio_project.data.retrofit.models.UserTripModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class TripService(private val context: Context) {
    private val tripApi = API.getRetrofitInstance().create(TripInterface::class.java)

    fun getTripById(uuid: UUID, onResponse: (TripModel?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.getTripById(uuid)
        call.enqueue(object : Callback<TripModel> {
            override fun onResponse(call: Call<TripModel>, response: Response<TripModel>) {
                if (response.isSuccessful) {
                    val trip = response.body()
                    onResponse(trip)
                } else {
                    onFailure(Throwable("Failed to get trip details: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getUserTrips(userId: String?, onResponse: (List<TripModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = userId?.let { tripApi.getUserTrips(it) }
        call?.enqueue(object : Callback<List<TripModel>> {
            override fun onResponse(call: Call<List<TripModel>>, response: Response<List<TripModel>>) {
                if (response.isSuccessful) {
                    val trips = response.body()
                    onResponse(trips)
                } else {
                    onFailure(Throwable("Failed to get user trips: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<TripModel>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun deleteTrip(userUuid: String?, tripUuid: UUID, onResponse: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.deleteTrip(userUuid, tripUuid)
        call.enqueue(object : Callback<TripModel> {
            override fun onResponse(call: Call<TripModel>, response: Response<TripModel>) {
                if (response.isSuccessful) {
                    onResponse()
                } else {
                    onFailure(Throwable("Failed to delete trip: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getTripLocations(tripId: UUID?, onResponse: (List<LocationModel>?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripId?.let { tripApi.getTripsLocations(it) }
        call?.enqueue(object : Callback<List<LocationModel>> {
            override fun onResponse(call: Call<List<LocationModel>>, response: Response<List<LocationModel>>) {
                if (response.isSuccessful) {
                    val trips = response.body()
                    onResponse(trips)
                } else {
                    onFailure(Throwable("Failed to get user trips: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<LocationModel>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createTrip(trip: TripModelCreate, onResponse: (String, UUID?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.createTrip(trip)
        call.enqueue(object : Callback<TripModelCreate> {
            override fun onResponse(call: Call<TripModelCreate>, response: Response<TripModelCreate>) {
                if (response.isSuccessful) {
                    val tripResponse = response.body()
                    val tripUUID = tripResponse?.uuid
                    onResponse("success", tripUUID)
                } else {
                    onFailure(Throwable("Error creating location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripModelCreate>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun createUserTrip(userTrip: UserTripModel, onResponse: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.createUserTrip(userTrip)
        call.enqueue(object : Callback<UserTripModel> {
            override fun onResponse(call: Call<UserTripModel>, response: Response<UserTripModel>) {
                if (response.isSuccessful) {
                    onResponse("success")
                } else {
                    onFailure(Throwable("Error creating trip location: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<UserTripModel>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun updateTrip(tripUuid: UUID, trip: TripModelEdit, onResponse: (String?, TripModelEdit?) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.updateTrip(tripUuid, trip)
        call.enqueue(object : Callback<TripModelEdit> {
            override fun onResponse(call: Call<TripModelEdit>, response: Response<TripModelEdit>) {
                if (response.isSuccessful) {
                    val tripResponse = response.body()
                    onResponse("success", tripResponse)
                } else {
                    onFailure(Throwable("Error updating trip: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TripModelEdit>, t: Throwable) {
                onFailure(t)
            }
        })
    }

    fun getTripUser(tripId: UUID, onResponse: (List<UserModel>) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = tripApi.getTripUser(tripId)
        call.enqueue(object : Callback<List<UserModel>> {
            override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    if (userList != null) {
                        onResponse(userList)
                    } else {
                        onFailure(Throwable("Empty response"))
                    }
                } else {
                    onFailure(Throwable("Error fetching users: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                onFailure(t)
            }
        })
    }

}