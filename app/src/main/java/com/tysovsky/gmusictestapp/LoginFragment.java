package com.tysovsky.gmusictestapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tysovsky on 9/10/17.
 */

public class LoginFragment extends Fragment {
    public static final String TAG = "LoginFragment";

    public MainActivity mainActivity;
    EditText etUsername, etPassword;
    Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUsername = (EditText)view.findViewById(R.id.etUsername);
        etPassword = (EditText)view.findViewById(R.id.etPassword);
        btnLogin = (Button)view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.login(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });


        return view;
    }


    public void setMainActivity(MainActivity activity){
        this.mainActivity = activity;
    }


}
