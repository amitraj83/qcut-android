package com.qcut.customer.fragment;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qcut.customer.R;
import com.qcut.customer.activity.MainActivity;
import com.qcut.customer.utils.AppUtils;
import com.volobot.stringchooser.StringChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment implements View.OnClickListener {

    MainActivity mainActivity;

    private RelativeLayout rlt_unqueue;
    private LinearLayout llt_queue, llt_leave_queue, llt_select_barber;

    public QueueFragment(MainActivity activity) {
        mainActivity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        initUIView(view);
        return view;
    }

    private void initUIView(View view) {
        rlt_unqueue = view.findViewById(R.id.rlt_unqueu_view);
        llt_queue = view.findViewById(R.id.llt_queue_view);
        llt_leave_queue = view.findViewById(R.id.llt_leave_queue);
        llt_select_barber = view.findViewById(R.id.llt_select_barber);

        if (AppUtils.isQueued) {
            rlt_unqueue.setVisibility(View.GONE);
            llt_queue.setVisibility(View.VISIBLE);
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
                        rlt_unqueue.setVisibility(View.GONE);
                        llt_queue.setVisibility(View.VISIBLE);
                    }
                });
                dialog.show();
                break;
            case R.id.llt_leave_queue:
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.action_search);
                break;
        }
    }
}