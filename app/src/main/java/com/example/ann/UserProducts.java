package com.example.ann;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.NestedScrollView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;

public class UserProducts extends AppCompatActivity {

    private FirebaseDatabase db= FirebaseDatabase.getInstance() ;
    private DatabaseReference products =db.getReference("products");
    FirebaseStorage storage= FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    private FirebaseAuth mAuth;




    EditText search,maxPrice,minPrice;
    Button btn_search;
    LinearLayoutCompat user_products ;

    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userproducts);

        user_products = findViewById((R.id.user_products));

        products.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Connect Error", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap dataMap = (HashMap) task.getResult().getValue();
                    Product[] tableProduct = new Product[dataMap.size()];
                    int compteur = 0;
                    for (Object i : dataMap.values()) {
                        HashMap dataProduct = (HashMap) i;
                        tableProduct[compteur] = new Product(
                                dataProduct.get("productId").toString(),
                                dataProduct.get("productName").toString(),
                                dataProduct.get("productPrice").toString(),
                                dataProduct.get("productLocal").toString(),
                                dataProduct.get("productImage").toString(),
                                dataProduct.get("productDate").toString(),
                                dataProduct.get("userPhone").toString(),
                                dataProduct.get("userEmail").toString()
                        );
                        compteur++;
                    }
                    getUserProducts(tableProduct);

                }
            }
        });
    }

    private void getImageFromFirebaseStorage(String name, String productId, ImageView imageView){
        // Create a storage reference from our app


        FirebaseStorage storage= FirebaseStorage.getInstance();
        // Create a reference with an initial file path and name
        StorageReference imageReference = storage.getReference().child("productImages/"+productId+"/"+name);
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convert bytes data into a Bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp,300,300,false));

            }
        });

    }
    private void DeleteImageFromFirebaseStorage(String productId,String imageName){
        StorageReference imageRef =storage.getReference().child("productImages/"+productId+"/"+imageName);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Deleting Product
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void DeleteProductFromFirebase(String productId,String imageName){
        DatabaseReference productRef = db.getReference("products/"+productId);
        productRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Deleting Successfill",Toast.LENGTH_SHORT).show();
                DeleteImageFromFirebaseStorage(productId,imageName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failure :\n"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserProducts(Product[] listProducts){

        Product p;
        String email=mAuth.getCurrentUser().getEmail();
        for (int i = 0; i < listProducts.length; i++) {

            //pour chaque produit
            p = listProducts[i];

            if ((mAuth.getCurrentUser().getEmail()).equals(p.getUserEmail()) ) {

                StorageReference imagesRef = storageReference.child("ProductImages/" + p.getProductId() + "/" + p.getProductImage());
                //LinearlayoutCompat Product View
                LinearLayoutCompat product = new LinearLayoutCompat(this);
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.product_margin), 0, getResources().getDimensionPixelSize(R.dimen.product_margin));
                product.setOrientation(LinearLayoutCompat.HORIZONTAL);
                product.setBackground(getResources().getDrawable(R.drawable.border));


                LinearLayoutCompat vl1 = new LinearLayoutCompat(this);
                LinearLayoutCompat.LayoutParams vl1params = new LinearLayoutCompat.LayoutParams(
                        350, LinearLayout.LayoutParams.WRAP_CONTENT);
                vl1.setOrientation(LinearLayoutCompat.VERTICAL);


                //productImage View
                //download image from firebase Storage


                ImageView productImage = new ImageView(this);
                LinearLayout.LayoutParams productImageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                productImageParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                productImage.setLayoutParams(productImageParams);

                productImage.setPadding(10, 10, 10, 10);
                productImage.setImageDrawable(getResources().getDrawable(R.drawable.download));
                getImageFromFirebaseStorage(p.getProductImage(), p.getProductId(), productImage);

                //Delete Button
                Button buttonDelete = new Button(this);
                LinearLayout.LayoutParams buttonDeleteParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                buttonDeleteParams.setMargins( getResources().getDimensionPixelSize(R.dimen.product_margin),getResources().getDimensionPixelSize(R.dimen.product_margin),getResources().getDimensionPixelSize(R.dimen.product_margin),getResources().getDimensionPixelSize(R.dimen.product_margin));
                buttonDelete.setLayoutParams(buttonDeleteParams);

                buttonDelete.setPadding(10, 10, 10, 10);
                buttonDelete.setText("Delete");
                buttonDelete.setBackground(getResources().getDrawable(R.drawable.errorborder));
                buttonDelete.setTextColor(getResources().getColor(R.color.purple_200));
                buttonDelete.setTypeface(buttonDelete.getTypeface(), Typeface.BOLD_ITALIC);
                Product finalP = p;
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        confirmDelete(finalP.getProductName(),finalP.getProductId(),finalP.getProductImage());
                    }
                });



                //verticalLayoutCompat view vl
                LinearLayoutCompat vl2 = new LinearLayoutCompat(this);
                LinearLayoutCompat.LayoutParams vl2params = new LinearLayoutCompat.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                vl2.setOrientation(LinearLayoutCompat.VERTICAL);

                //productPrice View
                TextView productPrice = new TextView(this);
                LinearLayout.LayoutParams productPriceParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                productPriceParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                productPrice.setLayoutParams(productPriceParams);
                productPrice.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_large));
                productPrice.setText(p.getProductPrice() + " DT");
                productPrice.setTextColor(getResources().getColor(R.color.purple_500));
                productPrice.setTypeface(productPrice.getTypeface(), Typeface.BOLD_ITALIC);

                // productName View
                TextView productName = new TextView(this);
                LinearLayout.LayoutParams productNameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                productNameParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                productName.setLayoutParams(productNameParams);
                productName.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_Small));
                productName.setText(p.getProductName());
                productName.setTextColor(getResources().getColor(R.color.purple_500));
                productName.setTypeface(productName.getTypeface(), Typeface.BOLD_ITALIC);

                //userPhoneView
                TextView userPhone = new TextView(this);
                LinearLayout.LayoutParams userPhoneParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                userPhoneParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                userPhone.setLayoutParams(userPhoneParams);
                userPhone.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_Small));
                userPhone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_phone_24, 0, 0, 0);
                userPhone.setCompoundDrawablePadding(15);
                userPhone.setText(p.getUserPhone());

                //productLocal View
                TextView productLocal = new TextView(this);
                LinearLayout.LayoutParams productLocalParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                productLocalParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                productLocal.setLayoutParams(productLocalParams);
                productLocal.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_Small));
                productLocal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_place_24, 0, 0, 0);
                productLocal.setCompoundDrawablePadding(15);
                productLocal.setText(p.getProductLocal());

                //productDate View
                TextView productDate = new TextView(this);
                LinearLayout.LayoutParams productDateParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                productDateParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                productDate.setLayoutParams(productDateParams);
                productDate.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_Small));
                productDate.setPadding(0, 0, 0, 10);
                productDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_access_time_24, 0, 0, 0);
                productDate.setCompoundDrawablePadding(15);
                productDate.setText(p.getProductDate());

                //ajouter les Views au layout parents
                vl1.addView(productImage,productImageParams);
                vl1.addView(buttonDelete,buttonDeleteParams);

                vl2.addView(productPrice, productPriceParams);
                vl2.addView(productName, productNameParams);
                vl2.addView(userPhone, userPhoneParams);
                vl2.addView(productLocal, productLocalParams);
                vl2.addView(productDate, productDateParams);

                product.addView(vl1, vl1params);
                product.addView(vl2, vl2params);
                user_products.addView(product, layoutParams);

            }
        }
    }
    public void confirmDelete(String productName ,String productId, String imageName){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Dialog on Android");
        dialog.setMessage("Are you sure you want to delete '"+productName +"' ?" );
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
                DeleteProductFromFirebase(productId,imageName);
            }
        })
                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }


    @Override
    protected void onDestroy() {
        // helper.close();
        super.onDestroy();
    }

}