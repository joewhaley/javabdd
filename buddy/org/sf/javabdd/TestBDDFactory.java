// TestBDDFactory.java, created Aug 2, 2003 10:02:48 PM by joewhaley
// Copyright (C) 2003 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package org.sf.javabdd;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This BDD factory is used to test other BDD factories.  It is a wrapper around
 * two BDD factories, and all operations are performed on both factories.  It
 * throws an exception if the results from the two implementations do not match.</p>
 * 
 * @see org.sf.javabdd.BDDFactory
 * 
 * @author John Whaley
 * @version $Id: TestBDDFactory.java,v 1.12 2004/09/15 03:02:53 joewhaley Exp $
 */
public class TestBDDFactory extends BDDFactory {

    BDDFactory f1, f2;

    public TestBDDFactory(BDDFactory a, BDDFactory b) {
        f1 = a; f2 = b;
    }

    public static BDDFactory init(int nodenum, int cachesize) {
        BDDFactory a = BuDDyFactory.init(nodenum, cachesize);
        BDDFactory b = JDDFactory.init(nodenum, cachesize);
        return new TestBDDFactory(a, b);
    }

    public static final void assertSame(boolean b, String s) {
        if (!b) {
            throw new InternalError(s);
        }
    }
    
    public static final void assertSame(BDD b1, BDD b2, String s) {
        if (!b1.toString().equals(b2.toString())) {
        //if (b1.nodeCount() != b2.nodeCount()) {
            System.out.println("b1 = "+b1.nodeCount());
            System.out.println("b2 = "+b2.nodeCount());
            System.out.println("b1 = "+b1.toString());
            System.out.println("b2 = "+b2.toString());
            throw new InternalError(s);
        }
    }

    public static final void assertSame(boolean b, BDD b1, BDD b2, String s) {
        if (!b) {
            System.err.println("b1 = "+b1);
            System.err.println("b2 = "+b2);
            throw new InternalError(s);
        }
    }
    
    private class TestBDD extends BDD {

        BDD b1, b2;

        TestBDD(BDD a, BDD b) {
            this.b1 = a;
            this.b2 = b;
            assertSame(a, b, "constructor");
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#getFactory()
         */
        public BDDFactory getFactory() {
            return TestBDDFactory.this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isZero()
         */
        public boolean isZero() {
            boolean r1 = b1.isZero();
            boolean r2 = b2.isZero();
            assertSame(r1 == r2, b1, b2, "isZero");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isOne()
         */
        public boolean isOne() {
            boolean r1 = b1.isOne();
            boolean r2 = b2.isOne();
            assertSame(r1 == r2, b1, b2, "isOne");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#var()
         */
        public int var() {
            int r1 = b1.var();
            int r2 = b2.var();
            assertSame(r1 == r2, b1, b2, "var");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#high()
         */
        public BDD high() {
            BDD r1 = b1.high();
            BDD r2 = b2.high();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#low()
         */
        public BDD low() {
            BDD r1 = b1.low();
            BDD r2 = b2.low();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#id()
         */
        public BDD id() {
            BDD r1 = b1.id();
            BDD r2 = b2.id();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#not()
         */
        public BDD not() {
            BDD r1 = b1.not();
            BDD r2 = b2.not();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#ite(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD ite(BDD thenBDD, BDD elseBDD) {
            BDD c1 = ((TestBDD)thenBDD).b1;
            BDD c2 = ((TestBDD)thenBDD).b2;
            BDD d1 = ((TestBDD)elseBDD).b1;
            BDD d2 = ((TestBDD)elseBDD).b2;
            BDD r1 = b1.ite(c1, d1);
            BDD r2 = b2.ite(c2, d2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#relprod(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD relprod(BDD that, BDD var) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            BDD d1 = ((TestBDD)var).b1;
            BDD d2 = ((TestBDD)var).b2;
            BDD r1 = b1.relprod(c1, d1);
            BDD r2 = b2.relprod(c2, d2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#compose(org.sf.javabdd.BDD, int)
         */
        public BDD compose(BDD g, int var) {
            BDD c1 = ((TestBDD)g).b1;
            BDD c2 = ((TestBDD)g).b2;
            BDD r1 = b1.compose(c1, var);
            BDD r2 = b2.compose(c2, var);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#veccompose(org.sf.javabdd.BDDPairing)
         */
        public BDD veccompose(BDDPairing pair) {
            BDDPairing c1 = ((TestBDDPairing)pair).b1;
            BDDPairing c2 = ((TestBDDPairing)pair).b2;
            BDD r1 = b1.veccompose(c1);
            BDD r2 = b2.veccompose(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            BDD r1 = b1.constrain(c1);
            BDD r2 = b2.constrain(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            BDD c1 = ((TestBDD)var).b1;
            BDD c2 = ((TestBDD)var).b2;
            BDD r1 = b1.exist(c1);
            BDD r2 = b2.exist(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#forAll(org.sf.javabdd.BDD)
         */
        public BDD forAll(BDD var) {
            BDD c1 = ((TestBDD)var).b1;
            BDD c2 = ((TestBDD)var).b2;
            BDD r1 = b1.forAll(c1);
            BDD r2 = b2.forAll(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#unique(org.sf.javabdd.BDD)
         */
        public BDD unique(BDD var) {
            BDD c1 = ((TestBDD)var).b1;
            BDD c2 = ((TestBDD)var).b2;
            BDD r1 = b1.unique(c1);
            BDD r2 = b2.unique(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrict(org.sf.javabdd.BDD)
         */
        public BDD restrict(BDD var) {
            BDD c1 = ((TestBDD)var).b1;
            BDD c2 = ((TestBDD)var).b2;
            BDD r1 = b1.restrict(c1);
            BDD r2 = b2.restrict(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrictWith(org.sf.javabdd.BDD)
         */
        public BDD restrictWith(BDD var) {
            BDD c1 = ((TestBDD)var).b1;
            BDD c2 = ((TestBDD)var).b2;
            b1.restrictWith(c1);
            b2.restrictWith(c2);
            assertSame(b1, b2, "restrict");
            return this;
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#simplify(org.sf.javabdd.BDD)
         */
        public BDD simplify(BDD d) {
            BDD c1 = ((TestBDD)d).b1;
            BDD c2 = ((TestBDD)d).b2;
            BDD r1 = b1.simplify(c1);
            BDD r2 = b2.simplify(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#support()
         */
        public BDD support() {
            BDD r1 = b1.support();
            BDD r2 = b2.support();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#apply(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public BDD apply(BDD that, BDDOp opr) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            BDD r1 = b1.apply(c1, opr);
            BDD r2 = b2.apply(c2, opr);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyWith(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public BDD applyWith(BDD that, BDDOp opr) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            b1.applyWith(c1, opr);
            b2.applyWith(c2, opr);
            assertSame(b1, b2, "applyWith "+opr);
            return this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyAll(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyAll(BDD that, BDDOp opr, BDD var) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            BDD e1 = ((TestBDD)var).b1;
            BDD e2 = ((TestBDD)var).b2;
            BDD r1 = b1.applyAll(c1, opr, e1);
            BDD r2 = b2.applyAll(c2, opr, e2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDOp opr, BDD var) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            BDD e1 = ((TestBDD)var).b1;
            BDD e2 = ((TestBDD)var).b2;
            BDD r1 = b1.applyEx(c1, opr, e1);
            BDD r2 = b2.applyEx(c2, opr, e2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDOp opr, BDD var) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            BDD e1 = ((TestBDD)var).b1;
            BDD e2 = ((TestBDD)var).b2;
            BDD r1 = b1.applyUni(c1, opr, e1);
            BDD r2 = b2.applyUni(c2, opr, e2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOne()
         */
        public BDD satOne() {
            BDD r1 = b1.satOne();
            BDD r2 = b2.satOne();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#fullSatOne()
         */
        public BDD fullSatOne() {
            BDD r1 = b1.fullSatOne();
            BDD r2 = b2.fullSatOne();
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOne(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOne(BDD var, BDD pol) {
            BDD c1 = ((TestBDD)var).b1;
            BDD c2 = ((TestBDD)var).b2;
            BDD d1 = ((TestBDD)pol).b1;
            BDD d2 = ((TestBDD)pol).b2;
            BDD r1 = b1.satOne(c1, d1);
            BDD r2 = b2.satOne(c2, d2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            List r1 = b1.allsat();
            List r2 = b2.allsat();
            assertSame(r1.size() == r2.size(), b1, b2, "allsat");
            List r = new LinkedList();
            Iterator i = r1.iterator();
            Iterator j = r2.iterator();
            while (i.hasNext()) {
                BDD c1 = (BDD) i.next();
                BDD c2 = (BDD) j.next();
                r.add(new TestBDD(c1, c2));
            }
            return r;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replace(org.sf.javabdd.BDDPairing)
         */
        public BDD replace(BDDPairing pair) {
            BDDPairing c1 = ((TestBDDPairing)pair).b1;
            BDDPairing c2 = ((TestBDDPairing)pair).b2;
            BDD r1 = b1.replace(c1);
            BDD r2 = b2.replace(c2);
            return new TestBDD(r1, r2);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public BDD replaceWith(BDDPairing pair) {
            BDDPairing c1 = ((TestBDDPairing)pair).b1;
            BDDPairing c2 = ((TestBDDPairing)pair).b2;
            b1.replaceWith(c1);
            b2.replaceWith(c2);
            assertSame(b1, b2, "replaceWith");
            return this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#printDot()
         */
        public void printDot() {
            // TODO Compare!
            b1.printDot();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#nodeCount()
         */
        public int nodeCount() {
            int r1 = b1.nodeCount();
            int r2 = b2.nodeCount();
            assertSame(r1 == r2, b1, b2, "nodeCount");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#pathCount()
         */
        public double pathCount() {
            double r1 = b1.pathCount();
            double r2 = b2.pathCount();
            assertSame(r1 == r2, b1, b2, "pathCount");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satCount()
         */
        public double satCount() {
            double r1 = b1.satCount();
            double r2 = b2.satCount();
            assertSame(r1 == r2, b1, b2, "satCount");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#varProfile()
         */
        public int[] varProfile() {
            int[] r1 = b1.varProfile();
            int[] r2 = b2.varProfile();
            assertSame(r1.length == r2.length, "varProfile");
            for (int i=0; i<r1.length; ++i) {
                assertSame(r1[i] == r2[i], "varProfile");
            }
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#equals(org.sf.javabdd.BDD)
         */
        public boolean equals(BDD that) {
            BDD c1 = ((TestBDD)that).b1;
            BDD c2 = ((TestBDD)that).b2;
            boolean r1 = b1.equals(c1);
            boolean r2 = b2.equals(c2);
            assertSame(r1 == r2, b1, b2, "equals");
            return r1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#hashCode()
         */
        public int hashCode() {
            // TODO Compare!
            b1.hashCode();
            return b2.hashCode();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#free()
         */
        public void free() {
            b1.free();
            b2.free();
        }
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#zero()
     */
    public BDD zero() {
        return new TestBDD(f1.zero(), f2.zero());
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#one()
     */
    public BDD one() {
        return new TestBDD(f1.one(), f2.one());
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#initialize(int, int)
     */
    protected void initialize(int nodenum, int cachesize) {
        f1.initialize(nodenum, cachesize);
        f2.initialize(nodenum, cachesize);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#isInitialized()
     */
    public boolean isInitialized() {
        boolean r1 = f1.isInitialized();
        boolean r2 = f2.isInitialized();
        assertSame(r1 == r2, "isInitialized");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#done()
     */
    public void done() {
        f1.done();
        f2.done();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxNodeNum(int)
     */
    public int setMaxNodeNum(int size) {
        int r1 = f1.setMaxNodeNum(size);
        int r2 = f2.setMaxNodeNum(size);
        assertSame(r1 == r2, "setMaxNodeNum");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMinFreeNodes(int)
     */
    public void setMinFreeNodes(int x) {
        f1.setMinFreeNodes(x);
        f2.setMinFreeNodes(x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public int setMaxIncrease(int x) {
        int r1 = f1.setMaxIncrease(x);
        int r2 = f2.setMaxIncrease(x);
        assertSame(r1 == r2, "setMaxIncrease");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setCacheRatio(int)
     */
    public int setCacheRatio(int x) {
        int r1 = f1.setCacheRatio(x);
        int r2 = f2.setCacheRatio(x);
        assertSame(r1 == r2, "setCacheRatio");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varNum()
     */
    public int varNum() {
        int r1 = f1.varNum();
        int r2 = f2.varNum();
        assertSame(r1 == r2, "varNum");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarNum(int)
     */
    public int setVarNum(int num) {
        int r1 = f1.setVarNum(num);
        int r2 = f2.setVarNum(num);
        //assertSame(r1 == r2, "setVarNum");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#ithVar(int)
     */
    public BDD ithVar(int var) {
        return new TestBDD(f1.ithVar(var), f2.ithVar(var));
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nithVar(int)
     */
    public BDD nithVar(int var) {
        return new TestBDD(f1.nithVar(var), f2.nithVar(var));
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printAll()
     */
    public void printAll() {
        // TODO Compare!
        f1.printAll();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printTable(org.sf.javabdd.BDD)
     */
    public void printTable(BDD b) {
        // TODO Compare!
        BDD b1 = ((TestBDD)b).b1;
        f1.printTable(b1);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#load(java.lang.String)
     */
    public BDD load(String filename) throws IOException {
        return new TestBDD(f1.load(filename), f2.load(filename));
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD var) throws IOException {
        // TODO Compare!
        BDD b1 = ((TestBDD)var).b1;
        f1.save(filename, b1);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#level2Var(int)
     */
    public int level2Var(int level) {
        int r1 = f1.level2Var(level);
        int r2 = f2.level2Var(level);
        assertSame(r1 == r2, "level2Var");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#var2Level(int)
     */
    public int var2Level(int var) {
        int r1 = f1.var2Level(var);
        int r2 = f2.var2Level(var);
        assertSame(r1 == r2, "var2Level");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void reorder(ReorderMethod m) {
        f1.reorder(m);
        f2.reorder(m);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void autoReorder(ReorderMethod method) {
        f1.autoReorder(method);
        f2.autoReorder(method);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod, int)
     */
    public void autoReorder(ReorderMethod method, int max) {
        f1.autoReorder(method, max);
        f2.autoReorder(method, max);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderMethod()
     */
    public ReorderMethod getReorderMethod() {
        ReorderMethod r1 = f1.getReorderMethod();
        ReorderMethod r2 = f2.getReorderMethod();
        assertSame(r1.equals(r2), "getReorderMethod");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderTimes()
     */
    public int getReorderTimes() {
        int r1 = f1.getReorderTimes();
        int r2 = f2.getReorderTimes();
        assertSame(r1 == r2, "getReorderTimes");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#disableReorder()
     */
    public void disableReorder() {
        f1.disableReorder();
        f2.disableReorder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#enableReorder()
     */
    public void enableReorder() {
        f1.enableReorder();
        f2.enableReorder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderVerbose(int)
     */
    public int reorderVerbose(int v) {
        int r1 = f1.reorderVerbose(v);
        int r2 = f2.reorderVerbose(v);
        assertSame(r1 == r2, "reorderVerbose");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarOrder(int[])
     */
    public void setVarOrder(int[] neworder) {
        f1.setVarOrder(neworder);
        f2.setVarOrder(neworder);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(org.sf.javabdd.BDD, boolean)
     */
    public void addVarBlock(BDD var, boolean fixed) {
        BDD c1 = ((TestBDD)var).b1;
        BDD c2 = ((TestBDD)var).b2;
        f1.addVarBlock(c1, fixed);
        f2.addVarBlock(c2, fixed);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(int, int, boolean)
     */
    public void addVarBlock(int first, int last, boolean fixed) {
        f1.addVarBlock(first, last, fixed);
        f2.addVarBlock(first, last, fixed);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varBlockAll()
     */
    public void varBlockAll() {
        f1.varBlockAll();
        f2.varBlockAll();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearVarBlocks()
     */
    public void clearVarBlocks() {
        f1.clearVarBlocks();
        f2.clearVarBlocks();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printOrder()
     */
    public void printOrder() {
        // TODO Compare!
        f1.printOrder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nodeCount(java.util.Collection)
     */
    public int nodeCount(Collection r) {
        LinkedList a1 = new LinkedList();
        LinkedList a2 = new LinkedList();
        for (Iterator i=r.iterator(); i.hasNext();) {
            TestBDD b = (TestBDD)i.next();
            a1.add(b.b1);
            a2.add(b.b2);
        }
        int r1 = f1.nodeCount(a1);
        int r2 = f2.nodeCount(a2);
        assertSame(r1 == r2, "nodeCount");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getAllocNum()
     */
    public int getAllocNum() {
        int r1 = f1.getAllocNum();
        int r2 = f2.getAllocNum();
        assertSame(r1 == r2, "getAllocNum");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getNodeNum()
     */
    public int getNodeNum() {
        int r1 = f1.getNodeNum();
        int r2 = f2.getNodeNum();
        assertSame(r1 == r2, "getNodeNum");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderGain()
     */
    public int reorderGain() {
        int r1 = f1.reorderGain();
        int r2 = f2.reorderGain();
        assertSame(r1 == r2, "reorderGain");
        return r1;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printStat()
     */
    public void printStat() {
        // TODO Compare!
        f1.printStat();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makePair()
     */
    public BDDPairing makePair() {
        BDDPairing p1 = f1.makePair();
        BDDPairing p2 = f2.makePair();
        return new TestBDDPairing(p1, p2);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#swapVar(int, int)
     */
    public void swapVar(int v1, int v2) {
        f1.swapVar(v1, v2);
        f2.swapVar(v1, v2);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createDomain(int, long)
     */
    protected BDDDomain createDomain(int a, long b) {
        return new TestBDDDomain(a, b);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createBitVector(int)
     */
    protected BDDBitVector createBitVector(int a) {
        return new TestBDDBitVector(a);
    }

    private static class TestBDDPairing extends BDDPairing {
        
        BDDPairing b1, b2;
        
        TestBDDPairing(BDDPairing p1, BDDPairing p2) {
            this.b1 = p1;
            this.b2 = p2;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public void set(int oldvar, int newvar) {
            b1.set(oldvar, newvar);
            b2.set(oldvar, newvar);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, org.sf.javabdd.BDD)
         */
        public void set(int oldvar, BDD newvar) {
            b1.set(oldvar, newvar);
            b2.set(oldvar, newvar);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public void reset() {
            b1.reset();
            b2.reset();
        }
        
    }
        
    private class TestBDDDomain extends BDDDomain {

        TestBDDDomain(int a, long b) {
            super(a, b);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#getFactory()
         */
        public BDDFactory getFactory() {
            return TestBDDFactory.this;
        }
        
    }

    private class TestBDDBitVector extends BDDBitVector {

        TestBDDBitVector(int a) {
            super(a);
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDBitVector#getFactory()
         */
        public BDDFactory getFactory() {
            return TestBDDFactory.this;
        }
        
    }
    
}
