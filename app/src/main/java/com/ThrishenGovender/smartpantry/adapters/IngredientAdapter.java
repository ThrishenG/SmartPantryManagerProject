package com.ThrishenGovender.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient item = ingredients.get(position);
        holder.textName.setText(item.name);
        holder.textCategory.setText(item.category != null ? item.category : "Uncategorized");

        String qtyText = item.quantity + " " + item.unit;
        holder.textQtyUnit.setText(qtyText);

        if (item.quantity <= 2) {
            holder.badgeLowStock.setVisibility(View.VISIBLE);
        } else {
            holder.badgeLowStock.setVisibility(View.GONE);
        }

        int badgeColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.badgeFresh);
        int iconRes = android.R.drawable.ic_menu_agenda;

        if (item.category != null) {
            switch (item.category.toLowerCase()) {
                case "fresh":
                    badgeColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.badgeFresh);
                    iconRes = android.R.drawable.ic_menu_compass;
                    break;
                case "dairy":
                    badgeColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.badgeDairy);
                    iconRes = android.R.drawable.ic_menu_slideshow;
                    break;
                case "grains":
                    badgeColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.badgeGrains);
                    iconRes = android.R.drawable.ic_menu_myplaces;
                    break;
                case "cans":
                    badgeColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.badgeCans);
                    iconRes = android.R.drawable.ic_menu_gallery;
                    break;
            }
        }

        holder.badgeView.setCardBackgroundColor(badgeColor);
        holder.imgCategory.setImageResource(iconRes);

        // Handle Row Clicks for Editing
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public Ingredient getIngredientAt(int position) {
        return ingredients.get(position);
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textCategory, textQtyUnit;
        CardView badgeView, badgeLowStock;
        ImageView imgCategory;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textIngredientName);
            textCategory = itemView.findViewById(R.id.textCategory);
            textQtyUnit = itemView.findViewById(R.id.textQuantityUnit);
            badgeView = itemView.findViewById(R.id.categoryIconBadge);
            badgeLowStock = itemView.findViewById(R.id.badgeLowStock);
            imgCategory = itemView.findViewById(R.id.imgCategory);
        }
    }
}