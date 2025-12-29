package minesweeper.game;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlaged;
    private int adjacentMines;

    public Cell(){
        this.isMine=false;
        this.isRevealed=false;
        this.isFlaged=false;
        this.adjacentMines=0;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlaged() {
        return isFlaged;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    public void setFlaged(boolean flaged) {
        isFlaged = flaged;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    //Helper method for debugging
    @Override
    public String toString() {
        if (isFlaged) return "F";
        if (!isRevealed) return "?";
        if (isMine) return "X";
        return adjacentMines == 0 ? " " : String.valueOf(adjacentMines);
    }
}
