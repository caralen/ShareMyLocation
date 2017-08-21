package hr.fer.ruazosa.sharemylocation;

/**
 * Created on 6/25/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity {
    Button btnCreateGroup;
    EditText editGroupName;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView userIcon;
    private TextView userIconLabel;
    private String userChoosenTask;
    private String iconString;
    long userID;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Intent intent = getIntent();
        Bundle extras =getIntent().getExtras();
        userID=extras.getLong("id");

        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        editGroupName = (EditText) findViewById(R.id.editGroupName);
        //editAdminName=(EditText) findViewById(R.id.editAdminName) ;

        userIcon = (ImageView) findViewById(hr.fer.ruazosa.sharemylocation.R.id.photoadd);
        userIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addGroupPhoto();
            }
        });
        userIconLabel = (TextView) findViewById(hr.fer.ruazosa.sharemylocation.R.id.iconLabel);

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreate();
            }
        });


        mProgressView = findViewById(R.id.createGroup_progress);
        mLoginFormView = findViewById(R.id.btnCreateGroup);
    }

    private void attemptCreate() {

        editGroupName.setError(null);
        //editAdminName.setError(null);

        boolean cancel = false;
        View focusView = null;

        //String adminName = editAdminName.getText().toString();
        String groupName = editGroupName.getText().toString();

        if (TextUtils.isEmpty(iconString)) {
            userIconLabel.setError(getString(R.string.error_field_required));
            focusView = userIcon;
            cancel = true;
        }

//        if (TextUtils.isEmpty(adminName)) {
//            editAdminName.setError(getString(R.string.error_field_required));
//            focusView = editAdminName;
//            cancel = true;
//        }
        if (TextUtils.isEmpty(groupName)) {
            editGroupName.setError(getString(R.string.error_field_required));
            focusView = editGroupName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else{
            Group group= new Group();
            group.setGroupName(groupName);
            group.setGroupAdmin(userID);
            //group.setNoOfMembers(1);
            group.setIcon(iconString);


            // Retrofit begin
            showProgress(true);
            RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
            Call<Group> call = restServiceClient.createGroup(group);
            call.enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    showProgress(false);
                    Group groupData = response.body();
                    if (groupData.getErrorMessage() == null) {
                        Toast.makeText(getApplicationContext(), "Group created successfully ", Toast.LENGTH_SHORT).show();
                        CreateGroupActivity.this.finish();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Creating group failed", Toast.LENGTH_SHORT).show();
                        CreateGroupActivity.this.finish();
                    }

                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Creating group failed", Toast.LENGTH_SHORT).show();
                    CreateGroupActivity.this.finish();
                }
            } );
        }
    }

    private void addGroupPhoto() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        builder.setTitle("Choose a method: ");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = CameraUtility.checkPermission(CreateGroupActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask = "Choose from Gallery";
                    if (result)
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
