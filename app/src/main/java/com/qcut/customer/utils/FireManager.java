package com.qcut.customer.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONObject;

import java.util.Map;

public class FireManager {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final DatabaseReference mainRef = database.getReference();

    public static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String id = currentUser.getUid();
            return id;
        }


        return null;
    }

    public static void saveDataToFirebase(final Map<String, String> params, String url, final saveInfoCallback callback) {
        mainRef.child(url).setValue(params).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSetDataCallback(params);
            }
        });
    }

    public interface saveInfoCallback {
        void onSetDataCallback(Map<String, String> params);
    }

    public static void getDataFromFirebase(String url, final getInfoCallback callback) {
        mainRef.child(url).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onGetDataCallback(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.notFound();
            }
        });
    }

    public interface getInfoCallback {
        void onGetDataCallback(DataSnapshot snapshot);
        void notFound();
    }
}
