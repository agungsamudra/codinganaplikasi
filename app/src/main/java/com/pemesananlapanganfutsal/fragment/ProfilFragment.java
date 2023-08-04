package com.pemesananlapanganfutsal.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pemesananlapanganfutsal.PemesananActivity;
import com.pemesananlapanganfutsal.ProfilActivity;
import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.utils.SessionHandler;

public class ProfilFragment extends Fragment {

    private SessionHandler sessionHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profil, container, false);

        sessionHandler = new SessionHandler(v.getContext());
        Button btn_pemesanan = v.findViewById(R.id.btn_pemesanan);
        Button btn_profil = v.findViewById(R.id.btn_profil);
        Button btn_logout = v.findViewById(R.id.btn_logout);

        btn_pemesanan.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), PemesananActivity.class);
            startActivity(intent);
        });

        btn_profil.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ProfilActivity.class);
            startActivity(intent);
        });

        btn_logout.setOnClickListener(view -> {
            sessionHandler.logoutUser();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
            fragmentTransaction.commit();
        });

        return v;
    }
}