package it.matteobreganni.mypasswords.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.RecentServiceItem;
import utils.RecentServicesAdapter;
import utils.ServiceItem;
import utils.ServicesAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecentServicesAdapter recentServicesAdapter;
    private List<RecentServiceItem> itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Populate the recyclerview
        itemList = new ArrayList<>();
        itemList.add(new RecentServiceItem("Title1", "Description1"));
        itemList.add(new RecentServiceItem("Title2", "Description2"));
        itemList.add(new RecentServiceItem("Title3", "Description3"));

        recentServicesAdapter = new RecentServicesAdapter(itemList, new RecentServicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecentServiceItem item) {
                Toast.makeText(getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(recentServicesAdapter);


        return view;
    }
}