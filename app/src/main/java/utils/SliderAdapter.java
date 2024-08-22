package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.matteobreganni.mypasswords.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<SlideItem> slideItems;

    public SliderAdapter(Context context, List<SlideItem> slideItems) {
        this.context = context;
        this.slideItems = slideItems;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slide, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.imageView.setImageResource(slideItems.get(position).getImage());
        holder.title.setText(slideItems.get(position).getTitle());
        holder.description.setText(slideItems.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return slideItems.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, description;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slide_image);
            title = itemView.findViewById(R.id.slide_title);
            description = itemView.findViewById(R.id.slide_description);
        }
    }
}

