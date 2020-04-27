package com.qcut.customer.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.qcut.customer.R;
import com.qcut.customer.activity.MainActivity;
import com.qcut.customer.adapter.BarberShopAdapter;
import com.qcut.customer.model.BarberService;
import com.qcut.customer.model.BarberShop;
import com.qcut.customer.utils.AppUtils;
import com.qcut.customer.utils.FireManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

                    listBarberShop.add(item);
                }
                Collections.sort(listBarberShop, new BarberComparator());
                adapter.notifyDataSetChanged();

                FireManager.getDataFromFirebase("servicesAvailable", new FireManager.getInfoCallback() {
                    @Override
                    public void onGetDataCallback(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (BarberShop barberShop : listBarberShop) {
                                if (snapshot.child(barberShop.key).exists()) {
                                    List<BarberService> services = new ArrayList<>();
                                    Iterator<DataSnapshot> iterator = snapshot.child(barberShop.key).getChildren().iterator();
                                    while (iterator.hasNext()) {
                                        DataSnapshot serviceKey = iterator.next();
                                        String serviceName = String.valueOf(serviceKey.child("serviceName").getValue());
                                        String servicePrice = String.valueOf(serviceKey.child("servicePrice").getValue());
                                        services.add(new BarberService(serviceName, servicePrice));
                                    }
                                    barberShop.services = services;
                                }
                            }
                        }
                    }

                    @Override
                    public void notFound() {

                    }
                });

            }

            @Override
            public void notFound() {
                AppUtils.onDismissProgressDialog(dialog);
                Toast.makeText(getContext(), "There is no data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class BarberComparator implements Comparator<BarberShop> {

        @Override
        public int compare(BarberShop o1, BarberShop o2) {
            if (o1.distance < o2.distance) {
                return -1;
            } else if (o1.distance == o2.distance) {
                return 0;
            } else {
                return 1;
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BarberShop barberShop = listBarberShop.get(position);
        boolean isLoggedIn = AppUtils.preferences.getBoolean(AppUtils.IS_LOGGED_IN, false);
        if (isLoggedIn) {
            final String customerKey = AppUtils.preferences.getString(AppUtils.USER_ID, null);
            FireManager.getDataFromFirebase(FireManager.RootNames.BARBER_WAITING_QUEUES + "/" + barberShop.key,
                    new FireManager.getInfoCallback() {
                        @Override
                        public void onGetDataCallback(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                boolean customerFoundInQueue = false;
                                Iterator<DataSnapshot> barberIT = snapshot.getChildren().iterator();
                                while (barberIT.hasNext()) {
                                    DataSnapshot aBarber = barberIT.next();
                                    if (aBarber.child(customerKey).exists()) {
                                        customerFoundInQueue = true;
                                        break;
                                    }
                                }
                                if (customerFoundInQueue) {
                                    mainActivity.bottomNavigationView.setSelectedItemId(R.id.action_queue);
                                }
                            } else {
                                mainActivity.onGoPageViewFragment(barberShop);
                            }
                        }

                        @Override
                        public void notFound() {
                            mainActivity.onGoPageViewFragment(barberShop);
                        }
                    });
        } else {
            mainActivity.onGoPageViewFragment(barberShop);
        }
    }
}
