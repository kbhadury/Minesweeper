import java.io.*;
import java.util.Scanner;

public class ScoreManager
{
    private final String SCORES_FILENAME = "scores.dat";
    public static final int NUM_MODES = 2;
    public static final int NUM_DIFFS = 4;
    public static final int SCORES_PER_BOARD = 6;

    private Scanner reader;
    private FileWriter writer;
    private File scoresFile;
    private Score[][][] scores;

    public ScoreManager()
    {
        reader = null;
        writer = null;
        scoresFile = null;
        scores = new Score[NUM_MODES][NUM_DIFFS][SCORES_PER_BOARD];
    }

    //Read latest scores from file and return data
    //On error, returns null
    //Does not perform any String processing
    public Score[][][] getScores()
    {
        //Attempt to open scores file
        scoresFile = new File(SCORES_FILENAME);
        if(!scoresFile.exists())
        {
            return null;
        }

        try
        {
            reader = new Scanner(scoresFile);
        }
        catch(IOException ioEx)
        {
            System.err.println(ioEx.getMessage());
            return null;
        }

        //Read in scores
        //Throwaway first line
        reader.nextLine();
        boolean isTime = true;
        for(int mode = 0; mode < NUM_MODES; ++mode)
        {
            for(int diff = 0; diff < NUM_DIFFS; ++diff)
            {
                for(int i = 0; i < SCORES_PER_BOARD; ++i)
                {
                    String name = reader.nextLine();
                    int value = reader.nextInt();
                    reader.nextLine(); //advance to start of next line
                    scores[mode][diff][i] = new Score(name, value, isTime);
                }
            }
            isTime = false; //switch to donut mode
        }
        
        reader.close();
        return scores;
    }

    //Write to scores file
    public void setScores(Score[][][] data)
    {
    }
}