package com.example.counsellorfinal;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ClientProfile extends AppCompatActivity {
    private static final String TAG = "ClientProfile";
    private String item;
    String counsellorName;
    String userPR;
    String userLog;
    String userFinal;
    private TextView responseTextView;
    private TextView out;
    private TextView out1;
    private Button Login;
    private Button Chat;

    private Button Udetails;
    private OkHttpClient client = new OkHttpClient();




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userPR = getIntent().getStringExtra("userPR"); //From register
        counsellorName = getIntent().getStringExtra("counsellorName");//From Home
        item = getIntent().getStringExtra("item");// From Home

        if (userLog == null)
        {
            userFinal = userPR;
            userPR = null;
        } else
        {
            userFinal = userLog;
            userLog = null;
        }


        out1 = findViewById(R.id.textView5);
        out = findViewById(R.id.nameDoc);

        responseTextView = findViewById(R.id.response_text);

        if (counsellorName != null && item != null) {
            out.setText("Your Counsellor for " + item + ": " + counsellorName);
        } else {
            Log.e(TAG, "Intent extras are null");
            out.setText("Counsellor details are missing.");
        }
        Udetails = findViewById(R.id.button);
        Login = findViewById(R.id.view);
        Chat=findViewById(R.id.chatbutton);
        Login.setOnClickListener(this::LOGIN);
        Udetails.setOnClickListener(this::Udetail);
        Chat.setOnClickListener(this::GoChat);


        // Update assignments table with user, counsellor, and illness details
        updateAssignmentsTable(userFinal, counsellorName, item);
    }

    private void GoChat(View view)
    {
        Intent intent = new Intent(ClientProfile.this, ChatActivity.class);
        intent.putExtra("currentUser", userFinal);
        intent.putExtra("counsellor",counsellorName);
        startActivity(intent);
    }

    private void LOGIN(View view) {
        // Request for Counsellor's details
        RequestBody counsellorFormBody = new FormBody.Builder()
                .add("doctor", counsellorName)  // Send counsellorName for counsellor details
                .build();

        Request counsellorRequest = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/profile.php")
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
                    Log.d(TAG, "Response Data: " + responseData); // Log the response
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            if (jsonObject.has("error")) {
                                responseTextView.setText(jsonObject.getString("error"));
                            } else {
                                // Ensure the JSON keys match those in the database
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

    private void updateAssignmentsTable(String username, String counsellorName, String illnessName)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("counsellor_name", counsellorName)
                .add("illness_name", illnessName)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2672925/assignment.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Assignment Update Failed", e);
                runOnUiThread(() -> responseTextView.setText("Assignment Update Failed"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ClientProfile.this, "Assignment Updated", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e(TAG, "Assignment Update Not Successful: " + response.message());
                    runOnUiThread(() -> responseTextView.setText("Assignment Update Not Successful"));
                }
            }
        });
    }


    private void Udetail(View view)
    {
        // Request for User's details
        RequestBody userFormBody = new FormBody.Builder()
                .add("username", userFinal)  // Send username for user details
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
                                        + "Illness Name: " + jsonObject.getString("Illness_Name");
                                responseTextView.append(userDetails);
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



