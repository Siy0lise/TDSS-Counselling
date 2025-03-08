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

public class Register extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    private TextView display;
    private EditText user, pass, email;
    private Button Login;
    private Button Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // display = findViewById(R.id.textView2);
        user = findViewById(R.id.usernameText);
        pass = findViewById(R.id.PasswordET);
        email = findViewById(R.id.Email);
        //   Register = findViewById(R.id.regButton);
        Register = findViewById(R.id.signUpBtn);
        Login = findViewById(R.id.Login);

        Login.setOnClickListener(v -> startActivity(new Intent(Register.this, ClientLogin.class)));

        Register.setOnClickListener(this::REGISTER);
    }

    public void REGISTER(View view) {
        String username = user.getText().toString();
        String password = pass.getText().toString();
        String emailStr = email.getText().toString();

        if (username.isEmpty() || password.isEmpty() || emailStr.isEmpty()) {
            Toast.makeText(this, "Please enter username, password and email", Toast.LENGTH_SHORT).show();
            return;
        }

        post("https://lamp.ms.wits.ac.za/home/s2672925/clientsRegister.php", username, password, emailStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(Register.this, "Request failed. Please try again.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    runOnUiThread(() -> processJSON(responseStr));
                } else {
                    runOnUiThread(() -> Toast.makeText(Register.this, "Invalid response from server.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    Call post(String url, String username, String password, String email, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("email", email)
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
            String userPR = user.getText().toString();
            if (message.equalsIgnoreCase("Success"))
            {
                Intent intent = new Intent(Register.this, Home.class);
                intent.putExtra("userPR", userPR);
                startActivity(intent);
               // startActivity(new Intent(Register.this, Home.class));
            } else {
                Toast.makeText(this, "Invalid login. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing response.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
