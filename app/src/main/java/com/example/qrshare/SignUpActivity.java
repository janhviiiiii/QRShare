package com.example.qrshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrshare.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    //declaration of variables
    private Button button;
    private TextView text;
    private CheckBox checkbox;


    //patterns/format compulsion
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

    //for db
    ActivitySignUpBinding binding;
    DatabaseHelper databaseHelper;

    public SignUpActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for db connectivity
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

            binding.btnSignup.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                        String email = binding.etxtSignupEmail.getText().toString();
                        String username = binding.etxtSignupUsername.getText().toString();
                        String password = binding.etxtSignupPassword.getText().toString();
                        String confirmPassword = binding.etxtSignupConfirmPassword.getText().toString();

//                        if (email.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("")) {
//                            Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
//                        }
                    checkbox = findViewById(R.id.chkbox_agree);
                    if(binding.chkboxAgree.isChecked()) {
                        if (email.equals("") || username.equals("") || password.equals("") || confirmPassword.equals("") || !checkbox.isChecked()) {
                            Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //********testing*********
                            //pattern check
                            if (email.matches(emailPattern) && password.matches(passwordPattern) && confirmPassword.matches(passwordPattern)) {
                                //to check
                                //Toast.makeText(SignUpActivity.this, "Pattern matched", Toast.LENGTH_SHORT).show();

                                //code
                                if (password.equals(confirmPassword)) {
                                    Boolean checkUserEmail = databaseHelper.checkEmail(email);
                                    if (checkUserEmail == false) {
                                        Boolean insert = databaseHelper.insertData(username, email, password);

                                        if (insert == true) {

                                            Toast.makeText(SignUpActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this, "User already exists. Please Login", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "Invalid Email or Password! Note: \n Password must contain atleast one Special Character, Number, Capital letter Character.", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "Incorrect email or password format! \n Note: Password must contain atleast one Special Character, Number, Capital letter Character.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else{
                        if(email.equals("")||password.equals("")||confirmPassword.equals("")||username.equals(("")))
                        {
                            Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Please check the terms and condition checkbox to proceed further", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


        binding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
   }
}
