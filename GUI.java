import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Random;

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
    JTextArea[][] leaderboards;
    BoardPanel boardP;
    static final int BOARD_PX = 600;
    Timer timer;

    //Images
    BufferedImage flagBI;
    BufferedImage questionBI;
    BufferedImage badFlagBI;
    BufferedImage mineBI;
    BufferedImage donutBI;
    BufferedImage[] numbersBI;

    //Overwritten selection options
    final int CLASSIC_OPTION = JOptionPane.YES_OPTION;
    final int DONUT_OPTION = JOptionPane.NO_OPTION;

    //Key codes
    final char SURROUND_KEY = ' ';

    //Game
    Board board;
    Mode mode;
    BufferedImage mineSkinBI;
    boolean isGameInProgress;
    boolean isInputEnabled;
    int numFlags;
    int numDonutsFound;
    int time;
    int clicks;
    String[] gameOverTitles = {
            "Prognosis: negative",
            "I'll just step here...",
            "Why did I sign up for this?",
            "Give and take",
            "Do what's right for you"
        };
    String[] gameOverMessages = {
            "At least the pain only lasted for 0.00002 seconds.",
            "KA-BLAMO!!!",
            "Hey, it's a tough job, but someone has to do it. Thank you.",
            "Sometimes you sweep the mine, and sometimes the mines sweeps you.",
            "There's no shame in playing on Easy mode."
        };
    String[] classicWinTitles = {
            "Nice and tidy",
            "What a pro",
            "Ready for a challenge?"
        };
    String[] classicWinMessages = {
            "Consider those mines swept!",
            "Wow, you made that look easy!",
            "Now do it on EXTREME mode WITH wrapping ;)"
        };
    String[] donutWinTitles = {
            "Oof...",
            "Yum!",
            "Skwawk!"
        };
    String[] donutWinMessages = {
            "So... full... I think I need to lie down...",
            "Deeeeeee-licious!",
            "Miiiighty tasty!"
        };
    Random randMessage;
    boolean isSurroundShown;
    ScoreManager scoreManager;

    //Constructor
    public GUI()
    {
        //Init to medium classic game
        board = new Board(Difficulty.MEDIUM, false);
        mode = Mode.CLASSIC;
        isGameInProgress = false;
        isInputEnabled = false;
        numFlags = 0;
        numDonutsFound = 0;
        time = 0;
        clicks = 0;
        timer = new Timer(1000, this);
        randMessage = new Random();
        isSurroundShown = false;
        scoreManager = new ScoreManager();

        //Load images
        try
        {
            flagBI = ImageIO.read(new File("flag.png"));
            questionBI = ImageIO.read(new File("question.png"));
            mineBI = ImageIO.read(new File("mine.png"));
            donutBI = ImageIO.read(new File("donut.png"));
            badFlagBI = ImageIO.read(new File("badFlag.png"));

            numbersBI = new BufferedImage[9];
            for(int i = 1; i <= 8; ++i)
            {
                numbersBI[i] = ImageIO.read(new File(i + ".png"));
            }
        }
        catch(IOException ioEx)
        {
            System.err.println(ioEx.getMessage());
            System.exit(1);
        }
        mineSkinBI = mineBI;

        //Init leaderboard textareas
        leaderboards = new JTextArea[ScoreManager.NUM_MODES][ScoreManager.NUM_DIFFS];
        Font leaderboardFont = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        for(int mode = 0; mode < ScoreManager.NUM_MODES; ++mode)
        {
            for(int diff = 0; diff < ScoreManager.NUM_DIFFS; ++diff)
            {
                leaderboards[mode][diff] = new JTextArea(8, 25);
                leaderboards[mode][diff].setEditable(false);
                leaderboards[mode][diff].setFont(leaderboardFont);
            }
        }
    }

    //Handles GUI initialization
    public void createGUI()
    {
        //Set up default fonts
        Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
        Font menuFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
        UIManager.put("Label.font", labelFont);
        UIManager.put("Menu.font", menuFont);
        UIManager.put("MenuItem.font", menuFont);
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

        //Add interactivity
        boardP.addMouseListener(boardP);
        ToggleSurroundAction toggleSurround = new ToggleSurroundAction();
        boardP.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(SURROUND_KEY), "toggleSurrounding");
        boardP.getActionMap().put("toggleSurrounding", toggleSurround);

        //Set up right panel with leaderboard and game info
        JPanel rightP = new JPanel();
        rightP.setBackground(MyColors.BG_COLOR);
        rightP.setPreferredSize(new Dimension(270, 600));
        rightP.setMaximumSize(new Dimension(270, 600));        
        rightP.setLayout(new GridLayout(2, 1, 0, 20));

        JPanel leaderboardP = new JPanel();
        leaderboardP.setBackground(MyColors.BG_COLOR);

        leaderboardL = new JLabel("High scores");

        leaderboardClassicTP = new JTabbedPane();
        leaderboardClassicTP.addTab("Easy", leaderboards[0][0]);
        leaderboardClassicTP.addTab("Medium", leaderboards[0][1]);
        leaderboardClassicTP.addTab("Hard", leaderboards[0][2]);
        leaderboardClassicTP.addTab("Extreme", leaderboards[0][3]);
        leaderboardDonutTP = new JTabbedPane();
        leaderboardDonutTP.addTab("Easy", leaderboards[1][0]);
        leaderboardDonutTP.addTab("Medium", leaderboards[1][1]);
        leaderboardDonutTP.addTab("Hard", leaderboards[1][2]);
        leaderboardDonutTP.addTab("Extreme", leaderboards[1][3]);
        leaderboardModeTP = new JTabbedPane();
        leaderboardModeTP.setPreferredSize(new Dimension(260, 240));
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

    //Input handler for GUI components
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == timer)
        {
            ++time;
            timerL.setText(String.format("%02d:%02d", time/60, time%60));
        }
        else if(e.getSource() == easyMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
            {
                return;
            }
            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.EASY))
            {
                startNewGame();
            }
        }
        else if(e.getSource() == mediumMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
            {
                return;
            }
            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.MEDIUM))
            {
                startNewGame();
            }
        }
        else if(e.getSource() == hardMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
            {
                return;
            }

            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.HARD))
            {
                startNewGame();
            }
        }
        else if(e.getSource() == extremeMI)
        {
            if(isGameInProgress && !confirmAction("This will quit the current game.  Proceed?", "Are you sure?"))
            {
                return;
            }

            isInputEnabled = false;
            isGameInProgress = false;
            timer.stop();
            if(promptOptionsAndInitBoard(Difficulty.EXTREME))
            {
                startNewGame();
            }
        }
        else if(e.getSource() == quitMI && confirmAction("Are you sure you want to quit?", "Really quit?"))
        {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        else if(e.getSource() == aboutMI)
        {
            showAboutDialog();
        }
    }

    /*****Game management routines*****/
    //Gets mode and wrap options and calls board constructor
    //Returns false if the user doesn't specify all options
    private boolean promptOptionsAndInitBoard(Difficulty diff)
    {
        boolean doWrap;

        if(diff == null)
        {
            String diffStr = promptDiff();
            if(diffStr == null) //closed dialog box
            {
                return false;
            }
            else
            {
                diff = Difficulty.valueOf(diffStr.toUpperCase());
            }
        }

        int modePrompt = promptMode();
        if(modePrompt == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        else if(modePrompt == CLASSIC_OPTION)
        {
            mode = Mode.CLASSIC;
            mineSkinBI = mineBI;
        }
        else
        {
            mode = Mode.DONUT;
            mineSkinBI = donutBI;
        }

        int wrapPrompt = promptWrap();
        if(wrapPrompt == JOptionPane.CLOSED_OPTION)
        {
            return false;
        }
        else
        {
            doWrap = (wrapPrompt == JOptionPane.YES_OPTION);
        }

        board = new Board(diff, doWrap);
        return true;
    }

    //Resets GUI with stats and repaints board
    private void startNewGame()
    {
        //Reset stats
        numFlags = 0;
        time = 0;
        clicks = 0;
        numDonutsFound = 0;
        if(mode == Mode.CLASSIC)
        {
            flagsL.setBorder(new TitledBorder("Flags placed"));
            flagsL.setText("0");
            minesL.setBorder(new TitledBorder("Mines hidden"));
            minesL.setText("" + board.getDiff().getMines());
            timerL.setBorder(new TitledBorder("Time"));
            timerL.setText("00:00");
        }
        else if(mode == Mode.DONUT)
        {
            flagsL.setBorder(new TitledBorder("Calories consumed"));
            flagsL.setText("0");
            minesL.setBorder(new TitledBorder("Donuts hidden"));
            minesL.setText("" + board.getDiff().getMines());
            timerL.setBorder(new TitledBorder("Clicks"));
            timerL.setText("0");
        }

        //Update leaderboard
        if(scoreManager.loadScores() == -1)
        {
            showError("Fatal: the file 'scores.dat' is missing or corrupted.");
            System.exit(1);
        }

        for(int mode = 0; mode < ScoreManager.NUM_MODES; ++mode)
        {
            for(int diff = 0; diff < ScoreManager.NUM_DIFFS; ++diff)
            {
                String text = "***Wrapping OFF***\n" + 
                    scoreManager.getScore(mode, diff, ScoreManager.NOWRAP_INDEX, 0) + "\n" + 
                    scoreManager.getScore(mode, diff, ScoreManager.NOWRAP_INDEX, 1) + "\n" + 
                    scoreManager.getScore(mode, diff, ScoreManager.NOWRAP_INDEX, 2) + "\n" + 
                    "***Wrapping ON!***\n" +
                    scoreManager.getScore(mode, diff, ScoreManager.WRAP_INDEX, 0) + "\n" + 
                    scoreManager.getScore(mode, diff, ScoreManager.WRAP_INDEX, 1) + "\n" + 
                    scoreManager.getScore(mode, diff, ScoreManager.WRAP_INDEX, 2);
                leaderboards[mode][diff].setText(text);
            }
        }

        leaderboardModeTP.setSelectedIndex(mode.getIndex());
        JTabbedPane currTP = (JTabbedPane)(leaderboardModeTP.getComponentAt(mode.getIndex()));
        currTP.setSelectedIndex(board.getDiff().getIndex());

        //Repaint board and enable play
        boardP.repaint();
        isInputEnabled = true;
    }

    //Ends the game based on whether it was a win or loss
    //It is not possible to lose in DONUT mode
    private void doGameOver(boolean isWin)
    {
        //Stop the game
        isInputEnabled = false;
        isGameInProgress = false;
        timer.stop();

        //Perform corresponding action
        int choice;
        if(!isWin) //loss
        {
            checkForBadFlags();
            board.revealMines();
            boardP.repaint();
            choice = promptRestartOnLoss();
        }
        else //win (only possible branch in DONUT mode)
        {
            int scoreValue = 99999; //just to make the compiler happy
            boolean scoreIsTime = false;

            //Reveal all mines
            if(mode == Mode.CLASSIC)
            {
                //Update timer display in case timer fired
                timerL.setText(String.format("%02d:%02d", time/60, time%60));
                board.revealMines();
                scoreValue = time;
                scoreIsTime = true;
            }
            else if(mode == Mode.DONUT)
            {
                scoreValue = clicks;
                scoreIsTime = false;
            }
            boardP.repaint();

            //Check for high score and update leaderboard if needed
            int wrapIndex = board.getWrap() ? ScoreManager.WRAP_INDEX : ScoreManager.NOWRAP_INDEX;
            if(scoreManager.isHighScoreInArray(scoreValue, mode.getIndex(), board.getDiff().getIndex(), wrapIndex))
            {
                String scoreName = promptName();
                scoreManager.insertScoreInArray(new Score(scoreName, scoreValue, scoreIsTime), mode.getIndex(), board.getDiff().getIndex(), wrapIndex);
                if(scoreManager.writeScores() == -1)
                {
                    showError("Fatal: could not write to 'scores.dat'");
                    System.exit(1);
                }
            }

            choice = promptRestartOnWin();
        }

        if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) //quit game
        {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        else if(choice == JOptionPane.YES_OPTION) //reset game
        {
            if(promptOptionsAndInitBoard(null))
                startNewGame();
        }
    }

    //Check for misplaced flags and highlight them
    private void checkForBadFlags()
    {
        int size = board.getDiff().getSize();
        for(int row = 0; row < size; ++row)
        {
            for(int col = 0; col < size; ++col)
            {
                if(board.getUpperTile(row, col) == BoardTile.FLAGGED && board.getLowerInt(row, col) != BoardTile.MINE.getValue())
                {
                    board.setUpperTile(BoardTile.BAD_FLAG, row, col);
                }
            }
        }
    }

    //Check to see if the board has been cleared correctly
    private boolean checkForWin()
    {
        if(mode == Mode.CLASSIC)
        {
            int size = board.getDiff().getSize();
            int numSpaces = size*size;
            int numMines = board.getDiff().getMines();
            int numClearSpaces = board.getNumClearSpaces();
            return (numSpaces - numMines == numClearSpaces);
        }
        else if(mode == Mode.DONUT)
        {
            int numMines = board.getDiff().getMines();
            return (numDonutsFound == numMines);
        }

        return false; //this should never happen
    }

    /****Start dialog box routines*****/
    //Returns user's choice as a String, or null if cancelled
    private String promptDiff()
    {
        Object[] options = {
                "Easy (" + Difficulty.EASY.getSize() + "x" + Difficulty.EASY.getSize() + ", " + Difficulty.EASY.getMines() + " mines)",
                "Medium (" + Difficulty.MEDIUM.getSize() + "x" + Difficulty.MEDIUM.getSize() + ", " + Difficulty.MEDIUM.getMines() + " mines)",
                "Hard (" + Difficulty.HARD.getSize() + "x" + Difficulty.HARD.getSize() + ", " + Difficulty.HARD.getMines() + " mines)",
                "Extreme (" + Difficulty.EXTREME.getSize() + "x" + Difficulty.EXTREME.getSize() + ", " + Difficulty.EXTREME.getMines() + " mines)"
            };

        String result = (String)JOptionPane.showInputDialog(frame, "Choose your difficulty", "Difficulty", JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if(result == null)
        {
            return null;
        }
        else
        {
            return (result.split(" "))[0]; //get difficulty only
        }
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

    //Ask if the user wants to play again after losing
    private int promptRestartOnLoss()
    {
        int index = randMessage.nextInt(gameOverMessages.length);
        String title = gameOverTitles[index];
        String message = gameOverMessages[index];
        Object[] options = {"Yeah, let's do it!", "No, I quit"};
        return JOptionPane.showOptionDialog(frame, "<html><body><p style='width: 200px;'>"+ message +"\n\nTry again?", title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, null);
    }

    //Show message appropriate to mode and ask if user wants to play again
    private int promptRestartOnWin()
    {
        Object[] options = {"Yeah, let's do it!", "No, I quit"};
        String message = null;
        String title = null;
        if(mode == Mode.CLASSIC)
        {
            int index = randMessage.nextInt(classicWinMessages.length);
            title = classicWinTitles[index];
            message = classicWinMessages[index] + "\nTime: " + timerL.getText();
        }
        else if(mode == Mode.DONUT)
        {
            int index = randMessage.nextInt(donutWinMessages.length);
            title = donutWinTitles[index];
            message = donutWinMessages[index] + "\nClicks: " + timerL.getText();
        }
        return JOptionPane.showOptionDialog(frame, message + "\n\nPlay again?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
    }

    //Ask the user for their name
    //Will repeat until a valid name is entered (10 or fewer chars)
    private String promptName()
    {
        String name = null;
        String message = "Congrats, you made the leaderboard!\n\nEnter your name (limit 10 characters):";

        do
        {
            name = JOptionPane.showInputDialog(frame, message, "Who IS that minesweeper master??", JOptionPane.QUESTION_MESSAGE);
        }
        while(name == null || name.length() < 1 || name.length() > 10);

        return name;
    }

    private void showAboutDialog()
    {
        JLabel text = new JLabel("<html><body><p>Made by Kiran Bhadury");
        JOptionPane.showMessageDialog(frame, text);
    }
    /*****End dialog box routines*****/

    //Custom class to draw Minesweeper grid
    private class BoardPanel extends JPanel implements MouseListener
    {
        int tileSize;
        Graphics2D g2d;

        private BoardPanel()
        {
            super();
            setFocusable(true);
        }

        public int getTileSize()
        {
            return tileSize;
        }

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
            {
                for(int col = 0; col < size; ++col)
                {
                    drawTile(board.getUpperTile(row, col), row, col, col*tileSize, row*tileSize);
                }
            }

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
                    g2d.drawImage(numbersBI[board.getLowerInt(row, col)], x, y, tileSize, tileSize, MyColors.CLEARED_COLOR, null);
                }
                break;

                case FLAGGED:
                g2d.drawImage(flagBI, x+2, y+2, tileSize-2, tileSize-2, MyColors.HIDDEN_COLOR, null);
                break;

                case MINE:
                g2d.drawImage(mineSkinBI, x, y, tileSize, tileSize, MyColors.HIDDEN_COLOR, null);
                break;

                case HIT_MINE:
                g2d.drawImage(mineBI, x, y, tileSize, tileSize, MyColors.HIT_MINE_COLOR, null);
                break;

                case BAD_FLAG:
                g2d.drawImage(badFlagBI, x, y, tileSize, tileSize, MyColors.HIDDEN_COLOR, null);
                break;

                case QUESTION:
                g2d.drawImage(questionBI, x, y, tileSize, tileSize, MyColors.HIDDEN_COLOR, null);
                break;
            }

            if(board.getOverlayInt(row, col) == 1)
            {
                g2d.setColor(MyColors.HIGHLIGHT_COLOR);
                g2d.fillRect(x + 2, y + 2, tileSize - 4, tileSize - 4);
            }
        }

        //Mouse click handler, only checks for left and right mouse buttons
        public void mouseClicked(MouseEvent e)
        {
            if(!isInputEnabled)
            {
                return;
            }

            //Request focus
            boardP.requestFocus();

            //Get tile position
            int row = e.getY() / tileSize;
            int col = e.getX() / tileSize;

            //Only start timer once first tile is clicked
            if(!isGameInProgress)
            {
                isGameInProgress = true;
                board.addMinesAndAvoid(row, col);
                if(mode == Mode.CLASSIC)
                {
                    timer.start();
                }
            }

            //Update tile
            if(e.getButton() == MouseEvent.BUTTON1 && board.getUpperTile(row, col) != BoardTile.FLAGGED) //right click
            {
                //Perform corresponding action
                int lowerInt = board.getLowerInt(row, col);
                BoardTile upperTile = board.getUpperTile(row, col);

                if(lowerInt == BoardTile.MINE.getValue())
                {
                    if(mode == Mode.CLASSIC)
                    {
                        board.setUpperTile(BoardTile.HIT_MINE, row, col);
                        doGameOver(false);
                        return;
                    }
                    else if(mode == Mode.DONUT && upperTile == BoardTile.HIDDEN) //avoid counting duplicates
                    {
                        board.setUpperTile(BoardTile.MINE, row, col);
                        ++numDonutsFound;
                        flagsL.setText("" + numDonutsFound * 100); //100 calories per donut!
                    }
                }
                else if(lowerInt == 0)
                {
                    board.recursivelyClear(row, col);
                }
                else //number
                {
                    board.setUpperTile(BoardTile.CLEARED, row, col);
                }

                //Only count clicks on hidden tiles
                if(mode == Mode.DONUT && upperTile == BoardTile.HIDDEN)
                {
                    ++clicks;
                    timerL.setText("" + clicks);
                }

                //Check for win on click
                if(checkForWin())
                {
                    doGameOver(true);
                }
            }
            else if(e.getButton() == MouseEvent.BUTTON3) //left click
            {
                BoardTile tile = board.getUpperTile(row, col);
                if(tile == BoardTile.HIDDEN)
                {
                    board.setUpperTile(BoardTile.FLAGGED, row, col);
                    if(mode == Mode.CLASSIC)
                    {
                        ++numFlags;
                        flagsL.setText("" + numFlags);
                    }
                }
                else if(tile == BoardTile.FLAGGED)
                {
                    board.setUpperTile(BoardTile.QUESTION, row, col);
                    if(mode == Mode.CLASSIC)
                    {
                        --numFlags;
                        flagsL.setText("" + numFlags);
                    }
                }
                else if(tile == BoardTile.QUESTION)
                {
                    board.setUpperTile(BoardTile.HIDDEN, row, col);
                }
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

    //Private class to handle Action to toggle surround highlighting
    private class ToggleSurroundAction extends AbstractAction
    {
        int curRow, curCol;

        private ToggleSurroundAction()
        {
            super();
            curRow = -1;
            curCol = -1;
        }

        public void actionPerformed(ActionEvent e)
        {
            if(isSurroundShown) //hide surround highlights
            {
                board.setOverlayAt(0, curRow, curCol);
            }
            else //show highlights
            {
                Point mousePt = boardP.getMousePosition();
                if(mousePt == null) //mouse not over board
                {
                    return;
                }
                curRow = mousePt.y / boardP.getTileSize();
                curCol = mousePt.x / boardP.getTileSize();
                board.setOverlayAt(1, curRow, curCol);
            }
            isSurroundShown = !isSurroundShown;
            boardP.repaint();
        }
    }
}