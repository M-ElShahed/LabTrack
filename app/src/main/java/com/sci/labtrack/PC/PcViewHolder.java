package com.sci.labtrack.PC;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sci.labtrack.R;

public  class PcViewHolder extends RecyclerView.ViewHolder {
    public Button pcImgButton;
    public Button statuesButton;
    public ImageButton reportButton;
    public RelativeLayout pcCard;
    public CardView pcCardView;
    public PcViewHolder(@NonNull View itemView) {
        super(itemView);
        this.reportButton = itemView.findViewById(R.id.reportButton);
        this.pcCard = itemView.findViewById(R.id.pcCard);
        this.pcImgButton= itemView.findViewById(R.id.pcImgText);
        this.pcCardView = itemView.findViewById(R.id.pcCardView);
        this.statuesButton = itemView.findViewById(R.id.statuesButton);
    }
}