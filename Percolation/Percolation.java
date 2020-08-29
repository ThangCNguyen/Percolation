import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

// Models an N-by-N percolation system.
public class Percolation {

    private boolean[] grid;
    private WeightedQuickUnionUF combineTopToBottom;
    private WeightedQuickUnionUF combineToBottom;
    private int sizeOfGrid;
    private int connectTop;
    private int connectBottom;
    private int numberOfOpenSites;

    /**
     * Create N by N grid, with all sites blocked
     *
     * @param N length and width
     */
    public Percolation(int N) {
        if (N < 1) throw new IllegalArgumentException("Size must be greater than 0");
        sizeOfGrid = N;
        connectTop = N * N;
        connectBottom = N * N + 1;
        combineTopToBottom = new WeightedQuickUnionUF((N * N) + 2);
        combineToBottom = new WeightedQuickUnionUF((N * N) + 1);
        grid = new boolean[N * N];
        for (int i = 0; i < N * N; i++) {
            grid[i] = false;
        }
    }

    /**
     * Opens the site (row i, column j) if it is not open already
     */
    public void open(int row, int col) {
        //check if the input is within the boundary of the grid
        if (row < 0 || row > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("row index i = " + row + " must be between 0 and " + (sizeOfGrid - 1));
        }
        if (col < 0 || col > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("column index j = " + col + " must be between 0 and " + (sizeOfGrid - 1));
        }

        grid[encode(row, col)] = true;
        numberOfOpenSites++;

        // Check if we aren't on the rightmost column and if right is open
        if (col < sizeOfGrid - 1 && isOpen(row, col + 1)) {
            union(encode(row, col), encode(row, col) + 1);
        }
        // Check if we aren't on the leftmost column and if left is open
        if (col > 0 && isOpen(row, col - 1)) {
            union(encode(row, col), encode(row, col) - 1);
        }
        // Check if we aren't on the top row and if the row above is open
        // If false, check if we are on the top row
        if (row > 0 && isOpen(row - 1, col)) {
            union(encode(row, col), encode(row, col) - sizeOfGrid);
        } else if (row == 0) {
            union(encode(row, col), connectTop);
        }
        // Check if we aren't on the bottom row and if the row below is open
        // If false, check if we are on the bottom row
        if (row < sizeOfGrid - 1 && isOpen(row + 1, col)) {
            union(encode(row, col), encode(row, col) + sizeOfGrid);
        } else if (row == sizeOfGrid - 1) {
            combineTopToBottom.union(encode(row, col), connectBottom);
        }
    }

    /**
     * Is site (row i, column j) open?
     *
     * @return true or false
     */
    public boolean isOpen(int row, int col) {
        //check if the input is within the boundary of the grid
        if (row < 0 || row > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("row index i = " + row + " must be between 0 and " + (sizeOfGrid - 1));
        }
        if (col < 0 || col > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("column index j = " + col + " must be between 0 and " + (sizeOfGrid - 1));
        }

        return grid[encode(row, col)] == true;
    }

    /**
     * Is site (row i, column j) full?
     *
     * @return true or false
     */
    public boolean isFull(int row, int col) {
        //check if the input is within the boundary of the grid
        if (row < 0 || row > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("row index i = " + row + " must be between 0 and " + (sizeOfGrid - 1));
        }
        if (col < 0 || col > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("column index j = " + col + " must be between 0 and " + (sizeOfGrid - 1));
        }

        return combineToBottom.connected(encode(row, col), connectTop);
    }

    /**
     * Does the system percolate?
     *
     * @return
     */
    public boolean percolates() {
        return combineTopToBottom.connected(connectTop, connectBottom);
    }

    //return the number of site open after all the the site is connected
    private int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    /**
     * Merges the component containing site p with the component containing site q in both WeightedQuickUnionUF objects.
     */
    private void union(int p, int q) {
        combineTopToBottom.union(p, q);
        combineToBottom.union(p, q);
    }

    /**
     * Converts the 2d coordinates that are given to 1d coordinates.
     *
     * @return where the node is in a 1d array
     */
    private int encode(int i, int j) {
        //check if the input is within the boundary of the grid
        if (i < 0 || i > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("row index i = " + i + " must be between 0 and " + (sizeOfGrid - 1));
        }
        if (j < 0 || j > (sizeOfGrid - 1)) {
            throw new IndexOutOfBoundsException("column index j = " + j + " must be between 0 and " + (sizeOfGrid - 1));
        }

        return (i * sizeOfGrid) + j;
    }

    // Test client. [DO NOT EDIT]
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        int N = in.readInt();
        Percolation perc = new Percolation(N);
        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            perc.open(i, j);
        }
        StdOut.println(perc.numberOfOpenSites() + " open sites");
        if (perc.percolates()) {
            StdOut.println("percolates");
        } else {
            StdOut.println("does not percolate");
        }

        // Check if site (i, j) optionally specified on the command line
        // is full.
        if (args.length == 3) {
            int i = Integer.parseInt(args[1]);
            int j = Integer.parseInt(args[2]);
            StdOut.println(perc.isFull(i, j));
        }
    }
}
