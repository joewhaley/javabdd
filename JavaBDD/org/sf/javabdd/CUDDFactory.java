/*
 * CUDDFactory.java
 * 
 * Created on Mar 24, 2003
 *
 */
package org.sf.javabdd;

import java.util.Collection;
import java.util.List;

/**
 * CUDDFactory
 * 
 * @author John Whaley
 * @version $Id$
 */
public class CUDDFactory extends BDDFactory {

    public static BDDFactory init(int nodenum, int cachesize) {
        if (INSTANCE != null) {
            throw new InternalError("Error: CUDDFactory already initialized.");
        }
        INSTANCE = new CUDDFactory();
        INSTANCE.initialize(nodenum/8, cachesize);
        return INSTANCE;
    }
    
    private static CUDDFactory INSTANCE;
    
    static {
        System.loadLibrary("cudd");
        registerNatives();
    }
    
    private static native void registerNatives();
    
    private CUDDFactory() {}
    
    private long zero;
    private long one;
    
    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#zero()
     */
    public BDD zero() {
        return new CUDDBDD(zero);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#one()
     */
    public BDD one() {
        return new CUDDBDD(one);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#buildCube(int, int, java.util.Collection)
     */
    public BDD buildCube(int value, int width, Collection var) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#buildCube(int, int, int[])
     */
    public BDD buildCube(int value, int width, int[] var) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makeSet(int[])
     */
    public BDD makeSet(int[] v) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#initialize(int, int)
     */
    protected native void initialize(int nodenum, int cachesize);

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#isInitialized()
     */
    public native boolean isInitialized();

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#done()
     */
    public native void done();

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxNodeNum(int)
     */
    public int setMaxNodeNum(int size) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMinFreeNodes(int)
     */
    public void setMinFreeNodes(int x) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public int setMaxIncrease(int x) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setCacheRatio(int)
     */
    public int setCacheRatio(int x) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varNum()
     */
    public native int varNum();

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarNum(int)
     */
    public native int setVarNum(int num);

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#extVarNum(int)
     */
    public int extVarNum(int num) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#ithVar(int)
     */
    public native BDD ithVar(int var);

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nithVar(int)
     */
    public BDD nithVar(int var) {
        BDD b = ithVar(var);
        BDD c = b.not(); b.free();
        return c;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printAll()
     */
    public void printAll() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printTable(org.sf.javabdd.BDD)
     */
    public void printTable(BDD b) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#load(java.lang.String)
     */
    public BDD load(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD var) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#level2Var(int)
     */
    public native int level2Var(int level);

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#var2Level(int)
     */
    public native int var2Level(int var);

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void reorder(ReorderMethod m) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void autoReorder(ReorderMethod method) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod, int)
     */
    public void autoReorder(ReorderMethod method, int max) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderMethod()
     */
    public ReorderMethod getReorderMethod() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderTimes()
     */
    public int getReorderTimes() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#disableReorder()
     */
    public void disableReorder() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#enableReorder()
     */
    public void enableReorder() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderVerbose(int)
     */
    public int reorderVerbose(int v) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarOrder(int[])
     */
    public void setVarOrder(int[] neworder) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(org.sf.javabdd.BDD, boolean)
     */
    public void addVarBlock(BDD var, boolean fixed) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(int, int, boolean)
     */
    public void addVarBlock(int first, int last, boolean fixed) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varBlockAll()
     */
    public void varBlockAll() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearVarBlocks()
     */
    public void clearVarBlocks() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printOrder()
     */
    public void printOrder() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nodeCount(java.util.Collection)
     */
    public int nodeCount(Collection r) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getAllocNum()
     */
    public int getAllocNum() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getNodeNum()
     */
    public int getNodeNum() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderGain()
     */
    public int reorderGain() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printStat()
     */
    public void printStat() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makePair()
     */
    public BDDPairing makePair() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#swapVar(int, int)
     */
    public void swapVar(int v1, int v2) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#extDomain(int[])
     */
    public BDDDomain[] extDomain(int[] domainSizes) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#overlapDomain(org.sf.javabdd.BDDDomain, org.sf.javabdd.BDDDomain)
     */
    public BDDDomain overlapDomain(BDDDomain d1, BDDDomain d2) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makeSet(org.sf.javabdd.BDDDomain[])
     */
    public BDD makeSet(BDDDomain[] v) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearAllDomains()
     */
    public void clearAllDomains() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#numberOfDomains()
     */
    public int numberOfDomains() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getDomain(int)
     */
    public BDDDomain getDomain(int i) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * CUDDBDD
     * 
     * @author SUIF User
     * @version $Id$
     */
    public static class CUDDBDD extends BDD {

        private long _ddnode_ptr;
        
        private CUDDBDD(long ddnode) {
            this._ddnode_ptr = ddnode;
            this.addRef();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#getFactory()
         */
        public BDDFactory getFactory() {
            return INSTANCE;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isZero()
         */
        public native boolean isZero();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isOne()
         */
        public native boolean isOne();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#var()
         */
        public native int var();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#high()
         */
        public native BDD high();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#low()
         */
        public native BDD low();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#id()
         */
        public BDD id() {
            return new CUDDBDD(_ddnode_ptr);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#not()
         */
        public native BDD not();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#ite(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public native BDD ite(BDD thenBDD, BDD elseBDD);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#relprod(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public native BDD relprod(BDD that, BDD var);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#compose(org.sf.javabdd.BDD, int)
         */
        public native BDD compose(BDD that, int var);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#veccompose(org.sf.javabdd.BDDPairing)
         */
        public BDD veccompose(BDDPairing pair) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#forAll(org.sf.javabdd.BDD)
         */
        public BDD forAll(BDD var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#unique(org.sf.javabdd.BDD)
         */
        public BDD unique(BDD var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrict(org.sf.javabdd.BDD)
         */
        public native BDD restrict(BDD var);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#simplify(org.sf.javabdd.BDD)
         */
        public BDD simplify(BDD d) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#support()
         */
        public native BDD support();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#apply(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public native BDD apply(BDD that, BDDOp opr);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyWith(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public native void applyWith(BDD that, BDDOp opr);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyAll(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyAll(BDD that, BDDOp opr, BDD var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDOp opr, BDD var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDOp opr, BDD var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOne()
         */
        public native BDD satOne();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#fullSatOne()
         */
        public BDD fullSatOne() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOneSet(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOneSet(BDD var, BDD pol) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#scanSet()
         */
        public int[] scanSet() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#scanSetDomains()
         */
        public int[] scanSetDomains() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#scanVar(org.sf.javabdd.BDDDomain)
         */
        public int scanVar(BDDDomain d) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#scanAllVar()
         */
        public int[] scanAllVar() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replace(org.sf.javabdd.BDDPairing)
         */
        public BDD replace(BDDPairing pair) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public void replaceWith(BDDPairing pair) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#printSet()
         */
        public void printSet() {
            BDDFactory f = this.getFactory();
            int[] set = new int[f.varNum()];
            bdd_printset_rec(f, this, set);
        }
        
        static void bdd_printset_rec(BDDFactory f, BDD r, int[] set) {
            int n;
            boolean first;

            if (r.isZero())
                return;
            else if (r.isOne()) {
                System.out.print('<');
                first = true;

                for (n = 0; n < set.length; n++) {
                    if (set[n] > 0) {
                        if (!first)
                            System.out.print(", ");
                        first = false;
                        System.out.print(f.level2Var(n));
                        System.out.print(':');
                        System.out.print((set[n] == 2 ? 1 : 0));
                    }
                }
                System.out.print('>');
            } else {
                set[f.var2Level(r.var())] = 1;
                bdd_printset_rec(f, r.low(), set);

                set[f.var2Level(r.var())] = 2;
                bdd_printset_rec(f, r.high(), set);

                set[f.var2Level(r.var())] = 0;
            }
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#printSetWithDomains()
         */
        public void printSetWithDomains() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#printDot()
         */
        public void printDot() {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#nodeCount()
         */
        public native int nodeCount();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#pathCount()
         */
        public native double pathCount();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satCount()
         */
        public native double satCount();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satCount(org.sf.javabdd.BDD)
         */
        public double satCount(BDD varset) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#logSatCount()
         */
        public double logSatCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#logSatCount(org.sf.javabdd.BDD)
         */
        public double logSatCount(BDD varset) {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#varProfile()
         */
        public int[] varProfile() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#equals(org.sf.javabdd.BDD)
         */
        public boolean equals(BDD that) {
            return this._ddnode_ptr == ((CUDDBDD) that)._ddnode_ptr;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#hashCode()
         */
        public int hashCode() {
            return (int) this._ddnode_ptr;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#addRef()
         */
        protected native void addRef();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#delRef()
         */
        protected native void delRef();

    }
    
    /**
     * CUDDBDDDomain
     * 
     * @author SUIF User
     * @version $Id$
     */
    public static class CUDDBDDDomain extends BDDDomain {

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#getIndex()
         */
        public int getIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#domain()
         */
        public BDD domain() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#size()
         */
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#buildEquals(org.sf.javabdd.BDDDomain)
         */
        public BDD buildEquals(BDDDomain that) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#set()
         */
        public BDD set() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#ithVar(int)
         */
        public BDD ithVar(int val) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#varNum()
         */
        public int varNum() {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#vars()
         */
        public int[] vars() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * CUDDBDDPairing
     * 
     * @author John Whaley
     * @version $Id$
     */
    public static class CUDDBDDPairing extends BDDPairing {

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public void set(int oldvar, int newvar) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int[], int[])
         */
        public void set(int[] oldvar, int[] newvar) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public void set(BDD oldvar, BDD newvar) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(org.sf.javabdd.BDD[], org.sf.javabdd.BDD[])
         */
        public void set(BDD[] oldvar, BDD[] newvar) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(org.sf.javabdd.BDDDomain, org.sf.javabdd.BDDDomain)
         */
        public void set(BDDDomain p1, BDDDomain p2) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(org.sf.javabdd.BDDDomain[], org.sf.javabdd.BDDDomain[])
         */
        public void set(BDDDomain[] p1, BDDDomain[] p2) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public void reset() {
            // TODO Auto-generated method stub
            
        }
    }

}
