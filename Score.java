public class Score
{
    private String name;
    private int value;
    private boolean isTime;

    public Score(String name, int value, boolean isTime)
    {
        this.name = name;
        this.value = value;
        this.isTime = isTime;
    }

    //Formats name to 10 chars wide
    //Assumes name is not more than 10 chars
    @Override
    public String toString()
    {
        if(isTime)
        {
            return String.format("%-10s: %02d:%02d", name, value/60, value%60);
        }
        else
        {
            return String.format("%-10s: %d", name, value);            
        }
    }

    public int getValue()
    {
        return value;
    }
}