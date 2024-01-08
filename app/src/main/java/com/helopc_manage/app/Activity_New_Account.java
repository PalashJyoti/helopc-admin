package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.helopc_manage.shopheaven_manage.app.R;

public class Activity_New_Account extends AppCompatActivity {
    private TextInputEditText caemail,capassword;    //ca stands for create account activity
    private Button create;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        Initializer();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String  email = caemail.getText().toString().trim();
                String  password = capassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    caemail.setError("Email is required");
                }
                if(TextUtils.isEmpty(password)){
                    capassword.setError("Password is required");
                }
                if(password.length() < 6){
                    capassword.setError("Password must be more 6 characters");
                }
                progressDialog.setMessage("Registering  ...");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Verification();

                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Activity_New_Account.this, "Error Occurred",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) ;

            }
        });
    }
    private void Initializer(){
        caemail = findViewById(R.id.loginemail);
        capassword = findViewById(R.id.loginpass);
        create = findViewById(R.id.create);
    }
    private void Verification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Activity_New_Account.this,"Verification mail has been sent",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Activity_New_Account.this, Activity_Login.class));
                    }else {
                        Toast.makeText(Activity_New_Account.this,"Registration failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
class User {
    String name,phone,email;

    public User() {
    }

    public User(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}