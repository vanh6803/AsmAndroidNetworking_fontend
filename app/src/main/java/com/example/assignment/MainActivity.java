package com.example.assignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment.adapter.AdapterListDataFromNasa;
import com.example.assignment.adapter.HackNasaAdapter;
import com.example.assignment.api.ApiMyServer;
import com.example.assignment.api.ApiNasa;
import com.example.assignment.api.ApiResponeNasa;
import com.example.assignment.databinding.ActivityMainBinding;
import com.example.assignment.models.HackNasa;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private HackNasa hackNasa;
    private static final String API_KEY = "IP5pVaHaWYJXW1YFdrA03mXo4IayAmPLytGphJqi";
    private ApiNasa apiNasa;
    String base64UrlHd;
    String base64url;
    private String dateSelected, daySelected, monthSelected, yearSelected;

    private List<HackNasa> list;
    private AdapterListDataFromNasa adapter;

    private String host= "21.98.225.248" ;
    private int port = 8080;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean isConnected = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hackNasa = new HackNasa();


        initViews();
    }

    private void initViews() {
        if (!isConnected){
            connectToServer(host,port);
            Toast.makeText(this, " connect to server success", Toast.LENGTH_SHORT).show();
        }
        // -------------select day - month - year-------------
        List<String> days = new ArrayList<>();

        for (int i = 1; i <= 31; i++) {
            days.add(String.valueOf(i));
        }
        List<String> months = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        List<String> years = new ArrayList<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            years.add(String.valueOf(i));
        }
        days.add(0, "days");
        months.add(0, "months");
        years.add(0, "years");

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);

        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spnYear.setAdapter(yearsAdapter);
        binding.spnMonth.setAdapter(monthsAdapter);
        binding.spnDate.setAdapter(daysAdapter);

        binding.spnDate.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        binding.spnYear.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        binding.spnMonth.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        //----------------------------------------------------------------

        //clicked call api
        binding.btnGetDataFormNasa.setOnClickListener(v -> callApiGetDataFormNasa(API_KEY, dateSelected));

        //gone layout
        binding.layoutShowData.setVisibility(View.GONE);

        // clicked push data to my server
        binding.btnPushData.setOnClickListener(v -> sendDataToServer());

        //next screen
        binding.btnGetDataFormMyServer.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, DataFromMyServerActivity.class));
        });



        list = new ArrayList<>();
        adapter = new AdapterListDataFromNasa(this);
        //select thread
        List<Integer> threads = new ArrayList<>();
        for (int i = 1; i <=31; i++){
            threads.add(i);
        }
        ArrayAdapter<Integer> threadsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, threads);
        threadsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectThread.setAdapter(threadsAdapter);
        binding.selectThread.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                //clicked get list data from nasa
                binding.btnGetListDataFormNasa.setOnClickListener(v ->getListDataFromNasa(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // push list data to my server
        binding.btnPushListData.setOnClickListener(v->{
            pushDataListToServer(list);
            int message = list.size();
            if (isConnected){
                sendMessageToServer(message);
                Toast.makeText(this, "send message success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please connect to server first", Toast.LENGTH_SHORT).show();
            }
        });

        //logout
        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "logout success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

    }



    //push list data
    private void pushDataListToServer(List<HackNasa> dataList) {
        ExecutorService executor = Executors.newFixedThreadPool(dataList.size());

        for (HackNasa data : dataList) {
            executor.submit(() -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String hdurl = data.getHdurl();
                    String base64UrlHd = "";
                    if (hdurl != null) {
                        base64UrlHd = convertUrlToBase64(hdurl);
                        data.setHdurl(base64UrlHd);
                    }

                    String url = data.getUrl();
                    String base64url = "";
                    if (url != null) {
                        base64url = convertUrlToBase64(url);
                        data.setUrl(base64url);
                    }
                }

                ApiMyServer.apiService.postData(data).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("API", t.getMessage());
                    }
                });
            });
        }

        executor.shutdown();

        binding.tvNotification.setText("push list data success");
        binding.tvNotification.setTextColor(Color.parseColor("#198754"));


    }

    // Gọi API để lấy dữ liệu từ NASA và lưu vào danh sách
    private void cloneCallApiGetDataFormNasa(String api_key, String date) {
        apiNasa = ApiResponeNasa.getApiNasa();
        apiNasa.getDataFromNasa(api_key, date).enqueue(new Callback<HackNasa>() {
            @Override
            public void onResponse(Call<HackNasa> call, Response<HackNasa> response) {
                HackNasa hackNasa = response.body();
                if (hackNasa != null) {
                    list.add(hackNasa); // Lưu dữ liệu vào danh sách
                    adapter.notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật dữ liệu
                }
            }

            @Override
            public void onFailure(Call<HackNasa> call, Throwable t) {
                Log.d("EEE", t.getMessage());
            }
        });
    }


    //get list data from nasa
    private void getListDataFromNasa(int numberThreads) {
        list.clear();
        adapter.notifyDataSetChanged();

        // Tính toán ngày cách đây 10 ngày
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, -numberThreads);
        String endDateString = formatDate(endDate.getTime());

        // Tạo danh sách các ngày từ ngày hiện tại đến 10 ngày trước đó
        List<String> datesToCallApi = new ArrayList<>();
        Calendar tempDate = Calendar.getInstance();
        while (tempDate.after(endDate)) {
            datesToCallApi.add(formatDate(tempDate.getTime()));
            tempDate.add(Calendar.DAY_OF_MONTH, -1);
        }

        // Kiểm tra nếu không có ngày nào để gọi API
        if (datesToCallApi.isEmpty()) {
            // Xử lý trường hợp khi không có dữ liệu để gọi API
            // Ví dụ, hiển thị thông báo hoặc cập nhật giao diện người dùng để thông báo không có dữ liệu.
            Toast.makeText(this, "Không có dữ liệu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo ExecutorService để quản lý các luồng
        ExecutorService executor = Executors.newFixedThreadPool(datesToCallApi.size());

        // Tạo danh sách các Future object để lưu kết quả của mỗi cuộc gọi API
        List<Future<HackNasa>> futures = new ArrayList<>();

        // Bắt đầu các cuộc gọi API trong các luồng
        for (String date : datesToCallApi) {
            binding.tvNotification.setText("calling api ...");
            Future<HackNasa> future = (Future<HackNasa>) executor.submit(() -> cloneCallApiGetDataFormNasa(API_KEY, date));
            futures.add(future);
        }

        // Đợi tất cả các luồng hoàn thành và thu thập kết quả
        for (Future<HackNasa> future : futures) {
            try {
                HackNasa result = future.get();
                if (result != null) {
                    list.add(result);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Tắt ExecutorService
        executor.shutdown();

        binding.tvNotification.setText("get list data success");
        binding.tvNotification.setTextColor(Color.parseColor("#198754"));

        binding.rcv.setVisibility(View.VISIBLE);
        adapter.setData(list);
        binding.rcv.setAdapter(adapter);
    }


    // Helper method to format date as "yyyy-MM-dd"
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }



    //call api to get data from api nasa
    private void callApiGetDataFormNasa(String api_key, String date) {
        apiNasa = ApiResponeNasa.getApiNasa();
        apiNasa.getDataFromNasa(api_key, date).enqueue(new Callback<HackNasa>() {
            @Override
            public void onResponse(Call<HackNasa> call, Response<HackNasa> response) {
                hackNasa = response.body();
                binding.layoutShowData.setVisibility(View.VISIBLE);
                binding.tvTitle.setText(hackNasa.getTitle());
                binding.tvDate.setText(hackNasa.getDate());
                binding.tvExplanation.setText(hackNasa.getExplanation());
                if (hackNasa.getHdurl() != null) {
                    Glide.with(MainActivity.this).load(hackNasa.getHdurl()).error(R.drawable.baseline_error_24).into(binding.imgHd);
                } else {
                    Glide.with(MainActivity.this).load(hackNasa.getUrl()).error(R.drawable.baseline_error_24).into(binding.imgHd);
                }
                binding.tvNotification.setText("get data from Nasa successfully");
                binding.tvNotification.setTextColor(Color.parseColor("#198754"));

                Log.d("callApiGetDataFormNasa", response.body().toString());
            }

            @Override
            public void onFailure(Call<HackNasa> call, Throwable t) {
    
                Log.d("EEE", t.getMessage());
                binding.tvNotification.setText("get data from Nasa failed");
                binding.tvNotification.setTextColor(Color.RED);
            }
        });
    }

    // push data to my server
    private void sendDataToServer() {


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (hackNasa.getHdurl() != null) {
                base64UrlHd = convertUrlToBase64(hackNasa.getHdurl());
            }else {
                base64UrlHd ="";
            }

             base64url = convertUrlToBase64(hackNasa.getUrl());
        }

            hackNasa.setHdurl(base64UrlHd);
            hackNasa.setUrl(base64url);

        Log.d("sendDataToServer", hackNasa.toString());
        ApiMyServer.apiService.postData(hackNasa).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                binding.tvNotification.setText("push data to my server successfully");
                binding.tvNotification.setTextColor(Color.parseColor("#198754"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                binding.tvNotification.setText("post data to my server failed");
                binding.tvNotification.setTextColor(Color.RED);
                Log.d("API", t.getMessage());
            }
        });
    }

    // convert string url to base64
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String convertUrlToBase64(String url) {
        byte[] byteInput = url.getBytes();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        String encodedString = base64Encoder.encodeToString(byteInput);
        return encodedString;
    }

    // listener item selected from spinner
    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            daySelected = binding.spnDate.getSelectedItem().toString();
            monthSelected = binding.spnMonth.getSelectedItem().toString();
            yearSelected = binding.spnYear.getSelectedItem().toString();
            if (!daySelected.equals("days") && !monthSelected.equals("months") && !yearSelected.equals("years")) {
                dateSelected = yearSelected + "-" + monthSelected + "-" + daySelected;
                Log.d("Selected Date", dateSelected);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    //send number image saved
    private void sendMessageToServer(int message) {
        if (output!=null){
            new Thread(()->{
                output.println(message);
            }).start();
        }
    }

    //connect to server socket
    private void connectToServer(String host, int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(host, port);
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);
                    handler.post(()->{
                        Toast.makeText(MainActivity.this, "connect to app socket success", Toast.LENGTH_SHORT).show();
                        isConnected = true;
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}

