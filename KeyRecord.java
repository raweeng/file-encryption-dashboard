// ----------------------------------
// KeyRecord.java
// ----------------------------------

public class KeyRecord {

    private int    keyId;
    private String fileName;
    private byte[] encryptedKey;
    private String createdDate;
    private String ownerUsername;

    // Constructor: creates a complete key record
    public KeyRecord(int keyId, String fileName,
                     byte[] encryptedKey, String createdDate,
                     String ownerUsername) {
        this.keyId         = keyId;
        this.fileName      = fileName;
        this.encryptedKey  = encryptedKey;
        this.createdDate   = createdDate;
        this.ownerUsername = ownerUsername;
    }

    // Getters: public interface to private data
    public int    getKeyId()         { return keyId; }
    public String getFileName()      { return fileName; }
    public byte[] getEncryptedKey()  { return encryptedKey; }
    public String getCreatedDate()   { return createdDate; }
    public String getOwnerUsername() { return ownerUsername; }
}