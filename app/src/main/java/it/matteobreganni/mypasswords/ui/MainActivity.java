package it.matteobreganni.mypasswords.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
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

import it.matteobreganni.mypasswords.R;
import it.matteobreganni.mypasswords.databinding.ActivityMainBinding;
import utils.EncryptionHandlers;
import utils.FileHandlers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*Setting default stuff*/
        replaceFragment(new HomeFragment());    // Set the default fragment
        binding.drawerNavigationView.getMenu().getItem(0).setChecked(true);; // Set the default selected item of the drawer menu
        binding.bottomNavigationView.setSelectedItemId(R.id.home);  // Set the default selected item of the bottom menu
        binding.bottomNavigationView.setBackground(null);
        initializeDrawerMenuAccountsList(this); // Initializes the drawer menu's items with the saved accounts

        /*Drawer menu initializations*/
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);  // Links drawer to the action bar
        binding.drawerLayout.addDrawerListener(toggle); // Toggle will handle the interactions between action bar and drawer
        toggle.syncState(); // Syncs the action bar icon to the state of the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enables the icon to open the drawer

        // Handles the drawer menu item selections
        binding.drawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                TextView textView = findViewById(R.id.textView);
                textView.setText(String.valueOf(item.getItemId()));

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
                binding.drawerLayout.closeDrawer(GravityCompat.START);

                binding.bottomNavigationView.setSelectedItemId(R.id.home);
                changeFabColor(false);
                replaceFragment(new SettingsFragment());
            }
        });

        // Handles the bottom menu item selections
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.add:
                    replaceFragment(new AddFragment());
                    changeFabColor(false);
                    EncryptionHandlers.encrypt("as");
                    break;

                case R.id.search:
                    replaceFragment(new SearchFragment());
                    changeFabColor(false);
                    break;

                case R.id.home:
                    replaceFragment(new HomeFragment());
                    changeFabColor(true);
                    break;
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

    // Handles the opening of the drawer menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Replaces the fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
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