package com.qcut.customer.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.qcut.customer.model.BarberService;
import com.qcut.customer.model.BarberShop;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FireManager {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final DatabaseReference mainRef = database.getReference();

    public interface RootNames {
        String BARBER_WAITING_QUEUES = "barberWaitingQueues";
        String CUSTOMERS = "Customers";
        String BARBERS = "barbers";
        String SHOP_DETAILS = "shopDetails";
    }

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

    public static void saveDataToFirebase(final Map<String, Object> params, String url, final saveObjectCallback callback) {
        mainRef.child(url).setValue(params).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSetDataCallback(params);
            }
        });
    }

    public static void updateDataToFirebase(final Map<String, Object> params, String url, final updateInfoCallback callback) {
        mainRef.child(url).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSetDataCallback(params);
            }
        });
    }

    public interface saveInfoCallback {
        void onSetDataCallback(Map<String, String> params);
    }

    public interface saveObjectCallback {
        void onSetDataCallback(Map<String, Object> params);
    }

    public interface updateInfoCallback {
        void onSetDataCallback(Map<String, Object> params);
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

    public static void queryDataFromFirebase(String url, String key, String value,
                                             final getInfoCallback callback) {
        Query query = mainRef.child(url).orderByChild(key).equalTo(value);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void getShopDetails(String shopKey, final ShopInfoCallBack callBack) {
        mainRef.child(RootNames.SHOP_DETAILS+"/"+shopKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final BarberShop barberShop = dataSnapshot.getValue(BarberShop.class);

                    FireManager.getDataFromFirebase("servicesAvailable/"+barberShop.key, new FireManager.getInfoCallback() {
                        @Override
                        public void onGetDataCallback(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                List<BarberService> services = new ArrayList<>();
                                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                                while (iterator.hasNext()) {
                                    DataSnapshot serviceKey = iterator.next();
                                    String serviceName = String.valueOf(serviceKey.child("serviceName").getValue());
                                    String servicePrice = String.valueOf(serviceKey.child("servicePrice").getValue());
                                    services.add(new BarberService(serviceName, servicePrice));
                                }
                                barberShop.services = services;
                            }
                            callBack.onGetShopDetails(barberShop);
                        }

                        @Override
                        public void notFound() {
                            callBack.onGetShopDetails(barberShop);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack.notFound();
            }
        });
    }
    public interface ShopInfoCallBack {
        void onGetShopDetails(BarberShop snapshot);
        void notFound();
    }

    public static void userQueueStatusData(final getInfoCallback callback) {
        String shopKey = AppUtils.preferences.getString(AppUtils.QUEUED_SHOP_KEY, null);
        final String userId = AppUtils.preferences.getString(AppUtils.USER_ID, null);
        if (!StringUtils.isEmpty(shopKey) && !StringUtils.isEmpty(userId)) {
            mainRef.child(RootNames.BARBER_WAITING_QUEUES+"/"+shopKey+"_"+TimeUtil.getTodayDDMMYYYY()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Iterator<DataSnapshot> barbers = dataSnapshot.getChildren().iterator();
                        while (barbers.hasNext()) {
                            DataSnapshot barber = barbers.next();
                            if (barber.child(userId).exists()) {
                                callback.onGetDataCallback(barber.child(userId));
                                break;
                            }
                        }
                    } else {
                        callback.notFound();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.notFound();
                }
            });
        } else {
            callback.notFound();
        }
    }

}
