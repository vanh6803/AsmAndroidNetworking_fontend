package com.example.assignment.api;

import com.example.assignment.Config;
import com.example.assignment.models.ApiResponseData;
import com.example.assignment.models.HackNasa;
import com.example.assignment.models.ResponeDataFromMyServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiMyServer {
    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();

    ApiMyServer apiService = new Retrofit.Builder()
            .baseUrl(Config.URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiMyServer.class);

    @POST("api/add")
    Call<Void> postData(@Body HackNasa data);

    @GET("api/")
    Call<ResponeDataFromMyServer> getData();

    @GET("api/detail/{id}")
    Call<ApiResponseData> getDataDetail(@Path("id")String id);

    @PUT("api/update/{id}")
    Call<Void> updateData(@Path("id") String id, @Body HackNasa data);

    @DELETE("api/delete/{id}")
    Call<Void> deleteData(@Path("id") String id);
}
