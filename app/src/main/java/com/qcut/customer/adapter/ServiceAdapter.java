package com.qcut.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qcut.customer.R;

public class ServiceAdapter extends BaseAdapter {

    Context mContext;
    public ServiceAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 10;
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
        Context context;
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_service, null);
        return convertView;
    }
}
