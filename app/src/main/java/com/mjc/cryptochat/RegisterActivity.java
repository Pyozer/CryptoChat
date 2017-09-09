package com.mjc.cryptochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText mRegisterEmail;
    private EditText mRegisterPass;
    private EditText mRegisterPassConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterEmail = findViewById(R.id.register_email);
        mRegisterPass = findViewById(R.id.register_password);
        mRegisterPassConf = findViewById(R.id.register_password_conf);

        findViewById(R.id.register_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validForm();
            }
        });
        findViewById(R.id.register_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validForm() {
        // TODO: Vérifié la validité du formulaire

        return true;
    }

    private void registerUser() {
        if(!validForm()) return;

        // TODO: Inscrire l'utilisateur à Firebase
    }
}