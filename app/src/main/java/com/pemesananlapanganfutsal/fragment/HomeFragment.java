package com.pemesananlapanganfutsal.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.pemesananlapanganfutsal.DetailLapanganActivity;
import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.Server;
import com.pemesananlapanganfutsal.adapter.LapanganAdapter;
import com.pemesananlapanganfutsal.model.Lapangan;
import com.pemesananlapanganfutsal.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ProgressDialog pDialog;
    private static final String url = "/api/terlaris";
    private static final String url_image = "/assets/images/lapangan/";
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private LapanganAdapter lapanganAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Lapangan> lapanganList1;
    private ArrayList<Lapangan> lapanganList2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView1 = view.findViewById(R.id.rc_rec);
        recyclerView2 = view.findViewById(R.id.rc_new);
        getData();
        return view;
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Sedang diproses...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void getData() {
        displayLoader();
        StringRequest smr = new StringRequest(Request.Method.GET, Server.URL + url,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (jObj.getString("status").equals("true")) {
                            // menu paling banyak dipesan
                            JSONArray result = jObj.getJSONArray("data_best");
                            lapanganList1 = new ArrayList<>();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);
                                lapanganList1.add(
                                        new Lapangan(
                                                jsonObject.getInt("id_lapangan"),
                                                jsonObject.getString("nama_lapangan"),
                                                Server.URL + url_image + jsonObject.getString("foto"),
                                                jsonObject.getInt("harga_sewa")
                                        )
                                );
                            }

                            lapanganAdapter = new LapanganAdapter(lapanganList1);
                            recyclerView1.setHasFixedSize(true);
                            layoutManager = new GridLayoutManager(getContext(), 2);
                            recyclerView1.setLayoutManager(layoutManager);
                            recyclerView1.setAdapter(lapanganAdapter);
                            lapanganAdapter.setOnItemClickListener(position -> {
                                Intent myIntent = new Intent(getActivity(), DetailLapanganActivity.class);
                                myIntent.putExtra("ID_LAPANGAN", Integer.toString(lapanganList1.get(position).getIdLapangan()));
                                startActivity(myIntent);
                            });

                            // menu semua lapangan
                            JSONArray result2 = jObj.getJSONArray("data_new");
                            lapanganList2 = new ArrayList<>();
                            for (int i = 0; i < result2.length(); i++) {
                                JSONObject jsonObject = result2.getJSONObject(i);
                                lapanganList2.add(
                                        new Lapangan(
                                                jsonObject.getInt("id_lapangan"),
                                                jsonObject.getString("nama_lapangan"),
                                                Server.URL + url_image + jsonObject.getString("foto"),
                                                jsonObject.getInt("harga_sewa")
                                        )
                                );
                            }
                            lapanganAdapter = new LapanganAdapter(lapanganList2);
                            recyclerView2.setHasFixedSize(true);
                            layoutManager = new GridLayoutManager(getContext(), 2);
                            recyclerView2.setLayoutManager(layoutManager);
                            recyclerView2.setAdapter(lapanganAdapter);
                            lapanganAdapter.setOnItemClickListener(position -> {
                                Intent myIntent = new Intent(getActivity(), DetailLapanganActivity.class);
                                myIntent.putExtra("ID_LAPANGAN", Integer.toString(lapanganList2.get(position).getIdLapangan()));
                                startActivity(myIntent);
                            });

                        } else {
                            Toast.makeText(getContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        smr.setShouldCache(false);
        VolleySingleton.getInstance(getContext()).addToRequestQueue(smr);
    }
}