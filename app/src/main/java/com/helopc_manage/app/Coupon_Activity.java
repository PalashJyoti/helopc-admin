package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class Coupon_Activity extends AppCompatActivity implements PromoAdapter.ItemClickListener{

    EditText amount,promocode,deliverycharges,tandoc;
    Button create,addcharges,addtaxcharges;
    RecyclerView recyclerView;

    private ValueEventListener valueEventListener;

    private PromoAdapter adapter;
    private List<Promo> list;

    AlertDialog.Builder builder;
    TextView delc,tnoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        amount = findViewById(R.id.amount);
        promocode = findViewById(R.id.promocode);
        create = findViewById(R.id.create);
        deliverycharges = findViewById(R.id.deliverycharges);
        addcharges = findViewById(R.id.addcharges);
        recyclerView = findViewById(R.id.recyclerView);
        delc = findViewById(R.id.delc);
        tnoc = findViewById(R.id.tnoc);
        tandoc = findViewById(R.id.tc);
        addtaxcharges = findViewById(R.id.addtaxcharges);

        builder = new AlertDialog.Builder(this);




        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Promo_Codes");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapter = new PromoAdapter(getApplicationContext(),list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(Coupon_Activity.this);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Promo upload = postSnapshot.getValue(Promo.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Amount = amount.getText().toString();
                String Promo = promocode.getText().toString().toUpperCase();


                if(Amount==null||Promo==null|| Amount.equals("") || Promo.equals("")){
                    Toast.makeText(getApplicationContext(),"Promo Code or Amount is missing",Toast.LENGTH_SHORT).show();
                }else {
                    Promo promo = new Promo();
                    promo.setCoupon(Promo);
                    promo.setPercent(Integer.parseInt(Amount));
                    FirebaseDatabase.getInstance().getReference("Promo_Codes").child(Promo).setValue(promo);
                    Toast.makeText(getApplicationContext(),"Coupon Code created successfully",Toast.LENGTH_SHORT).show();
                    amount.setText("");
                    promocode.setText("");
                }



            }
        });
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("DeliveryCharges");
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DeliveryCharges user = snapshot.getValue(DeliveryCharges.class);
                if (user != null) {
                    delc.setText("Delivery charges : ₹ "+user.getAmount());
                    
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        addcharges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Price = deliverycharges.getText().toString();
                if(Price==null|| Price.equals("") ){
                    Toast.makeText(getApplicationContext(),"Amount is missing",Toast.LENGTH_SHORT).show();
                }else {
                    DeliveryCharges promo = new DeliveryCharges();
                    promo.setAmount(Price);

                    FirebaseDatabase.getInstance().getReference("DeliveryCharges").setValue(promo);
                    Toast.makeText(getApplicationContext(),"Delivery Charges added successfully",Toast.LENGTH_SHORT).show();
                    deliverycharges.setText("");
                }

            }
        });





        DatabaseReference dbt = FirebaseDatabase.getInstance().getReference("Tax and Other Charges");
        dbt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DeliveryCharges user = snapshot.getValue(DeliveryCharges.class);
                if (user != null) {
                    tnoc.setText("Tax and Other Charges : "+user.getAmount()+"%");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        addtaxcharges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Price = tandoc.getText().toString();
                if(Price==null|| Price.equals("") ){
                    Toast.makeText(getApplicationContext(),"Amount is missing",Toast.LENGTH_SHORT).show();
                }else {
                    DeliveryCharges promo = new DeliveryCharges();
                    promo.setAmount(Price);

                    FirebaseDatabase.getInstance().getReference("Tax and Other Charges").setValue(promo);
                    Toast.makeText(getApplicationContext(),"Tax and Other Charges added successfully",Toast.LENGTH_SHORT).show();
                    tandoc.setText("");
                }

            }
        });
    }

    @Override
    public void ItemClick(int position) {
        builder.setMessage("Are you sure you want to delete the Promo Code")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Promo_Codes").child(list.get(position).getKey());
                        db.removeValue();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Remove Promo Code");
        alert.show();





    }
}
class DeliveryCharges{
    String amount;

    public DeliveryCharges() {
    }

    public DeliveryCharges(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
class Promo{
    String coupon,price,key,city;
    int percent;

    public Promo() {
    }

    public Promo(String coupon, String price) {
        this.coupon = coupon;
        this.price = price;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.viewHolder> {

    private Context mContext;
    private List<Promo> mUploads;
    private ItemClickListener  mListener;
    public PromoAdapter(Context context, List<Promo> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Promo uploadCurrent = mUploads.get(position);
        holder.couponcode.setText(uploadCurrent.getCoupon());
        holder.amount.setText(uploadCurrent.getPercent()+"% price cut");



    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item_layout,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView couponcode,amount;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            couponcode = itemView.findViewById(R.id.couponcode);
            amount = itemView.findViewById(R.id.amount);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.ItemClick(position);
                }
            }
        }


    }
    public interface ItemClickListener {
        void ItemClick(int position);

    }
    public void setOnItemClickListener(ItemClickListener  listener) {
        mListener = listener;
    }

}