package hr.fer.ruazosa.sharemylocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 6/13/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText mail;
    private EditText password;
    private EditText repeatPassword;
    private EditText firstName;
    private EditText lastName;
    private EditText number;
    private Button button;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView userIcon;
    private TextView userIconLabel;
    private String userChoosenTask;
    private String iconString; //tu dodamo odma default string koji će bit ak on niš ne stavi

    private View mProgressView;
    private View mLoginFormView;

    private String radioText = "";
    private TextView sextext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(hr.fer.ruazosa.sharemylocation.R.layout.activity_register);

        username = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.editUserName);
        mail = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.editEmail);
        password = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.changePassword);
        repeatPassword = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.editReType);
        firstName = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.firstname);
        lastName = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.last);
        number = (EditText) findViewById(hr.fer.ruazosa.sharemylocation.R.id.phonenumber);
        button = (Button) findViewById(hr.fer.ruazosa.sharemylocation.R.id.save);
        sextext = (TextView) findViewById(R.id.sexText);



        userIcon = (ImageView) findViewById(hr.fer.ruazosa.sharemylocation.R.id.photoadd);
        userIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });
        userIconLabel = (TextView) findViewById(hr.fer.ruazosa.sharemylocation.R.id.iconLabel);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(hr.fer.ruazosa.sharemylocation.R.id.save);
        mProgressView = findViewById(hr.fer.ruazosa.sharemylocation.R.id.register_progress);
    }

    private void attemptRegister() {

        username.setError(null);
        mail.setError(null);
        password.setError(null);
        repeatPassword.setError(null);
        firstName.setError(null);
        lastName.setError(null);
        number.setError(null);
        sextext.setError(null);

        boolean cancel = false;
        View focusView = null;



        String userName = username.getText().toString();
        String userMail = mail.getText().toString();
        String userPassword = password.getText().toString();
        String userRepeatPassword = repeatPassword.getText().toString();
        String firstname = firstName.getText().toString();
        String lastname = lastName.getText().toString();
        String phoneNumber = number.getText().toString();



        if (TextUtils.isEmpty(userName)) {
            username.setError(getString(R.string.error_field_required));
            focusView = username;
            cancel = true;
        }
        if (TextUtils.isEmpty(userMail)) {
            mail.setError(getString(R.string.error_field_required));
            focusView = mail;
            cancel = true;
        }
        if (TextUtils.isEmpty(userPassword)) {
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }
        if (TextUtils.isEmpty(userRepeatPassword)) {
            repeatPassword.setError(getString(R.string.error_field_required));
            focusView = repeatPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            firstName.setError(getString(R.string.error_field_required));
            focusView = firstName;
            cancel = true;
        }
        if (TextUtils.isEmpty(lastname)) {
            lastName.setError(getString(R.string.error_field_required));
            focusView = lastName;
            cancel = true;
        }

        if (radioText == "") {
            sextext.setError(getString(R.string.error_field_required));
            focusView = sextext;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            number.setError(getString(R.string.error_field_required));
            focusView = number;
            cancel = true;
        }


        if (!userPassword.equals(userRepeatPassword)) {
            repeatPassword.setError(getString(R.string.different_password_error));
            focusView = repeatPassword;
            cancel = true;
        }

        if (!TextUtils.isEmpty(userMail) && !userMail.contains("@")) {
            mail.setError(getString(R.string.invalid_mail));
            focusView = mail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else{

            if (TextUtils.isEmpty(iconString)) {
                setDefaultIcon();
            }


            User user = new User();
            user.setUserName(userName);
            user.setPassword(md5(userPassword));
            user.setSex(radioText);
            user.setUserFirstName(firstname);
            user.setUserLastName(lastname);
            user.setTelephoneNumber(phoneNumber);
            user.setUserIcon(iconString);
            user.setUserToken(MyFCM.finalToken);
            user.setEmail(userMail);


            showProgress(true);

            // Retrofit begin
            RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
            Call<User> call = restServiceClient.registerUser(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    showProgress(false);
                    User userData = response.body();
                    if (userData.getErrorMessage() == null) {
                        Toast.makeText(getApplicationContext(), "Registered successfully ", Toast.LENGTH_SHORT).show();
                        RegisterActivity.this.finish();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        RegisterActivity.this.finish();
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.finish();
                }
            } );
        }
    }

    //------------------------------------------------------
    // ak nekaj ne radi u register-u onda je možda zbog ovog
    //------------------------------------------------------

    private void setDefaultIcon() {
        Bitmap icon = BitmapFactory.decodeResource(RegisterActivity.this.getResources(), R.drawable.default_avatar);

        iconString = encodeToBase64(icon);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CameraUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny - dodati!
                }
                break;
        }
    }

    private void addPhoto(){
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Choose a method: ");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= CameraUtility.checkPermission(RegisterActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Gallery";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        userIcon.setImageBitmap(thumbnail);
        iconString=encodeToBase64(thumbnail);
    }


    private void onSelectFromGalleryResult(Intent data) {

        final Bundle extras = data.getExtras();
        if (extras != null) {
            //Get image
            Bitmap newProfilePic = extras.getParcelable("data");
            userIcon.setImageBitmap(newProfilePic);
            iconString = encodeToBase64(newProfilePic);
        }
    }

    private String encodeToBase64(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    private Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void onRadioButtonClicked(View view){

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    radioText = "M";
                break;
            case R.id.female:
                if (checked)
                    radioText = "F";
                break;
        }
    }
}
