package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.adapter.PemesananAdapter;
import com.pemesananlapanganfutsal.model.Pemesanan;
import com.pemesananlapanganfutsal.model.User;
import com.pemesananlapanganfutsal.utils.SessionHandler;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PemesananActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/pemesanan?id=";
    private RecyclerView recyclerView;
    private PemesananAdapter pemesananAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Pemesanan> pemesananList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemesanan);
        setTitle("Pemesanan");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SessionHandler sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        recyclerView = findViewById(R.id.rc_pemesanan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(PemesananActivity.this);
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void getData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url + user.getIdPemesan(),
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            JSONArray result = jObj.getJSONArray("data");
                            pemesananList = new ArrayList<>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);
                                pemesananList.add(
                                        new Pemesanan(
                                                jsonObject.getString("id_pemesanan"),
                                                jsonObject.getString("kode_pemesanan"),
                                                jsonObject.getString("tanggal"),
                                                jsonObject.getInt("total"),
                                                jsonObject.getString("status")
                                        )
                                );
                            }
                            pemesananAdapter = new PemesananAdapter(pemesananList);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(PemesananActivity.this);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(pemesananAdapter);
                            pemesananAdapter.setOnItemClickListener(position -> {
                                Intent myIntent = new Intent(PemesananActivity.this, PemesananDetailActivity.class);
                                myIntent.putExtra("ID_PEMESANAN", pemesananList.get(position).getIdPemesanan());
                                startActivity(myIntent);
                            });
                        } else {
                            Toast.makeText(PemesananActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PemesananActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(PemesananActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(PemesananActivity.this).addToRequestQueue(smr);
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