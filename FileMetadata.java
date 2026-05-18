// -----------------------------------------
// FileMetadata.java
// -----------------------------------------

public class FileMetadata {

    // Private fields - information hiding 
    private String fileName;
    private String filePath;
    private String fileType;
    private long   fileSize;

    // Constructor: initialises all metadata fields
    public FileMetadata(String fileName, String filePath,
                        String fileType, long fileSize) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    // Public getter methods - controlled access to private data
    public String getFileName()  { return fileName; }
    public String getFilePath()  { return filePath; }
    public String getFileType()  { return fileType; }
    public long   getFileSize()  { return fileSize; }

    // Returns a formatted summary string for display in the GUI
    @Override
    public String toString() {
        return "File: " + fileName +
               " | Type: " + fileType +
               " | Size: " + fileSize + " bytes";
    }
}