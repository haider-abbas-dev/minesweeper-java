package minesweeper.game;

import java.util.Random;
import java.util.Queue;
import java.util.LinkedList;

public class MinesweeperGame {
    private Cell[][] grid;
    private int rows;
    private int cols;
    private int totalMines;
    private int revealedCells;
    private boolean gameOver;
    private boolean gameWon;

    public MinesweeperGame(int rows, int cols, int mines){
        this.rows=rows;
        this.cols=cols;
        this.totalMines=mines;
        this.revealedCells=0;
        this.gameOver=false;
        this.gameWon=false;

        initialzeGrid();
        placeMines();
        calculateAdjacentMines();
    }

    private void initialzeGrid(){
        grid=new Cell[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                grid[i][j]=new Cell();
            }
        }
    }

    private void placeMines(){
        Random random=new Random();
        int minesPlaced=0;

        while (minesPlaced<totalMines){
            int row=random.nextInt(rows);
            int col=random.nextInt(cols);

            if(!grid[row][col].isMine()){
                grid[row][col].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateAdjacentMines(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(!grid[i][j].isMine()){
                    int count= countAdjacentMines(i,j);
                    grid[i][j].setAdjacentMines(count);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col){
        int count=0;

        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                if(i==0&&j==0){
                    continue;
                }
                int newRow=row+i;
                int newCol=col+j;

                if(isValidPosition(newRow, newCol)&&grid[newRow][newCol].isMine()){
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isValidPosition(int row, int col){
        return row>=0&&row<rows&&col>=0&&col<cols;
    }

    public void revealCell(int row,int col){
        if(gameOver||!isValidPosition(row,col)){
            return;
        }
        Cell cell=grid[row][col];
        if(cell.isFlaged()||cell.isRevealed()){
            return;
        }

        if(cell.isMine()){
            cell.setRevealed(true);
            gameOver=true;
            revealAllMines();
            return;
        }

        Queue<int[]> queue=new LinkedList<>();
        queue.add(new int[]{row,col});

        while(!queue.isEmpty()){
            int[] pos=queue.poll();
            int r=pos[0];
            int c=pos[1];

            if(!isValidPosition(r,c)||grid[r][c].isRevealed()||grid[r][c].isFlaged()){
                continue;
            }
            grid[r][c].setRevealed(true);
            revealedCells++;

            if(grid[r][c].getAdjacentMines()==0){
                for(int i=-1;i<=1;i++){
                    for(int j=-1;j<=1;j++){
                        if(i==0&&j==0){
                            continue;
                        }
                        queue.add(new int[]{r+i,c+j});
                    }
                }
            }
        }

        if(revealedCells==(rows*cols-totalMines)){
            gameWon=true;
            gameOver=true;
        }

    }
    private void revealAllMines(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                if(grid[i][j].isMine()){
                    grid[i][j].setRevealed(true);
                }
            }
        }
    }

    public void toggleFlag(int row, int col){
        if(gameOver||!isValidPosition(row, col)){
            return;
        }
        Cell cell=grid[row][col];
        if(!cell.isRevealed()){
            cell.setFlaged(!cell.isFlaged());
        }
    }
    public Cell getCell(int row, int col){
        return grid[row][col];
    }

    public int getRows(){
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getTotalMines() {
        return totalMines;
    }

    //for testing
    public void printGrid(){
        System.out.println("===Grid===");
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Cell cell=grid[i][j];
                if(cell.isMine()){
                    System.out.print("* ");
                }
                else{
                    System.out.print(cell.getAdjacentMines()+" ");
                }
            }
            System.out.println();
        }
    }
}
