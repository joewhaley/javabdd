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

	long time = System.currentTimeMillis();

        /* Initialize with reasonable nodes and cache size and NxN variables */
	int numberOfNodes = (int) (Math.pow(4.4, N-6))*1000;
	int cacheSize = 1000;
	numberOfNodes = Math.max(1000, numberOfNodes);
        if (System.getProperty("bdd", "buddy").equals("cudd")) {
            B = CUDDFactory.init(numberOfNodes, cacheSize);
        } else {
            B = BuDDyFactory.init(numberOfNodes, cacheSize);
        }
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
        BDD solution = queen.satOne();
        System.out.println("Here is "+(long) solution.satCount() + " solution:");
        solution.printSet();
        System.out.println();

        queen.free();
        solution.free();
        for (i = 0; i < N; i++)
            for (j = 0; j < N; j++)
                X[i][j].free();

        B.done();

	time = System.currentTimeMillis() - time;
	System.out.println("Time: "+time/1000.+" seconds");
    }

    static void build(int i, int j) {
        BDD a = B.one(), b = B.one(), c = B.one(), d = B.one();
        int k, l;

        /* No one in the same column */
        for (l = 0; l < N; l++) {
            if (l != j) {
		BDD t = X[i][l].not();
		BDD u = X[i][j].imp(t);
		t.free();
                a.andWith(u);
            }
        }

        /* No one in the same row */
        for (k = 0; k < N; k++) {
            if (k != i) {
		BDD t = X[k][j].not();
		BDD u = X[i][j].imp(t);
		t.free();
                b.andWith(u);
            }
        }

        /* No one in the same up-right diagonal */
        for (k = 0; k < N; k++) {
            int ll = k - i + j;
            if (ll >= 0 && ll < N) {
                if (k != i) {
		    BDD t = X[k][ll].not();
		    BDD u = X[i][j].imp(t);
		    t.free();
                    c.andWith(u);
                }
            }
        }

        /* No one in the same down-right diagonal */
        for (k = 0; k < N; k++) {
            int ll = i + j - k;
            if (ll >= 0 && ll < N) {
                if (k != i) {
		    BDD t = X[k][ll].not();
		    BDD u = X[i][j].imp(t);
		    t.free();
                    d.andWith(u);
                }
            }
        }
        
        c.andWith(d);
        b.andWith(c);
        a.andWith(b);
        queen.andWith(a);
    }

}
