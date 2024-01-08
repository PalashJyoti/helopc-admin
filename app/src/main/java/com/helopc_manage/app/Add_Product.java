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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Random;


public class Add_Product extends AppCompatActivity implements AdapterProduct.OnItemClickListener{

    ImageView image;
    ProgressDialog progressDialog;
    private Uri imguri;
    private StorageReference storageReference;
    private StorageTask mUploadTask;

    Spinner spinner;
    TextInputEditText name,amount;
    AppCompatButton add;
    RecyclerView recyclerview;

    private AdapterProduct adapter;
    private List<Model_Product> list;
    private ValueEventListener valueEventListener;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        spinner = findViewById(R.id.spinner);
        name = findViewById(R.id.name);
        add = findViewById(R.id.add);
        image = findViewById(R.id.image);
        recyclerview = findViewById(R.id.recyclerview);
        amount = findViewById(R.id.amount);
        storageReference = FirebaseStorage.getInstance().getReference("Product First Image");

        DatabaseReference dbCATE = FirebaseDatabase.getInstance().getReference().child("Category");
        dbCATE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("category").getValue(String.class);
                    areas.add(areaName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Add_Product.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(Add_Product.this);

                }else {
                    RequestPermission();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Product name can't be Empty");
                    return;
                }else {
                    uploadFile();
                }


            }
        });

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new AdapterProduct(Add_Product.this,list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(Add_Product.this);
        valueEventListener = FirebaseDatabase.getInstance().getReference("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Product upload = postSnapshot.getValue(Model_Product.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Add_Product.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to select an image")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Add_Product.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
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
                        .start(Add_Product.this);
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


                            String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                            String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());

                            Model_Product modelProduct = new Model_Product();
                            modelProduct.setCategory(spinner.getSelectedItem().toString());
                            modelProduct.setName(name.getText().toString());
                            modelProduct.setKeyid(currentDate+currentTime);
                            modelProduct.setImage(downloadUrl.toString());
                            String s = String.format("%.2f", Float.parseFloat(amount.getText().toString()));
                            modelProduct.setAmount(s);

                              FirebaseDatabase.getInstance().getReference("Product").child(currentDate+currentTime).setValue(modelProduct);
                            FirebaseDatabase.getInstance().getReference("Product Category Wise").child(spinner.getSelectedItem().toString()).child(currentDate+currentTime).setValue(modelProduct);

                            name.setText(null);
                            amount.setText(null);
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

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 31) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    @Override
    public void onItemClick(int position) {

        Bundle args = new Bundle();
        args.putString("keyid",list.get(position).getKeyid());
        args.putString("category",list.get(position).getCategory());
        args.putString("name",list.get(position).getName());
        BottomSheet bottomSheet = new BottomSheet();
        bottomSheet.setArguments(args);
        bottomSheet.show(getSupportFragmentManager(), "Add");
    }
}
class Model_Product{
    String image,category,name,keyid,key;
    String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.viewHolder> {

    private Context mContext;
    private List<Model_Product> mUploads;
    private OnItemClickListener mListener;
    public AdapterProduct(Context context, List<Model_Product> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Product med = mUploads.get(position);
        if(med.getName()!=null){
            holder.name.setText(UppercaseFirstLetter(med.getName()));
        }
        if(med.getCategory()!=null){
            holder.category.setText(UppercaseFirstLetter(med.getCategory()));
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,category;
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
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