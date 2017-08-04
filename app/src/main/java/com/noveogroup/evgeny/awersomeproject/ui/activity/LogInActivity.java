package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noveogroup.evgeny.awersomeproject.R;
import com.noveogroup.evgeny.awersomeproject.db.api.RealTimeDBApi;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;
import mehdi.sakout.fancybuttons.FancyButton;

public class LogInActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.name)
    EditText name;
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
    private RealTimeDBApi dbApi;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        unbinder = ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        dbApi = RealTimeDBApi.getInstance();
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
        createNewCheckBox.setEnabled(false);
        if (isChecked) {
            if (!isEmailValid(email.getText().toString())) {
                Toast.makeText(this, R.string.not_valid_email, Toast.LENGTH_SHORT).show();
                updateUI();
                return;
            }
            if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.password_cant_be_empty, Toast.LENGTH_SHORT).show();
                updateUI();
                return;
            }
            if (name.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.name_cant_be_empty, Toast.LENGTH_SHORT).show();
                updateUI();
                return;
            }
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, R.string.paswords_do_not_match, Toast.LENGTH_SHORT).show();
                updateUI();
                return;
            }
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = auth.getCurrentUser();
                            dbApi.writeUser(user.getUid(), name.getText().toString(), 0, new Date());
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, R.string.could_not_create_new_user, Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    });
        } else {
            if (!isEmailValid(email.getText().toString())) {
                Toast.makeText(this, R.string.not_valid_email, Toast.LENGTH_SHORT).show();
                updateUI();
                return;
            }
            if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                updateUI();
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
                            Toast.makeText(this, R.string.authentification_failed, Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    });
        }
    }

    @OnCheckedChanged(R.id.create_new_checkbox)
    void onCheckedChanged(boolean isChecked) {
        this.isChecked = isChecked;
        updateUI();
    }

    private void updateUI() {
        createNewCheckBox.setEnabled(true);
        signInButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (isChecked) {
            name.setVisibility(View.VISIBLE);
            signInButton.setText(getString(R.string.sing_in));
            confirmPassword.setVisibility(View.VISIBLE);
        } else {
            name.setVisibility(View.GONE);
            signInButton.setText(getString(R.string.log_in));
            confirmPassword.setVisibility(View.GONE);
        }
    }

    private static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.isEmpty();
    }
}
