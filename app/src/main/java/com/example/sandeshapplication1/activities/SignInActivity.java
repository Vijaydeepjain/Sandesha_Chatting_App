package com.example.sandeshapplication1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandeshapplication1.R;
import com.example.sandeshapplication1.utilities.Constant;
import com.example.sandeshapplication1.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignInActivity extends AppCompatActivity {
  TextView movetosignup;
  EditText email_signin;
  EditText password_signin;
  Button signin_button;
  ProgressBar progressBar_signin;
  private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getBoolean(Constant.KEY_IS_SIGNED_IN))
        {
            Intent intent = new Intent (getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
       /* binding = ActivitySignInBinding.inflate(getLayoutInflater());*/
        setContentView(R.layout.activity_sign_in);

        movetosignup=findViewById(R.id.signin_createaccount);
        email_signin=findViewById(R.id.signinemail);
        password_signin=findViewById(R.id.signin_password);
        signin_button=findViewById(R.id.signin_button);
        progressBar_signin=findViewById(R.id.progressBar_signin);

        setListeners();
        //addDataToFirestore();
    }

    private void setListeners()
    {
        movetosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
        signin_button.setOnClickListener(v ->{
            if(isValidSignInDetails())
            {
                signIn();
            }
        });
    }

    private void signIn(){

        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constant.KEY_COLLECTION_USERS)
                .whereEqualTo(Constant.KEY_EMAIL,email_signin.getText().toString())
                .whereEqualTo(Constant.KEY_PASSWORD,password_signin.getText().toString())
                .get()
                .addOnCompleteListener(task ->{
                 if(task.isSuccessful() && task.getResult()!=null &&
                 task.getResult().getDocuments().size()>0)
                 {
                     DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);

                     preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN,true);
                     preferenceManager.putString(Constant.KEY_USER_ID,documentSnapshot.getId());
                     preferenceManager.putString(Constant.KEY_NAME, documentSnapshot.getString(Constant.KEY_NAME));
                     preferenceManager.putString(Constant.KEY_IMAGE,documentSnapshot.getString(Constant.KEY_IMAGE));

                     Intent intent = new Intent (getApplicationContext() , MainActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                 }
                 else {
                     loading(false);
                     showToast("Unable to sign in");
                 }
                });
    }

    private void loading (Boolean isLoading)
    {
        if(isLoading)
        {
            signin_button.setVisibility(View.INVISIBLE);
            progressBar_signin.setVisibility(View.VISIBLE);

        }
        else
        {
            progressBar_signin.setVisibility(View.INVISIBLE);
            signin_button.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message )
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private Boolean isValidSignInDetails() {
        if (email_signin.getText().toString().trim().isEmpty())
        {
            showToast("enter email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email_signin.getText().toString()).matches())
        {
            showToast("enter valid email");
            return false;
        }
        else if (password_signin.getText().toString().trim().isEmpty())
        {
            showToast("enter password");
            return false;
        }
        else {
            return true;
        }
    }
}