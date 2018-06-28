//All negative so as not to interfere with numbers representing mine counts
public enum BoardTile
{
    HIDDEN(-1),
    CLEARED(-2),
    MINE(-3),
    FLAGGED(-4),
    BAD_FLAG(-5),
    HIT_MINE(-6),
    QUESTION(-7);
    
    private final int value;
    private BoardTile(int value)
    {
        this.value = value;
    }
    
    public int getValue()
    {
        return value;
    }
}
