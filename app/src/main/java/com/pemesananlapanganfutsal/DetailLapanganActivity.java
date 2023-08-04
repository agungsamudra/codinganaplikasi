package com.pemesananlapanganfutsal;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.model.User;
import com.pemesananlapanganfutsal.utils.SessionHandler;
import com.pemesananlapanganfutsal.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DetailLapanganActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/lapangan?id=";
    private static final String url_image = "/assets/images/lapangan/";
    private String id_lapangan;
    private ImageView img_lapangan;
    private TextView tv_nama_lapangan;
    private TextView tv_harga;
    private TextView tv_deskripsi;
    private RatingBar rating;
    private String str_rating;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_lapangan);
        setTitle("Detail Lapangan");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_lapangan = extras.getString("ID_LAPANGAN");
        }

        SessionHandler sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        img_lapangan = findViewById(R.id.img_lapangan);
        tv_nama_lapangan = findViewById(R.id.tv_nama_lapangan);
        tv_harga = findViewById(R.id.tv_harga);
        tv_deskripsi = findViewById(R.id.tv_deskripsi);
        TextView tv_ulasan = findViewById(R.id.tv_ulasan);
        rating = findViewById(R.id.rating);

        Button btn_jadwal = findViewById(R.id.btn_jadwal);
        btn_jadwal.setOnClickListener(view -> {
            if (isEmpty(user.getIdPemesan())) {
                Toast.makeText(DetailLapanganActivity.this, "Silahkan login dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent myIntent = new Intent(DetailLapanganActivity.this, CekJadwalActivity.class);
            myIntent.putExtra("ID_LAPANGAN", id_lapangan);
            startActivity(myIntent);
        });

        tv_ulasan.setOnClickListener(view -> {
            Intent myIntent = new Intent(DetailLapanganActivity.this, UlasanActivity.class);
            myIntent.putExtra("ID_LAPANGAN", id_lapangan);
            myIntent.putExtra("RATING", str_rating);
            startActivity(myIntent);
        });

        getData();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(DetailLapanganActivity.this);
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
                            JSONObject result = jObj.getJSONObject("data");
                            Picasso.get().load(Server.URL + url_image + result.getString("foto")).into(img_lapangan);
                            tv_nama_lapangan.setText(result.getString("nama_lapangan"));
                            tv_harga.setText("Rp. " + String.format(Locale.US, "%,d", result.getInt("harga_sewa")).replace(',', '.') + "/jam");
                            tv_deskripsi.setText("Kontak: " + result.getString("kontak"));
                            str_rating = jObj.getString("rating");
                            rating.setRating(Float.parseFloat(str_rating));
                        } else {
                            Toast.makeText(DetailLapanganActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailLapanganActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(DetailLapanganActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
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