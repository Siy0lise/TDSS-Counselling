package com.example.counsellorfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ClientProfile2 extends AppCompatActivity {
    private TextView responseTextView;
    private OkHttpClient client = new OkHttpClient();
    private TextView nameDoc;
    private Button loginButton;
    private Button userDetailsButton;
    private String userLog;
    private TextView out;
    private TextView out1;
    private String counsellorName;
    private String illnessName;
    private Button Gochat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        responseTextView = findViewById(R.id.response_text);
        out1 = findViewById(R.id.textView5);
        out = findViewById(R.id.nameDoc);
        loginButton = findViewById(R.id.view);
        Gochat=findViewById(R.id.chatbutton);
        userDetailsButton = findViewById(R.id.button);
        Gochat.setOnClickListener(this::GoChat);
        loginButton.setOnClickListener(this::fetchCounsellorInfo);
        userDetailsButton.setOnClickListener(this::fetchUserDetails);

        // Get username from the intent
        userLog = getIntent().getStringExtra("userLog");
    }

    private void GoChat(View view)
    {
        Intent intent = new Intent(ClientProfile2.this, ChatActivity.class);
        intent.putExtra("currentUser2", userLog);
        intent.putExtra("counsellor2",counsellorName);
        startActivity(intent);
    }

    private void fetchCounsellorInfo(View view) {
        RequestBody counsellorFormBody = new FormBody.Builder()
                .add("username", userLog)
                .build();

        Request counsellorRequest = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/clientsLog2.php")
                .post(counsellorFormBody)
                .build();

        client.newCall(counsellorRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseTextView.setText("Counsellor Request Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (jsonObject.has("error")) {
                                responseTextView.setText(jsonObject.getString("error"));
                            } else {
                                counsellorName = jsonObject.getString("Counsellor_Name");
                                illnessName = jsonObject.getString("Illness_Name");

                                out.setText("Your Counsellor for " + illnessName + ": " + counsellorName);
                                fetchDoctorDetails();
                            }
                        } catch (JSONException e) {
                            responseTextView.setText("Error parsing Counsellor JSON");
                        }
                    });
                } else {
                    runOnUiThread(() -> responseTextView.setText("Counsellor Request Not Successful"));
                }
            }
        });
    }

    private void fetchDoctorDetails() {
        RequestBody doctorFormBody = new FormBody.Builder()
                .add("doctor", counsellorName)
                .build();

        Request doctorRequest = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/profile.php")
                .post(doctorFormBody)
                .build();

        client.newCall(doctorRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseTextView.setText("Doctor Details Request Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (jsonObject.has("error")) {
                                responseTextView.setText(jsonObject.getString("error"));
                            } else {
                                String doctorDetails = "Counsellor Details:\n"
                                        + "Practioner_No: " + jsonObject.getInt("Practioner_No") + "\n"
                                        + "Name: " + jsonObject.getString("Counsellor_Name") + "\n"
                                        + "Email: " + jsonObject.getString("Email") + "\n"
                                        + "Qualifications: " + jsonObject.getString("Qualifications") + "\n"
                                        + "Availability: " + jsonObject.getString("Availability") + "\n"
                                        + "Ratings: " + jsonObject.getDouble("Ratings") + "\n\n";
                                out1.setText(doctorDetails);
                            }
                        } catch (JSONException e) {
                            responseTextView.setText("Error parsing Doctor JSON");
                        }
                    });
                } else {
                    runOnUiThread(() -> responseTextView.setText("Doctor Details Request Not Successful"));
                }
            }
        });
    }

    private void fetchUserDetails(View view) {
        RequestBody userFormBody = new FormBody.Builder()
                .add("username", userLog)
                .build();

        Request userRequest = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/profile2.php")
                .post(userFormBody)
                .build();

        client.newCall(userRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseTextView.setText("User Details Request Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (jsonObject.has("error")) {
                                responseTextView.setText(jsonObject.getString("error"));
                            } else {
                                String userDetails = "User Details:\n"
                                        + "Username: " + jsonObject.getString("Username") + "\n"
                                        + "Email: " + jsonObject.getString("Email") + "\n"
                                        + "Illness Name: " + jsonObject.getString("Illness_Name") + "\n\n";
                                responseTextView.append(userDetails);
                            }
                        } catch (JSONException e) {
                            responseTextView.setText("Error parsing User JSON");
                        }
                    });
                } else {
                    runOnUiThread(() -> responseTextView.setText("User Details Request Not Successful"));
                }
            }
        });
    }
}
