// BasicTests.java, created Oct 18, 2004 10:42:34 PM by jwhaley
// Copyright (C) 2004 jwhaley
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package bdd;

import junit.framework.Assert;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDException;
import net.sf.javabdd.BDDFactory;

/**
 * BasicTests
 * 
 * @author jwhaley
 * @version $Id$
 */
public class BasicTests extends BDDTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(BasicTests.class);
    }

    public void testIsZeroOne() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            BDD x = bdd.zero();
            BDD y = bdd.one();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            BDD z = bdd.ithVar(1);
            Assert.assertEquals(true, x.isZero());
            Assert.assertEquals(false, x.isOne());
            Assert.assertEquals(false, y.isZero());
            Assert.assertEquals(true, y.isOne());
            Assert.assertEquals(false, z.isZero());
            Assert.assertEquals(false, z.isOne());
            x.free(); y.free(); z.free();
        }
    }
    
    public void testVar() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            bdd.setVarOrder(new int[] { 0, 1, 2, 3, 4 });
            BDD a = bdd.ithVar(1);
            BDD b = bdd.ithVar(2);
            BDD c = bdd.ithVar(3);
            BDD d = bdd.one();
            BDD e = bdd.zero();
            Assert.assertEquals(1, a.var());
            Assert.assertEquals(2, b.var());
            Assert.assertEquals(3, c.var());
            try {
                d.var();
                Assert.fail();
            } catch (BDDException x) { }
            try {
                e.var();
                Assert.fail();
            } catch (BDDException x) { }
            BDD f = a.and(b);
            Assert.assertEquals(1, f.var());
            a.free(); b.free(); c.free(); d.free(); e.free(); f.free();
        }
    }
    
    public void testVarOrder() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            bdd.setVarOrder(new int[] { 0, 1, 2, 3, 4 });
            BDD a = bdd.ithVar(0);
            BDD b = bdd.ithVar(1);
            BDD c = bdd.ithVar(2);
            BDD d = bdd.ithVar(3);
            BDD e = bdd.ithVar(4);
            Assert.assertEquals(0, a.var());
            Assert.assertEquals(1, b.var());
            Assert.assertEquals(2, c.var());
            Assert.assertEquals(3, d.var());
            Assert.assertEquals(4, e.var());
            bdd.setVarOrder(new int[] { 2, 3, 4, 0, 1 });
            Assert.assertEquals(0, a.var());
            Assert.assertEquals(1, b.var());
            Assert.assertEquals(2, c.var());
            Assert.assertEquals(3, d.var());
            Assert.assertEquals(4, e.var());
            Assert.assertEquals(3, a.level());
            Assert.assertEquals(4, b.level());
            Assert.assertEquals(0, c.level());
            Assert.assertEquals(1, d.level());
            Assert.assertEquals(2, e.level());
            a.free(); b.free(); c.free(); d.free(); e.free();
        }
    }
    
    public void testLowHigh() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            bdd.setVarOrder(new int[] { 0, 1, 2, 3, 4 });
            BDD a, b, c;
            a = bdd.ithVar(0);
            a.andWith(bdd.ithVar(1));
            a.andWith(bdd.nithVar(2));
            Assert.assertEquals(0, a.var());
            b = a.low();
            Assert.assertEquals(true, b.isZero());
            b.free();
            b = a.high();
            Assert.assertEquals(1, b.var());
            c = b.high();
            b.free();
            Assert.assertEquals(2, c.var());
            b = c.low();
            Assert.assertEquals(true, b.isOne());
            a.free(); b.free(); c.free();
        }
    }
    
    public void testNot() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            BDD a, b, c;
            a = bdd.ithVar(0);
            b = a.not();
            c = bdd.nithVar(0);
            Assert.assertEquals(b, c);
            c.free();
            c = b.high();
            Assert.assertEquals(true, c.isZero());
            c.free();
            c = b.low();
            Assert.assertEquals(true, c.isOne());
            a.free(); b.free(); c.free();
        }
    }
    
    public void testId() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            BDD a, b;
            a = bdd.ithVar(1);
            b = a.id();
            a.andWith(bdd.ithVar(0));
            Assert.assertTrue(!a.equals(b));
            Assert.assertTrue(a.var() == 0);
            Assert.assertTrue(b.var() == 1);
            b.andWith(bdd.zero());
            Assert.assertTrue(b.isZero());
            Assert.assertTrue(!a.isZero());
            a.free(); b.free();
        }
    }
    
    void testApply(BDDFactory bdd, BDDFactory.BDDOp op,
            boolean b1, boolean b2, boolean b3, boolean b4) {
        BDD a;
        Assert.assertEquals(b1, (a = bdd.zero().applyWith(bdd.zero(), op)).isOne());
        a.free();
        Assert.assertEquals(b2, (a = bdd.zero().applyWith(bdd.one(), op)).isOne());
        a.free();
        Assert.assertEquals(b3, (a = bdd.one().applyWith(bdd.zero(), op)).isOne());
        a.free();
        Assert.assertEquals(b4, (a = bdd.one().applyWith(bdd.one(), op)).isOne());
        a.free();
    }
    
    public void testOr() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            BDD a, b, c;
            a = bdd.ithVar(1);
            b = bdd.ithVar(2);
            c = bdd.nithVar(1);
            c.orWith(a);
            Assert.assertTrue(c.isOne());
            a = bdd.zero();
            a.orWith(bdd.zero());
            Assert.assertTrue(a.isZero());
            b.orWith(b);
            Assert.assertEquals(2, b.var());
            a.free(); b.free(); c.free();
            testApply(bdd, BDDFactory.or, false, true, true, true);
        }
    }
    
    public void testXor() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            BDD a, b, c;
            a = bdd.ithVar(1);
            b = bdd.ithVar(2);
            c = bdd.nithVar(1);
            c.xorWith(a);
            Assert.assertTrue(c.isOne());
            a = bdd.zero();
            a.orWith(bdd.zero());
            Assert.assertTrue(a.isZero());
            b.xorWith(b);
            Assert.assertTrue(b.isZero());
            a.free(); b.free(); c.free();
            testApply(bdd, BDDFactory.xor, false, true, true, false);
        }
    }
    
    public void testImp() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.imp, true, true, false, true);
        }
    }
    
    public void testBiimp() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.biimp, true, false, false, true);
        }
    }
    
    public void testDiff() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.diff, false, false, true, false);
        }
    }
    
    public void testLess() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.less, false, true, false, false);
        }
    }
    
    public void testInvImp() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.invimp, true, false, true, true);
        }
    }
    
    public void testNand() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.nand, true, true, true, false);
        }
    }
    
    public void testNor() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            // TODO: more tests
            testApply(bdd, BDDFactory.nor, true, false, false, false);
        }
    }
    
    public void testIte() {
        reset();
        Assert.assertTrue(hasNext());
        while (hasNext()) {
            BDDFactory bdd = nextFactory();
            if (bdd.varNum() < 5) bdd.setVarNum(5);
            BDD a, b, c, d, e;
            a = bdd.ithVar(1);
            b = bdd.one();
            c = bdd.zero();
            d = a.ite(b, c);
            Assert.assertEquals(a, d);
            d.free();
            d = a.ite(c, b);
            e = d.not();
            Assert.assertEquals(a, e);
            d.free(); e.free();
            e = bdd.ithVar(2);
            d = e.ite(a, a);
            Assert.assertEquals(a, d);
            // TODO: more tests.
            a.free(); b.free(); c.free(); d.free(); e.free();
        }
    }
}
