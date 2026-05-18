// -----------------------------------------------
// FileHandler.java
// -----------------------------------------------

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    // Tracks the currently selected input and output file paths
    private String currentFilePath;
    private String outputFilePath;

    private AuditLogger auditLogger;

    // Constructor
    public FileHandler(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    // Sets the selected file path and validates it exists
    public void chooseFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            this.currentFilePath = filePath;
            auditLogger.logAction(
                "File selected: " + filePath);
        } else {
            auditLogger.logError(
                "File not found: " + filePath);
            this.currentFilePath = null;
        }
    }

    // Reads the selected file and returns its bytes.
    // IOException is a checked exception - must be declared.
    public byte[] readFile() throws IOException {
        if (currentFilePath == null) {
            throw new IOException("No file has been selected.");
        }
        Path path = Paths.get(currentFilePath);
        byte[] data = Files.readAllBytes(path);
        auditLogger.logAction(
            "File read: " + data.length + " bytes loaded.");
        return data;
    }

    // Writes encrypted bytes to a new file with .enc extension
    public boolean writeEncryptedFile(byte[] data)
            throws IOException {
        if (currentFilePath == null) {
            auditLogger.logError(
                "Cannot write: no source file selected.");
            return false;
        }
        outputFilePath = currentFilePath + ".enc";
        Path outPath = Paths.get(outputFilePath);
        Files.write(outPath, data);
        auditLogger.logAction(
            "Encrypted file written: " + outputFilePath);
        return true;
    }

    // Writes decrypted bytes to a file with .dec extension
    public boolean writeDecryptedFile(byte[] data)
            throws IOException {
        if (currentFilePath == null) {
            auditLogger.logError(
                "Cannot write: no source file selected.");
            return false;
        }
        outputFilePath = currentFilePath + ".dec";
        Path outPath = Paths.get(outputFilePath);
        Files.write(outPath, data);
        auditLogger.logAction(
            "Decrypted file written: " + outputFilePath);
        return true;
    }

    // Builds and returns a FileMetadata object for the current file
    public FileMetadata getFileMetadata() {
        if (currentFilePath == null) return null;
        File file = new File(currentFilePath);
        String name = file.getName();
        String type = name.contains(".")
            ? name.substring(name.lastIndexOf('.') + 1)
            : "unknown";
        return new FileMetadata(
            name, currentFilePath, type, file.length());
    }

    // Getters
    public String getCurrentFilePath() { return currentFilePath; }
    public String getOutputFilePath()  { return outputFilePath; }
}