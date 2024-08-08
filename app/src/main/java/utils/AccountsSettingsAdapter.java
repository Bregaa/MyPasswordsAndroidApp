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
            Button buttonConfirm = dialogView.findViewById(R.id.editAccountNameButtonConfirm);

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
                        if(entry[1].equals(newName)){
                            sameNameFound = true;
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
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View dialogView = inflater.inflate(R.layout.dialog_check_password_account, null);

            EditText editTextCheckAccountPassword = dialogView.findViewById(R.id.editTextCheckAccountPassword);
            Button buttonConfirm = dialogView.findViewById(R.id.checkPasswordButtonConfirm);


            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            // Confirm button listener
            buttonConfirm.setOnClickListener(view -> {
                String password = editTextCheckAccountPassword.getText().toString().trim();
                if (password.isEmpty()) {  // Checks if the input is correct
                    editTextCheckAccountPassword.setError("Password cannot be empty");
                }
                else{
                    // Checks if the encrypted input password matches the saved one
                    String encryptedPassword = EncryptionHandlers.encrypt(password);
                    String accountName = holder.accountName.getText().toString();

                    // Gets the account email's hash
                    List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), "accounts.txt");
                    String accountEmailHash = null;
                    for (String[] entry : fileContent) {
                        if(entry[1].equals(accountName)){
                            accountEmailHash = entry[0];
                            break;
                        }
                    }
                    if(accountEmailHash == null){
                        Toast.makeText(view.getContext(), "Unexpected error retrieving the saved email's hash", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        List<String> accountFileContent = FileHandlers.readFileLines(view.getContext(), (accountEmailHash + ".txt"));
                        String passwordHash = accountFileContent.get(0);
                        if(passwordHash.equals(encryptedPassword)){
                            Toast.makeText(view.getContext(), "The password is correct!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(view.getContext(), "The password is NOT correct!", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        });

        // Delete button listener
        holder.deleteAccountButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View dialogView = inflater.inflate(R.layout.dialog_delete_account, null);

            Button buttonConfirm = dialogView.findViewById(R.id.deleteAccountButtonConfirm);

            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            // Confirm button listener
            buttonConfirm.setOnClickListener(view -> {
                // Deletes the account's line in accounts.txt and the account's file
                String accountName = holder.accountName.getText().toString();

                // Gets the account email's hash and deletes the account from accounts.txt
                List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), "accounts.txt");
                List<String> newFileContent = new ArrayList<>();
                String accountEmailHash = null;
                for (String[] entry : fileContent) {
                    if(!entry[1].equals(accountName)){
                        accountEmailHash = entry[0];
                        newFileContent.add(entry[0] + "," + entry[1]);
                    }
                }
                FileHandlers.writeFileLines(view.getContext(), "accounts.txt", newFileContent);

                // Deletes account's file
                if(accountEmailHash == null){
                    Toast.makeText(view.getContext(), "Unexpected error retrieving the saved email's hash", Toast.LENGTH_SHORT).show();
                }else{
                    boolean accountDeleted = FileHandlers.deleteFile(view.getContext(), (accountEmailHash + ".txt"));
                    if(accountDeleted){
                        Toast.makeText(view.getContext(), "Account deleted!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(view.getContext(), "There was an error, account partially deleted.", Toast.LENGTH_LONG).show();
                    }

                    // Removes the account from the settings fragment and from the drawer's menu
                    Menu menu = navigationView.getMenu();
                    menu.removeItem(Integer.parseInt(accountEmailHash));
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());
                }

                dialog.dismiss();
            });

            dialog.show();
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
