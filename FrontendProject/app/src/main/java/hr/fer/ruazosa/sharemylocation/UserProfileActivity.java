package hr.fer.ruazosa.sharemylocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 30.06.2017..
 */

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextView username;
    private TextView profileInfo;
    private TextView name;
    private TextView email;
    private TextView phoneNumber;
    private Button editProfile;
    private Button btnSetNewPassword;
    private int requestCode = 1;

    long id;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(hr.fer.ruazosa.sharemylocation.R.layout.user_profile);

        Bundle extras =getIntent().getExtras();
        id=extras.getLong("id");

        profilePicture = (CircleImageView) findViewById(R.id.profilePic);
        username = (TextView) findViewById(R.id.username);
        profileInfo = (TextView) findViewById(R.id.personalinfo);
        name = (TextView) findViewById(R.id.firstnamelastname);
        email = (TextView) findViewById(R.id.email);
        phoneNumber = (TextView) findViewById(R.id.phone);
        editProfile = (Button) findViewById(R.id.editprofile);
        btnSetNewPassword = (Button) findViewById(R.id.changePassword);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("userID", id);
                startActivityForResult(intent, requestCode);

            }
        });

        btnSetNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
                intent.putExtra("userID", id);
                startActivity(intent);

            }
        });

        // Retrofit begin
        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<User> call = restServiceClient.getUserForUserID(id);
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
                    UserProfileActivity.this.finish();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Loading personal info failed", Toast.LENGTH_SHORT).show();
                UserProfileActivity.this.finish();
            }
        } );


    }

    private void setProfile(User userData) {
        profilePicture.setImageBitmap(decodeBase64(userData.getUserIcon()));
        username.setText(userData.getUserName());
        name.setText(userData.getUserFirstName() + " " + userData.getUserLastName());
        email.setText(userData.getEmail());
        phoneNumber.setText(userData.getTelephoneNumber());
    }

    private Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        User newUser = (User) data.getSerializableExtra("newUser");
        if(newUser!=null && !currentUser.equals(newUser)) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        else if (newUser==null){
            Toast.makeText(UserProfileActivity.this, "Profile not updated", Toast.LENGTH_LONG).show();
        }

    }



}
