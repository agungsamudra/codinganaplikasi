package com.pemesananlapanganfutsal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.model.Lapangan;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class LapanganAdapter extends RecyclerView.Adapter<LapanganAdapter.LapanganViewHolder> {
    private ArrayList<Lapangan> lapanganList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    public LapanganAdapter(ArrayList<Lapangan> lapanganList) {
        this.lapanganList = lapanganList;
    }

    @NonNull
    @Override
    public LapanganViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lapangan_item, parent, false);
        LapanganViewHolder lapanganViewHolder = new LapanganViewHolder(view, listener);
        return lapanganViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LapanganViewHolder holder, int position) {
        Lapangan currentItem = lapanganList.get(position);
        Picasso.get().load(currentItem.getImgUrl()).into(holder.img_lapangan);
        holder.tv_harga.setText("Rp. " + String.format(Locale.US, "%,d", currentItem.getHarga()).replace(',', '.') + "/jam");
        holder.tv_nama_lapangan.setText(currentItem.getNamaLapangan());
    }

    @Override
    public int getItemCount() {
        return lapanganList.size();
    }

    public static class LapanganViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_lapangan;
        public TextView tv_nama_lapangan;
        public TextView tv_harga;

        public LapanganViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            img_lapangan = itemView.findViewById(R.id.img_lapangan);
            tv_nama_lapangan = itemView.findViewById(R.id.tv_nama_lapangan);
            tv_harga = itemView.findViewById(R.id.tv_harga);

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
