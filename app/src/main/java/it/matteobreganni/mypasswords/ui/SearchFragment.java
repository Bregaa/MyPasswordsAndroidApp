package it.matteobreganni.mypasswords.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.FileHandlers;
import utils.OtherFunctions;
import utils.ServiceItem;
import utils.ServicesAdapter;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServicesAdapter servicesAdapter;
    private List<ServiceItem> itemList;
    private TextView textViewSearchPasswordAccountName;
    private EditText editTextSearchServiceName;

    private int selectedAccountHash;
    private List<String[]> fileAccountContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        textViewSearchPasswordAccountName = view.findViewById(R.id.textViewSearchPasswordAccountName);
        editTextSearchServiceName = view.findViewById(R.id.editTextSearchServiceName);


        NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
        Menu menu = navigationView.getMenu();
        String selectedAccountName = OtherFunctions.findSelectedMenuItemNameInGroup(menu, R.id.drawerGroup1);
        textViewSearchPasswordAccountName.setText("for " + selectedAccountName);

        selectedAccountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
        fileAccountContent = FileHandlers.readFileAndDivideLines(getContext(), selectedAccountHash + ".txt");

        // Populates the recyclerview with the services and aliases
        itemList = new ArrayList<>();

        servicesAdapter = new ServicesAdapter(itemList, new ServicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ServiceItem item) {
                Toast.makeText(view.getContext(), "Item clicked!", Toast.LENGTH_SHORT).show();
                // TODO
            }
        }, new ServicesAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(ServiceItem item) {
                // Handle delete button click

                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_delete_service, null);

                Button buttonConfirm = dialogView.findViewById(R.id.deleteServiceButtonConfirm);

                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

                // Confirm button listener
                buttonConfirm.setOnClickListener(view -> {
                    String serviceNameToDelete = item.getTitle();

                    // Gets selected account
                    NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
                    Menu menu = navigationView.getMenu();
                    int accountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                    if(accountHash == -1){
                        Toast.makeText(view.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
                    }else {
                        // Finds and delete the service's line in the file
                        List<String[]> fileContent = FileHandlers.readFileAndDivideLines(view.getContext(), accountHash + ".txt");
                        int indexFound = 0;
                        for (int i = 1; i < fileContent.size(); i++){   // Skip hash line
                            String[] line = fileContent.get(i);
                            if(line[0].equals(serviceNameToDelete)){
                                indexFound = i;
                                break;
                            }
                        }
                        if(indexFound == 0){
                            Toast.makeText(view.getContext(), "Unexpected error finding the service to delete in the file", Toast.LENGTH_SHORT).show();
                        }else{
                            List<String> fileContentLines = FileHandlers.readFileLines(view.getContext(), (accountHash + ".txt"));
                            fileContentLines.remove(indexFound);
                            fileContent.remove(indexFound);
                            fileAccountContent = fileContent;
                            FileHandlers.writeFileLines(view.getContext(), accountHash + ".txt", fileContentLines);
                            itemList.remove(item);
                            servicesAdapter.notifyDataSetChanged();
                        }
                    }
                    dialog.dismiss();

                });
                dialog.show();
            }
        });
        recyclerView.setAdapter(servicesAdapter);

        editTextSearchServiceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If the saved file is from a different account, get the right file (the file's content is saved in a variable to not read the file every time)
                if(OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1) != selectedAccountHash) {
                    selectedAccountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                    fileAccountContent = FileHandlers.readFileAndDivideLines(getContext(), selectedAccountHash + ".txt");
                }
                itemList.clear();

                String serviceName = OtherFunctions.serviceNameToStandard(s.toString());
                // If the text isn't empty
                if(!serviceName.equals("")){
                    List<String[]> searchResults = new ArrayList<>();
                    // Find all lines with a serviceName match (service or alias)
                    for (int i = 1; i < fileAccountContent.size(); i++) {   // Skips the first item since that's the hash
                        String[] line = fileAccountContent.get(i);
                        boolean matchFound = false;
                        for (int j = 0; j < line.length; j++) {
                            if (line[j].contains(serviceName)) {
                                matchFound = true;
                                break;
                            }
                        }
                        if (matchFound) {

                            searchResults.add(line);
                        }
                    }

                    for (String[] line : searchResults) {
                        String aliases = "Aliases: ";
                        for (int i = 1; i < line.length; i++){
                            if(i == 1){
                                aliases = aliases + line[i];
                            }else{
                                aliases = aliases + ", " + line[i];
                            }
                        }
                        if(aliases.equals("Aliases: ")){
                            aliases = "No aliases for this service!";
                        }
                        itemList.add(new ServiceItem(line[0], aliases));
                        Log.d("asd", "5");
                    }
                    servicesAdapter.notifyDataSetChanged();
                }else{
                    servicesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });



        return view;
    }
}
