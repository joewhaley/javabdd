// R2.java, created Jul 28, 2004 2:55:30 AM by joewhaley
// Copyright (C) 2004 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package regression;

import org.sf.javabdd.BDD;
import org.sf.javabdd.BDDDomain;
import org.sf.javabdd.BDDFactory;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * support() bug
 * 
 * @author John Whaley
 * @version $Id$
 */
public class R2 extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(R2.class);
    }
    
    public void testR2() {
        BDDFactory bdd = BDDFactory.init(1000, 1000);
        BDD zero = bdd.zero();
        BDD one = bdd.one();
        Assert.assertTrue(zero.isZero());
        Assert.assertTrue(one.isOne());
        BDD s0 = zero.support();
        BDD s1 = one.support();
        Assert.assertTrue(s0.isOne());
        Assert.assertTrue(s1.isOne());
        bdd.done();
    }
}
