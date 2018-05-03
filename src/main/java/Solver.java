import edu.princeton.cs.algs4.*;

import java.util.Comparator;
import java.util.Objects;

public class Solver {

    /**
     * Priority queue for the puzzle.
     */
    private final MinPQ<Node> mainPQ;

    /**
     * Priority queue for twin puzzle.
     */
    private final MinPQ<Node> twinPQ;

    /**
     * Is this puzzle solvable?
     */
    private boolean isSolvable;

    /**
     * Number of moves so far.
     */
    private int moves;

    /**
     * The chain of boards leading to the solution.
     */
    private Stack<Board> solution;

    /**
     * Find a solution to the initial board (using the A* algorithm).
     *
     * @param initial the {@link Board} to start with
     */
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Initial board cannot be null.");
        }

        this.mainPQ = new MinPQ<>();
        this.twinPQ = new MinPQ<>();
        this.isSolvable = false;
        this.moves = 0;
        this.solution = new Stack<>();

        solve(initial);
    }

    /**
     * Is the initial board solvable?
     *
     * @return true if yes, false otherwise
     */
    public boolean isSolvable() {
        return this.isSolvable;
    }

    /**
     * Min number of moves to solve initial board; -1 if unsolvable.
     *
     * @return the number of moves required
     */
    public int moves() {
        return this.moves;
    }

    /**
     * Sequence of boards in a shortest solution; null if unsolvable.
     *
     * @return the solution boards
     */
    public Iterable<Board> solution() {
        return this.solution;
    }

    /**
     * Solve a slider puzzle.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    /**
     * Solve the puzzle using A* algorithm.
     *
     * @param initial the initial board
     */
    private void solve(Board initial) {
        this.mainPQ.insert(new Node(initial, this.moves, null));
        this.twinPQ.insert(new Node(initial.twin(), this.moves, null));

        Node nextMain = this.mainPQ.delMin();
        Node nextTwin = this.twinPQ.delMin();
        while (!nextMain.board.isGoal() && !nextTwin.board.isGoal()) {
            this.moves++;

            for (Board board : nextMain.board.neighbors()) {
                if (nextMain.parent != null && board.equals(nextMain.parent.board)) {
                    continue;
                }
                this.mainPQ.insert(new Node(board, this.moves, nextMain));
            }
            for (Board board : nextTwin.board.neighbors()) {
                if (nextTwin.parent != null && board.equals(nextTwin.parent.board)) {
                    continue;
                }
                this.twinPQ.insert(new Node(board, this.moves, nextTwin));
            }

            nextMain = this.mainPQ.delMin();
            nextTwin = this.twinPQ.delMin();
        }

        if (nextMain.board.isGoal()) {
            this.isSolvable = true;

            this.solution.push(nextMain.board);

            while (nextMain.parent != null) {
                nextMain = nextMain.parent;
                this.solution.push(nextMain.board);
            }
        }
    }

    /**
     * Inner class that represents a search node for {@link edu.princeton.cs.algs4.MinPQ} to use.
     * Is immutable itself as the underlying data structures.
     */
    private static final class Node implements Comparable<Node> {

        /**
         * Comparator to be used by {@link #compareTo(Node)} method.
         */
        private static final Comparator<Node> NODE_COMPARATOR = Comparator.comparingInt(n -> n.priority);

        private final Board board;

        private final int moves;

        private final Node parent;

        /**
         * Stores Manhattan or Hamming priority: corresponding value/distance plus number of moves made so far.
         */
        private final int priority;

        /**
         * Basic constructor with all required arguments.
         *
         * @param board  the board to use
         * @param moves  number of moves made so far
         * @param parent the predecessor {@link Node}
         */
        private Node(Board board, int moves, Node parent) {
            this.board = Objects.requireNonNull(board, "Board should be specified.");
            this.moves = moves;
            this.parent = parent;

            this.priority = this.board.manhattan() + this.moves;
        }

        @Override
        public int compareTo(Node n) {
            return NODE_COMPARATOR.compare(this, n);
        }

    }

}
