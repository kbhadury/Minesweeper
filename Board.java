import java.util.*;

public class Board
{
    Difficulty diff;
    Mode mode;
    boolean doWrap;
    int[][] lowerLayer;
    BoardTile[][] upperLayer;

    public Board(Difficulty diff, Mode mode, boolean doWrap)
    {
        this.diff = diff;
        this.mode = mode;
        this.doWrap = doWrap;

        generateBoard();
    }

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

    public void setUpperTile(BoardTile tile, int row, int col)
    {
        upperLayer[row][col] = tile;
    }

    private void generateBoard()
    {
        int size = diff.getSize();
        int mines = diff.getMines();
        
        lowerLayer = new int[size][size];
        upperLayer = new BoardTile[size][size];

        //Setup upper layer
        for(int row = 0; row < size; ++row)
            for(int col = 0; col < size; ++col)
                upperLayer[row][col] = BoardTile.HIDDEN;

        //Setup lower layer with randomly-placed mines
        int[] availableSpaces = new int[size*size];
        for(int i = 0; i < availableSpaces.length; ++i)
            availableSpaces[i] = i;
        Random random = new Random();
        for(int j = 0; j < mines; ++j)
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
            for(int col = 0; col < size; ++col)
                if(lowerLayer[row][col] != BoardTile.MINE.getValue())
                    lowerLayer[row][col] = countMinesAround(row, col);
    }

    private int countMinesAround(int row, int col)
    {
        int result = 0;
        for(int r = -1; r <= 1; ++r)
            for(int c = -1; c <= 1; ++c)
                if(isInBounds(row + r, col + c) && lowerLayer[row + r][col + c] == BoardTile.MINE.getValue())
                    ++result;
        if(lowerLayer[row][col] == BoardTile.MINE.getValue())
            --result; //avoid counting ourself;

        return result;
    }

    private boolean isInBounds(int row, int col)
    {
        return (row >= 0 && row < diff.getSize() && col >= 0 && col < diff.getSize());
    }
}