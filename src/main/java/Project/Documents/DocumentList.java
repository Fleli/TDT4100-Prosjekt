package Project.Documents;

import java.util.ArrayList;
import java.util.List;

public class DocumentList {
    
    private List<Document> documents;
    
    public DocumentList ( List<Document> docs ) {
        
        if ( docs == null ) {
            throw new IllegalArgumentException("Documents passed to DocumentList must be non-null.");
        }
        
        documents = docs;
        
    }
    
    public List<Document> getDocuments() {
        return new ArrayList<Document>(documents);
    }
    
    public List<Document> getDocuments_sortedBy_openDate() {
        
        List<Document> docs = new ArrayList<Document>(documents);
        
        docs.sort( new DocumentComparator_OpenDate() );
        
        return docs;
        
    }
    
}
