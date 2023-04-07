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
        
        lastRow = new UIDocumentTableEntry(
            new String[] { "Filnavn", "Forfatter", "Opprettet", "Sist åpnet", "Størrelse", "Filtype" }, 
            fontSize, 6, this
        );
        
        addChild(lastRow);
        
        List<Document> documents = docList.getDocuments_sortedBy_openDate();
        
        for ( Document doc : documents ) {
            lastRow.addBelow(doc);
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
            pressedEnter();
        }
        
    }
    
    public void pressedEnter() {
        program.beginEditing(selected.getDocument());
    }
    
    @Override
    public void afterKeyDown() {
        
        super.afterKeyDown();
        
        confirmSelection();
        
        System.out.println("Selected file: " + selected);
        
    }
    
    @Override
    public void afterMouseDown() {
        
        super.afterMouseDown();
        
        confirmSelection();
        
        System.out.println("Selected file: " + selected);
        
    }
    
    private void confirmSelection() {
        
        if (selected == null) {
            return;
        }
        
        if ( !selected.isSelected() ) {
            selected = null;
        }
        
    }
    
}
