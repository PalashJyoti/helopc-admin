package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.helopc_manage.shopheaven_manage.app.R;

public class BottomSheet extends BottomSheetDialogFragment{


    AppCompatButton image,des,colour,offer,delete;
    String keyid;
    TextView product,category;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet,container,false);

        des = view.findViewById(R.id.des);
        image = view.findViewById(R.id.image);
        colour = view.findViewById(R.id.colour);
        offer=view.findViewById(R.id.offer);
        delete = view.findViewById(R.id.delete);
        product = view.findViewById(R.id.product);
        category = view.findViewById(R.id.category);


        Bundle mArgs = getArguments();
        keyid = mArgs.getString("keyid");
        category.setText(mArgs.getString("category"));
        product.setText(mArgs.getString("name"));

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Add_Product_Images.class);
                intent.putExtra("keyid",keyid);
                startActivity(intent);
            }
        });
        des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Add_Product_description.class);
                intent.putExtra("keyid",keyid);
                startActivity(intent);
            }
        });
        colour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Add_Product_Colour.class);
                intent.putExtra("keyid",keyid);
                startActivity(intent);
            }
        });
        offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),DealsActivity.class);
                intent.putExtra("keyid",keyid);
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbp = FirebaseDatabase.getInstance().getReference("Product").child(keyid);
                dbp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Model_Product user = snapshot.getValue(Model_Product.class);
                        if(user!=null){
                            FirebaseDatabase.getInstance().getReference("Product Category Wise").child(user.getCategory()).child(keyid).removeValue();
                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getImage());
                            imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference("Product Colour").child(keyid).removeValue();
                                    FirebaseDatabase.getInstance().getReference("Product Colour, Sizes and Stock").child(keyid).removeValue();
                                    FirebaseDatabase.getInstance().getReference("Product Description").child(keyid).removeValue();
                                    dbp.removeValue();
                                    dismiss();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Images").child(keyid);

                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Model_image upload = postSnapshot.getValue(Model_image.class);

                            FirebaseDatabase.getInstance().getReference("Product Images").child(keyid).removeValue();
                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getImage());
                            imageRef.delete();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });




        return view;



    }

}