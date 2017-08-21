package hr.fer.ruazosa.sharemylocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    long userID;
    public static long groupID;
    ListView messageList;
    EditText messageDescription;
    FloatingActionButton send;
    Message messageModel;

    private LocationManager locationManager;
    private LocationListener listener;
    private Location location;

    public static String currentLatitude;
    public static String currentLongitude;
    public static Activity activityContext;
    public static Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        userID = extras.getLong("id");
        group = (Group) extras.getSerializable("group");
        groupID = group.getId();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(group.getGroupName());

        messageList = (ListView) findViewById(R.id.listView);
//        messageList.setSelection(messageList.getCount()-1);

        messageDescription = (EditText) findViewById(R.id.message);
        send = (FloatingActionButton) findViewById(R.id.send_button);
        activityContext = this;

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLongitude != null){

                    messageModel = new Message();
                    messageModel.setDescription(messageDescription.getText().toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    messageModel.setMessageTimestamp(sdf.format(new Date()));
                    messageModel.setNegReaction(0);
                    messageModel.setPosReaction(0);
                    messageModel.setUserID(userID);
                    messageModel.setGroupID(groupID);
                    messageModel.setGeogLength(Double.parseDouble(currentLongitude));
                    messageModel.setGeogWidth(Double.parseDouble(currentLatitude));

                    messageDescription.setText("");

                    // Retrofit begin
                    RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
                    Call<Message> call = restServiceClient.addMessageToGroup(messageModel);
                    call.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            Message m = response.body();
                            if (m != null) {
                                Toast.makeText(getApplicationContext(), "Message added successfully ", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(), "Adding message failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Error adding message to group!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(ChatActivity.this, "Nemam koordinate", Toast.LENGTH_SHORT).show();
                }
            }
        } );


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location onLocation) {
                currentLongitude = "" + onLocation.getLongitude();
                currentLatitude = "" + onLocation.getLatitude();
                /*location.setLatitude(onLocation.getLatitude());
                location.setLongitude(onLocation.getLongitude());*/

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };


        locationManager = (LocationManager) ChatActivity.this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();


        for (int i = 0; i < providers.size(); i++) {
            // check if provider is available
            if (locationManager.isProviderEnabled(providers.get(i))) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                                ,10);
                    }
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(providers.get(i), 0, 0, listener);
                location = locationManager.getLastKnownLocation(providers.get(i));

                /*if (location != null) {
                    currentLongitude = "" + location.getLongitude();
                    currentLatitude = "" + location.getLatitude();
                }*/

            }
        }
        getMessages();
    }



    public void getMessages() {
            final ArrayList<Message> messages = new ArrayList<Message>();
            // Retrofit begin
            RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
            Call<List<Message>> call = restServiceClient.getAllGroupMessages(groupID);
            call.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    List<Message> userData = response.body();
                    if (!userData.isEmpty()) {
                        for (Message m : userData) {
                            messages.add(m);
                        }
                    }
                    ArrayAdapter<Message> adapter = new MessageMapAdapter(ChatActivity.activityContext, messages);
                    messageList.setAdapter(adapter);
                }


                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error loading messages!", Toast.LENGTH_SHORT).show();
                    ArrayAdapter<Message> adapter = new MessageMapAdapter(ChatActivity.activityContext, messages);
                    messageList.setAdapter(adapter);
                }
            });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.exit_group) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // R.I.P. :(
    /*public String myDateFormater (Date date){
        String year=date.toString().substring(30);
        String day=date.toString().substring(8,10);
        String hour=date.toString().substring(11,19);
        String monthName=date.toString().substring(4,7);
        String month=null;

        switch (monthName) {
            case  "Jan" :
                month = new String("01");
                break;
            case  "Feb" :
                month = new String("02");
                break;
            case  "Mar" :
                month = new String("03");
                break;
            case  "Apr" :
                month = new String("04");
                break;
            case  "May" :
                month = new String("05");
                break;
            case  "Jun" :
                month = new String("06");
                break;
            case "Jul" :
                month = new String("07");
                break;
            case "Aug" :
                month = new String("08");
                break;
            case "Sep" :
                month = new String("09");
                break;
            case "Oct" :
                month = new String("10");
                break;
            case "Nov" :
                month = new String("11");
                break;
            case "Dec" :
                month = new String("12");
                break;
        }
        String parsedDate=year+"-"+day+"-"+month+"T"+hour+"Z";

        return parsedDate;*/
        /*SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d = sdf.parse(time);
        String formattedTime = output.format(d);
        return formattedTime;*/


    public void exitGroup(MenuItem menuItem) {

        // Retrofit begin
        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<UserGroup> call = restServiceClient.removeFromGroup(userID, groupID);
        call.enqueue(new Callback<UserGroup>() {
            @Override
            public void onResponse(Call<UserGroup> call, Response<UserGroup> response) {
                UserGroup userData = response.body();
                if (userData.getErrorMessage() == null) {
                    Toast.makeText(getApplicationContext(), "Removed from group", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error leaving group", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserGroup> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error leaving group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void seeGroupInfo(MenuItem menuItem){
        goToGroupProfile();
    }

    private void goToGroupProfile() {
        Intent profileIntent = new Intent(ChatActivity.this, GroupInfoActivity.class);
        profileIntent.putExtra("group", group);
        startActivity(profileIntent);
    }

}
