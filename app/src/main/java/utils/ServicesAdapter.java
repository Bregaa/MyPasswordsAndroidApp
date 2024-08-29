package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.matteobreganni.mypasswords.R;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.SearchViewHolder> {

    private List<ServiceItem> items;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    // Interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(ServiceItem item);
    }

    // Interface for delete button clicks
    public interface OnDeleteClickListener {
        void onDeleteClick(ServiceItem item);
    }

    // Constructor with both listeners
    public ServicesAdapter(List<ServiceItem> items, OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        this.items = items;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        ServiceItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.bind(item, listener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        CardView cardView;
        ImageButton deleteButton;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.serviceItemTitle);
            description = itemView.findViewById(R.id.serviceItemDescription);
            cardView = itemView.findViewById(R.id.serviceItemCardView);
            deleteButton = itemView.findViewById(R.id.buttonDeleteService);
        }

        public void bind(final ServiceItem item, final OnItemClickListener listener, final OnDeleteClickListener deleteListener) {
            // Item click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

            // Delete button click listener
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDeleteClick(item);
                }
            });
        }
    }
}
