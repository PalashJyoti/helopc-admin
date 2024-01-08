package com.helopc_manage.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.helopc_manage.shopheaven_manage.app.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class One_Order extends AppCompatActivity implements AdapterOrderItem.OnCategoryItemClickListener{


    private TextView orderid,amount,way,name,email,phone,city,area,houseno,apart,landmark,txttime,callnow,road,pincode;
    private RecyclerView recyclerView;

    private AdapterOrderItem adapterOrderItem;
    private List<Model_Cart> list;
    private ValueEventListener valueEventListener;

    Button orderconfirm,cancel,share;
    AlertDialog.Builder builder;
    String AMount,Phone,Way,Name,Road,Area,Landmark,PIN,Amount,Mode;
    int phone_permission = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_order);

        Bundle bundle = getIntent().getExtras();
        String uid = bundle.getString("uid");
        String id = bundle.getString("id");

        pincode = findViewById(R.id.pincode);
        cancel = findViewById(R.id.cancel);
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
        txttime = findViewById(R.id.txttime);
        share = findViewById(R.id.share);

        orderid.setText("Order id : "+id);
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AmountWay amountWay = snapshot.getValue(AmountWay.class);
                if(amountWay != null){
                    amount.setText("Amount : "+amountWay.getAmount());
                    Amount = amountWay.getAmount();
                    way.setText("Mode of payment : "+amountWay.getWay());
                    Way = amountWay.getWay();
                    AMount = amountWay.getAmount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Personal Details");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    name.setText("Name : "+user.getName());
                    Name = user.getName();
                    Phone = user.getPhone();
                    email.setText("Email : "+user.getEmail());
                    phone.setText("Phone : "+user.getPhone());

                    Phone = user.getPhone();

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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Address");
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
        DatabaseReference dbrr = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).child("Items");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = new ArrayList<>();
        adapterOrderItem = new AdapterOrderItem(getApplicationContext(),list);
        recyclerView.setAdapter(adapterOrderItem);
        adapterOrderItem.setOnItemClickListener(One_Order.this);
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

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).child("Shipping Date");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShippingDate upload = snapshot.getValue(ShippingDate.class);
                if(upload!=null){
                    txttime.setText(upload.getShippingDate());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        orderconfirm = findViewById(R.id.orderconfirm);
        orderconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker simpleDatePicker = findViewById(R.id.simpleDatePicker);
                int day = simpleDatePicker.getDayOfMonth();
                int month = simpleDatePicker.getMonth();
                int year = simpleDatePicker.getYear();
                String shippingDate = day +"/"+ (month + 1) +"/"+ year;

                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                ShippingDate shippingDate1 = new ShippingDate();
                shippingDate1.setShippingDate(shippingDate);
                shippingDate1.setOrderConfirmTime(currentDate+" at "+currentTime);
                shippingDate1.setUid(uid);


                FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).child("Shipping Date").setValue(shippingDate1);
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Orders").child(id).child("Shipping Date").setValue(shippingDate1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Order done", Toast.LENGTH_SHORT).show();
                            }
                        });
                DatabaseReference databaseReference2b = FirebaseDatabase.getInstance().getReference().child("Order to be delivered");

                DatabaseReference db2b = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference2b.child(id).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Personal Details");
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =  snapshot.getValue(User.class);
                        SendBulkSmsOnConfirm(user.getName(),id,shippingDate,user.getPhone());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//                FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).removeValue()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        });
                FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).child("status").setValue("Packaging");

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShippingDate shippingDate1 = new ShippingDate();
                shippingDate1.setShippingDate("cancel");
                shippingDate1.setUid(uid);
                shippingDate1.setOrderConfirmTime("");

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Personal Details");
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =  snapshot.getValue(User.class);
                        SendBulkSmsOnCancelled(user.getName(),id,user.getPhone());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).child("Shipping Date").setValue(shippingDate1);
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Orders").child(id).child("Shipping Date").setValue(shippingDate1);

                if(Way.equals("Payment Done via Online")){
                    Amount aamount = new Amount();
                    DatabaseReference dre = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Wallet");

                    dre.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Amount money = snapshot.getValue(Amount.class);
                            if(money!=null){
                                aamount.setAmount(money.getAmount()+Math.round(Float.parseFloat(AMount)));
                            }else {
                                aamount.setAmount(Math.round(Float.parseFloat(AMount)));
                            }
                            FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Wallet").setValue(aamount);
                        }
                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });

                }
                DatabaseReference databaseReference2b = FirebaseDatabase.getInstance().getReference().child("Cancelled");

                DatabaseReference db2b = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference2b.child(id).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    Toast.makeText(getApplicationContext(), "Cancelled......", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed......", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                }; db2b.addListenerForSingleValueEvent(valueEventListener);

                FirebaseDatabase.getInstance().getReference().child("Orders").child(uid).child(id).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String text = Name+"\n"+Phone+"\n"+Road+", "+Area+", "+Landmark+", "+PIN+"\n₹"+Amount+"\n"+Way;
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
        });

    }
    private void SendBulkSmsOnConfirm(String UserName,String id,String time,String MerchantNumber){

        String newApi = "AIYgFlyqaStKTZxUrNspiJWMn6G052X487LkoEOvdDjz3mQ1CBo5QIC84rX7PsuxDTFl6aRBkLAWNUwJ";
        String newUrl = "https://www.fast2sms.com/dev/bulkV2?authorization="+newApi+"&route=dlt&sender_id=VINTRO&message=132412&variables_values="+UserName+"|"+id+"|"+time+"&flash=0&numbers="+MerchantNumber;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(newUrl)
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

    private void SendBulkSmsOnCancelled(String UserName,String id,String number){
        String newApi = "AIYgFlyqaStKTZxUrNspiJWMn6G052X487LkoEOvdDjz3mQ1CBo5QIC84rX7PsuxDTFl6aRBkLAWNUwJ";

        String newUrl = "https://www.fast2sms.com/dev/bulkV2?authorization="+newApi+"&route=dlt&sender_id=VINTRO&message=132411&variables_values="+UserName+"|"+id+"&flash=0&numbers="+number;


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(newUrl)
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

    public String showTime(int hour, int min) {
        String time,format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        time = String.valueOf(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));
        return time;
    }


    @Override
    public void onCategoryItemClick(int position) {
       /* Model_Product orderedMed = list.get(position);
        String presurl = orderedMed.getPresurl();
        if(presurl!=null){
            builder = new AlertDialog.Builder(this);
            builder.setMessage("See the prescription")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(presurl));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.android.chrome");
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException ex) {
                                intent.setPackage(null);
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Prescription");
            alert.show();




        }*/

    }
    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(One_Order.this, Manifest.permission.CALL_PHONE)){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to make call")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(One_Order.this,new String[] {Manifest.permission.CALL_PHONE},phone_permission);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions(One_Order.this,new String[] {Manifest.permission.CALL_PHONE},phone_permission);
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
}
class AdapterOrderItem extends RecyclerView.Adapter<AdapterOrderItem.viewHolder> {

    private Context mContext;
    private List<Model_Cart> mUploads;
    private OnCategoryItemClickListener  mListener;
    public AdapterOrderItem(Context context, List<Model_Cart> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Cart med = mUploads.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(med.getKeyid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Model_Product upload = snapshot.getValue(Model_Product.class);
                if(upload!=null){
                    holder.name.setText(UppercaseFirstLetter(upload.getName()));
                    holder.category.setText(UppercaseFirstLetter(upload.getCategory()));
                    Picasso.get()
                            .load(upload.getImage())
                            .into(holder.image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.colour.setText(med.getColour());
        holder.quantity.setText("Qty "+med.getQuantity());

    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new viewHolder(view);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,category,colour,quantity;
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            image = itemView.findViewById(R.id.image);
            colour = itemView.findViewById(R.id.colour);
            quantity = itemView.findViewById(R.id.quantity);

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
    private String UppercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }

}
class Address{
    String apartment,area,city,houseno,landmark,road,pincode;

    public Address() {
    }

    public Address(String apartment, String area, String city, String houseno, String landmark) {
        this.apartment = apartment;
        this.area = area;
        this.city = city;
        this.houseno = houseno;
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseno() {
        return houseno;
    }

    public void setHouseno(String houseno) {
        this.houseno = houseno;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}
class OrderedMed{
    String Name,type,Price,key,Xnum,presurl,Category,PImgUrl;

    public OrderedMed() {
    }

    public OrderedMed(String name, String type, String price, String xnum, String presurl, String category, String PImgUrl) {
        Name = name;
        this.type = type;
        Price = price;
        Xnum = xnum;
        this.presurl = presurl;
        Category = category;
        this.PImgUrl = PImgUrl;
    }

    public String getPImgUrl() {
        return PImgUrl;
    }

    public void setPImgUrl(String PImgUrl) {
        this.PImgUrl = PImgUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getXnum() {
        return Xnum;
    }

    public void setXnum(String xnum) {
        Xnum = xnum;
    }

    public String getPresurl() {
        return presurl;
    }

    public void setPresurl(String presurl) {
        this.presurl = presurl;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
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

class PrescriptionStatus{
    String prescriptionStatus;

    public PrescriptionStatus(String prescriptionStatus) {
        this.prescriptionStatus = prescriptionStatus;
    }

    public PrescriptionStatus() {
    }

    public String getPrescriptionStatus() {
        return prescriptionStatus;
    }

    public void setPrescriptionStatus(String prescriptionStatus) {
        this.prescriptionStatus = prescriptionStatus;
    }
}
class ReturnedAmount{
    String returnedAmount;

    public ReturnedAmount() {
    }

    public ReturnedAmount(String returnedAmount) {
        this.returnedAmount = returnedAmount;
    }

    public String getReturnedAmount() {
        return returnedAmount;
    }

    public void setReturnedAmount(String returnedAmount) {
        this.returnedAmount = returnedAmount;
    }
}
class ShippingDate{
    String shippingDate,orderConfirmTime,uid;

    public ShippingDate() {
    }

    public ShippingDate(String shippingDate,String orderConfirmTime) {
        this.shippingDate = shippingDate;
        this.orderConfirmTime = orderConfirmTime;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getOrderConfirmTime() {
        return orderConfirmTime;
    }

    public void setOrderConfirmTime(String orderConfirmTime) {
        this.orderConfirmTime = orderConfirmTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
class Amount{
    int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

class Model_Cart{
    String keyid,key,amount,quantity,colour,total;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
