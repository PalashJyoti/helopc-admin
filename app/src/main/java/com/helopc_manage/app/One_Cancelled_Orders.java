package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

import java.util.ArrayList;
import java.util.List;

public class One_Cancelled_Orders extends AppCompatActivity implements AdapterOrderItem.OnCategoryItemClickListener{
    private TextView orderid,amount,way,name,email,phone,city,area,houseno,apart,landmark,callnow,road,pincode,date,cdate;
    private RecyclerView recyclerView;

    private AdapterOrderItem adapterOrderItem;
    private List<Model_Cart> list;
    String Phone,Way,Name,Road,Area,Landmark,PIN,Amount,Mode;;
    int phone_permission = 1;
    AppCompatButton delete;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_cancelled_orders);
        Bundle bundle = getIntent().getExtras();
        String keyid = bundle.getString("keyid");

        cdate = findViewById(R.id.cdate);
        date = findViewById(R.id.date);
        pincode = findViewById(R.id.pincode);
        callnow = findViewById(R.id.callnow);
        road = findViewById(R.id.road);
        orderid = findViewById(R.id.orderid);
        amount = findViewById(R.id.amount);
        way = findViewById(R.id.way);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        area = findViewById(R.id.area);
        houseno = findViewById(R.id.houseno);
        apart = findViewById(R.id.apart);
        landmark = findViewById(R.id.landmark);
        recyclerView = findViewById(R.id.recylerview);
        delete = findViewById(R.id.delete);

        orderid.setText("Order id : "+keyid);
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Cancelled").child(keyid);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AmountWay amountWay = snapshot.getValue(AmountWay.class);
                if(amountWay != null){
                    amount.setText("Amount : "+amountWay.getAmount());
                    Amount = amountWay.getAmount();
                    way.setText("Mode of payment : "+amountWay.getWay());
                    Mode = amountWay.getWay();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference dbd = FirebaseDatabase.getInstance().getReference().child("Cancelled").child(keyid).child("Shipping Date");
        dbd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShippingDate amountWay = snapshot.getValue(ShippingDate.class);
                if(amountWay != null){
                    date.setText("Shipping Date : "+amountWay.getShippingDate());
                    cdate.setText("Confirm Time : "+amountWay.getOrderConfirmTime());

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(amountWay.getUid()).child("Address");
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Address user = snapshot.getValue(Address.class);
                            if (user != null) {
                                apart.setText("Apartment, Building : "+user.getApartment());
                                city.setText("City : "+user.getCity());
                                area.setText("Area : "+user.getArea());
                                houseno.setText("House no. : "+user.getHouseno());
                                landmark.setText("Landmark : "+user.getLandmark());
                                road.setText("Road : "+user.getRoad());
                                pincode.setText("Pin Code : "+user.getPincode());
                                Road = user.getRoad();
                                Area = user.getArea();
                                Landmark = user.getLandmark();
                                PIN = user.getPincode();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(amountWay.getUid()).child("Personal Details");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                name.setText("Name : "+user.getName());
                                email.setText("Email : "+user.getEmail());
                                phone.setText("Phone : "+user.getPhone());
                                Phone = user.getPhone();
                                Name = user.getName();

                                callnow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                                            String tel = "tel: "+ user.getPhone();
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse(tel));
                                            startActivity(intent);

                                        }
                                        else {
                                            RequestPermission();
                                        }

                                    }


                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        DatabaseReference dbrr = FirebaseDatabase.getInstance().getReference().child("Cancelled").child(keyid).child("Items");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterOrderItem = new AdapterOrderItem(getApplicationContext(),list);
        recyclerView.setAdapter(adapterOrderItem);
        adapterOrderItem.setOnItemClickListener(One_Cancelled_Orders.this);
        valueEventListener = dbrr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Cart upload = postSnapshot.getValue(Model_Cart.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapterOrderItem.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

  /*      share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = Name+"\n"+Phone+"\n"+Road+", "+Area+", "+Landmark+", "+PIN+"\n₹"+Amount+"\n"+Mode;
                // change with required  application package

                intent.setPackage("com.whatsapp");
                if (intent != null) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);//
                    startActivity(Intent.createChooser(intent, text));
                } else {

                    Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });*/
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Cancelled").child(keyid).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });
    }
    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(One_Cancelled_Orders.this, Manifest.permission.CALL_PHONE)){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to make call")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(One_Cancelled_Orders.this,new String[] {Manifest.permission.CALL_PHONE},phone_permission);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions(One_Cancelled_Orders.this,new String[] {Manifest.permission.CALL_PHONE},phone_permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == phone_permission) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String tel = "tel: "+ Phone;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(tel));
                startActivity(intent);



            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onCategoryItemClick(int position) {

    }
}