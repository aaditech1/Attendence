package com.example.facedetproject.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facedetproject.Models.AttendanceModels.AcknowledgeClass;
import com.example.facedetproject.R;

import java.util.ArrayList;

public class ReportViewAdapter extends RecyclerView.Adapter {

    LayoutInflater inflater;
    ArrayList<AcknowledgeClass> mMypojoclasslist;
    private Context mContext;


    public ReportViewAdapter(Context ctx, ArrayList<AcknowledgeClass> myPojoClassList) {
        mMypojoclasslist = myPojoClassList;
        mContext = ctx;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.recordlayout, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        String status;
        int colorValue;
        if ((mMypojoclasslist.get(position).getState().equals("out"))) {
            status = "Thank You";
            colorValue = Color.parseColor("#FF0000");
        } else {
            status = "Welcome";
            colorValue = Color.parseColor("#0000FF");
        }
        viewHolder.tvState.setText(status);
        viewHolder.tvState.setTextColor(colorValue);
        viewHolder.tvStaffName.setText(mMypojoclasslist.get(position).getName());
        viewHolder.tvStaffName.setTextColor(Color.parseColor("#4B0082"));
        viewHolder.tvEmployeeId.setText(mMypojoclasslist.get(position).getId());
        viewHolder.tvTime.setText(mMypojoclasslist.get(position).getCurrent_time().split("T")[1].split("\\.")[0]);
        viewHolder.tvDistance.setText(mMypojoclasslist.get(position).getDist().substring(0,5));
    }


    @Override
    public int getItemCount() {
        return mMypojoclasslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvState, tvStaffName, tvEmployeeId, tvTime, tvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvState = itemView.findViewById(R.id.tvState);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }


}
