package it.matteobreganni.mypasswords.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.AliasesAdapter;
import utils.FileHandlers;
import utils.OtherFunctions;

public class GeneratedPasswordFragment extends Fragment {

    private ImageButton buttonShowPassword;
    private TextView textViewGeneratedPasswordHidden;
    private TextView textViewGeneratedPasswordShown;
    private Button buttonCopyPassword;
    private Button buttonAddAliases;

    private EditText editTextServiceName;

    private TextView hiddenServiceName;

    AddFragment addFragment;
    RetrievePasswordFragment anotherFragment;

    @Override
    public void onStart() {
        super.onStart();

        // Gets the parent fragment, either AddFragment or RetrievePasswordFragment
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof AddFragment) {
            //addFragment = (AddFragment) parentFragment;
            editTextServiceName = parentFragment.getActivity().findViewById(R.id.editTextServiceName);
        } else if (parentFragment instanceof RetrievePasswordFragment) {
            //anotherFragment = (RetrievePasswordFragment) parentFragment;
            hiddenServiceName = parentFragment.getActivity().findViewById(R.id.hiddenServiceName);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_generated_password, container, false);

        buttonShowPassword = rootView.findViewById(R.id.buttonShowPassword);
        textViewGeneratedPasswordHidden = rootView.findViewById(R.id.textViewGeneratedPasswordHidden);
        textViewGeneratedPasswordShown = rootView.findViewById(R.id.textViewGeneratedPasswordShown);
        buttonCopyPassword = rootView.findViewById(R.id.buttonCopyPassword);
        buttonAddAliases = rootView.findViewById(R.id.buttonAddAliases);


        buttonShowPassword.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (isPasswordVisible) {
                    buttonShowPassword.setImageResource(R.drawable.eye_crossed);
                    textViewGeneratedPasswordHidden.setVisibility(View.VISIBLE);
                    textViewGeneratedPasswordShown.setVisibility(View.GONE);
                } else {
                    buttonShowPassword.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
                    textViewGeneratedPasswordHidden.setVisibility(View.GONE);
                    textViewGeneratedPasswordShown.setVisibility(View.VISIBLE);
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });

        buttonCopyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Password", textViewGeneratedPasswordShown.getText().toString());
                clipboard.setPrimaryClip(clip);
                //Toast.makeText(getActivity(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAddAliases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog.Builder and inflate the custom layout
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_aliases, null);
                builder.setView(dialogView);

                // Sets title
                // TODO
                String serviceName;
                if(editTextServiceName != null){
                    serviceName = OtherFunctions.serviceNameToStandard(editTextServiceName.getText().toString().trim());
                }else{
                    serviceName = OtherFunctions.serviceNameToStandard(hiddenServiceName.getText().toString().trim());
                }
                TextView dialogAliasesTitle = dialogView.findViewById(R.id.dialogAliasesTitle);
                dialogAliasesTitle.setText(serviceName + "'s aliases");

                // Initialize RecyclerView within the dialog view
                RecyclerView recyclerViewAliases = dialogView.findViewById(R.id.recyclerViewAliases);
                recyclerViewAliases.setLayoutManager(new GridLayoutManager(getContext(), 2));

                // Initialize list and adapter
                List<String> aliasesList = new ArrayList<>();
                AliasesAdapter aliasesAdapter = new AliasesAdapter(aliasesList, GeneratedPasswordFragment.this);
                recyclerViewAliases.setAdapter(aliasesAdapter);

                // Fills the recyclerview with the aliases for the selected service (useless in this bit of the code though, but I will use it later and delete it from here if I remember)
                // Gets selected account
                NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
                Menu menu = navigationView.getMenu();
                int accountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                if(accountHash == -1){
                    Toast.makeText(v.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
                }else {

                    List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), accountHash + ".txt");
                    for (int i = 1; i < fileContent.size(); i++) {  // Skipping the first line since that's the encrypted password
                        String[] line = fileContent.get(i);
                        // Check if the first item (main service name) in the line equals the service
                        if (line.length > 0 && line[0].equals(serviceName)) {
                            for (int j = 1; j < line.length; j++){
                                aliasesList.add(line[j]);
                            }
                        }
                    }
                }
                aliasesAdapter.notifyDataSetChanged();

                // Listener for the confirm button, to add an alias
                Button addAliasConfirmButton = dialogView.findViewById(R.id.addAliasConfirmButton);
                addAliasConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Gets the input alias
                        EditText editTextNewAlias = dialogView.findViewById(R.id.editTextNewAlias);
                        String newAlias = OtherFunctions.serviceNameToStandard(editTextNewAlias.getText().toString().trim());

                        // Checks if the alias has already been used
                        if(newAlias.isEmpty()){
                            editTextNewAlias.setError("New alias cannot be empty");
                        }else{
                            // Gets selected account
                            NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
                            Menu menu = navigationView.getMenu();
                            int accountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                            if(accountHash == -1){
                                Toast.makeText(v.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
                            }else{
                                // Checks if the alias has already been used
                                boolean aliasFound = false;
                                List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), accountHash + ".txt");
                                if(OtherFunctions.serviceExists(fileContent, newAlias)){
                                    aliasFound = true;
                                    editTextNewAlias.setError("Alias already used as main service name.");
                                    Toast.makeText(v.getContext(), "Alias already used as main service name."
                                            , Toast.LENGTH_LONG).show();
                                }else{
                                    String serviceFromAlias = OtherFunctions.findServiceFromAlias(fileContent, newAlias);
                                    if(!serviceFromAlias.equals("")){
                                        aliasFound = true;
                                        editTextNewAlias.setError("Alias already used with the service '"
                                                + serviceFromAlias + "'. Remove the alias through the search page or use a different name.");
                                        Toast.makeText(v.getContext(), "Alias already used with the service '"
                                                        + serviceFromAlias + "'. Remove the alias through the search page or use a different name."
                                                , Toast.LENGTH_LONG).show();
                                    }
                                }

                                // If the alias is a new alias
                                if(!aliasFound){
                                    // Adds the alias to the account's service's list of alias
                                    String serviceName;
                                    if(editTextServiceName != null){
                                        serviceName = OtherFunctions.serviceNameToStandard(editTextServiceName.getText().toString().trim());
                                    }else{
                                        serviceName = OtherFunctions.serviceNameToStandard(hiddenServiceName.getText().toString().trim());
                                    }
                                    List<String> fileContentLines = FileHandlers.readFileLines(v.getContext(), (accountHash + ".txt"));


                                    // Finds the service's line in the file
                                    int serviceIndex = 0;
                                    for (int i = 1; i < fileContent.size(); i++) {  // Skipping the first line since that's the encrypted password
                                        String[] line = fileContent.get(i);
                                        // Check if the first item (main service name) in the line equals the service
                                        if (line.length > 0 && line[0].equals(serviceName)) {
                                            serviceIndex = i;
                                        }
                                    }
                                    if(serviceIndex == 0){
                                        Toast.makeText(v.getContext(), "Unexpected error finding the service index", Toast.LENGTH_SHORT).show();
                                    }else{
                                        // Appends the alias in the service's line and overwrites the file
                                        String previousServiceLine = fileContentLines.get(serviceIndex);
                                        String appendServiceLine = previousServiceLine + "," + newAlias;
                                        fileContentLines.set(serviceIndex, appendServiceLine);
                                        FileHandlers.writeFileLines(v.getContext(), accountHash + ".txt", fileContentLines);

                                        // Adds the alias to the recyclerview
                                        editTextNewAlias.setText("");
                                        aliasesList.add(newAlias);
                                        aliasesAdapter.notifyItemInserted(aliasesList.size() - 1);
                                    }
                                }

                            }
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return rootView;
    }


}