package com.pemesananlapanganfutsal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pemesananlapanganfutsal.R;
import com.pemesananlapanganfutsal.model.Ulasan;

import java.util.ArrayList;

public class UlasanAdapter extends RecyclerView.Adapter<UlasanAdapter.UlasanViewHolder> {
    private ArrayList<Ulasan> ulasanList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    public UlasanAdapter(ArrayList<Ulasan> ulasanList) {
        this.ulasanList = ulasanList;
    }

    @NonNull
    @Override
    public UlasanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ulasan_item, parent, false);
        UlasanViewHolder ulasanViewHolder = new UlasanViewHolder(view, listener);
        return ulasanViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UlasanViewHolder holder, int position) {
        Ulasan currentItem = ulasanList.get(position);
        holder.tv_nama_pemesan.setText(currentItem.getNamaPemesan());
        holder.tv_tanggal.setText(currentItem.getTanggal());
        holder.tv_ulasan.setText(currentItem.getUlasan());
        holder.rating.setRating(Float.parseFloat(currentItem.getRating()));
    }

    @Override
    public int getItemCount() {
        return ulasanList.size();
    }

    public static class UlasanViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nama_pemesan;
        public TextView tv_tanggal;
        public TextView tv_ulasan;
        public RatingBar rating;

        public UlasanViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tv_nama_pemesan = itemView.findViewById(R.id.tv_nama_pemesan);
            tv_tanggal = itemView.findViewById(R.id.tv_tanggal);
            tv_ulasan = itemView.findViewById(R.id.tv_ulasan);
            rating = itemView.findViewById(R.id.rating);

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
