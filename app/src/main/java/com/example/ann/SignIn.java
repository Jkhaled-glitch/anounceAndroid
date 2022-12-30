
package com.example.ann;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SignIn extends AppCompatActivity {
    EditText userEmail,userPassword;
    Button btn_SignIn,btn_SignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        userEmail = findViewById(R.id.userEmail1);
        userPassword = findViewById(R.id.userPassword1);
        btn_SignIn=findViewById(R.id.btn_SignIn);
        btn_SignUp=findViewById(R.id.btn_SignUp);
        mAuth = FirebaseAuth.getInstance();





        btn_SignIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (!userEmail.getText().toString().isEmpty() && !userPassword.getText().toString().isEmpty() ) {
                   mAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                           .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {
                                       // Sign in success, update UI with the signed-in user's information
                                       FirebaseUser user = mAuth.getCurrentUser();
                                       Toast.makeText(getApplicationContext(), "successful !", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(SignIn.this,ScrollingActivity.class);
                                       startActivity(intent);
                                   }
                                   else {
                                       Toast.makeText(getApplicationContext(), "Authentication failed: \n"+task.getException().getMessage(),
                                               Toast.LENGTH_LONG).show();

                                   }
                               }
                           });
               }
               else {
                   if(userEmail.getText().toString().isEmpty())
                       userEmail.setError("Enter your email");
                   else {
                       if (userPassword.getText().toString().isEmpty())
                           userPassword.setError("Enter password");
                   }
               }
           }

        });

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
            }
        });
    }
}


