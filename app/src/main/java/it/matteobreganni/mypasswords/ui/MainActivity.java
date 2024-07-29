package it.matteobreganni.mypasswords.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import it.matteobreganni.mypasswords.R;
import it.matteobreganni.mypasswords.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void changeFabColor(boolean isHomeSelected) {
        if (isHomeSelected) {
            binding.homeFabButton.setColorFilter(R.color.lavender, PorterDuff.Mode.SRC_IN);
        } else {
            binding.homeFabButton.setColorFilter(R.color.black, PorterDuff.Mode.SRC_IN);
        }
    }
}