package Project.Views.ViewIDE.LanguageDelegate;

import java.util.List;

import Project.Compiler.Compiler.Compiler;
import Project.Compiler.Compiler.CompilerProfiler;
import Project.Compiler.Compiler.Error;
import Project.Compiler.InstructionGeneration.DebugRegion;
import Project.VirtualMachine.Runtime;
import Project.VirtualMachine.VMException;
import Project.VirtualMachine.Heap.VMHeapArea;
import Project.UIElements.UIAction;
import Project.UIElements.UICodeLine;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.ViewIDE.IDE;
import Project.Views.ViewIDE.DebugArea.ConsoleView;
import javafx.scene.paint.Color;

public class Delegate_f implements LanguageDelegate {
    
    public static final int default_stack_size = 2048;
    public static final int default_heap_size = 2048;
    
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
            int number_of_clock_cycles = runtime.getNumberOfClockCycles();
            
            debugArea.finishedRun(leaks, number_of_clock_cycles);
            
        } catch (VMException exception) {
            
            debugArea.notifyException(exception);
            
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
        
        CompilerProfiler profiler = compiler.getProfiler();
        lowerBand.refreshCompileProfiling(profiler);
        
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
        SyntaxHighlightEngine.syntaxHighlight(line, debugRegion);
    }
    
    public void reactOnTextWritten(UICodeLine topLine) {
        
        Compiler compiler = new Compiler();
        
        String sourceCode = topLine.recursivelyFetchSourceCode();
        
        compiler.compile(sourceCode, false, 1);
        
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
        lowerBand = new LowerBand_f();
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
