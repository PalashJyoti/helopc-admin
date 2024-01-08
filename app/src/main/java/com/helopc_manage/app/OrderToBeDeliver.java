package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

import java.util.ArrayList;
import java.util.List;

public class OrderToBeDeliver extends AppCompatActivity implements AdapterOrderID.OnCategoryItemClickListener{
    private RecyclerView recyclerView;
    private TextView textv;

    private AdapterOrderID adapterItem;
    private List<AmountWay> list;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_to_be_deliver);

        recyclerView = findViewById(R.id.recylerview);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Order to be delivered");

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        adapterItem = new AdapterOrderID(getApplicationContext(),list);
        recyclerView.setAdapter(adapterItem);
        adapterItem.setOnItemClickListener(OrderToBeDeliver.this);
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
        Intent intent = new Intent(getApplicationContext(),OneOrderToBeDeliver.class);
        intent.putExtra("keyid",id);
        startActivity(intent);
    }
}