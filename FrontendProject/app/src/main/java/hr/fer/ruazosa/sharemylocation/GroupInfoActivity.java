package hr.fer.ruazosa.sharemylocation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupInfoActivity extends AppCompatActivity {

    private CircleImageView groupIcon;
    private TextView groupName;
    private TextView groupInfo;
    private TextView adminUsername;
    private TextView numOfMembers;

    public  static  Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        Bundle extras =getIntent().getExtras();
        group = (Group) extras.getSerializable("group");


        groupIcon = (CircleImageView) findViewById(R.id.groupPic);
        groupName = (TextView) findViewById(R.id.groupname);
        groupInfo = (TextView) findViewById(R.id.groupinfo);
        adminUsername = (TextView) findViewById(R.id.admin);
        numOfMembers = (TextView) findViewById(R.id.numofmembers);

        // Retrofit begin
        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<User> call = restServiceClient.getUserForUserID(group.getGroupAdmin());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userData = response.body();
                if (userData.getErrorMessage() == null) {
                    setProfile(userData);

                }
                else {
                    Toast.makeText(getApplicationContext(), "Loading group info failed", Toast.LENGTH_SHORT).show();
                    GroupInfoActivity.this.finish();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Loading group info failed", Toast.LENGTH_SHORT).show();
                GroupInfoActivity.this.finish();
            }
        } );

    }

    private void setProfile(User userData) {
        groupIcon.setImageBitmap(decodeBase64(group.getIcon()));
        groupName.setText(group.getGroupName());
        adminUsername.setText(userData.getUserName());
        numOfMembers.setText(String.valueOf(group.getNoOfMembers()));
    }

    private Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
