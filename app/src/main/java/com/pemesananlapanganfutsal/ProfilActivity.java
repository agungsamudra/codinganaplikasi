package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

public class ProfilActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String url = "/api/pemesan?id=";
    private static final String url_post = "/api/akun";
    private EditText et_nama;
    private EditText et_alamat;
    private EditText et_no_hp;
    private EditText et_email;
    private EditText et_password;
    private User user;
    private String email_lama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setTitle("Profil");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SessionHandler sessionHandler = new SessionHandler(this);
        user = sessionHandler.getUserDetails();

        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_no_hp = findViewById(R.id.et_no_hp);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        Button btn_ubah = findViewById(R.id.btn_ubah);
        btn_ubah.setOnClickListener(view -> {
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
                        String message = jObj.getString("message");
                        if (jObj.getString("status").equals("true")) {
                            Toast.makeText(ProfilActivity.this, message, Toast.LENGTH_SHORT).show();
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
                params.put("nama_pemesan", et_nama.getText().toString());
                params.put("alamat", et_alamat.getText().toString());
                params.put("no_hp", et_no_hp.getText().toString());
                params.put("email", et_email.getText().toString());
                params.put("email_lama", email_lama);
                params.put("password", et_password.getText().toString());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(smr);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(ProfilActivity.this);
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
                            et_email.setText(result.getString("email"));
                            email_lama = result.getString("email");
                        } else {
                            Toast.makeText(ProfilActivity.this, jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfilActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(ProfilActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(ProfilActivity.this).addToRequestQueue(smr);
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
        if (et_email.getText().toString().equals("")) {
            et_email.setError("Isi dulu Email");
            et_email.requestFocus();
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