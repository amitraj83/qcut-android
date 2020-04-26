package com.qcut.customer.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.qcut.customer.R;
import com.qcut.customer.activity.LoginActivity;
import com.qcut.customer.activity.MainActivity;
import com.qcut.customer.utils.AppUtils;
import com.qcut.customer.utils.SharedPrefManager;
import com.volobot.stringchooser.StringChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    MainActivity mainActivity;

    Boolean isShowEdit = false;
    int editSelectIndex = 0;

    private LinearLayout llt_profile_view, llt_profile_edit, llt_logout, llt_profile_name, llt_profile_password, llt_cancel, llt_save;
    private ImageView imgEditName, imgEditPassword, imgEditLocation;
    private TextView txtTitle;
    private StringChooser stringChooser;

    private GoogleSignInClient mGoogleSignInClient;

    public ProfileFragment(MainActivity activity) {
        mainActivity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        llt_profile_view = view.findViewById(R.id.llt_profile_view);
        llt_profile_edit = view.findViewById(R.id.llt_edit_profile);
        llt_logout = view.findViewById(R.id.llt_logout);
        llt_profile_name = view.findViewById(R.id.llt_edit_name);
        llt_profile_password = view.findViewById(R.id.llt_edit_password);
        llt_cancel = view.findViewById(R.id.llt_cacel);
        llt_save = view.findViewById(R.id.llt_save);

        imgEditName = view.findViewById(R.id.img_edit_name);
        imgEditPassword = view.findViewById(R.id.img_edit_password);
        imgEditLocation = view.findViewById(R.id.img_edit_location);

        txtTitle = view.findViewById(R.id.txt_title);

        stringChooser = view.findViewById(R.id.location_string_choose);

        List<String> strings = new ArrayList<>();
        strings.add("Doublin 11");
        strings.add("Doublin 12");
        strings.add("Doublin 13");
        strings.add("Doublin 14");
        strings.add("Doublin 15");

        stringChooser.setStrings(strings);

        initUIView();
        initUIEvent();
        return view;
    }

    private void initUIView() {
        if (!isShowEdit) {
            llt_profile_view.setVisibility(View.VISIBLE);
            llt_profile_edit.setVisibility(View.GONE);
            mainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            llt_profile_view.setVisibility(View.GONE);
            llt_profile_edit.setVisibility(View.VISIBLE);
            if (editSelectIndex == 0) {
                txtTitle.setText("Name");
                llt_profile_name.setVisibility(View.VISIBLE);
                llt_profile_password.setVisibility(View.GONE);
                stringChooser.setVisibility(View.GONE);
            } else if (editSelectIndex == 1) {
                txtTitle.setText("Password");
                llt_profile_name.setVisibility(View.GONE);
                llt_profile_password.setVisibility(View.VISIBLE);
                stringChooser.setVisibility(View.GONE);
            } else if (editSelectIndex == 2){
                txtTitle.setText("Location");
                llt_profile_name.setVisibility(View.GONE);
                llt_profile_password.setVisibility(View.GONE);
                stringChooser.setVisibility(View.VISIBLE);
            }
            mainActivity.bottomNavigationView.setVisibility(View.GONE);
        }

    }

    private void initUIEvent() {
        imgEditName.setOnClickListener(this);
        imgEditPassword.setOnClickListener(this);
        imgEditLocation.setOnClickListener(this);
        llt_logout.setOnClickListener(this);
        llt_cancel.setOnClickListener(this);
        llt_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.llt_save || id == R.id.llt_cacel) {
            isShowEdit = false;
            initUIView();
        } else if (id == R.id.img_edit_name) {
            isShowEdit = true;
            editSelectIndex = 0;
            initUIView();
        } else if (id == R.id.img_edit_password) {
            isShowEdit = true;
            editSelectIndex = 1;
            initUIView();
        } else if (id == R.id.img_edit_location) {
            isShowEdit = true;
            editSelectIndex = 2;
            initUIView();
        } else {
            FirebaseAuth.getInstance().signOut();
//            SharedPrefManager sharedPrefManager = new SharedPrefManager(mainActivity);
//            if (sharedPrefManager.getStringSharedPref("type").equals("google")){
//                mGoogleSignInClient.signOut().addOnCompleteListener(mainActivity,
//                        new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                mainActivity.onGoLoginActivity();
//                            }
//                        });
//            } else if (sharedPrefManager.getStringSharedPref("type").equals("facebook")) {
//                LoginManager.getInstance().logOut();
//                mainActivity.onGoLoginActivity();
//            }
            AppUtils.preferences.edit().putBoolean(AppUtils.IS_LOGGED_IN, false).apply();
            AppUtils.preferences.edit().putString(AppUtils.USER_ID, null).apply();
            AppUtils.preferences.edit().putString(AppUtils.USER_DISPLAY_NAME, null).apply();
            AppUtils.preferences.edit().putString(AppUtils.USER_EMAIL, null).apply();
            mainActivity.onGoLoginActivity();
        }
    }
}
