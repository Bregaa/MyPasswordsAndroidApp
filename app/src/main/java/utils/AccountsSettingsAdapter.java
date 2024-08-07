package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.matteobreganni.mypasswords.R;

public class AccountsSettingsAdapter extends RecyclerView.Adapter<AccountsSettingsAdapter.ViewHolder> {

    private List<AccountSettingsItem> items;

    public AccountsSettingsAdapter(List<AccountSettingsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountSettingsItem item = items.get(position);
        holder.accountName.setText(item.getName());

        // Set up click listeners for buttons if needed
        holder.checkAccountPasswordButton.setOnClickListener(v -> {
            // Handle check button click
        });

        holder.deleteAccountButton.setOnClickListener(v -> {
            // Handle delete button click
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accountName;
        ImageButton checkAccountPasswordButton;
        ImageButton deleteAccountButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            checkAccountPasswordButton = itemView.findViewById(R.id.checkAccountPasswordButton);
            deleteAccountButton = itemView.findViewById(R.id.deleteAccountButton);
        }
    }
}
