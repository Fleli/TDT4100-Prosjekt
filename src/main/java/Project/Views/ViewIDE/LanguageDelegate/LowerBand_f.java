package Project.Views.ViewIDE.LanguageDelegate;

import Project.Program;
import Project.Compiler.Compiler.CompilerProfiler;
import Project.UIElements.UINode;
import Project.Views.ViewIDE.ViewIDE;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class LowerBand_f extends UINode {
    
    private Rectangle background;
    
    private Label[] profilingInfoLabels;
    
    private String[] stages = { "Lex", "Parse", "Bind", "Optimize", "Generate" };
    
    public LowerBand_f() {
        
        super();
        
        setViewOrder(-5);
        setTranslateY(ViewIDE.upperBandHeight + ViewIDE.codeAreaHeight);
        
        background = new Rectangle(
            Program.viewSize.width, 
            Program.viewSize.height - ViewIDE.upperBandHeight - ViewIDE.codeAreaHeight
        );
        background.setFill(ViewIDE.bandColor);
        getChildren().add(background);
        
        initProfilingInfo();
        
    }
    
    private void initProfilingInfo() {
        
        int n_stages = CompilerProfiler.n_stages;
        profilingInfoLabels = new Label[n_stages];
        
        for (int i = 0 ; i < n_stages ; i++) {
            profilingInfoLabels[i] = new Label();
            profilingInfoLabels[i].setFont( new Font("Courier New", 14) );
            profilingInfoLabels[i].setTextFill(Color.WHITE);
            profilingInfoLabels[i].setText(stages[i] + ": Compile to get profiling information.");
            profilingInfoLabels[i].setTranslateY(5 + i * 18);
            getChildren().add(profilingInfoLabels[i]);
        }
        
    }
    
    public void refreshCompileProfiling(CompilerProfiler profiler) {
        
        for (int i = 0 ; i < CompilerProfiler.n_stages ; i++) {
            
            Long time = profiler.time(i);
            String rhs;
            
            if (time == null) {
                rhs = ": did not run";
            } else {
                rhs = ": " + time + " ms";
            }
            
            profilingInfoLabels[i].setText(stages[i] + rhs);
            
        }
        
    }
    
}
