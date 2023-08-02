package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.assignment.adapter.HackNasaAdapter;
import com.example.assignment.api.ApiMyServer;
import  com.example.assignment.databinding.ActivityDataFromMyServerBinding;
import com.example.assignment.models.HackNasa;
import com.example.assignment.models.ResponeDataFromMyServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFromMyServerActivity extends AppCompatActivity {

    private ActivityDataFromMyServerBinding binding;
    private List<HackNasa> listHackNasa;
    private HackNasaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityDataFromMyServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

    }

    private void initViews() {
        listHackNasa = new ArrayList<>();
        adapter = new HackNasaAdapter(this);
        binding.btnBack.setOnClickListener(v->finish());
        getData();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String id = listHackNasa.get(viewHolder.getAdapterPosition()).get_id();
                deleteData(id);
            }
        });

        itemTouchHelper.attachToRecyclerView(binding.rcv);

    }

    private void getData() {
        ApiMyServer.apiService.getData().enqueue(new Callback<ResponeDataFromMyServer>() {
            @Override
            public void onResponse(Call<ResponeDataFromMyServer> call, Response<ResponeDataFromMyServer> response) {
                listHackNasa = response.body().getData();
                adapter.setData(listHackNasa);
                binding.rcv.setAdapter(adapter);
                Log.d("CCC", listHackNasa.toString());
            }

            @Override
            public void onFailure(Call<ResponeDataFromMyServer> call, Throwable t) {

            }
        });
    }

    private void deleteData(String id){
        ApiMyServer.apiService.deleteData(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                for (int i = 0; i < listHackNasa.size(); i++) {
                    if (listHackNasa.get(i).get_id().equals(id)) {
                        listHackNasa.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
                Toast.makeText(DataFromMyServerActivity.this, "delete success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}