package com.pemesananlapanganfutsal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private final static String url = "/api/pemesan";
    private TextView nama_pemesan;
    private TextView alamat;
    private TextView no_hp;
    private TextView email;
    private TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nama_pemesan = findViewById(R.id.nama_pemesan);
        alamat = findViewById(R.id.alamat);
        no_hp = findViewById(R.id.no_hp);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        Button btn_daftar = findViewById(R.id.btn_daftar);
        Button btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btn_daftar.setOnClickListener(view -> {
            if (validateInputs()) {
                postData();
            }
        });
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
        pDialog = new ProgressDialog(RegisterActivity.this);
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
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
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
                params.put("nama_pemesan", nama_pemesan.getText().toString());
                params.put("alamat", alamat.getText().toString());
                params.put("no_hp", no_hp.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(smr);
    }

    private boolean validateInputs() {
        if (nama_pemesan.getText().toString().equals("")) {
            nama_pemesan.setError("Isi dulu Nama Lengkap");
            nama_pemesan.requestFocus();
            return false;
        }
        if (alamat.getText().toString().equals("")) {
            alamat.setError("Isi dulu Alamat");
            alamat.requestFocus();
            return false;
        }
        if (no_hp.getText().toString().equals("")) {
            no_hp.setError("Isi dulu No HP");
            no_hp.requestFocus();
            return false;
        }
        if (email.getText().toString().equals("")) {
            email.setError("Isi dulu Email");
            email.requestFocus();
            return false;
        }
        if (password.getText().toString().equals("")) {
            password.setError("Isi dulu Password");
            password.requestFocus();
            return false;
        }
        return true;
    }
}