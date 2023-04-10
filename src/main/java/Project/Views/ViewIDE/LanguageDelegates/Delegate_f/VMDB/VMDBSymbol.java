package Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDB;

public class VMDBSymbol {
    
    public int value;
    public String name;
    
    
    public VMDBSymbol(int value, String name) {
        this.value = value;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name + " = " + value;
    }
    
    public String getName() {
        return name;
    }
    
    public int getValue() {
        return value;
    }
    
}
