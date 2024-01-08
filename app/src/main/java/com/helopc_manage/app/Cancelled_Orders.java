package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

import java.util.ArrayList;
import java.util.List;

public class Cancelled_Orders extends AppCompatActivity implements AdapterOrderID.OnCategoryItemClickListener{
    private RecyclerView recyclerView;

    private AdapterOrderID adapterItem;
    private List<AmountWay> list;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancelled_orders);
        recyclerView = findViewById(R.id.recyclerView);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cancelled");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterItem = new AdapterOrderID(getApplicationContext(),list);
        recyclerView.setAdapter(adapterItem);
        adapterItem.setOnItemClickListener(Cancelled_Orders.this);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    AmountWay upload = postSnapshot.getValue(AmountWay.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapterItem.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCategoryItemClick(int position) {
        AmountWay selectedItem = list.get(position);
        String id = selectedItem.getKey();
        Intent intent = new Intent(getApplicationContext(),One_Cancelled_Orders.class);
        intent.putExtra("keyid",id);
        startActivity(intent);
    }}