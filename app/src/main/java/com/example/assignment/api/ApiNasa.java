package com.example.assignment.api;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiNasa {

    ApiNasa apiNasa = new Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiNasa.class);
    @GET("planetary/apod")
    Call<ApiNasa> getDataFromNasa(@Query("api_key") String apiKey, @Query("date") String date);

}
