package minesweeper.ui;

import minesweeper.game.MinesweeperGame;
import minesweeper.game.Cell;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinesweeperFrame extends JFrame {

    private enum Difficulty{
        BOT(9,9,10),
        NOOB(16,16,40),
        PRO(20,24,99),
        CUSTOM(9,9,10);

        final int rows, cols, mines;

        Difficulty(int rows, int cols,int mines){
            this.rows=rows;
            this.cols=cols;
            this.mines=mines;
        }
    }
    private MinesweeperGame game;
    private JButton[][] buttons;
    private JLabel statusLabel;
    private JPanel gridPanel;

    private Difficulty currentDifficulty=Difficulty.BOT;
    private int rows=9;
    private int cols=9;
    private int mines=10;

    public MinesweeperFrame(){
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        createMenuBar();
        createTopPanel();
        startNewGame();
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }
    private void createTopPanel(){
        JPanel topPanel=new JPanel();
        topPanel.setLayout(new FlowLayout());

        statusLabel=new JLabel("Mines: "+mines);
        statusLabel.setFont(new Font("Arial",Font.BOLD,16));

        JButton restartButton=new JButton("New Game");
        restartButton.addActionListener(e -> startNewGame());

        topPanel.add(statusLabel);
        topPanel.add(restartButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createMenuBar(){
        JMenuBar menuBar=new JMenuBar();

        JMenu gameMenu=new JMenu("Menu");
        JMenu difficultyMenu=new JMenu("Difficulty");

        JMenuItem botItem=new JMenuItem("BOT");
        botItem.addActionListener(e -> changeDifficulty(Difficulty.BOT));

        JMenuItem noobItem=new JMenuItem("NOOB");
        noobItem.addActionListener(e -> changeDifficulty(Difficulty.NOOB));

        JMenuItem proItem=new JMenuItem("Pro");
        proItem.addActionListener(e -> changeDifficulty(Difficulty.PRO));

        difficultyMenu.addSeparator();
        JMenuItem customItem=new JMenuItem("Custom");
        customItem.addActionListener(e -> showCustomDifficultyDialog());

        difficultyMenu.add(botItem);
        difficultyMenu.add(noobItem);
        difficultyMenu.add(proItem);
        difficultyMenu.add(customItem);

        gameMenu.add(difficultyMenu);
        menuBar.add(gameMenu);

        setJMenuBar(menuBar);
    }

    private void changeDifficulty(Difficulty difficulty){
        currentDifficulty=difficulty;
        rows=difficulty.rows;
        cols=difficulty.cols;
        mines= difficulty.mines;

        startNewGame();
    }

    private void showCustomDifficultyDialog(){
        JPanel panel=new JPanel(new GridLayout(3,2,5,5));

        SpinnerNumberModel rowsModel=new SpinnerNumberModel(rows,5,30,1);
        SpinnerNumberModel colsModel=new SpinnerNumberModel(rows,5,30,1);

        int maxMines=(rows*cols)-1;
        SpinnerNumberModel minesModel=new SpinnerNumberModel(mines,1,maxMines,1);

        JSpinner rowsSpinner=new JSpinner(rowsModel);
        JSpinner colsSpinner=new JSpinner(colsModel);
        JSpinner minesSpinner=new JSpinner(minesModel);

        rowsSpinner.setPreferredSize(new Dimension(80,25));
        colsSpinner.setPreferredSize(new Dimension(80,25));
        minesSpinner.setPreferredSize(new Dimension(80,25));

        ChangeListener updateMaxMines=e -> {
            int r=(Integer) rowsSpinner.getValue();
            int c=(Integer) colsSpinner.getValue();
            int max=(r*c)-1;
            int currentMines=(Integer) minesSpinner.getValue();

            SpinnerNumberModel model=new SpinnerNumberModel(
                    Math.min(currentMines, max),
                    1,
                    max,
                    1
            );
            minesSpinner.setModel(model);
        };
        rowsSpinner.addChangeListener(updateMaxMines);
        colsSpinner.addChangeListener(updateMaxMines);

        panel.add(new JLabel("Rows: "));
        panel.add(rowsSpinner);
        panel.add(new JLabel("Columns: "));
        panel.add(colsSpinner);
        panel.add(new JLabel("Mines: "));
        panel.add(minesSpinner);

        int result=JOptionPane.showConfirmDialog(
                this,
                panel,
                "Custom Difficulty",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if(result==JOptionPane.OK_OPTION){
            int newRows=(Integer) rowsSpinner.getValue();
            int newCols=(Integer) colsSpinner.getValue();
            int newMines=(Integer) minesSpinner.getValue();

            currentDifficulty=Difficulty.CUSTOM;
            rows=newRows;
            cols=newCols;
            mines=newMines;

            startNewGame();
        }
    }

    private void startNewGame(){
        if(gridPanel!=null){
            remove(gridPanel);
        }

        game=new MinesweeperGame(rows,cols,mines);

        gridPanel=new JPanel();
        gridPanel.setLayout(new GridLayout(rows,cols));

        buttons=new JButton[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                final int row=i;
                final int col=j;

                JButton button=new JButton();
                int buttonSize=(rows>12||cols>12)?30:40;
                button.setPreferredSize(new Dimension(buttonSize,buttonSize));
                button.setFont(new Font("Monospaced",Font.BOLD,20));
                button.setFocusPainted(false);
                button.setMargin(new Insets(0,0,0,0));

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(game.isGameOver()){
                            return;
                        }
                        if(SwingUtilities.isLeftMouseButton(e)){
                            handleLeftClick(row,col);
                        }
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(row,col);
                        }
                    }
                });

                buttons[i][j]=button;
                gridPanel.add(button);
            }
        }

        add(gridPanel, BorderLayout.CENTER);
        statusLabel.setText("Mines: "+mines);

        revalidate();
        repaint();
        pack();
        setLocationRelativeTo(null);
    }

    private void handleLeftClick(int row,int col){
        game.revealCell(row,col);
        updateBoard();
        checkGameState();
    }

    private void handleRightClick(int row, int col){
        game.toggleFlag(row,col);
        updateBoard();
        updateMineCounter();
    }

    private void updateMineCounter(){
        int flagCount=0;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(game.getCell(i,j).isFlaged()){
                    flagCount++;
                }
            }
        }
        statusLabel.setText("Mines: "+(mines-flagCount));
    }

    private void updateBoard(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Cell cell=game.getCell(i,j);
                JButton button=buttons[i][j];

                if(cell.isFlaged()){
                    button.setText("\uD83D\uDEA9");
                    button.setBackground(Color.orange);
                    button.setForeground(Color.BLACK);
                    button.setEnabled(true);
                }
                else if (cell.isRevealed()) {

                    if(cell.isMine()){
                        button.setText("\uD83D\uDCA3");
                        button.setBackground(Color.RED);
                        button.setForeground(Color.BLACK);
                    }
                    else{
                        int adjacentMines=cell.getAdjacentMines();
                        button.setBackground(Color.LIGHT_GRAY);
                        if(adjacentMines>0){
                            button.setText(String.valueOf(adjacentMines));
                            button.setForeground(getNumberColor(adjacentMines));
                        }
                        else{
                            button.setText("");
                            button.setBackground(UIManager.getColor("Button.background"));
                            button.setForeground(Color.BLACK);
                        }
                    }
                    button.setEnabled(false);
                }
                else{
                    button.setText("");
                    button.setBackground(null);
                    button.setEnabled(true);
                    button.setForeground(Color.BLACK);
                }
            }
        }
    }

    private Color getNumberColor(int count){
        switch (count){
            case 1: return Color.BLUE;
            case 2:return Color.GREEN;
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            case 5: return new Color(128, 0, 0);
            case 6: return Color.CYAN;
            case 7: return Color.BLACK;
            case 8: return Color.GRAY;
            default: return Color.BLACK;
        }
    }

    private void checkGameState(){
        if(game.isGameOver()){
            if(game.isGameWon()){
                statusLabel.setText("🎉 YOU WIN!");
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You Won!",
                        "Victory",JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                statusLabel.setText("💥 Game Over!");
                JOptionPane.showMessageDialog(this,
                        "You hit a mine!",
                        "Game Over",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
