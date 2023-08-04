package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class PemesananDetailActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/pemesanandetail?id=";
    private String id_pemesanan, id_lapangan, total;
    private TextView tv_kode_pemesanan;
    private TextView tv_tanggal;
    private TextView tv_nama_pemesan;
    private TextView tv_nama_lapangan;
    private TextView tv_tgl_booking;
    private TextView tv_total;
    private TextView tv_dp;
    private TextView tv_status;
    private Button btn_rating, btn_dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemesanan_detail);
        setTitle("Detail Pemesanan");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_pemesanan = extras.getString("ID_PEMESANAN");
        }

        tv_kode_pemesanan = findViewById(R.id.tv_kode_pemesanan);
        tv_tanggal = findViewById(R.id.tv_tanggal);
        tv_nama_pemesan = findViewById(R.id.tv_nama_pemesan);
        tv_nama_lapangan = findViewById(R.id.tv_nama_lapangan);
        tv_tgl_booking = findViewById(R.id.tv_tgl_booking);
        tv_total = findViewById(R.id.tv_total);
        tv_dp = findViewById(R.id.tv_dp);
        tv_status = findViewById(R.id.tv_status);

        btn_rating = findViewById(R.id.btn_rating);
        btn_rating.setOnClickListener(view -> {
            Intent myIntent = new Intent(PemesananDetailActivity.this, KirimUlasanActivity.class);
            myIntent.putExtra("ID_PEMESANAN", id_pemesanan);
            myIntent.putExtra("ID_LAPANGAN", id_lapangan);
            startActivity(myIntent);
        });

        btn_dp = findViewById(R.id.btn_dp);
        btn_dp.setOnClickListener(view -> {
            Intent myIntent = new Intent(PemesananDetailActivity.this, PembayaranDPActivity.class);
            myIntent.putExtra("ID_PEMESANAN", id_pemesanan);
            myIntent.putExtra("TOTAL", total);
            startActivity(myIntent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(PemesananDetailActivity.this);
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void getData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url + id_pemesanan,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            JSONObject result = jObj.getJSONObject("data");
                            tv_kode_pemesanan.setText(result.getString("kode_pemesanan"));
                            tv_tanggal.setText(result.getString("tanggal"));
                            tv_nama_pemesan.setText(result.getString("nama_pemesan"));
                            tv_nama_lapangan.setText(result.getString("nama_lapangan"));
                            tv_tgl_booking.setText(result.getString("tgl_booking") + " jam " + result.getString("jam_booking"));
                            tv_total.setText("Rp. " + String.format(Locale.US, "%,d", result.getInt("total")).replace(',', '.'));
                            tv_dp.setText("Rp. " + String.format(Locale.US, "%,d", result.getInt("dp")).replace(',', '.'));
                            tv_status.setText(result.getString("status"));
                            total = result.getString("total");

                            if (result.getString("status").equals("Lunas")) {
                                if (result.getInt("ulasan") == 0) {
                                    btn_rating.setVisibility(View.VISIBLE);
                                } else {
                                    btn_rating.setVisibility(View.GONE);
                                }
                            }

                            if (result.getString("status").equals("Belum Lunas")) {
                                if (result.getInt("dp") > 0) {
                                    btn_dp.setVisibility(View.GONE);
                                } else {
                                    btn_dp.setVisibility(View.VISIBLE);
                                }
                            } else {
                                btn_dp.setVisibility(View.GONE);
                            }

                            id_lapangan = result.getString("id_lapangan");
                        } else {
                            Toast.makeText(PemesananDetailActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PemesananDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(PemesananDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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