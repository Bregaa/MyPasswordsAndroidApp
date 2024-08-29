package com.matteobreganni.mypasswords.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import com.matteobreganni.mypasswords.R;
import utils.AccountSettingsItem;
import utils.AccountsSettingsAdapter;
import utils.FileHandlers;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AccountsSettingsAdapter adapter;
    private List<AccountSettingsItem> items;
    private ImageButton settingsGithubButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsGithubButton = view.findViewById(R.id.settingsGithubButton);

        // Initialize the accounts list
        items = new ArrayList<>();

        List<String[]> fileContent = FileHandlers.readFileAndDivideLines(view.getContext(), "accounts.txt");
        for (String[] entry : fileContent) {
            items.add(new AccountSettingsItem(entry[1]));
        }

        // Initialize the adapter and set it to the RecyclerView
        NavigationView navigationView = getActivity().findViewById(R.id.drawerNavigationView);
        adapter = new AccountsSettingsAdapter(items, navigationView);
        recyclerView.setAdapter(adapter);

        // Github button listener
        settingsGithubButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://github.com/Bregaa/MyPasswordsAndroidApp"));
            startActivity(intent);
        });
    }
}