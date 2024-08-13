package utils;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class OtherFunctions {

    // Returns a standardized service name
    public static String serviceNameToStandard(String inputName){
        return inputName.trim()
                .toLowerCase()
                .replaceAll("\\s+", "") // Removes spaces
                .replace(",", "");  // Removes ','s
    }

    // Finds the selected item in the drawer menu
    public static int findSelectedMenuItemIdInGroup(Menu menu, int groupId) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getGroupId() == groupId && item.isChecked()) {
                return item.getItemId();
            }
        }
        return -1;
    }

    // Gets the selected account's name
    public static String findSelectedMenuItemNameInGroup(Menu menu, int groupId) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getGroupId() == groupId && item.isChecked()) {
                return item.getTitle().toString();
            }
        }
        return "";
    }

    // Checks if the service exists in the given fileContent
    public static boolean serviceExists(List<String[]> fileContent, String serviceName){
        for (int i = 1; i < fileContent.size(); i++) {  // Skipping the first line since that's the encrypted password
            String[] line = fileContent.get(i);
            // Check if the first item (main service name) in the line equals the service
            if (line.length > 0 && line[0].equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    // Finds teh name of the service, given an alias
    public static String findServiceFromAlias(List<String[]> fileContent, String aliasName) {
        for (int i = 1; i < fileContent.size(); i++) {  // Skipping the first line since that's the encrypted password
            String[] line = fileContent.get(i);
            // Checks if one of the aliases is equal to the service
            for (int j = 1; j < line.length; j++) { // Skips the main service index 0
                if (line[j].equals(aliasName)) {
                    return line[0];
                }
            }
        }
        return "";
    }
}
