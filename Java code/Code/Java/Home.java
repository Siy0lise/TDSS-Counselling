package com.example.counsellorfinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Home extends AppCompatActivity {
    String[] items = {"Anxiety", "Depression", "Suicide", "Bipolar", "Childhood Trauma", "Emotional Intelligence", "Family Problems", "Grief", "HIV/AIDS", "Isolation", "LGBTQ"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    private TextView responseTextView;

    String item;
    String userPR;
    String userLog;
    private Button Login;
    OkHttpClient client = new OkHttpClient();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteTextView.setAdapter(adapterItems);

        responseTextView = findViewById(R.id.response_text);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(Home.this, "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userPR = getIntent().getStringExtra("userPR");
        Login = findViewById(R.id.find);
        Login.setOnClickListener(this::LOGIN);
    }

    private void LOGIN(View view) {
        RequestBody formBody = new FormBody.Builder()
                .add("illness_name", item)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/find.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseTextView.setText("Request Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String counsellorName = jsonObject.getString("Counsellor_Name");
                            Intent intent = new Intent(Home.this, ClientProfile.class);
                            intent.putExtra("userPR", userPR);
                            intent.putExtra("counsellorName", counsellorName);
                            intent.putExtra("item", item);
                            //intent.putExtra("userLog", userLog);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            responseTextView.setText("Error parsing JSON");
                        }
                    });

                    // Add illness to Users table
                    addIllnessToDatabase();
                } else {
                    runOnUiThread(() -> responseTextView.setText("Request Not Successful"));
                }
            }
        });
    }

    private void addIllnessToDatabase() {
        RequestBody formBody = new FormBody.Builder()
                .add("illness_name", item)
                .add("username", userPR) // Assuming userPassed contains the username register
                .build();

        Request request = new Request.Builder()

                .url("https://lamp.ms.wits.ac.za/home/s2672925/illness.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseTextView.setText("Failed to add illness to database"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(Home.this, "Illness added to database", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> responseTextView.setText("Failed to add illness to database"));
                }
            }
        });
    }
}




