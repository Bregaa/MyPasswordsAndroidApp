package utils;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import it.matteobreganni.mypasswords.ui.MainActivity;

public class AccountsSettingsAdapter extends RecyclerView.Adapter<AccountsSettingsAdapter.ViewHolder> {

    private List<AccountSettingsItem> items;
    private NavigationView navigationView;

    public AccountsSettingsAdapter(List<AccountSettingsItem> items, NavigationView navigationView) {
        this.items = items;
        this.navigationView = navigationView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountSettingsItem item = items.get(position);
        holder.accountName.setText(item.getName());

        // Edit account's name button listener
        holder.editAccountNameButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View dialogView = inflater.inflate(R.layout.dialog_edit_account_name, null);

            EditText editTextNewAccountName = dialogView.findViewById(R.id.editTextNewAccountName);
            Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);

            // Sets the hint of the EditText as the previous name
            String oldName = holder.accountName.getText().toString();
            editTextNewAccountName.setHint(oldName);

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            // Confirm button listener
            buttonConfirm.setOnClickListener(view -> {
                String newName = editTextNewAccountName.getText().toString().trim();
                if (newName.isEmpty()) {  // Checks if the input is correct
                    editTextNewAccountName.setError("New name cannot be empty");
                }
                else{
                    // Checks if there is an account with the same name
                    List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), "accounts.txt");
                    List<String> newFileContent = new ArrayList<>();
                    boolean sameNameFound = false;
                    for (String[] entry : fileContent) {
                        Log.d("asd", entry[1] + " " + newName);
                        if(entry[1].equals(newName)){
                            sameNameFound = true;
                            Log.d("asd", "aaaaaa");
                        }
                        if(entry[1].equals(oldName)){
                            entry[1] = newName;
                        }
                        newFileContent.add(entry[0] + "," + entry[1]);
                    }

                    if(sameNameFound){
                        editTextNewAccountName.setError("An account with same name exists.");
                    }else{
                        // Overwrites accounts.txt with the new name
                        FileHandlers.writeFileLines(view.getContext(), "accounts.txt", newFileContent);

                        //Changes the name in the settings fragment and in the drawer menu
                        holder.accountName.setText(newName);
                        Menu menu = navigationView.getMenu();
                        MenuItem itemToUpdate = findMenuItemByName(menu, oldName);
                        if (itemToUpdate != null) {
                            itemToUpdate.setTitle(newName);
                        }

                        dialog.dismiss();
                        Toast.makeText(view.getContext(), "Account name changed!", Toast.LENGTH_SHORT).show();
                    }             }
            });

            dialog.show();
        });

        // Check password button listener
        holder.checkAccountPasswordButton.setOnClickListener(v -> {
            // Handle check button click
        });

        // Delete button listener
        holder.deleteAccountButton.setOnClickListener(v -> {
            // Handle delete button click
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accountName;
        ImageButton editAccountNameButton;
        ImageButton checkAccountPasswordButton;
        ImageButton deleteAccountButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.accountName);
            editAccountNameButton = itemView.findViewById(R.id.editAccountNameButton);
            checkAccountPasswordButton = itemView.findViewById(R.id.checkAccountPasswordButton);
            deleteAccountButton = itemView.findViewById(R.id.deleteAccountButton);
        }
    }

    // Finds the item of the drawer menu by name
    private MenuItem findMenuItemByName(Menu menu, String name) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getTitle().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
