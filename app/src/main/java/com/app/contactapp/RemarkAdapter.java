package com.app.contactapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RemarkAdapter extends RecyclerView.Adapter<RemarkAdapter.RemarkViewHolder> {
    private List<MyContactDetails> contactVOList;
    private Context mContext;
    private OnItemClickListener mListener;

    public RemarkAdapter(List<MyContactDetails> contactVOList, Context mContext) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }

    @Override
    public RemarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_remark_view, null);
        RemarkViewHolder contactViewHolder = new RemarkViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(RemarkViewHolder holder, int position) {
        if (position < contactVOList.size()) {
            if(contactVOList.get(position)!=null) {
//            if (contactVOList.get(position).getId().equals(String.valueOf(position))) {
                MyContactDetails contactVO = contactVOList.get(position);
                holder.descriptionTv.setText(contactVO.getDescription());
                holder.remark1Tv.setText(contactVO.getRemark1());
                holder.remark2Tv.setText(contactVO.getRemark2());
                holder.dateTv.setText("Date: " + contactVO.getDate());
                holder.statusTv.setText("Status: " + contactVO.getStatus());
                if(contactVO.getNotify().equals("0")){
                    holder.notifyCB.setSelected(false);
                }
                else {
                    holder.notifyCB.setSelected(true);
                }
            }
//            }
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class RemarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView descriptionTv, remark1Tv, remark2Tv, dateTv, statusTv;
        ImageView notifyCB;

        public RemarkViewHolder(View itemView) {
            super(itemView);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            remark1Tv = itemView.findViewById(R.id.remark1Tv);
            remark2Tv = itemView.findViewById(R.id.remark2Tv);
            dateTv = itemView.findViewById(R.id.dateTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            notifyCB = itemView.findViewById(R.id.notifyCB);

            notifyCB.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    mListener.setOnRemarkClickListener(position, v);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (mListener != null) {
                if (position != RecyclerView.NO_POSITION) {
                    mListener.setOnRemarkLongClickListener(position);
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void setOnRemarkClickListener(int position, View v);

        void setOnRemarkLongClickListener(int position);
    }

    public void setOnRemarkClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
