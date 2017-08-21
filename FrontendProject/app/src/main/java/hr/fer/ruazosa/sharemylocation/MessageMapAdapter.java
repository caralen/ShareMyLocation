package hr.fer.ruazosa.sharemylocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter that displays a title and {@link com.google.android.gms.maps.MapView} for each item.
 * The layout is defined in <code>lite_list_demo_row.xml</code>. It contains a MapView
 * that is programatically initialised in
 * {@link #getView(int, android.view.View, android.view.ViewGroup)}
 */
public class MessageMapAdapter extends ArrayAdapter<Message> {

    private final HashSet<MapView> mMaps = new HashSet<MapView>();
    User user;
    ViewHolder holder;
    private static Context context;
    public static int i = 0;


    public MessageMapAdapter(Context context, ArrayList<Message> messageArrayList) {
        super(context, R.layout.mesage_element, messageArrayList);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        // Check if a view can be reused, otherwise inflate a layout and set up the view holder
        if (row == null) {
            // Inflate view from layout file
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.mesage_element, null);

            // Set up holder and assign it to the View
            holder = new ViewHolder();
            holder.mapView = (MapView) row.findViewById(R.id.lite_listrow_map);
            holder.title = (TextView) row.findViewById(R.id.lite_listrow_text);
            holder.userName = (TextView) row.findViewById(R.id.username);
            holder.time = (TextView) row.findViewById(R.id.time);
            // Set holder as tag for row for more efficient access.
            row.setTag(holder);

            // Initialise the MapView
            holder.initializeMapView();

            // Keep track of MapView
            mMaps.add(holder.mapView);
        } else {
            // View has already been initialised, get its holder
            holder = (ViewHolder) row.getTag();
            holder.map.clear();
        }

        // Get the NamedLocation for this item and attach it to the MapView
        Message item = getItem(position);
        holder.mapView.setTag(item);

        // Ensure the map has been initialised by the on map ready callback in ViewHolder.
        // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
        // when the callback is received.
        if (holder.map != null) {
            // The map is already ready to be used
            LatLng location = new LatLng(item.getGeogWidth(), item.getGeogLength());
            setMapLocation(holder.map, location);
        }

        // Set the text label for this item
        holder.title.setText(item.getDescription());
        Date date = new Date(Long.parseLong(item.getMessageTimestamp()));
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);
        //holder.time.setText(dateFormatted.substring(0,5));
        holder.time.setText(parseHour(dateFormatted));

        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<User> call = restServiceClient.getUserForUserID(item.getUserID());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userData = response.body();
                if (userData.getErrorMessage() == null) {
                    user=userData;
                    holder.userName.setText(user.getUserName());
                }
                else {
                    Toast.makeText(getContext(), "Error loading message", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading message", Toast.LENGTH_SHORT).show();
            }
        } );

        return row;
    }

    private String parseHour(String dateFormatted) {
        String hour=dateFormatted.substring(0,2);
        int hourInt=Integer.parseInt(hour);

        if(hourInt==1){
            hour=new String("23");
        }
        else if (hourInt==0){
            hour=new String("22");
        }
        else {
            hourInt-=2;
            if(hourInt<10 && hourInt>=0){
                String tmp="0"+String.valueOf(hourInt);
                hour=new String(tmp);
            }
            else{
                hour=new String(String.valueOf(hourInt));
            }
        }
        String time = hour+dateFormatted.substring(2,5);
        return time;
    }

    /**
     * Retuns the set of all initialised {@link MapView} objects.
     *
     * @return All MapViews that have been initialised programmatically by this adapter
     */
    public HashSet<MapView> getMaps() {
        return mMaps;
    }


    /**
     *
     * Adds a marker and centers the camera on the NamedLocation with the normal map type.
     */
    private static void setMapLocation(GoogleMap map, LatLng data) {
        i = i + 1;
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 14f));
        map.addMarker(new MarkerOptions().position(data));
        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    /**
     * Once the  the <code>map</code> field is set, otherwise it is null.
     * When the {@link #onMapReady(com.google.android.gms.maps.GoogleMap)} callback is received and
     * the {@link com.google.android.gms.maps.GoogleMap} is ready, it stored in the {@link #map}
     * field. The map is then initialised with the NamedLocation that is stored as the tag of the
     * MapView. This ensures that the map is initialised with the latest data that it should
     * display.
     */
    class ViewHolder implements OnMapReadyCallback {

        MapView mapView;
        TextView title;
        GoogleMap map;
        TextView userName;
        TextView time;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(MessageMapAdapter.context);
            map = googleMap;
            Message data = (Message) mapView.getTag();
            if (data != null) {
                LatLng location = new LatLng(data.getGeogWidth(), data.getGeogLength());
                setMapLocation(map, location);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

    }



    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the ListView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                // Clear the map and free up resources by changing the map type to none
                holder.map.clear();
                holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }

        }
    };
}
