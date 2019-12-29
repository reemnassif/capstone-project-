package com.khlafawi.capmedicine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineViewHolder extends RecyclerView.ViewHolder {

    ImageView medicine_image;
    TextView medicineName, medicineDose;

    public MedicineViewHolder(@NonNull View itemView) {
        super(itemView);

        this.medicine_image = itemView.findViewById(R.id.medicine_image);
        this.medicineName = itemView.findViewById(R.id.medicineName);
        this.medicineDose = itemView.findViewById(R.id.medicineDose);

    }
}
