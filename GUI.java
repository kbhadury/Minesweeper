import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class GUI implements ActionListener
{
    //GUI objects
    JFrame frame;
    JMenuBar menuBar;
    JMenu gameM;
    JMenu newGameM;
    JMenuItem easyMI;
    JMenuItem mediumMI;
    JMenuItem hardMI;
    JMenuItem extremeMI;
    JMenuItem quitMI;
    JMenu helpM;
    JMenuItem helpMI;
    JMenuItem aboutMI;
    JLabel minesL;
    JLabel flagsL;
    JLabel timerL;
    JLabel leaderboardL;
    JTabbedPane leaderboardModeTP;
    JTabbedPane leaderboardClassicTP;
    JTabbedPane leaderboardDonutTP;
    BoardPanel boardP;
    static final int BOARD_PX = 600;
    Timer timer;

    //Images
    BufferedImage flagBI;
    BufferedImage mineBI;
    BufferedImage donutBI;

    //Mode select options
    final int CLASSIC_OPTION = JOptionPane.YES_OPTION;
    final int DONUT_OPTION = JOptionPane.NO_OPTION;

    //Game
    Board board;
    boolean isGameInProgress;
    boolean isInputEnabled;
    int numFlags;
    int time;

    //Debug
    String sampleLB = "Kevin: 06:31\nJeff: 03:19\nKaren: 12:25";

    public GUI()
    {
        board = new Board(Difficulty.MEDIUM, Mode.CLASSIC, false); //init to Medium
        isGameInProgress = false;
        isInputEnabled = true;
        numFlags = 0;
        time = 0;
        timer = new Timer(1000, this);

        //Load images
        try
        {
            flagBI = ImageIO.read(new File("flag.png"));
            mineBI = ImageIO.read(new File("mine.png"));
            donutBI = ImageIO.read(new File("donut.png"));
        }
        catch(IOException ioEx)
        {
            System.err.println(ioEx.getMessage());
            System.exit(1);
        }
    }

    public void createGUI()
    {
        //Set up fonts
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
        Font menuFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
        Font leaderboardFont = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        UIManager.put("Label.font", labelFont);
        UIManager.put("Menu.font", menuFont);
        UIManager.put("MenuItem.font", menuFont);
        UIManager.put("TextArea.font", leaderboardFont);
        UIManager.put("TitledBorder.font", menuFont);

        //Set up frame
        frame = new JFrame("Minesweeper!");
        frame.setBounds(200, 200, 930, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

        //Set up menu bar
        menuBar = new JMenuBar();
        gameM = new JMenu("Game");
        newGameM = new JMenu("New game");
        easyMI = new JMenuItem("Easy (" + Difficulty.EASY.getSize() + "x" + Difficulty.EASY.getSize() + ", " + Difficulty.EASY.getMines() + " mines)");
        easyMI.addActionListener(this);
        mediumMI = new JMenuItem("Medium (" + Difficulty.MEDIUM.getSize() + "x" + Difficulty.MEDIUM.getSize() + ", " + Difficulty.MEDIUM.getMines() + " mines)");
        mediumMI.addActionListener(this);        
        hardMI = new JMenuItem("Hard (" + Difficulty.HARD.getSize() + "x" + Difficulty.HARD.getSize() + ", " + Difficulty.HARD.getMines() + " mines)");        
        hardMI.addActionListener(this);     
        extremeMI = new JMenuItem("Extreme (" + Difficulty.EXTREME.getSize() + "x" + Difficulty.EXTREME.getSize() + ", " + Difficulty.EXTREME.getMines() + " mines)");        
        extremeMI.addActionListener(this);
        newGameM.add(easyMI);
        newGameM.add(mediumMI);
        newGameM.add(hardMI);
        newGameM.add(extremeMI);
        quitMI = new JMenuItem("Quit");
        quitMI.addActionListener(this);
        gameM.add(newGameM);
        gameM.add(quitMI);
        helpM = new JMenu("Help");
        helpMI = new JMenuItem("How to play");
        helpMI.addActionListener(this);
        aboutMI = new JMenuItem("About");
        aboutMI.addActionListener(this);
        helpM.add(helpMI);
        helpM.add(aboutMI);
        menuBar.add(gameM);
        menuBar.add(helpM);
        frame.setJMenuBar(menuBar);

        //Set up main panel
        JPanel mainP = new JPanel();
        mainP.setLayout(new BoxLayout(mainP, BoxLayout.LINE_AXIS));
        mainP.setBackground(MyColors.BG_COLOR);

        //Set up board panel on left
        boardP = new BoardPanel();
        boardP.setPreferredSize(new Dimension(BOARD_PX, BOARD_PX));
        boardP.setMaximumSize(new Dimension(BOARD_PX, BOARD_PX));
        boardP.setBorder(new BevelBorder(BevelBorder.LOWERED));
        boardP.setBackground(MyColors.HIDDEN_COLOR);
        boardP.addMouseListener(boardP);

        //Set up right panel with leaderboard and game info
        JPanel rightP = new JPanel();
        rightP.setBackground(MyColors.BG_COLOR);
        rightP.setPreferredSize(new Dimension(270, 600));
        rightP.setMaximumSize(new Dimension(270, 600));        
        rightP.setLayout(new GridLayout(2, 1, 0, 20));

        JPanel leaderboardP = new JPanel();
        leaderboardP.setBackground(MyColors.BG_COLOR);

        leaderboardL = new JLabel("Leaderboard");

        leaderboardClassicTP = new JTabbedPane();
        leaderboardClassicTP.addTab("Easy", new JTextArea(sampleLB));
        leaderboardClassicTP.addTab("Medium", new JTextArea(sampleLB));
        leaderboardClassicTP.addTab("Hard", new JTextArea(sampleLB));
        leaderboardClassicTP.addTab("Extreme", new JTextArea(sampleLB));
        leaderboardDonutTP = new JTabbedPane();
        leaderboardDonutTP.addTab("Easy", new JTextArea(sampleLB));
        leaderboardDonutTP.addTab("Medium", new JTextArea(sampleLB));
        leaderboardDonutTP.addTab("Hard", new JTextArea(sampleLB));
        leaderboardDonutTP.addTab("Extreme", new JTextArea(sampleLB));
        leaderboardModeTP = new JTabbedPane();
        leaderboardModeTP.setPreferredSize(new Dimension(260, 200));
        leaderboardModeTP.addTab("Classic", leaderboardClassicTP);
        leaderboardModeTP.addTab("Donut", leaderboardDonutTP);

        leaderboardP.add(leaderboardL);
        leaderboardP.add(leaderboardModeTP);
        rightP.add(leaderboardP);

        JPanel infoP = new JPanel();
        infoP.setBackground(MyColors.BG_COLOR);
        infoP.setLayout(new GridLayout(3, 1, 0, 10));
        timerL = new JLabel("xx:xx");
        timerL.setBorder(new TitledBorder("Time"));
        minesL = new JLabel("x");
        minesL.setBorder(new TitledBorder("Mines hidden"));
        flagsL = new JLabel("x");
        flagsL.setBorder(new TitledBorder("Flags placed"));
        infoP.add(timerL);
        infoP.add(minesL);
        infoP.add(flagsL);
        rightP.add(infoP);

        //Add to main panel
        mainP.add(Box.createRigidArea(new Dimension(15, 0)));
        mainP.add(boardP);
        mainP.add(Box.createRigidArea(new Dimension(15, 0)));
        mainP.add(rightP);
        mainP.add(Box.createRigidArea(new Dimension(15, 0)));

        //Add to frame
        frame.add(mainP);

        //Init
        startNewGame();
        frame.setVisible(true);
    }

    //Resets GUI with stats and repaints board
    private void startNewGame()
    {
        //Reset stats
        numFlags = 0;
        time = 0;
        flagsL.setText("0");
        minesL.setText("" + board.getDiff().getMines());
        timerL.setText("00:00");

        JTabbedPane currTP = null;

        switch(board.getMode())
        {
            case CLASSIC:
            leaderboardModeTP.setSelectedIndex(0);
            currTP = leaderboardClassicTP;
            break;

            case DONUT:
            leaderboardModeTP.setSelectedIndex(1);
            currTP = leaderboardDonutTP;
            break;
        }

        switch(board.getDiff())
        {
            case EASY:
            currTP.setSelectedIndex(0);
            break;

            case MEDIUM:
            currTP.setSelectedIndex(1);
            break;

            case HARD:
            currTP.setSelectedIndex(2);
            break;

            case EXTREME:
            currTP.setSelectedIndex(3);
            break;
        }

        //Repaint board and enable play
        boardP.repaint();
        isInputEnabled = true;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == timer)
        {
            ++time;
            timerL.setText(String.format("%02d", time/60) + ":" + String.format("%02d", time%60));
        }
        else if(e.getSource() == easyMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
                return;
            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.EASY))
                startNewGame();
        }
        else if(e.getSource() == mediumMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
                return;
            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.MEDIUM))
                startNewGame();
        }
        else if(e.getSource() == hardMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
                return;
            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.HARD))
                startNewGame();
        }
        else if(e.getSource() == extremeMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
                return;
            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.EXTREME))
                startNewGame();
        }
        else if(e.getSource() == quitMI)
        {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }

    //Gets mode and wrap options and calls board constructor
    //Returns false if the user doesn't specify all options
    private boolean promptOptionsAndInitBoard(Difficulty diff)
    {
        Mode mode;
        boolean doWrap;

        int modePrompt = promptMode();
        if(modePrompt == JOptionPane.CLOSED_OPTION)
            return false;
        else if(modePrompt == CLASSIC_OPTION)
            mode = Mode.CLASSIC;
        else
            mode = Mode.DONUT;

        int wrapPrompt = promptWrap();
        if(wrapPrompt == JOptionPane.CLOSED_OPTION)
            return false;
        else
            doWrap = (wrapPrompt == JOptionPane.YES_OPTION);

        board = new Board(diff, mode, doWrap);
        return true;
    }

    //Returns int so we can detect if window closed
    private int promptMode()
    {
        Object[] options = {"Classic mode", "Donut mode"};
        return JOptionPane.showOptionDialog(frame, "Select a mode", "Mode select", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
    }

    //Returns int so we can detect if window closed    
    private int promptWrap()
    {
        return JOptionPane.showConfirmDialog(frame, "Enable wrapping?", "Wrap", JOptionPane.YES_NO_OPTION);
    }

    //Convenience method for confirming an action
    //Returns boolean so that closing the window returns as false
    private boolean confirmAction(String message, String title)
    {
        int result = JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION);
        return (result == JOptionPane.YES_OPTION);
    }

    //Convenience method for displaying errors to the user
    private void showError(String message)
    {
        JOptionPane.showMessageDialog(frame, message, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    //Custom class to draw Minesweeper grid
    private class BoardPanel extends JPanel implements MouseListener
    {
        int tileSize;
        Graphics2D g2d;

        @Override
        public void paintComponent(Graphics g)
        {
            //Setup
            super.paintComponent(g);
            g2d = (Graphics2D)g;
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
            int size = board.getDiff().getSize();
            tileSize = BOARD_PX / size; 

            //Draw tiles
            for(int row = 0; row < size; ++row)
                for(int col = 0; col < size; ++col)
                    drawTile(board.getUpperTile(row, col), row, col, col*tileSize, row*tileSize);

            //Draw gridlines
            g2d.setColor(MyColors.CLEARED_COLOR);
            for(int i = 1; i < size; ++i)
            {
                g2d.drawLine(0, i*tileSize, BOARD_PX, i*tileSize);
                g2d.drawLine(i*tileSize, 0, i*tileSize, BOARD_PX);
            }
        }

        //Draw a tile on the board at the given (x,y) coords
        private void drawTile(BoardTile tile, int row, int col, int x, int y)
        {
            switch(tile)
            {
                case HIDDEN:  
                g2d.setColor(MyColors.HIDDEN_COLOR);
                g2d.fillRect(x, y, tileSize, tileSize);
                break;

                case CLEARED:
                g2d.setColor(MyColors.CLEARED_COLOR);
                g2d.fillRect(x, y, tileSize, tileSize);
                if(board.getLowerInt(row, col) > 0)
                {
                    g2d.setColor(MyColors.NUMBER_COLOR);
                    g2d.drawString("" + board.getLowerInt(row, col), x + tileSize/2, y + tileSize/2);
                }
                break;

                case FLAGGED:
                g2d.drawImage(flagBI, x+2, y+2, tileSize-2, tileSize-2, MyColors.HIDDEN_COLOR, null);
                break;
                
                case MINE:
                g2d.drawImage(mineBI, x, y, tileSize, tileSize, MyColors.HIDDEN_COLOR, null);
                break;
                
                case HIT_MINE:
                g2d.drawImage(mineBI, x, y, tileSize, tileSize, MyColors.HIT_MINE, null);
                break;
            }
        }

        //Mouse click handler, only checks for left and right mouse buttons
        public void mouseClicked(MouseEvent e)
        {
            if(!isInputEnabled)
                return;

            //Only start timer once first tile is clicked
            if(!isGameInProgress)
            {
                isGameInProgress = true;
                timer.start();
            }

            //Update tile
            int row = e.getY() / tileSize;
            int col = e.getX() / tileSize;
            if(e.getButton() == MouseEvent.BUTTON1 && board.getUpperTile(row, col) != BoardTile.FLAGGED) //right click
            {
                int tile = board.getLowerInt(row, col);
                if(tile == BoardTile.MINE.getValue())
                {
                    board.setUpperTile(BoardTile.MINE, row, col);
                }
                else if(tile == 0)
                {
                    board.setUpperTile(BoardTile.CLEARED, row, col);
                }
                else //number
                {
                    board.setUpperTile(BoardTile.CLEARED, row, col);
                }
            }
            else if(e.getButton() == MouseEvent.BUTTON3 && board.getUpperTile(row, col) == BoardTile.HIDDEN) //left click on hidden
            {
                board.setUpperTile(BoardTile.FLAGGED, row, col);
                ++numFlags;
                flagsL.setText("" + numFlags);
            }
            else if(e.getButton() == MouseEvent.BUTTON3 && board.getUpperTile(row, col) == BoardTile.FLAGGED) //left click on flag
            {
                board.setUpperTile(BoardTile.HIDDEN, row, col);
                --numFlags;
                flagsL.setText("" + numFlags);
            }

            //Update board
            repaint();
        }

        //Unused but required by interface
        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
        }
    }
}