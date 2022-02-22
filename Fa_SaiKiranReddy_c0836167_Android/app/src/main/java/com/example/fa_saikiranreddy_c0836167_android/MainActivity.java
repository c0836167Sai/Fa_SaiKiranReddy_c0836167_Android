package com.example.fa_saikiranreddy_c0836167_android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int UPDATE_LIST = 200;
    private RecyclerView recyclerView;
    private FavoritePlacesAdapter favoritePlacesAdapter;
    private RecyclerTouchListener touchListener;
    private ArrayList<FavoritePlace> placesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        favoritePlacesAdapter = new FavoritePlacesAdapter(this);
        placesList = new ArrayList<>();
        recyclerView.setAdapter(favoritePlacesAdapter);
        getPlacesList();

        touchListener = new RecyclerTouchListener(this, recyclerView);
        touchListener
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        //navigateToMapsActivity("", position);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {

                    }
                })
                .setSwipeOptionViews(R.id.delete_place, R.id.edit_place, R.id.visit_place)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        switch (viewID) {
                            case R.id.delete_place:
                                deletePlace(placesList.get(position));
                                break;
                            case R.id.edit_place:
                                navigateToMapsActivity("edit", position);
                                break;
                            case R.id.visit_place:
                                UpdatePlace up = new UpdatePlace(position);
                                up.execute();
                                break;
                        }
                    }
                });
        recyclerView.addOnItemTouchListener(touchListener);
        findViewById(R.id.floating_button_add).setOnClickListener(view -> {
            navigateToMapsActivity("add", -1);
        });
    }

    private void updateRecyclerViewData() {
        getPlacesList();
    }

    private void navigateToMapsActivity(String action, int position) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        if (position != -1) {

            intent.putExtra("ClickedPlace", placesList.get(position));

            String address = placesList.get(position).getAddress();
            intent.putExtra("address", address.isEmpty() ? placesList.get(position).getDate() : address);
            intent.putExtra("lat", placesList.get(position).getLatitude());
            intent.putExtra("lng", placesList.get(position).getLongitude());
        }
        intent.putExtra("action", action);
        if (!action.isEmpty()) {
            startActivityForResult(intent, UPDATE_LIST);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.addOnItemTouchListener(touchListener);
    }

    private void getPlacesList() {
        class GetTasks extends AsyncTask<Void, Void, List<FavoritePlace>> {

            @Override
            protected List<FavoritePlace> doInBackground(Void... voids) {
                List<FavoritePlace> placeList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .placeDao()
                        .getAll();
                return placeList;
            }

            @Override
            protected void onPostExecute(List<FavoritePlace> favoritePlaces) {
                super.onPostExecute(favoritePlaces);
                placesList.clear();
                placesList.addAll(favoritePlaces);
                favoritePlacesAdapter.setFavoritePlaces(placesList);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void deletePlace(final FavoritePlace place) {
        class DeletePlace extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .placeDao()
                        .delete(place);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                updateRecyclerViewData();
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
            }
        }

        DeletePlace dp = new DeletePlace();
        dp.execute();
    }

    class UpdatePlace extends AsyncTask<Void, Void, Void> {

        FavoritePlace place;

        public UpdatePlace(int position) {
            place = placesList.get(position);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            place.setCategory("Visited");
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .placeDao()
                    .update(place);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerViewData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_LIST) {
            updateRecyclerViewData();
        }
    }

}