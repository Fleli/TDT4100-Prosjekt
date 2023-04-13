package Project.Views.ViewIDE.LanguageDelegate;

import java.util.List;

import Project.UIElements.UIButton;
import Project.UIElements.UINode;
import Project.UIElements.UISize;
import Project.Views.ViewIDE.DebugArea.ConsoleView;
import Project.Views.ViewIDE.DebugArea.DebugAreaView;
import Project.Views.ViewIDE.DebugArea.HeapView;
import Project.Views.ViewIDE.DebugArea.RuntimeView;
import Project.Views.ViewIDE.DebugArea.StackView;
import Project.VirtualMachine.VMException;
import Project.VirtualMachine.Heap.VMHeapArea;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DebugArea_f extends UINode {
    
    private final static int n_views = 4;
    private final static double buttonHeight = 20;
    
    private final static int base_red = 70;
    private final static int base_green = 100;
    private final static int base_blue = 140;
    private final static int brightness_increase = -10;
    
    private final static double buttonFontSize = 14;
    
    private ConsoleView console;
    private StackView variables;
    private HeapView allocations;
    private RuntimeView runtime;
    
    private DebugAreaView[] views = new DebugAreaView[n_views];
    private UIButton[] buttons = new UIButton[n_views];
    private String[] buttonText = { "Konsoll", "Variabler", "Heap", "VM" };
    
    public DebugArea_f(double width, double height, Color backgroundColor) {
        
        super();
        
        double viewHeight = height - buttonHeight;
        
        console = new ConsoleView   (width,     viewHeight);
        variables = new StackView   (width,     viewHeight, 14, 6);
        allocations = new HeapView  (width,     viewHeight);
        runtime = new RuntimeView   (width,     viewHeight, 14, 6);
        
        UISize buttonSize = new UISize(width / n_views, buttonHeight);
        
        views[0] = console;
        views[1] = variables;
        views[2] = allocations;
        views[3] = runtime;
        
        for (int i = 0 ; i < n_views ; i++) {
            
            // For Java sin skyld :)
            final int index = i;
            
            // Fargeverdier for knapp og bakgrunn
            int r = base_red + i * brightness_increase;
            int g = base_green + i * brightness_increase;
            int b = base_blue + i * brightness_increase;
            
            // Plassering og tekst for knapp
            Point2D position = new Point2D(i * width / n_views, 0);
            String text = buttonText[i];
            
            // Oppretting og styling av knapp
            buttons[i] = new UIButton(position, buttonSize, text);
            buttons[i].setMainLabelFont( new Font("Courier New", buttonFontSize) );
            buttons[i].setMainLabelStyle("-fx-font-weight: bold;");
            buttons[i].setMainLabelFontColor(Color.DARKBLUE);
            buttons[i].setMainLabelTranslationY(0);
            buttons[i].setFill(r, g, b);
            buttons[i].setActionInside( () -> {
                selectView(index);
            } );
            
            // Styling og plassering av views
            views[i].setFill(r, g, b);
            views[i].setTranslateY(buttonHeight);
            views[i].setViewOrder(1);
            
            // Legger til children
            addChild(views[i]);
            addChild(buttons[i]);
            
        }
        
        selectView(1);
        
    }
    
    public ConsoleView getConsole() {
        return console;
    }
    
    public void setDebugger(VMDebugger debugger) {
        variables.setDebugger(debugger);
        runtime.setDebugger(debugger);
        allocations.setDebugger(debugger);
        console.setDebugger(debugger);
    }
    
    public void delegatedScroll(double dx, double dy) {
        
        for (DebugAreaView view : views) {
            if (view.isVisible()) {
                view.delegatedScroll(dx, dy);
            }
        }
        
    }
    
    public void refresh() {
        for (int i = 0 ; i < n_views ; i++) {
            views[i].refresh();
        }
    }
    
    /**
     * Choose 
     * @param viewNumber Index of view from viewArray, [console, variables, heap]
     */
    public void selectView(int viewNumber) {
        for (int i = 0 ; i < views.length ; i++) {
            if (i == viewNumber) {
                views[i].setVisible(true);
            } else {
                views[i].setVisible(false);
            }
        }
    }
    
    public StackView getStackView() {
        return variables;
    }
    
    public void clear() {
        for (int i = 0 ; i < views.length ; i++) {
            views[i].clear();
        }
    }
    
    public void notifyException(VMException exception) {
        selectView(3);
        runtime.notifyException(exception);
    }
    
    public void finishedRun(List<VMHeapArea> leaks, int number_of_clock_cycles) {
        
        runtime.clear();
        
        if (leaks != null  &&  leaks.size() > 0) {
            
            selectView(3);
            
            runtime.print(
                "" + leaks.size() + " memory leaks were detected.\n", 
                Color.LIGHTGRAY, "-fx-font-weight: bold;"
            );
            
            for (int i = 0 ; i < leaks.size() ; i++) {
                
                VMHeapArea leak = leaks.get(i);
                
                String description =    "Memory leak " + (i + 1) + ":\n"
                                +       "  Leaking " + leak.getSize() + " words,\n"
                                +       "  allocated at line " + leak.getAllocLine() + "\n";
                                
                runtime.print(description, Color.PINK, "");
                
            }
            
        } else {
            
            runtime.print("Successfully finished execution.\n\n", Color.WHITE, "-fx-font-weight: bold;");
            
        }
        
        variables.clear();
        variables.print("To see variables live,\nrun in debug mode.", Color.WHITE, "-fx-font-weight: bold;");
        
        allocations.clear();
        allocations.print("To see allocations live,\nrun in debug mode", Color.WHITE, "-fx-font-weight: bold;");
        
        runtime.print("Number of clock cycles:\n", Color.LIGHTGREEN, "");
        runtime.print("  " + number_of_clock_cycles + "\n", Color.LIGHTGREEN, "");
        
    }
    
}
