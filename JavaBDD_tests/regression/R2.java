// R2.java, created Jul 28, 2004 2:55:30 AM by joewhaley
// Copyright (C) 2004 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package regression;

import junit.framework.Assert;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.BDDVarSet;
import bdd.BDDTestCase;

/**
 * support() bug
 * 
 * @author John Whaley
 * @version $Id$
 */
public class R2 extends BDDTestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(R2.class);
    }
    
    public void testR2() {
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            BDD zero = bdd.zero();
            BDD one = bdd.one();
            Assert.assertTrue(bdd.toString(), zero.isZero());
            Assert.assertTrue(bdd.toString(), one.isOne());
            BDDVarSet s0 = zero.support();
            BDDVarSet s1 = one.support();
            Assert.assertTrue(bdd.toString(), s0.isEmpty());
            Assert.assertTrue(bdd.toString(), s1.isEmpty());
            zero.free(); one.free();
            s0.free(); s1.free();
        }
    }
}
