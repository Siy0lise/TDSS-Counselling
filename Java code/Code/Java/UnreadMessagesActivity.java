package com.example.counsellorfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UnreadMessagesActivity extends AppCompatActivity {
    private ListView usersListView;
    private List<String> userList;
    private ArrayAdapter<String> adapter;
    private OkHttpClient client;
    private String counsellor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unread_messages);

        usersListView = findViewById(R.id.usersListView);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        usersListView.setAdapter(adapter);

        client = new OkHttpClient();
        counsellor = getIntent().getStringExtra("counsellor");
       // counsellor = "Dr. Fikizolo";
        loadUnreadMessages();

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = userList.get(position);
                Intent intent = new Intent(UnreadMessagesActivity.this, ChatActivity1.class);
                intent.putExtra("sender", selectedUser);
                intent.putExtra("receiver", counsellor);
                startActivity(intent);
            }
        });
    }

    private void loadUnreadMessages() {
        String url = "https://lamp.ms.wits.ac.za/home/s2672925/fetch_unread_messages.php";
        RequestBody formBody = new FormBody.Builder()
                .add("receiver", counsellor)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("UnreadMessagesActivity", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray messages = new JSONArray(responseData);
                                userList.clear();
                                for (int i = 0; i < messages.length(); i++) {
                                    JSONObject messageObj = messages.getJSONObject(i);
                                    String sender = messageObj.getString("Sender");
                                    if (!userList.contains(sender)) {
                                        userList.add(sender);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("UnreadMessagesActivity", "JSON Parsing error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e("UnreadMessagesActivity", "Unsuccessful response: " + response.code());
                }
            }
        });
    }
}
