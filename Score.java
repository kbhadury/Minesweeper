public class Score
{
    private String name;
    private int value;
    
    //Formats name string to be 10 chars wide
    //Assumes name is 10 or fewer chars
    public Score(String name, int value)
    {
        this.name = String.format("%10s", name);
        this.value = value;
    }
    
    public String getScore()
    {
        return name + ": " + value;
    }
    
    public int getValue()
    {
        return value;
    }
}