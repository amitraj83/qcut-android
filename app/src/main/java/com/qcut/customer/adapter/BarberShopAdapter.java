package com.qcut.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.qcut.customer.R;
import com.qcut.customer.model.BarberShop;
import com.qcut.customer.utils.AppUtils;

import java.util.List;

public class BarberShopAdapter extends BaseAdapter {
    Context mContext;
    List<BarberShop> mListBarberShop;

    public BarberShopAdapter(Context context, List<BarberShop> list) {
        mContext = context;
        mListBarberShop = list;
    }

    @Override
    public int getCount() {
        return mListBarberShop.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barbershop, null);
        TextView txtDistance = convertView.findViewById(R.id.txt_distance);
        TextView txtAddress1 = convertView.findViewById(R.id.txt_address1);
        TextView txtAddress2 = convertView.findViewById(R.id.txt_address2);

        txtAddress1.setText(mListBarberShop.get(position).addressLine1);
        txtAddress2.setText(mListBarberShop.get(position).addressLine2);

        String destLocation = mListBarberShop.get(position).gmapLink;
        double lat = Double.parseDouble(destLocation.split(",")[0]);
        double lon = Double.parseDouble(destLocation.split(",")[1]);
        LatLng p1 = new LatLng(AppUtils.gLat, AppUtils.gLon);
        LatLng p2 = new LatLng(lat, lon);
        String distance = String.format("%.1f",AppUtils.onCalculationByDistance(p1, p2)) + "Km";
        txtDistance.setText(distance);
        return convertView;
    }
}
