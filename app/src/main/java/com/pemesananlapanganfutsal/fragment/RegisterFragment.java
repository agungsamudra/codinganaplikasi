package com.pemesananlapanganfutsal.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.Server;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private ProgressDialog pDialog;
    private final static String url = "/api/pemesan";
    private TextView nama_pemesan;
    private TextView alamat;
    private TextView no_hp;
    private TextView email;
    private TextView password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        nama_pemesan = v.findViewById(R.id.nama_pemesan);
        alamat = v.findViewById(R.id.alamat);
        no_hp = v.findViewById(R.id.no_hp);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);

        Button btn_daftar = v.findViewById(R.id.btn_daftar);

        btn_daftar.setOnClickListener(view -> {
            if (validateInputs()) {
                postData();
            }
        });

        return v;
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(getContext());
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
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        if (jObj.getString("status").equals("true")) {
                            FragmentManager fragmentManager = getParentFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, new LoginFragment());
                            fragmentTransaction.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

        VolleySingleton.getInstance(getContext()).addToRequestQueue(smr);
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