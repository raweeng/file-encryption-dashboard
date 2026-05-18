// -------------------------------------------------
// EncryptionService.java
// -------------------------------------------------

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptionService {

    // The encryption algorithm used throughout the service
    private static final String ALGORITHM = "AES";

    private SecretKey secretKey;
    private AuditLogger auditLogger;

    // Constructor: receives a key and shared logger
    public EncryptionService(SecretKey secretKey,
                             AuditLogger auditLogger) {
        this.secretKey   = secretKey;
        this.auditLogger = auditLogger;
    }

    // Updates the active encryption key
    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    // Validates that a key is present before attempting encryption
    public boolean validateKey() {
        return secretKey != null;
    }

    // Encrypts a byte array using AES.
    public byte[] encrypt(byte[] data) {

        // Check the key is loaded before proceeding
        if (!validateKey()) {
            auditLogger.logError(
                "Encryption failed: no key loaded.");
            return null;
        }

        try {
            // all AES internals - no external library required.
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // The actual encryption - just one method call
            byte[] encryptedData = cipher.doFinal(data);

            auditLogger.logAction(
                "File encrypted successfully. " +
                "Output size: " + encryptedData.length + " bytes.");
            return encryptedData;

        // individually - adding 30+ lines for a single operation.
        } catch (NoSuchAlgorithmException e) {
            auditLogger.logError("Algorithm not found: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            auditLogger.logError("Padding error: " + e.getMessage());
        } catch (InvalidKeyException e) {
            auditLogger.logError("Invalid key: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            auditLogger.logError("Block size error: " + e.getMessage());
        } catch (BadPaddingException e) {
            auditLogger.logError("Bad padding: " + e.getMessage());
        }
        return null;
    }

    // Decrypts a byte array using AES with the stored key
    public byte[] decrypt(byte[] encryptedData) {

        if (!validateKey()) {
            auditLogger.logError(
                "Decryption failed: no key loaded.");
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);

            auditLogger.logAction(
                "File decrypted successfully. " +
                "Output size: " + decryptedData.length + " bytes.");
            return decryptedData;

        } catch (NoSuchAlgorithmException e) {
            auditLogger.logError("Algorithm not found: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            auditLogger.logError("Padding error: " + e.getMessage());
        } catch (InvalidKeyException e) {
            auditLogger.logError("Invalid key: " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            auditLogger.logError("Block size error: " + e.getMessage());
        } catch (BadPaddingException e) {
            auditLogger.logError("Bad padding: " + e.getMessage());
        }
        return null;
    }
}