package Project.UIElements;

import java.util.List;

import Project.Program;
import Project.Documents.Document;
import Project.Documents.DocumentList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class UIDocumentTable extends UINode {
    
    private UIDocumentTableEntry lastRow;
    
    private UIDocumentTableEntry selected;
    
    private Program program;
    
    public UIDocumentTable(DocumentList docList, Program mainProgram, double fontSize) {
        
        super();
        
        this.program = mainProgram;
        
        List<Document> documents = docList.getDocuments_sortedBy_openDate();
        
        for ( Document doc : documents ) {
            if (lastRow == null) {
                lastRow = new UIDocumentTableEntry(fontSize, 6, doc, this);
                addChild(lastRow);
            } else {
                lastRow.addBelow(doc);
            }
        }
        
    }
    
    public void setLastRow(UIDocumentTableEntry entry) {
        lastRow = entry;
    }
    
    public void select(UIDocumentTableEntry entry) {
        
        if (selected != null) {
            selected.deactivate();
        }
        
        selected = entry;
        selected.activate();
        
    }
    
    public UIDocumentTableEntry getSelected() {
        return selected;
    }
    
    @Override
    public void keyDown(KeyEvent keyEvent) {
        
        super.keyDown(keyEvent);
        
        if ( keyEvent.getCode() == KeyCode.ENTER  &&  selected != null ) {
            program.beginEditing(selected.getDocument());
        }
        
    }
    
}
