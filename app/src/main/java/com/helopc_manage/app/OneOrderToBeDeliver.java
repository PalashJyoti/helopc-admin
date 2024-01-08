package com.helopc_manage.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.helopc_manage.shopheaven_manage.app.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OneOrderToBeDeliver extends AppCompatActivity implements AdapterOrderItem.OnCategoryItemClickListener {
    private TextView orderid, amount, way, name, email, phone, city, area, houseno, apart, landmark, callnow, road, pincode, date, cdate;
    private RecyclerView recyclerView;

    private AdapterOrderItem adapterOrderItem;
    private List<Model_Cart> list;
    Button successful, share;
    String Phone, Way, Name, Road, Area, Landmark, PIN, Amount, Mode;
    int phone_permission = 1;
    private ValueEventListener valueEventListener;
    String uid;
    String keyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_order_to_be_deliver);
        Bundle bundle = getIntent().getExtras();
        keyid = bundle.getString("keyid");

        uid = FirebaseAuth.getInstance().getUid();

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
        recyclerView = findViewById(R.id.recyclerview);
        successful = findViewById(R.id.successful);
        share = findViewById(R.id.share);

        orderid.setText("Order id : " + keyid);
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Order to be delivered").child(keyid);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AmountWay amountWay = snapshot.getValue(AmountWay.class);
                if (amountWay != null) {
                    amount.setText("Amount : " + amountWay.getAmount());
                    Amount = amountWay.getAmount();
                    way.setText("Mode of payment : " + amountWay.getWay());
                    Mode = amountWay.getWay();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference dbd = FirebaseDatabase.getInstance().getReference().child("Order to be delivered").child(keyid).child("Shipping Date");
        dbd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShippingDate amountWay = snapshot.getValue(ShippingDate.class);
                if (amountWay != null) {
                    date.setText("Shipping Date : " + amountWay.getShippingDate());
                    cdate.setText("Confirm Time : " + amountWay.getOrderConfirmTime());

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(amountWay.getUid()).child("Address");
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Address user = snapshot.getValue(Address.class);
                            if (user != null) {
                                apart.setText("Apartment, Building : " + user.getApartment());
                                city.setText("City : " + user.getCity());
                                area.setText("Area : " + user.getArea());
                                houseno.setText("House no. : " + user.getHouseno());
                                landmark.setText("Landmark : " + user.getLandmark());
                                road.setText("Road : " + user.getRoad());
                                pincode.setText("Pin Code : " + user.getPincode());
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
                                name.setText("Name : " + user.getName());
                                email.setText("Email : " + user.getEmail());
                                phone.setText("Phone : " + user.getPhone());
                                Phone = user.getPhone();
                                Name = user.getName();

                                callnow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            String tel = "tel: " + user.getPhone();
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse(tel));
                                            startActivity(intent);

                                        } else {
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


        DatabaseReference dbrr = FirebaseDatabase.getInstance().getReference().child("Order to be delivered").child(keyid).child("Items");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterOrderItem = new AdapterOrderItem(getApplicationContext(), list);
        recyclerView.setAdapter(adapterOrderItem);
        adapterOrderItem.setOnItemClickListener(OneOrderToBeDeliver.this);
        valueEventListener = dbrr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Cart upload = postSnapshot.getValue(Model_Cart.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
//                    Toast.makeText(getApplicationContext(), upload.getSize(), Toast.LENGTH_SHORT).show();
                }
                adapterOrderItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        successful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbrr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Model_Cart upload = postSnapshot.getValue(Model_Cart.class);

                            Items items = new Items();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Product Colour and Stock")
                                    .child(upload.getKeyid()).child(upload.getColour().toLowerCase());
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Items user = snapshot.getValue(Items.class);
                                    if(user!=null){
                                        if(user.getItems()>0){
                                            items.setItems(user.getItems()-Integer.parseInt(upload.getQuantity()));
                                            items.setStock(user.getStock());
                                            databaseReference.setValue(items);
                                        }

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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(keyid).child("status");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String status=snapshot.getValue().toString();
                        if(status.equals("On the way")){
                            reference.setValue("Delivered");
                        }else {
                            Toast.makeText(OneOrderToBeDeliver.this, "Assign the order first", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Order to be delivered").child(keyid).child("Shipping Date");



                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ShippingDate upload = snapshot.getValue(ShippingDate.class);
                        if(upload!=null){
                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(upload.getUid()).child("Personal Details");
                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user =  snapshot.getValue(User.class);

                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Admin Data");
                                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Admin_Data uploadd = snapshot.getValue(Admin_Data.class);
                                            if(upload!=null){
                                                SendBulkSmsOnSuccess(user.getName(),keyid,uploadd.getName(),user.getPhone());
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


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


                DatabaseReference databaseReference2b = FirebaseDatabase.getInstance().getReference().child("Delivered").child(keyid);

                DatabaseReference db2b = FirebaseDatabase.getInstance().getReference().child("Order to be delivered").child(keyid);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference2b.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    Toast.makeText(getApplicationContext(), "Success......", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed......", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                }; db2b.addListenerForSingleValueEvent(valueEventListener);
                db2b.removeValue();
                finish();

                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = Name + "\n" + Phone + "\n" + Road + ", " + Area + ", " + Landmark + ", " + PIN + "\n₹" + Amount + "\n" + Mode;
                // change with required  application package

                intent.setPackage("com.whatsapp");
                if (intent != null) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);//
                    startActivity(Intent.createChooser(intent, text));
                } else {

                    Toast.makeText(getApplicationContext(), "App not found", Toast.LENGTH_SHORT)
                            .show();
                }
                FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(keyid).child("status").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String status = snapshot.getValue(String.class);
                        if (status.equals("Packaging")) {
                            FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(keyid).child("status").setValue("On the way");
                        } else if (status.equals("Delivered")) {
                            Toast.makeText(getApplicationContext(), "Order is already delivered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void RequestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(OneOrderToBeDeliver.this, Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to make call")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(OneOrderToBeDeliver.this, new String[]{Manifest.permission.CALL_PHONE}, phone_permission);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(OneOrderToBeDeliver.this, new String[]{Manifest.permission.CALL_PHONE}, phone_permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == phone_permission) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String tel = "tel: " + Phone;
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

    private void SendBulkSmsOnSuccess(String name, String id, String vintro, String MerchantNumber) {
        //      String newApi = "ni8oh9C72tXpd43AlNzTjcyQRLqa5IsMZV6fHkG1rOuewF0SmWy7uY3GPcTor5eKzR4pXjUZ8kasO9Fm";
        String Api = "AIYgFlyqaStKTZxUrNspiJWMn6G052X487LkoEOvdDjz3mQ1CBo5QIC84rX7PsuxDTFl6aRBkLAWNUwJ";
        String URL = "https://www.fast2sms.com/dev/bulkV2?authorization=" + Api + "&route=dlt&sender_id=VINTRO&message=132413&variables_values=" + name + "|" + id + "|" + vintro + "&flash=0&numbers=" + MerchantNumber;
        //      String newUrl = "https://www.fast2sms.com/dev/bulkV2?authorization="+newApi+"&route=dlt&sender_id=REBITE&message=130050&variables_values="+UserName+"&flash=0&numbers="+MerchantNumber;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            }
        });
    }
}

class Admin_Data {
    String name, number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

class Items{
    int items;
    Boolean stock;

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public Boolean getStock() {
        return stock;
    }

    public void setStock(Boolean stock) {
        this.stock = stock;
    }
}

class Status{
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
  }