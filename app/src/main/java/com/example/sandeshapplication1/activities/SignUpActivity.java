package com.example.sandeshapplication1.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sandeshapplication1.R;
import com.example.sandeshapplication1.utilities.Constant;
import com.example.sandeshapplication1.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    TextView movetosignin;
    EditText name_signup;
    private String encodedImage;
    EditText email_signup;
    EditText password_signup;

    Button button_signup;

   ProgressBar progressBar;

   ImageView icon_signup;

   TextView icontext_signup;

   FrameLayout frameLayout_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
       // ActivitySignupBinding binding;
       preferenceManager = new PreferenceManager(getApplicationContext());
        movetosignin = findViewById(R.id.signup_move_page);
        name_signup= findViewById(R.id.signup_name);
        email_signup= findViewById(R.id.signup_email);
        password_signup=findViewById(R.id.signup_password);
        button_signup=findViewById(R.id.signup_button);
        progressBar=findViewById(R.id.signup_progressbar);
        icon_signup = findViewById(R.id.imageicon_signup);
        icontext_signup=findViewById(R.id.textimage_signup);
        frameLayout_signup = findViewById(R.id.layoutImage);


        setListeners();
    }

     private void setListeners(){
        movetosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(getApplicationContext() , SignInActivity.class);
                startActivity(intent);
            }
        });
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidSignUpDetail()){
                    signUp();
                }

            }
        });

        frameLayout_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
     }

     private void showToast(String message)
     {
         Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
     }
     private void signUp()
     {
         loading(true);
         FirebaseFirestore database = FirebaseFirestore.getInstance();
         HashMap<String , Object> user =new HashMap<>();
         user.put(Constant.KEY_NAME,name_signup.getText().toString());
         user.put(Constant.KEY_EMAIL,email_signup.getText().toString());
         user.put(Constant.KEY_PASSWORD,password_signup.getText().toString());
         user.put(Constant.KEY_IMAGE,encodedImage);
         database.collection(Constant.KEY_COLLECTION_USERS)
                 .add(user)
                 .addOnSuccessListener(documentReference -> {
                     loading(false);
                     preferenceManager.putBoolean(Constant.KEY_IS_SIGNED_IN,true);
                     preferenceManager.putString(Constant.KEY_USER_ID,documentReference.getId());
                     preferenceManager.putString(Constant.KEY_NAME,name_signup.getText().toString());
                     preferenceManager.putString(Constant.KEY_IMAGE,encodedImage);
                     Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
                     startActivity(intent);
                 })
                 .addOnFailureListener(exception ->{
                   loading(false);
                   showToast(exception.getMessage());
                 });
     }

     private String encodedImage(Bitmap bitmap)
     {
         int previewWidth =150;
         int previewHeight = bitmap.getHeight()*previewWidth/ bitmap.getWidth();
         Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50 ,byteArrayOutputStream);
         byte [] bytes =byteArrayOutputStream.toByteArray();
         return Base64.encodeToString(bytes,Base64.DEFAULT);

     }
     private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
             new ActivityResultContracts.StartActivityForResult(),
             result -> {
                 if(result.getResultCode()== RESULT_OK){
                     if(result.getData()!=null)
                     {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            icon_signup.setImageBitmap(bitmap);
                            icontext_signup.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);


                        }
                        catch (FileNotFoundException e)
                        {
                             e.printStackTrace();
                        }
                     }
                 }
             }
     );

     private Boolean isValidSignUpDetail()
     {
         Math patterns;
         if(encodedImage==null)
         {
             showToast("select profile image");
             return false;
         }
         else if(name_signup.getText().toString().trim().isEmpty())
         {
             showToast("Enter name");
             return false;
         }
         else if(email_signup.getText().toString().trim().isEmpty())
         {
             showToast("Enter Email");
             return false;
         }
         else if(!Patterns.EMAIL_ADDRESS.matcher(email_signup.getText().toString()).matches())
         {
             showToast("enter valid email");
             return false;
         }
         else if(password_signup.getText().toString().trim().isEmpty())
         {
           showToast("enter password");
           return false;
         }
         else
         {
             return true;
         }
     }
     private void loading (Boolean isLoading)
     {
         if(isLoading)
         {
             button_signup.setVisibility(View.INVISIBLE);
             progressBar.setVisibility(View.VISIBLE);
         }
         else
         {
             progressBar.setVisibility(View.INVISIBLE);
             button_signup.setVisibility(View.VISIBLE);
         }
     }
}