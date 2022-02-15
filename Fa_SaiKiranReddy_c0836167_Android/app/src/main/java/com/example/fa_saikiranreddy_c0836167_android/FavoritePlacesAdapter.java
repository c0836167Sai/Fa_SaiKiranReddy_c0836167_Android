package com.example.fa_saikiranreddy_c0836167_android;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoritePlacesAdapter extends RecyclerView.Adapter<FavoritePlacesAdapter.MyViewHolder> {
    private Context mContext;
    private List<FavoritePlace> favoritePlaces;

    FavoritePlacesAdapter(Context context) {
        mContext = context;
        favoritePlaces = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FavoritePlace favoritePlace = favoritePlaces.get(position);
        holder.tvTitle.setText(favoritePlace.getAddress());
        holder.tvDate.setText(favoritePlace.getDate());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.rowFG.setBackgroundColor(favoritePlace.getCategory().equalsIgnoreCase("Visited") ?
                    mContext.getColor(R.color.teal_200) : mContext.getColor(R.color.yellow));
        }
    }

    @Override
    public int getItemCount() {
        return favoritePlaces.size();
    }

    public void setFavoritePlaces(List<FavoritePlace> favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDate;
        private CardView rowFG;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_loc_title);
            tvDate = itemView.findViewById(R.id.tv_saved_date);
            rowFG = itemView.findViewById(R.id.rowFG);
        }
    }
}