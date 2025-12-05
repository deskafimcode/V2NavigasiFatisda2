package com.example.v2navigasifatisda;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LangkahAdapter extends RecyclerView.Adapter<LangkahAdapter.ViewHolder> {

    private List<Langkah> langkahList;
    private Context context;

    public LangkahAdapter(Context context, List<Langkah> langkahList) {
        this.context = context;
        this.langkahList = langkahList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teksLangkah;
        ImageView gambarLangkah;

        public ViewHolder(View view) {
            super(view);
            teksLangkah = view.findViewById(R.id.teksLangkah);
            gambarLangkah = view.findViewById(R.id.imageLangkah);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.langkah, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Langkah langkah = langkahList.get(position);
        holder.teksLangkah.setText(langkah.teks);
        holder.gambarLangkah.setImageResource(langkah.gambarResId);

        holder.gambarLangkah.setOnClickListener(v -> {
            Intent intent = new Intent(context, Zoom_Image.class);
            intent.putExtra("image_res_id", langkah.gambarResId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return langkahList.size();
    }
}
