package com.example.counsellorfinal;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ChatActivity1 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private Button sendButton, end;
    private String sender;
    private String receiver;
    private Handler handler;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity1);

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        end = findViewById(R.id.extraButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        sender = getIntent().getStringExtra("sender"); // User who sent the message
        receiver = getIntent().getStringExtra("receiver"); // Counsellor

        // Log the values of sender and receiver to verify they are passed correctly
        Log.d("ChatActivity1", "Sender: " + sender);
        Log.d("ChatActivity1", "Receiver: " + receiver);

        if (sender == null || receiver == null) {
            Toast.makeText(this, "Sender or Receiver is null", Toast.LENGTH_LONG).show();
            return;
        }

        client = new OkHttpClient();

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent= new Intent(ChatActivity1.this,Choose.class);
                startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText("");
                }
            }
        });

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 2000); // Fetch messages every 2 seconds
            }
        }, 0);
    }

    private void loadMessages() {
        String url = "https://lamp.ms.wits.ac.za/home/s2672925/fetch22.php";
        RequestBody formBody = new FormBody.Builder()
                .add("sender", sender)
                .add("receiver", receiver)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("ChatActivity1", "Error: " + e.getMessage());
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
                                messageList.clear();
                                for (int i = 0; i < messages.length(); i++) {
                                    JSONObject messageObj = messages.getJSONObject(i);
                                    String sender = messageObj.getString("Sender");
                                    String receiver = messageObj.getString("Receiver");
                                    String message = messageObj.getString("Message");
                                    String timestamp = messageObj.getString("Timestamp");
                                    boolean unread = messageObj.getInt("Unread") == 1;
                                    messageList.add(new Message(sender, receiver, message, timestamp, unread));
                                }
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("ChatActivity1", "JSON Parsing error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e("ChatActivity1", "Unsuccessful response: " + response.code());
                }
            }
        });
    }

    private void sendMessage(final String message) {
        String url = "https://lamp.ms.wits.ac.za/home/s2672925/send_message.php";
        RequestBody formBody = new FormBody.Builder()
                .add("sender", receiver) // Counsellor sending the message
                .add("receiver", sender) // User receiving the message
                .add("message", message)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("ChatActivity1", "Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    loadMessages(); // Refresh messages after sending
                } else {
                    Log.e("ChatActivity1", "Unsuccessful response: " + response.code());
                }
            }
        });
    }
}
