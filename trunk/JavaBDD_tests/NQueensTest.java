// NQueensTest.java, created Jul 28, 2004 1:44:28 AM by joewhaley
// Copyright (C) 2004 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * NQueensTest
 * 
 * @author John Whaley
 * @version $Id$
 */
public class NQueensTest extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(NQueensTest.class);
    }
    
    public static final int CHECK = 10;
    public static final double[] ANSWERS =
    { 1., 0., 0., 2., 10., 4., 40., 92., 352., 724., 2680., 14200.,
      73712., 365596., 2279184., 14772512., 95815104., 666090624.,
      4968057848., 39029188884., 314666222712., 2691008701644.,
      24233937684440. };
    
    public void testNQueens() {
        for (int i = 1; i <= CHECK; ++i) {
            NQueens.N = i;
            double n = NQueens.runTest();
            Assert.assertEquals(n, ANSWERS[i-1], 0.1);
            NQueens.freeAll();
        }
        NQueens.B.done();
        NQueens.B = null;
    }
}
