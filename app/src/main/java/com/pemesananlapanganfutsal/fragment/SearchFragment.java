package com.pemesananlapanganfutsal.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.SearchHasilActivity;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        final EditText et_kata_kunci = v.findViewById(R.id.et_kata_kunci);

        Button btn_cari = v.findViewById(R.id.btn_cari);
        btn_cari.setOnClickListener(view -> {
            Intent myIntent = new Intent(getActivity(), SearchHasilActivity.class);
            myIntent.putExtra("KATA_KUNCI", et_kata_kunci.getText().toString());
            startActivity(myIntent);
        });

        return v;
    }
}