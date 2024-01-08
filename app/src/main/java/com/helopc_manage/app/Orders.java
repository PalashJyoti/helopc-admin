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
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

import java.util.ArrayList;
import java.util.List;

public class Orders extends AppCompatActivity implements AdapterOrderID.OnCategoryItemClickListener{
    private RecyclerView recyclerView;
    private TextView textv;

    private AdapterOrderID adapterItem;
    private List<AmountWay> list;
    private ValueEventListener valueEventListener;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders2);

        textv = findViewById(R.id.textv);
        recyclerView = findViewById(R.id.recylerview);


        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("id");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid);
        textv.setText("Orders from "+uid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterItem = new AdapterOrderID(getApplicationContext(),list);
        recyclerView.setAdapter(adapterItem);
        adapterItem.setOnItemClickListener(Orders.this);
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
        Intent intent = new Intent(getApplicationContext(),One_Order.class);
        intent.putExtra("uid",uid);
        intent.putExtra("id",id);
        startActivity(intent);
    }
}
class AdapterOrderID extends RecyclerView.Adapter<AdapterOrderID.viewHolder> {

    private Context mContext;
    private List<AmountWay> mUploads;
    private OnCategoryItemClickListener  mListener;
    public AdapterOrderID(Context context, List<AmountWay> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AmountWay uploadCurrent = mUploads.get(position);
        holder.orderid.setText(uploadCurrent.getKey());
        holder.amount.setText("Rs. "+uploadCurrent.getAmount());
        holder.way.setText(uploadCurrent.getWay());
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordersid,parent,false);
        return new viewHolder(view);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView orderid,amount,way;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            orderid = itemView.findViewById(R.id.orderid);
            amount = itemView.findViewById(R.id.amount);
            way = itemView.findViewById(R.id.way);

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


class AmountWay{
    String amount,way,key;

    public AmountWay() {
    }

    public AmountWay(String amount, String way, String key) {
        this.amount = amount;
        this.way = way;
        this.key = key;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
