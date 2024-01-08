package com.helopc_manage.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc_manage.shopheaven_manage.app.R;

public class BottomSheet_Size extends BottomSheetDialogFragment{


    AppCompatButton delete;
    String keyid,Colour,key;
    TextView colour;
    EditText etS;
    Button btnS;
    CheckBox stock;
    DatabaseReference dbs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_size,container,false);

        delete = view.findViewById(R.id.delete);
        colour = view.findViewById(R.id.colour);

        stock = view.findViewById(R.id.stock);

        etS=view.findViewById(R.id.etS);

        btnS=view.findViewById(R.id.btnS);

        Bundle mArgs = getArguments();
        keyid = mArgs.getString("keyid");
        Colour = mArgs.getString("colour");
        key = mArgs.getString("key");
        colour.setText(UppercaseFirstLetter(Colour));

        dbs = FirebaseDatabase.getInstance().getReference("Product Colour and Stock").child(keyid).child(Colour);

        dbs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Stock user = snapshot.getValue(Stock.class);
                int amount=snapshot.child("items").getValue(Integer.class);
                etS.setText(String.valueOf(amount));
                if(user!=null){
                    if (amount>0) {
                        stock.setChecked(true);
                        dbs.child("stock").setValue(true);
                    }else {
                        stock.setChecked(false);
                        dbs.child("stock").setValue(false);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbs.child("items").setValue(Integer.parseInt(etS.getText().toString()));
            }
        });

        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Stock user = snapshot.getValue(Stock.class);
                        if(user!=null){
                            if (user.isStock() != true) {
                                stock.setChecked(true);
                                Stock sizeAndStock = new Stock();
                                sizeAndStock.setStock(true);
                                dbs.setValue(sizeAndStock);
                            }else {
                                stock.setChecked(false);
                                Stock sizeAndStock = new Stock();
                                sizeAndStock.setStock(false);
                                dbs.setValue(sizeAndStock);
                            }
                        }else {
                            stock.setChecked(true);
                            Stock sizeAndStock = new Stock();
                            sizeAndStock.setStock(true);
                            dbs.setValue(sizeAndStock);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Product Colour").child(keyid).child(key).removeValue();
                dbs.removeValue();
                Toast.makeText(getContext(),"Colour Deleted",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return view;
    }
    private String UppercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }
}
class Stock{
    boolean stock;

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }
}