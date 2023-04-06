package Project.Documents;

import java.util.Comparator;

public class DocumentComparator_OpenDate implements Comparator<Document> {

    @Override
    public int compare(Document o1, Document o2) {
       
        if ( o1.getOpen_year() != o2.getOpen_year() ) {
            return o1.getOpen_year() - o2.getOpen_year();
        }
        
        if ( o1.getOpen_month() != o2.getOpen_month() ) {
            return o1.getOpen_month() - o2.getOpen_month();
        }
        
        return o1.getOpen_day() - o2.getOpen_day();
        
    }
    
}
