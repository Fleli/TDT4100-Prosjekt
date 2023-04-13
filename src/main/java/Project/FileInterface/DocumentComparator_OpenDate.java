package Project.FileInterface;

import java.util.Comparator;

public class DocumentComparator_OpenDate implements Comparator<Document> {

    @Override
    public int compare(Document o1, Document o2) {
       
        if ( o1.getOpen_year() != o2.getOpen_year() ) {
            return o2.getOpen_year() - o1.getOpen_year();
        }
        
        if ( o1.getOpen_month() != o2.getOpen_month() ) {
            return o2.getOpen_month() - o1.getOpen_month();
        }
        
        return o2.getOpen_day() - o1.getOpen_day();
        
    }
    
}
