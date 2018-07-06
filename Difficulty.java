public enum Difficulty
{
    EASY(8, 8, 0),
    MEDIUM(12, 15, 1),
    HARD(15, 25, 2),
    EXTREME(20, 40, 3);
    
    private final int size;
    private final int mines;
    private final int index;
    private Difficulty(int size, int mines, int index)
    {
        this.size = size;
        this.mines = mines;
        this.index = index;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public int getMines()
    {
        return mines;
    }
    
    public int getIndex()
    {
        return index;
    }
}
