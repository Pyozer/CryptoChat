package com.mjc.cryptochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private TextView login, password;
    private Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (TextView)findViewById(R.id.login);
        password = (TextView)findViewById(R.id.password);

        connect = (Button)findViewById(R.id.connect);
    }
}
