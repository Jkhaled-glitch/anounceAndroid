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

public class SignUp extends AppCompatActivity {
    EditText userEmail,userPassword;
    ImageButton userImage;
    Button btn_SignIn,btn_SignUp;
    private FirebaseAuth mAuth;
    String imagePath="";
    Uri filePath=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        userEmail=  findViewById(R.id.userEmailUp);
        userPassword=  findViewById(R.id.userPasswordUp);
        userImage = findViewById(R.id.userImageUp);
        btn_SignIn=findViewById(R.id.btn_SignIn2);
        btn_SignUp=findViewById(R.id.btn_SignUp2);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userEmail.getText().toString().isEmpty() && !userPassword.getText().toString().isEmpty() &&
                        !imagePath.isEmpty()){



                    mAuth.createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        uploadImage(filePath,"userProfile", userEmail.getText().toString());
                                        startActivity(new Intent(getApplicationContext(),SignIn.class));
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(getApplicationContext(),"failure: "+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    if(userEmail.getText().toString().isEmpty()) {
                        userEmail.setError("Enter Email");
                    }
                    else{
                        if(userPassword.getText().toString().isEmpty()) {
                            userPassword.setError("Enter Password");
                        }
                        else{
                            userImage.setBackground(getResources().getDrawable(R.drawable.errorborder));
                        }
                    }

                }

                }

        });
        btn_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(GalleryIntent,1);
            }
        });

    }
    public void uploadImage(Uri filePath, String imageName, String userEmail)
    {
        FirebaseStorage storage= FirebaseStorage.getInstance();
        StorageReference storageReference=storage.getReference();
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "usersImages/"
                                    +userEmail+"/"
                                    +imageName);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {

                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            " Successful!!",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Error :" + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploading  "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==1) {
            String[] filePathColumn={MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);

            filePath=data.getData();
            if (cursor == null) {
                imagePath = data.getData().getPath();
            }
            else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                imagePath = cursor.getString(idx);
                cursor.close();
            }
            //Transformer la photo en Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //Afficher le Bitmap
            userImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 190, 240, false));
        }
    }

}


