package com.app.contactapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.github.tamir7.contacts.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ContactListFragment extends Fragment implements AllContactsAdapter.OnItemClickListener {
    RecyclerView rvContacts;
    List<MyContact> userList = new ArrayList<>();
    List<MyContact> searchList = new ArrayList<>();
    androidx.appcompat.widget.SearchView searchView;
    String TAG = "ContactListFragment";
    TextView no_of_con;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        rvContacts = view.findViewById(R.id.contactRV);
        searchView = view.findViewById(R.id.search);
        no_of_con = getActivity().findViewById(R.id.no_of_contact);

        loadAllContact();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    searchList.clear();
                    searchList.addAll(userList);
                    loadRV();

                } else {
                    searchList.clear();
                    Contacts.initialize(getContext());
                    Query mainQuery = Contacts.getQuery();
                    Query q1 = Contacts.getQuery();
                    q1.whereStartsWith(Contact.Field.DisplayName, newText);
                    Query q2 = Contacts.getQuery();
                    q2.whereContains(Contact.Field.PhoneNumber, newText);
                    List<Query> qs = new ArrayList<>();
                    qs.add(q1);
                    qs.add(q2);
                    mainQuery.or(qs);
                    List<Contact> contacts = mainQuery.find();
                    for (int i = 0; i < contacts.size(); i++) {
                        MyContact user = new MyContact();
                        long id = contacts.get(i).getId();
                        String givenName = contacts.get(i).getGivenName();
                        String familyName = contacts.get(i).getFamilyName();
                        List<PhoneNumber> number = contacts.get(i).getPhoneNumbers();
                        for (int j = 0; j < number.size(); j++) {
                            String phoneNumber = number.get(j).getNumber();
                            Log.d(TAG, "onCreate: Number" + phoneNumber);
                            user.setContactNumber(phoneNumber);
                        }
                        String photo = contacts.get(i).getPhotoUri();
                        List<Address> address = contacts.get(i).getAddresses();
                        if (address.size() != 0) {
                            for (int j = 0; j < address.size(); j++) {
                                String city = address.get(j).getCity();
                                String po = address.get(j).getPostcode();
                                String street = address.get(j).getStreet();
                                Log.d(TAG, "onCreate: ID: " + id + " City: " + city + " Po : " + po + " Street : " + street);
                                user.setPostCode(po);
                                user.setStreet(street);
                                user.setCity(city);
                            }
                        }
                        user.setContactId(String.valueOf(id));
                        if (givenName != null && familyName != null) {
                            user.setContactName(givenName + " " + familyName);
                        } else if (givenName != null) {
                            user.setContactName(givenName);
                        } else {
                            user.setContactName(familyName);
                        }
                        user.setContactImage(photo);

                        searchList.add(user);
                    }
                    loadRV();
                }
                return false;
            }
        });
        return view;
    }

    private void loadAllContact() {
        userList.clear();
        searchList.clear();
        Contacts.initialize(getContext());
//        Get All Contacts
        List<Contact> contacts = Contacts.getQuery().find();
        for (int i = 0; i < contacts.size(); i++) {
            MyContact user = new MyContact();
            long id = contacts.get(i).getId();
            String givenName = contacts.get(i).getGivenName();
            String familyName = contacts.get(i).getFamilyName();
            List<PhoneNumber> number = contacts.get(i).getPhoneNumbers();
            for (int j = 0; j < number.size(); j++) {
                String phoneNumber = number.get(j).getNumber();
                Log.d(TAG, "onCreate: Number" + phoneNumber);
                user.setContactNumber(phoneNumber);
            }
            String photo = contacts.get(i).getPhotoUri();
            List<Address> address = contacts.get(i).getAddresses();
            if (address.size() != 0) {
                for (int j = 0; j < address.size(); j++) {
                    String city = address.get(j).getCity();
                    String po = address.get(j).getPostcode();
                    String street = address.get(j).getStreet();
                    Log.d(TAG, "onCreate: ID: " + id + " City: " + city + " Po : " + po + " Street : " + street);
                    user.setPostCode(po);
                    user.setStreet(street);
                    user.setCity(city);
                }
            }
            user.setContactId(String.valueOf(id));
            if (givenName != null && familyName != null) {
                user.setContactName(givenName + " " + familyName);
            } else if (givenName != null) {
                user.setContactName(givenName);
            } else {
                user.setContactName(familyName);
            }
            user.setContactImage(photo);

            userList.add(user);
            Log.d(TAG, "onCreate: added ID: " + id + " GivenName : " + givenName + " FamilyName: " + familyName + " PhotoUri: " + photo);
        }

        no_of_con.setText(userList.size()+" Numbers");
        searchList.addAll(userList);
        loadRV();
    }


    private void loadRV() {
        AllContactsAdapter contactAdapter = new AllContactsAdapter(searchList, getContext());
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setAdapter(contactAdapter);
        contactAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), ContactDetailsActivity.class);
        intent.putExtra("list", (Serializable) searchList.get(position));
        startActivity(intent);
        Log.d("TAG", "onItemClick: " + userList.get(position).getContactName() + userList.get(position).getContactNumber() + userList.get(position).getContactImage());
    }
}