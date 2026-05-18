// -------------------------------
// KeyManager.java
// -------------------------------

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class KeyManager {

    // Private key store: maps a file name to its SecretKey
    private Map<String, SecretKey> keyStore = new HashMap<>();

    private AuditLogger auditLogger;

    // Constructor: injects the shared AuditLogger
    public KeyManager(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    // Generates a new 256-bit AES key for the given file.
    public SecretKey generateKey(String fileName)
            throws NoSuchAlgorithmException {

        // KeyGenerator is part of Java's built-in javax.crypto library
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256-bit AES key size
        SecretKey secretKey = keyGen.generateKey();

        // Store the generated key linked to the file name
        keyStore.put(fileName, secretKey);
        auditLogger.logAction(
            "Key generated for file: " + fileName);

        return secretKey;
    }

    // Retrieves the stored key for a given file name
    public SecretKey retrieveKey(String fileName) {
        SecretKey key = keyStore.get(fileName);
        if (key != null) {
            auditLogger.logAction(
                "Key retrieved for file: " + fileName);
        } else {
            auditLogger.logError(
                "No key found for file: " + fileName);
        }
        return key;
    }

    // Deletes the key associated with the given file name
    public void deleteKey(String fileName) {
        if (keyStore.containsKey(fileName)) {
            keyStore.remove(fileName);
            auditLogger.logAction(
                "Key deleted for file: " + fileName);
        } else {
            auditLogger.logError(
                "Cannot delete: key not found for " + fileName);
        }
    }

    // Checks whether a key exists for the given file name
    public boolean keyExists(String fileName) {
        return keyStore.containsKey(fileName);
    }

    // Returns the number of stored keys
    public int getKeyCount() {
        return keyStore.size();
    }
}