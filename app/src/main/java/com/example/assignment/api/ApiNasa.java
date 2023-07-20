package com.example.assignment.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiNasa {
    @GET("planetary/apod")
    Call<ApiNasa> getDataFromNasa(@Query("api_key") String apiKey, @Query("date") String date);

}
