package com.qcut.customer.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qcut.customer.R;
import com.qcut.customer.adapter.ServiceAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {

    private ListView lst_service;

    ServiceAdapter adapter;

    public ServiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        lst_service = view.findViewById(R.id.lst_service);
        adapter = new ServiceAdapter(getContext());
        lst_service.setAdapter(adapter);
        return view;
    }

}
