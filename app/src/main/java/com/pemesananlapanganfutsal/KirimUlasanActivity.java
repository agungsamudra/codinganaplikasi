package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.model.User;
import com.pemesananlapanganfutsal.utils.SessionHandler;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class KirimUlasanActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/rating";
    private String id_pemesanan, id_lapangan;
    private EditText et_ulasan;
    private RatingBar rating;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_ulasan);
        setTitle("Kirim Ulasan");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_pemesanan = extras.getString("ID_PEMESANAN");
            id_lapangan = extras.getString("ID_LAPANGAN");
        }

        SessionHandler sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        et_ulasan = findViewById(R.id.et_ulasan);
        rating = findViewById(R.id.rating);

        Button btn_kirim = findViewById(R.id.btn_kirim);
        btn_kirim.setOnClickListener(view -> {
            if (validateInputs()) {
                postData();
            }
        });
    }

    private boolean validateInputs() {
        if (rating.getRating() == 0.0) {
            Toast.makeText(this, "Isi dulu rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_ulasan.getText().toString().equals("")) {
            et_ulasan.setError("Isi dulu ulasan");
            et_ulasan.requestFocus();
            return false;
        }
        return true;
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(KirimUlasanActivity.this);
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void postData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.POST, Server.URL + url,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String message = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        if (jObj.getString("status").equals("true")) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_pemesan", user.getIdPemesan());
                params.put("id_lapangan", id_lapangan);
                params.put("rating", String.valueOf(rating.getRating()));
                params.put("ulasan", et_ulasan.getText().toString());
                params.put("id_pemesanan", id_pemesanan);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(smr);
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