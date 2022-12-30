package com.example.ann;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.LinearLayoutCompat;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;

public class Search extends AppCompatActivity {
    private FirebaseDatabase db= FirebaseDatabase.getInstance() ;
    private DatabaseReference products =db.getReference("products");
    FirebaseStorage storage= FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    EditText search,maxPrice,minPrice;
    Button btn_search;
    LinearLayoutCompat productsSearchDisplay ;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);

        search=findViewById(R.id.search);
       maxPrice=findViewById(R.id.maxPrice);
        minPrice=findViewById(R.id.minPrice);
        btn_search=findViewById(R.id.btn_search);
        productsSearchDisplay = findViewById((R.id.products_search));

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search.getText().toString().isEmpty()){
                    search.setError("Enter a text");
                }
                else {
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
                                    tableProduct[compteur] = new Product(dataProduct.get("productId").toString(),
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
                                getProductsSearch(tri(tableProduct));

                            }
                        }
                    });
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
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp,250,240,false));

            }
        });

    }

    private void getProductsSearch(Product[] listProducts){

        Product p;
        for (int i = 0; i < listProducts.length; i++) {
            //pour chaque produit
            p = listProducts[i];
            if ((p.getProductName().toUpperCase ()).contains(search.getText().toString().toUpperCase()) &&
                    (minPrice.getText().toString().isEmpty() || Float.parseFloat(p.getProductPrice()) >= Float.parseFloat(minPrice.getText().toString())) &&
                    (maxPrice.getText().toString().isEmpty() || Float.parseFloat(p.getProductPrice()) <= Float.parseFloat(maxPrice.getText().toString()))
            ) {
                productsSearchDisplay.removeAllViewsInLayout();

                StorageReference imagesRef = storageReference.child("ProductImages/" + p.getProductId() + "/" + p.getProductImage());
                //LinearlayoutCompat View
                LinearLayoutCompat product = new LinearLayoutCompat(this);
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.product_margin), 0, getResources().getDimensionPixelSize(R.dimen.product_margin));
                product.setOrientation(LinearLayoutCompat.HORIZONTAL);
                product.setBackground(getResources().getDrawable(R.drawable.border));


                //productImage View
                //download image from firebase Storage


                ImageView productImage = new ImageView(this);
                LinearLayout.LayoutParams productImageParams = new LinearLayout.LayoutParams(450, ViewGroup.LayoutParams.MATCH_PARENT);
                productImageParams.setMargins(getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.text_margin), getResources().getDimensionPixelSize(R.dimen.product_margin));
                productImage.setLayoutParams(productImageParams);

                productImage.setPadding(10, 10, 10, 10);
                productImage.setImageDrawable(getResources().getDrawable(R.drawable.download));
                getImageFromFirebaseStorage(p.getProductImage(), p.getProductId(), productImage);


                //verticalLayoutCompat view vl
                LinearLayoutCompat vl = new LinearLayoutCompat(this);
                LinearLayoutCompat.LayoutParams vlparams = new LinearLayoutCompat.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                vl.setOrientation(LinearLayoutCompat.VERTICAL);

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
                productName.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_large));
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
                vl.addView(productPrice, productPriceParams);
                vl.addView(productName, productNameParams);
                vl.addView(userPhone, userPhoneParams);
                vl.addView(productLocal, productLocalParams);
                vl.addView(productDate, productDateParams);
                product.addView(productImage, productImageParams);
                product.addView(vl, vlparams);
                productsSearchDisplay.addView(product, layoutParams);

            }
        }




    }
    public  Product[] tri (@NonNull Product[] products){

        Product p;
        Product[] tmp = products;

        Long date1,date2;
        boolean permut= true;
        int fin  = tmp.length-1;
        String sdate1,sdate2;
        while(permut && fin > 0){

            for(int i =0;i<fin;i++){
                permut=false;
                sdate1=  tmp[i].getProductDate().substring(6,10)+tmp[i].getProductDate().substring(3,5)+
                        tmp[i].getProductDate().substring(0,2) +tmp[i].getProductDate().substring(11,13)+
                        tmp[i].getProductDate().substring(14,16);
                date1=Long.parseLong(sdate1);


                sdate2=tmp[i+1].getProductDate().substring(6,10)+products[i+1].getProductDate().substring(3,5)+
                        tmp[i+1].getProductDate().substring(0,2) +tmp[i+1].getProductDate().substring(11,13)+
                        tmp[i+1].getProductDate().substring(14,16);

                date2=Long.parseLong(sdate2);
                if(date1 < date2){
                    //permutation
                    p=tmp[i];
                    tmp[i]=tmp[i+1];
                    tmp[i+1]=p;
                    permut=true;
                }
            }

            fin--;
        }

        return tmp;


    }
    @Override
    protected void onDestroy() {
        // helper.close();
        super.onDestroy();
    }

}