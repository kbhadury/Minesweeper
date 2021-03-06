import java.io.*;
import java.util.Scanner;

public class ScoreManager
{
    private final String SCORES_FILENAME = "scores.dat";
    private final String WARNING = "### If you are editing this file, you are cheating!! ##\n";
    public static final int NUM_MODES = 2;
    public static final int NUM_DIFFS = 4;
    public static final int NUM_WRAP = 2; //either wrap or no wrap
    public static final int NUM_TOP_SCORES = 3;
    public static final int NOWRAP_INDEX = 0;    
    public static final int WRAP_INDEX = 1;

    private Scanner reader;
    private FileWriter writer;
    private File scoresFile;
    private Score[][][][] scores;

    public ScoreManager()
    {
        reader = null;
        writer = null;
        scoresFile = null;
        scores = new Score[NUM_MODES][NUM_DIFFS][NUM_WRAP][NUM_TOP_SCORES];
    }

    //Read latest scores from file
    //On error, returns -1
    //Does not perform any String processing
    public int loadScores()
    {
        //Attempt to open scores file
        scoresFile = new File(SCORES_FILENAME);
        if(!scoresFile.exists())
        {
            return -1; 
        }

        try
        {
            reader = new Scanner(scoresFile);
        }
        catch(IOException ioEx)
        {
            System.err.println(ioEx.getMessage());
            return -1;
        }

        //Read in scores
        //Throwaway first line
        reader.nextLine();
        boolean isTime = true;
        for(int mode = 0; mode < NUM_MODES; ++mode)
        {
            for(int diff = 0; diff < NUM_DIFFS; ++diff)
            {
                for(int wrap = 0; wrap < NUM_WRAP; ++wrap)
                {
                    for(int i = 0; i < NUM_TOP_SCORES; ++i)
                    {
                        String name = reader.nextLine();
                        int value = reader.nextInt();
                        reader.nextLine(); //advance to start of next line
                        scores[mode][diff][wrap][i] = new Score(name, value, isTime);
                    }
                }
            }
            isTime = false; //switch to donut mode
        }

        reader.close();
        return 0;
    }

    public Score getScore(int index0, int index1, int index2, int index3)
    {
        return scores[index0][index1][index2][index3];
    }

    //Checks if the given value is good enough to be a new high score in the referenced array
    public boolean isHighScoreInArray(int value, int index0, int index1, int index2)
    {
        //If we're better than 3rd place, it's a new high score
        //Smaller score = better!!  It's like golf but with mines
        return (value < scores[index0][index1][index2][2].getValue());
    }

    //Inserts the given score to the array referenced by the two indices
    public void insertScoreInArray(Score score, int index0, int index1, int index2)
    {
        //Figure out where to insert the new score
        int insertAt = 2;
        while(insertAt >= 0 && scores[index0][index1][index2][insertAt].getValue() > score.getValue())
        {
            --insertAt;
        }

        if(insertAt == 2) //loop never ran, no high score
        {
            return;
        }
        else //the loop takes us past the insert point, so add 1
        {
            ++insertAt;
        }
        //At this point, insertAt is either 0, 1, or 2

        //Shift scores down
        for(int i = 2; i > insertAt; --i)
        {
            scores[index0][index1][index2][i] = scores[index0][index1][index2][i-1];
        }
        scores[index0][index1][index2][insertAt] = score;
    }

    //Write to scores file
    //On error, returns -1
    public int writeScores()
    {
        //Attempt to open scores file
        scoresFile = new File(SCORES_FILENAME);
        if(!scoresFile.exists())
        {
            return -1; 
        }

        try
        {
            writer = new FileWriter(scoresFile);
        }
        catch(IOException ioEx)
        {
            System.err.println(ioEx.getMessage());
            return -1;
        }

        //Write to file
        try
        {
            writer.write(WARNING);

            for(int mode = 0; mode < NUM_MODES; ++mode)
            {
                for(int diff = 0; diff < NUM_DIFFS; ++diff)
                {
                    for(int wrap = 0; wrap < NUM_WRAP; ++wrap)
                    {
                        for(int i = 0; i < NUM_TOP_SCORES; ++i)
                        {
                            Score score = scores[mode][diff][wrap][i];
                            writer.write(score.getName() + "\n");
                            writer.write(score.getValue() + "\n");
                        }
                    }
                }
            }

            writer.write("END"); //so the scanner doesn't crash out of bounds
            writer.close();
        }
        catch(IOException ioEx)
        {
            System.err.println(ioEx.getMessage());
            return -1;
        }

        return 0;
    }
}