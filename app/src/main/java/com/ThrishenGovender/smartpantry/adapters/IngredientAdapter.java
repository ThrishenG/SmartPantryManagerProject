package com.ThrishenGovender.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ThrishenGovender.smartpantry.R;
import com.ThrishenGovender.smartpantry.models.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients = new ArrayList<>();

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient currentIngredient = ingredients.get(position);
        holder.textViewName.setText(currentIngredient.name);
        holder.textViewQuantity.setText(String.valueOf(currentIngredient.quantity));
        holder.textViewUnit.setText(currentIngredient.unit);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Method to update the list when database changes
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewQuantity, textViewUnit;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textIngredientName);
            textViewQuantity = itemView.findViewById(R.id.textIngredientQuantity);
            textViewUnit = itemView.findViewById(R.id.textIngredientUnit);
        }
    }
}
