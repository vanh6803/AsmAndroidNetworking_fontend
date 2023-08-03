package com.example.assignment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment.R;
import com.example.assignment.models.HackNasa;

import java.util.List;

public class AdapterListDataFromNasa extends RecyclerView.Adapter<AdapterListDataFromNasa.DataFormNasaViewHolder> {
    private Context context;
    private List<HackNasa> list;

    public AdapterListDataFromNasa(Context context) {
        this.context = context;
    }

    public void setData(List<HackNasa> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DataFormNasaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataFormNasaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DataFormNasaViewHolder holder, int position) {
        HackNasa obj = list.get(position);
        if (obj == null){
            return;
        }

        Glide.with(context).load(obj.getUrl()).error(R.drawable.baseline_error_24).into(holder.img);
        holder.tvTitle.setText(obj.getTitle());
        holder.tvContent.setEllipsize(TextUtils.TruncateAt.END);
        holder.tvContent.setText(obj.getExplanation());
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class DataFormNasaViewHolder extends RecyclerView.ViewHolder{

        private ImageView img;
        private TextView tvTitle, tvContent;
        private CardView itemSelected;

        public DataFormNasaViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            itemSelected = itemView.findViewById(R.id.layout_selected);
        }
    }
}
