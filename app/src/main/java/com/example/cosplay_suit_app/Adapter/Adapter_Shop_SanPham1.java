package com.example.cosplay_suit_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cosplay_suit_app.API;
import com.example.cosplay_suit_app.Activity.Chitietsanpham;
import com.example.cosplay_suit_app.DTO.DTO_SanPham;
import com.example.cosplay_suit_app.DTO.Favorite;
import com.example.cosplay_suit_app.DTO.ItemImageDTO;
import com.example.cosplay_suit_app.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class Adapter_Shop_SanPham1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static String url = API.URL;
    static final String BASE_URL = url + "/user/api/";
    private List<Favorite> favoriteList = new ArrayList<>();
    boolean isMyFavorite = false;
    String id;

    private List<DTO_SanPham> mlist;
    Context context;

    public Adapter_Shop_SanPham1( Context context) {

        this.context = context;
    }
    public void updateData(List<DTO_SanPham> mlist){
        this.mlist = mlist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sanpham_shop1, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DTO_SanPham sanPham = mlist.get(position);

        Adapter_Shop_SanPham1.ItemViewHolder viewHolder = (Adapter_Shop_SanPham1.ItemViewHolder) holder;
        viewHolder.tv_nameSp.setText(sanPham.getNameproduct());
        viewHolder.tv_gia.setText(String.valueOf(sanPham.getPrice()+"đ"));
        viewHolder.tv_gia_gachchan.setText(sanPham.getPrice()*2 +"đ");
        viewHolder.tv_gia_gachchan.setPaintFlags(viewHolder.tv_gia_gachchan.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (sanPham.getListImage() != null && !sanPham.getListImage().isEmpty()) {
            ItemImageDTO firstImage = sanPham.getListImage().get(0);
            String imageUrl = firstImage.getImage();

            // Tiến hành tải và hiển thị ảnh từ URL bằng Glide
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.image)
                    .placeholder(R.drawable.image)
                    .centerCrop()
                    .into(viewHolder.anh_sp);
        }

        viewHolder.ll_chitietsp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Chitietsanpham.class);
                intent.putExtra("id_product", sanPham.getId());
                intent.putExtra("name", sanPham.getNameproduct());
                intent.putExtra("price", sanPham.getPrice());
                intent.putExtra("about", sanPham.getDescription());
                intent.putExtra("slkho", sanPham.getAmount());
                intent.putExtra("id_shop",sanPham.getId_shop());
                intent.putExtra("time_product",sanPham.getTime_product());
                intent.putExtra("id_category",sanPham.getId_category());
                // Chuyển danh sách thành JSON
                String listImageJson = new Gson().toJson(sanPham.getListImage());
                // Đặt chuỗi JSON vào Intent
                intent.putExtra("listImage", listImageJson);

                String listsizeJson = new Gson().toJson(sanPham.getListProp());
                intent.putExtra("listsize", listsizeJson);
                Log.d("check", "onClick: " + listsizeJson);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_nameSp,tv_gia,tv_soluong,tv_gia_gachchan,id_slsp_da_ban;
        ImageView anh_sp, img_favorite;
        FrameLayout ll_chitietsp;


        public ItemViewHolder(View view) {
            super(view);

            tv_nameSp = view.findViewById(R.id.tv_nameSp);
            anh_sp = view.findViewById(R.id.anh_sp);
            ll_chitietsp = view.findViewById(R.id.id_chitietsp);
            tv_gia = view.findViewById(R.id.tv_gia);
            tv_gia_gachchan = view.findViewById(R.id.tv_gia_gachchan);

        }

    }

}
