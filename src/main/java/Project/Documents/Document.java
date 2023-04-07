package Project.Documents;

import java.io.IOException;

public class Document {
    
    private String fileName;
    private String extension;
    
    private String author;
    
    private int create_day, create_month, create_year;
    private int open_day, open_month, open_year;
    
    private String content;
    
    private int fileSize;
    
    public Document(String file, long fileSize) throws IOException {
        
        // TODO: Sjekk først filformat (bør nok ha en egen funksjon som gjør kun dette). Deretter, basert
        // på det oppdagede filformatet, kan filen analyseres of et dokument produseres.
        
        // Men må aller først definere hvilke filformat som skal støttes, og hvordan disse skal lagres.
        
        String[] split = file.split("\\$");
        
        if ( split.length != 10 ) {
            throw new IOException("The file is corrupt (does not obey the .f file format). Split was " + split.length + ". File was " + file);
        }
        
        extension = split[0];
        fileName = split[1];
        author = split[2];
        
        try {
            create_day = Integer.valueOf(split[3]);
            create_month = Integer.valueOf(split[4]);
            create_year = Integer.valueOf(split[5]);
            open_day = Integer.valueOf(split[6]);
            open_month = Integer.valueOf(split[7]);
            open_year = Integer.valueOf(split[8]);
        } catch (Exception e) {
            throw new IOException("The format of the file's date storage is incorrect.");
        }
        
        content = split[9];
        
        this.fileSize = (int) fileSize;
        
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getContent() {
        return content;
    }
    
    public int getCreate_day() {
        return create_day;
    }
    
    public int getCreate_month() {
        return create_month;
    }
    
    public int getCreate_year() {
        return create_year;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public int getOpen_day() {
        return open_day;
    }
    
    public int getOpen_month() {
        return open_month;
    }
    
    public int getOpen_year() {
        return open_year;
    }
    
    public int getFileSize() {
        return fileSize;
    }
    
    @Override
    public String toString() {
        if (extension.equals("f")) {
            return "THIS FILE: "
                +   fileName
                +   "."
                +   extension
                +   " by "
                +   author
                +   ". Created at "
                +   create_day
                +   "/"
                +   create_month
                +   "/"
                +   create_year
                +   " and last opened at "
                +   open_day
                +   "/"
                +   open_month
                +   "/"
                +   open_year
                +   " and with content "
                +   content
                ;
        } else {
            throw new IllegalStateException("yo dude, extension is " + extension);
        }
    }
    
    public String getCreationDate_formattedString() {
        return create_day + "/" + create_month + "/" + create_year;
    }
    
    public String getOpenDate_formattedString() {
        return open_day + "/" + open_month + "/" + open_year;
    }
    
    public String getFileNameWithExtension() {
        return fileName + "." + extension;
    }
    
    public void setContent(String newContent) {
        content = newContent;
    }
    
    public String getStorableString() {
        
        return 
                    getExtension()
            + "$" + getFileName()
            + "$" + getAuthor()
            + "$" + getCreate_day()
            + "$" + getCreate_month()
            + "$" + getCreate_year()
            + "$" + getOpen_day()
            + "$" + getOpen_month()
            + "$" + getOpen_year()
            + "$" + getContent()
        ;
        
    }
    
    public String getTypeDescription() {
        
        switch (extension) {
            case "f":
                return "f-kildekodefil";
            case "fmv":
                return "fvm-utførbar fil";
            default:
                return extension + "-fil";
        }
        
    }
    
}
