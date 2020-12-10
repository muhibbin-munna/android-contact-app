package com.app.contactapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


import static android.widget.SearchView.*;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, PopupMenu.OnMenuItemClickListener, OnClickListener {

    EditTextV2 name, remark1, remark2, remark3;
    Button menu;
    String TAG = "MainActivity";


    private static final int READ_REQUEST_PERMISSIONS = 300;
    private boolean REQUEST_PERMISSION_MODE = false;
    public boolean isPermissionReceived = false;
    AlertDialog alertDialog;
    static TextView no_of_contact;
    SharedPreferences sharedpreferences;
    ImageButton uploadImageButton;
    int RESULT_LOAD_IMG = 1;
    ImageView ppImageView;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        name = findViewById(R.id.nameEditText);
        remark1 = findViewById(R.id.remarkEditText1);
        remark2 = findViewById(R.id.remarkEditText2);
        remark3 = findViewById(R.id.remarkEditText3);
        menu = findViewById(R.id.menu);
        ppImageView = findViewById(R.id.ppImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        no_of_contact = findViewById(R.id.no_of_contact);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        if (!checkIfAlreadyhavePermission()) {
            Log.d(TAG, "onCreate: check");
            requestForSpecificPermission();
        } else {
            Fragment contactListFragment = new ContactListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contactFrame, contactListFragment, "pframe")
                    .commit();
        }

        sharedpreferences = getSharedPreferences("com.app.contactapp", Context.MODE_PRIVATE);
        String namePref = sharedpreferences.getString("name", "Enter Name");
        String remark1Pref = sharedpreferences.getString("remark1", "Remark");
        String remark2Pref = sharedpreferences.getString("remark2", "Remark");
        String remark3Pref = sharedpreferences.getString("remark3", "Remark");
        String previouslyEncodedImage = sharedpreferences.getString("pp_data", "");
        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            ppImageView.setImageBitmap(bitmap);
        }
        name.setText(namePref);
        remark1.setText(remark1Pref);
        remark2.setText(remark2Pref);
        remark3.setText(remark3Pref);

        name.setOnEditorActionListener(this);
        remark1.setOnEditorActionListener(this);
        remark2.setOnEditorActionListener(this);
        remark3.setOnEditorActionListener(this);

        uploadImageButton.setOnClickListener(this);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("name", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        remark1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remark1", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        remark2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remark2", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        remark3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("remark3", String.valueOf(s));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                Toast.makeText(this, "Item 3 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item4:
                Toast.makeText(this, "Item 4 clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item5:
                Toast.makeText(this, "Item 5 clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            switch (v.getId()) {
                case R.id.nameEditText:
                    name.setFocusable(false);
                    name.setFocusableInTouchMode(true);
                    break;
                case R.id.remarkEditText1:
                    remark1.setFocusable(false);
                    remark1.setFocusableInTouchMode(true);
                    break;
                case R.id.remarkEditText2:
                    remark2.setFocusable(false);
                    remark2.setFocusableInTouchMode(true);
                    break;
                case R.id.remarkEditText3:
                    remark3.setFocusable(false);
                    remark3.setFocusableInTouchMode(true);
                    break;
            }
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadImageButton:
                openFileChooser();
                break;
        }
    }

    private void openFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int ratio = selectedImage.getHeight() / selectedImage.getWidth();
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 200, 200 * ratio, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("pp_data", encodedImage);
                edit.apply();
                ppImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        Log.d(TAG, "checkIfAlreadyhavePermission: ");
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 101);
        Log.d(TAG, "requestForSpecificPermission: ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Fragment contactListFragment = new ContactListFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contactFrame, contactListFragment, "pframe")
                        .commitAllowingStateLoss();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            //            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}