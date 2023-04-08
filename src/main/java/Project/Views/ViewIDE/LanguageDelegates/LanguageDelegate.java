package Project.Views.ViewIDE.LanguageDelegates;

import Project.UIElements.UICodeLine;

public interface LanguageDelegate {
    
    public void run(String sourceCode);
    
    public void syntaxHighlight(UICodeLine line);
    
    /**
     * Have the language delegate react anytime text is written, for example
     * auto-compiling and error message display.
     * @param topLine NOT necessarily the line that text was written to, but the
     * line at the top (first in the {@code UICodeLine} linked list)
     */
    public void reactOnTextWritten(UICodeLine topLine);
    
}
