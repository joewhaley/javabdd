package org.sf.javabdd;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of BDDFactory that relies on the BuDDy library through a
 * native interface.  You can use this by calling the "BuDDyFactory.init()"
 * method with the desired arguments.  This will return you an instance of the
 * BDDFactory class that you can use.  Call "done()" on that instance when you
 * are finished.
 * 
 * This class (and the BuDDy library) do NOT support multithreading.
 * Furthermore, there can be only one instance active at a time.  You can only
 * call "init()" again after you have called "done()" on the original instance.
 * 
 * @see org.sf.javabdd.BDDFactory
 * 
 * @author John Whaley
 * @version $Id$
 */
public class BuDDyFactory extends BDDFactory {

    public static BDDFactory init(int nodenum, int cachesize) {
        if (INSTANCE != null) {
            throw new InternalError("Error: BDDFactory already initialized.");
        }
        INSTANCE = new BuDDyFactory();
        INSTANCE.initialize(nodenum, cachesize);
        return INSTANCE;
    }
    
    private static BuDDyFactory INSTANCE;
    
    static {
        String libname = "buddy";
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
    
    private BuDDyFactory() {}

    /**
     * @see org.sf.javabdd.BDDFactory#zero()
     */
    public BDD zero() { return new BuDDyBDD(0); }

    /**
     * @see org.sf.javabdd.BDDFactory#one()
     */
    public BDD one() { return new BuDDyBDD(1); }
    
    /**
     * @see org.sf.javabdd.BDDFactory#buildCube(int, java.util.Collection)
     */
    public BDD buildCube(int value, Collection var) {
        return this.buildCube0(value, (BuDDyBDD[]) var.toArray(new BuDDyBDD[var.size()]));
    }
    protected native BuDDyBDD buildCube0(int value, BuDDyBDD[] var);

    /**
     * @see org.sf.javabdd.BDDFactory#buildCube(int, int[])
     */
    public BDD buildCube(int value, int[] var) {
        return buildCube1(value, var);
    }
    protected native BDD buildCube1(int value, int[] var);

    /**
     * @see org.sf.javabdd.BDDFactory#makeSet(int[])
     */
    public native BDD makeSet(int[] v);
    
    /**
     * @see org.sf.javabdd.BDDFactory#initialize(int, int)
     */
    protected native void initialize(int nodenum, int cachesize);

    /**
     * @see org.sf.javabdd.BDDFactory#isInitialized()
     */
    public native boolean isInitialized();

    /**
     * @see org.sf.javabdd.BDDFactory#done()
     */
    public void done() {
        INSTANCE = null;
        done0();
    }
    protected native void done0();

    /**
     * @see org.sf.javabdd.BDDFactory#setMaxNodeNum(int)
     */
    public native int setMaxNodeNum(int size);

    /**
     * @see org.sf.javabdd.BDDFactory#setMinFreeNodes(int)
     */
    public native void setMinFreeNodes(int x);

    /**
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public native int setMaxIncrease(int x);

    /**
     * @see org.sf.javabdd.BDDFactory#setCacheRatio(int)
     */
    public native int setCacheRatio(int x);

    /**
     * @see org.sf.javabdd.BDDFactory#varNum()
     */
    public native int varNum();

    /**
     * @see org.sf.javabdd.BDDFactory#setVarNum(int)
     */
    public native int setVarNum(int num);

    /**
     * @see org.sf.javabdd.BDDFactory#extVarNum(int)
     */
    public native int extVarNum(int num);

    /**
     * @see org.sf.javabdd.BDDFactory#ithVar(int)
     */
    public native BDD ithVar(int var);

    /**
     * @see org.sf.javabdd.BDDFactory#nithVar(int)
     */
    public native BDD nithVar(int var);

    /**
     * @see org.sf.javabdd.BDDFactory#swapVar(int, int)
     */
    public native void swapVar(int v1, int v2);

    /**
     * @see org.sf.javabdd.BDDFactory#makePair()
     */
    public native BDDPairing makePair();
    
    /**
     * @see org.sf.javabdd.BDDFactory#printAll()
     */
    public native void printAll();

    /**
     * @see org.sf.javabdd.BDDFactory#printTable(org.sf.javabdd.BDD)
     */
    public void printTable(BDD b) { printTable0((BuDDyBDD) b); }
    protected native void printTable0(BuDDyBDD b);

    /**
     * @see org.sf.javabdd.BDDFactory#load(java.lang.String)
     */
    public native BDD load(String filename);

    /**
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD v) { save0(filename, (BuDDyBDD) v); }
    protected native void save0(String filename, BuDDyBDD v);

    /**
     * @see org.sf.javabdd.BDDFactory#level2Var(int)
     */
    public native int level2Var(int level);

    /**
     * @see org.sf.javabdd.BDDFactory#var2Level(int)
     */
    public native int var2Level(int var);

    /**
     * @see org.sf.javabdd.BDDFactory#reorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public native void reorder(BDDFactory.ReorderMethod m);

    /**
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void autoReorder(BDDFactory.ReorderMethod method) {
        autoReorder0(method);
    }
    protected native void autoReorder0(BDDFactory.ReorderMethod method);

    /**
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod, int)
     */
    public void autoReorder(BDDFactory.ReorderMethod method, int max) {
        autoReorder1(method, max);
    }
    protected native void autoReorder1(BDDFactory.ReorderMethod method, int max);

    /**
     * @see org.sf.javabdd.BDDFactory#getReorderMethod()
     */
    public native BDDFactory.ReorderMethod getReorderMethod();

    /**
     * @see org.sf.javabdd.BDDFactory#getReorderTimes()
     */
    public native int getReorderTimes();

    /**
     * @see org.sf.javabdd.BDDFactory#disableReorder()
     */
    public native void disableReorder();

    /**
     * @see org.sf.javabdd.BDDFactory#enableReorder()
     */
    public native void enableReorder();

    /**
     * @see org.sf.javabdd.BDDFactory#reorderVerbose(int)
     */
    public native int reorderVerbose(int v);

    /**
     * @see org.sf.javabdd.BDDFactory#setVarOrder(int[])
     */
    public native void setVarOrder(int[] neworder);
    
    /**
     * @see org.sf.javabdd.BDDFactory#addVarBlock(org.sf.javabdd.BDD, boolean)
     */
    public void addVarBlock(BDD var, boolean fixed) { addVarBlock0((BuDDyBDD) var, fixed); }
    private native void addVarBlock0(BuDDyBDD var, boolean fixed);

    /**
     * @see org.sf.javabdd.BDDFactory#addVarBlock(int, int, boolean)
     */
    public void addVarBlock(int first, int last, boolean fixed) {
        addVarBlock1(first, last, fixed);
    }
    protected native void addVarBlock1(int first, int last, boolean fixed);

    /**
     * @see org.sf.javabdd.BDDFactory#varBlockAll()
     */
    public native void varBlockAll();

    /**
     * @see org.sf.javabdd.BDDFactory#clearVarBlocks()
     */
    public native void clearVarBlocks();

    /**
     * @see org.sf.javabdd.BDDFactory#printOrder()
     */
    public native void printOrder();

    /**
     * @see org.sf.javabdd.BDDFactory#nodeCount(java.util.Collection)
     */
    public native int nodeCount(Collection r);

    /**
     * @see org.sf.javabdd.BDDFactory#getAllocNum()
     */
    public native int getAllocNum();

    /**
     * @see org.sf.javabdd.BDDFactory#getNodeNum()
     */
    public native int getNodeNum();

    /**
     * @see org.sf.javabdd.BDDFactory#reorderGain()
     */
    public native int reorderGain();

    /**
     * @see org.sf.javabdd.BDDFactory#printStat()
     */
    public native void printStat();

    protected BDDDomain createDomain(int a, long b) {
        return new BuDDyBDDDomain(a, b);
    }

    /**
     * An implementation of a BDD class, used by the BuDDy interface.
     */
    public static class BuDDyBDD extends BDD {
    
        private int _id;
        
        private BuDDyBDD(int id) {
            this._id = id;
            this.addRef();
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#getFactory()
         */
        public BDDFactory getFactory() {
            return INSTANCE;
        }
        
        /**
         * @see org.sf.javabdd.BDD#isZero()
         */
        public boolean isZero() {
            return _id == 0;
        }
        
        /**
         * @see org.sf.javabdd.BDD#isOne()
         */
        public boolean isOne() {
            return _id == 1;
        }
        
        /**
         * @see org.sf.javabdd.BDD#var()
         */
        public native int var();
        
        /**
         * @see org.sf.javabdd.BDD#level()
         */
        public native int level();
        
        /**
         * @see org.sf.javabdd.BDD#high()
         */
        public native BDD high();
        
        /**
         * @see org.sf.javabdd.BDD#low()
         */
        public native BDD low();
        
        /**
         * @see org.sf.javabdd.BDD#id()
         */
        public native BDD id();
        
        /**
         * @see org.sf.javabdd.BDD#not()
         */
        public native BDD not();
        
        /**
         * @see org.sf.javabdd.BDD#ite(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD ite(BDD thenBDD, BDD elseBDD) {
            return ite0((BuDDyBDD) thenBDD, (BuDDyBDD) elseBDD);
        }
        protected native BuDDyBDD ite0(BuDDyBDD thenBDD, BuDDyBDD elseBDD);
        
        /**
         * @see org.sf.javabdd.BDD#relprod(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD relprod(BDD that, BDD var) {
            return relprod0((BuDDyBDD) that, (BuDDyBDD) var);
        }
        protected native BuDDyBDD relprod0(BuDDyBDD that, BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#compose(org.sf.javabdd.BDD, int)
         */
        public BDD compose(BDD that, int var) {
            return compose0((BuDDyBDD) that, var);
        }
        protected native BuDDyBDD compose0(BuDDyBDD that, int var);
        
        /**
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            return constrain0((BuDDyBDD) that);
        }
        protected native BuDDyBDD constrain0(BuDDyBDD that);
        
        /**
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            return exist0((BuDDyBDD) var);
        }
        protected native BuDDyBDD exist0(BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#forAll(org.sf.javabdd.BDD)
         */
        public BDD forAll(BDD var) {
            return forAll0((BuDDyBDD) var);
        }
        protected native BuDDyBDD forAll0(BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#unique(org.sf.javabdd.BDD)
         */
        public BDD unique(BDD var) {
            return unique0((BuDDyBDD) var);
        }
        protected native BuDDyBDD unique0(BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#restrict(org.sf.javabdd.BDD)
         */
        public BDD restrict(BDD var) {
            return restrict0((BuDDyBDD) var);
        }
        protected native BuDDyBDD restrict0(BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#simplify(org.sf.javabdd.BDD)
         */
        public BDD simplify(BDD d) {
            return simplify0((BuDDyBDD) d);
        }
        protected native BuDDyBDD simplify0(BuDDyBDD d);
        
        /**
         * @see org.sf.javabdd.BDD#support()
         */
        public native BDD support();
        
        /**
         * @see org.sf.javabdd.BDD#apply(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public BDD apply(BDD that, BDDFactory.BDDOp opr) {
            return apply0((BuDDyBDD) that, opr);
        }
        protected native BuDDyBDD apply0(BuDDyBDD that, BDDFactory.BDDOp opr);
        
        /**
         * @see org.sf.javabdd.BDD#applyWith(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public void applyWith(BDD that, BDDFactory.BDDOp opr) {
            applyWith0((BuDDyBDD) that, opr);
        }
        protected native void applyWith0(BuDDyBDD that, BDDFactory.BDDOp opr);
        
        /**
         * @see org.sf.javabdd.BDD#applyAll(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyAll(BDD that, BDDFactory.BDDOp opr, BDD var) {
            return applyAll0((BuDDyBDD) that, opr, (BuDDyBDD) var);
        }
        protected native BuDDyBDD applyAll0(BuDDyBDD that, BDDFactory.BDDOp opr, BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDFactory.BDDOp opr, BDD var) {
            return applyEx0((BuDDyBDD) that, opr, (BuDDyBDD) var);
        }
        protected native BuDDyBDD applyEx0(BuDDyBDD that, BDDFactory.BDDOp opr, BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDFactory.BDDOp opr, BDD var) {
            return applyUni0((BuDDyBDD) that, opr, (BuDDyBDD) var);
        }
        protected native BuDDyBDD applyUni0(BuDDyBDD that, BDDFactory.BDDOp opr, BuDDyBDD var);
        
        /**
         * @see org.sf.javabdd.BDD#satOne()
         */
        public native BDD satOne();
        
        /**
         * @see org.sf.javabdd.BDD#fullSatOne()
         */
        public native BDD fullSatOne();
        
        /**
         * @see org.sf.javabdd.BDD#satOneSet(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOneSet(BDD var, BDD pol) {
            return satOneSet0((BuDDyBDD) var, (BuDDyBDD) pol);
        }
        protected native BuDDyBDD satOneSet0(BuDDyBDD var, BuDDyBDD pol);
        
        /**
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            return Arrays.asList(allsat0());
        }
        protected native byte[][] allsat0();
        
        /**
         * @see org.sf.javabdd.BDD#printSet()
         */
        public native void printSet();
        
        /**
         * @see org.sf.javabdd.BDD#printDot()
         */
        public native void printDot();

        /**
         * @see org.sf.javabdd.BDD#nodeCount()
         */
        public native int nodeCount();
        
        /**
         * @see org.sf.javabdd.BDD#pathCount()
         */
        public native double pathCount();
        
        /**
         * @see org.sf.javabdd.BDD#satCount()
         */
        public double satCount() {
            return satCount0();
        }
        protected native double satCount0();
        
        /**
         * @see org.sf.javabdd.BDD#satCount(org.sf.javabdd.BDD)
         */
        public double satCount(BDD varset) {
            return satCount1((BuDDyBDD) varset);
        }
        protected native double satCount1(BuDDyBDD varset);
        
        /**
         * @see org.sf.javabdd.BDD#logSatCount()
         */
        public double logSatCount() {
            return logSatCount0();
        }
        protected native double logSatCount0();
        
        /**
         * @see org.sf.javabdd.BDD#logSatCount(org.sf.javabdd.BDD)
         */
        public double logSatCount(BDD varset) {
            return logSatCount1((BuDDyBDD) varset);
        }
        protected native double logSatCount1(BuDDyBDD varset);
        
        /**
         * @see org.sf.javabdd.BDD#varProfile()
         */
        public native int[] varProfile();
        
        /**
         * @see org.sf.javabdd.BDD#addRef()
         */
        protected native void addRef();
        
        /**
         * @see org.sf.javabdd.BDD#delRef()
         */
        protected native void delRef();
        
        /**
         * @see java.lang.Object#finalize()
         */
        /*
        protected void finalize() throws Throwable {
            super.finalize();
            if (false && _id >= 0) {
                System.out.println("BDD not freed! "+System.identityHashCode(this));
            }
            this.delRef();
        }
        */
        
        /**
         * @see org.sf.javabdd.BDD#veccompose(org.sf.javabdd.BDDPairing)
         */
        public BDD veccompose(BDDPairing pair) {
            return veccompose0((BuDDyBDDPairing) pair);
        }
        protected native BuDDyBDD veccompose0(BuDDyBDDPairing pair);
        
        /**
         * @see org.sf.javabdd.BDD#replace(org.sf.javabdd.BDDPairing)
         */
        public BDD replace(BDDPairing pair) {
            return replace0((BuDDyBDDPairing) pair);
        }
        protected native BuDDyBDD replace0(BuDDyBDDPairing pair);
        
        /**
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public void replaceWith(BDDPairing pair) {
            replaceWith0((BuDDyBDDPairing) pair); 
        }
        protected native void replaceWith0(BuDDyBDDPairing pair);
        
        /**
         * @see org.sf.javabdd.BDD#equals(org.sf.javabdd.BDD)
         */
        public boolean equals(BDD that) {
            return this._id == ((BuDDyBDD) that)._id;
        }
        
        /**
         * @see org.sf.javabdd.BDD#hashCode()
         */
        public int hashCode() {
            return this._id;
        }

    }
    
    /**
     * An implementation of a BDDDomain, used by the BuDDy interface.
     */
    public static class BuDDyBDDDomain extends BDDDomain {

        private BuDDyBDDDomain(int a, long b) {
            super(a, b);
        }

        public BDDFactory getFactory() { return INSTANCE; }

    }
    
    /**
     * An implementation of a BDDPairing, used by the BuDDy interface.
     */
    public static class BuDDyBDDPairing extends BDDPairing {
        
        private long _ptr;
        
        private BuDDyBDDPairing(long ptr) {
                this._ptr = ptr;
        }
        
        /**
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public void set(int oldvar, int newvar) {
            set0(oldvar, newvar);
        }
        protected native void set0(int oldvar, int newvar);
        
        /**
         * @see org.sf.javabdd.BDDPairing#set(int[], int[])
         */
        public void set(int[] oldvar, int[] newvar) {
            set1(oldvar, newvar);
        }
        protected native void set1(int[] oldvar, int[] newvar);
        
        /**
         * @see org.sf.javabdd.BDDPairing#set(int, org.sf.javabdd.BDD)
         */
        public void set(int oldvar, BDD newvar) {
            set2(oldvar, (BuDDyBDD) newvar);
        }
        protected native void set2(int oldvar, BuDDyBDD newvar);
        
        /**
         * @see org.sf.javabdd.BDDPairing#set(int[], org.sf.javabdd.BDD[])
         */
        public void set(int[] oldvar, BDD[] newvar) {
            set3(oldvar, newvar);
        }
        protected native void set3(int[] oldvar, BDD[] newvar);
        
        /**
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public native void reset();
        
        /**
         * @see java.lang.Object#finalize()
         */
        protected void finalize() throws Throwable {
            super.finalize();
            this.free();
        }

        /**
         * Free the memory allocated for this pair.
         */
        protected native void free();
        
    }
    
    protected BDDBitVector createBitVector(int a) {
        return new BuDDyBDDBitVector(a);
    }
    
    /**
     * An implementation of a BDDDomain, used by the BuDDy interface.
     */
    public static class BuDDyBDDBitVector extends BDDBitVector {

        private BuDDyBDDBitVector(int a) {
            super(a);
        }

        public BDDFactory getFactory() { return INSTANCE; }

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
        
        bdd.printTable(c);
    }
}
