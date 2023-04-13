package Project.FileInterface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Project.Documents.Document;
import Project.Documents.DocumentList;

public class FileInterface {
    
    private static final String filePath = "TDT4100-prosjekt-frederee/src/main/java/Project/Files/";
    
    public static void createFileNamed(
        
        String fileName,
        String extension,
        String author
        
    ) throws IOException {
        
        File newFile = new File(filePath + fileName + "." + extension);
        
        boolean success = newFile.createNewFile();
        
        if (!success) {
            throw new IOException("The file " + fileName + "." + extension + " already exists.");
        }
        
        LocalDateTime now = java.time.LocalDateTime.now();
        
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();
        
        String fileContent_plaintext = 
              extension     + "$"
            + fileName      + "$"
            + author        + "$"
            + day           + "$"
            + month         + "$"
            + year          + "$"
            + day           + "$"
            + month         + "$"
            + year          + "$"
            + "# " + fileName + "." + extension
            + "\n# Opprettet av " + author + " " + day + "/" + month + "/" + year + "."
            + "\n# Dette er en .f-kildekodefil. "
            + "\n# Åndsverkslovens kapittel 3, §§ 41-42 kan være aktuelle i forbindelse med denne filen."
            + "\n\n"
        ;
        
        String encryptedContent = Encryption.encrypt(fileContent_plaintext);
        
        BufferedWriter writer = new BufferedWriter( new FileWriter(filePath + fileName + "." + extension) );
        writer.write(encryptedContent, 0, encryptedContent.length());
        writer.close();
        
    }
    
    public static boolean fileExists(
        
        String fileName, 
        String extension
        
    ) {
        
        File fileToCheck = new File(filePath + fileName + "." + extension);
        
        return fileToCheck.exists();
        
    }
    
    public static Document getDocument(
        
        String name, 
        String extension
        
    ) throws IOException {
        
        if ( !fileExists(name, extension) ) {
            throw new IOException("The file " + name + "." + extension + " was asked for but does not exist.");
        }
        
        File file = new File(filePath + name + "." + extension);
        
        return getDocumentFromFile(file);
        
    }
    
    private static Document getDocumentFromFile(File file) throws IOException {
        
        if ( !file.canRead() ) {
            throw new IOException("Cannot read from the file.");
        }
        
        BufferedReader reader;
        reader = new BufferedReader( new FileReader(file) );
        
        StringBuilder fileContent = new StringBuilder();
        String line = reader.readLine();
        
        boolean addedLine = false;
        
        while ( line != null ) {
            fileContent.append(line + "\n");
            line = reader.readLine();
            addedLine = true;
        }
        
        // Det legges til én \n for mye når linjene addes, så vi må fjerne den siste.
        if (addedLine) {
            fileContent.delete(fileContent.length() - 1, fileContent.length());
        }
        
        reader.close();
        
        String decryptedContent = Encryption.decrypt(fileContent.toString());
        
        return new Document(decryptedContent, file.length());
        
    }
    
    /**
     * TODO: Use this in the Open File Manager
     * Return a {@code List} containing all {@code Document}s in the ../Files/ folder (where all local
     * {@code .f} etc. files are stored). Note that this function will convert from {@code File} to 
     * {@code Document} format.
     * @return Returns a {@code DocumentList} object with {@code Document} objects for each file
     * @throws IOException Throws if an error occurs during retrieval or conversion of the stored files.
     */
    public static DocumentList getAllDocuments() throws IOException {
        
        List<Document> documents = new ArrayList<Document>();
        
        File folder = new File(filePath);
        File[] folderContent = folder.listFiles();
        
        for ( File file : folderContent ) {
            documents.add(getDocumentFromFile(file));
        }
        
        return new DocumentList(documents);
        
    }
    
    public static void saveDocument(Document document) throws IOException {
        
        File file = new File(filePath + document.getFileNameWithExtension());
        
        if ( !file.exists() ) {
            throw new IOException("The file " + document.getFileNameWithExtension() + " does not exist.");
        }
        
        document.updateOpenedDate();
        
        BufferedWriter writer = new BufferedWriter( new FileWriter(file) );
        
        String content = document.getStorableString();
        String encrypted = Encryption.encrypt(content);
        
        writer.write(encrypted);
        
        writer.close();
        
    }
    
    public static void delete(String fileName, String extension) throws IOException {
        
        String path = "TDT4100-prosjekt-frederee/src/main/java/Project/Files/" + fileName + "." + extension;
        File file = new File(path);
        
        boolean didDelete = file.delete();
        
        if (!didDelete) {
            throw new IOException("Cannot delete file " + fileName + "." + extension);
        }
        
    }
    
}
