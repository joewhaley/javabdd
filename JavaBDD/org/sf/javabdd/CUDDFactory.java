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

    protected native BDD makeNode(int level, BDD low, BDD high);
    
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
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public int setMaxIncrease(int x) {
        // TODO Auto-generated method stub
        System.err.println("Warning: setMaxIncrease() not yet implemented");
        return 0;
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
    public void setVarOrder(int[] neworder) {
        // TODO Auto-generated method stub
        System.err.println("Warning: setVarOrder() not yet implemented");
    }

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

    protected CUDDBDDDomain[] domain;
    protected int fdvarnum;
    protected int firstbddvar;

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#extDomain(int[])
     */
    public BDDDomain[] extDomain(int[] dom) {
        int offset = fdvarnum;
        int binoffset;
        int extravars = 0;
        int n, bn;
        boolean more;
        int num = dom.length;

        /* Build domain table */
        if (domain == null) /* First time */ {
            domain = new CUDDBDDDomain[num];
        } else /* Allocated before */ {
            if (fdvarnum + num > domain.length) {
                int fdvaralloc = domain.length + Math.max(num, domain.length);
                CUDDBDDDomain[] d2 = new CUDDBDDDomain[fdvaralloc];
                System.arraycopy(domain, 0, d2, 0, domain.length);
                domain = d2;
            }
        }

        /* Create bdd variable tables */
        for (n = 0; n < num; n++) {
            domain[n + fdvarnum] = new CUDDBDDDomain(n + fdvarnum, dom[n]);
            extravars += domain[n + fdvarnum].varNum();
        }

        binoffset = firstbddvar;
        int bddvarnum = INSTANCE.varNum();
        if (firstbddvar + extravars > bddvarnum)
            INSTANCE.setVarNum(firstbddvar + extravars);

        /* Set correct variable sequence (interleaved) */
        for (bn = 0, more = true; more; bn++) {
            more = false;

            for (n = 0; n < num; n++) {
                if (bn < domain[n + fdvarnum].varNum()) {
                    more = true;
                    domain[n + fdvarnum].ivar[bn] = binoffset++;
                }
            }
        }

        for (n = 0; n < num; n++) {
            domain[n + fdvarnum].var =
                INSTANCE.makeSet(domain[n + fdvarnum].ivar);
            //domain[n+fdvarnum].var.addRef();
        }

        fdvarnum += num;
        firstbddvar += extravars;

        BDDDomain[] r = new BDDDomain[num];
        System.arraycopy(domain, offset, r, 0, num);
        return r;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#overlapDomain(org.sf.javabdd.BDDDomain, org.sf.javabdd.BDDDomain)
     */
    public BDDDomain overlapDomain(BDDDomain d1, BDDDomain d2) {
        CUDDBDDDomain d;
        int n;

        CUDDBDDDomain cd1 = (CUDDBDDDomain) d1;
        CUDDBDDDomain cd2 = (CUDDBDDDomain) d2;

        int fdvaralloc = domain.length;
        if (fdvarnum + 1 > fdvaralloc) {
            fdvaralloc += fdvaralloc;
            CUDDBDDDomain[] domain2 = new CUDDBDDDomain[fdvaralloc];
            System.arraycopy(domain, 0, domain2, 0, domain.length);
            domain = domain2;
        }

        d = domain[fdvarnum];
        d.realsize = cd1.realsize * cd2.realsize;
        d.ivar = new int[cd1.varNum() + cd2.varNum()];

        for (n = 0; n < cd1.varNum(); n++)
            d.ivar[n] = cd1.ivar[n];
        for (n = 0; n < cd2.varNum(); n++)
            d.ivar[cd1.varNum() + n] = cd2.ivar[n];

        d.var = INSTANCE.makeSet(d.ivar);
        //bdd_addref(d.var);

        fdvarnum++;
        return d;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearAllDomains()
     */
    public void clearAllDomains() {
        domain = null;
        fdvarnum = 0;
        firstbddvar = 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#numberOfDomains()
     */
    public int numberOfDomains() {
        return fdvarnum;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getDomain(int)
     */
    public BDDDomain getDomain(int i) {
        if (i < 0 || i >= fdvarnum)
            throw new IndexOutOfBoundsException();
        return domain[i];
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
            throw new UnsupportedOperationException();
        }

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
        public BDD replace(BDDPairing p) {
            // TODO very inefficient.
            if (this.isZero())
                return INSTANCE.zero();
            BDD res;
            CUDDBDDPairing pair = (CUDDBDDPairing) p;
            CUDDBDD[] replacepair = pair.result;
            int replacelast = pair.last;
            //int replaceid = pair.id << 2 | CACHEID_REPLACE;
            INSTANCE.disableReorder();
            res = replace_rec(this, replacepair, replacelast);
            INSTANCE.enableReorder();
            return res;
        }

        static BDD replace_rec(BDD r, CUDDBDD[] replacepair, int replacelast) {
            BDD res;
   
            if (r.isZero() || r.isOne() || r.level() > replacelast)
                return r;

            BDD low = replace_rec(r.low(), replacepair, replacelast);
            BDD high = replace_rec(r.high(), replacepair, replacelast);
            res = bdd_correctify(replacepair[r.level()].level(), low, high);
            low.free();
            high.free();

            return res;
        }

        static BDD bdd_correctify(int level, BDD l, BDD r) {
            BDD res;
   
            if (level < l.level() && level < r.level())
                return INSTANCE.makeNode(level, l, r);

            if (level == l.level() || level == r.level()) {
                throw new BDDException();
            }

            if (l.level() == r.level()) {
                BDD low = bdd_correctify(level, l.low(), r.low());
                BDD high = bdd_correctify(level, l.high(), r.high());
                res = INSTANCE.makeNode(l.level(), low, high);
                low.free(); high.free();
            } else if (l.level() < r.level()) {
                BDD low = bdd_correctify(level, l.low(), r);
                BDD high = bdd_correctify(level, l.high(), r);
                res = INSTANCE.makeNode(l.level(), low, high);
                low.free(); high.free();
            } else {
                BDD low = bdd_correctify(level, l, r.low());
                BDD high = bdd_correctify(level, l, r.high());
                res = INSTANCE.makeNode(r.level(), low, high);
                low.free(); high.free();
            }
            
            return res;
        }

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

        /* The index of this domain. */
        int index;

        /* The specified domain (0...N-1) */
        int realsize;
        /* Variable indices for the variable set */
        int[] ivar;
        /* The BDD variable set */
        BDD var;

        private CUDDBDDDomain(int index, int range) {
            
            int calcsize = 2;
            
            if (range <= 0  || range > Integer.MAX_VALUE/2)
                throw new InternalError();

            this.index = index;
            
            this.realsize = range;
            int binsize = 1;

            while (calcsize < range)
            {
               binsize++;
               calcsize <<= 1;
            }

            this.ivar = new int[binsize];
            this.var = INSTANCE.one();
            
        }

        public BDDFactory getFactory() { return INSTANCE; }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#getIndex()
         */
        public int getIndex() {
            return index;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#size()
         */
        public int size() {
            return this.realsize;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#set()
         */
        public BDD set() {
            return var.id();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#varNum()
         */
        public int varNum() {
            return this.ivar.length;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#vars()
         */
        public int[] vars() {
            return this.ivar;
        }
    }

    /**
     * CUDDBDDPairing
     * 
     * @author SUIF User
     * @version $Id$
     */
    public static class CUDDBDDPairing extends BDDPairing {

        CUDDBDD[] result;
        int last;

        private CUDDBDDPairing() {
            this.result = new CUDDBDD[INSTANCE.varNum()];
            for (int n = 0; n < result.length; n++)
                this.result[n] =
                    (CUDDBDD) INSTANCE.ithVar(INSTANCE.level2Var(n));

            this.last = -1;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public void set(int oldvar, int newvar) {
            int bddvarnum = INSTANCE.varNum();
            if (oldvar < 0 || oldvar > bddvarnum - 1)
                throw new BDDException();
            if (newvar < 0 || newvar > bddvarnum - 1)
                throw new BDDException();

            this.result[INSTANCE.var2Level(oldvar)].free();
            this.result[INSTANCE.var2Level(oldvar)] = (CUDDBDD) INSTANCE.ithVar(newvar);

            this.last = Math.max(this.last, INSTANCE.var2Level(oldvar));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, org.sf.javabdd.BDD)
         */
        public void set(int oldvar, BDD newvar) {
            int bddvarnum = INSTANCE.varNum();
            if (oldvar < 0 || oldvar >= bddvarnum)
                throw new BDDException();
            int oldlevel = INSTANCE.var2Level(oldvar);

            this.result[oldlevel].free();
            this.result[oldlevel] = (CUDDBDD) newvar.id();

            this.last = Math.max(oldlevel, this.last);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public void reset() {
            for (int n = 0; n < this.result.length; n++)
                this.result[n] = (CUDDBDD) INSTANCE.ithVar(INSTANCE.level2Var(n));
            this.last = 0;
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (int i=0; i<result.length; ++i) {
                if (i > 0) sb.append(',');
                sb.append(i);
                sb.append('=');
                sb.append(result[i]);
            }
            sb.append(" last=");
            sb.append(last);
            return sb.toString();
        }
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

}
