package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.example.assignment.api.ApiNasa;
import com.example.assignment.api.RetrofitInstance;
import com.example.assignment.databinding.ActivityMainBinding;
import com.example.assignment.models.HackNasa;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private HackNasa hackNasa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hackNasa = new HackNasa();
        initViews();
    }

    private void initViews() {

        String date = "2023-07-13";
        binding.btnGetDataFormNasa.setOnClickListener(v -> callApiGetDataFormNasa(date));
    }

    private void callApiGetDataFormNasa(String date) {
        ApiNasa apiNasa = RetrofitInstance.getApiNasa();

        apiNasa.getDataFromNasa("IP5pVaHaWYJXW1YFdrA03mXo4IayAmPLytGphJqi", date).enqueue(new Callback<ApiNasa>() {
            @Override
            public void onResponse(Call<ApiNasa> call, Response<ApiNasa> response) {
//                hackNasa = (HackNasa) response.body();
//                binding.tvNotification.setText("get data successfully");
                Log.d("AAA", response.body().toString());
            }

            @Override
            public void onFailure(Call<ApiNasa> call, Throwable t) {
                Log.d("EEE", t.getMessage());
            }
        });
    }
}