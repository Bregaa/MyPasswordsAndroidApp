package it.matteobreganni.mypasswords.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import it.matteobreganni.mypasswords.R;
import it.matteobreganni.mypasswords.databinding.ActivityMainBinding;

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

        /*Drawer menu initializations*/
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);  // Links drawer to the action bar
        binding.drawerLayout.addDrawerListener(toggle); // Toggle will handle the interactions between action bar and drawer
        toggle.syncState(); // Syncs the action bar icon to the state of the drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enables the icon to open the drawer

        // Handles the drawer menu item selections
        binding.drawerNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                // TODO: if needed
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

        // Handles the bottom menu item selections
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.add:
                    replaceFragment(new AddFragment());
                    changeFabColor(false);
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
            //binding.homeFabButton.getDrawable().setTint(getResources().getColor(R.color.primary, getTheme()));
            binding.homeFabButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.bottomMenuSelectedItem, getTheme())));
        } else {
            //binding.homeFabButton.setColorFilter(R.color.black, PorterDuff.Mode.SRC_IN);
            binding.homeFabButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.bottomMenuUnselectedItem, getTheme())));
        }
    }

    private void showAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.popup_add_account, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();

        Button submitButton = customLayout.findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle submit button click
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}