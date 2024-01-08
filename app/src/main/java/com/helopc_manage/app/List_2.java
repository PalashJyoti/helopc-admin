package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.helopc_manage.shopheaven_manage.app.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class List_2 extends AppCompatActivity implements AdapterList_2.OnItemClickListener {

    String[] Type = {  "category", "product"};
    private Spinner type,category,product;
    AppCompatButton add;
    RecyclerView recyclerview;
    ImageView image;

    LinearLayout categorylayout,productlayout;

    private Uri imguri;
    ProgressDialog progressDialog;

    private StorageReference storageReference;
    private StorageTask mUploadTask;


    private AdapterList_2 adapter;
    private List<Model_List_2> list;
    private ValueEventListener valueEventListener;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_2);

        type = findViewById(R.id.type);
        category = findViewById(R.id.category);
        product = findViewById(R.id.product);
        add = findViewById(R.id.add);
        recyclerview = findViewById(R.id.recyclerview);
        image = findViewById(R.id.image);
        categorylayout = findViewById(R.id.categorylayout);
        productlayout = findViewById(R.id.productlayout);
        builder = new AlertDialog.Builder(this);

        DatabaseReference dbcategory = FirebaseDatabase.getInstance().getReference().child("Category");
        dbcategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("category").getValue(String.class);
                    areas.add(areaName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(List_2.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference().child("Product");
        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("name").getValue(String.class);
                    areas.add(areaName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(List_2.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                product.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> gameKindArray= new ArrayAdapter<String>(List_2.this,android.R.layout.simple_spinner_item, Type);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(gameKindArray);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(type.getSelectedItem().toString().equals("category")){
                    categorylayout.setVisibility(View.VISIBLE);
                    productlayout.setVisibility(View.GONE);
                }

                if(type.getSelectedItem().toString().equals("product")){
                    categorylayout.setVisibility(View.GONE);
                    productlayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("List_2");

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new AdapterList_2(List_2.this,list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(List_2.this);
        valueEventListener = FirebaseDatabase.getInstance().getReference("List_2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_List_2 upload = postSnapshot.getValue(Model_List_2.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(List_2.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(List_2.this);

                }else {
                    RequestPermission();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

    }
    @Override
    public void onItemClick(int position) {

        builder.setMessage("Do you want to Delete the Item ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseDatabase.getInstance().getReference("List_2").child(list.get(position).getKey()).removeValue();

                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(list.get(position).getImage());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(List_2.this, "Category deleted", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete Item");
        alert.show();

    }
    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to select an image")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(List_2.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(List_2.this);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imguri = result.getUri();
                try {
                    ContentResolver resolver = getContentResolver();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, imguri);

                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile() {


        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading .....");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (imguri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imguri));
            mUploadTask = fileReference.putFile(imguri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();


                            Model_List_2 model_list_1 = new Model_List_2();

                            if(type.getSelectedItem().toString().equals("category")){
                                model_list_1.setCategory(category.getSelectedItem().toString());
                                model_list_1.setImage(downloadUrl.toString());
                                model_list_1.setType(type.getSelectedItem().toString());

                                String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                                String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());

                                FirebaseDatabase.getInstance().getReference("List_2").child(currentDate+currentTime).setValue(model_list_1)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }

                            if(type.getSelectedItem().toString().equals("product")){


                                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product");

                                db.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                            Model_Product upload = postSnapshot.getValue(Model_Product.class);

                                            if(upload.getName().equals(product.getSelectedItem().toString())){

                                                model_list_1.setImage(downloadUrl.toString());
                                                model_list_1.setProduct(product.getSelectedItem().toString());
                                                model_list_1.setType(type.getSelectedItem().toString());
                                                model_list_1.setKeyid(upload.getKeyid());

                                                String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                                                String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());

                                                FirebaseDatabase.getInstance().getReference("List_2").child(currentDate+currentTime).setValue(model_list_1)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Getting failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int current = (int)(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            progressDialog.setProgress(current);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}


class Model_List_2 {
    String key, category, product, image, type, keyid;

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

class AdapterList_2 extends RecyclerView.Adapter<AdapterList_2.viewHolder> {

    private Context mContext;
    private List<Model_List_2> mUploads;
    private OnItemClickListener mListener;
    public AdapterList_2(Context context, List<Model_List_2> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_List_2 med = mUploads.get(position);

        if(med.getCategory()!=null){
            holder.name.setText(UppercaseFirstLetter(med.getCategory()));
        }else {
            holder.name.setText(UppercaseFirstLetter(med.getProduct()));
        }
        Picasso.get()
                .load(med.getImage())
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
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