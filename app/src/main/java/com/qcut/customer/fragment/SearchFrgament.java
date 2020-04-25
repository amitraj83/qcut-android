package com.qcut.customer.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.qcut.customer.R;
import com.qcut.customer.activity.MainActivity;
import com.qcut.customer.adapter.BarberShopAdapter;
import com.qcut.customer.model.BarberShop;
import com.qcut.customer.utils.AppUtils;
import com.qcut.customer.utils.FireManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFrgament extends Fragment
        implements AdapterView.OnItemClickListener{

    private ListView lst_barberShop;

    MainActivity mainActivity;
    BarberShopAdapter adapter;
    List<BarberShop> listBarberShop = new ArrayList<>();

    public SearchFrgament(MainActivity activity) {
        mainActivity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initData();
//        View barberShopItem = inflater.inflate(R.layout.item_barbershop, container, false);
//        Button button = barberShopItem.findViewById(R.id.queue_button);
//        button.setOnClickListener(this);

        View view = inflater.inflate(R.layout.fragment_search_frgament, container, false);
        lst_barberShop = view.findViewById(R.id.lst_barberShop);
        adapter = new BarberShopAdapter(getContext(), listBarberShop);
        lst_barberShop.setAdapter(adapter);
        lst_barberShop.setOnItemClickListener(this);

        return view;
    }

    private void initData() {
        final ProgressDialog dialog = AppUtils.onShowProgressDialog(getActivity(), "Loading..", false);
        FireManager.getDataFromFirebase("shopDetails", new FireManager.getInfoCallback() {
            @Override
            public void onGetDataCallback(DataSnapshot snapshot) {
                AppUtils.onDismissProgressDialog(dialog);
                listBarberShop.clear();
                for (DataSnapshot postDict: snapshot.getChildren()) {
                    BarberShop item = new BarberShop();
                    item = postDict.getValue(BarberShop.class);

                    String destLocation = item.gmapLink;
                    double lat = Double.parseDouble(destLocation.split(",")[0]);
                    double lon = Double.parseDouble(destLocation.split(",")[1]);
                    LatLng p1 = new LatLng(AppUtils.gLat, AppUtils.gLon);
                    LatLng p2 = new LatLng(lat, lon);
                    item.distance = AppUtils.onCalculationByDistance(p1, p2);
//                    String distance = String.format("%.1f", + "Km";

                    listBarberShop.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void notFound() {
                AppUtils.onDismissProgressDialog(dialog);
                Toast.makeText(getContext(), "There is no data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.onGoPageViewFragment(listBarberShop.get(position));
    }
}
