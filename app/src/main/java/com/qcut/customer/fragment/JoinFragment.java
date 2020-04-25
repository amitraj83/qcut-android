package com.qcut.customer.fragment;


import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.qcut.customer.R;
import com.qcut.customer.activity.MainActivity;
import com.qcut.customer.adapter.TabAdapter;
import com.qcut.customer.model.BarberShop;
import com.qcut.customer.utils.AppUtils;
import com.volobot.stringchooser.StringChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    MainActivity mainActivity;

    TabAdapter pageAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout llt_join_btn1, llt_join_btn;
    BarberShop barberShop;
    private TextView shopName, shopAddressLine1, shopAddressLine2;
    private TextView distance, likes, status;

    public JoinFragment(MainActivity activity, BarberShop barberShop) {
        mainActivity = activity;
        this.barberShop = barberShop;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join, container, false);
        initUIView(view);
        initData(barberShop);
        return view;
    }

    private void initData(BarberShop barberShop) {
        shopName.setText(barberShop.shopName);
        shopAddressLine1.setText(barberShop.addressLine1);
        shopAddressLine2.setText(barberShop.addressLine2);

        distance.setText(String.format("%.1f",barberShop.distance) + "Km");
        likes.setText("21 likes");
        status.setText(barberShop.status);


    }

    private void initUIView(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        llt_join_btn = view.findViewById(R.id.llt_join_btn);
        llt_join_btn1 = view.findViewById(R.id.llt_join_btn1);

        shopName = view.findViewById(R.id.shop_name);
        shopAddressLine1 = view.findViewById(R.id.txt_address1);
        shopAddressLine2 = view.findViewById(R.id.txt_address2);

        distance = view.findViewById(R.id.txt_distance);
        likes = view.findViewById(R.id.txt_likes);
        status = view.findViewById(R.id.txt_status);

        Drawable distanceIcon = mainActivity.getResources().getDrawable(R.drawable.ic_location_white);
        distanceIcon.setBounds(0, 0, 60, 60);
        distance.setCompoundDrawables(distanceIcon, null, null, null);


        Drawable likesIcon = mainActivity.getResources().getDrawable(R.drawable.ic_favorite_white);
        likesIcon.setBounds(0, 0, 60, 60);
        likes.setCompoundDrawables(likesIcon, null, null, null);

        Drawable statusIcon = mainActivity.getResources().getDrawable(R.drawable.circle_grey);
        if (barberShop.status.equalsIgnoreCase("ONLINE")) {
            statusIcon = mainActivity.getResources().getDrawable(R.drawable.circle_green);
        }
        statusIcon.setBounds(0, 0, 30, 30);
        status.setCompoundDrawables(statusIcon, null, null, null);

//        Drawable statusIcon = mainActivity.getResources().getDrawable(R.drawable.circle_grey);
//        statusIcon.setBounds(0, 0, 60, 60);
//        if (barberShop.status.equalsIgnoreCase("ONLINE")) {
//            statusIcon = mainActivity.getResources().getDrawable(R.drawable.circle_green);
//        }
//        status.setCompoundDrawables(statusIcon, null, null, null);


        pageAdapter = new TabAdapter(getFragmentManager());
        pageAdapter.addFragment(new ServiceFragment(), "SERVICES");
        pageAdapter.addFragment(new HoursFragment(), "HOURS");
        pageAdapter.addFragment(new DetailFragment(), "DETAILS");

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(this);

        llt_join_btn1.setOnClickListener(this);
        llt_join_btn.setOnClickListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            llt_join_btn1.setVisibility(View.VISIBLE);
            llt_join_btn.setVisibility(View.GONE);
        } else {
            llt_join_btn1.setVisibility(View.GONE);
            llt_join_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llt_join_btn:
                onShowBarberSelect();
                break;
            case R.id.llt_join_btn1:
                onShowBarberSelect();
                break;
        }
    }

    private void onShowBarberSelect() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
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
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.action_queue);
                mainActivity.onGoQueueFrg();
            }
        });
        dialog.show();
    }
}
