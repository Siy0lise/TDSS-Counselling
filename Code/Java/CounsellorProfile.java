package com.example.counsellorfinal;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
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

public class CounsellorProfile extends AppCompatActivity {
    private TextView responseTextView;
    private TextView doc;
    private static final String TAG = "ClientProfile";
    String userN;

    String nameFinal;
    private TextView textView5;
    private Button Login, chat;
    private OkHttpClient client = new OkHttpClient();
    StringBuilder doctorDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsellor_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userN = getIntent().getStringExtra("userPassed");
        Login = findViewById(R.id.view);
        chat= findViewById(R.id.chatbut);
        responseTextView = findViewById(R.id.response_text);
        doc= findViewById(R.id.nameDoc);
        doc.setText("Good Day "+userN);
        textView5 = findViewById(R.id.textView5);

        Login.setOnClickListener(this::LOGIN);
        chat.setOnClickListener(this::GoChat);
    }

    private void GoChat(View view)
    {
        Intent intent= new Intent(CounsellorProfile.this,UnreadMessagesActivity.class);
        intent.putExtra("counsellor",userN);
        startActivity(intent);
    }
    private void LOGIN(View view)
    {
        if (userN == null || userN.isEmpty()) {
            Toast.makeText(this, "User not passed correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody counsellorFormBody = new FormBody.Builder()
                .add("doctor", userN)
                .build();

        Request counsellorRequest = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/patients.php")
                .post(counsellorFormBody)
                .build();

        client.newCall(counsellorRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Counsellor Request Failed", e);
                runOnUiThread(() -> responseTextView.setText("Counsellor Request Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONArray jsonArray = new JSONArray(responseData);
                            doctorDetails = new StringBuilder("Patient Details:\n");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                doctorDetails.append("Username: ").append(jsonObject.getString("Username")).append("\n");

                                nameFinal = jsonObject.getString("Username");
                                Udetail(nameFinal);
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Counsellor JSON Parsing Error", e);
                            responseTextView.setText("Error parsing Counsellor JSON");
                        }
                    });
                } else {
                    Log.e(TAG, "Counsellor Request Not Successful: " + response.message());
                    runOnUiThread(() -> responseTextView.setText("Counsellor Request Not Successful"));
                }
            }
        });
    }


    private void Udetail(String s) {
        // Request for User's details
        RequestBody userFormBody = new FormBody.Builder()
                .add("username", s)  // Send username for user details
                .build();

        Request userRequest = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/profile2.php")
                .post(userFormBody)
                .build();

        client.newCall(userRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "User Request Failed", e);
                runOnUiThread(() -> responseTextView.append("User Request Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (jsonObject.has("error")) {
                                responseTextView.append(jsonObject.getString("error"));
                            } else {
                                // Display User's details
                                String userDetails = "User Details:\n"
                                        + "Username: " + jsonObject.getString("Username") + "\n"
                                        + "Email: " + jsonObject.getString("Email") + "\n"
                                        + "Illness Name: " + jsonObject.getString("Illness_Name") + "\n";
                                responseTextView.append(userDetails);

                                // Append an extra newline character after each user's details
                                responseTextView.append("\n");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "User JSON Parsing Error", e);
                            responseTextView.append("Error parsing User JSON");
                        }
                    });
                } else {
                    Log.e(TAG, "User Request Not Successful: " + response.message());
                    runOnUiThread(() -> responseTextView.append("User Request Not Successful"));
                }
            }
        });
    }

}
