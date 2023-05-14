package com.example.sandeshapplication1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.lights.LightsManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.sandeshapplication1.R;
import com.example.sandeshapplication1.adapters.ChatAdapter;
import com.example.sandeshapplication1.databinding.ActivityChatBinding;
import com.example.sandeshapplication1.models.ChatMessage;
import com.example.sandeshapplication1.models.User;
import com.example.sandeshapplication1.utilities.Constant;
import com.example.sandeshapplication1.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiveUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadRecieverDetails();
        init();
        listenMessages();
    }

    private void init()
    {
        preferenceManager = new PreferenceManager((getApplicationContext()));
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiveUser.image),
                preferenceManager.getString(Constant.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database= FirebaseFirestore.getInstance();

    }

   private void sendMessage()
   {
       HashMap<String ,Object> message = new HashMap<>();
       message.put(Constant.KEY_SENDER_ID,preferenceManager.getString(Constant.KEY_USER_ID));
       message.put(Constant.KEY_RECEIVER_ID ,receiveUser.id);
       message.put(Constant.KEY_MESSAGE,binding.inputMessage.getText().toString());
       message.put(Constant.KEY_TIMESTAMP,new Date());
       database.collection(Constant.KEY_COLLECTION_CHAT).add(message);
       binding.inputMessage.setText(null);
   }

   private void listenMessages()
   {
       database.collection(Constant.KEY_COLLECTION_CHAT)
               .whereEqualTo(Constant.KEY_SENDER_ID,preferenceManager.getString(Constant.KEY_USER_ID))
               .whereEqualTo(Constant.KEY_RECEIVER_ID,receiveUser.id)
               .addSnapshotListener(eventListener);
       database.collection(Constant.KEY_COLLECTION_CHAT)
               .whereEqualTo(Constant.KEY_SENDER_ID,receiveUser.id)
               .whereEqualTo(Constant.KEY_RECEIVER_ID,preferenceManager.getString(Constant.KEY_USER_ID))
               .addSnapshotListener(eventListener);

   }


   private final EventListener<QuerySnapshot>eventListener=(value,error) ->
   {
       if(error!=null)
           return ;
       if(value!=null){
           int count =chatMessages.size();
           for(DocumentChange documentChange:value.getDocumentChanges())
           {
               if(documentChange.getType()== DocumentChange.Type.ADDED)
               {
                   ChatMessage chatMessage =new ChatMessage();
                   chatMessage.senderId=documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                   chatMessage.receiverId=documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                   chatMessage.message=documentChange.getDocument().getString(Constant.KEY_MESSAGE);
                   chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP));
                   chatMessage.dateObject=documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                   chatMessages.add(chatMessage);
               }

           }

           Collections.sort(chatMessages,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
           if(count==0)
           {
               chatAdapter.notifyDataSetChanged();

           }
           else{
               chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
               binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
           }
           binding.chatRecyclerView.setVisibility(View.VISIBLE);
       }
       binding.progressBar.setVisibility(View.GONE);
   };

    private Bitmap getBitmapFromEncodedString (String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);

    }
    private void loadRecieverDetails()
    {
        receiveUser = (User)getIntent().getSerializableExtra(Constant.KEY_USER);
        binding.textName.setText(receiveUser.name);
    }
    private void setListener()
    {
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutsend.setOnClickListener(v -> sendMessage());
    }

    public String getReadableDateTime(Date date)
    {
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);

    }


}