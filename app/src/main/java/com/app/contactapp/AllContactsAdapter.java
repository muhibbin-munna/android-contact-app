package com.app.contactapp;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder> {

    private List<MyContact> contactVOList;
    private Context mContext;
    private OnItemClickListener mListener;

    public AllContactsAdapter(List<MyContact> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        MyContact contactVO = contactVOList.get(position);
        holder.tvContactName.setText(contactVO.getContactName());
        holder.tvPhoneNumber.setText(contactVO.getContactNumber());
    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvContactName;
        TextView tvPhoneNumber;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
