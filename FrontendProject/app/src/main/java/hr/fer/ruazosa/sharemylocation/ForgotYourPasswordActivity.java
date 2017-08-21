package hr.fer.ruazosa.sharemylocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 23.06.2017..
 */

public class ForgotYourPasswordActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userMail;
    private EditText userPassword;
    private EditText userRepeatPassword;
    private Button saveButton;

    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(hr.fer.ruazosa.sharemylocation.R.layout.activity_forgot_your_password);


        userName = (EditText) findViewById(R.id.editUserName);
        userMail = (EditText) findViewById(R.id.editEmail);
        userPassword = (EditText) findViewById(R.id.editNewPassword);
        userRepeatPassword = (EditText) findViewById(R.id.editReType);
        saveButton = (Button) findViewById(R.id.save);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptChangePassword();
            }
        });

        mProgressView = findViewById(R.id.changePassword_progress);
        mLoginFormView = findViewById(R.id.save);
    }

    private void attemptChangePassword() {

        userName.setError(null);
        userPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        String username = userName.getText().toString();
        String password = md5(userPassword.getText().toString());
        String repeatPassword = md5(userRepeatPassword.getText().toString());


        if (TextUtils.isEmpty(username)) {
            userName.setError(getString(hr.fer.ruazosa.sharemylocation.R.string.error_field_required));
            focusView = userName;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            userPassword.setError(getString(R.string.error_field_required));
            focusView = userPassword;
            cancel = true;
        }
        if (!password.equals(repeatPassword)) {
            userRepeatPassword.setError(getString(R.string.different_password_error));
            focusView = userRepeatPassword;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            // Retrofit begin
            RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
            Call<User> call = restServiceClient.resetPassword(username, password);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);
                    User userData = response.body();
                    if (userData.getErrorMessage() == null) {
                        Toast.makeText(getApplicationContext(), "Password changed successfully"
                                , Toast.LENGTH_SHORT).show();
                        ForgotYourPasswordActivity.this.finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Password change failed", Toast.LENGTH_SHORT).show();
                        ForgotYourPasswordActivity.this.finish();
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Password change failed", Toast.LENGTH_SHORT).show();
                    ForgotYourPasswordActivity.this.finish();
                }
            } );
        }



    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}