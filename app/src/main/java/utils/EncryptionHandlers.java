package utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncryptionHandlers {
    // Regular expressions for validation
    private static Pattern lowercasePattern = Pattern.compile("[a-z]");
    private static Pattern uppercasePattern = Pattern.compile("[A-Z]");
    private static Pattern numberPattern = Pattern.compile("\\d");
    private static Pattern symbolPattern = Pattern.compile("[+/-]");

    public static String encrypt(String string){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] hash = digest.digest(string.getBytes("UTF-8"));

            String base64Hash = Base64.getEncoder().encodeToString(hash);
            if (base64Hash.length() > 15) {
                base64Hash = base64Hash.substring(0, 15);
            }

            // Check for at least one lowercase letter, one uppercase letter, one number, and one symbol
            Matcher lowercaseMatcher = lowercasePattern.matcher(base64Hash);
            Matcher uppercaseMatcher = uppercasePattern.matcher(base64Hash);
            Matcher numberMatcher = numberPattern.matcher(base64Hash);
            Matcher symbolMatcher = symbolPattern.matcher(base64Hash);
            if(!(lowercaseMatcher.find() && uppercaseMatcher.find() && numberMatcher.find() && symbolMatcher.find())){
                Log.d("EncryptionHandlers.encrypt()", "Trying again...");
                return encrypt(base64Hash);
            }

            Log.d("EncryptionHandlers.encrypt()", base64Hash);
            return base64Hash;
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            Log.e("EncryptionHandlers.encrypt()", e.toString());
            throw new RuntimeException(e);
        }
    }
}
