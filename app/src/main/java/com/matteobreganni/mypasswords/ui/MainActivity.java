package com.matteobreganni.mypasswords.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import com.matteobreganni.mypasswords.R;
import com.matteobreganni.mypasswords.databinding.ActivityMainBinding;
import utils.FileHandlers;
import utils.OtherFunctions;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;

    private static final int BACK_PRESS_INTERVAL = 2000;
    private long lastBackPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if the introduction has been shown fully
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean hasIntroductionBeenShown = prefs.getBoolean("hasIntroductionBeenShown", false);
        if (!hasIntroductionBeenShown) {
            // Launch IntroductionActivity
            Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Setting default stuff
        replaceFragment(new HomeFragment(), "HomeFragment");    // Set the default fragment
        binding.drawerNavigationView.getMenu().getItem(0).setChecked(true);; // Set the default selected item of the drawer menu
        binding.bottomNavigationView.setSelectedItemId(R.id.home);  // Set the default selected item of the bottom menu
        binding.bottomNavigationView.setBackground(null);
        initializeDrawerMenuAccountsList(this); // Initializes the drawer menu's items with the saved accounts

        // Drawer menu initializations
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);  // Links drawer to the action bar
        binding.drawerLayout.addDrawerListener(toggle); // Toggle will handle the interactions between action bar and drawer
        toggle.syncState(); // Syncs the action bar icon to the state of the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enables the icon to open the drawer

        // Handles the drawer menu item selections
        binding.drawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Check if the fragment "fragment_add" is currently being shown
                Fragment fragment = fragmentManager.findFragmentByTag("AddFragment");
                if (fragment != null && fragment.isVisible()) {
                    // Find the TextView within the fragment and updates its text
                    TextView textViewGeneratePasswordAccountName = fragment.getView().findViewById(R.id.textViewGeneratePasswordAccountName);
                    if (textViewGeneratePasswordAccountName != null) {
                        textViewGeneratePasswordAccountName.setText("for " + item.getTitle());
                    }
                    EditText serviceNameEditText = fragment.getView().findViewById(R.id.editTextServiceName);
                    serviceNameEditText.setText("");
                    EditText editTextAddServicePassword = fragment.getView().findViewById(R.id.editTextAddServicePassword);
                    editTextAddServicePassword.setText("");
                    CardView generatedPasswordArea = fragment.getView().findViewById(R.id.generatedPasswordSection);
                    generatedPasswordArea.setVisibility(View.GONE);
                    Button aliasesButton = fragment.getView().findViewById(R.id.buttonAddAliases);
                    aliasesButton.setVisibility(View.GONE);
                }
                // Check if the fragment "fragment_search" is currently being shown
                fragment = fragmentManager.findFragmentByTag("SearchFragment");
                if (fragment != null && fragment.isVisible()) {
                    // Find the TextView within the fragment and updates its text
                    TextView textViewSearchPasswordAccountName = fragment.getView().findViewById(R.id.textViewSearchPasswordAccountName);
                    if (textViewSearchPasswordAccountName != null) {
                        textViewSearchPasswordAccountName.setText("for " + item.getTitle());
                    }
                    EditText editTextSearchServiceName = fragment.getView().findViewById(R.id.editTextSearchServiceName);
                    editTextSearchServiceName.setText("");

                }
                // Check if the fragment "fragment_retrieve_password" is currently being shown
                fragment = fragmentManager.findFragmentByTag("RetrievePasswordFragment");
                if (fragment != null && fragment.isVisible()) {
                    // Find the TextView within the fragment and updates its text
                    TextView textViewRetrievePasswordAccountName = fragment.getView().findViewById(R.id.textViewRetrievePasswordAccountName);
                    EditText editTextRetrieveServicePassword = fragment.getView().findViewById(R.id.editTextRetrieveServicePassword);
                    editTextRetrieveServicePassword.setText("");
                    CardView generatedPasswordArea = fragment.getView().findViewById(R.id.generatedPasswordSection);
                    generatedPasswordArea.setVisibility(View.GONE);
                    Button aliasesButton = fragment.getView().findViewById(R.id.buttonAddAliases);
                    aliasesButton.setVisibility(View.GONE);

                    //Go back to previous fragment
                    getSupportFragmentManager().popBackStack();
                }


                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //Handles the drawer menu's "add account" button
        MenuItem addButton = binding.drawerNavigationView.getMenu().findItem(R.id.addAccount);
        addButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAddAccountDialog();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //Handles the drawer menu's "settings" button
        MaterialButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationView navigationView = findViewById(R.id.drawerNavigationView);
                Menu menu = navigationView.getMenu();
                if(menu.size() == 1){
                    Toast.makeText(v.getContext(), "First, create an account!", Toast.LENGTH_SHORT).show();
                }else{
                    binding.drawerLayout.closeDrawer(GravityCompat.START);

                    binding.bottomNavigationView.setSelectedItemId(R.id.home);
                    changeFabColor(false);
                    replaceFragment(new SettingsFragment(), "SettingsFragment");
                }
            }
        });

        // Handles the bottom menu item selections
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            NavigationView navigationView = this.findViewById(R.id.drawerNavigationView);
            Menu menu = navigationView.getMenu();
            if(menu.size() == 1){
                Toast.makeText(this, "First, create an account!", Toast.LENGTH_SHORT).show();
                if(item.getItemId() != R.id.home){
                    changeFabColor(false);
                }else{
                    changeFabColor(true);
                }
            }else {
                switch (item.getItemId()) {
                    case R.id.add:
                        replaceFragment(new AddFragment(), "AddFragment");
                        break;

                    case R.id.search:
                        replaceFragment(new SearchFragment(), "SearchFragment");
                        break;

                    case R.id.home:
                        replaceFragment(new HomeFragment(), "HomeFragment");
                        break;
                }
            }
            return true;
        });
        binding.homeFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNavigationView.setSelectedItemId(R.id.home);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // If there are fragments in the back stack, pop the back stack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            // Otherwise, implement double back press to exit
            if (lastBackPressedTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();  // Close app
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                lastBackPressedTime = System.currentTimeMillis();
            }
        }
    }

    // Handles the opening of the drawer menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Replaces the fragment
    private void replaceFragment(Fragment fragment, String fragmentName){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, fragmentName);
        fragmentTransaction.commit();
    }

    // Changes the color of the fab's drawable when selected / not selected, to be in sync with the bottom menu
    private void changeFabColor(boolean isHomeSelected) {
        if (isHomeSelected) {
            binding.homeFabButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.bottomMenuSelectedItem, getTheme())));
        } else {
            binding.homeFabButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.bottomMenuUnselectedItem, getTheme())));
        }
    }

    // Shows the popup to add an account and manages it
    private void showAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_account, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        Button submitButton = customLayout.findViewById(R.id.buttonSubmitNewAccount);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handles the popup's new account submit button
                OtherFunctions.hideKeyboard(v.getContext(), v.getRootView());

                EditText nameEditText = customLayout.findViewById(R.id.editTextAccountName);
                EditText emailEditText = customLayout.findViewById(R.id.editTextAccountEmail);
                EditText passwordEditText = customLayout.findViewById(R.id.editTextAccountPassword);
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty()) {  // Checks if the input is correct
                    emailEditText.setError("Email cannot be empty");
                } else if (password.isEmpty()){
                    passwordEditText.setError("Password cannot be empty");
                } else {
                    // Decides the account's name
                    String account;
                    if(name.isEmpty()){
                        account = email;
                    }else{
                        account = name;
                    }

                    // Checks if there is an account with the same name
                    boolean isModified = false;
                    List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), "accounts.txt");
                    for (String[] entry : fileContent) {
                        if(entry[1].equals(account)){
                            isModified = true;
                        }
                    }
                    if(isModified){
                        if(name.isEmpty()){
                            emailEditText.setError("An account with same name exists.");
                        }else{
                            nameEditText.setError("An account with same name exists.");
                        }
                    }else{
                        // Adds the account to the local files and to the drawer menu
                        int fileHash = FileHandlers.addAccount(v.getContext(), name, email, password);
                        if(fileHash != 0){
                            Menu menu = binding.drawerNavigationView.getMenu();
                            addAccountToDrawer(menu, fileHash, account, true);
                        }
                        OtherFunctions.switchToHomeFragmentAndClearStack(v.getContext());
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Account added!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        dialog.show();
    }

    // Methods to add accounts to the drawer menu
    private void initializeDrawerMenuAccountsList(Context context){
        Menu menu = binding.drawerNavigationView.getMenu();

        boolean first = false;
        List<String[]> fileContent = FileHandlers.readFileAndDivideLines(context, "accounts.txt");
        for (String[] entry : fileContent) {
            if(!first){
                first = true;
                addAccountToDrawer(menu, Integer.parseInt(entry[0]), entry[1], true);
            }else{
                addAccountToDrawer(menu, Integer.parseInt(entry[0]), entry[1], false);
            }
        }
    }
    private void addAccountToDrawer(Menu menu, int itemId, String itemName, boolean checked){
        MenuItem newItem = menu.add(R.id.drawerGroup1, itemId, 0, itemName);
        newItem.setCheckable(true);
        if(checked){
            newItem.setChecked(true);
        }
    }


}