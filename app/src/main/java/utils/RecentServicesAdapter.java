package utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.matteobreganni.mypasswords.R;

public class RecentServicesAdapter extends RecyclerView.Adapter<RecentServicesAdapter.HomeViewHolder> {

    private List<RecentServiceItem> items;
    private RecentServicesAdapter.OnItemClickListener listener;

    // Interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(RecentServiceItem item);
    }

    // Constructor with both listeners
    public RecentServicesAdapter(List<RecentServiceItem> items, RecentServicesAdapter.OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentServicesAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_service, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentServicesAdapter.HomeViewHolder holder, int position) {
        RecentServiceItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        CardView cardView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recentServiceItemTitle);
            description = itemView.findViewById(R.id.recentServiceItemDescription);
            cardView = itemView.findViewById(R.id.recentServiceItemCardView);
        }

        public void bind(final RecentServiceItem item, final RecentServicesAdapter.OnItemClickListener listener) {
            // Item click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
