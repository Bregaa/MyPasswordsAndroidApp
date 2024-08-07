package it.matteobreganni.mypasswords.ui;

import android.accounts.Account;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.AccountSettingsItem;
import utils.AccountsSettingsAdapter;
import utils.FileHandlers;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AccountsSettingsAdapter adapter;
    private List<AccountSettingsItem> items;

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

        // Initialize the accounts list
        items = new ArrayList<>();

        List<String[]> fileContent = FileHandlers.readFile(view.getContext(), "accounts.txt");
        for (String[] entry : fileContent) {
            items.add(new AccountSettingsItem(entry[1]));
        }

        // Initialize the adapter and set it to the RecyclerView
        adapter = new AccountsSettingsAdapter(items);
        recyclerView.setAdapter(adapter);
    }
}