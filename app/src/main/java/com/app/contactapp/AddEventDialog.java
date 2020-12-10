package com.app.contactapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddEventDialog extends AppCompatDialogFragment {
    private EditText edit_des, edit_date, edit_status, edit_remark1, edit_remark2;
    private AddEventDialogListener listener;
    String row_index,has_data;
    MyContactDetails user;

    public AddEventDialog(String row_index,String has_data) {
        this.row_index = row_index;
        this.has_data = has_data;

    }
    public AddEventDialog(String row_index,MyContactDetails user) {
        this.user = user;
        this.row_index = row_index;
        this.has_data = "yes";
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_event_layout, null);

        builder.setView(view)
                .setTitle("Edit")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String description = edit_des.getText().toString();
                        String date = edit_date.getText().toString();
                        String status = edit_status.getText().toString();
                        String remark1 = edit_remark1.getText().toString();
                        String remark2 = edit_remark2.getText().toString();
                        if (has_data.equals("null")) {
//                            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
                            listener.applyTexts(row_index,description, date, status, remark1, remark2);
                            Toast.makeText(getContext(), "added", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            listener.updateTexts(user.getId(),description, date, status, remark1, remark2);
                            Toast.makeText(getContext(), "data updated", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        edit_des = view.findViewById(R.id.edit_des);
        edit_date = view.findViewById(R.id.edit_date);
        edit_status = view.findViewById(R.id.edit_status);
        edit_remark1 = view.findViewById(R.id.edit_remark1);
        edit_remark2 = view.findViewById(R.id.edit_remark2);
        if(!has_data.equals("null")){
            edit_des.setText(user.getDescription());
            edit_date.setText(user.getDate());
            edit_status.setText(user.getStatus());
            edit_remark1.setText(user.getRemark1());
            edit_remark2.setText(user.getRemark2());
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddEventDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement AddEventDialogListener");
        }
    }

    public interface AddEventDialogListener {
        void applyTexts(String row_index, String description, String date, String status, String remark1, String remark2);

        void updateTexts(String id, String description, String date, String status, String remark1, String remark2);
    }
}
