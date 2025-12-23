package com.example.dustbinpro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dustbinpro.IntroSliderActivity;
import com.example.dustbinpro.R;

public class IntroSliderAdapter extends RecyclerView.Adapter<IntroSliderAdapter.SliderViewHolder> {

    private final int[] images = {
            R.drawable.ic_waste_management,
            R.drawable.ic_clean_city,
            R.drawable.ic_team_work
    };
    private final String[] titles = {
            "Professional Waste Management",
            "Clean Communities",
            "Dedicated Team"
    };
    private final String[] descriptions = {
            "We provide efficient dustbin cleaning and waste management services in Soweto and Chiawelo",
            "Helping to keep Kasi clean and healthy for everyone",
            "Our experienced team is committed to quality service"
    };

    public IntroSliderAdapter(IntroSliderActivity activity) {
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.image.setImageResource(images[position]);
        holder.title.setText(titles[position]);
        holder.description.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageSlider);
            title = itemView.findViewById(R.id.titleSlider);
            description = itemView.findViewById(R.id.descriptionSlider);
        }
    }
}