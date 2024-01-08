package com.helopc_manage.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

public class DealsActivity extends AppCompatActivity {

    TextInputEditText percent;
    AppCompatButton setOffer, revokeOffer;
    String keyid;
    double mrp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        Bundle bundle = getIntent().getExtras();
        keyid = bundle.getString("keyid");

        percent = findViewById(R.id.percent);
        setOffer = findViewById(R.id.setOffer);
        revokeOffer = findViewById(R.id.revokeOffer);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Product").child(keyid);
        DatabaseReference refd = FirebaseDatabase.getInstance().getReference().child("Deals");

        refd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                percent.setText(snapshot.child(keyid).child("offer").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double offerPercent = Double.parseDouble(percent.getText().toString().trim());
                int OfferI = (int) offerPercent;
                String offer = OfferI + "";
                ref.child("offer").setValue(offer);
                ref.child("amount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String amount = snapshot.getValue(String.class);
                        mrp = Double.parseDouble(amount);
                        double OfferAmount = mrp - ((offerPercent / 100) * mrp);
                        String offerAmount = OfferAmount + "";
                        ref.child("offerAmount").setValue(offerAmount).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        refd.child(keyid).setValue(snapshot.getValue());
                                                        Toast.makeText(getApplicationContext(), "Offer added Successfully", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }
        });

        revokeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refd.child(keyid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ref.child("offer").setValue(null);
                        ref.child("offerAmount").setValue(null);
                        Toast.makeText(getApplicationContext(), "Offer removed Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}