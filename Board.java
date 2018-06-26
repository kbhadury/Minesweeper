import java.util.*;

public class Board
{
    Difficulty diff;
    Mode mode;
    boolean doWrap;
    int[][] lowerLayer;
    BoardTile[][] upperLayer;

    //Constructor
    public Board(Difficulty diff, Mode mode, boolean doWrap)
    {
        this.diff = diff;
        this.mode = mode;
        this.doWrap = doWrap;

        generateEmptyField(); //filled after first click
    }

    /*****Start getters and setters*****/
    public Difficulty getDiff()
    {
        return diff;
    }

    public Mode getMode()
    {
        return mode;
    }

    public int getLowerInt(int row, int col)
    {
        return lowerLayer[row][col];
    }

    public BoardTile getUpperTile(int row, int col)
    {
        return upperLayer[row][col];
    }

    public int getNumClearSpaces()
    {
        int count = 0;
        int size = diff.getSize();
        for(int row = 0; row < size; ++row)
        {
            for(int col = 0; col < size; ++col)
            {
                if(upperLayer[row][col] == BoardTile.CLEARED)
                {
                    ++count;
                }
            }
        }

        return count;
    }

    public void setUpperTile(BoardTile tile, int row, int col)
    {
        upperLayer[row][col] = tile;
    }
    /*****End getters and setters*****/

    /*****Start board operations*****/
    //Creates a minefield with no mines
    public void generateEmptyField()
    {
        int size = diff.getSize();
        int mines = diff.getMines();

        lowerLayer = new int[size][size];
        upperLayer = new BoardTile[size][size];

        //Setup empty layers
        for(int row = 0; row < size; ++row)
        {
            for(int col = 0; col < size; ++col)
            {
                upperLayer[row][col] = BoardTile.HIDDEN;
                lowerLayer[row][col] = 0;
            }
        }
    }

    //Adds mines and numbers to the board, avoiding the given space
    public void addMinesAndAvoid(int badRow, int badCol)
    {
        int size = diff.getSize();
        int mines = diff.getMines();
        int[] availableSpaces = new int[size*size];
        for(int i = 0; i < availableSpaces.length; ++i)
        {
            availableSpaces[i] = i;
        }

        //Remove the given space
        availableSpaces[badRow*size + badCol] = availableSpaces[availableSpaces.length - 1];
        availableSpaces[availableSpaces.length - 1] = -1;

        Random random = new Random();
        for(int j = 1; j <= mines; ++j)
        {
            //Thanks, Sean
            int selectedIndex = random.nextInt(availableSpaces.length - j);
            int selectedValue = availableSpaces[selectedIndex];
            lowerLayer[selectedValue/size][selectedValue%size] = BoardTile.MINE.getValue();
            availableSpaces[selectedIndex] = availableSpaces[availableSpaces.length - 1 - j];
            availableSpaces[availableSpaces.length - 1 - j] = -1; //represents invalid space
        }

        //Count mines
        for(int row = 0; row < size; ++row)
        {
            for(int col = 0; col < size; ++col)
            {
                if(lowerLayer[row][col] != BoardTile.MINE.getValue())
                {
                    lowerLayer[row][col] = countMinesAround(row, col);
                }
            }
        }
    }

    private int countMinesAround(int row, int col)
    {
        int result = 0;
        for(int r = -1; r <= 1; ++r)
        {
            for(int c = -1; c <= 1; ++c)
            {
                int testRow = row + r;
                int testCol = col + c;
                
                //Compute wrapped position if needed
                if(doWrap)
                {
                    testRow = (testRow + diff.getSize()) % diff.getSize();
                    testCol = (testCol + diff.getSize()) % diff.getSize();
                }

                //Count mine if there is one
                if(isInBounds(testRow, testCol) && lowerLayer[testRow][testCol] == BoardTile.MINE.getValue())
                {
                    ++result;
                }
            }
        }
        if(lowerLayer[row][col] == BoardTile.MINE.getValue())
        {
            --result; //avoid counting ourself;
        }

        return result;
    }

    //Used at end-game
    public void revealMines()
    {
        int size = diff.getSize();
        for(int row = 0; row < size; ++row)
        {
            for(int col = 0; col < size; ++col)
            {
                if(lowerLayer[row][col] == BoardTile.MINE.getValue())
                {
                    upperLayer[row][col] = BoardTile.MINE;
                }
            }
        }
    }

    //Recursively clear empty spaces around the given space
    public void recursivelyClear(int row, int col)
    {
        //Compute wrapped position if needed
        if(doWrap)
        {
            row = (row + diff.getSize()) % diff.getSize();
            col = (col + diff.getSize()) % diff.getSize();
        }

        if(!isInBounds(row, col) || upperLayer[row][col] == BoardTile.CLEARED)
        {
            return;
        }

        //Clear regardless, since we want to reveal numbered spaces on edges of clear area
        upperLayer[row][col] = BoardTile.CLEARED;

        if(lowerLayer[row][col] != 0)
        {
            return;
        }
        else
        {
            recursivelyClear(row - 1, col - 1);
            recursivelyClear(row - 1, col);
            recursivelyClear(row - 1, col + 1);
            recursivelyClear(row, col - 1);
            recursivelyClear(row, col + 1);
            recursivelyClear(row + 1, col - 1);
            recursivelyClear(row + 1, col);
            recursivelyClear(row + 1, col + 1);            
        }
    }

    //Check if the given space is in bounds
    private boolean isInBounds(int row, int col)
    {
        return (row >= 0 && row < diff.getSize() && col >= 0 && col < diff.getSize());
    }
    /*****End board operations*****/
}