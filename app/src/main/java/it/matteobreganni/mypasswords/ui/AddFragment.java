package it.matteobreganni.mypasswords.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.EncryptionHandlers;
import utils.FileHandlers;
import utils.OtherFunctions;
import utils.RecentServicesHandlers;

public class AddFragment extends Fragment {

    private TextView textViewGeneratePasswordAccountName;
    private TextView editTextServiceName;
    private Button buttonGeneratePassword;
    private TextView textViewGeneratedPasswordTitle;
    private Button buttonAddAliases;
    private CardView generatedPasswordSection;
    private LinearLayout generatePasswordWarningSection;
    private TextView generatePasswordWarningText;
    private EditText editTextAddServicePassword;
    private TextView textViewGeneratedPasswordShown;

    private int selectedAccountHash;
    private List<String[]> fileAccountContent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        // Gets the child fragment, the second section of the page, shared with the search fragment
        GeneratedPasswordFragment generatedPasswordFragment = (GeneratedPasswordFragment) getChildFragmentManager().findFragmentById(R.id.reusable_fragment);

        // Setting bottom menu selections
        FloatingActionButton homeFabButton = getActivity().findViewById(R.id.homeFabButton);
        BottomNavigationView bottomMenu = getActivity().findViewById(R.id.bottomNavigationView);
        homeFabButton.setImageTintList(ColorStateList.valueOf(
                getResources().getColor(R.color.bottomMenuUnselectedItem, requireActivity().getTheme())
        ));
        if(bottomMenu.getSelectedItemId() != R.id.addFragment){
            bottomMenu.setSelectedItemId(R.id.addFragment);
        }

        // Retrieve references to the UI components
        textViewGeneratePasswordAccountName = rootView.findViewById(R.id.textViewGeneratePasswordAccountName);
        editTextServiceName = rootView.findViewById(R.id.editTextServiceName);
        buttonGeneratePassword = rootView.findViewById(R.id.buttonGeneratePassword);
        textViewGeneratedPasswordTitle = generatedPasswordFragment.getView().findViewById(R.id.textViewGeneratedPasswordTitle);
        buttonAddAliases = generatedPasswordFragment.getView().findViewById(R.id.buttonAddAliases);
        generatedPasswordSection = generatedPasswordFragment.getView().findViewById(R.id.generatedPasswordSection);
        generatePasswordWarningSection = rootView.findViewById(R.id.generatePasswordWarningSection);
        generatePasswordWarningText = rootView.findViewById(R.id.generatePasswordWarningText);
        editTextAddServicePassword = rootView.findViewById(R.id.editTextAddServicePassword);
        textViewGeneratedPasswordShown = generatedPasswordFragment.getView().findViewById(R.id.textViewGeneratedPasswordShown);

        NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
        Menu menu = navigationView.getMenu();
        String selectedAccountName = OtherFunctions.findSelectedMenuItemNameInGroup(menu, R.id.drawerGroup1);
        textViewGeneratePasswordAccountName.setText("for " + selectedAccountName);

        selectedAccountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
        fileAccountContent = FileHandlers.readFileAndDivideLines(getContext(), selectedAccountHash + ".txt");


        editTextServiceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If the saved file is from a different account, get the right file (the file's content is saved in a variable to not read the file every time)
                if(OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1) != selectedAccountHash){
                    selectedAccountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                    fileAccountContent = FileHandlers.readFileAndDivideLines(getContext(), selectedAccountHash + ".txt");
                }

                String serviceName = OtherFunctions.serviceNameToStandard(s.toString());
                List<String> similarServices = new ArrayList<>();
                // If the input text is longer than 1 character
                if(serviceName.length() > 1){
                    // Creates a list of services with edit distance <= 2 compared to the input text
                    for (int i = 1; i < fileAccountContent.size(); i++) {
                        String[] line = fileAccountContent.get(i);
                        for (String str : line) {
                            if(editDistance(serviceName, str) <= 2){
                                similarServices.add(str);
                            }
                        }
                    }

                    // If similar service names were found
                    if(similarServices.size() != 0){
                        // Concatenates teh service names to be shown in the warning text
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
                String serviceName = OtherFunctions.serviceNameToStandard(editTextServiceName.getText().toString());

                if(serviceName.isEmpty()){
                    editTextServiceName.setError("Service name cannot be empty");
                }else{
                    // Gets selected account
                    NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
                    Menu menu = navigationView.getMenu();
                    int accountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                    String accountName = OtherFunctions.findSelectedMenuItemNameInGroup(menu, R.id.drawerGroup1);
                    if(accountHash == -1){
                        Toast.makeText(v.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
                    }else{
                        // Checks if the service name has already been used
                        boolean sameServiceFound = false;
                        List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), accountHash + ".txt");
                        if(OtherFunctions.serviceExists(fileContent, serviceName)){
                            sameServiceFound = true;
                            editTextServiceName.setError("Service name already used. Switch to the 'search' page!");
                            /*Toast.makeText(v.getContext(), "Service name already used. Switch to the 'search' page!"
                                    , Toast.LENGTH_LONG).show();*/
                        }else{
                            String serviceFromAlias = OtherFunctions.findServiceFromAlias(fileContent, serviceName);
                            if(!serviceFromAlias.equals("")){
                                sameServiceFound = true;
                                editTextServiceName.setError("Service name already used as alias of the service '"
                                        + serviceFromAlias + "'. Remove the alias through the search page or use a different name.");
                                /*Toast.makeText(v.getContext(), "Service name already used as alias of the service '"
                                                + serviceFromAlias + "'. Remove the alias through the search page or use a different name."
                                        , Toast.LENGTH_LONG).show();*/
                            }
                        }

                        // If the service is a new service
                        if(!sameServiceFound){
                            List<String> fileContentLines = FileHandlers.readFileLines(v.getContext(), (accountHash + ".txt"));

                            // Checks if the input password is correct (same as the encrypted saved password)
                            String savedPassword = fileContentLines.get(0);
                            String secretKey = editTextAddServicePassword.getText().toString().trim();
                            String encryptedPassword = EncryptionHandlers.encrypt(secretKey);
                            if(savedPassword.equals(encryptedPassword)){
                                OtherFunctions.hideKeyboard(v.getContext(), v.getRootView());
                                // Adds the service to the account's list of services
                                fileContentLines.add(serviceName);
                                fileAccountContent.add(new String[] { serviceName });
                                FileHandlers.writeFileLines(v.getContext(), accountHash + ".txt", fileContentLines);

                                // Add the service to the recentService file
                                RecentServicesHandlers.addRecentService(v.getContext(), serviceName, accountName);

                                //Toast.makeText(v.getContext(), serviceName + "'s password generated!", Toast.LENGTH_SHORT).show();
                                textViewGeneratedPasswordTitle.setText(serviceName + "'s password:");
                                generatedPasswordSection.setVisibility(View.VISIBLE);
                                buttonAddAliases.setVisibility(View.VISIBLE);

                                // Generates the password and sets it to the TextView
                                String password = EncryptionHandlers.generatePassword(String.valueOf(accountHash), secretKey, serviceName);
                                textViewGeneratedPasswordShown.setText(password);
                            }else{
                                editTextAddServicePassword.setError("Wrong secret key for this account");
                            }
                        }

                    }
                }
            }
        });
        return rootView;
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
