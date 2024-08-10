package it.matteobreganni.mypasswords.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.AliasesAdapter;
import utils.EncryptionHandlers;
import utils.FileHandlers;

public class AddFragment extends Fragment {

    private TextView textViewGeneratePasswordAccountName;
    private TextView editTextServiceName;
    private Button buttonGeneratePassword;
    private TextView textViewGeneratedPasswordTitle;
    private Button buttonCopyPassword;
    private Button buttonAddAliases;
    private ImageButton buttonShowPassword;
    private TextView textViewGeneratedPasswordHidden;
    private TextView textViewGeneratedPasswordShown;
    private CardView generatedPasswordSection;
    private LinearLayout generatePasswordWarningSection;
    private TextView generatePasswordWarningText;

    private int selectedAccountHash;
    private List<String[]> fileAccountContent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        // Retrieve references to the UI components
        textViewGeneratePasswordAccountName = rootView.findViewById(R.id.textViewGeneratePasswordAccountName);
        editTextServiceName = rootView.findViewById(R.id.editTextServiceName);
        buttonGeneratePassword = rootView.findViewById(R.id.buttonGeneratePassword);
        textViewGeneratedPasswordTitle = rootView.findViewById(R.id.textViewGeneratedPasswordTitle);
        buttonCopyPassword = rootView.findViewById(R.id.buttonCopyPassword);
        buttonAddAliases = rootView.findViewById(R.id.buttonAddAliases);
        buttonShowPassword = rootView.findViewById(R.id.buttonShowPassword);
        textViewGeneratedPasswordHidden = rootView.findViewById(R.id.textViewGeneratedPasswordHidden);
        textViewGeneratedPasswordShown = rootView.findViewById(R.id.textViewGeneratedPasswordShown);
        generatedPasswordSection = rootView.findViewById(R.id.generatedPasswordSection);
        generatePasswordWarningSection = rootView.findViewById(R.id.generatePasswordWarningSection);
        generatePasswordWarningText = rootView.findViewById(R.id.generatePasswordWarningText);

        NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
        Menu menu = navigationView.getMenu();
        String selectedAccountName = findSelectedMenuItemNameInGroup(menu, R.id.drawerGroup1);
        textViewGeneratePasswordAccountName.setText("for " + selectedAccountName);

        selectedAccountHash = findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
        fileAccountContent = FileHandlers.readFileAndDivideLines(getContext(), selectedAccountHash + ".txt");


        editTextServiceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String serviceName = s.toString();
                List<String> similarServices = new ArrayList<>();
                if(serviceName.length() > 1){
                    for (int i = 1; i < fileAccountContent.size(); i++) {
                        String[] line = fileAccountContent.get(i);
                        for (String str : line) {
                            if(editDistance(serviceName, str) <= 2){
                                similarServices.add(str);
                            }
                        }
                    }

                    if(similarServices.size() != 0){
                        String servicesConcatenated = similarServices.get(0);
                        for (int i = 1; i < similarServices.size(); i++) {
                            servicesConcatenated += ", " + similarServices.get(i);
                        }
                        generatePasswordWarningText.setText("Similar services used before found ("+ servicesConcatenated + ")! " +
                                "It's recommended to switch to the 'search' function to not misspell the service."
                        );
                        generatePasswordWarningSection.setVisibility(View.VISIBLE);
                    }else{
                        generatePasswordWarningSection.setVisibility(View.GONE);
                    }
                }else{
                    generatePasswordWarningSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });


        buttonGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets input service
                String serviceName = editTextServiceName.getText().toString().trim()
                        .toLowerCase()
                        .replaceAll("\\s+", "")
                        .replace(",", "");

                if(serviceName.isEmpty()){
                    editTextServiceName.setError("Service name cannot be empty");
                }else{
                    // Gets selected account
                    NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
                    Menu menu = navigationView.getMenu();
                    int accountHash = findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                    if(accountHash == -1){
                        Toast.makeText(v.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
                    }else{
                        boolean sameServiceFound = false;
                        List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), accountHash + ".txt");
                        for (int i = 1; i < fileContent.size(); i++) {  // Skipping the first line since that's the encrypted password
                            String[] line = fileContent.get(i);
                            // Check if the first item (main service name) in the line equals the service
                            if (line.length > 0 && line[0].equals(serviceName)) {
                                sameServiceFound = true;
                                editTextServiceName.setError("Service name already used. Switch to the 'search' page!");
                                Toast.makeText(v.getContext(), "Service name already used. Switch to the 'search' page!"
                                        , Toast.LENGTH_LONG).show();
                            }
                            // Checks if one of the aliases is equal to the service
                            for (int j = 1; j < line.length; j++) { // Skips the main service index 0
                                if (line[j].equals(serviceName)) {
                                    sameServiceFound = true;
                                    editTextServiceName.setError("Service name already used as alias of the service '"
                                            + line[0] + "'. Remove the alias through the search page or use a different name.");
                                    Toast.makeText(v.getContext(), "Service name already used as alias of the service '"
                                            + line[0] + "'. Remove the alias through the search page or use a different name."
                                            , Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        if(!sameServiceFound){
                            // Adds the service to the account's list of services
                            List<String> fileContentLines = FileHandlers.readFileLines(v.getContext(), (accountHash + ".txt"));
                            fileContentLines.add(serviceName);
                            fileAccountContent.add(new String[] { serviceName });
                            FileHandlers.writeFileLines(v.getContext(), accountHash + ".txt", fileContentLines);

                            //Toast.makeText(v.getContext(), serviceName + "'s password generated!", Toast.LENGTH_SHORT).show();
                            textViewGeneratedPasswordTitle.setText(serviceName + "'s password:");
                            generatedPasswordSection.setVisibility(View.VISIBLE);
                            buttonAddAliases.setVisibility(View.VISIBLE);

                            // Generates the password and sets it to the TextView
                            String password = EncryptionHandlers.generatePassword(String.valueOf(accountHash), serviceName);
                            textViewGeneratedPasswordShown.setText(password);
                        }

                    }
                }
            }
        });

        buttonShowPassword.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (isPasswordVisible) {
                    textViewGeneratedPasswordHidden.setVisibility(View.VISIBLE);
                    textViewGeneratedPasswordShown.setVisibility(View.GONE);
                } else {
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

                // Initialize RecyclerView within the dialog view
                RecyclerView recyclerViewAliases = dialogView.findViewById(R.id.recyclerViewAliases);
                recyclerViewAliases.setLayoutManager(new GridLayoutManager(getContext(), 2));

                // Initialize list and adapter
                List<String> aliasesList = new ArrayList<>();
                AliasesAdapter aliasesAdapter = new AliasesAdapter(aliasesList);
                recyclerViewAliases.setAdapter(aliasesAdapter);

                // Example data (you would dynamically add to this list)
                aliasesList.add("Alias 1");
                aliasesList.add("Alias 2");
                aliasesList.add("Alias 3");
                aliasesList.add("Alias 4");
                aliasesList.add("Alias 5");
                aliasesAdapter.notifyDataSetChanged();


                // TODO: other  listeners

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return rootView;
    }

    // Gets the selected account's name
    private String findSelectedMenuItemNameInGroup(Menu menu, int groupId) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getGroupId() == groupId && item.isChecked()) {
                return item.getTitle().toString();
            }
        }
        return "";
    }

    // Finds the selected item in the drawer menu
    private int findSelectedMenuItemIdInGroup(Menu menu, int groupId) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getGroupId() == groupId && item.isChecked()) {
                return item.getItemId();
            }
        }
        return -1;
    }

    // Checks the edit distance between two strings
    public int editDistance(String s1, String s2) {
        return levenshteinDistance(s1, s2);
    }
    public int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[len1][len2];
    }



}
