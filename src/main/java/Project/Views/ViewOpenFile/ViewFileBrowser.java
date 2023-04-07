package Project.Views.ViewOpenFile;

import Project.Program;
import Project.Documents.DocumentList;
import Project.UIElements.UIDocumentTable;
import Project.UIElements.UISize;
import Project.Views.UIView;

public class ViewFileBrowser extends UIView {
    
    public static final double topBandHeight = 120;
    
    private static final double fontSize = 18;
    
    private UIDocumentTable table;
    private FileBrowserTopBand topBand;
    private FileBrowserLeftBand leftBand;
    
    public ViewFileBrowser(UISize size, DocumentList docList, Program mainProgram) {
        
        super(size);
        
        // TODO: Den eneste grunnen til at dette fungerer, er at leftband får keydown før table.
        // bør gjøre dette mer vanntett, f.eks. med en egen styringsmekanisme/blokkering slik at
        // table-deselect ikke nødvendigvis kjøres ved trykk på venstresiden av skjermen
        
        topBand = new FileBrowserTopBand(size, mainProgram);
        addChild(topBand);
        
        leftBand = new FileBrowserLeftBand(this);
        leftBand.setTranslateY(topBandHeight);
        addChild(leftBand);
        
        table = new UIDocumentTable(docList, mainProgram, fontSize);
        table.setTranslateY(topBandHeight);
        getChildren().add(table);
        
    }
    
    public UIDocumentTable getTable() {
        return table;
    }
    
}
