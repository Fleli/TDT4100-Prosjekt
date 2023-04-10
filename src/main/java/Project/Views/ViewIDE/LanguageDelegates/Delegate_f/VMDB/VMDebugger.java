package Project.Views.ViewIDE.LanguageDelegates.Delegate_f.VMDB;

import java.util.ArrayList;
import java.util.List;

import Project.Compiler.Parser.StatementTypes.Declaration;

public class VMDebugger {
    
    private int vmdb_pointer = 1;
    
    private int[] vmdb_stack;
    private String[] vmdb_symbols;
    
    private String lastInstruction_name;
    private int lastInstruction_opcode;
    private Integer lastInstruction_operand;
    private int program_counter;
    
    public VMDebugger() {
        
        vmdb_stack = new int[256];
        vmdb_symbols = new String[256];
        
    }
    
    public void push_new_var(Declaration declaration) {
        
        String name = declaration.getName();
        
        vmdb_stack      [ vmdb_pointer ]    =   0;
        vmdb_symbols    [ vmdb_pointer ]    =   name;
        
        vmdb_pointer++;
        
    }
    
    public void refresh(int[] stack) {
        
        for ( int i = 0 ; i <= vmdb_pointer ; i++ ) {
            vmdb_stack[i] = stack[i];
        }
        
    }
    
    public void readjust(int newPointer) {
        
        vmdb_pointer = newPointer;
        
    }
    
    public List<VMDBSymbol> getSymbolList() {
        
        List<VMDBSymbol> symbols = new ArrayList<VMDBSymbol>();
        
        for (int i = 1 ; i < vmdb_pointer ; i++ ) {
            int value = vmdb_stack[i];
            String name = vmdb_symbols[i];
            VMDBSymbol symbol = new VMDBSymbol(value, name);
            symbols.add(symbol);
        }
        
        return symbols;
        
    }
    
    public void setLastInstruction_name(String lastInstruction_name) {
        this.lastInstruction_name = lastInstruction_name;
    }
    
    public void setLastInstruction_opcode(int lastInstruction_opcode) {
        this.lastInstruction_opcode = lastInstruction_opcode;
    }
    
    public void setLastInstruction_operand(Integer lastInstruction_operand) {
        this.lastInstruction_operand = lastInstruction_operand;
    }
    
    public void setProgram_counter(int program_counter) {
        this.program_counter = program_counter;
    }
    
    public String getRuntimeViewDescription() {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("Executed (" + lastInstruction_opcode + ")");
        
        while (sb.length() < 15) sb.append(" ");
        
        sb.append(lastInstruction_name + "\n");
        
        if (lastInstruction_operand != null) {
            sb.append("  operand: " + lastInstruction_operand + "\n");
        }
        
        sb.append("Program counter = " + program_counter);
        
        return sb.toString();
        
    }
    
}
