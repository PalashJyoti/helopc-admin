package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
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

public class Add_Product_Colour extends AppCompatActivity implements AdapterProductColour.OnItemClickListener {
    TextInputEditText name, amount;
    AppCompatButton add;
    RecyclerView recyclerview;

    private AdapterProductColour adapter;
    private List<Model_Product_Colour> list;
    private ValueEventListener valueEventListener;
    AlertDialog.Builder builder;
    String keyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_colour);
        Bundle bundle = getIntent().getExtras();
        keyid = bundle.getString("keyid");

        name = findViewById(R.id.name);
        add = findViewById(R.id.add);
        recyclerview = findViewById(R.id.recyclerview);
        amount = findViewById(R.id.amount);
        builder = new AlertDialog.Builder(this);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Model_Product_Colour modelProduct = new Model_Product_Colour();
                modelProduct.setColour(name.getText().toString().toLowerCase());
                String s = String.format("%.2f", Float.parseFloat(amount.getText().toString()));
                modelProduct.setAmount(s);

                FirebaseDatabase.getInstance().getReference("Product Colour").child(keyid).child(name.getText().toString().toLowerCase()).setValue(modelProduct);

                FirebaseDatabase.getInstance().getReference("Product Colour and Stock").child(keyid).child(name.getText().toString().toLowerCase()).child("items").setValue(0);
                FirebaseDatabase.getInstance().getReference("Product Colour and Stock").child(keyid).child(name.getText().toString().toLowerCase()).child("stock").setValue(false);
            }
        });

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new AdapterProductColour(Add_Product_Colour.this, list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(Add_Product_Colour.this);
        valueEventListener = FirebaseDatabase.getInstance().getReference("Product Colour").child(keyid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Product_Colour upload = postSnapshot.getValue(Model_Product_Colour.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Product_Colour.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Bundle args = new Bundle();
        args.putString("keyid", keyid);
        args.putString("key", list.get(position).getKey());
        args.putString("colour", list.get(position).getColour());
        BottomSheet_Size bottomSheet = new BottomSheet_Size();
        bottomSheet.setArguments(args);
        bottomSheet.show(getSupportFragmentManager(), "Add");
    }
}

class Model_Product_Colour {
    String colour, key;
    String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

class AdapterProductColour extends RecyclerView.Adapter<AdapterProductColour.viewHolder> {

    private Context mContext;
    private List<Model_Product_Colour> mUploads;
    private OnItemClickListener mListener;

    public AdapterProductColour(Context context, List<Model_Product_Colour> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Product_Colour med = mUploads.get(position);
        if (med.getColour() != null) {
            holder.name.setText(UppercaseFirstLetter(med.getColour()));
        }


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description, parent, false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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