package Project.VirtualMachine;

import java.util.Arrays;

/**
 * Runtime stack. Note that {@code framePointer} *is* the bottom of the current frame. The value it
 * points to (that is, {@code data[framePointer]}), is the previous frame's framePointer. Since the
 * {@code framePointer}'s initial value is {@code 0} and cannot (initially) point to any other frame,
 * the {@code stackPointer}'s initial value is {@code 1}. Thus, if {@code pop()} were to be called
 * with {@code stackPointer = 1}, the {@code VMStack} object will throw a stack underflow
 * {@code VMException}.
 */
public class VMStack {
    
    private int[] data;
    
    private int framePointer = 0;
    private int stackPointer = 1;
    
    private int size;
    
    public VMStack ( int size ) {
        
        data = new int[size];
        this.size = size;
        
    }
    
    /**
     * Will push a new word to the top of the stack.
     * @param word The word that is to be pushed.
     * @throws VMException Throws if the stack is full.
     */
    public void push ( int word ) throws VMException {
        
        if ( stackPointer == size ) {
            throw new VMException("Cannot push more due to stack overflow.", "stack");
        }
        
        data [ stackPointer ] = word;
        stackPointer++;
        
    }
    
    /**
     * Return value at specified zero-indexed offset from the last element added to the stack.
     * @param offsetFromTop The specified offset. Offset of zero is the last added element.
     * @return The value at the specified offset
     * @throws VMException Will throw if the specified offset yields an invalid index.
     */
    public int peek ( int offsetFromTop ) throws VMException {
        
        if ( offsetFromTop < 0  ||  stackPointer - offsetFromTop < 0 ) {
            throw new VMException("Invalid offset at stack peek.", "stack");
        }
        
        return data [ stackPointer - offsetFromTop ];
        
    }
    
    public int[] getStack() {
        return data;
    }
    
    public int peekAtFramePointerOffset ( int offset ) throws VMException {
        
        if ( framePointer + offset < 0  ||  framePointer + offset >= stackPointer ) {
            throw new VMException("Invalid frame pointer offset " + offset + " with frame pointer = " + framePointer + " and stack pointer = " + stackPointer, "stack");
        }
        
        if ( framePointer + offset == 0 ) {
            throw new VMException("Frame pointer + offset == 0, so something went wrong", "stack");
        }
        
        return data [ framePointer + offset ];
        
    }
    
    public void writeToFramePointerOffset ( int value , int offset ) throws VMException {
        
        if ( framePointer + offset < 0  ||  framePointer + offset >= stackPointer ) {
            throw new VMException("Invalid frame pointer offset (" + offset + "). Note that frame pointer = " + framePointer + ". Stack pointer is currently " + stackPointer, "stack");
        }
        
        data [ framePointer + offset ] = value;
        
    }
    
    /**
     * Will pop the last pushed element and reduce the stack pointer by 1.
     * @return Returns the element that was last pushed to the stack.
     * @throws VMException Throws if the stack is empty.
     */
    public int pop() throws VMException {
        
        if ( stackPointer == 1 ) {
            throw new VMException("Stack underflow, cannot pop from empty stack.", "stack");
        }
        
        stackPointer--;
        return data [ stackPointer ];
        
    }
    
    /**
     * Add a new stack frame. This will push the old frame pointer to the top of the stack and
     * increase the stack pointer, thus creating a fresh frame ready for new local variables
     * to be pushed.
     * @throws VMException Will throw if there is no space for a new frame.
     */
    public void newFrame() throws VMException {
        
        if ( stackPointer >= size ) {
            throw new VMException("Frame overflow: Cannot create new frame at " + stackPointer, "stack");
        }
        
        int oldFramePointer = framePointer;
        framePointer = stackPointer;
        push(oldFramePointer);
        
    }
    
    /**
     * Remove the top frame. This will also set the stack pointer to the value of the old frame
     * pointer, thus essentially "removing" the top scope.
     * @throws VMException Will throw if only one frame (the base frame) is left, since at least
     * one frame must be present at all times.
     */
    public void removeFrame() throws VMException {
        
        if ( framePointer == 0 ) {
            throw new VMException("Cannot remove the base frame (framePointer = 0).", "stack");
        }
        
        stackPointer = framePointer;
        framePointer = pop();
        
    }
    
    public int getElementAtIndex ( int index ) throws VMException {
        
        if ( index < 0  ||  index > size ) {
            throw new VMException("Cannot get data at stack location " + index + " with stack sized " + size, "stack");
        }
        
        return data[index];
        
    }
    
    public void adjust_stack_pointer ( int adjustment ) throws VMException {
        
        if ( stackPointer + adjustment < 0  ||  stackPointer + adjustment >= size ) {
            throw new VMException("Cannot adjust stack pointer from " + stackPointer + " to " + stackPointer + adjustment, "stack");
        }
        
        stackPointer += adjustment;
        
    }
    
    public int getStackPointer() {
        return stackPointer;
    }
    
    public String getFirst ( int numberOfElements ) {
        
        StringBuilder s = new StringBuilder("[");
        
        for ( int index = 0 ; index < numberOfElements ; index++ ) {
            
            if ( index == stackPointer ) {
                s.append("->");
            }
            
            s.append(data[index] + ", ");
            
        }
        
        s.delete(s.length() - 2, s.length());
        
        return s.toString();
        
    }
    
    @Override
    public String toString() {
        return "Stack sized " + size + " with stack pointer " + stackPointer + " and data \n" + Arrays.toString(data);
    }
    
}
