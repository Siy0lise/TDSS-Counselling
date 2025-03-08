package com.example.counsellorfinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private Button sendButton, end;
    private String currentUser1, currentUser2;
    private String counsellor1, counsellor2;
    private Handler handler;
    private OkHttpClient client;

    private String currentUser,counsellor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        end = findViewById(R.id.extraButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        currentUser1 = getIntent().getStringExtra("currentUser"); //Client1profile
        counsellor1 = getIntent().getStringExtra("counsellor");//Client1Profile

        currentUser2 = getIntent().getStringExtra("currentUser2"); //Client2profile
        counsellor2 = getIntent().getStringExtra("counsellor2");//Client2Profile

        if (currentUser1 == null & counsellor1==null)
        {
            currentUser=currentUser2;
            counsellor=counsellor2;
        }
        else
        {
            currentUser=currentUser1;
            counsellor=counsellor1;
        }


       // currentUser = "Zee"; // This should be fetched dynamically
        //counsellor = "Dr. Babalwa"; // This should be fetched dynamically



        client = new OkHttpClient();

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent= new Intent(ChatActivity.this,Choose.class);
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
                handler.postDelayed(this, 5000); // Fetch messages every 2 seconds
            }
        }, 0);
    }

    private void loadMessages() {
        String url = "https://lamp.ms.wits.ac.za/home/s2672925/fetch2.php";
        RequestBody formBody = new FormBody.Builder()
                .add("sender", currentUser)
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
                Log.e("ChatActivity", "Error: " + e.getMessage()); // Log the error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    Log.d("ChatActivity", "Response Data: " + responseData); // Log the response data
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
                                    int unread = messageObj.getInt("Unread"); // Get unread as an integer
                                    boolean isUnread = unread == 1; // Convert it to boolean
                                    messageList.add(new Message(sender, receiver, message, timestamp, isUnread));
                                }
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the last message
                                Log.d("ChatActivity", "Messages: " + messageList.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("ChatActivity", "JSON Parsing error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.e("ChatActivity", "Unsuccessful response: " + response.code());
                }
            }
        });
    }

    private void sendMessage(final String message) {
        String url = "https://lamp.ms.wits.ac.za/home/s2672925/send_message.php";
        RequestBody formBody = new FormBody.Builder()
                .add("sender", currentUser)
                .add("receiver", counsellor)
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
                Log.e("ChatActivity", "Error: " + e.getMessage()); // Log the error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    loadMessages(); // Refresh messages after sending
                } else {
                    Log.e("ChatActivity", "Unsuccessful response: " + response.code());
                }
            }
        });
    }
}
