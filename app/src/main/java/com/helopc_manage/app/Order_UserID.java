package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Order_UserID extends AppCompatActivity implements AdapterUserID.OnCategoryItemClickListener{

    private RecyclerView recyclerView;


    private AdapterUserID adapterItem;
    private List<userID> list;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        recyclerView = findViewById(R.id.recylerview);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterItem = new AdapterUserID(getApplicationContext(),list);
        recyclerView.setAdapter(adapterItem);
        adapterItem.setOnItemClickListener(Order_UserID.this);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    userID upload = postSnapshot.getValue(userID.class);
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
        userID selectedItem = list.get(position);
        String id = selectedItem.getKey();
        Intent intent = new Intent(getApplicationContext(),Orders.class);
        intent.putExtra("id",id);
        startActivity(intent);


    }
}


class AdapterUserID extends RecyclerView.Adapter<AdapterUserID.viewHolder> {

    private Context mContext;
    private List<userID> mUploads;
    private OnCategoryItemClickListener  mListener;
    public AdapterUserID(Context context, List<userID> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        userID uploadCurrent = mUploads.get(position);
        holder.userid.setText(uploadCurrent.getKey());
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
        return new viewHolder(view);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView userid;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userid = itemView.findViewById(R.id.userid);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick(position);
                }
            }
        }
    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);
    }
    public void setOnItemClickListener(OnCategoryItemClickListener  listener) {
        mListener = listener;
    }

}
class userID{
    String key;

    public userID() {
    }

    public userID(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
