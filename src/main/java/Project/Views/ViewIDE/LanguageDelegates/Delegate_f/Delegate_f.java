package Project.Views.ViewIDE.LanguageDelegates.Delegate_f;

import java.util.List;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.Compiler.CompilerProfiler;
import Project.Compiler.Compiler.Error;
import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.Compiler.Lexer.Token;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;
import Project.VirtualMachine.Heap.VMHeapArea;
import Project.UIElements.UIAction;
import Project.UIElements.UICodeLine;
import Project.UIElements.UILabel;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.ViewIDE.DebugArea.ConsoleView;
import Project.Views.ViewIDE.IDEs.IDE;
import Project.Views.ViewIDE.LanguageDelegates.LanguageDelegate;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class Delegate_f implements LanguageDelegate {
    
    public static final int default_stack_size = 1024;
    public static final int default_heap_size = 1024;
    
    private IDE ide;
    
    private Runtime debugRuntime;
    private DebugRegion debugRegion;
    private VMDebugger debugger;
    private DebugArea_f debugArea;
    
    private UpperBand_f upperBand;
    private LowerBand_f lowerBand;
    
    public Delegate_f(IDE ide) {
        
        this.ide = ide;
        
        debugger = new VMDebugger();
        
        UISize debugAreaViewSize = ide.getDebugAreaViewSize();
        debugArea = new DebugArea_f(debugAreaViewSize.width, debugAreaViewSize.height, Color.rgb(150, 160, 200));
        
    }
    
    public UINode getDebugArea() {
        return debugArea;
    }
    
    public void run(String sourceCode, int stack_size, int heap_size, UICodeLine topLine) {
        
        killDebugger(topLine);
        
        clearConsole();
        ConsoleView console = getConsole();
        
        Compiler compiler = new Compiler();
        compiler.compile(sourceCode, true, 1);
        
        CompilerProfiler profiler = compiler.getProfiler();
        lowerBand.refreshCompileProfiling(profiler);
        
        if (compiler.getExecutable() == null) {
            console.print("Cannot run due to unresolved\ncompilation errors.", Color.PINK, "-fx-font-weight: bold;");
            return;
        }
        
        Runtime runtime = new Runtime(compiler.getExecutable(), stack_size, heap_size, console);
        Runtime.printDebugInfo = false;
        
        try {
            
            runtime.run();
            
            List<VMHeapArea> leaks = runtime.getHeapUsage();
            
            debugArea.finishedRun(leaks);
            
        } catch (VMException exception) {
            
            debugArea.notifyException(exception);
            System.out.println("Runtime exception: " + exception.getLocalizedMessage());
            
        }
        
    }
    
    @Override
    public void run(UICodeLine topLine) {
        String sourceCode = ide.getContent();
        run(sourceCode, default_stack_size, default_heap_size, topLine);
    }
    
    public void initDebugger(String sourceCode, int stack_size, int heap_size, UICodeLine topLine) {
        
        debugArea.clear();
        ConsoleView console = getConsole();
        
        Compiler compiler = new Compiler();
        compiler.compile(sourceCode, true, 1);
        
        debugger = new VMDebugger();
        debugArea.setDebugger(debugger);
        debugRuntime = new Runtime(compiler.getExecutable(), stack_size, heap_size, console, debugger);
        // Do something to UI, so that the debugger is visible & usable
        
        debugger_nextClock(topLine);
        
    }
    
    public void killDebugger(UICodeLine topLine) {
        debugger = null;
        debugRuntime = null;
        debugRegion = null;
        debugArea.setDebugger(null);
        topLine.syntaxHighlightAll();
    }
    
    public void debug(UICodeLine topLine) {
        String sourceCode = ide.getContent();
        initDebugger(sourceCode, default_stack_size, default_heap_size, topLine);
    }
    
    public void debugger_nextClock(UICodeLine topLine) {
        
        if (debugRuntime == null) {
            // TODO: Gjør noe med UI her
            return;
        }
        
        try {
            
            debugRuntime.clock();
            
            debugRegion = debugRuntime.getActiveDebugRegion();
            
            topLine.syntaxHighlightAll();
            
            debugArea.refresh();
            
        } catch (VMException exception) {
            
            killDebugger(topLine);
            debugArea.notifyException(exception);
            
        }
        
    }
    
    public void syntaxHighlight(UICodeLine line) {
        
        String text = line.getText();
        double fontSize = line.getFontSize();
        
        Compiler compiler = new Compiler();
        
        UILabel syntaxHighlightedLabel = new UILabel(fontSize);
        
        List<Token> syntaxHighlightableTokens = compiler.getSyntaxHighlightableTokens(text);
        
        int netLeftBraces = 0;
        
        Integer debugStart = null;
        int debugEnd = 0;
        
        line.removeHighlight();
        
        if ( debugRegion != null  &&  debugRegion.start_line == line.getLineNumber()) {
            debugStart = debugRegion.start_col;
        }
        
        for ( Token token : syntaxHighlightableTokens ) {
            
            String content = token.content();
            String type = token.type();
            int col = token.startColumn();
            
            boolean isIssue = false;
            boolean isWarning = false;
            
            Integer errorLineLength = null;
            
            for (Error error : line.getErrors()) {
                
                int lineNumber = error.getLine();
                Integer column = error.getColumn();
                DebugRegion debugRegion = error.getDebugRegion();
                
                if (lineNumber == line.getLineNumber()  &&  column != null  &&  column == token.startColumn()) {
                    
                    if (error.getSeverity().equals("issue")) {
                        isIssue = true;
                    } else {
                        isWarning = true;
                    }
                    
                    if (debugRegion != null) {
                        errorLineLength = debugRegion.end_col - debugRegion.start_col - 1;
                    } else {
                        errorLineLength = token.content().length();
                    }
                    
                }
                
            }
            
            if ( token.typeIs("{") ) {
                netLeftBraces += 1;
            } else if ( token.typeIs("}") ) {
                netLeftBraces -= 1;
            }
            
            String special = "";
            SVGPath squiggly = null;
            
            if (debugStart != null) {
                
                if (debugRegion.start_col <= token.startColumn()  && 
                    debugRegion.end_col >= token.startColumn() + token.content().length() - 1)
                {
                    debugEnd = token.startColumn() + token.content().length() - 1;
                }
                    
            }
            
            if (isIssue || isWarning) {
                
                double width = errorLineLength * fontSize * 0.6;
                StringBuilder svgContent = new StringBuilder("M");
                
                for ( int x = 0 ; x < width ; x += 2 ) {
                    
                    int y = (x % 4 == 0) ? 0 : 2;
                    
                    svgContent.append(" " + x + " " + y + ",");
                    
                }
                
                svgContent.deleteCharAt(svgContent.length() - 1);
                
                squiggly = new SVGPath();
                squiggly.setContent(svgContent.toString());
                squiggly.setStrokeWidth(1);
                
                
                squiggly.setTranslateY(fontSize + 1);
                
            }
            
            if (isIssue) {
                squiggly.setStroke(Color.RED);
            } else if (isWarning) {
                squiggly.setStroke(Color.YELLOW);
            }
            
            Color color;
            
            boolean isDebug = false;
            
            if (debugRegion != null) {
                isDebug = debugRegion.columnContains(token)  &&  line.getLineNumber() == debugRegion.start_line;
            }
            
            switch (type) {
                
                case "identifier": {
                    if (isDebug) {
                        color = Color.rgb(50, 90, 100);
                    } else {
                        color = Color.rgb(120, 240, 240);
                    }
                    break;
                } case "intLiteral": {
                    if (isDebug) {
                        color = Color.rgb(130, 130, 20);
                    } else {
                        color = Color.rgb(235, 235, 80);
                    }
                    break;
                } case "operator": {
                    if (isDebug) {
                        color = Color.rgb(90, 90, 90);
                    } else {
                        color = Color.rgb(220, 220, 220);
                    }
                    break;
                } case "control": {
                    if (isDebug) {
                        color = Color.rgb(100, 100, 100);
                    } else {
                        color = Color.rgb(235, 235, 235);
                    }
                    break;
                } case "comment": {
                    color = Color.rgb(30, 160, 40);
                    special += "-fx-font-style: italic;";
                    break;
                } case "keyword": {
                    special += "-fx-font-weight: bold;";
                    if (Compiler.isDataTypes(content) && isDebug) {
                        color = Color.rgb(30, 150, 75);
                    } else if (Compiler.isDataTypes(content)) {
                        color = Color.rgb(90, 220, 130);
                    } else if (isDebug) {
                        color = Color.rgb(230, 100, 100);
                    } else {
                        color = Color.rgb(230, 100, 100);
                    }
                    // Kan bruke for feilmeldinger:  -fx-border-color: red; -fx-border-width: 0 0 1 0;
                    break;
                } case "stringLiteral": {
                    color = Color.rgb(230, 160, 60);
                    break;
                } case "error": {
                    color = Color.rgb(240, 80, 80);
                    special += "-fx-border-color: red; -fx-border-width: 0 0 1 0;";
                    break;
                } default: {
                    if (isDebug) {
                        color = Color.rgb(120, 120, 130);
                    } else {
                        color = Color.rgb(240, 240, 240);
                    }
                    break;
                }
                
            }
               
            syntaxHighlightedLabel.addAttributedText(content, color, col, special, squiggly);
            
        }
        
        if (debugStart != null) {
            line.highlight(debugStart, debugEnd, Color.YELLOW);
        }
         
        line.setNetLeftBraces(netLeftBraces);
        
        line.setAttributedText(syntaxHighlightedLabel);
        
    }
    
    public void reactOnTextWritten(UICodeLine topLine) {
        
        Compiler compiler = new Compiler();
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        compiler.compile(sourceCode, false);
        
        topLine.clearErrors();
        
        for ( Error error : compiler.getErrors() ) {
            topLine.pushDownError(error);
        }
        
        topLine.finishedErrors();
        
    }
    
    public UINode getIDEUpperBand(Color color, double height, UICodeLine topLine) {
        upperBand = new UpperBand_f(color, height, this, topLine);
        return upperBand;
    }
    
    public UINode getIDELowerBand(Color color, UICodeLine topLine) {
        lowerBand = new LowerBand_f(this, topLine);
        return lowerBand;
    }
    
    @Override
    public void setBackToMenuAction(UIAction action) {
        upperBand.setBackToMenuAction(action);
    }
    
    @Override
    public void scrolledInDebugArea(double dx, double dy) {
        debugArea.delegatedScroll(dx, dy);
    }
    
    private void clearConsole() {
        getConsole().clear();
    }
    
    private ConsoleView getConsole() {
        return debugArea.getConsole();
    }
    
    public void clearDebugArea() {
        debugArea.clear();
    }
    
    public void requestDebugAreaView(int view) {
        debugArea.selectView(view);
    }
    
    public void ctrlRight(UICodeLine topLine) {
        debugger_nextClock(topLine);
    }
    
}
