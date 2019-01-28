package com.apokpak.shop.login_register;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apokpak.shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference reference;

    EditText txtUsername, txtEmail, txtPassword;
    Button btnRegister;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = txtUsername.getText().toString();
                String email = txtEmail.getText().toString();
                String pass = txtPassword.getText().toString();

                if (userName.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Isi dahulu seluruh form.", Toast.LENGTH_SHORT).show();
                } else {
                    doRegister(userName, email, pass);
                }
            }
        });
    }

    private void init() {
        txtUsername = findViewById(R.id.txt_username);
        txtEmail = findViewById(R.id.txt_email_regist);
        txtPassword = findViewById(R.id.txt_password_regist);
        btnRegister = findViewById(R.id.btn_do_regist);
        progressBar = findViewById(R.id.progressBar_regist);
    }

    private void doRegister(final String userName, final String emailReg, String passReg) {
        btnRegister.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(emailReg, passReg)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser newUser = auth.getCurrentUser();
                            String userId = newUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userid", userId);
                            hashMap.put("username", userName);
                            hashMap.put("email", emailReg);
                            hashMap.put("imageProfile", "default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Horray, your account registered.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Ops.. Saving data failed.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegisterActivity.this, "Ops.. Register failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
