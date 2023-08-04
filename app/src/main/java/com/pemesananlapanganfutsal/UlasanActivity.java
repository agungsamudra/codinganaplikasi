package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.adapter.UlasanAdapter;
import com.pemesananlapanganfutsal.model.Ulasan;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UlasanActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/ulasan?id=";
    private RecyclerView recyclerView;
    private UlasanAdapter ulasanAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Ulasan> ulasanList;
    private String id_lapangan;
    private String str_rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ulasan);
        setTitle("Rating dan ulasan");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_lapangan = extras.getString("ID_LAPANGAN");
            str_rating = extras.getString("RATING");
        }

        recyclerView = findViewById(R.id.rc_ulasan);
        TextView tv_rating = findViewById(R.id.tv_rating);
        RatingBar rating = findViewById(R.id.rating);

        tv_rating.setText(str_rating);
        rating.setRating(Float.parseFloat(str_rating));

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(UlasanActivity.this);
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void getData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url + id_lapangan,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            JSONArray result = jObj.getJSONArray("data");
                            ulasanList = new ArrayList<>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);
                                ulasanList.add(
                                        new Ulasan(
                                                jsonObject.getString("nama_pemesan"),
                                                jsonObject.getString("tanggal"),
                                                jsonObject.getString("rating"),
                                                jsonObject.getString("ulasan")
                                        )
                                );
                            }
                            ulasanAdapter = new UlasanAdapter(ulasanList);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(UlasanActivity.this);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(ulasanAdapter);
                        } else {
                            Toast.makeText(UlasanActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UlasanActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(UlasanActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(UlasanActivity.this).addToRequestQueue(smr);
    }
}