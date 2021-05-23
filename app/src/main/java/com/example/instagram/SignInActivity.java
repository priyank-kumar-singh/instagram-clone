package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private EditText edtEmail, edtPassword;
    private TextView switchToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setTitle(R.string.login);

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        switchToSignUp = findViewById(R.id.switchToSignUp);

        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(btnLogin);
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(this);
        switchToSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchToSignUp:
                SignInActivity.this.finish();
                break;
            case R.id.btnLogin:
                String email, password;
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    FancyToast.makeText(SignInActivity.this,
                            "All Fields are required", FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                } else {
                    loginUser(email, password);
                }
                break;
        }
    }

    private void loginUser(String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging user.");
        progressDialog.show();

        ParseUser user = new ParseUser();
        user.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                progressDialog.dismiss();
                if (user != null && e == null) {
                    FancyToast.makeText(SignInActivity.this,
                            "Login Successful", FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS, false).show();
                } else {
                    FancyToast.makeText(SignInActivity.this,
                            e.getMessage(), FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                }
            }
        });
    }

    public void rootLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
