package com.sci.labtrack.PC;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sci.labtrack.R;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.sci.labtrack.R2.id.statuesButton;

public class PcAdapter extends RecyclerView.Adapter<PcViewHolder> {
    Listener listener;
    ArrayList<PC> pcArrayList ;
    View view;

    public PcAdapter( ArrayList<PC> pcArrayList,Listener mListener) {
        listener = mListener;
        this.pcArrayList = pcArrayList;
    }

    @NonNull
    @Override
    public PcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pc_card,parent,false);
        return new PcViewHolder(view) ;
    }


    @Override
    public void onBindViewHolder(@NonNull PcViewHolder holder, final int position) {
        int i = 0 ;
        if(pcArrayList.get(position).isSelection()) {
            holder.pcCard.setBackgroundColor(Color.parseColor("#40e6ff"));
            holder.pcCard.setBackgroundResource(R.color.Selection);
            holder.reportButton.setBackgroundResource(R.color.Selection);
        }
        else {
            holder.pcCard.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.reportButton.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if(pcArrayList.get(position).isNeedRepair()) {
            holder.pcCardView.setBackgroundResource(R.color.colorAccent);
            holder.pcImgButton.setBackgroundResource(R.drawable.ic_computer_monitor_and_mouse);
            holder.statuesButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.wrong,0,0,0);
            if(!pcArrayList.get(position).isKeyBoard()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noKeyboard));
                i++;
            } if(!pcArrayList.get(position).isMouse()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noMouse));
                i++;
            } if(!pcArrayList.get(position).isMonitor()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noMonitor));
                i++;
            }

             if (!pcArrayList.get(position).getMissingSoftware().isEmpty()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.missingPrograms));
                i++;
            }
             if (!pcArrayList.get(position).isOperatingSystemStatue()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noOperatingsystem));
                i++;
            } if(!pcArrayList.get(position).isRamWorking()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noRam));
                i++;
            } if(!pcArrayList.get(position).isHardDiskWorking()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noHDD));
                i++;
            } if(!pcArrayList.get(position).isProcessorWorking()){
                holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.noProcessor));
                i++;
            }
            if(!pcArrayList.get(position).getProblemDescription().isEmpty()) {
                holder.statuesButton.setText(pcArrayList.get(position).getProblemDescription());
                i++;
            }
            if(i>1){
                holder.statuesButton.setText(i+holder.statuesButton.getContext().getString(R.string.problemFound));
            }
           // Log.e(TAG,i+"");
        }
        else{
            holder.pcCardView.setBackgroundResource(R.color.colorPrimaryDark);
            holder.pcImgButton.setBackgroundResource(R.drawable.ic_computer_monitor_and_mouse_working);
            holder.statuesButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.correct,0,0,0);
            holder.statuesButton.setText(holder.statuesButton.getContext().getString(R.string.allIsGood));
        }

        holder.pcImgButton.setText(""+(pcArrayList.get(position).getNumberInLab()));
        holder.pcImgButton.setOnLongClickListener(v->{
            listener.longClickActivated(true);
            listener.onStateChange(true,pcArrayList.get(position),position);
            return true ;
        });
        holder.itemView.setOnLongClickListener(v -> {
                listener.longClickActivated(true);
                listener.onStateChange(true,pcArrayList.get(position),position);
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            if (!pcArrayList.get(position).isSelection()) {
                listener.onStateChange(true, pcArrayList.get(position), position);
            } else {
                listener.onStateChange(false, pcArrayList.get(position), position);
            }
        });
        holder.pcImgButton.setOnClickListener(v -> {
            if (!pcArrayList.get(position).isSelection()) {
                listener.onStateChange(true, pcArrayList.get(position), position);
            } else {
                listener.onStateChange(false, pcArrayList.get(position), position);
            }
        });
        holder.reportButton.setOnClickListener(v -> listener.onReportClick(pcArrayList.get(position), position,pcArrayList));
    }
    @Override
    public int getItemCount() {
        return pcArrayList.size();
    }
    public interface Listener {
        public void onStateChange(boolean state,PC pc,int pos);
        public void longClickActivated(boolean activated);
        public void onReportClick(PC pc,int pos,ArrayList<PC> pcArrayList);
    }
}
