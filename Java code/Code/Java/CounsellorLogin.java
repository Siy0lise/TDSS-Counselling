package com.example.counsellorfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CounsellorLogin extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    private TextView responseTextView;
    private EditText u, p;
    private Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_counsellor_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        u = findViewById(R.id.emailET);
        p = findViewById(R.id.passwordET);
        Login = findViewById(R.id.signInBtn);
        Login.setOnClickListener(this::LOGIN);

    }

    public void LOGIN(View view) {
        String username = u.getText().toString();
        String password = p.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        post("https://lamp.ms.wits.ac.za/home/s2672925/counsellors.php", username, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> responseTextView.setText("Request failed. Please try again."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    runOnUiThread(() -> processJSON(responseStr));
                } else {
                    runOnUiThread(() -> responseTextView.setText("Invalid response from server."));
                }
            }
        });
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    Call post(String url, String username, String password, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private void processJSON(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String message = jsonResponse.getString("message");
            String userPassed= u.getText().toString();
            if (message.equalsIgnoreCase("Success"))
            {
                Intent in = new Intent(CounsellorLogin.this,CounsellorProfile.class);
                in.putExtra("userPassed",userPassed);
                startActivity(in);
            } else {
                Toast.makeText(this, "Invalid login. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

