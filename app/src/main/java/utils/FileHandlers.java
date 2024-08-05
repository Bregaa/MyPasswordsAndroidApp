package utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileHandlers {
    public static int addAccount(Context context, String name, String email, String password){
        int emailHash = email.hashCode();

        // Selects the string to save the account as
        String account;
        if(name.isEmpty()){
            account = email;
        }else{
            account = name;
        }
        // Encrypts the password
        String encryptedPassword = EncryptionHandlers.encrypt(password);

        if(!fileExists(context, "accounts.txt")){
            // If accounts.txt doesn't exist
            createFileAndWriteLine(context, "accounts.txt", (String.valueOf(emailHash) + "," + account));
            createFileAndWriteLine(context, (String.valueOf(emailHash) + ".txt"), encryptedPassword);
            return emailHash;
        }else{
            // If accounts.txt exists
            if(!fileExists(context, (String.valueOf(emailHash) + ".txt"))) {
                // If the account's file doesn't exist
                appendLine(context, "accounts.txt", (String.valueOf(emailHash) + "," + account));
                createFileAndWriteLine(context, (String.valueOf(emailHash) + ".txt"), encryptedPassword);
                return emailHash;
            }else{
                // If the account's file exists
                Toast.makeText(context, "Account already exists", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
        //logAllFiles(context);
    }

    private static boolean fileExists(Context context, String fileName){
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    public static void createFileAndWriteLine(Context context, String fileName, String line) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            if(line != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendLine(Context context, String fileName, String line) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(line);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logAllFiles(Context context){
        File directory = context.getFilesDir();
        String[] fileNames = directory.list();
        if (fileNames != null) {
            for (String fileName : fileNames) {
                Log.d("FileList", fileName);
            }
        } else {
            Log.d("FileList", "No files found.");
        }
    }

    public static List<String[]> readFile(Context context, String fileName) {
        List<String[]> contentList = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("asd", line);
                String[] parts = line.split(",");
                contentList.add(parts);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentList;
    }
}
