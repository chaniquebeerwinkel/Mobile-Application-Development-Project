package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private ArrayList<PantryItem> pantryItems;
    private DatabaseHelper databaseHelper;

    public PantryAdapter(
            Context context,
            ArrayList<PantryItem> pantryItems) {

        this.context = context;
        this.pantryItems = pantryItems;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_pantry,
                        parent,
                        false
                );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item = pantryItems.get(position);

        holder.txtName.setText(item.getName());

        String quantityText =
                formatQuantity(item.getQuantity())
                        + " "
                        + item.getUnit();

        holder.txtQuantity.setText(quantityText);

        if (item.getExpiryDate() == null
                || item.getExpiryDate().isEmpty()) {

            holder.txtExpiry.setText("Expiry: Not specified");

        } else {

            holder.txtExpiry.setText(
                    "Expiry: " + item.getExpiryDate()
            );
        }

        // EDIT
        holder.btnEdit.setOnClickListener(view -> {

            Intent intent = new Intent(
                    context,
                    AddEditIngredientActivity.class
            );

            intent.putExtra(
                    "ingredient_id",
                    item.getId()
            );

            intent.putExtra(
                    "ingredient_name",
                    item.getName()
            );

            intent.putExtra(
                    "ingredient_quantity",
                    item.getQuantity()
            );

            intent.putExtra(
                    "ingredient_unit",
                    item.getUnit()
            );

            intent.putExtra(
                    "ingredient_expiry",
                    item.getExpiryDate()
            );

            context.startActivity(intent);
        });

        // DELETE
        holder.btnDelete.setOnClickListener(view -> {

            databaseHelper.deletePantryItem(
                    item.getId()
            );

            pantryItems.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(
                    position,
                    pantryItems.size()
            );
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    private String formatQuantity(double quantity) {

        if (quantity == (long) quantity) {

            return String.valueOf((long) quantity);

        } else {

            return String.valueOf(quantity);
        }
    }

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtQuantity;
        TextView txtExpiry;

        Button btnEdit;
        Button btnDelete;

        public PantryViewHolder(@NonNull View itemView) {

            super(itemView);

            txtName = itemView.findViewById(
                    R.id.txtIngredientName
            );

            txtQuantity = itemView.findViewById(
                    R.id.txtIngredientQuantity
            );

            txtExpiry = itemView.findViewById(
                    R.id.txtIngredientExpiry
            );

            btnEdit = itemView.findViewById(
                    R.id.btnEdit
            );

            btnDelete = itemView.findViewById(
                    R.id.btnDelete
            );
        }
    }
}
