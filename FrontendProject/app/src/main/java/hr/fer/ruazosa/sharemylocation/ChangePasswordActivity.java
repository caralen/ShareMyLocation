package hr.fer.ruazosa.sharemylocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldPassword;
    EditText newPassword;
    EditText reType;
    Button changePassword;
    boolean cancel;
    View focusView = null;
    User currentUser;
    long userID;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = (EditText) findViewById(R.id.editUserName);
        newPassword = (EditText) findViewById(R.id.editEmail);
        reType = (EditText) findViewById(R.id.editNewPassword);

        changePassword = (Button) findViewById(R.id.btnUpdatePassword);

        Bundle extras = getIntent().getExtras();
        userID = extras.getLong("userID");

        // Retrofit begin
        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<User> call = restServiceClient.getUserForUserID(userID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userData = response.body();
                if (userData.getErrorMessage() == null) {
                    currentUser = userData;

                } else {
                    Toast.makeText(getApplicationContext(), "Loading personal info failed", Toast.LENGTH_SHORT).show();
                    ChangePasswordActivity.this.finish();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Loading personal info failed", Toast.LENGTH_SHORT).show();
                ChangePasswordActivity.this.finish();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel = false;

                String txtOldPassword = md5(oldPassword.getText().toString());
                String txtNewPassword = md5(newPassword.getText().toString());
                String txtReType = md5(reType.getText().toString());

                oldPassword.setError(null);
                newPassword.setError(null);
                reType.setError(null);


                if (TextUtils.isEmpty(txtOldPassword)) {
                    oldPassword.setError(getString(R.string.error_field_required));
                    focusView = oldPassword;
                    cancel = true;
                }

                if (TextUtils.isEmpty(txtNewPassword)) {
                    newPassword.setError(getString(R.string.error_field_required));
                    focusView = newPassword;
                    cancel = true;
                }

                if (TextUtils.isEmpty(txtReType)) {
                    reType.setError(getString(R.string.error_field_required));
                    focusView = reType;
                    cancel = true;
                }

                if (txtNewPassword.length() < 3) {
                    newPassword.setError(getString(R.string.error_invalid_password));
                    focusView = newPassword;
                    cancel = true;
                }

                if (!txtNewPassword.equals(txtReType)) {
                    reType.setError(getString(R.string.error_invalid_retype));
                    focusView = newPassword;
                    cancel = true;
                }
                if (!currentUser.getPassword().equals(txtOldPassword)) {
                    oldPassword.setError(getString(R.string.error_invalid_old_password));
                    focusView = oldPassword;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    showProgress(true);
                    Bundle extras = getIntent().getExtras();
                    userID = extras.getLong("userID");

                    // Retrofit begin
                    RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
                    Call<User> call = restServiceClient.changePassword(userID, txtNewPassword);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            showProgress(false);
                            User userData = response.body();
                            if (userData.getErrorMessage() == null) {
                                Toast.makeText(getApplicationContext(), "Password changed successfully"
                                        , Toast.LENGTH_SHORT).show();
                                ChangePasswordActivity.this.finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Password change failed", Toast.LENGTH_SHORT).show();
                                ChangePasswordActivity.this.finish();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Password change failed", Toast.LENGTH_SHORT).show();
                            ChangePasswordActivity.this.finish();
                        }
                    });
                }

            }
        });

        mProgressView = findViewById(R.id.changePassword_progress);
        mLoginFormView = findViewById(R.id.btnUpdatePassword);
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
}
