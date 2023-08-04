package com.pemesananlapanganfutsal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.model.User;
import com.pemesananlapanganfutsal.utils.SessionHandler;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/pemesan?id=";
    private static final String url_post = "/api/pemesanan";
    private EditText et_nama;
    private EditText et_alamat;
    private EditText et_no_hp;
    private User user;
    private int total;
    private String id_lapangan, tgl_booking, jam_booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setTitle("Checkout");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SessionHandler sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        total = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            total = extras.getInt("HARGA_SEWA");
            id_lapangan = extras.getString("ID_LAPANGAN");
            tgl_booking = extras.getString("TGL_BOOKING");
            jam_booking = extras.getString("JAM_BOOKING");
        }

        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_no_hp = findViewById(R.id.et_no_hp);
        TextView tv_total = findViewById(R.id.tv_total);
        tv_total.setText("Rp. " + String.format(Locale.US, "%,d", total).replace(',', '.'));

        Button btn_checkout = findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(view -> {
            if (validateInputs()) {
                postData();
            }
        });

        getData();
    }

    private void postData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.POST, Server.URL + url_post,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                        if (jObj.getString("status").equals("true")) {
                            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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
                params.put("total", String.valueOf(total));
                params.put("nama_pemesan", et_nama.getText().toString());
                params.put("alamat", et_alamat.getText().toString());
                params.put("no_hp", et_no_hp.getText().toString());
                params.put("id_lapangan", id_lapangan);
                params.put("tgl_booking", tgl_booking);
                params.put("jam_booking", jam_booking);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(smr);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(CheckoutActivity.this);
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
                            JSONObject result = jObj.getJSONObject("data");
                            et_nama.setText(result.getString("nama_pemesan"));
                            et_alamat.setText(result.getString("alamat"));
                            et_no_hp.setText(result.getString("no_hp"));
                        } else {
                            Toast.makeText(CheckoutActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CheckoutActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(CheckoutActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(CheckoutActivity.this).addToRequestQueue(smr);
    }

    private boolean validateInputs() {
        if (et_nama.getText().toString().equals("")) {
            et_nama.setError("Isi dulu Nama");
            et_nama.requestFocus();
            return false;
        }
        if (et_alamat.getText().toString().equals("")) {
            et_alamat.setError("Isi dulu Alamat");
            et_alamat.requestFocus();
            return false;
        }
        if (et_no_hp.getText().toString().equals("")) {
            et_no_hp.setError("Isi dulu No HP");
            et_no_hp.requestFocus();
            return false;
        }
        return true;
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