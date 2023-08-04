package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.adapter.LapanganAdapter;
import com.pemesananlapanganfutsal.model.Lapangan;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchHasilActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/search?kata_kunci=";
    private static final String url_image = "/assets/images/lapangan/";
    private RecyclerView recyclerView;
    private LapanganAdapter lapanganAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Lapangan> lapanganList;
    private String kata_kunci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hasil);
        setTitle("Hasil Pencarian");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            kata_kunci = extras.getString("KATA_KUNCI");
        }

        recyclerView = findViewById(R.id.rc_search);
        getData();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(SearchHasilActivity.this);
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void getData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url + kata_kunci,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            JSONArray result = jObj.getJSONArray("data");
                            lapanganList = new ArrayList<>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);
                                lapanganList.add(
                                        new Lapangan(
                                                jsonObject.getInt("id_lapangan"),
                                                jsonObject.getString("nama_lapangan"),
                                                Server.URL + url_image + jsonObject.getString("foto"),
                                                jsonObject.getInt("harga_sewa")
                                        )
                                );
                            }
                            lapanganAdapter = new LapanganAdapter(lapanganList);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new GridLayoutManager(SearchHasilActivity.this, 2);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(lapanganAdapter);
                            lapanganAdapter.setOnItemClickListener(position -> {
                                Intent myIntent = new Intent(SearchHasilActivity.this, DetailLapanganActivity.class);
                                myIntent.putExtra("ID_LAPANGAN", Integer.toString(lapanganList.get(position).getIdLapangan()));
                                startActivity(myIntent);
                            });
                        } else {
                            Toast.makeText(SearchHasilActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchHasilActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(SearchHasilActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(SearchHasilActivity.this).addToRequestQueue(smr);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}