package it.matteobreganni.mypasswords.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.AliasesAdapter;

public class AddFragment extends Fragment {

    private Button buttonGeneratePassword;
    private Button buttonCopyPassword;
    private Button buttonAddAliases;
    private ImageButton buttonShowPassword;
    private TextView textViewGeneratedPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        // Retrieve references to the UI components
        buttonGeneratePassword = rootView.findViewById(R.id.buttonGeneratePassword);
        buttonCopyPassword = rootView.findViewById(R.id.buttonCopyPassword);
        buttonAddAliases = rootView.findViewById(R.id.buttonAddAliases);
        buttonShowPassword = rootView.findViewById(R.id.buttonShowPassword);
        textViewGeneratedPassword = rootView.findViewById(R.id.textViewGeneratedPassword);

        // Set onClickListeners for each button
        buttonGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle password generation logic here
                String generatedPassword = "GeneratedPassword123"; // Replace with actual password generation logic
                textViewGeneratedPassword.setText(generatedPassword);
                Toast.makeText(getActivity(), "Password generated", Toast.LENGTH_SHORT).show();
            }
        });

        buttonShowPassword.setOnClickListener(new View.OnClickListener() {
            private boolean isPasswordVisible = false;

            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (isPasswordVisible) {
                    textViewGeneratedPassword.setText("***************");
                } else {
                    textViewGeneratedPassword.setText("GeneratedPassword123"); // Replace with actual password
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });

        buttonCopyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle copying password logic here
                Toast.makeText(getActivity(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
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
}
