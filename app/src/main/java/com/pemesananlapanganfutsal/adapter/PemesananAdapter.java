package com.pemesananlapanganfutsal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.model.Pemesanan;

import java.util.ArrayList;
import java.util.Locale;

public class PemesananAdapter extends RecyclerView.Adapter<PemesananAdapter.PemesananViewHolder> {
    private ArrayList<Pemesanan> pemesananList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    public PemesananAdapter(ArrayList<Pemesanan> pemesananList) {
        this.pemesananList = pemesananList;
    }

    @NonNull
    @Override
    public PemesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pemesanan_item, parent, false);
        PemesananViewHolder pemesananViewHolder = new PemesananViewHolder(view, listener);
        return pemesananViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PemesananViewHolder holder, int position) {
        Pemesanan currentItem = pemesananList.get(position);
        holder.tv_kode_pemesanan.setText("Kode: "+currentItem.getKodePemesanan());
        holder.tv_tanggal.setText("Tanggal: "+currentItem.getTanggal());
        holder.tv_status.setText("Status: "+currentItem.getStatus());
        holder.tv_total.setText("Rp. " + String.format(Locale.US, "%,d", currentItem.getTotal()).replace(',', '.'));
    }

    @Override
    public int getItemCount() {
        return pemesananList.size();
    }

    public static class PemesananViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_kode_pemesanan;
        public TextView tv_tanggal;
        public TextView tv_total;
        public TextView tv_status;

        public PemesananViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tv_kode_pemesanan = itemView.findViewById(R.id.tv_kode_pemesanan);
            tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
            tv_total = itemView.findViewById(R.id.tv_total);
            tv_status = itemView.findViewById(R.id.tv_status);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
