package com.helopc_manage.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.helopc_manage.shopheaven_manage.app.R;

public class Activity_Login extends AppCompatActivity {

    private TextInputEditText loginemail,loginpass;
    private Button login;
    private TextView newacc,forgotpass;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);
        Initializer();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            finish();
            startActivity(new Intent(Activity_Login.this, MainActivity.class));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = loginemail.getText().toString().trim();
                String userPassword = loginpass.getText().toString().trim();

                if (TextUtils.isEmpty(userEmail)) {
                    loginemail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(userPassword)) {
                    loginpass.setError("Password is required");
                    return;
                }
                progressDialog.setMessage("Logging you in...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    progressDialog.dismiss();

                                    //EmailVerification();

                                    FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                                    Boolean verified = firebaseUser.isEmailVerified();
                                    if(verified){
                                        finish();

                                        String userEmail = loginemail.getText().toString().trim();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.putExtra("userEmail",userEmail);
                                        startActivity(intent);

                                    }else {
                                        Toast.makeText(Activity_Login.this,"Verify your email first ",Toast.LENGTH_SHORT).show();
                                        firebaseAuth.signOut();
                                    }

                                    //...........................
                                }
                            }
                        });




            }
        });
        newacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Login.this, Activity_New_Account.class));
            }
        });

    }
    private void Initializer(){
        loginemail = findViewById(R.id.loginemail);
        loginpass = findViewById(R.id.loginpass);
        login = findViewById(R.id.login);
        newacc = findViewById(R.id.newacc);
        forgotpass = findViewById(R.id.forgotpass);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}