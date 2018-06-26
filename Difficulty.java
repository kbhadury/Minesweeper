public enum Difficulty
{
    EASY(8, 10),
    MEDIUM(12, 15),
    HARD(15, 25),
    EXTREME(20, 40);
    
    private final int size;
    private final int mines;
    private Difficulty(int size, int mines)
    {
        this.size = size;
        this.mines = mines;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public int getMines()
    {
        return mines;
    }
}
