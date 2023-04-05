package Project.FileSystem;

public class File {
    
    private String content;
    private String fileName;
    private String extension;
    
    public File ( String fileName, String extension ) {
        this.fileName = fileName;
        this.extension = extension;
    }
    
    public void setContent(String newContent) {
        this.content = newContent;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getExtension() {
        return extension;
    }
    
}
