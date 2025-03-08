package com.example.counsellorfinal;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Choose extends AppCompatActivity {
    private Button Client;
    private Button Counsellor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Client = findViewById(R.id.client);
        Counsellor = findViewById(R.id.Counsellor);

        Counsellor.setOnClickListener(v -> startActivity(new Intent(Choose.this, CounsellorLogin.class)));
        Client.setOnClickListener(v -> startActivity(new Intent(Choose.this, Register.class)));
    }
}
