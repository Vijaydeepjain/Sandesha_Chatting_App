package com.example.sandeshapplication1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sandeshapplication1.adapters.UsersAdapter;
import com.example.sandeshapplication1.databinding.ActivityUsersBinding;
import com.example.sandeshapplication1.listeners.UserListener;
import com.example.sandeshapplication1.models.User;
import com.example.sandeshapplication1.utilities.Constant;
import com.example.sandeshapplication1.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.index.qual.EnsuresLTLengthOf;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();

    }
    private void setListeners()
    {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }



    private void getUsers()
    {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constant.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constant.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult()!=null)
                    {
                        List< User> users =new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                        {
                            if(currentUserId.equals(queryDocumentSnapshot.getId()))
                            {
                                continue;
                            }
                            User user = new User();
                            user.name=queryDocumentSnapshot.getString(Constant.KEY_NAME);
                            user.email=queryDocumentSnapshot.getString(Constant.KEY_EMAIL);
                            user.image=queryDocumentSnapshot.getString(Constant.KEY_IMAGE);
                            user.token =queryDocumentSnapshot.getString(Constant.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();

                            users.add(user);

                        }
                        if(users.size()>0)
                        {
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                        else {
                            showErrorMessage();
                        }
                    }
                    else
                    {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage()
    {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }



    private void loading (Boolean isLoading)
    {
        if(isLoading)
        {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked (User user )
    {
        Intent intent = new Intent (getApplicationContext() , ChatActivity.class);
        intent.putExtra(Constant.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}