package utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RecentServicesHandlers {
    public static void addRecentService(Context context, String serviceName, String accountName){
        deleteRecentService(context, serviceName, accountName); // If the service was already in the list, removes it before adding it at the top
        String newRecentService = serviceName + "," + accountName;
        List<String> fileContent = new ArrayList<>();
        if(FileHandlers.fileExists(context, "recentServices.txt")){
            fileContent = FileHandlers.readFileLines(context, "recentServices.txt");
            if(fileContent.size() < 10){
                fileContent.add(0, newRecentService);
            }else{
                fileContent.add(0, newRecentService);
                fileContent.remove(fileContent.size() - 1);
            }
        }else{
            fileContent.add(newRecentService);
        }
        FileHandlers.writeFileLines(context, "recentServices.txt", fileContent);
    }

    public static void deleteRecentService(Context context, String serviceName, String accountName){
        if(FileHandlers.fileExists(context, "recentServices.txt")){
            List<String> fileContent = FileHandlers.readFileLines(context, "recentServices.txt");
            boolean serviceFound = false;
            for (int i = 0; i < fileContent.size(); i++){
                String line = fileContent.get(i);
                Log.d("deleteRecentService", line);
                if(line.equals(serviceName + "," + accountName)){
                    fileContent.remove(i);
                    serviceFound = true;
                }
            }
            if(serviceFound){
                FileHandlers.writeFileLines(context, "recentServices.txt", fileContent);
            }
        }
    }

    public static void deleteRecentAccountServices(Context context, String accountName){
        if(FileHandlers.fileExists(context, "recentServices.txt")){
            List<String> fileContentLines = FileHandlers.readFileLines(context, "recentServices.txt");
            List<String[]> fileContent = FileHandlers.readFileAndDivideLines(context, "recentServices.txt");
            Log.d("asd", String.valueOf(fileContentLines.size()));
            Log.d("asd", String.valueOf(fileContent.size()));
            int howManyServicesRemoved = 0;
            boolean serviceFound = false;
            for (int i = 0; i < fileContent.size(); i++){
                String[] line = fileContent.get(i);
                if(line[1].equals(accountName)){
                    fileContentLines.remove(i - howManyServicesRemoved);
                    howManyServicesRemoved++;
                    serviceFound = true;
                }
            }
            if(serviceFound){
                FileHandlers.writeFileLines(context, "recentServices.txt", fileContentLines);
            }
        }
    }

    public static void editRecentServicesAccountNames(Context context, String oldAccountName, String newAccountName){
        if(FileHandlers.fileExists(context, "recentServices.txt")){
            List<String> fileContentLines = FileHandlers.readFileLines(context, "recentServices.txt");
            List<String[]> fileContent = FileHandlers.readFileAndDivideLines(context, "recentServices.txt");
            boolean somethingChanged = false;
            for (int i = 0; i < fileContent.size(); i++){
                String[] line = fileContent.get(i);
                if(line[1].equals(oldAccountName)){
                    fileContentLines.set(i, line[0] + "," + newAccountName);
                    somethingChanged = true;
                }
            }
            if(somethingChanged){
                FileHandlers.writeFileLines(context, "recentServices.txt", fileContentLines);
            }
        }
    }
}
