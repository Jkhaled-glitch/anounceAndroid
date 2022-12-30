package com.example.ann;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class AddProduct extends AppCompatActivity {
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    EditText productName,productPrice,productLocal,productUserPhone;
    ImageButton productImage;
    Button btn_add;
    String imagePath="";
    Uri filePath=null;
    //db and Products
    FirebaseDatabase db;
    DatabaseReference products;

    Product newProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addproduct);
        productName = findViewById(R.id.nom);
        productPrice = findViewById(R.id.price);
        productLocal = findViewById(R.id.local);
        productImage = findViewById(R.id.image);
        productUserPhone=findViewById(R.id.phone);
        btn_add = findViewById(R.id.btn_add);

        db = FirebaseDatabase.getInstance();
        products = db.getReference("products");



        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent galleryIntent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               startActivityForResult(galleryIntent,1);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!productName.getText().toString().isEmpty() || !productPrice.getText().toString().isEmpty()
                        || !productLocal.getText().toString().isEmpty() || !imagePath.isEmpty() ||
                   !productUserPhone.getText().toString().isEmpty() ) {
                    SimpleDateFormat datFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    addNewProduct( productName.getText().toString(),
                            productPrice.getText().toString(),
                            productLocal.getText().toString(),
                            imagePath,
                            datFormat.format(new Date()),
                            productUserPhone.getText().toString(),
                            currentUser.getEmail()

                    );
                    //Reset allValues values
                    productName.setText("");
                    productPrice.setText("");
                    productLocal.setText("");
                    productUserPhone.setText("");
                    productImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_to_photos_24));
                    }
                 else {
                    if(productName.getText().toString().isEmpty()) {
                        productName.setError("Enter Title");
                    }
                    else {
                        if (productPrice.getText().toString().isEmpty()) {
                            productPrice.setError("Enter Price");
                        }
                        else {
                                if (productLocal.getText().toString().isEmpty()) {
                                    productLocal.setError("Enter Region");
                                }
                                else {
                                    if(productUserPhone.getText().toString().isEmpty()) {
                                        productUserPhone.setError("Enter Your number");
                                    }
                                    else{
                                        productImage.setBackground(getResources().getDrawable(R.drawable.errorborder));
                                    }
                                }

                        }
                    }
                 }
            }

        });

    }

    public void uploadImage(Uri filePath,String imageName,String productId)
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
                            "productImages/"
                                    +productId+"/"
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
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
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

    private void addNewProduct(String productName, String productPrice,String productLocal,String imagePath,String productDate, String userPhone,String userEmail) {

        db.getReference("lastProductId").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String lastProductId=String.valueOf(task.getResult().getValue());
                            String newProductId=String.valueOf(Integer.parseInt(lastProductId) +1 );
                            String imageName=UUID.randomUUID().toString();
                            Product newProduct = new Product(newProductId,productName, productPrice, productLocal, imageName, productDate,  userPhone,userEmail);
                            uploadImage(filePath,imageName,newProductId);
                            db.getReference().child("lastProductId").setValue( newProductId);
                            products.child(newProductId).setValue(newProduct);

                        }
                    }
                });



        //

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
            productImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 190, 240, false));
        }
    }

}

