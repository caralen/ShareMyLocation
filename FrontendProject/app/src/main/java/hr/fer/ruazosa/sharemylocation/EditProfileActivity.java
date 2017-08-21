package hr.fer.ruazosa.sharemylocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    EditText changedUserName;
    EditText changedFirstName;
    EditText changedSecondName;
    EditText changedEmail;
    EditText changedPhoneNumber;
    long userID;
    Button btnUpdate;
    private View mProgressView;
    private View mLoginFormView;
    View focusView = null;
    boolean cancel;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView userIcon;
    private String userChoosenTask;
    private String iconString;
    CircleImageView profilePicture;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changedUserName = (EditText) findViewById(R.id.editUserName);
        changedFirstName = (EditText) findViewById(R.id.editEmail);
        changedSecondName = (EditText) findViewById(R.id.editNewPassword);
        changedEmail = (EditText) findViewById(R.id.changedEmail);
        changedPhoneNumber = (EditText) findViewById(R.id.changedPhoneNumber);
        btnUpdate = (Button) findViewById(R.id.updateProfile);
        userIcon = (ImageView) findViewById(R.id.profilePic);
        profilePicture = (CircleImageView) findViewById(R.id.profilePic);

        userIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtChangedUserName = changedUserName.getText().toString();
                String txtChangedFirstName = changedFirstName.getText().toString();
                String txtChangedSecondName = changedSecondName.getText().toString();
                String txtChangedEmail = changedEmail.getText().toString();
                String txtChangedPhoneNumber = changedPhoneNumber.getText().toString();

                changedUserName.setError(null);
                changedFirstName.setError(null);
                changedSecondName.setError(null);
                changedEmail.setError(null);
                changedPhoneNumber.setError(null);


                cancel = false;

                if (TextUtils.isEmpty(txtChangedUserName)) {
                    changedUserName.setError(getString(R.string.error_field_required));
                    focusView = changedUserName;
                    cancel = true;
                }

                if (TextUtils.isEmpty(txtChangedFirstName)) {
                    changedFirstName.setError(getString(R.string.error_field_required));
                    focusView = changedFirstName;
                    cancel = true;
                }

                if (TextUtils.isEmpty(txtChangedSecondName)) {
                    changedSecondName.setError(getString(R.string.error_field_required));
                    focusView = changedSecondName;
                    cancel = true;
                }

                if (TextUtils.isEmpty(txtChangedEmail)) {
                    changedEmail.setError(getString(R.string.error_field_required));
                    focusView = changedEmail;
                    cancel = true;
                }

                if (TextUtils.isEmpty(txtChangedPhoneNumber)) {
                    changedPhoneNumber.setError(getString(R.string.error_field_required));
                    focusView = changedPhoneNumber;
                    cancel = true;
                }

                if (txtChangedUserName.length()<3) {
                    changedUserName.setError(getString(R.string.error_invalid_username));
                    focusView = changedUserName;
                    cancel = true;
                }

                if (txtChangedFirstName.length()<2) {
                    changedFirstName.setError(getString(R.string.error_invalid_firstname));
                    focusView = changedFirstName;
                    cancel = true;
                }

                if (txtChangedSecondName.length()<2) {
                    changedSecondName.setError(getString(R.string.error_invalid_secondname));
                    focusView = changedSecondName;
                    cancel = true;
                }

                if (!TextUtils.isEmpty(txtChangedEmail) && !txtChangedEmail.contains("@")) {
                    changedEmail.setError(getString(R.string.invalid_mail));
                    focusView = changedEmail;
                    cancel = true;
                }


                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                }
                else {

                    if (TextUtils.isEmpty(iconString)) {
                        setDefaultIcon();
                    }

                    User user = new User();
                    user.setId(userID);
                    user.setUserName(changedUserName.getText().toString());
                    user.setUserFirstName(changedFirstName.getText().toString());
                    user.setUserLastName(changedSecondName.getText().toString());
                    user.setEmail(changedEmail.getText().toString());
                    user.setTelephoneNumber(changedPhoneNumber.getText().toString());
                    user.setUserIcon(iconString);
                    showProgress(true);


                    // Retrofit begin
                    RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
                    Call<User> call = restServiceClient.changeUser(user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            showProgress(false);

                            User userData = response.body();
                            if (userData.getErrorMessage() == null) {
                                setProfile(userData);

                            } else {
                                Toast.makeText(getApplicationContext(), "Loading personal info failed", Toast.LENGTH_SHORT).show();
                                EditProfileActivity.this.finish();
                            }
                            Intent output = new Intent();
                            output.putExtra("newUser", userData);
                            setResult(RESULT_OK, output);
                            EditProfileActivity.this.finish();

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Username taken - choose another one", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        Bundle extras =getIntent().getExtras();
        userID=extras.getLong("userID");

        // Retrofit begin
        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<User> call = restServiceClient.getUserForUserID(userID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userData = response.body();
                if (userData.getErrorMessage() == null) {
                    currentUser = userData;
                    setProfile(userData);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Loading personal info failed", Toast.LENGTH_SHORT).show();
                    EditProfileActivity.this.finish();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Loading personal info failed", Toast.LENGTH_SHORT).show();
                EditProfileActivity.this.finish();
            }
        } );

        mProgressView = findViewById(R.id.updateProfile_progress);
        mLoginFormView = findViewById(R.id.updateProfile);
    }

    private void setDefaultIcon() {
        Bitmap icon = BitmapFactory.decodeResource(EditProfileActivity.this.getResources(), R.drawable.default_avatar);

        iconString = encodeToBase64(icon);
    }

    private void setProfile(User userData) {
        profilePicture.setImageBitmap(decodeBase64(userData.getUserIcon()));
        changedUserName.setText(userData.getUserName());
        changedFirstName.setText(userData.getUserFirstName());
        changedSecondName.setText(userData.getUserLastName());
        changedEmail.setText(userData.getEmail());
        changedPhoneNumber.setText(userData.getTelephoneNumber());
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

    private void addPhoto(){
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Choose a method: ");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= CameraUtility.checkPermission(EditProfileActivity.this);

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

    @Override
    public void onBackPressed () {
        Intent output = new Intent();
        output.putExtra("newUser", currentUser);
        setResult(RESULT_OK, output);
        EditProfileActivity.this.finish();
    }
}
