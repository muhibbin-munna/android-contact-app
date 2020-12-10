package com.app.contactapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.app.contactapp.ContactDetailsActivity.user;

public class ParticularFragment extends Fragment {

    TextView nameTv, phoneTv, ageTv, addressLine1Tv, addressLine2Tv, cityTv, postCodeTv, remarkTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_particular, container, false);
        nameTv = view.findViewById(R.id.nameTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        addressLine1Tv = view.findViewById(R.id.addressLine1Tv);
        addressLine2Tv = view.findViewById(R.id.addressLine2Tv);
        ageTv = view.findViewById(R.id.ageTv);
        cityTv = view.findViewById(R.id.cityTv);
        postCodeTv = view.findViewById(R.id.postCodeTv);
        remarkTv = view.findViewById(R.id.remarkTv);
        if (user.getContactName() != null) {
            nameTv.setText(user.getContactName());
        }
        if (user.getContactNumber() != null) {
            phoneTv.setText(user.getContactNumber());
        }
        if (user.getStreet() != null) {
            addressLine1Tv.setText(user.getStreet());
        }
        if (user.getCity() != null) {
            cityTv.setText(user.getCity());
        }
        if (user.getPostCode() != null) {
            postCodeTv.setText(user.getPostCode());
        }

        return view;
    }

}