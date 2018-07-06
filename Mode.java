public enum Mode
{
    CLASSIC(0),
    DONUT(1);
    
    private final int index;
    private Mode(int index)
    {
        this.index = index;
    }
    
    public int getIndex()
    {
        return index;
    }
}
