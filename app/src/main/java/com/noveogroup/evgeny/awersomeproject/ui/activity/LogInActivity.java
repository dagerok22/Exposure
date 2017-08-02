package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;

import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import mehdi.sakout.fancybuttons.FancyButton;

import static mehdi.sakout.fancybuttons.FancyButton.TAG;

public class LogInActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    @BindView(R.id.log_in_btn)
    FancyButton signInButton;
    @BindView(R.id.create_new_checkbox)
    CheckBox createNewCheckBox;
    private FirebaseAuth auth;
    private boolean isChecked;
    private FirebaseUser user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        unbinder = ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @OnClick(R.id.log_in_btn)
    void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setEnabled(false);
        if (isChecked){
            if (!isEmailValid(email.getText().toString())){
                Toast.makeText(this, "Not valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.getText().toString().equals(confirmPassword.getText().toString())){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = auth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Could not create new user", Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    });
        }else {
            if (!isEmailValid(email.getText().toString())){
                Toast.makeText(this, "Not valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = auth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    });
        }
    }

    @OnCheckedChanged(R.id.create_new_checkbox)
    void onCheckedChanged(boolean isChecked){
        this.isChecked = isChecked;
        updateUI();
    }

    private void updateUI() {
        signInButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        if (user != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (isChecked){
            signInButton.setText("Sign up");
            confirmPassword.setVisibility(View.VISIBLE);
        }else {
            signInButton.setText("Log in");
            confirmPassword.setVisibility(View.GONE);
        }
    }

    private static boolean isEmailValid(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.isEmpty();
    }
}
