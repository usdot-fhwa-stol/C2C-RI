package net.sf.jameleon.event;

public interface BreakPoint{

    /**
     * Tells if this class is supposed to pause
     * @return true if class should pause and wait for user interaction.
     */
    public boolean isBreakPoint();

}
