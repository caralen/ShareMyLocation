package hr.fer.ruazosa.sharemylocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    long userID;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String fcmtok = FirebaseInstanceId.getInstance().getToken();
        MyFCM.finalToken = fcmtok;
        setContentView(hr.fer.ruazosa.sharemylocation.R.layout.activity_main);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.username);

        mPasswordView = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == hr.fer.ruazosa.sharemylocation.R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button button = (Button) findViewById(hr.fer.ruazosa.sharemylocation.R.id.loginB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mProgressView = findViewById(hr.fer.ruazosa.sharemylocation.R.id.login_progress);
        mLoginFormView = findViewById(hr.fer.ruazosa.sharemylocation.R.id.loginB);

    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = md5(mPasswordView.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("This field is too short");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(hr.fer.ruazosa.sharemylocation.R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError("This field is to short");
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            // Retrofit begin
            RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
            Call<User> call = restServiceClient.loginUser(username, password, MyFCM.finalToken);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);
                    User userData = response.body();
                    if (userData.getErrorMessage() == null) {
                        Toast.makeText(getApplicationContext(), "Hello "
                                + userData.getUserFirstName(), Toast.LENGTH_SHORT).show();
                        userID=userData.getId();

                        changeUserToken(userData);
                        goToHomepage(userID);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            } );
        }
    }

    private void changeUserToken(User userData) {
        
    }

    private void goToHomepage(long id) {
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 1;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
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

    public void register(View v){
        Intent regIntent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(regIntent);
    }

    public void forgotYourPass(View v){
        Intent forgotYourPassIntent = new Intent(MainActivity.this, ForgotYourPasswordActivity.class);
        startActivity(forgotYourPassIntent);
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
