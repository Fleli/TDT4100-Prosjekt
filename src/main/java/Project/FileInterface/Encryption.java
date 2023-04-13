package Project.FileInterface;

public class Encryption {
    
    public static String encrypt(String plaintextString) {
        
        StringBuilder encryptedString = new StringBuilder();
        
        for (int index = 0 ; index < plaintextString.length() ; index++) {
            
            char c_plain = plaintextString.charAt(index);
            int value_plain = Character.valueOf(c_plain);
            
            int indexBasedOffset = (index * index * (index + 1) * (index + 3)) % 16;
            
            int charIndex = (value_plain == 10) ? (1) : (value_plain - 30);
            int encryptedInt = charIndex + 34 + indexBasedOffset;
            
            String c_encrypted = new String(Character.toChars(encryptedInt));
            
            encryptedString.append(c_encrypted);
            
        }
        
        return encryptedString.toString();
        
    }
    
    public static String decrypt(String encryptedString) {
        
        // Støtter kun chars i [0-127]
        
        StringBuilder decryptedString = new StringBuilder();
        
        for (int index = 0 ; index < encryptedString.length() ; index++) {
            
            char c_encrypted = encryptedString.charAt(index);
            int encryptedInt = Character.valueOf(c_encrypted);
            
            int indexBasedOffset = (index * index * (index + 1) * (index + 3)) % 16;
            
            int charIndex = encryptedInt - 34 - indexBasedOffset;
            int decryptedInt = (charIndex == 1) ? (10) : (charIndex + 30);
            
            String c_plain = new String(Character.toChars(decryptedInt));
            
            decryptedString.append(c_plain);
            
        }
        
        return decryptedString.toString();
        
    }
    
}
