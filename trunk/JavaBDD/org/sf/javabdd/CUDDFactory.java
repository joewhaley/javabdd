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
        INSTANCE.initialize(nodenum/256, cachesize);
        return INSTANCE;
    }
    
    private static CUDDFactory INSTANCE;
    
    static {
        String libname = "cudd";
        try {
            System.loadLibrary(libname);
        } catch (java.lang.UnsatisfiedLinkError x) {
            // Cannot find library, try loading it from the current directory...
            libname = System.mapLibraryName(libname);
            String currentdir = System.getProperty("user.dir");
            String sep = System.getProperty("file.separator");
            System.load(currentdir+sep+libname);
        }
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
    public void done() {
        done0();
        INSTANCE = null;
    }
    private native void done0();

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxNodeNum(int)
     */
    public int setMaxNodeNum(int size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMinFreeNodes(int)
     */
    public void setMinFreeNodes(int x) {
        // TODO Auto-generated method stub
        System.err.println("Warning: setMinFreeNodes() not yet implemented");
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public int setMaxIncrease(int x) {
        // TODO Auto-generated method stub
        System.err.println("Warning: setMaxIncrease() not yet implemented");
        return 50000;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setCacheRatio(int)
     */
    public int setCacheRatio(int x) {
        // TODO Auto-generated method stub
        System.err.println("Warning: setCacheRatio() not yet implemented");
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
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printTable(org.sf.javabdd.BDD)
     */
    public void printTable(BDD b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#load(java.lang.String)
     */
    public BDD load(String filename) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD var) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void autoReorder(ReorderMethod method) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod, int)
     */
    public void autoReorder(ReorderMethod method, int max) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderMethod()
     */
    public ReorderMethod getReorderMethod() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderTimes()
     */
    public int getReorderTimes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#disableReorder()
     */
    public void disableReorder() {
        // TODO Auto-generated method stub
        System.err.println("Warning: disableReorder() not yet implemented");
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#enableReorder()
     */
    public void enableReorder() {
        // TODO Auto-generated method stub
        System.err.println("Warning: enableReorder() not yet implemented");
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderVerbose(int)
     */
    public int reorderVerbose(int v) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarOrder(int[])
     */
    public native void setVarOrder(int[] neworder);

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(org.sf.javabdd.BDD, boolean)
     */
    public void addVarBlock(BDD var, boolean fixed) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(int, int, boolean)
     */
    public void addVarBlock(int first, int last, boolean fixed) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varBlockAll()
     */
    public void varBlockAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearVarBlocks()
     */
    public void clearVarBlocks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printOrder()
     */
    public void printOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nodeCount(java.util.Collection)
     */
    public int nodeCount(Collection r) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getAllocNum()
     */
    public int getAllocNum() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getNodeNum()
     */
    public int getNodeNum() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderGain()
     */
    public int reorderGain() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printStat()
     */
    public void printStat() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makePair()
     */
    public BDDPairing makePair() {
        return new CUDDBDDPairing();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#swapVar(int, int)
     */
    public void swapVar(int v1, int v2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    protected BDDDomain createDomain(int a, long b) {
        return new CUDDBDDDomain(a, b);
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
        public native BDD veccompose(BDDPairing pair);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#forAll(org.sf.javabdd.BDD)
         */
        public BDD forAll(BDD var) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#unique(org.sf.javabdd.BDD)
         */
        public BDD unique(BDD var) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDOp opr, BDD var) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDOp opr, BDD var) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOneSet(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOneSet(BDD var, BDD pol) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replace(org.sf.javabdd.BDDPairing)
         */
        public native BDD replace(BDDPairing p);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public void replaceWith(BDDPairing pair) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#printDot()
         */
        public void printDot() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
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
         * @see org.sf.javabdd.BDD#varProfile()
         */
        public int[] varProfile() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
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

        private CUDDBDDDomain(int index, long range) {
            super(index, range);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#getFactory()
         */
        public BDDFactory getFactory() {
            return INSTANCE;
        }
        
    }

    /**
     * CUDDBDDPairing
     * 
     * @author SUIF User
     * @version $Id$
     */
    public static class CUDDBDDPairing extends BDDPairing {

        long _ptr;

        private CUDDBDDPairing() {
            _ptr = alloc();
        }

        private static native long alloc();

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public native void set(int oldvar, int newvar);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, org.sf.javabdd.BDD)
         */
        public native void set(int oldvar, BDD newvar);

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public native void reset();
        
    }

    public static void main(String[] args) {
        BDDFactory bdd = init(1000000, 100000);
        
        BDDDomain[] doms = bdd.extDomain(new int[] {50, 10, 15, 20, 15});
        
        BDD b = bdd.one();
        for (int i=0; i<doms.length-1; ++i) {
            b.andWith(doms[i].ithVar(i));
        }
        
        for (int i=0; i<bdd.numberOfDomains(); ++i) {
            BDDDomain d = bdd.getDomain(i);
            int[] ivar = d.vars();
            System.out.print("Domain #"+i+":");
            for (int j=0; j<ivar.length; ++j) {
                System.out.print(' ');
                System.out.print(j);
                System.out.print(':');
                System.out.print(ivar[j]);
            }
            System.out.println();
        }
        
        BDDPairing p = bdd.makePair(doms[2], doms[doms.length-1]);
        System.out.println("Pairing: "+p);
        
        System.out.println("Before replace(): "+b);
        BDD c = b.replace(p);
        System.out.println("After replace(): "+c);
    }

    
    protected BDDBitVector createBitVector(int a) {
        return new CUDDBDDBitVector(a);
    }
    
    /**
     * An implementation of a BDDDomain, used by the BuDDy interface.
     */
    public static class CUDDBDDBitVector extends BDDBitVector {

        private CUDDBDDBitVector(int a) {
            super(a);
        }

        public BDDFactory getFactory() { return INSTANCE; }

    }
    
}
