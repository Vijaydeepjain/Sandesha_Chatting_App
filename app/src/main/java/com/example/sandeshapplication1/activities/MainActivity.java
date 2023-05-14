package com.example.sandeshapplication1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandeshapplication1.R;
import com.example.sandeshapplication1.utilities.Constant;
import com.example.sandeshapplication1.utilities.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView textname_main;
    ImageView image_profile_main;
    ImageView Signout_main;
    FloatingActionButton floatingActionButton;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textname_main=findViewById(R.id.textName);
        image_profile_main=findViewById(R.id.image_profile);
        Signout_main = findViewById(R.id.imageSignOut);
        floatingActionButton=findViewById(R.id.fabNewChat);

        preferenceManager = new PreferenceManager(getApplicationContext());

        loadUserDetails();
        getToken();
        setListeners();
    }

    private void setListeners()
    {
        Signout_main.setOnClickListener(v -> signOut());
        floatingActionButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));

    }

    private void loadUserDetails()
    {
        textname_main.setText(preferenceManager.getString(Constant.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constant.KEY_IMAGE), Base64.DEFAULT);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        image_profile_main.setImageBitmap(bitmap);


    }

    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private void getToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken (String token)
    {
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        DocumentReference documentReference =database.collection(Constant.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constant.KEY_USER_ID)
        );
        documentReference.update(Constant.KEY_FCM_TOKEN , token)
                .addOnFailureListener(e -> showToast("unable to update token"));
    }
    private void signOut()
    {
        showToast("Signing out ...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constant.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constant.KEY_USER_ID)
                        );
        HashMap<String , Object> updates = new HashMap<>();
        updates.put(Constant.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("unable to signout"));
    }
}