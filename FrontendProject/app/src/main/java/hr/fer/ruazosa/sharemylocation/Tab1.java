package hr.fer.ruazosa.sharemylocation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 29.6.2017..
 */

public class Tab1 extends ListFragment {

    long userID;
    long groupID;
    private ListView list;
    List<Group> userGroups = new ArrayList<>();
    SwipeRefreshLayout swipeContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Animate the background color of clicked Item
                ColorDrawable[] color = {
                        new ColorDrawable(Color.parseColor("#ffffff")),
                        new ColorDrawable(Color.parseColor("#efeded"))
                };
                TransitionDrawable trans = new TransitionDrawable(color);
                view.setBackground(trans);
                trans.startTransition(2000); // duration 2 seconds

                // Go back to the default background color of Item
                ColorDrawable[] color2 = {
                        new ColorDrawable(Color.parseColor("#efeded")),
                        new ColorDrawable(Color.parseColor("#ffffff"))
                };
                TransitionDrawable trans2 = new TransitionDrawable(color2);
                view.setBackground(trans2);
                trans2.startTransition(2000); // duration 2 seconds
                goToGroup(position);

                goToGroup(position);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            userID = bundle.getLong("id");
        }

        // Create the list fragment's content view by calling the super method
        final View listFragmentView = super.onCreateView(inflater, container, savedInstanceState);

        // Now create a SwipeRefreshLayout to wrap the fragment's content view
        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());

        // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
        // the SwipeRefreshLayout
        mSwipeRefreshLayout.addView(listFragmentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Make sure that the SwipeRefreshLayout will fill the fragment
        mSwipeRefreshLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        // Now return the SwipeRefreshLayout as this fragment's content view

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRefreshing(false);
                generateData();
            }
        });

        return mSwipeRefreshLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        generateData();
    }


    public void generateData(){
        final ArrayList<Model> models = new ArrayList<Model>();
        // Retrofit begin
        RestServiceClient restServiceClient = RestServiceClient.retrofit.create(RestServiceClient.class);
        Call<List<Group>> call = restServiceClient.getUserGroups(userID);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                List<Group> userData = response.body();
                if (!userData.isEmpty()) {
                    for (Group g : userData) {
                        models.add(new Model(g.getIcon(), g.getGroupName()));
                    }
                    userGroups = new ArrayList<Group>();
                    for (Group g : userData) {
                        userGroups.add(g);
                    }
                }
                ArrayAdapter<Model> adapter = new MyAdapter(getActivity(), models);
                setListAdapter(adapter);

            }


            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), "No groups", Toast.LENGTH_SHORT).show();
                ArrayAdapter<Model> adapter = new MyAdapter(getActivity(), models);
                setListAdapter(adapter);
            }
        });
    }


    private void goToGroup(final int position) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("id", userID);
        groupID=userGroups.get(position).getId();
        intent.putExtra("group",userGroups.get(position));
        startActivity(intent);

    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }


    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

        public ListFragmentSwipeRefreshLayout(Context context) {
            super(context);
        }


        @Override
        public boolean canChildScrollUp() {
            final ListView listView = getListView();
            if (listView.getVisibility() == View.VISIBLE) {
                return canListViewScrollUp(listView);
            } else {
                return false;
            }
        }

    }

    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }


    public void setColorScheme(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        mSwipeRefreshLayout.setColorScheme(colorRes1, colorRes2, colorRes3, colorRes4);
    }


    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    private static boolean canListViewScrollUp(ListView listView) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS and above we can call canScrollVertically() to determine this
            return ViewCompat.canScrollVertically(listView, -1);
        } else {
            // Pre-ICS we need to manually check the first visible item and the child view's top
            // value
            return listView.getChildCount() > 0 &&
                    (listView.getFirstVisiblePosition() > 0
                            || listView.getChildAt(0).getTop() < listView.getPaddingTop());
        }
    }
}
