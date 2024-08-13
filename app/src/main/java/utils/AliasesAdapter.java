package utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import it.matteobreganni.mypasswords.R;
import it.matteobreganni.mypasswords.ui.MainActivity;

public class AliasesAdapter extends RecyclerView.Adapter<AliasesAdapter.AliasViewHolder> {

    private final List<String> aliases;
    private Fragment fragment;

    public AliasesAdapter(List<String> aliases, Fragment fragment) {
        this.aliases = aliases;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public AliasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alias, parent, false);
        return new AliasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AliasViewHolder holder, int position) {
        String alias = aliases.get(position);
        holder.textViewAlias.setText(alias);
        holder.buttonDeleteAlias.setOnClickListener(v -> {
            // Removes the alias from the file and recyclerview
            EditText editTextServiceName = fragment.getView().findViewById(R.id.editTextServiceName);
            TextView textViewGeneratedPasswordTitle = fragment.getView().findViewById(R.id.textViewGeneratedPasswordTitle);
            String serviceName = textViewGeneratedPasswordTitle.getText().toString().trim().substring(0,
                    textViewGeneratedPasswordTitle.getText().toString().trim().length() - 12);
            Log.d("asd", serviceName);
            NavigationView navigationView = fragment.getActivity().findViewById(R.id.drawerNavigationView);
            Menu menu = navigationView.getMenu();
            int accountHash = OtherFunctions.findSelectedMenuItemIdInGroup(menu, R.id.drawerGroup1);
            if(accountHash == -1){
                Toast.makeText(v.getContext(), "Unexpected error getting the selected account", Toast.LENGTH_SHORT).show();
            }else{
                List<String[]> fileContent = FileHandlers.readFileAndDivideLines(v.getContext(), accountHash + ".txt");
                boolean found = false;
                int lineIndex = 0;
                String newLine = "";
                for (int i = 1; i < fileContent.size(); i++) {
                    String[] line = fileContent.get(i);
                    if(line[0].equals(serviceName)){
                        newLine = line[0];
                        for(int j = 1; j < line.length; j++){
                            if(!line[j].equals(alias)){
                                newLine = newLine + "," + line[j];
                            }else{
                                found = true;
                            }
                        }
                        lineIndex = i;
                        break;
                    }
                }
                if(!found || lineIndex == 0){
                    Toast.makeText(v.getContext(), "Unexpected error finding the alias", Toast.LENGTH_SHORT).show();
                }else{
                    // Removes the alias from the file
                    List<String> fileContentLines = FileHandlers.readFileLines(v.getContext(), (accountHash + ".txt"));
                    fileContentLines.set(lineIndex, newLine);
                    FileHandlers.writeFileLines(v.getContext(), accountHash + ".txt", fileContentLines);

                    // Removes the alias's item from the recyclerview
                    aliases.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, aliases.size());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return aliases.size();
    }

    static class AliasViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAlias;
        ImageButton buttonDeleteAlias;

        AliasViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAlias = itemView.findViewById(R.id.textViewAlias);
            buttonDeleteAlias = itemView.findViewById(R.id.buttonDeleteAlias);
        }
    }
}