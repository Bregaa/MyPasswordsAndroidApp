package it.matteobreganni.mypasswords.ui;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.EncryptionHandlers;
import utils.FileHandlers;
import utils.OtherFunctions;

public class RetrievePasswordFragment extends Fragment {

    private TextView hiddenServiceName;
    private TextView textViewRetrievePasswordAccountName;
    private TextView textViewRetrievePasswordTitle;
    private Button buttonRetrievePassword;
    private EditText editTextRetrieveServicePassword;

    private TextView textViewGeneratedPasswordTitle;
    private CardView generatedPasswordSection;
    private Button buttonAddAliases;
    private TextView textViewGeneratedPasswordShown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_retrieve_password, container, false);

        // Gets the child fragment, the second section of the page, shared with the search fragment
        GeneratedPasswordFragment generatedPasswordFragment = (GeneratedPasswordFragment) getChildFragmentManager().findFragmentById(R.id.reusable_fragment);

        hiddenServiceName = rootView.findViewById(R.id.hiddenServiceName);
        textViewRetrievePasswordTitle = rootView.findViewById(R.id.textViewRetrievePasswordTitle);
        textViewRetrievePasswordAccountName = rootView.findViewById(R.id.textViewRetrievePasswordAccountName);
        buttonRetrievePassword = rootView.findViewById(R.id.buttonRetrievePassword);
        editTextRetrieveServicePassword = rootView.findViewById(R.id.editTextRetrieveServicePassword);

        textViewGeneratedPasswordTitle = generatedPasswordFragment.getView().findViewById(R.id.textViewGeneratedPasswordTitle);
        generatedPasswordSection = generatedPasswordFragment.getView().findViewById(R.id.generatedPasswordSection);
        buttonAddAliases = generatedPasswordFragment.getView().findViewById(R.id.buttonAddAliases);
        textViewGeneratedPasswordShown = generatedPasswordFragment.getView().findViewById(R.id.textViewGeneratedPasswordShown);

        NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
        Menu menu = navigationView.getMenu();
        String selectedAccountName = OtherFunctions.findSelectedMenuItemNameInGroup(menu, R.id.drawerGroup1);
        textViewRetrievePasswordAccountName.setText("for " + selectedAccountName);

        // Sets the service name from the data sent by the previous fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            String serviceName = bundle.getString("serviceName");
            if (serviceName != null) {
                hiddenServiceName.setText(serviceName);
                textViewRetrievePasswordTitle.setText("Get " + serviceName + "'s password");
            }else{
                Toast.makeText(rootView.getContext(), "Unexpected error getting the service name (1)", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        }else{
            Toast.makeText(rootView.getContext(), "Unexpected error getting the service name (2)", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }

        buttonRetrievePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceName = OtherFunctions.serviceNameToStandard(hiddenServiceName.getText().toString().trim());
                // Gets selected account
                NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
                Menu menu = navigationView.getMenu();
                int accountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
                if(accountHash == -1){
                    Toast.makeText(v.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
                }else{
                    // Checks if the input password is correct (same as the encrypted saved password)
                    List<String> fileContentLines = FileHandlers.readFileLines(v.getContext(), (accountHash + ".txt"));
                    String savedPassword = fileContentLines.get(0);
                    String secretKey = editTextRetrieveServicePassword.getText().toString().trim();
                    String encryptedPassword = EncryptionHandlers.encrypt(secretKey);
                    if(savedPassword.equals(encryptedPassword)){
                        textViewGeneratedPasswordTitle.setText(serviceName + "'s password:");
                        generatedPasswordSection.setVisibility(View.VISIBLE);
                        buttonAddAliases.setVisibility(View.VISIBLE);

                        // Generates the password and sets it to the TextView
                        String password = EncryptionHandlers.generatePassword(String.valueOf(accountHash), secretKey, serviceName);
                        textViewGeneratedPasswordShown.setText(password);
                    }else{
                        editTextRetrieveServicePassword.setError("Wrong secret key for this account");
                    }
                }
            }
        });


        return rootView;
    }
}