package com.sh22.energy_saver_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class SignInActivity extends AppCompatActivity {
    EditText email, password;
    Button LoginButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         email = findViewById(R.id.username);
         password = findViewById(R.id.password);

        LoginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (!isEmailValid(String.valueOf(email))){
                    email.setError("Invalid Email or Password");
                    return;
                }
                if (!isPasswordValid(String.valueOf(password))){
                    password.setError("Invalid Email or Password");
                    return;
                }
                boolean isAuthenticated = true;

                if (isAuthenticated){
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(SignInActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                }
            }
            public boolean isPasswordValid(String userPassword) {
                if(userPassword == "1234"){
                    //add password Authentication
                    return true;
                }
                return false;
            }

            public boolean isEmailValid(String userEmail) {
                if(userEmail == "Admin"){
                    // add email authentication
                    return true;
                }
                return false;
            }


        });
    }

}





