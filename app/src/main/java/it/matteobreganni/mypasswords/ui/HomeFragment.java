package it.matteobreganni.mypasswords.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.FileHandlers;
import utils.OtherFunctions;
import utils.RecentServiceItem;
import utils.RecentServicesAdapter;
import utils.ServiceItem;
import utils.ServicesAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecentServicesAdapter recentServicesAdapter;
    private List<RecentServiceItem> itemList;
    private CardView noRecentPasswordsSection;
    private CardView noAccountSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setting bottom menu selections
        FloatingActionButton homeFabButton = getActivity().findViewById(R.id.homeFabButton);
        BottomNavigationView bottomMenu = getActivity().findViewById(R.id.bottomNavigationView);
        homeFabButton.setImageTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.bottomMenuSelectedItem, requireActivity().getTheme())
        ));
        if(bottomMenu.getSelectedItemId() != R.id.home){
            bottomMenu.setSelectedItemId(R.id.home);
        }

        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noRecentPasswordsSection = view.findViewById(R.id.noRecentPasswordsSection);
        noAccountSection = view.findViewById(R.id.noAccountSection);

        NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
        Menu menu = navigationView.getMenu();

        // Populates the recyclerview
        itemList = new ArrayList<>();
        if(FileHandlers.fileExists(view.getContext(), "recentServices.txt")){
            List<String[]> fileContent = FileHandlers.readFileAndDivideLines(view.getContext(), "recentServices.txt");
            for (String[] line : fileContent){
                itemList.add(new RecentServiceItem(line[0], "account: " + line[1]));
            }

        }

        // Shows or hides the two cardviews in the homepage, if there is no account and if there is no recent password
        if(menu.size() == 1){
            noAccountSection.setVisibility(View.VISIBLE);
        }else{
            noAccountSection.setVisibility(View.GONE);
            if(itemList.size() == 0){
                noRecentPasswordsSection.setVisibility(View.VISIBLE);
            }else{
                noRecentPasswordsSection.setVisibility(View.GONE);
            }
        }

        recentServicesAdapter = new RecentServicesAdapter(itemList, new RecentServicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecentServiceItem item) {
                // Creates a new instance of the RetrievePasswordFragment
                RetrievePasswordFragment retrievePasswordFragment = new RetrievePasswordFragment();

                // Creates a Bundle to pass the data
                Bundle bundle = new Bundle();
                bundle.putString("serviceName", item.getTitle());
                retrievePasswordFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, retrievePasswordFragment, "RetrievePasswordFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerView.setAdapter(recentServicesAdapter);


        return view;
    }
}