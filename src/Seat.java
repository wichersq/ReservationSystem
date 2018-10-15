/**
 * One object of Seat class represents a seat in seat row.
 */
public class Seat {
    private int row;
    private int col;

    /**
     * Constructor of the class
     *
     * @param row a row number of seat
     * @param col a column number of seat
     */
    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Get the column number of the seat
     *
     * @return a column number
     */
    public int getCol() {
        return col;
    }

    /**
     * Get the row number of the seat
     *
     * @return a row number
     */
    public int getRow() {
        return row;
    }
}
