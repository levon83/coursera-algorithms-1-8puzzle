import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

/**
 * Represents the model of the board which should be immutable.
 */
public class Board {

    /**
     * The dimension of this board.
     */
    private final int n;

    /**
     * The tiles on our board.
     */
    private final int[][] tiles;

    /**
     * Hamming value.
     */
    private final int hamming;

    /**
     * Manhattan value.
     */
    private final int manhattan;

    /**
     * Twin board.
     */
    private Board twin;

    /**
     * All the neighbors of the board.
     */
    private Iterable<Board> neighbors;

    /**
     * Construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column j).
     *
     * @param blocks the values of initial blocks
     */
    public Board(int[][] blocks) {
        if (blocks == null) {
            throw new IllegalArgumentException("Board state cannot be null.");
        }

        this.n = blocks.length;
        this.tiles = copy(blocks);
        this.hamming = calculateHamming();
        this.manhattan = calculateManhattan();
        this.twin = null;
        this.neighbors = null;
    }

    /**
     * Board dimension n.
     *
     * @return the dimension
     */
    public int dimension() {
        return this.n;
    }

    /**
     * Number of blocks out of place.
     *
     * @return the Humming priority value
     */
    public int hamming() {
        return this.hamming;
    }

    /**
     * Sum of Manhattan distances between blocks and goal.
     *
     * @return the Manhattan priority value
     */
    public int manhattan() {
        return this.manhattan;
    }

    /**
     * Is this board the goal board?
     *
     * @return true if it is, false otherwise
     */
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    /**
     * A board that is obtained by exchanging any pair of blocks.
     *
     * @return a twin board
     */
    public Board twin() {
        if (this.twin == null) {
            this.twin = findTwin();
        }

        return this.twin;
    }

    /**
     * Does this board equal y?
     *
     * @param y the board to compare to
     * @return true if equal, false otherwise
     */
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }

        if (!(y instanceof Board)) {
            return false;
        }

        Board board = (Board) y;

        int[] first = Arrays.stream(this.tiles).flatMapToInt(Arrays::stream).toArray();
        int[] second = Arrays.stream(board.tiles).flatMapToInt(Arrays::stream).toArray();

        return Arrays.equals(first, second);
    }

    /**
     * All neighboring boards.
     *
     * @return the boards
     */
    public Iterable<Board> neighbors() {
        if (this.neighbors == null) {
            this.neighbors = findNeighbors();
        }

        return this.neighbors;
    }

    /**
     * String representation of this board.
     *
     * @return the string
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * Unit tests (not graded).
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // Nothing special in here.
    }

    /**
     * Calculates number of blocks out of place.
     *
     * @return the Humming priority value calculated
     */
    private int calculateHamming() {
        int misplacedCount = 0;

        for (int i = 1; i < this.n * this.n; i++) {
            int row = (i - 1) / this.n;
            int col = (i - 1) - row * this.n;

            if (this.tiles[row][col] != i) {
                misplacedCount++;
            }
        }

        return misplacedCount;
    }

    /**
     * Calculate sum of Manhattan distances between blocks and goal.
     *
     * @return the Manhattan priority value calculated
     */
    private int calculateManhattan() {
        int distanceSum = 0;

        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                int tileValue = this.tiles[i][j];

                if (tileValue == 0) {
                    continue;
                }

                int row = (tileValue - 1) / this.n;
                int col = (tileValue - 1) - row * this.n;

                distanceSum += Math.abs(i - row) + Math.abs(j - col);
            }
        }

        return distanceSum;
    }

    /**
     * Find any twin of this board.
     *
     * @return a twin board
     */
    private Board findTwin() {
        Board result = null;
        int[][] copy = copy(this.tiles);

        int rowA = 0;
        int colA = 0;
        do {
            rowA = StdRandom.uniform(this.n);
            colA = StdRandom.uniform(this.n);
        } while (copy[rowA][colA] == 0);


        int rowB = 0;
        int colB = 0;
        do {
            rowB = StdRandom.uniform(this.n);
            colB = StdRandom.uniform(this.n);
        }
        while (copy[rowB][colB] == 0 || (rowB == rowA && colB == colA));

        swap(copy, rowA, colA, rowB, colB);

        result = new Board(copy);

        return result;
    }

    /**
     * Find all the neighbors of this board.
     *
     * @return an {@link Iterable} of found neighbors
     */
    private Iterable<Board> findNeighbors() {
        Queue<Board> result = new Queue<>();

        int row0 = 0;
        int col0 = 0;
        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                if (this.tiles[i][j] == 0) {
                    row0 = i;
                    col0 = j;
                    break;
                }
            }
        }

        int row = 0;
        int col = 0;
        int[][] copy;

        if (row0 > 0) {
            row = row0 - 1;
            col = col0;

            copy = copy(this.tiles);
            swap(copy, row0, col0, row, col);
            result.enqueue(new Board(copy));
        }

        if (row0 < this.n - 1) {
            row = row0 + 1;
            col = col0;

            copy = copy(this.tiles);
            swap(copy, row0, col0, row, col);
            result.enqueue(new Board(copy));
        }

        if (col0 > 0) {
            row = row0;
            col = col0 - 1;

            copy = copy(this.tiles);
            swap(copy, row0, col0, row, col);
            result.enqueue(new Board(copy));
        }

        if (col0 < this.n - 1) {
            row = row0;
            col = col0 + 1;

            copy = copy(this.tiles);
            swap(copy, row0, col0, row, col);
            result.enqueue(new Board(copy));
        }

        return result;
    }

    /**
     * Copy over the state.
     *
     * @param src the state to copy
     * @return new exact copy
     */
    private int[][] copy(int[][] src) {
        int[][] copy = Arrays.stream(src).map(row -> Arrays.copyOf(row, row.length)).toArray(int[][]::new);

        return copy;
    }

    /**
     * Swap two elements in the matrix.
     *
     * @param src  the data structure
     * @param rowA row index of first element
     * @param colA column index of first element
     * @param rowB row index of second element
     * @param colB column index of second element
     */
    private void swap(int[][] src, int rowA, int colA, int rowB, int colB) {
        int tmp = src[rowA][colA];
        src[rowA][colA] = src[rowB][colB];
        src[rowB][colB] = tmp;
    }

}
