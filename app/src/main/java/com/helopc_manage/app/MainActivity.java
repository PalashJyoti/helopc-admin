package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

public class MainActivity extends AppCompatActivity {

    RelativeLayout orders,orders2bd;
    ImageView reddot,greendot;
    AppCompatButton coupon,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orders = findViewById(R.id.orders);
        reddot = findViewById(R.id.reddot);
        greendot = findViewById(R.id.greendot);
        orders2bd = findViewById(R.id.orders2bd);
        coupon = findViewById(R.id.coupon);
        cancel = findViewById(R.id.cancel);

        new Thread(new Runnable() {
        public void run() {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if(postSnapshot.exists()){
                            reddot.setVisibility(View.VISIBLE);
                        }else {
                            reddot.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Order to be delivered");
            dbr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if(postSnapshot.exists()){
                            greendot.setVisibility(View.VISIBLE);
                        }else {
                            greendot.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }).start();

        AppCompatButton addcategory = findViewById(R.id.addcategory);
        addcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Add_Category.class));
            }
        });

        AppCompatButton addproduct = findViewById(R.id.addproduct);
        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Add_Product.class));
            }
        });

        AppCompatButton list1 = findViewById(R.id.list1);
        list1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),List_1.class));
            }
        });

        AppCompatButton list2 = findViewById(R.id.list2);
        list2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),List_2.class));
            }
        });

        orders2bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),OrderToBeDeliver.class));
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Order_UserID.class));
            }
        });
        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Coupon_Activity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Cancelled_Orders.class));
            }
        });
    }
}