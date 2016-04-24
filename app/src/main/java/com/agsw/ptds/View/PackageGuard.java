package com.agsw.ptds.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agsw.ptds.Controller.MainController;
import com.agsw.ptds.R;

public class PackageGuard extends AppCompatActivity implements TextView.OnEditorActionListener {

    EditText passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_guard);

        getSupportActionBar().hide();
        passwordText = (EditText) findViewById(R.id.passwordTxt);
        passwordText.setOnEditorActionListener(this);

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            if (textView.getText().toString().equals("0000")){
                startActivity(new Intent(this, MainActivity.class));
            }
            else if (textView.getText().toString().equals("1111")){
                MainController.debugMode = true;
                startActivity(new Intent(this, MainActivity.class));
            }
        }
        return false;
    }
}
