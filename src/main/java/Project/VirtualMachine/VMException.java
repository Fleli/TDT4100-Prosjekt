package Project.VirtualMachine;

public class VMException extends Exception {
    
    private String source;
    
    public VMException ( String message , String source ) {
        
        super(message);
        
        this.source = source;
        
    }
    
    public String getSource() {
        return source;
    }
    
    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage() + " at " + getSource();
    }
    
}
