import java.util.*;

public class Board
{
    Difficulty diff;
    boolean doWrap;
    int[][] lowerLayer;
    BoardTile[][] upperLayer;
    boolean[][] overlayLayer;

    //Constructor
    public Board(Difficulty diff, boolean doWrap)
    {
        this.diff = diff;
        this.doWrap = doWrap;

        generateEmptyField(); //filled after first click
    }

    /*****Start getters and setters*****/
    public Difficulty getDiff()
    {
        return diff;
    }

    public boolean getWrap()
    {
        return doWrap;
    }

    public int getLowerInt(int row, int col)
    {
        return lowerLayer[row][col];
    }

    public BoardTile getUpperTile(int row, int col)
    {
        return upperLayer[row][col];
    }

    public boolean getOverlayInt(int row, int col)
    {
        return overlayLayer[row][col];
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
        overlayLayer = new boolean[size][size];

        //Setup empty layers
        for(int row = 0; row < size; ++row)
        {
            for(int col = 0; col < size; ++col)
            {
                upperLayer[row][col] = BoardTile.HIDDEN;
                lowerLayer[row][col] = 0;
                overlayLayer[row][col] = false;
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
                if(lowerLayer[row][col] == BoardTile.MINE.getValue() && upperLayer[row][col] != BoardTile.HIT_MINE)
                {
                    upperLayer[row][col] = BoardTile.MINE;
                }
            }
        }
    }

    //Recursively clear empty spaces around the given space
    //Returns the number of flags removed by the clearing
    public int recursivelyClear(int row, int col)
    {
        int flagsRemoved = 0;

        //Compute wrapped position if needed
        if(doWrap)
        {
            row = (row + diff.getSize()) % diff.getSize();
            col = (col + diff.getSize()) % diff.getSize();
        }

        //Out of bounds or already cleared
        if(!isInBounds(row, col) || upperLayer[row][col] == BoardTile.CLEARED)
        {
            return flagsRemoved;
        }

        //Clear regardless, since we want to reveal numbered spaces on edges of clear area
        if(upperLayer[row][col] == BoardTile.FLAGGED)
        {
            ++flagsRemoved;
        }
        upperLayer[row][col] = BoardTile.CLEARED;

        //Now check if we should keep clearing spaces
        if(lowerLayer[row][col] != 0)
        {
            return flagsRemoved;
        }
        else
        {
            flagsRemoved += recursivelyClear(row - 1, col - 1);
            flagsRemoved += recursivelyClear(row - 1, col);
            flagsRemoved += recursivelyClear(row - 1, col + 1);
            flagsRemoved += recursivelyClear(row, col - 1);
            flagsRemoved += recursivelyClear(row, col + 1);
            flagsRemoved += recursivelyClear(row + 1, col - 1);
            flagsRemoved += recursivelyClear(row + 1, col);
            flagsRemoved += recursivelyClear(row + 1, col + 1);            
            return flagsRemoved;
        }
    }

    public void setOverlayAt(int row, int col)
    {
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

                if(isInBounds(testRow, testCol) && upperLayer[testRow][testCol] != BoardTile.CLEARED)
                {
                    overlayLayer[testRow][testCol] = true;
                }
            }
        }
    }

    public void clearOverlayAt(int row, int col)
    {
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

                if(isInBounds(testRow, testCol))
                {
                    overlayLayer[testRow][testCol] = false;
                }
            }
        }
    }

    //Check if the given space is in bounds
    private boolean isInBounds(int row, int col)
    {
        return (row >= 0 && row < diff.getSize() && col >= 0 && col < diff.getSize());
    }
    /*****End board operations*****/
}