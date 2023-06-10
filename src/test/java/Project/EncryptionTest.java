package Project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Project.FileInterface.Encryption;

public class EncryptionTest {
    
    String plaintext;
    String encrypted;
    String decrypted;
    
    @BeforeEach
    public void setup() {
        plaintext = "Dette er en skikkelig\ntrygg kryptering";
    }
    
    @Test
    public void test_encryptAndDecrypt() {
        
        encrypted = Encryption.encrypt(plaintext);
        decrypted = Encryption.decrypt(encrypted);
        
        assertEquals(decrypted, plaintext);
        
    }
    
    @Test
    public void test_encrypt_in_correct_range() {
        
        plaintext = "\n #Dette! er+et-mer*komplisert/eksempel^med[noen]{flere}<tegn>@som$ALLE&ligger:i:riktig;intervall";
        encrypted = Encryption.encrypt(plaintext);
        
        for (char c : encrypted.toCharArray()) {
            
            int ascii = Character.valueOf(c);
            
            assertTrue(ascii >= 32 || ascii <= 126);
            
        }
        
    }
    
    @Test
    public void test_decrypt_complex_string() {
        
        plaintext = "\n #Dette! er+et-mer*komplisert/eksempel^med[noen]{flere}<tegn>@som$ALLE&ligger:i:riktig;intervall";
        encrypted = Encryption.encrypt(plaintext);
        decrypted = Encryption.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
        
    }
    
}
