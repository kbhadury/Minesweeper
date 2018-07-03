import java.io.*;

public class ScoreManager
{
    private final String SCORES_FILENAME = "scores.txt";

    private FileReader reader;
    private FileWriter writer;
    private File scoresFile;
    private Score[] scores;

    public ScoreManager()
    {
        reader = null;
        writer = null;
        scoresFile = null;
        scores = null;
    }

    //Read latest scores from file and return data
    //On error, returns null
    //Does not perform any String processing, only verifies checksums
    public Score[] getScores()
    {
        //Attempt to open scores file
        scoresFile = new File(SCORES_FILENAME);
        if(!scoresFile.exists())
        {
            return null;
        }
        try
        {
            reader = new FileReader(scoresFile);
        }
        catch(IOException ioEx)
        {
            return null;
        }
        
        return scores;
    }

    //Write to scores file and update checksums
    public void setScores(Score[] data)
    {
    }
}