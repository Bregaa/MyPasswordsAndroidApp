package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.matteobreganni.mypasswords.R;

public class AliasesAdapter extends RecyclerView.Adapter<AliasesAdapter.AliasViewHolder> {

    private final List<String> aliases;

    public AliasesAdapter(List<String> aliases) {
        this.aliases = aliases;
    }

    @NonNull
    @Override
    public AliasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alias, parent, false);
        return new AliasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AliasViewHolder holder, int position) {
        String alias = aliases.get(position);
        holder.textViewAlias.setText(alias);
        holder.buttonDeleteAlias.setOnClickListener(v -> {
            // Handle delete alias logic here
            aliases.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, aliases.size());
        });
    }

    @Override
    public int getItemCount() {
        return aliases.size();
    }

    static class AliasViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAlias;
        ImageButton buttonDeleteAlias;

        AliasViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAlias = itemView.findViewById(R.id.textViewAlias);
            buttonDeleteAlias = itemView.findViewById(R.id.buttonDeleteAlias);
        }
    }
}