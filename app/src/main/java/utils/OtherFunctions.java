package utils;

import android.view.Menu;
import android.view.MenuItem;

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
}
