package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSignUp;
    private EditText edtUsername, edtEmail, edtPassword;
    private TextView switchToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle(R.string.sign_up);

        btnSignUp = findViewById(R.id.btnSignUp);
        edtUsername = findViewById(R.id.edtSignUpUsername);
        edtEmail = findViewById(R.id.edtSignUpEmail);
        edtPassword = findViewById(R.id.edtSignUpPassword);
        switchToLogin = findViewById(R.id.switchToLogin);

        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(btnSignUp);
                }
                return false;
            }
        });

        btnSignUp.setOnClickListener(this);
        switchToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchToLogin:
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSignUp:
                String name, email, password;
                name = edtUsername.getText().toString();
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                if (name.equals("") || email.equals("") || password.equals("")) {
                    FancyToast.makeText(SignUpActivity.this,
                            "All Fields are required", FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                } else if (password.length() < 8) {
                    FancyToast.makeText(SignUpActivity.this,
                            "Password length too short.", FancyToast.LENGTH_SHORT,
                            FancyToast.WARNING, false).show();
                } else {
                    createNewUser(name, email, password);
                }
                break;
        }
    }

    private void createNewUser(String name, String email, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(password);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up " + name);
        progressDialog.show();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    FancyToast.makeText(SignUpActivity.this,
                            "Sign Up Successful.", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                } else {
                    FancyToast.makeText(SignUpActivity.this,
                            e.getMessage(), FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                }
            }
        });
    }
}
