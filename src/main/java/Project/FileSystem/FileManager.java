package Project.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
    
    private List<File> files;
    
    public FileManager() {
        
        files = new ArrayList<File>();
        
    }
    
    public void addFile(File file) {
        // check override
        files.add(file);
    }
    
}
