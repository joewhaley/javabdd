import org.sf.javabdd.*;

/**
 * @author John Whaley
 */
public class NQueens {
    public static BDDFactory B;

    static int N; /* Size of the chess board */
    static BDD[][] X; /* BDD variable array */
    static BDD queen; /* N-queen problem express as a BDD */

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("USAGE:  java NQueens N");
            return;
        }
        N = Integer.parseInt(args[0]);
        if (N <= 0) {
            System.err.println("USAGE:  java NQueens N");
            return;
        }

        /* Initialize with 100000 nodes, 10000 cache entries and NxN variables */
        B = BuDDyFactory.init(N * N * 256, 10000);
        B.setVarNum(N * N);

        queen = B.one();

        int i, j;

        /* Build variable array */
        X = new BDD[N][N];
        for (i = 0; i < N; i++)
            for (j = 0; j < N; j++)
                X[i][j] = B.ithVar(i * N + j);

        /* Place a queen in each row */
        for (i = 0; i < N; i++) {
            BDD e = B.zero();
            for (j = 0; j < N; j++) {
                e.orWith(X[i][j].id());
            }
            queen.andWith(e);
        }

        /* Build requirements for each variable(field) */
        for (i = 0; i < N; i++)
            for (j = 0; j < N; j++) {
                System.out.println("Adding position " + i + "," + j);
                System.out.flush();
                build(i, j);
            }

        /* Print the results */
        System.out.println("There are " + (long) queen.satCount() + " solutions.");
        System.out.println("one is:");

        BDD solution = queen.satOne();
        solution.printSet();
        System.out.println();

        B.done();
    }

    static void build(int i, int j) {
        BDD a = B.one(), b = B.one(), c = B.one(), d = B.one();
        int k, l;

        /* No one in the same column */
        for (l = 0; l < N; l++) {
            if (l != j) {
                a.andWith(X[i][j].imp(X[i][l].not()));
            }
        }

        /* No one in the same row */
        for (k = 0; k < N; k++) {
            if (k != i) {
                b.andWith(X[i][j].imp(X[k][j].not()));
            }
        }

        /* No one in the same up-right diagonal */
        for (k = 0; k < N; k++) {
            int ll = k - i + j;
            if (ll >= 0 && ll < N) {
                if (k != i) {
                    c.andWith(X[i][j].imp(X[k][ll].not()));
                }
            }
        }

        /* No one in the same down-right diagonal */
        for (k = 0; k < N; k++) {
            int ll = i + j - k;
            if (ll >= 0 && ll < N) {
                if (k != i) {
                    d.andWith(X[i][j].imp(X[k][ll].not()));
                }
            }
        }
        
        c.andWith(d);
        b.andWith(c);
        a.andWith(b);
        queen.andWith(a);
    }

}
