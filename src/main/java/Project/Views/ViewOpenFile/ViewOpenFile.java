package Project.Views.ViewOpenFile;

import Project.Program;
import Project.Documents.DocumentList;
import Project.UIElements.UIDocumentTable;
import Project.UIElements.UISize;
import Project.Views.UIView;

public class ViewOpenFile extends UIView {
    
    private static final double fontSize = 18;
    
    public ViewOpenFile(UISize size, DocumentList docList, Program mainProgram) {
        
        super(size);
        
        UIDocumentTable table = new UIDocumentTable(docList, mainProgram, fontSize);
        table.setTranslateX(100);
        table.setTranslateY(100);
        getChildren().add(table);
        
    }
    
    
    
}
