package com.qcut.customer.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.qcut.customer.R;
import com.qcut.customer.activity.MainActivity;
import com.qcut.customer.model.BarberShop;
import com.qcut.customer.utils.AppUtils;
import com.qcut.customer.utils.BarberStatus;
import com.qcut.customer.utils.FireManager;
import com.qcut.customer.utils.TimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment implements View.OnClickListener {

    MainActivity mainActivity;

    private RelativeLayout rlt_unqueue;
    private LinearLayout llt_queue, llt_leave_queue, llt_select_barber;
    private BarberShop barberShop;
    private TextView shopName, addressLine1, addressLine2;
    private TextView distance, likes, status;
    private TextView customerName, waitingTime;

    public QueueFragment(MainActivity activity, BarberShop barberShop) {
        mainActivity = activity;
        this.barberShop = barberShop;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        shopName = view.findViewById(R.id.shop_name);
        addressLine1 = view.findViewById(R.id.txt_address1);
        addressLine2 = view.findViewById(R.id.txt_address2);
        distance = view.findViewById(R.id.txt_distance);
        likes = view.findViewById(R.id.txt_likes);
        status = view.findViewById(R.id.txt_status);
        customerName = view.findViewById(R.id.queue_view_customer_name);
        waitingTime = view.findViewById(R.id.queue_view_waiting_time);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        rlt_unqueue = view.findViewById(R.id.rlt_unqueu_view);
        llt_queue = view.findViewById(R.id.llt_queue_view);
        llt_leave_queue = view.findViewById(R.id.llt_leave_queue);
        llt_select_barber = view.findViewById(R.id.llt_select_barber);

        if (AppUtils.preferences.getBoolean(AppUtils.IS_QUEUED, false)) {

            final String shopKey = AppUtils.preferences.getString(AppUtils.QUEUED_SHOP_KEY, null);
            if (!StringUtils.isEmpty(shopKey)) {

                FireManager.getDataFromFirebase("shopDetails/" + shopKey, new FireManager.getInfoCallback() {
                    @Override
                    public void onGetDataCallback(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            rlt_unqueue.setVisibility(View.GONE);
                            llt_queue.setVisibility(View.VISIBLE);
                            shopName.setText(String.valueOf(snapshot.child("shopName").getValue()));
                            addressLine1.setText(String.valueOf(snapshot.child("addressLine1").getValue()));
                            addressLine2.setText(String.valueOf(snapshot.child("addressLine2").getValue()) + ", " +
                                    String.valueOf(snapshot.child("city").getValue()));

                            String key = String.valueOf(snapshot.child("key").getValue());
                            String destLocation = String.valueOf(snapshot.child("gmapLink").getValue());
                            if (!StringUtils.isEmpty(destLocation)
                                    && StringUtils.contains(destLocation, ",")) {
                                double lat = Double.parseDouble(destLocation.split(",")[0]);
                                double lon = Double.parseDouble(destLocation.split(",")[1]);
                                LatLng p1 = new LatLng(AppUtils.gLat, AppUtils.gLon);
                                LatLng p2 = new LatLng(lat, lon);
                                distance.setText(String.format("%.1f", AppUtils.onCalculationByDistance(p1, p2)) + "Km");
                            }

                            likes.setText("21 likes");
                            Drawable distanceIcon = mainActivity.getResources().getDrawable(R.drawable.ic_location_white);
                            distanceIcon.setBounds(0, 0, 60, 60);
                            distance.setCompoundDrawables(distanceIcon, null, null, null);


                            Drawable likesIcon = mainActivity.getResources().getDrawable(R.drawable.ic_favorite_white);
                            likesIcon.setBounds(0, 0, 60, 60);
                            likes.setCompoundDrawables(likesIcon, null, null, null);

                            final String customerDisplayName = AppUtils.preferences.getString(AppUtils.USER_DISPLAY_NAME, null);
                            if (!StringUtils.isEmpty(customerDisplayName)) {
                                customerName.setText(customerDisplayName);
                            }

                            Drawable statusIcon = mainActivity.getResources().getDrawable(R.drawable.circle_grey);
                            statusIcon.setBounds(0, 0, 30, 30);
                            status.setCompoundDrawables(statusIcon, null, null, null);


                        final String customerKey = AppUtils.preferences.getString(AppUtils.USER_ID, null);
                            if (!StringUtils.isEmpty(customerKey)) {

                                FireManager.mainRef.child("barberWaitingQueues/" + shopKey + "_" + TimeUtil.getTodayDDMMYYYY())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                    status.setText(BarberStatus.ONLINE.name());
                                                    Drawable statusIcon = mainActivity.getResources().getDrawable(R.drawable.circle_green);
                                                    statusIcon.setBounds(0, 0, 30, 30);
                                                    status.setCompoundDrawables(statusIcon, null, null, null);


                                                    Iterator<DataSnapshot> barberIT = dataSnapshot.getChildren().iterator();
                                                    while (barberIT.hasNext()) {
                                                        DataSnapshot aBarber = barberIT.next();
                                                        if (aBarber.child(customerKey).exists()) {
                                                            String expectedWaitingTimeStr = String.valueOf(aBarber.child(customerKey).child("expectedWaitingTime").getValue());
                                                            if (!StringUtils.isEmpty(expectedWaitingTimeStr) && StringUtils.isNumeric(expectedWaitingTimeStr)) {
                                                                long expectedWaitingTime = Long.valueOf(expectedWaitingTimeStr);
                                                                if (expectedWaitingTime == 0) {
                                                                    waitingTime.setText("Ready");
                                                                } else {
                                                                    String displayWaitingTime = TimeUtil.getDisplayWaitingTime(expectedWaitingTime);
                                                                    waitingTime.setText(displayWaitingTime);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void notFound() {

                    }
                });


            }

        } else {
            rlt_unqueue.setVisibility(View.VISIBLE);
            llt_queue.setVisibility(View.GONE);
        }


        llt_leave_queue.setOnClickListener(this);
        llt_select_barber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llt_select_barber:
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.action_search);
                /*DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                final Dialog dialog = new Dialog(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_barber, null);
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle(null);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(width * 6 / 7, ViewGroup.LayoutParams.WRAP_CONTENT);

                StringChooser stringChooser = dialog.findViewById(R.id.stringChooser);

                List<String> strings = new ArrayList<>();
                strings.add("John");
                strings.add("Merry");
                strings.add("Berk");

                stringChooser.setStrings(strings);


                LinearLayout llt_join_queue = dialog.findViewById(R.id.llt_join_dialog);
                llt_join_queue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        AppUtils.isQueued = true;
                        rlt_unqueue.setVisibility(View.GONE);
                        llt_queue.setVisibility(View.VISIBLE);
                    }
                });
                dialog.show();*/
                break;
            case R.id.llt_leave_queue:

                final String customerId = AppUtils.preferences.getString(AppUtils.USER_ID, null);
                String shopKey = AppUtils.preferences.getString(AppUtils.QUEUED_SHOP_KEY, null);
                if (!StringUtils.isEmpty(shopKey)) {
                    FireManager.getDataFromFirebase("barberWaitingQueues/" + shopKey + "_" + TimeUtil.getTodayDDMMYYYY(), new FireManager.getInfoCallback() {
                        @Override
                        public void onGetDataCallback(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Iterator<DataSnapshot> barberIT = snapshot.getChildren().iterator();
                                while (barberIT.hasNext()) {
                                    DataSnapshot aBarber = barberIT.next();
                                    if (aBarber.child(customerId).exists()) {
                                        Task<Void> voidTask = aBarber.child(customerId).getRef().removeValue();
                                        voidTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                AppUtils.preferences.edit().putBoolean(AppUtils.IS_QUEUED, false).apply();
                                                AppUtils.preferences.edit().putString(AppUtils.QUEUED_SHOP_KEY, "").apply();
                                                mainActivity.bottomNavigationView.setSelectedItemId(R.id.action_search);
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void notFound() {

                        }
                    });

                }



                break;
        }
    }
}
