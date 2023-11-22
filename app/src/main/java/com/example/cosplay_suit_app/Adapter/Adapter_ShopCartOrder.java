package com.example.cosplay_suit_app.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cosplay_suit_app.API;
import com.example.cosplay_suit_app.DTO.CartOrderDTO;
import com.example.cosplay_suit_app.DTO.ShopCartorderDTO;
import com.example.cosplay_suit_app.Interface_retrofit.CartOrderInterface;
import com.example.cosplay_suit_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Adapter_ShopCartOrder extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static String url = API.URL;
    static final String BASE_URL = url +"/bill/";
    List<ShopCartorderDTO> list;
    Context context;
    private boolean isOrdersUpdated = false;
    private Map<String, List<CartOrderDTO>> orderMap; // Map lưu trữ danh sách đơn hàng theo idshop
    private List<CartOrderDTO> allOrders;
    AdapterCartorder arrayAdapter;
    TextView tvthanhtien;
    String TAG = "adaptershopcartorder";
    int thanhtien = 0;
    public Adapter_ShopCartOrder(List<ShopCartorderDTO> list, Context context) {
        this.list = list;
        this.context = context;
        orderMap = new HashMap<>();
        allOrders = new ArrayList<>();
        // Lấy danh sách đơn hàng của tất cả người dùng
        getOrdersByUserId("651fc5da0457764a7a047306", new Callback<List<CartOrderDTO>>() {
            @Override
            public void onResponse(Call<List<CartOrderDTO>> call, Response<List<CartOrderDTO>> response) {
                if (response.isSuccessful()) {
                    allOrders = response.body();
                    for (CartOrderDTO order : allOrders) {
                        handleOrders(order.getDtoSanPham().getId_shop(), order);
                    }
                    notifyDataSetChanged();
                } else {
                    // Xử lý lỗi (nếu có)
                    Log.e("Adapter_ShopCartOrder", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<CartOrderDTO>> call, Throwable t) {
                // Xử lý lỗi khi gọi API
                Log.e("Adapter_ShopCartOrder", "Error: " + t.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopcartorder, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShopCartorderDTO shop = list.get(position);
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.tvnameshop.setText(shop.getName_shop());
        List<CartOrderDTO> ordersForShop = orderMap.get(shop.getId());
        arrayAdapter = new AdapterCartorder(ordersForShop, context, (AdapterCartorder.OnclickCheck) context);
        viewHolder.rcvcart.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        if (ordersForShop != null) {
            for (CartOrderDTO order : ordersForShop) {
                Log.d(TAG, "Order ID: " + order.getTotalPayment()); // Thay "getId()" bằng getter tương ứng trong OrderDTO
            }
        } else {
            Log.d(TAG, "No orders found for shop with ID: " + shop.getId());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView tvnameshop;
        RecyclerView rcvcart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvnameshop = itemView.findViewById(R.id.tv_nameshop);
            rcvcart = itemView.findViewById(R.id.rcv_cart);
        }
    }
    public void getOrdersByUserId(String userId, Callback<List<CartOrderDTO>> callback) {
        // Tạo một OkHttpClient với interceptor để ghi log (nếu cần)
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        // Tạo Gson ConverterFactory để chuyển đổi JSON thành Java objects
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        // Tạo một API service sử dụng Retrofit
        CartOrderInterface service = retrofit.create(CartOrderInterface.class);

        // Gọi API để lấy danh sách đơn hàng dựa trên userId
        Call<List<CartOrderDTO>> call = service.getusercartorder(userId);
        call.enqueue(callback);
    }

    private void handleOrders(String idShop, CartOrderDTO order) {
        if (orderMap.containsKey(idShop)) {
            orderMap.get(idShop).add(order);
        } else {
            List<CartOrderDTO> orders = new ArrayList<>();
            orders.add(order);
            orderMap.put(idShop, orders);
        }
    }
}