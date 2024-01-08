package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Add_Product_description extends AppCompatActivity implements AdapterProductDescription.OnItemClickListener{
    TextInputEditText name;
    AppCompatButton add;
    RecyclerView recyclerview;

    private AdapterProductDescription adapter;
    private List<Model_Product_Description> list;
    private ValueEventListener valueEventListener;
    AlertDialog.Builder builder;
    String keyid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_description);

        Bundle bundle = getIntent().getExtras();
        keyid = bundle.getString("keyid");

        name = findViewById(R.id.name);
        add = findViewById(R.id.add);
        recyclerview = findViewById(R.id.recyclerview);
        builder = new AlertDialog.Builder(this);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Model_Product_Description modelProduct = new Model_Product_Description();
                modelProduct.setDescription(name.getText().toString());

                FirebaseDatabase.getInstance().getReference("Product Description").child(keyid).push().setValue(modelProduct);
            }
        });

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new AdapterProductDescription(Add_Product_description.this,list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(Add_Product_description.this);
        valueEventListener = FirebaseDatabase.getInstance().getReference("Product Description").child(keyid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Product_Description upload = postSnapshot.getValue(Model_Product_Description.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Product_description.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 31) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    @Override
    public void onItemClick(int position) {

        builder.setMessage("Do you want to Delete the Description ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseDatabase.getInstance().getReference("Product Description").child(keyid).child(list.get(position).getKey()).removeValue();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete Description");
        alert.show();
    }
}

class Model_Product_Description{
    String description,key;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

class AdapterProductDescription extends RecyclerView.Adapter<AdapterProductDescription.viewHolder> {

    private Context mContext;
    private List<Model_Product_Description> mUploads;
    private OnItemClickListener mListener;
    public AdapterProductDescription(Context context, List<Model_Product_Description> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Product_Description med = mUploads.get(position);
        if(med.getDescription()!=null){
            holder.name.setText("* "+UppercaseFirstLetter(med.getDescription()));
        }



    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description,parent,false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    private String UppercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }

}