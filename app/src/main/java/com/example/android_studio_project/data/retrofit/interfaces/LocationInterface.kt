package com.example.android_studio_project.data.retrofit.interfaces

import com.example.android_studio_project.data.retrofit.models.LocationModel
import com.example.android_studio_project.data.retrofit.models.LocationTypeModel
import com.example.android_studio_project.data.retrofit.models.TripModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LocationInterface {
    @GET("locationType/")
    fun getTypes(): Call<List<LocationTypeModel>>

    @POST("location/create")
    fun createLocation(@Body locationData: LocationModel): Call<LocationModel>
}