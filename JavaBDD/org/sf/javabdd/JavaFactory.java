// JavaFactory.java, created Aug 1, 2003 7:06:47 PM by joewhaley
// Copyright (C) 2003 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package org.sf.javabdd;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * This is a 100% Java implementation of the BDD factory.  It is based on
 * the C source code for BuDDy.  As such, the implementation is very ugly,
 * but it works.  Like BuDDy, it uses reference counting scheme for garbage
 * collection.
 * 
 * @author John Whaley
 * @version $Id$
 */
public class JavaFactory extends BDDFactory {

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#init(int, int)
     */
    public static BDDFactory init(int nodenum, int cachesize) {
        BDDFactory INSTANCE = new JavaFactory();
        INSTANCE.initialize(nodenum, cachesize);
        return INSTANCE;
    }

    /**
     * Wrapper for the BDD index number used internally in the representation.
     */
    class bdd extends BDD {
        int _index;

        bdd(int index) {
            this._index = index;
            bdd_addref(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#getFactory()
         */
        public BDDFactory getFactory() {
            return JavaFactory.this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isZero()
         */
        public boolean isZero() {
            return _index == bddfalse;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isOne()
         */
        public boolean isOne() {
            return _index == bddtrue;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#var()
         */
        public int var() {
            return bdd_var(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#high()
         */
        public BDD high() {
            return new bdd(HIGH(_index));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#low()
         */
        public BDD low() {
            return new bdd(LOW(_index));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#id()
         */
        public BDD id() {
            return new bdd(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#not()
         */
        public BDD not() {
            return new bdd(bdd_not(_index));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#ite(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD ite(BDD thenBDD, BDD elseBDD) {
            int x = _index;
            int y = ((bdd) thenBDD)._index;
            int z = ((bdd) elseBDD)._index;
            return new bdd(bdd_ite(x, y, z));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#relprod(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD relprod(BDD that, BDD var) {
            int x = _index;
            int y = ((bdd) that)._index;
            int z = ((bdd) var)._index;
            return new bdd(bdd_relprod(x, y, z));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#compose(org.sf.javabdd.BDD, int)
         */
        public BDD compose(BDD g, int var) {
            int x = _index;
            int y = ((bdd) g)._index;
            return new bdd(bdd_compose(x, y, var));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#veccompose(org.sf.javabdd.BDDPairing)
         */
        public BDD veccompose(BDDPairing pair) {
            int x = _index;
            return new bdd(bdd_veccompose(x, ((bddPair) pair)));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            int x = _index;
            int y = ((bdd) that)._index;
            return new bdd(bdd_constrain(x, y));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            int x = _index;
            int y = ((bdd) var)._index;
            return new bdd(bdd_exist(x, y));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#forAll(org.sf.javabdd.BDD)
         */
        public BDD forAll(BDD var) {
            int x = _index;
            int y = ((bdd) var)._index;
            return new bdd(bdd_forall(x, y));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#unique(org.sf.javabdd.BDD)
         */
        public BDD unique(BDD var) {
            int x = _index;
            int y = ((bdd) var)._index;
            return new bdd(bdd_unique(x, y));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrict(org.sf.javabdd.BDD)
         */
        public BDD restrict(BDD var) {
            int x = _index;
            int y = ((bdd) var)._index;
            return new bdd(bdd_restrict(x, y));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#simplify(org.sf.javabdd.BDD)
         */
        public BDD simplify(BDD d) {
            int x = _index;
            int y = ((bdd) d)._index;
            return new bdd(bdd_simplify(x, y));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#support()
         */
        public BDD support() {
            int x = _index;
            return new bdd(bdd_support(x));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#apply(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public BDD apply(BDD that, BDDOp opr) {
            int x = _index;
            int y = ((bdd) that)._index;
            int z = opr.id;
            return new bdd(bdd_apply(x, y, z));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyWith(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public void applyWith(BDD that, BDDOp opr) {
            int x = _index;
            int y = ((bdd) that)._index;
            int z = opr.id;
            int a = bdd_apply(x, y, z);
            bdd_delref(x);
            if (this != that)
                bdd_delref(y);
            bdd_addref(a);
            this._index = a;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyAll(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyAll(BDD that, BDDOp opr, BDD var) {
            int x = _index;
            int y = ((bdd) that)._index;
            int z = opr.id;
            int a = ((bdd) var)._index;
            return new bdd(bdd_appall(x, y, z, a));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDOp opr, BDD var) {
            int x = _index;
            int y = ((bdd) that)._index;
            int z = opr.id;
            int a = ((bdd) var)._index;
            return new bdd(bdd_appex(x, y, z, a));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDOp opr, BDD var) {
            int x = _index;
            int y = ((bdd) that)._index;
            int z = opr.id;
            int a = ((bdd) var)._index;
            return new bdd(bdd_appuni(x, y, z, a));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOne()
         */
        public BDD satOne() {
            int x = _index;
            return new bdd(bdd_satone(x));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#fullSatOne()
         */
        public BDD fullSatOne() {
            int x = _index;
            return new bdd(bdd_fullsatone(x));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOneSet(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOneSet(BDD var, BDD pol) {
            int x = _index;
            int y = ((bdd) var)._index;
            int z = ((bdd) pol)._index;
            return new bdd(bdd_satoneset(x, y, z));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            int x = _index;
            List result = new LinkedList();
            bdd_allsat(x, result);
            return result;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replace(org.sf.javabdd.BDDPairing)
         */
        public BDD replace(BDDPairing pair) {
            int x = _index;
            return new bdd(bdd_replace(x, ((bddPair) pair)));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public void replaceWith(BDDPairing pair) {
            int x = _index;
            int y = bdd_replace(x, ((bddPair) pair));
            bdd_delref(x);
            bdd_addref(y);
            _index = y;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#nodeCount()
         */
        public int nodeCount() {
            return bdd_nodecount(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#pathCount()
         */
        public double pathCount() {
            return bdd_pathcount(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satCount()
         */
        public double satCount() {
            return bdd_satcount(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#varProfile()
         */
        public int[] varProfile() {
            int x = _index;
            return bdd_varprofile(x);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#equals(org.sf.javabdd.BDD)
         */
        public boolean equals(BDD that) {
            return this._index == ((bdd) that)._index;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#hashCode()
         */
        public int hashCode() {
            return _index;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#addRef()
         */
        protected void addRef() {
            bdd_addref(_index);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#delRef()
         */
        protected void delRef() {
            bdd_delref(_index);
        }
    }

    /**
     * Actual data structure for each BDD node.
     */
    static class BddNode {
        //unsigned int refcou : 10;
        //unsigned int level  : 22;
        int refcou_and_level;
        int low;
        int high;
        int hash;
        int next;

        static final int REF_MASK = 0xFFC00000;
        static final int MARK_MASK = 0x00200000;
        static final int LEV_MASK = 0x001FFFFF;

        static final int REF_INC = 0x00400000;

        final boolean hasRef() {
            boolean r = (refcou_and_level & REF_MASK) != 0;
            return r;
        }

        final void setMaxRef() {
            refcou_and_level |= REF_MASK;
        }

        final void clearRef() {
            refcou_and_level &= ~REF_MASK;
        }

        final void incRef() {
            if ((refcou_and_level & REF_MASK) != REF_MASK)
                refcou_and_level += REF_INC;
        }

        final void decRef() {
            int rc = refcou_and_level & REF_MASK;
            if (rc != REF_MASK && rc != 0)
                refcou_and_level -= REF_INC;
        }

        final int getRef() {
            return refcou_and_level >>> 22;
        }

        final void mark() {
            refcou_and_level |= MARK_MASK;
        }

        final void unmark() {
            refcou_and_level &= ~MARK_MASK;
        }

        final boolean isMarked() {
            return (refcou_and_level & MARK_MASK) != 0;
        }

        int getLevel() {
            return refcou_and_level & LEV_MASK;
        }

        int getLevelAndMarked() {
            return refcou_and_level & (LEV_MASK | MARK_MASK);
        }

        void setLevel(int val) {
            _assert(val == (val & LEV_MASK));
            refcou_and_level &= ~LEV_MASK;
            refcou_and_level |= val;
        }

        void setLevelAndMarked(int val) {
            _assert(val == (val & (LEV_MASK | MARK_MASK)));
            refcou_and_level &= ~(LEV_MASK | MARK_MASK);
            refcou_and_level |= val;
        }
    }

    final void SETMARK(int n) {
        bddnodes[n].mark();
    }
    final void UNMARK(int n) {
        bddnodes[n].unmark();
    }
    final boolean MARKED(int n) {
        return bddnodes[n].isMarked();
    }

    static void _assert(boolean b) {
        if (!b)
            throw new InternalError();
    }

    abstract static class BddCacheData {
        int a, b, c;
    }

    static class BddCacheDataI extends BddCacheData {
        int res;
    }

    static class BddCacheDataD extends BddCacheData {
        double dres;
    }

    static class BddCache {
        BddCacheData table[];
        int tablesize;
    }

    static class bddCacheStat {
        int uniqueAccess;
        int uniqueChain;
        int uniqueHit;
        int uniqueMiss;
        int opHit;
        int opMiss;
        int swapCount;
    }

    static class JavaBDDException extends BDDException {
        public JavaBDDException(int x) {
            super(errorstrings[-x]);
        }
    }

    static final int bddtrue = 1;
    static final int bddfalse = 0;

    static final int BDDONE = 1;
    static final int BDDZERO = 0;

    boolean bddrunning; /* Flag - package initialized */
    int bdderrorcond; /* Some error condition */
    int bddnodesize; /* Number of allocated nodes */
    int bddmaxnodesize; /* Maximum allowed number of nodes */
    int bddmaxnodeincrease; /* Max. # of nodes used to inc. table */
    BddNode[] bddnodes; /* All of the bdd nodes */
    int bddfreepos; /* First free node */
    int bddfreenum; /* Number of free nodes */
    int bddproduced; /* Number of new nodes ever produced */
    int bddvarnum; /* Number of defined BDD variables */
    int[] bddrefstack; /* Internal node reference stack */
    int bddrefstacktop; /* Internal node reference stack top */
    int[] bddvar2level; /* Variable -> level table */
    int[] bddlevel2var; /* Level -> variable table */
    int bddresized; /* Flag indicating a resize of the nodetable */

    int minfreenodes = 20;

    bddCacheStat bddcachestats;

    /*=== PRIVATE KERNEL VARIABLES =========================================*/

    int[] bddvarset; /* Set of defined BDD variables */
    int gbcollectnum; /* Number of garbage collections */
    int cachesize; /* Size of the operator caches */
    long gbcclock; /* Clock ticks used in GBC */
    int usednodes_nextreorder; /* When to do reorder next time */

    static final int BDD_MEMORY = (-1); /* Out of memory */
    static final int BDD_VAR = (-2); /* Unknown variable */
    static final int BDD_RANGE = (-3);
    /* Variable value out of range (not in domain) */
    static final int BDD_DEREF = (-4);
    /* Removing external reference to unknown node */
    static final int BDD_RUNNING = (-5);
    /* Called bdd_init() twice whithout bdd_done() */
    static final int BDD_FILE = (-6); /* Some file operation failed */
    static final int BDD_FORMAT = (-7); /* Incorrect file format */
    static final int BDD_ORDER = (-8);
    /* Vars. not in order for vector based functions */
    static final int BDD_BREAK = (-9); /* User called break */
    static final int BDD_VARNUM = (-10);
    /* Different number of vars. for vector pair */
    static final int BDD_NODES = (-11);
    /* Tried to set max. number of nodes to be fewer */
    /* than there already has been allocated */
    static final int BDD_OP = (-12); /* Unknown operator */
    static final int BDD_VARSET = (-13); /* Illegal variable set */
    static final int BDD_VARBLK = (-14); /* Bad variable block operation */
    static final int BDD_DECVNUM = (-15);
    /* Trying to decrease the number of variables */
    static final int BDD_REPLACE = (-16);
    /* Replacing to already existing variables */
    static final int BDD_NODENUM = (-17);
    /* Number of nodes reached user defined maximum */
    static final int BDD_ILLBDD = (-18); /* Illegal bdd argument */
    static final int BDD_SIZE = (-19); /* Illegal size argument */

    static final int BVEC_SIZE = (-20); /* Mismatch in bitvector size */
    static final int BVEC_SHIFT = (-21);
    /* Illegal shift-left/right parameter */
    static final int BVEC_DIVZERO = (-22); /* Division by zero */

    static final int BDD_ERRNUM = 24;

    /* Strings for all error mesages */
    static String errorstrings[] =
        {
            "Out of memory",
            "Unknown variable",
            "Value out of range",
            "Unknown BDD root dereferenced",
            "bdd_init() called twice",
            "File operation failed",
            "Incorrect file format",
            "Variables not in ascending order",
            "User called break",
            "Mismatch in size of variable sets",
            "Cannot allocate fewer nodes than already in use",
            "Unknown operator",
            "Illegal variable set",
            "Bad variable block operation",
            "Trying to decrease the number of variables",
            "Trying to replace with variables already in the bdd",
            "Number of nodes reached user defined maximum",
            "Unknown BDD - was not in node table",
            "Bad size argument",
            "Mismatch in bitvector size",
            "Illegal shift-left/right parameter",
            "Division by zero" };

    static final int MAXVAR = 0x1FFFFF;
    static final int DEFAULTMAXNODEINC = 50000;

    /*=== OTHER INTERNAL DEFINITIONS =======================================*/

    static final int PAIR(int a, int b) {
        //return Math.abs((a + b) * (a + b + 1) / 2 + a);
        return ((a + b) * (a + b + 1) / 2 + a);
    }
    static final int TRIPLE(int a, int b, int c) {
        //return Math.abs(PAIR(c, PAIR(a, b)));
        return (PAIR(c, PAIR(a, b)));
    }

    final int NODEHASH(int lvl, int l, int h) {
        return Math.abs(TRIPLE(lvl, l, h) % bddnodesize);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#zero()
     */
    public BDD zero() {
        return new bdd(bddfalse);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#one()
     */
    public BDD one() {
        return new bdd(bddtrue);
    }

    int bdd_ithvar(int var) {
        if (var < 0 || var >= bddvarnum) {
            bdd_error(BDD_VAR);
            return bddfalse;
        }

        return bddvarset[var * 2];
    }

    int bdd_nithvar(int var) {
        if (var < 0 || var >= bddvarnum) {
            bdd_error(BDD_VAR);
            return bddfalse;
        }

        return bddvarset[var * 2 + 1];
    }

    int bdd_varnum() {
        return bddvarnum;
    }

    static int bdd_error(int v) {
        throw new JavaBDDException(v);
    }

    int LOW(int r) {
        return bddnodes[r].low;
    }

    int HIGH(int r) {
        return bddnodes[r].high;
    }

    int LEVEL(int r) {
        return bddnodes[r].getLevel();
    }

    static boolean ISZERO(int r) {
        return r == bddfalse;
    }

    static boolean ISONE(int r) {
        return r == bddtrue;
    }

    static boolean ISCONST(int r) {
        return r == bddfalse || r == bddtrue;
    }

    void CHECK(int r) {
        if (!bddrunning)
            bdd_error(BDD_RUNNING);
        else if ((r) < 0 || (r) >= bddnodesize)
            bdd_error(BDD_ILLBDD);
        else if (r >= 2 && LOW(r) == -1)
            bdd_error(BDD_ILLBDD);
    }
    void CHECKa(int r, int x) {
        CHECK(r);
    }

    int bdd_var(int root) {
        CHECK(root);
        if (root < 2)
            throw new JavaBDDException(BDD_ILLBDD);

        return (bddlevel2var[LEVEL(root)]);
    }

    int bdd_low(int root) {
        CHECK(root);
        if (root < 2)
            return bdd_error(BDD_ILLBDD);

        return (LOW(root));
    }

    int bdd_high(int root) {
        CHECK(root);
        if (root < 2)
            return bdd_error(BDD_ILLBDD);

        return (HIGH(root));
    }

    void checkresize() {
        if (bddresized != 0)
            bdd_operator_noderesize();
        bddresized = 0;
    }

    static final int NOTHASH(int r) {
        return r;
    }
    static final int APPLYHASH(int l, int r, int op) {
        return TRIPLE(l, r, op);
    }
    static final int ITEHASH(int f, int g, int h) {
        return TRIPLE(f, g, h);
    }
    static final int RESTRHASH(int r, int var) {
        return PAIR(r, var);
    }
    static final int CONSTRAINHASH(int f, int c) {
        return PAIR(f, c);
    }
    static final int QUANTHASH(int r) {
        return r;
    }
    static final int REPLACEHASH(int r) {
        return r;
    }
    static final int VECCOMPOSEHASH(int f) {
        return f;
    }
    static final int COMPOSEHASH(int f, int g) {
        return PAIR(f, g);
    }
    static final int SATCOUHASH(int r) {
        return r;
    }
    static final int PATHCOUHASH(int r) {
        return r;
    }
    static final int APPEXHASH(int l, int r, int op) {
        return PAIR(l, r);
    }

    static final double M_LN2 = 0.69314718055994530942;

    static double log1p(double a) {
        return Math.log(1.0 + a);
    }

    final boolean INVARSET(int a) {
        return (quantvarset[a] == quantvarsetID); /* unsigned check */
    }
    final boolean INSVARSET(int a) {
        return Math.abs(quantvarset[a]) == quantvarsetID; /* signed check */
    }

    static final int bddop_and = 0;
    static final int bddop_xor = 1;
    static final int bddop_or = 2;
    static final int bddop_nand = 3;
    static final int bddop_nor = 4;
    static final int bddop_imp = 5;
    static final int bddop_biimp = 6;
    static final int bddop_diff = 7;
    static final int bddop_less = 8;
    static final int bddop_invimp = 9;

    /* Should *not* be used in bdd_apply calls !!! */
    static final int bddop_not = 10;
    static final int bddop_simplify = 11;

    int bdd_not(int r) {
        int res;
        firstReorder = 1;
        CHECKa(r, bddfalse);

        again : for (;;) {
            try {
                INITREF();

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = not_rec(r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();
                if (firstReorder-- == 1)
                    continue again;
                res = bddfalse;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int not_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (ISZERO(r))
            return bddtrue;
        if (ISONE(r))
            return bddfalse;

        entry = BddCache_lookupI(applycache, NOTHASH(r));

        if (entry.a == r && entry.c == bddop_not) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        PUSHREF(not_rec(LOW(r)));
        PUSHREF(not_rec(HIGH(r)));
        res = bdd_makenode(LEVEL(r), READREF(2), READREF(1));
        POPREF(2);

        entry.a = r;
        entry.c = bddop_not;
        entry.res = res;

        return res;
    }

    int bdd_ite(int f, int g, int h) {
        int res;
        firstReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(g, bddfalse);
        CHECKa(h, bddfalse);

        again : for (;;) {
            try {
                INITREF();

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = ite_rec(f, g, h);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int ite_rec(int f, int g, int h) {
        BddCacheDataI entry;
        int res;

        if (ISONE(f))
            return g;
        if (ISZERO(f))
            return h;
        if (g == h)
            return g;
        if (ISONE(g) && ISZERO(h))
            return f;
        if (ISZERO(g) && ISONE(h))
            return not_rec(f);

        entry = BddCache_lookupI(itecache, ITEHASH(f, g, h));
        if (entry.a == f && entry.b == g && entry.c == h) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        if (LEVEL(f) == LEVEL(g)) {
            if (LEVEL(f) == LEVEL(h)) {
                PUSHREF(ite_rec(LOW(f), LOW(g), LOW(h)));
                PUSHREF(ite_rec(HIGH(f), HIGH(g), HIGH(h)));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            } else if (LEVEL(f) < LEVEL(h)) {
                PUSHREF(ite_rec(LOW(f), LOW(g), h));
                PUSHREF(ite_rec(HIGH(f), HIGH(g), h));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            } else /* f > h */ {
                PUSHREF(ite_rec(f, g, LOW(h)));
                PUSHREF(ite_rec(f, g, HIGH(h)));
                res = bdd_makenode(LEVEL(h), READREF(2), READREF(1));
            }
        } else if (LEVEL(f) < LEVEL(g)) {
            if (LEVEL(f) == LEVEL(h)) {
                PUSHREF(ite_rec(LOW(f), g, LOW(h)));
                PUSHREF(ite_rec(HIGH(f), g, HIGH(h)));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            } else if (LEVEL(f) < LEVEL(h)) {
                PUSHREF(ite_rec(LOW(f), g, h));
                PUSHREF(ite_rec(HIGH(f), g, h));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            } else /* f > h */ {
                PUSHREF(ite_rec(f, g, LOW(h)));
                PUSHREF(ite_rec(f, g, HIGH(h)));
                res = bdd_makenode(LEVEL(h), READREF(2), READREF(1));
            }
        } else /* f > g */ {
            if (LEVEL(g) == LEVEL(h)) {
                PUSHREF(ite_rec(f, LOW(g), LOW(h)));
                PUSHREF(ite_rec(f, HIGH(g), HIGH(h)));
                res = bdd_makenode(LEVEL(g), READREF(2), READREF(1));
            } else if (LEVEL(g) < LEVEL(h)) {
                PUSHREF(ite_rec(f, LOW(g), h));
                PUSHREF(ite_rec(f, HIGH(g), h));
                res = bdd_makenode(LEVEL(g), READREF(2), READREF(1));
            } else /* g > h */ {
                PUSHREF(ite_rec(f, g, LOW(h)));
                PUSHREF(ite_rec(f, g, HIGH(h)));
                res = bdd_makenode(LEVEL(h), READREF(2), READREF(1));
            }
        }

        POPREF(2);

        entry.a = f;
        entry.b = g;
        entry.c = h;
        entry.res = res;

        return res;
    }

    int bdd_replace(int r, bddPair pair) {
        int res;
        firstReorder = 1;

        CHECKa(r, bddfalse);

        again : for (;;) {
            try {
                INITREF();
                replacepair = pair.result;
                replacelast = pair.last;
                replaceid = (pair.id << 2) | CACHEID_REPLACE;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = replace_rec(r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int replace_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (ISCONST(r) || LEVEL(r) > replacelast)
            return r;

        entry = BddCache_lookupI(replacecache, REPLACEHASH(r));
        if (entry.a == r && entry.c == replaceid) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        PUSHREF(replace_rec(LOW(r)));
        PUSHREF(replace_rec(HIGH(r)));

        res =
            bdd_correctify(
                LEVEL(replacepair[LEVEL(r)]),
                READREF(2),
                READREF(1));
        POPREF(2);

        entry.a = r;
        entry.c = replaceid;
        entry.res = res;

        return res;
    }

    int bdd_correctify(int level, int l, int r) {
        int res;

        if (level < LEVEL(l) && level < LEVEL(r))
            return bdd_makenode(level, l, r);

        if (level == LEVEL(l) || level == LEVEL(r)) {
            bdd_error(BDD_REPLACE);
            return 0;
        }

        if (LEVEL(l) == LEVEL(r)) {
            PUSHREF(bdd_correctify(level, LOW(l), LOW(r)));
            PUSHREF(bdd_correctify(level, HIGH(l), HIGH(r)));
            res = bdd_makenode(LEVEL(l), READREF(2), READREF(1));
        } else if (LEVEL(l) < LEVEL(r)) {
            PUSHREF(bdd_correctify(level, LOW(l), r));
            PUSHREF(bdd_correctify(level, HIGH(l), r));
            res = bdd_makenode(LEVEL(l), READREF(2), READREF(1));
        } else {
            PUSHREF(bdd_correctify(level, l, LOW(r)));
            PUSHREF(bdd_correctify(level, l, HIGH(r)));
            res = bdd_makenode(LEVEL(r), READREF(2), READREF(1));
        }
        POPREF(2);

        return res; /* FIXME: cache ? */
    }

    int bdd_apply(int l, int r, int op) {
        int res;
        firstReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);

        if (op < 0 || op > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        again : for (;;) {
            try {
                INITREF();
                applyop = op;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = apply_rec(l, r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int apply_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        switch (applyop) {
            case bddop_and :
                if (l == r)
                    return l;
                if (ISZERO(l) || ISZERO(r))
                    return 0;
                if (ISONE(l))
                    return r;
                if (ISONE(r))
                    return l;
                break;
            case bddop_or :
                if (l == r)
                    return l;
                if (ISONE(l) || ISONE(r))
                    return 1;
                if (ISZERO(l))
                    return r;
                if (ISZERO(r))
                    return l;
                break;
            case bddop_xor :
                if (l == r)
                    return 0;
                if (ISZERO(l))
                    return r;
                if (ISZERO(r))
                    return l;
                break;
            case bddop_nand :
                if (ISZERO(l) || ISZERO(r))
                    return 1;
                break;
            case bddop_nor :
                if (ISONE(l) || ISONE(r))
                    return 0;
                break;
            case bddop_imp :
                if (ISZERO(l))
                    return 1;
                if (ISONE(l))
                    return r;
                if (ISONE(r))
                    return 1;
                break;
        }

        if (ISCONST(l) && ISCONST(r))
            res = oprres[applyop][l << 1 | r];
        else {
            entry = BddCache_lookupI(applycache, APPLYHASH(l, r, applyop));

            if (entry.a == l && entry.b == r && entry.c == applyop) {
                if (CACHESTATS)
                    bddcachestats.opHit++;
                return entry.res;
            }
            if (CACHESTATS)
                bddcachestats.opMiss++;

            if (LEVEL(l) == LEVEL(r)) {
                PUSHREF(apply_rec(LOW(l), LOW(r)));
                PUSHREF(apply_rec(HIGH(l), HIGH(r)));
                res = bdd_makenode(LEVEL(l), READREF(2), READREF(1));
            } else if (LEVEL(l) < LEVEL(r)) {
                PUSHREF(apply_rec(LOW(l), r));
                PUSHREF(apply_rec(HIGH(l), r));
                res = bdd_makenode(LEVEL(l), READREF(2), READREF(1));
            } else {
                PUSHREF(apply_rec(l, LOW(r)));
                PUSHREF(apply_rec(l, HIGH(r)));
                res = bdd_makenode(LEVEL(r), READREF(2), READREF(1));
            }

            POPREF(2);

            entry.a = l;
            entry.b = r;
            entry.c = applyop;
            entry.res = res;
        }

        return res;
    }

    int bdd_relprod(int a, int b, int var) {
        return bdd_appex(a, b, bddop_and, var);
    }

    int bdd_appex(int l, int r, int opr, int var) {
        int res;
        firstReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (opr < 0 || opr > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        if (var < 2) /* Empty set */
            return bdd_apply(l, r, opr);

        again : for (;;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                INITREF();

                applyop = bddop_or;
                appexop = opr;
                appexid = (var << 5) | (appexop << 1); /* FIXME: range! */
                quantid = (appexid << 3) | CACHEID_APPEX;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = appquant_rec(l, r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int varset2vartable(int r) {
        int n;

        if (r < 2)
            return bdd_error(BDD_VARSET);

        quantvarsetID++;

        if (quantvarsetID == INT_MAX) {
            for (int i = 0; i < bddvarnum; ++i)
                quantvarset[i] = 0;
            quantvarsetID = 1;
        }

        for (n = r; n > 1; n = HIGH(n)) {
            quantvarset[LEVEL(n)] = quantvarsetID;
            quantlast = LEVEL(n);
        }

        return 0;
    }

    static final int INT_MAX = Integer.MAX_VALUE;

    int varset2svartable(int r) {
        int n;

        if (r < 2)
            return bdd_error(BDD_VARSET);

        quantvarsetID++;

        if (quantvarsetID == INT_MAX / 2) {
            for (int i = 0; i < bddvarnum; ++i)
                quantvarset[i] = 0;
            quantvarsetID = 1;
        }

        for (n = r; !ISCONST(n);) {
            if (ISZERO(LOW(n))) {
                quantvarset[LEVEL(n)] = quantvarsetID;
                n = HIGH(n);
            } else {
                quantvarset[LEVEL(n)] = -quantvarsetID;
                n = LOW(n);
            }
            quantlast = LEVEL(n);
        }

        return 0;
    }

    int appquant_rec(int l, int r) {
        BddCacheDataI entry;
        int res;

        switch (appexop) {
            case bddop_and :
                if (l == 0 || r == 0)
                    return 0;
                if (l == r)
                    return quant_rec(l);
                if (l == 1)
                    return quant_rec(r);
                if (r == 1)
                    return quant_rec(l);
                break;
            case bddop_or :
                if (l == 1 || r == 1)
                    return 1;
                if (l == r)
                    return quant_rec(l);
                if (l == 0)
                    return quant_rec(r);
                if (r == 0)
                    return quant_rec(l);
                break;
            case bddop_xor :
                if (l == r)
                    return 0;
                if (l == 0)
                    return quant_rec(r);
                if (r == 0)
                    return quant_rec(l);
                break;
            case bddop_nand :
                if (l == 0 || r == 0)
                    return 1;
                break;
            case bddop_nor :
                if (l == 1 || r == 1)
                    return 0;
                break;
        }

        if (ISCONST(l) && ISCONST(r))
            res = oprres[appexop][(l << 1) | r];
        else if (LEVEL(l) > quantlast && LEVEL(r) > quantlast) {
            int oldop = applyop;
            applyop = appexop;
            res = apply_rec(l, r);
            applyop = oldop;
        } else {
            entry = BddCache_lookupI(appexcache, APPEXHASH(l, r, appexop));
            if (entry.a == l && entry.b == r && entry.c == appexid) {
                if (CACHESTATS)
                    bddcachestats.opHit++;
                return entry.res;
            }
            if (CACHESTATS)
                bddcachestats.opMiss++;

            if (LEVEL(l) == LEVEL(r)) {
                PUSHREF(appquant_rec(LOW(l), LOW(r)));
                PUSHREF(appquant_rec(HIGH(l), HIGH(r)));
                if (INVARSET(LEVEL(l)))
                    res = apply_rec(READREF(2), READREF(1));
                else
                    res = bdd_makenode(LEVEL(l), READREF(2), READREF(1));
            } else if (LEVEL(l) < LEVEL(r)) {
                PUSHREF(appquant_rec(LOW(l), r));
                PUSHREF(appquant_rec(HIGH(l), r));
                if (INVARSET(LEVEL(l)))
                    res = apply_rec(READREF(2), READREF(1));
                else
                    res = bdd_makenode(LEVEL(l), READREF(2), READREF(1));
            } else {
                PUSHREF(appquant_rec(l, LOW(r)));
                PUSHREF(appquant_rec(l, HIGH(r)));
                if (INVARSET(LEVEL(r)))
                    res = apply_rec(READREF(2), READREF(1));
                else
                    res = bdd_makenode(LEVEL(r), READREF(2), READREF(1));
            }

            POPREF(2);

            entry.a = l;
            entry.b = r;
            entry.c = appexid;
            entry.res = res;
        }

        return res;
    }

    int quant_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (r < 2 || LEVEL(r) > quantlast)
            return r;

        entry = BddCache_lookupI(quantcache, QUANTHASH(r));
        if (entry.a == r && entry.c == quantid) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        PUSHREF(quant_rec(LOW(r)));
        PUSHREF(quant_rec(HIGH(r)));

        if (INVARSET(LEVEL(r)))
            res = apply_rec(READREF(2), READREF(1));
        else
            res = bdd_makenode(LEVEL(r), READREF(2), READREF(1));

        POPREF(2);

        entry.a = r;
        entry.c = quantid;
        entry.res = res;

        return res;
    }

    int bdd_constrain(int f, int c) {
        int res;
        firstReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(c, bddfalse);

        again : for (;;) {
            try {
                INITREF();
                miscid = CACHEID_CONSTRAIN;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = constrain_rec(f, c);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int constrain_rec(int f, int c) {
        BddCacheDataI entry;
        int res;

        if (ISONE(c))
            return f;
        if (ISCONST(f))
            return f;
        if (c == f)
            return BDDONE;
        if (ISZERO(c))
            return BDDZERO;

        entry = BddCache_lookupI(misccache, CONSTRAINHASH(f, c));
        if (entry.a == f && entry.b == c && entry.c == miscid) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        if (LEVEL(f) == LEVEL(c)) {
            if (ISZERO(LOW(c)))
                res = constrain_rec(HIGH(f), HIGH(c));
            else if (ISZERO(HIGH(c)))
                res = constrain_rec(LOW(f), LOW(c));
            else {
                PUSHREF(constrain_rec(LOW(f), LOW(c)));
                PUSHREF(constrain_rec(HIGH(f), HIGH(c)));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
                POPREF(2);
            }
        } else if (LEVEL(f) < LEVEL(c)) {
            PUSHREF(constrain_rec(LOW(f), c));
            PUSHREF(constrain_rec(HIGH(f), c));
            res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            POPREF(2);
        } else {
            if (ISZERO(LOW(c)))
                res = constrain_rec(f, HIGH(c));
            else if (ISZERO(HIGH(c)))
                res = constrain_rec(f, LOW(c));
            else {
                PUSHREF(constrain_rec(f, LOW(c)));
                PUSHREF(constrain_rec(f, HIGH(c)));
                res = bdd_makenode(LEVEL(c), READREF(2), READREF(1));
                POPREF(2);
            }
        }

        entry.a = f;
        entry.b = c;
        entry.c = miscid;
        entry.res = res;

        return res;
    }

    int bdd_compose(int f, int g, int var) {
        int res;
        firstReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(g, bddfalse);
        if (var < 0 || var >= bddvarnum) {
            bdd_error(BDD_VAR);
            return bddfalse;
        }

        again : for (;;) {
            try {
                INITREF();
                composelevel = bddvar2level[var];
                replaceid = (composelevel << 2) | CACHEID_COMPOSE;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = compose_rec(f, g);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int compose_rec(int f, int g) {
        BddCacheDataI entry;
        int res;

        if (LEVEL(f) > composelevel)
            return f;

        entry = BddCache_lookupI(replacecache, COMPOSEHASH(f, g));
        if (entry.a == f && entry.b == g && entry.c == replaceid) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        if (LEVEL(f) < composelevel) {
            if (LEVEL(f) == LEVEL(g)) {
                PUSHREF(compose_rec(LOW(f), LOW(g)));
                PUSHREF(compose_rec(HIGH(f), HIGH(g)));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            } else if (LEVEL(f) < LEVEL(g)) {
                PUSHREF(compose_rec(LOW(f), g));
                PUSHREF(compose_rec(HIGH(f), g));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            } else {
                PUSHREF(compose_rec(f, LOW(g)));
                PUSHREF(compose_rec(f, HIGH(g)));
                res = bdd_makenode(LEVEL(g), READREF(2), READREF(1));
            }
            POPREF(2);
        } else
            /*if (LEVEL(f) == composelevel) changed 2-nov-98 */ {
            res = ite_rec(g, HIGH(f), LOW(f));
        }

        entry.a = f;
        entry.b = g;
        entry.c = replaceid;
        entry.res = res;

        return res;
    }

    int bdd_veccompose(int f, bddPair pair) {
        int res;
        firstReorder = 1;

        CHECKa(f, bddfalse);

        again : for (;;) {
            try {
                INITREF();
                replacepair = pair.result;
                replaceid = (pair.id << 2) | CACHEID_VECCOMPOSE;
                replacelast = pair.last;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = veccompose_rec(f);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int veccompose_rec(int f) {
        BddCacheDataI entry;
        int res;

        if (LEVEL(f) > replacelast)
            return f;

        entry = BddCache_lookupI(replacecache, VECCOMPOSEHASH(f));
        if (entry.a == f && entry.c == replaceid) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        PUSHREF(veccompose_rec(LOW(f)));
        PUSHREF(veccompose_rec(HIGH(f)));
        res = ite_rec(replacepair[LEVEL(f)], READREF(1), READREF(2));
        POPREF(2);

        entry.a = f;
        entry.c = replaceid;
        entry.res = res;

        return res;
    }

    int bdd_exist(int r, int var) {
        int res;
        firstReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        again : for (;;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                INITREF();

                quantid = (var << 3) | CACHEID_EXIST; /* FIXME: range */
                applyop = bddop_or;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = quant_rec(r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int bdd_forall(int r, int var) {
        int res;
        firstReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        again : for (;;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                INITREF();
                quantid = (var << 3) | CACHEID_FORALL;
                applyop = bddop_and;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = quant_rec(r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int bdd_unique(int r, int var) {
        int res;
        firstReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        again : for (;;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                INITREF();
                quantid = (var << 3) | CACHEID_UNIQUE;
                applyop = bddop_xor;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = quant_rec(r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int bdd_restrict(int r, int var) {
        int res;
        firstReorder = 1;

        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (var < 2) /* Empty set */
            return r;

        again : for (;;) {
            if (varset2svartable(var) < 0)
                return bddfalse;
            try {
                INITREF();
                miscid = (var << 3) | CACHEID_RESTRICT;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = restrict_rec(r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int restrict_rec(int r) {
        BddCacheDataI entry;
        int res;

        if (ISCONST(r) || LEVEL(r) > quantlast)
            return r;

        entry = BddCache_lookupI(misccache, RESTRHASH(r, miscid));
        if (entry.a == r && entry.c == miscid) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        if (INSVARSET(LEVEL(r))) {
            if (quantvarset[LEVEL(r)] > 0) {
                res = restrict_rec(HIGH(r));
            } else {
                res = restrict_rec(LOW(r));
            }
        } else {
            PUSHREF(restrict_rec(LOW(r)));
            PUSHREF(restrict_rec(HIGH(r)));
            res = bdd_makenode(LEVEL(r), READREF(2), READREF(1));
            POPREF(2);
        }

        entry.a = r;
        entry.c = miscid;
        entry.res = res;

        return res;
    }

    int bdd_simplify(int f, int d) {
        int res;
        firstReorder = 1;

        CHECKa(f, bddfalse);
        CHECKa(d, bddfalse);

        again : for (;;) {
            try {
                INITREF();
                applyop = bddop_or;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = simplify_rec(f, d);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int simplify_rec(int f, int d) {
        BddCacheDataI entry;
        int res;

        if (ISONE(d) || ISCONST(f))
            return f;
        if (d == f)
            return BDDONE;
        if (ISZERO(d))
            return BDDZERO;

        entry = BddCache_lookupI(applycache, APPLYHASH(f, d, bddop_simplify));

        if (entry.a == f && entry.b == d && entry.c == bddop_simplify) {
            if (CACHESTATS)
                bddcachestats.opHit++;
            return entry.res;
        }
        if (CACHESTATS)
            bddcachestats.opMiss++;

        if (LEVEL(f) == LEVEL(d)) {
            if (ISZERO(LOW(d)))
                res = simplify_rec(HIGH(f), HIGH(d));
            else if (ISZERO(HIGH(d)))
                res = simplify_rec(LOW(f), LOW(d));
            else {
                PUSHREF(simplify_rec(LOW(f), LOW(d)));
                PUSHREF(simplify_rec(HIGH(f), HIGH(d)));
                res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
                POPREF(2);
            }
        } else if (LEVEL(f) < LEVEL(d)) {
            PUSHREF(simplify_rec(LOW(f), d));
            PUSHREF(simplify_rec(HIGH(f), d));
            res = bdd_makenode(LEVEL(f), READREF(2), READREF(1));
            POPREF(2);
        } else /* LEVEL(d) < LEVEL(f) */ {
            PUSHREF(apply_rec(LOW(d), HIGH(d))); /* Exist quant */
            res = simplify_rec(f, READREF(1));
            POPREF(1);
        }

        entry.a = f;
        entry.b = d;
        entry.c = bddop_simplify;
        entry.res = res;

        return res;
    }

    static int supportSize = 0;

    int bdd_support(int r) {
        int n;
        int res = 1;

        CHECKa(r, bddfalse);

        if (r < 2)
            return bddfalse;

        /* On-demand allocation of support set */
        if (supportSize < bddvarnum) {
            supportSet = new int[bddvarnum];
            //memset(supportSet, 0, bddvarnum*sizeof(int));
            supportSize = bddvarnum;
            supportID = 0;
        }

        /* Update global variables used to speed up bdd_support()
         * - instead of always memsetting support to zero, we use
         *   a change counter.
         * - and instead of reading the whole array afterwards, we just
         *   look from 'min' to 'max' used BDD variables.
         */
        if (supportID == 0x0FFFFFFF) {
            /* We probably don't get here -- but let's just be sure */
            for (int i = 0; i < bddvarnum; ++i)
                supportSet[i] = 0;
            supportID = 0;
        }
        ++supportID;
        supportMin = LEVEL(r);
        supportMax = supportMin;

        support_rec(r, supportSet);
        bdd_unmark(r);

        bdd_disable_reorder();

        for (n = supportMax; n >= supportMin; --n)
            if (supportSet[n] == supportID) {
                int tmp;
                bdd_addref(res);
                tmp = bdd_makenode(n, 0, res);
                bdd_delref(res);
                res = tmp;
            }

        bdd_enable_reorder();

        return res;
    }

    void support_rec(int r, int[] support) {
        BddNode node;

        if (r < 2)
            return;

        node = bddnodes[r];
        if (node.isMarked() || node.low == -1)
            return;

        support[node.getLevel()] = supportID;

        if (node.getLevel() > supportMax)
            supportMax = node.getLevel();

        node.mark();

        support_rec(node.low, support);
        support_rec(node.high, support);
    }

    int bdd_appall(int l, int r, int opr, int var) {
        int res;
        firstReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (opr < 0 || opr > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        if (var < 2) /* Empty set */
            return bdd_apply(l, r, opr);

        again : for (;;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                INITREF();
                applyop = bddop_and;
                appexop = opr;
                appexid = (var << 5) | (appexop << 1) | 1; /* FIXME: range! */
                quantid = (appexid << 3) | CACHEID_APPAL;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = appquant_rec(l, r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int bdd_appuni(int l, int r, int opr, int var) {
        int res;
        firstReorder = 1;

        CHECKa(l, bddfalse);
        CHECKa(r, bddfalse);
        CHECKa(var, bddfalse);

        if (opr < 0 || opr > bddop_invimp) {
            bdd_error(BDD_OP);
            return bddfalse;
        }

        if (var < 2) /* Empty set */
            return bdd_apply(l, r, opr);

        again : for (;;) {
            if (varset2vartable(var) < 0)
                return bddfalse;
            try {
                INITREF();
                applyop = bddop_xor;
                appexop = opr;
                appexid = (var << 5) | (appexop << 1) | 1; /* FIXME: range! */
                quantid = (appexid << 3) | CACHEID_APPUN;

                if (firstReorder == 0)
                    bdd_disable_reorder();
                res = appquant_rec(l, r);
                if (firstReorder == 0)
                    bdd_enable_reorder();
            } catch (BDDException x) {
                bdd_checkreorder();

                if (firstReorder-- == 1)
                    continue again;
                res = BDDZERO;
                /* avoid warning about res being uninitialized */
            }
            break;
        }

        checkresize();
        return res;
    }

    int bdd_satone(int r) {
        int res;

        CHECKa(r, bddfalse);
        if (r < 2)
            return r;

        bdd_disable_reorder();

        INITREF();
        res = satone_rec(r);

        bdd_enable_reorder();

        checkresize();
        return res;
    }

    int satone_rec(int r) {
        if (ISCONST(r))
            return r;

        if (ISZERO(LOW(r))) {
            int res = satone_rec(HIGH(r));
            int m = bdd_makenode(LEVEL(r), BDDZERO, res);
            PUSHREF(m);
            return m;
        } else {
            int res = satone_rec(LOW(r));
            int m = bdd_makenode(LEVEL(r), res, BDDZERO);
            PUSHREF(m);
            return m;
        }
    }

    int bdd_satoneset(int r, int var, int pol) {
        int res;

        CHECKa(r, bddfalse);
        if (ISZERO(r))
            return r;
        if (!ISCONST(pol)) {
            bdd_error(BDD_ILLBDD);
            return bddfalse;
        }

        bdd_disable_reorder();

        INITREF();
        satPolarity = pol;
        res = satoneset_rec(r, var);

        bdd_enable_reorder();

        checkresize();
        return res;
    }

    int satoneset_rec(int r, int var) {
        if (ISCONST(r) && ISCONST(var))
            return r;

        if (LEVEL(r) < LEVEL(var)) {
            if (ISZERO(LOW(r))) {
                int res = satoneset_rec(HIGH(r), var);
                int m = bdd_makenode(LEVEL(r), BDDZERO, res);
                PUSHREF(m);
                return m;
            } else {
                int res = satoneset_rec(LOW(r), var);
                int m = bdd_makenode(LEVEL(r), res, BDDZERO);
                PUSHREF(m);
                return m;
            }
        } else if (LEVEL(var) < LEVEL(r)) {
            int res = satoneset_rec(r, HIGH(var));
            if (satPolarity == BDDONE) {
                int m = bdd_makenode(LEVEL(var), BDDZERO, res);
                PUSHREF(m);
                return m;
            } else {
                int m = bdd_makenode(LEVEL(var), res, BDDZERO);
                PUSHREF(m);
                return m;
            }
        } else /* LEVEL(r) == LEVEL(var) */ {
            if (ISZERO(LOW(r))) {
                int res = satoneset_rec(HIGH(r), HIGH(var));
                int m = bdd_makenode(LEVEL(r), BDDZERO, res);
                PUSHREF(m);
                return m;
            } else {
                int res = satoneset_rec(LOW(r), HIGH(var));
                int m = bdd_makenode(LEVEL(r), res, BDDZERO);
                PUSHREF(m);
                return m;
            }
        }

    }

    int bdd_fullsatone(int r) {
        int res;
        int v;

        CHECKa(r, bddfalse);
        if (r == 0)
            return 0;

        bdd_disable_reorder();

        INITREF();
        res = fullsatone_rec(r);

        for (v = LEVEL(r) - 1; v >= 0; v--) {
            res = PUSHREF(bdd_makenode(v, res, 0));
        }

        bdd_enable_reorder();

        checkresize();
        return res;
    }

    int fullsatone_rec(int r) {
        if (r < 2)
            return r;

        if (LOW(r) != 0) {
            int res = fullsatone_rec(LOW(r));
            int v;

            for (v = LEVEL(LOW(r)) - 1; v > LEVEL(r); v--) {
                res = PUSHREF(bdd_makenode(v, res, 0));
            }

            return PUSHREF(bdd_makenode(LEVEL(r), res, 0));
        } else {
            int res = fullsatone_rec(HIGH(r));
            int v;

            for (v = LEVEL(HIGH(r)) - 1; v > LEVEL(r); v--) {
                res = PUSHREF(bdd_makenode(v, res, 0));
            }

            return PUSHREF(bdd_makenode(LEVEL(r), 0, res));
        }
    }

    static class bddGbcStat {
        int nodes;
        int freenodes;
        long time;
        long sumtime;
        int num;
    }

    void gbc_handler(boolean pre, bddGbcStat s) {
        bdd_default_gbchandler(pre, s);
    }

    void bdd_default_gbchandler(boolean pre, bddGbcStat s) {
        if (!pre) {
            System.err.print(
                "Garbage collection #"
                    + s.num
                    + ": "
                    + s.nodes
                    + " nodes / "
                    + s.freenodes
                    + " free");
            System.err.println(
                " / "
                    + (float) s.time / (float) 1000
                    + "s / "
                    + (float) s.sumtime / (float) 1000
                    + "s total");
        }
    }

    void bdd_gbc_rehash() {
        int n;

        bddfreepos = 0;
        bddfreenum = 0;

        for (n = bddnodesize - 1; n >= 2; n--) {
            BddNode node = bddnodes[n];

            if (node.low != -1) {
                int hash;

                hash = NODEHASH(node.getLevel(), node.low, node.high);
                node.next = bddnodes[hash].hash;
                bddnodes[hash].hash = n;
            } else {
                node.next = bddfreepos;
                bddfreepos = n;
                bddfreenum++;
            }
        }
    }

    long clock() {
        return System.currentTimeMillis();
    }

    void INITREF() {
        bddrefstacktop = 0;
    }
    int PUSHREF(int a) {
        bddrefstack[bddrefstacktop++] = a;
        return a;
    }
    int READREF(int a) {
        return bddrefstack[bddrefstacktop - a];
    }
    void POPREF(int a) {
        bddrefstacktop -= a;
    }

    int bdd_nodecount(int r) {
        int[] num = new int[1];

        CHECK(r);

        bdd_markcount(r, num);
        bdd_unmark(r);

        return num[0];
    }

    int bdd_anodecount(int[] r) {
        int n;
        int[] cou = new int[1];

        for (n = 0; n < r.length; n++)
            bdd_markcount(r[n], cou);

        for (n = 0; n < r.length; n++)
            bdd_unmark(r[n]);

        return cou[0];
    }

    int[] bdd_varprofile(int r) {
        CHECK(r);

        int[] varprofile = new int[bddvarnum];

        varprofile_rec(r, varprofile);
        bdd_unmark(r);
        return varprofile;
    }

    void varprofile_rec(int r, int[] varprofile) {
        BddNode node;

        if (r < 2)
            return;

        node = bddnodes[r];
        if (node.isMarked())
            return;

        varprofile[bddlevel2var[node.getLevel()]]++;
        node.mark();

        varprofile_rec(node.low, varprofile);
        varprofile_rec(node.high, varprofile);
    }

    double bdd_pathcount(int r) {
        CHECK(r);

        miscid = CACHEID_PATHCOU;

        return bdd_pathcount_rec(r);
    }

    double bdd_pathcount_rec(int r) {
        BddCacheDataD entry;
        double size;

        if (ISZERO(r))
            return 0.0;
        if (ISONE(r))
            return 1.0;

        entry = BddCache_lookupD(countcache, PATHCOUHASH(r));
        if (entry.a == r && entry.c == miscid)
            return entry.dres;

        size = bdd_pathcount_rec(LOW(r)) + bdd_pathcount_rec(HIGH(r));

        entry.a = r;
        entry.c = miscid;
        entry.dres = size;

        return size;
    }

    void bdd_allsat(int r, List result) {
        int v;

        CHECK(r);

        allsatProfile = new byte[bddvarnum];

        for (v = LEVEL(r) - 1; v >= 0; --v)
            allsatProfile[bddlevel2var[v]] = -1;

        INITREF();

        allsat_rec(r, result);

        free(allsatProfile);
        allsatProfile = null;
    }

    void allsat_rec(int r, List result) {
        if (ISONE(r)) {
            //allsatHandler(allsatProfile, bddvarnum);
            byte[] b = new byte[bddvarnum];
            System.arraycopy(allsatProfile, 0, b, 0, bddvarnum);
            result.add(b);
            return;
        }

        if (ISZERO(r))
            return;

        if (!ISZERO(LOW(r))) {
            int v;

            allsatProfile[bddlevel2var[LEVEL(r)]] = 0;

            for (v = LEVEL(LOW(r)) - 1; v > LEVEL(r); --v) {
                allsatProfile[bddlevel2var[v]] = -1;
            }

            allsat_rec(LOW(r), result);
        }

        if (!ISZERO(HIGH(r))) {
            int v;

            allsatProfile[bddlevel2var[LEVEL(r)]] = 1;

            for (v = LEVEL(HIGH(r)) - 1; v > LEVEL(r); --v) {
                allsatProfile[bddlevel2var[v]] = -1;
            }

            allsat_rec(HIGH(r), result);
        }
    }
    double bdd_satcount(int r) {
        double size = 1;

        CHECK(r);

        miscid = CACHEID_SATCOU;
        size = Math.pow(2.0, (double) LEVEL(r));

        return size * satcount_rec(r);
    }

    double bdd_satcountset(int r, int varset) {
        double unused = bddvarnum;
        int n;

        if (ISCONST(varset) || ISZERO(r)) /* empty set */
            return 0.0;

        for (n = varset; !ISCONST(n); n = HIGH(n))
            unused--;

        unused = bdd_satcount(r) / Math.pow(2.0, unused);

        return unused >= 1.0 ? unused : 1.0;
    }

    double satcount_rec(int root) {
        BddCacheDataD entry;
        BddNode node;
        double size, s;

        if (root < 2)
            return root;

        entry = BddCache_lookupD(countcache, SATCOUHASH(root));
        if (entry.a == root && entry.c == miscid)
            return entry.dres;

        node = bddnodes[root];
        size = 0;
        s = 1;

        s *= Math.pow(2.0, (float) (LEVEL(node.low) - node.getLevel() - 1));
        size += s * satcount_rec(node.low);

        s = 1;
        s *= Math.pow(2.0, (float) (LEVEL(node.high) - node.getLevel() - 1));
        size += s * satcount_rec(node.high);

        entry.a = root;
        entry.c = miscid;
        entry.dres = size;

        return size;
    }

    void bdd_gbc() {
        int r;
        int n;
        long c2, c1 = clock();

        //if (gbc_handler != NULL)
        {
            bddGbcStat s = new bddGbcStat();
            s.nodes = bddnodesize;
            s.freenodes = bddfreenum;
            s.time = 0;
            s.sumtime = gbcclock;
            s.num = gbcollectnum;
            gbc_handler(true, s);
        }

        for (r = 0; r < bddrefstacktop; r++)
            bdd_mark(bddrefstack[r]);

        for (n = 0; n < bddnodesize; n++) {
            if (HASREF(n))
                bdd_mark(n);
            bddnodes[n].hash = 0;
        }

        bddfreepos = 0;
        bddfreenum = 0;

        for (n = bddnodesize - 1; n >= 2; n--) {
            BddNode node = bddnodes[n];

            if (node.isMarked() && node.low != -1) {
                int hash;

                node.unmark();
                hash = NODEHASH(node.getLevel(), node.low, node.high);
                node.next = bddnodes[hash].hash;
                bddnodes[hash].hash = n;
            } else {
                node.low = -1;
                node.next = bddfreepos;
                bddfreepos = n;
                bddfreenum++;
            }
        }

        bdd_operator_reset();

        c2 = clock();
        gbcclock += c2 - c1;
        gbcollectnum++;

        //if (gbc_handler != NULL)
        {
            bddGbcStat s = new bddGbcStat();
            s.nodes = bddnodesize;
            s.freenodes = bddfreenum;
            s.time = c2 - c1;
            s.sumtime = gbcclock;
            s.num = gbcollectnum;
            gbc_handler(false, s);
        }
    }

    void DECREF(int n) {
        bddnodes[n].decRef();
    }
    void INCREF(int n) {
        bddnodes[n].incRef();
    }
    boolean HASREF(int n) {
        return bddnodes[n].hasRef();
    }

    int bdd_addref(int root) {
        if (root < 2 || !bddrunning)
            return root;
        if (root >= bddnodesize)
            return bdd_error(BDD_ILLBDD);
        if (LOW(root) == -1)
            return bdd_error(BDD_ILLBDD);

        INCREF(root);
        return root;
    }

    int bdd_delref(int root) {
        if (root < 2 || !bddrunning)
            return root;
        if (root >= bddnodesize)
            return bdd_error(BDD_ILLBDD);
        if (LOW(root) == -1)
            return bdd_error(BDD_ILLBDD);

        /* if the following line is present, fails there much earlier */
        if (!HASREF(root))
            bdd_error(BDD_BREAK); /* distinctive */

        DECREF(root);
        return root;
    }

    void bdd_mark(int i) {
        BddNode node;

        if (i < 2)
            return;

        node = bddnodes[i];
        if (node.isMarked() || node.low == -1)
            return;

        node.mark();

        bdd_mark(node.low);
        bdd_mark(node.high);
    }

    void bdd_mark_upto(int i, int level) {
        BddNode node = bddnodes[i];

        if (i < 2)
            return;

        if (node.isMarked() || node.low == -1)
            return;

        if (node.getLevel() > level)
            return;

        node.mark();

        bdd_mark_upto(node.low, level);
        bdd_mark_upto(node.high, level);
    }

    void bdd_markcount(int i, int[] cou) {
        BddNode node;

        if (i < 2)
            return;

        node = bddnodes[i];
        if (node.isMarked() || node.low == -1)
            return;

        node.mark();
        cou[0] += 1;

        bdd_markcount(node.low, cou);
        bdd_markcount(node.high, cou);
    }

    void bdd_unmark(int i) {
        BddNode node;

        if (i < 2)
            return;

        node = bddnodes[i];

        if (!node.isMarked() || node.low == -1)
            return;
        node.unmark();

        bdd_unmark(node.low);
        bdd_unmark(node.high);
    }

    void bdd_unmark_upto(int i, int level) {
        BddNode node = bddnodes[i];

        if (i < 2)
            return;

        if (!node.isMarked())
            return;

        node.unmark();

        if (node.getLevel() > level)
            return;

        bdd_unmark_upto(node.low, level);
        bdd_unmark_upto(node.high, level);
    }

    static final boolean CACHESTATS = false;

    int bdd_makenode(int level, int low, int high) {
        BddNode node;
        int hash;
        int res;

        if (CACHESTATS)
            bddcachestats.uniqueAccess++;

        /* check whether childs are equal */
        if (low == high)
            return low;

        /* Try to find an existing node of this kind */
        hash = NODEHASH(level, low, high);
        res = bddnodes[hash].hash;

        while (res != 0) {
            if (LEVEL(res) == level && LOW(res) == low && HIGH(res) == high) {
                if (CACHESTATS)
                    bddcachestats.uniqueHit++;
                return res;
            }

            res = bddnodes[res].next;
            if (CACHESTATS)
                bddcachestats.uniqueChain++;
        }

        /* No existing node => build one */
        if (CACHESTATS)
            bddcachestats.uniqueMiss++;

        /* Any free nodes to use ? */
        if (bddfreepos == 0) {
            if (bdderrorcond != 0)
                return 0;

            /* Try to allocate more nodes */
            bdd_gbc();

            /*
            if ((bddnodesize-bddfreenum) >= usednodes_nextreorder  &&
             bdd_reorder_ready())
            {
            longjmp(bddexception,1);
            }
            */

            if ((bddfreenum * 100) / bddnodesize <= minfreenodes) {
                bdd_noderesize(true);
                hash = NODEHASH(level, low, high);
            }

            /* Panic if that is not possible */
            if (bddfreepos == 0) {
                bdd_error(BDD_NODENUM);
                bdderrorcond = Math.abs(BDD_NODENUM);
                return 0;
            }
        }

        /* Build new node */
        res = bddfreepos;
        bddfreepos = bddnodes[bddfreepos].next;
        bddfreenum--;
        bddproduced++;

        node = bddnodes[res];
        node.setLevelAndMarked(level);
        node.low = low;
        node.high = high;

        /* Insert node */
        node.next = bddnodes[hash].hash;
        bddnodes[hash].hash = res;

        return res;
    }

    void resize_handler(int oldsize, int newsize) {
        // nop
    }

    int bdd_noderesize(boolean doRehash) {
        BddNode[] newnodes;
        int oldsize = bddnodesize;
        int n;

        if (bddnodesize >= bddmaxnodesize && bddmaxnodesize > 0)
            return -1;

        bddnodesize = bddnodesize << 1;

        if (bddnodesize > oldsize + bddmaxnodeincrease)
            bddnodesize = oldsize + bddmaxnodeincrease;

        if (bddnodesize > bddmaxnodesize && bddmaxnodesize > 0)
            bddnodesize = bddmaxnodesize;

        bddnodesize = bdd_prime_lte(bddnodesize);

        //if (resize_handler != NULL)
        resize_handler(oldsize, bddnodesize);

        newnodes = new BddNode[bddnodesize];
        System.arraycopy(bddnodes, 0, newnodes, 0, bddnodes.length);
        bddnodes = newnodes;

        if (doRehash)
            for (n = 0; n < oldsize; n++)
                bddnodes[n].hash = 0;

        for (n = oldsize; n < bddnodesize; n++) {
            bddnodes[n] = new BddNode();
            //bddnodes[n].setRefcou(0);
            //bddnodes[n].hash = 0;
            //bddnodes[n].setLevel(0);
            bddnodes[n].low = -1;
            bddnodes[n].next = n + 1;
        }
        bddnodes[bddnodesize - 1].next = bddfreepos;
        bddfreepos = oldsize;
        bddfreenum += bddnodesize - oldsize;

        if (doRehash)
            bdd_gbc_rehash();

        bddresized = 1;

        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#initialize(int, int)
     */
    protected void initialize(int initnodesize, int cs) {
        int n, err;

        if (bddrunning)
            throw new JavaBDDException(BDD_RUNNING);

        bddnodesize = bdd_prime_gte(initnodesize);

        bddnodes = new BddNode[bddnodesize];

        bddresized = 0;

        for (n = 0; n < bddnodesize; n++) {
            bddnodes[n] = new BddNode();
            //bddnodes[n].setRefcou(0);
            bddnodes[n].low = -1;
            //bddnodes[n].hash = 0;
            //bddnodes[n].setLevel(0);
            bddnodes[n].next = n + 1;
        }
        bddnodes[bddnodesize - 1].next = 0;

        bddnodes[0].setMaxRef();
        bddnodes[1].setMaxRef();
        bddnodes[0].low = bddnodes[0].high = 0;
        bddnodes[1].low = bddnodes[1].high = 1;

        bdd_operator_init(cs);

        bddfreepos = 2;
        bddfreenum = bddnodesize - 2;
        bddrunning = true;
        bddvarnum = 0;
        gbcollectnum = 0;
        gbcclock = 0;
        cachesize = cs;
        usednodes_nextreorder = bddnodesize;
        bddmaxnodeincrease = DEFAULTMAXNODEINC;

        bdderrorcond = 0;

        bddcachestats = new bddCacheStat();
        bddcachestats.uniqueAccess = 0;
        bddcachestats.uniqueChain = 0;
        bddcachestats.uniqueHit = 0;
        bddcachestats.uniqueMiss = 0;
        bddcachestats.opHit = 0;
        bddcachestats.opMiss = 0;
        bddcachestats.swapCount = 0;

        //bdd_gbc_hook(bdd_default_gbchandler);
        //bdd_error_hook(bdd_default_errhandler);
        //bdd_resize_hook(NULL);
        bdd_pairs_init();
        bdd_reorder_init();

        return;
    }

    /* Hash value modifiers to distinguish between entries in misccache */
    static final int CACHEID_CONSTRAIN = 0x0;
    static final int CACHEID_RESTRICT = 0x1;
    static final int CACHEID_SATCOU = 0x2;
    static final int CACHEID_SATCOULN = 0x3;
    static final int CACHEID_PATHCOU = 0x4;

    /* Hash value modifiers for replace/compose */
    static final int CACHEID_REPLACE = 0x0;
    static final int CACHEID_COMPOSE = 0x1;
    static final int CACHEID_VECCOMPOSE = 0x2;

    /* Hash value modifiers for quantification */
    static final int CACHEID_EXIST = 0x0;
    static final int CACHEID_FORALL = 0x1;
    static final int CACHEID_UNIQUE = 0x2;
    static final int CACHEID_APPEX = 0x3;
    static final int CACHEID_APPAL = 0x4;
    static final int CACHEID_APPUN = 0x5;

    /* Number of boolean operators */
    static final int OPERATOR_NUM = 11;

    /* Operator results - entry = left<<1 | right  (left,right in {0,1}) */
    static int oprres[][] =
        { { 0, 0, 0, 1 }, /* and                       ( & )         */ {
            0, 1, 1, 0 }, /* xor                       ( ^ )         */ {
            0, 1, 1, 1 }, /* or                        ( | )         */ {
            1, 1, 1, 0 }, /* nand                                    */ {
            1, 0, 0, 0 }, /* nor                                     */ {
            1, 1, 0, 1 }, /* implication               ( >> )        */ {
            1, 0, 0, 1 }, /* bi-implication                          */ {
            0, 0, 1, 0 }, /* difference /greater than  ( - ) ( > )   */ {
            0, 1, 0, 0 }, /* less than                 ( < )         */ {
            1, 0, 1, 1 }, /* inverse implication       ( << )        */ {
            1, 1, 0, 0 } /* not                       ( ! )         */
    };

    int applyop; /* Current operator for apply */
    int appexop; /* Current operator for appex */
    int appexid; /* Current cache id for appex */
    int quantid; /* Current cache id for quantifications */
    int[] quantvarset; /* Current variable set for quant. */
    int quantvarsetID; /* Current id used in quantvarset */
    int quantlast; /* Current last variable to be quant. */
    int replaceid; /* Current cache id for replace */
    int[] replacepair; /* Current replace pair */
    int replacelast; /* Current last var. level to replace */
    int composelevel; /* Current variable used for compose */
    int miscid; /* Current cache id for other results */
    int supportID; /* Current ID (true value) for support */
    int supportMin; /* Min. used level in support calc. */
    int supportMax; /* Max. used level in support calc. */
    int[] supportSet; /* The found support set */
    BddCache applycache; /* Cache for apply results */
    BddCache itecache; /* Cache for ITE results */
    BddCache quantcache; /* Cache for exist/forall results */
    BddCache appexcache; /* Cache for appex/appall results */
    BddCache replacecache; /* Cache for replace results */
    BddCache misccache; /* Cache for other results */
    BddCache countcache; /* Cache for count results */
    int cacheratio;
    int satPolarity;
    int firstReorder;
    /* Used instead of local variable in order
                   to avoid compiler warning about 'first'
                   being clobbered by setjmp */

    byte[] allsatProfile; /* Variable profile for bdd_allsat() */

    void bdd_operator_init(int cachesize) {
        applycache = BddCacheI_init(cachesize);
        itecache = BddCacheI_init(cachesize);
        quantcache = BddCacheI_init(cachesize);
        appexcache = BddCacheI_init(cachesize);
        replacecache = BddCacheI_init(cachesize);
        misccache = BddCacheI_init(cachesize);
        countcache = BddCacheD_init(cachesize);

        quantvarsetID = 0;
        quantvarset = null;
        cacheratio = 0;
        supportSet = null;
    }

    void bdd_operator_done() {
        if (quantvarset != null) {
            free(quantvarset);
            quantvarset = null;
        }

        BddCache_done(applycache);
        BddCache_done(itecache);
        BddCache_done(quantcache);
        BddCache_done(appexcache);
        BddCache_done(replacecache);
        BddCache_done(misccache);

        if (supportSet != null) {
            free(supportSet);
            supportSet = null;
        }
    }

    void bdd_operator_reset() {
        BddCache_reset(applycache);
        BddCache_reset(itecache);
        BddCache_reset(quantcache);
        BddCache_reset(appexcache);
        BddCache_reset(replacecache);
        BddCache_reset(misccache);
    }

    void bdd_operator_varresize() {
        if (quantvarset != null)
            free(quantvarset);

        quantvarset = new int[bddvarnum];

        //memset(quantvarset, 0, sizeof(int)*bddvarnum);
        quantvarsetID = 0;
    }

    void bdd_operator_noderesize() {
        if (cacheratio > 0) {
            int newcachesize = bddnodesize / cacheratio;

            BddCache_resize(applycache, newcachesize);
            BddCache_resize(itecache, newcachesize);
            BddCache_resize(quantcache, newcachesize);
            BddCache_resize(appexcache, newcachesize);
            BddCache_resize(replacecache, newcachesize);
            BddCache_resize(misccache, newcachesize);
        }
    }

    BddCache BddCacheI_init(int size) {
        int n;

        size = bdd_prime_gte(size);

        BddCache cache = new BddCache();
        cache.table = new BddCacheDataI[size];

        for (n = 0; n < size; n++) {
            cache.table[n] = new BddCacheDataI();
            cache.table[n].a = -1;
        }
        cache.tablesize = size;

        return cache;
    }

    BddCache BddCacheD_init(int size) {
        int n;

        size = bdd_prime_gte(size);

        BddCache cache = new BddCache();
        cache.table = new BddCacheDataD[size];

        for (n = 0; n < size; n++) {
            cache.table[n] = new BddCacheDataD();
            cache.table[n].a = -1;
        }
        cache.tablesize = size;

        return cache;
    }

    void BddCache_done(BddCache cache) {
        free(cache.table);
        cache.table = null;
        cache.tablesize = 0;
    }

    int BddCache_resize(BddCache cache, int newsize) {
        int n;

        free(cache.table);
        cache.table = null;

        newsize = bdd_prime_gte(newsize);

        boolean is_d = cache.table instanceof BddCacheDataD[];

        if (is_d)
            cache.table = new BddCacheDataD[newsize];
        else
            cache.table = new BddCacheDataI[newsize];

        for (n = 0; n < newsize; n++) {
            if (is_d)
                cache.table[n] = new BddCacheDataD();
            else
                cache.table[n] = new BddCacheDataI();
            cache.table[n].a = -1;
        }
        cache.tablesize = newsize;

        return 0;
    }

    BddCacheDataI BddCache_lookupI(BddCache cache, int hash) {
        return (BddCacheDataI) cache.table[Math.abs(hash % cache.tablesize)];
    }

    BddCacheDataD BddCache_lookupD(BddCache cache, int hash) {
        return (BddCacheDataD) cache.table[Math.abs(hash % cache.tablesize)];
    }

    void BddCache_reset(BddCache cache) {
        int n;
        for (n = 0; n < cache.tablesize; n++)
            cache.table[n].a = -1;
    }

    void bdd_setpair(bddPair pair, int oldvar, int newvar) {
        if (pair == null)
            return;

        if (oldvar < 0 || oldvar > bddvarnum - 1)
            bdd_error(BDD_VAR);
        if (newvar < 0 || newvar > bddvarnum - 1)
            bdd_error(BDD_VAR);

        bdd_delref(pair.result[bddvar2level[oldvar]]);
        pair.result[bddvar2level[oldvar]] = bdd_ithvar(newvar);
        pair.id = update_pairsid();

        if (bddvar2level[oldvar] > pair.last)
            pair.last = bddvar2level[oldvar];

        return;
    }

    void bdd_setbddpair(bddPair pair, int oldvar, int newvar) {
        int oldlevel;

        if (pair == null)
            return;

        CHECK(newvar);
        if (oldvar < 0 || oldvar >= bddvarnum)
            bdd_error(BDD_VAR);
        oldlevel = bddvar2level[oldvar];

        bdd_delref(pair.result[oldlevel]);
        pair.result[oldlevel] = bdd_addref(newvar);
        pair.id = update_pairsid();

        if (oldlevel > pair.last)
            pair.last = oldlevel;

        return;
    }

    void bdd_resetpair(bddPair p) {
        int n;

        for (n = 0; n < bddvarnum; n++)
            p.result[n] = bdd_ithvar(n);
        p.last = 0;
    }

    class bddPair extends BDDPairing {
        int[] result;
        int last;
        int id;
        bddPair next;

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public void set(int oldvar, int newvar) {
            bdd_setpair(this, oldvar, newvar);
        }
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, org.sf.javabdd.BDD)
         */
        public void set(int oldvar, BDD newvar) {
            bdd_setbddpair(this, oldvar, ((bdd) newvar)._index);
        }
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public void reset() {
            bdd_resetpair(this);
        }
    }

    bddPair pairs; /* List of all replacement pairs in use */
    int pairsid; /* Pair identifier */

    static final void free(Object o) {
    }

    /*************************************************************************
    *************************************************************************/

    void bdd_pairs_init() {
        pairsid = 0;
        pairs = null;
    }

    void bdd_pairs_done() {
        bddPair p = pairs;
        int n;

        while (p != null) {
            bddPair next = p.next;
            for (n = 0; n < bddvarnum; n++)
                bdd_delref(p.result[n]);
            p.result = null;
            free(p.result);
            free(p);
            p = next;
        }
    }

    int update_pairsid() {
        pairsid++;

        if (pairsid == (INT_MAX >> 2)) {
            bddPair p;
            pairsid = 0;
            for (p = pairs; p != null; p = p.next)
                p.id = pairsid++;
            bdd_operator_reset();
        }

        return pairsid;
    }

    void bdd_register_pair(bddPair p) {
        p.next = pairs;
        pairs = p;
    }

    void bdd_pairs_vardown(int level) {
        bddPair p;

        for (p = pairs; p != null; p = p.next) {
            int tmp;

            tmp = p.result[level];
            p.result[level] = p.result[level + 1];
            p.result[level + 1] = tmp;

            if (p.last == level)
                p.last++;
        }
    }

    int bdd_pairs_resize(int oldsize, int newsize) {
        bddPair p;
        int n;

        for (p = pairs; p != null; p = p.next) {
            int[] new_result = new int[newsize];
            System.arraycopy(p.result, 0, new_result, 0, oldsize);
            p.result = new_result;

            for (n = oldsize; n < newsize; n++)
                p.result[n] = bdd_ithvar(bddlevel2var[n]);
        }

        return 0;
    }

    void bdd_disable_reorder() {
        reorderdisabled = 1;
    }
    void bdd_enable_reorder() {
        reorderdisabled = 0;
    }
    void bdd_checkreorder() {
        bdd_reorder_auto();

        /* Do not reorder before twice as many nodes have been used */
        usednodes_nextreorder = 2 * (bddnodesize - bddfreenum);

        /* And if very little was gained this time (< 20%) then wait until
         * even more nodes (upto twice as many again) have been used */
        if (bdd_reorder_gain() < 20)
            usednodes_nextreorder
                += (usednodes_nextreorder * (20 - bdd_reorder_gain()))
                / 20;
    }

    boolean bdd_reorder_ready() {
        if ((bddreordermethod == BDD_REORDER_NONE)
            || (vartree == null)
            || (bddreordertimes == 0)
            || (reorderdisabled != 0))
            return false;
        return true;
    }

    void bdd_reorder(int method) {
        BddTree top;
        int savemethod = bddreordermethod;
        int savetimes = bddreordertimes;

        bddreordermethod = method;
        bddreordertimes = 1;

        if ((top = bddtree_new(-1)) == null)
            return;
        if (reorder_init() < 0)
            return;

        usednum_before = bddnodesize - bddfreenum;

        top.first = 0;
        top.last = bdd_varnum() - 1;
        top.fixed = false;
        top.next = null;
        top.nextlevel = vartree;

        reorder_block(top, method);
        vartree = top.nextlevel;
        free(top);

        usednum_after = bddnodesize - bddfreenum;

        reorder_done();
        bddreordermethod = savemethod;
        bddreordertimes = savetimes;
    }

    BddTree bddtree_new(int id) {
        BddTree t = new BddTree();

        t.first = t.last = -1;
        t.fixed = true;
        t.next = t.prev = t.nextlevel = null;
        t.seq = null;
        t.id = id;
        return t;
    }

    BddTree reorder_block(BddTree t, int method) {
        BddTree dis;

        if (t == null)
            return null;

        if (!t.fixed /*BDD_REORDER_FREE*/
            && t.nextlevel != null) {
            switch (method) {
                case BDD_REORDER_WIN2 :
                    t.nextlevel = reorder_win2(t.nextlevel);
                    break;
                case BDD_REORDER_WIN2ITE :
                    t.nextlevel = reorder_win2ite(t.nextlevel);
                    break;
                case BDD_REORDER_SIFT :
                    t.nextlevel = reorder_sift(t.nextlevel);
                    break;
                case BDD_REORDER_SIFTITE :
                    t.nextlevel = reorder_siftite(t.nextlevel);
                    break;
                case BDD_REORDER_WIN3 :
                    t.nextlevel = reorder_win3(t.nextlevel);
                    break;
                case BDD_REORDER_WIN3ITE :
                    t.nextlevel = reorder_win3ite(t.nextlevel);
                    break;
                case BDD_REORDER_RANDOM :
                    t.nextlevel = reorder_random(t.nextlevel);
                    break;
            }
        }

        for (dis = t.nextlevel; dis != null; dis = dis.next)
            reorder_block(dis, method);

        if (t.seq != null) {
            throw new InternalError();
            //qsort(t.seq, t.last-t.first+1, sizeof(int), varseqCmp);
        }

        return t;
    }

    BddTree reorder_win2(BddTree t) {
        BddTree dis = t, first = t;

        if (t == null)
            return t;

        if (verbose > 1) {
            System.out.println("Win2 start: " + reorder_nodenum() + " nodes");
            System.out.flush();
        }

        while (dis.next != null) {
            int best = reorder_nodenum();
            blockdown(dis);

            if (best < reorder_nodenum()) {
                blockdown(dis.prev);
                dis = dis.next;
            } else if (first == dis)
                first = dis.prev;

            if (verbose > 1) {
                System.out.print(".");
                System.out.flush();
            }
        }

        if (verbose > 1) {
            System.out.println();
            System.out.println("Win2 end: " + reorder_nodenum() + " nodes");
            System.out.flush();
        }

        return first;
    }

    BddTree reorder_win3(BddTree t) {
        BddTree dis = t, first = t;

        if (t == null)
            return t;

        if (verbose > 1) {
            System.out.println("Win3 start: " + reorder_nodenum() + " nodes");
            System.out.flush();
        }

        while (dis.next != null) {
            BddTree[] f = new BddTree[1];
            f[0] = first;
            dis = reorder_swapwin3(dis, f);
            first = f[0];

            if (verbose > 1) {
                System.out.print(".");
                System.out.flush();
            }
        }

        if (verbose > 1) {
            System.out.println();
            System.out.println("Win3 end: " + reorder_nodenum() + " nodes");
            System.out.flush();
        }

        return first;
    }

    BddTree reorder_win3ite(BddTree t) {
        BddTree dis = t, first = t;
        int lastsize;

        if (t == null)
            return t;

        if (verbose > 1)
            System.out.println(
                "Win3ite start: " + reorder_nodenum() + " nodes");

        do {
            lastsize = reorder_nodenum();
            dis = first;

            while (dis.next != null && dis.next.next != null) {
                BddTree[] f = new BddTree[1];
                f[0] = first;
                dis = reorder_swapwin3(dis, f);
                first = f[0];

                if (verbose > 1) {
                    System.out.print(".");
                    System.out.flush();
                }
            }

            if (verbose > 1)
                System.out.println(" " + reorder_nodenum() + " nodes");
        }
        while (reorder_nodenum() != lastsize);

        if (verbose > 1)
            System.out.println("Win3ite end: " + reorder_nodenum() + " nodes");

        return first;
    }

    BddTree reorder_swapwin3(BddTree dis, BddTree[] first) {
        boolean setfirst = dis.prev == null;
        BddTree next = dis;
        int best = reorder_nodenum();

        if (dis.next.next == null) /* Only two blocks left => win2 swap */ {
            blockdown(dis.prev);

            if (best < reorder_nodenum()) {
                blockdown(dis.prev);
                next = dis.next;
            } else {
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }
        } else /* Real win3 swap */ {
            int pos = 0;
            blockdown(dis); /* B A* C (4) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            blockdown(dis); /* B C A* (3) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            dis = dis.prev.prev;
            blockdown(dis); /* C B* A (2) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            blockdown(dis); /* C A B* (1) */
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            dis = dis.prev.prev;
            blockdown(dis); /* A C* B (0)*/
            pos++;
            if (best > reorder_nodenum()) {
                pos = 0;
                best = reorder_nodenum();
            }

            if (pos >= 1) /* A C B -> C A* B */ {
                dis = dis.prev;
                blockdown(dis);
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }

            if (pos >= 2) /* C A B -> C B A* */ {
                blockdown(dis);
                next = dis.prev;
                if (setfirst)
                    first[0] = dis.prev.prev;
            }

            if (pos >= 3) /* C B A -> B C* A */ {
                dis = dis.prev.prev;
                blockdown(dis);
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }

            if (pos >= 4) /* B C A -> B A C* */ {
                blockdown(dis);
                next = dis.prev;
                if (setfirst)
                    first[0] = dis.prev.prev;
            }

            if (pos >= 5) /* B A C -> A B* C */ {
                dis = dis.prev.prev;
                blockdown(dis);
                next = dis;
                if (setfirst)
                    first[0] = dis.prev;
            }
        }

        return next;
    }

    BddTree reorder_sift_seq(BddTree t, BddTree seq[], int num) {
        BddTree dis;
        int n;

        if (t == null)
            return t;

        for (n = 0; n < num; n++) {
            long c2, c1 = clock();

            if (verbose > 1) {
                System.out.print("Sift ");
                //if (reorder_filehandler)
                //   reorder_filehandler(stdout, seq[n].id);
                //else
                System.out.print(seq[n].id);
                System.out.print(": ");
            }

            reorder_sift_bestpos(seq[n], num / 2);

            if (verbose > 1) {
                System.out.println();
                System.out.print("> " + reorder_nodenum() + " nodes");
            }

            c2 = clock();
            if (verbose > 1)
                System.out.println(
                    " (" + (float) (c2 - c1) / (float) 1000 + " sec)\n");
        }

        /* Find first block */
        for (dis = t; dis.prev != null; dis = dis.prev)
            /* nil */;

        return dis;
    }

    void reorder_sift_bestpos(BddTree blk, int middlePos) {
        int best = reorder_nodenum();
        int maxAllowed;
        int bestpos = 0;
        boolean dirIsUp = true;
        int n;

        if (bddmaxnodesize > 0)
            maxAllowed =
                MIN(best / 5 + best, bddmaxnodesize - bddmaxnodeincrease - 2);
        else
            maxAllowed = best / 5 + best;

        /* Determine initial direction */
        if (blk.pos > middlePos)
            dirIsUp = false;

        /* Move block back and forth */
        for (n = 0; n < 2; n++) {
            int first = 1;

            if (dirIsUp) {
                while (blk.prev != null
                    && (reorder_nodenum() <= maxAllowed || first != 0)) {
                    first = 0;
                    blockdown(blk.prev);
                    bestpos--;

                    if (verbose > 1) {
                        System.out.print("-");
                        System.out.flush();
                    }

                    if (reorder_nodenum() < best) {
                        best = reorder_nodenum();
                        bestpos = 0;

                        if (bddmaxnodesize > 0)
                            maxAllowed =
                                MIN(
                                    best / 5 + best,
                                    bddmaxnodesize - bddmaxnodeincrease - 2);
                        else
                            maxAllowed = best / 5 + best;
                    }
                }
            } else {
                while (blk.next != null
                    && (reorder_nodenum() <= maxAllowed || first != 0)) {
                    first = 0;
                    blockdown(blk);
                    bestpos++;

                    if (verbose > 1) {
                        System.out.print("+");
                        System.out.flush();
                    }

                    if (reorder_nodenum() < best) {
                        best = reorder_nodenum();
                        bestpos = 0;

                        if (bddmaxnodesize > 0)
                            maxAllowed =
                                MIN(
                                    best / 5 + best,
                                    bddmaxnodesize - bddmaxnodeincrease - 2);
                        else
                            maxAllowed = best / 5 + best;
                    }
                }
            }

            if (reorder_nodenum() > maxAllowed && verbose > 1) {
                System.out.print("!");
                System.out.flush();
            }

            dirIsUp = !dirIsUp;
        }

        /* Move to best pos */
        while (bestpos < 0) {
            blockdown(blk);
            bestpos++;
        }
        while (bestpos > 0) {
            blockdown(blk.prev);
            bestpos--;
        }
    }

    BddTree reorder_random(BddTree t) {
        BddTree dis;
        BddTree[] seq;
        int n, num = 0;

        if (t == null)
            return t;

        for (dis = t; dis != null; dis = dis.next)
            num++;
        seq = new BddTree[num];
        for (dis = t, num = 0; dis != null; dis = dis.next)
            seq[num++] = dis;

        for (n = 0; n < 4 * num; n++) {
            int blk = rng.nextInt(num);
            if (seq[blk].next != null)
                blockdown(seq[blk]);
        }

        /* Find first block */
        for (dis = t; dis.prev != null; dis = dis.prev)
            /* nil */;

        free(seq);

        if (verbose != 0)
            System.out.println("Random order: " + reorder_nodenum() + " nodes");
        return dis;
    }

    static int siftTestCmp(Object aa, Object bb) {
        sizePair a = (sizePair) aa;
        sizePair b = (sizePair) bb;

        if (a.val < b.val)
            return -1;
        if (a.val > b.val)
            return 1;
        return 0;
    }

    static class sizePair {
        int val;
        BddTree block;
    }

    BddTree reorder_sift(BddTree t) {
        BddTree dis, seq[];
        sizePair[] p;
        int n, num;

        for (dis = t, num = 0; dis != null; dis = dis.next)
            dis.pos = num++;

        p = new sizePair[num];
        seq = new BddTree[num];

        for (dis = t, n = 0; dis != null; dis = dis.next, n++) {
            int v;

            /* Accumulate number of nodes for each block */
            p[n].val = 0;
            for (v = dis.first; v <= dis.last; v++)
                p[n].val -= levels[v].nodenum;

            p[n].block = dis;
        }

        /* Sort according to the number of nodes at each level */
        Arrays.sort(p, 0, num, new Comparator() {

            public int compare(Object o1, Object o2) {
                return siftTestCmp(o1, o2);
            }

        });

        /* Create sequence */
        for (n = 0; n < num; n++)
            seq[n] = p[n].block;

        /* Do the sifting on this sequence */
        t = reorder_sift_seq(t, seq, num);

        free(seq);
        free(p);

        return t;
    }

    BddTree reorder_siftite(BddTree t) {
        BddTree first = t;
        int lastsize;
        int c = 1;

        if (t == null)
            return t;

        do {
            if (verbose > 1)
                System.out.println("Reorder " + (c++) + "\n");

            lastsize = reorder_nodenum();
            first = reorder_sift(first);
        } while (reorder_nodenum() != lastsize);

        return first;
    }

    void blockdown(BddTree left) {
        BddTree right = left.next;
        int n;
        int leftsize = left.last - left.first;
        int rightsize = right.last - right.first;
        int leftstart = bddvar2level[left.seq[0]];
        int[] lseq = left.seq;
        int[] rseq = right.seq;

        /* Move left past right */
        while (bddvar2level[lseq[0]] < bddvar2level[rseq[rightsize]]) {
            for (n = 0; n < leftsize; n++) {
                if (bddvar2level[lseq[n]] + 1 != bddvar2level[lseq[n + 1]]
                    && bddvar2level[lseq[n]] < bddvar2level[rseq[rightsize]]) {
                    reorder_vardown(lseq[n]);
                }
            }

            if (bddvar2level[lseq[leftsize]] < bddvar2level[rseq[rightsize]]) {
                reorder_vardown(lseq[leftsize]);
            }
        }

        /* Move right to where left started */
        while (bddvar2level[rseq[0]] > leftstart) {
            for (n = rightsize; n > 0; n--) {
                if (bddvar2level[rseq[n]] - 1 != bddvar2level[rseq[n - 1]]
                    && bddvar2level[rseq[n]] > leftstart) {
                    reorder_varup(rseq[n]);
                }
            }

            if (bddvar2level[rseq[0]] > leftstart)
                reorder_varup(rseq[0]);
        }

        /* Swap left and right data in the order */
        left.next = right.next;
        right.prev = left.prev;
        left.prev = right;
        right.next = left;

        if (right.prev != null)
            right.prev.next = right;
        if (left.next != null)
            left.next.prev = left;

        n = left.pos;
        left.pos = right.pos;
        right.pos = n;
    }

    BddTree reorder_win2ite(BddTree t) {
        BddTree dis, first = t;
        int lastsize;
        int c = 1;

        if (t == null)
            return t;

        if (verbose > 1)
            System.out.println(
                "Win2ite start: " + reorder_nodenum() + " nodes");

        do {
            lastsize = reorder_nodenum();

            dis = t;
            while (dis.next != null) {
                int best = reorder_nodenum();

                blockdown(dis);

                if (best < reorder_nodenum()) {
                    blockdown(dis.prev);
                    dis = dis.next;
                } else if (first == dis)
                    first = dis.prev;
                if (verbose > 1) {
                    System.out.print(".");
                    System.out.flush();
                }
            }

            if (verbose > 1)
                System.out.println(" " + reorder_nodenum() + " nodes");
            c++;
        }
        while (reorder_nodenum() != lastsize);

        return first;
    }

    void bdd_reorder_auto() {
        if (!bdd_reorder_ready())
            return;

        //if (reorder_handler != null)
        reorder_handler(true);

        bdd_reorder(bddreordermethod);
        bddreordertimes--;

        //if (reorder_handler != null)
        reorder_handler(false);
    }

    void reorder_handler(boolean b) {
    }

    int bdd_reorder_gain() {
        if (usednum_before == 0)
            return 0;

        return (100 * (usednum_before - usednum_after)) / usednum_before;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#isInitialized()
     */
    public boolean isInitialized() {
        return this.bddrunning;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#done()
     */
    public void done() {
        bdd_done();
    }

    void bdd_done() {
        /*sanitycheck(); FIXME */
        //bdd_fdd_done();
        //bdd_reorder_done();
        bdd_pairs_done();

        free(bddnodes);
        free(bddrefstack);
        free(bddvarset);
        free(bddvar2level);
        free(bddlevel2var);

        bddnodes = null;
        bddrefstack = null;
        bddvarset = null;

        bdd_operator_done();

        bddrunning = false;
        bddnodesize = 0;
        bddmaxnodesize = 0;
        bddvarnum = 0;
        bddproduced = 0;

        //err_handler = null;
        //gbc_handler = null;
        //resize_handler = null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxNodeNum(int)
     */
    public int setMaxNodeNum(int size) {
        return bdd_setmaxnodenum(size);
    }

    int bdd_setmaxnodenum(int size) {
        if (size > bddnodesize || size == 0) {
            int old = bddmaxnodesize;
            bddmaxnodesize = size;
            return old;
        }

        return bdd_error(BDD_NODES);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMinFreeNodes(int)
     */
    public void setMinFreeNodes(int x) {
        bdd_setminfreenodes(x);
    }

    int bdd_setminfreenodes(int mf) {
        int old = minfreenodes;

        if (mf < 0 || mf > 100)
            return bdd_error(BDD_RANGE);

        minfreenodes = mf;
        return old;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public int setMaxIncrease(int x) {
        return bdd_setmaxincrease(x);
    }

    int bdd_setmaxincrease(int size) {
        int old = bddmaxnodeincrease;

        if (size < 0)
            return bdd_error(BDD_SIZE);

        bddmaxnodeincrease = size;
        return old;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setCacheRatio(int)
     */
    public int setCacheRatio(int x) {
        return bdd_setcacheratio(x);
    }

    int bdd_setcacheratio(int r) {
        int old = cacheratio;

        if (r <= 0)
            return bdd_error(BDD_RANGE);
        if (bddnodesize == 0)
            return old;

        cacheratio = r;
        bdd_operator_noderesize();
        return old;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varNum()
     */
    public int varNum() {
        return bdd_varnum();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarNum(int)
     */
    public int setVarNum(int num) {
        return bdd_setvarnum(num);
    }

    int bdd_setvarnum(int num) {
        int bdv;
        int oldbddvarnum = bddvarnum;

        bdd_disable_reorder();

        if (num < 1 || num > MAXVAR) {
            bdd_error(BDD_RANGE);
            return bddfalse;
        }

        if (num < bddvarnum)
            return bdd_error(BDD_DECVNUM);
        if (num == bddvarnum)
            return 0;

        if (bddvarset == null) {
            bddvarset = new int[num * 2];
            bddlevel2var = new int[num + 1];
            bddvar2level = new int[num + 1];
        } else {
            int[] bddvarset2 = new int[num * 2];
            System.arraycopy(bddvarset, 0, bddvarset2, 0, bddvarset.length);
            bddvarset = bddvarset2;
            int[] bddlevel2var2 = new int[num + 1];
            System.arraycopy(
                bddlevel2var,
                0,
                bddlevel2var2,
                0,
                bddlevel2var.length);
            bddlevel2var = bddlevel2var2;
            int[] bddvar2level2 = new int[num + 1];
            System.arraycopy(
                bddvar2level,
                0,
                bddvar2level2,
                0,
                bddvar2level.length);
            bddvar2level = bddvar2level2;
        }

        if (bddrefstack != null)
            free(bddrefstack);
        bddrefstack = new int[num * 2 + 1];
        bddrefstacktop = 0;

        for (bdv = bddvarnum; bddvarnum < num; bddvarnum++) {
            bddvarset[bddvarnum * 2] = PUSHREF(bdd_makenode(bddvarnum, 0, 1));
            bddvarset[bddvarnum * 2 + 1] = bdd_makenode(bddvarnum, 1, 0);
            POPREF(1);

            if (bdderrorcond != 0) {
                bddvarnum = bdv;
                return -bdderrorcond;
            }

            bddnodes[bddvarset[bddvarnum * 2]].setMaxRef();
            bddnodes[bddvarset[bddvarnum * 2 + 1]].setMaxRef();
            bddlevel2var[bddvarnum] = bddvarnum;
            bddvar2level[bddvarnum] = bddvarnum;
        }

        bddnodes[0].setLevelAndMarked(num);
        bddnodes[1].setLevelAndMarked(num);
        bddvar2level[num] = num;
        bddlevel2var[num] = num;

        bdd_pairs_resize(oldbddvarnum, bddvarnum);
        bdd_operator_varresize();

        bdd_enable_reorder();

        return 0;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#ithVar(int)
     */
    public BDD ithVar(int var) {
        return new bdd(bdd_ithvar(var));
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nithVar(int)
     */
    public BDD nithVar(int var) {
        return new bdd(bdd_nithvar(var));
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printAll()
     */
    public void printAll() {
        bdd_fprintall(System.out);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printTable(org.sf.javabdd.BDD)
     */
    public void printTable(BDD b) {
        int x = ((bdd) b)._index;
        bdd_fprinttable(System.out, x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#load(java.lang.String)
     */
    public BDD load(String filename) throws IOException {
        DataInputStream is = new DataInputStream(new FileInputStream(filename));
        int result = bdd_load(is);
        return new bdd(result);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD b) throws IOException {
        int x = ((bdd) b)._index;
        DataOutputStream is =
            new DataOutputStream(new FileOutputStream(filename));
        bdd_save(is, x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#level2Var(int)
     */
    public int level2Var(int level) {
        return bddlevel2var[level];
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#var2Level(int)
     */
    public int var2Level(int var) {
        return bddvar2level[var];
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void reorder(ReorderMethod m) {
        int x = m.id;
        bdd_reorder(x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void autoReorder(ReorderMethod method) {
        int x = method.id;
        bdd_autoreorder(x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod, int)
     */
    public void autoReorder(ReorderMethod method, int max) {
        int x = method.id;
        bdd_autoreorder_times(x, max);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderMethod()
     */
    public ReorderMethod getReorderMethod() {
        switch (bddreordermethod) {
            case BDD_REORDER_NONE :
                return REORDER_NONE;
            case BDD_REORDER_WIN2 :
                return REORDER_WIN2;
            case BDD_REORDER_WIN2ITE :
                return REORDER_WIN2ITE;
            case BDD_REORDER_WIN3 :
                return REORDER_WIN3;
            case BDD_REORDER_WIN3ITE :
                return REORDER_WIN3ITE;
            case BDD_REORDER_SIFT :
                return REORDER_SIFT;
            case BDD_REORDER_SIFTITE :
                return REORDER_SIFTITE;
            case BDD_REORDER_RANDOM :
                return REORDER_RANDOM;
            default :
                throw new BDDException();
        }
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderTimes()
     */
    public int getReorderTimes() {
        return bddreordertimes;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#disableReorder()
     */
    public void disableReorder() {
        bdd_disable_reorder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#enableReorder()
     */
    public void enableReorder() {
        bdd_enable_reorder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderVerbose(int)
     */
    public int reorderVerbose(int v) {
        return bdd_reorder_verbose(v);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarOrder(int[])
     */
    public void setVarOrder(int[] neworder) {
        bdd_setvarorder(neworder);
    }

    static class BddTree {
        int first, last; /* First and last variable in this block */
        int pos; /* Sifting position */
        int[] seq; /* Sequence of first...last in the current order */
        boolean fixed; /* Are the sub-blocks fixed or may they be reordered */
        int id; /* A sequential id number given by addblock */
        BddTree next, prev;
        BddTree nextlevel;
    }

    /* Current auto reord. method and number of automatic reorderings left */
    int bddreordermethod;
    int bddreordertimes;

    /* Flag for disabling reordering temporarily */
    int reorderdisabled;

    BddTree vartree;
    int blockid;

    int[] extroots;
    int extrootsize;

    levelData levels[]; /* Indexed by variable! */

    static class levelData {
        int start; /* Start of this sub-table (entry in "bddnodes") */
        int size; /* Size of this sub-table */
        int maxsize; /* Max. allowed size of sub-table */
        int nodenum; /* Number of nodes in this level */
    }

    static class imatrix {
        byte rows[][];
        int size;
    }

    /* Interaction matrix */
    imatrix iactmtx;

    int verbose;
    //bddinthandler reorder_handler;
    //bddfilehandler reorder_filehandler;
    //bddsizehandler reorder_nodenum;

    /* Number of live nodes before and after a reordering session */
    int usednum_before;
    int usednum_after;

    void bdd_reorder_init() {
        reorderdisabled = 0;
        vartree = null;

        bdd_clrvarblocks();
        //bdd_reorder_hook(bdd_default_reohandler);
        bdd_reorder_verbose(0);
        bdd_autoreorder_times(BDD_REORDER_NONE, 0);
        //reorder_nodenum = bdd_getnodenum;
        usednum_before = usednum_after = 0;
        blockid = 0;
    }

    int reorder_nodenum() {
        return bdd_getnodenum();
    }

    int bdd_getnodenum() {
        return bddnodesize - bddfreenum;
    }

    int bdd_reorder_verbose(int v) {
        int tmp = verbose;
        verbose = v;
        return tmp;
    }

    int bdd_autoreorder(int method) {
        int tmp = bddreordermethod;
        bddreordermethod = method;
        bddreordertimes = -1;
        return tmp;
    }

    int bdd_autoreorder_times(int method, int num) {
        int tmp = bddreordermethod;
        bddreordermethod = method;
        bddreordertimes = num;
        return tmp;
    }

    static final int BDD_REORDER_NONE = 0;
    static final int BDD_REORDER_WIN2 = 1;
    static final int BDD_REORDER_WIN2ITE = 2;
    static final int BDD_REORDER_SIFT = 3;
    static final int BDD_REORDER_SIFTITE = 4;
    static final int BDD_REORDER_WIN3 = 5;
    static final int BDD_REORDER_WIN3ITE = 6;
    static final int BDD_REORDER_RANDOM = 7;

    static final int BDD_REORDER_FREE = 0;
    static final int BDD_REORDER_FIXED = 1;

    static long c1;

    void bdd_default_reohandler(int prestate) {

        if (verbose > 0) {
            if (prestate != 0) {
                System.out.println("Start reordering");
                c1 = clock();
            } else {
                long c2 = clock();
                System.out.println(
                    "End reordering. Went from "
                        + usednum_before
                        + " to "
                        + usednum_after
                        + " nodes ("
                        + (float) (c2 - c1) / (float) 1000
                        + " sec)");
            }
        }
    }

    void bdd_reorder_done() {
        bddtree_del(vartree);
        bdd_operator_reset();
        vartree = null;
    }

    void bddtree_del(BddTree t) {
        if (t == null)
            return;

        bddtree_del(t.nextlevel);
        bddtree_del(t.next);
        if (t.seq != null)
            free(t.seq);
        t.seq = null;
        free(t);
    }

    void bdd_clrvarblocks() {
        bddtree_del(vartree);
        vartree = null;
        blockid = 0;
    }

    int NODEHASHr(int var, int l, int h) {
        return (Math.abs(PAIR(l, (h)) % levels[var].size) + levels[var].start);
    }

    void bdd_setvarorder(int[] neworder) {
        int level;

        /* Do not set order when variable-blocks are used */
        if (vartree != null) {
            bdd_error(BDD_VARBLK);
            return;
        }

        reorder_init();

        for (level = 0; level < bddvarnum; level++) {
            int lowvar = neworder[level];

            while (bddvar2level[lowvar] > level)
                reorder_varup(lowvar);
        }

        reorder_done();
    }

    int reorder_varup(int var) {
        if (var < 0 || var >= bddvarnum)
            return bdd_error(BDD_VAR);
        if (bddvar2level[var] == 0)
            return 0;
        return reorder_vardown(bddlevel2var[bddvar2level[var] - 1]);
    }

    int reorder_vardown(int var) {
        int n, level;

        if (var < 0 || var >= bddvarnum)
            return bdd_error(BDD_VAR);
        if ((level = bddvar2level[var]) >= bddvarnum - 1)
            return 0;

        resizedInMakenode = false;

        if (imatrixDepends(iactmtx, var, bddlevel2var[level + 1])) {
            int toBeProcessed = reorder_downSimple(var);
            levelData l = levels[var];

            if (l.nodenum < (l.size) / 3
                || l.nodenum >= (l.size * 3) / 2
                && l.size < l.maxsize) {
                reorder_swapResize(toBeProcessed, var);
                reorder_localGbcResize(toBeProcessed, var);
            } else {
                reorder_swap(toBeProcessed, var);
                reorder_localGbc(var);
            }
        }

        /* Swap the var<->level tables */
        n = bddlevel2var[level];
        bddlevel2var[level] = bddlevel2var[level + 1];
        bddlevel2var[level + 1] = n;

        n = bddvar2level[var];
        bddvar2level[var] = bddvar2level[bddlevel2var[level]];
        bddvar2level[bddlevel2var[level]] = n;

        /* Update all rename pairs */
        bdd_pairs_vardown(level);

        if (resizedInMakenode)
            reorder_rehashAll();

        return 0;
    }

    boolean imatrixDepends(imatrix mtx, int a, int b) {
        return (mtx.rows[a][b / 8] & (1 << (b % 8))) != 0;
    }

    void reorder_setLevellookup() {
        int n;

        for (n = 0; n < bddvarnum; n++) {
            levels[n].maxsize = bddnodesize / bddvarnum;
            levels[n].start = n * levels[n].maxsize;
            levels[n].size =
                Math.min(levels[n].maxsize, (levels[n].nodenum * 5) / 4);

            if (levels[n].size >= 4)
                levels[n].size = bdd_prime_lte(levels[n].size);

        }
    }

    int VARr(int n) {
        return bddnodes[n].getLevelAndMarked();
    }
    int VARpr(BddNode p) {
        return p.getLevelAndMarked();
    }
    void setVARr(int n, int v) {
        bddnodes[n].setLevelAndMarked(v);
    }
    void setVARpr(BddNode p, int v) {
        p.setLevelAndMarked(v);
    }

    void reorder_rehashAll() {
        int n;

        reorder_setLevellookup();
        bddfreepos = 0;

        for (n = bddnodesize - 1; n >= 0; n--)
            bddnodes[n].hash = 0;

        for (n = bddnodesize - 1; n >= 2; n--) {
            BddNode node = bddnodes[n];

            if (node.hasRef()) {
                int hash;

                hash = NODEHASH2(VARpr(node), node.low, node.high);
                node.next = bddnodes[hash].hash;
                bddnodes[hash].hash = n;
            } else {
                node.next = bddfreepos;
                bddfreepos = n;
            }
        }
    }

    void reorder_localGbc(int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];
        int vl1 = levels[var1].start;
        int size1 = levels[var1].size;
        int n;

        for (n = 0; n < size1; n++) {
            int hash = n + vl1;
            int r = bddnodes[hash].hash;
            bddnodes[hash].hash = 0;

            while (r != 0) {
                BddNode node = bddnodes[r];
                int next = node.next;

                if (node.hasRef()) {
                    node.next = bddnodes[hash].hash;
                    bddnodes[hash].hash = r;
                } else {
                    DECREF(node.low);
                    DECREF(node.high);

                    node.low = -1;
                    node.next = bddfreepos;
                    bddfreepos = r;
                    levels[var1].nodenum--;
                    bddfreenum++;
                }

                r = next;
            }
        }
    }

    public static boolean SWAPCOUNT = false;

    int reorder_downSimple(int var0) {
        int toBeProcessed = 0;
        int var1 = bddlevel2var[bddvar2level[var0] + 1];
        int vl0 = levels[var0].start;
        int size0 = levels[var0].size;
        int n;

        levels[var0].nodenum = 0;

        for (n = 0; n < size0; n++) {
            int r;

            r = bddnodes[n + vl0].hash;
            bddnodes[n + vl0].hash = 0;

            while (r != 0) {
                BddNode node = bddnodes[r];
                int next = node.next;

                if (VARr(node.low) != var1 && VARr(node.high) != var1) {
                    /* Node does not depend on next var, let it stay in the chain */
                    node.next = bddnodes[n + vl0].hash;
                    bddnodes[n + vl0].hash = r;
                    levels[var0].nodenum++;
                } else {
                    /* Node depends on next var - save it for later procesing */
                    node.next = toBeProcessed;
                    toBeProcessed = r;
                    if (SWAPCOUNT)
                        bddcachestats.swapCount++;

                }

                r = next;
            }
        }

        return toBeProcessed;
    }

    void reorder_swapResize(int toBeProcessed, int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];

        while (toBeProcessed != 0) {
            BddNode node = bddnodes[toBeProcessed];
            int next = node.next;
            int f0 = node.low;
            int f1 = node.high;
            int f00, f01, f10, f11;

            /* Find the cofactors for the new nodes */
            if (VARr(f0) == var1) {
                f00 = LOW(f0);
                f01 = HIGH(f0);
            } else
                f00 = f01 = f0;

            if (VARr(f1) == var1) {
                f10 = LOW(f1);
                f11 = HIGH(f1);
            } else
                f10 = f11 = f1;

            /* Note: makenode does refcou. */
            f0 = reorder_makenode(var0, f00, f10);
            f1 = reorder_makenode(var0, f01, f11);
            node = bddnodes[toBeProcessed]; /* Might change in makenode */

            /* We know that the refcou of the grandchilds of this node
            * is greater than one (these are f00...f11), so there is
            * no need to do a recursive refcou decrease. It is also
            * possible for the node.low/high nodes to come alive again,
            * so deref. of the childs is delayed until the local GBC. */

            DECREF(node.low);
            DECREF(node.high);

            /* Update in-place */
            setVARpr(node, var1);
            node.low = f0;
            node.high = f1;

            levels[var1].nodenum++;

            /* Do not rehash yet since we are going to resize the hash table */

            toBeProcessed = next;
        }
    }

    static final int MIN(int a, int b) {
        return Math.min(a, b);
    }

    void reorder_localGbcResize(int toBeProcessed, int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];
        int vl1 = levels[var1].start;
        int size1 = levels[var1].size;
        int n;

        for (n = 0; n < size1; n++) {
            int hash = n + vl1;
            int r = bddnodes[hash].hash;
            bddnodes[hash].hash = 0;

            while (r != 0) {
                BddNode node = bddnodes[r];
                int next = node.next;

                if (node.hasRef()) {
                    node.next = toBeProcessed;
                    toBeProcessed = r;
                } else {
                    DECREF(node.low);
                    DECREF(node.high);

                    node.low = -1;
                    node.next = bddfreepos;
                    bddfreepos = r;
                    levels[var1].nodenum--;
                    bddfreenum++;
                }

                r = next;
            }
        }

        /* Resize */
        if (levels[var1].nodenum < levels[var1].size)
            levels[var1].size =
                MIN(levels[var1].maxsize, levels[var1].size / 2);
        else
            levels[var1].size =
                MIN(levels[var1].maxsize, levels[var1].size * 2);

        if (levels[var1].size >= 4)
            levels[var1].size = bdd_prime_lte(levels[var1].size);

        /* Rehash the remaining live nodes */
        while (toBeProcessed != 0) {
            BddNode node = bddnodes[toBeProcessed];
            int next = node.next;
            int hash = NODEHASH2(VARpr(node), node.low, node.high);

            node.next = bddnodes[hash].hash;
            bddnodes[hash].hash = toBeProcessed;

            toBeProcessed = next;
        }
    }

    void reorder_swap(int toBeProcessed, int var0) {
        int var1 = bddlevel2var[bddvar2level[var0] + 1];

        while (toBeProcessed != 0) {
            BddNode node = bddnodes[toBeProcessed];
            int next = node.next;
            int f0 = node.low;
            int f1 = node.high;
            int f00, f01, f10, f11, hash;

            /* Find the cofactors for the new nodes */
            if (VARr(f0) == var1) {
                f00 = LOW(f0);
                f01 = HIGH(f0);
            } else
                f00 = f01 = f0;

            if (VARr(f1) == var1) {
                f10 = LOW(f1);
                f11 = HIGH(f1);
            } else
                f10 = f11 = f1;

            /* Note: makenode does refcou. */
            f0 = reorder_makenode(var0, f00, f10);
            f1 = reorder_makenode(var0, f01, f11);
            node = bddnodes[toBeProcessed]; /* Might change in makenode */

            /* We know that the refcou of the grandchilds of this node
            * is greater than one (these are f00...f11), so there is
            * no need to do a recursive refcou decrease. It is also
            * possible for the node.low/high nodes to come alive again,
            * so deref. of the childs is delayed until the local GBC. */

            DECREF(node.low);
            DECREF(node.high);

            /* Update in-place */
            setVARpr(node, var1);
            node.low = f0;
            node.high = f1;

            levels[var1].nodenum++;

            /* Rehash the node since it got new childs */
            hash = NODEHASH2(VARpr(node), node.low, node.high);
            node.next = bddnodes[hash].hash;
            bddnodes[hash].hash = toBeProcessed;

            toBeProcessed = next;
        }
    }

    int NODEHASH2(int var, int l, int h) {
        return (Math.abs(PAIR(l, h) % levels[var].size) + levels[var].start);
    }

    boolean resizedInMakenode;

    int reorder_makenode(int var, int low, int high) {
        BddNode node;
        int hash;
        int res;

        if (CACHESTATS)
            bddcachestats.uniqueAccess++;

        /* Note: We know that low,high has a refcou greater than zero, so
        there is no need to add reference *recursively* */

        /* check whether childs are equal */
        if (low == high) {
            INCREF(low);
            return low;
        }

        /* Try to find an existing node of this kind */
        hash = NODEHASH2(var, low, high);
        res = bddnodes[hash].hash;

        while (res != 0) {
            if (LOW(res) == low && HIGH(res) == high) {
                if (CACHESTATS)
                    bddcachestats.uniqueHit++;
                INCREF(res);
                return res;
            }
            res = bddnodes[res].next;

            if (CACHESTATS)
                bddcachestats.uniqueChain++;
        }

        /* No existing node -> build one */
        if (CACHESTATS)
            bddcachestats.uniqueMiss++;

        /* Any free nodes to use ? */
        if (bddfreepos == 0) {
            if (bdderrorcond != 0)
                return 0;

            /* Try to allocate more nodes - call noderesize without
            * enabling rehashing.
             * Note: if ever rehashing is allowed here, then remember to
            * update local variable "hash" */
            bdd_noderesize(false);
            resizedInMakenode = true;

            /* Panic if that is not possible */
            if (bddfreepos == 0) {
                bdd_error(BDD_NODENUM);
                bdderrorcond = Math.abs(BDD_NODENUM);
                return 0;
            }
        }

        /* Build new node */
        res = bddfreepos;
        bddfreepos = bddnodes[bddfreepos].next;
        levels[var].nodenum++;
        bddproduced++;
        bddfreenum--;

        node = bddnodes[res];
        setVARpr(node, var);
        node.low = low;
        node.high = high;

        /* Insert node in hash chain */
        node.next = bddnodes[hash].hash;
        bddnodes[hash].hash = res;

        /* Make sure it is reference counted */
        node.clearRef();
        node.incRef();
        INCREF(node.low);
        INCREF(node.high);

        return res;
    }

    int reorder_init() {
        int n;

        levels = new levelData[bddvarnum];

        for (n = 0; n < bddvarnum; n++) {
            levels[n] = new levelData();
            levels[n].start = -1;
            levels[n].size = 0;
            levels[n].nodenum = 0;
        }

        /* First mark and recursive refcou. all roots and childs. Also do some
         * setup here for both setLevellookup and reorder_gbc */
        if (mark_roots() < 0)
            return -1;

        /* Initialize the hash tables */
        reorder_setLevellookup();

        /* Garbage collect and rehash to new scheme */
        reorder_gbc();

        return 0;
    }

    int mark_roots() {
        boolean[] dep = new boolean[bddvarnum];
        int n;

        for (n = 2, extrootsize = 0; n < bddnodesize; n++) {
            /* This is where we go from .level to .var!
            * - Do NOT use the LEVEL macro here. */
            bddnodes[n].setLevelAndMarked(
                bddlevel2var[bddnodes[n].getLevelAndMarked()]);

            if (bddnodes[n].hasRef()) {
                SETMARK(n);
                extrootsize++;
            }
        }

        extroots = new int[extrootsize];

        iactmtx = imatrixNew(bddvarnum);

        for (n = 2, extrootsize = 0; n < bddnodesize; n++) {
            BddNode node = bddnodes[n];

            if (node.isMarked()) {
                node.unmark();
                extroots[extrootsize++] = n;

                for (int i = 0; i < bddvarnum; ++i)
                    dep[i] = false;
                dep[VARpr(node)] = true;
                levels[VARpr(node)].nodenum++;

                addref_rec(node.low, dep);
                addref_rec(node.high, dep);

                addDependencies(dep);
            }

            /* Make sure the hash field is empty. This saves a loop in the
            initial GBC */
            node.hash = 0;
        }

        bddnodes[0].hash = 0;
        bddnodes[1].hash = 0;

        free(dep);
        return 0;
    }

    imatrix imatrixNew(int size) {
        imatrix mtx = new imatrix();
        int n, m;

        mtx.rows = new byte[size][];

        for (n = 0; n < size; n++) {
            mtx.rows[n] = new byte[size / 8 + 1];
        }

        mtx.size = size;

        return mtx;
    }

    void addref_rec(int r, boolean[] dep) {
        if (r < 2)
            return;

        if (!bddnodes[r].hasRef()) {
            bddfreenum--;

            /* Detect variable dependencies for the interaction matrix */
            dep[VARr(r) & ~BddNode.MARK_MASK] = true;

            /* Make sure the nodenum field is updated. Used in the initial GBC */
            levels[VARr(r) & ~BddNode.MARK_MASK].nodenum++;

            addref_rec(LOW(r), dep);
            addref_rec(HIGH(r), dep);
        } else {
            int n;

            /* Update (from previously found) variable dependencies
            * for the interaction matrix */
            for (n = 0; n < bddvarnum; n++)
                dep[n]
                    |= imatrixDepends(iactmtx, VARr(r) & ~BddNode.MARK_MASK, n);
        }

        INCREF(r);
    }

    void addDependencies(boolean[] dep) {
        int n, m;

        for (n = 0; n < bddvarnum; n++) {
            for (m = n; m < bddvarnum; m++) {
                if ((dep[n]) && (dep[m])) {
                    imatrixSet(iactmtx, n, m);
                    imatrixSet(iactmtx, m, n);
                }
            }
        }
    }

    void imatrixSet(imatrix mtx, int a, int b) {
        mtx.rows[a][b / 8] |= 1 << (b % 8);
    }

    void reorder_gbc() {
        int n;

        bddfreepos = 0;
        bddfreenum = 0;

        /* No need to zero all hash fields - this is done in mark_roots */

        for (n = bddnodesize - 1; n >= 2; n--) {
            BddNode node = bddnodes[n];

            if (node.hasRef()) {
                int hash;

                hash = NODEHASH2(VARpr(node), node.low, node.high);
                node.next = bddnodes[hash].hash;
                bddnodes[hash].hash = n;

            } else {
                node.low = -1;
                node.next = bddfreepos;
                bddfreepos = n;
                bddfreenum++;
            }
        }
    }

    void reorder_done() {
        int n;

        for (n = 0; n < extrootsize; n++)
            SETMARK(extroots[n]);
        for (n = 2; n < bddnodesize; n++) {
            if (MARKED(n))
                UNMARK(n);
            else
                bddnodes[n].clearRef();

            /* This is where we go from .var to .level again!
            * - Do NOT use the LEVEL macro here. */
            bddnodes[n].setLevelAndMarked(
                bddvar2level[bddnodes[n].getLevelAndMarked()]);
        }

        free(extroots);
        free(levels);
        imatrixDelete(iactmtx);
        bdd_gbc();
    }

    void imatrixDelete(imatrix mtx) {
        int n;

        for (n = 0; n < mtx.size; n++) {
            free(mtx.rows[n]);
            mtx.rows[n] = null;
        }
        free(mtx.rows);
        mtx.rows = null;
        free(mtx);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(org.sf.javabdd.BDD, boolean)
     */
    public void addVarBlock(BDD var, boolean fixed) {
        int x = ((bdd) var)._index;
        int[] set = var.scanSet();
        bdd_addvarblock(set, fixed);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(int, int, boolean)
     */
    public void addVarBlock(int first, int last, boolean fixed) {
        bdd_intaddvarblock(first, last, fixed);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varBlockAll()
     */
    public void varBlockAll() {
        bdd_varblockall();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearVarBlocks()
     */
    public void clearVarBlocks() {
        bdd_clrvarblocks();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printOrder()
     */
    public void printOrder() {
        bdd_fprintorder(System.out);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nodeCount(java.util.Collection)
     */
    public int nodeCount(Collection r) {
        int[] a = new int[r.size()];
        int j = 0;
        for (Iterator i = r.iterator(); i.hasNext();) {
            bdd b = (bdd) i.next();
            a[j++] = b._index;
        }
        return bdd_anodecount(a);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getAllocNum()
     */
    public int getAllocNum() {
        return bdd_getallocnum();
    }

    int bdd_getallocnum() {
        return bddnodesize;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getNodeNum()
     */
    public int getNodeNum() {
        return bdd_getnodenum();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderGain()
     */
    public int reorderGain() {
        return bdd_reorder_gain();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printStat()
     */
    public void printStat() {
        bdd_fprintstat(System.out);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makePair()
     */
    public BDDPairing makePair() {
        bddPair p = new bddPair();
        p.result = new int[bddvarnum];
        int n;
        for (n = 0; n < bddvarnum; n++)
            p.result[n] = bdd_ithvar(bddlevel2var[n]);

        p.id = update_pairsid();
        p.last = -1;

        bdd_register_pair(p);
        return p;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#swapVar(int, int)
     */
    public void swapVar(int v1, int v2) {
        bdd_swapvar(v1, v2);
    }

    int bdd_swapvar(int v1, int v2) {
        int l1, l2;

        /* Do not swap when variable-blocks are used */
        if (vartree != null)
            return bdd_error(BDD_VARBLK);

        /* Don't bother swapping x with x */
        if (v1 == v2)
            return 0;

        /* Make sure the variable exists */
        if (v1 < 0 || v1 >= bddvarnum || v2 < 0 || v2 >= bddvarnum)
            return bdd_error(BDD_VAR);

        l1 = bddvar2level[v1];
        l2 = bddvar2level[v2];

        /* Make sure v1 is before v2 */
        if (l1 > l2) {
            int tmp = v1;
            v1 = v2;
            v2 = tmp;
            l1 = bddvar2level[v1];
            l2 = bddvar2level[v2];
        }

        reorder_init();

        /* Move v1 to v2's position */
        while (bddvar2level[v1] < l2)
            reorder_vardown(v1);

        /* Move v2 to v1's position */
        while (bddvar2level[v2] > l1)
            reorder_varup(v2);

        reorder_done();

        return 0;
    }

    void bdd_fprintall(PrintStream out) {
        int n;

        for (n = 0; n < bddnodesize; n++) {
            if (LOW(n) != -1) {
                out.print(
                    "["
                        + right(n, 5)
                        + " - "
                        + right(bddnodes[n].getRef(), 2)
                        + "] ");
                // TODO: labelling of vars
                out.print(right(bddlevel2var[LEVEL(n)], 3));

                out.print(": " + right(LOW(n), 3));
                out.println(" " + right(HIGH(n), 3));
            }
        }
    }

    void bdd_fprinttable(PrintStream out, int r) {
        BddNode node;
        int n;

        out.println("ROOT: " + r);
        if (r < 2)
            return;

        bdd_mark(r);

        for (n = 0; n < bddnodesize; n++) {
            if (MARKED(n)) {
                node = bddnodes[n];
                node.unmark();

                out.print("[" + right(n, 5) + "] ");
                // TODO: labelling of vars
                out.print(right(bddlevel2var[node.getLevel()], 3));

                out.print(": " + right(node.low, 3));
                out.println(" " + right(node.high, 3));
            }
        }
    }

    int lh_nodenum;
    int lh_freepos;
    int[] loadvar2level;
    LoadHash[] lh_table;

    StringTokenizer tokenizer;

    String readNext(DataInput ifile) throws IOException {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            String s = ifile.readLine();
            if (s == null)
                bdd_error(BDD_FORMAT);
            tokenizer = new StringTokenizer(s);
        }
        return tokenizer.nextToken();
    }

    int bdd_load(DataInput ifile) throws IOException {
        int n, vnum, tmproot;
        int root;

        lh_nodenum = Integer.parseInt(readNext(ifile));
        vnum = Integer.parseInt(readNext(ifile));

        /* Check for constant true / false */
        if (lh_nodenum == 0 && vnum == 0) {
            root = Integer.parseInt(readNext(ifile));
            return root;
        }

        loadvar2level = new int[vnum];
        for (n = 0; n < vnum; n++) {
            loadvar2level[n] = Integer.parseInt(readNext(ifile));
        }

        if (vnum > bddvarnum)
            bdd_setvarnum(vnum);

        lh_table = new LoadHash[lh_nodenum];

        for (n = 0; n < lh_nodenum; n++) {
            lh_table[n] = new LoadHash();
            lh_table[n].first = -1;
            lh_table[n].next = n + 1;
        }
        lh_table[lh_nodenum - 1].next = -1;
        lh_freepos = 0;

        tmproot = bdd_loaddata(ifile);

        for (n = 0; n < lh_nodenum; n++)
            bdd_delref(lh_table[n].data);

        free(lh_table);
        lh_table = null;
        free(loadvar2level);
        loadvar2level = null;

        root = tmproot;
        return root;
    }

    static class LoadHash {
        int key;
        int data;
        int first;
        int next;
    }

    int bdd_loaddata(DataInput ifile) throws IOException {
        int key, var, low, high, root = 0, n;

        for (n = 0; n < lh_nodenum; n++) {
            key = Integer.parseInt(readNext(ifile));
            var = Integer.parseInt(readNext(ifile));
            low = Integer.parseInt(readNext(ifile));
            high = Integer.parseInt(readNext(ifile));

            if (low >= 2)
                low = loadhash_get(low);
            if (high >= 2)
                high = loadhash_get(high);

            if (low < 0 || high < 0 || var < 0)
                return bdd_error(BDD_FORMAT);

            root = bdd_addref(bdd_ite(bdd_ithvar(var), high, low));

            loadhash_add(key, root);
        }

        return root;
    }

    void loadhash_add(int key, int data) {
        int hash = key % lh_nodenum;
        int pos = lh_freepos;

        lh_freepos = lh_table[pos].next;
        lh_table[pos].next = lh_table[hash].first;
        lh_table[hash].first = pos;

        lh_table[pos].key = key;
        lh_table[pos].data = data;
    }

    int loadhash_get(int key) {
        int hash = lh_table[key % lh_nodenum].first;

        while (hash != -1 && lh_table[hash].key != key)
            hash = lh_table[hash].next;

        if (hash == -1)
            return -1;
        return lh_table[hash].data;
    }

    void bdd_save(DataOutput out, int r) throws IOException {
        int[] n = new int[1];

        if (r < 2) {
            out.writeBytes("0 0 " + r + "\n");
            return;
        }

        bdd_markcount(r, n);
        bdd_unmark(r);
        out.writeBytes(n[0] + " " + bddvarnum + "\n");

        for (int x = 0; x < bddvarnum; x++)
            out.writeBytes(bddvar2level[x] + " ");
        out.writeBytes("\n");

        bdd_save_rec(out, r);
        bdd_unmark(r);

        return;
    }

    void bdd_save_rec(DataOutput out, int root) throws IOException {
        BddNode node = bddnodes[root];
        int err;

        if (root < 2)
            return;

        if (node.isMarked())
            return;
        node.mark();

        bdd_save_rec(out, node.low);
        bdd_save_rec(out, node.high);

        out.writeBytes(root + " ");
        out.writeBytes(bddlevel2var[node.getLevel()] + " ");
        out.writeBytes(node.low + " ");
        out.writeBytes(node.high + "\n");

        return;
    }

    static String right(int x, int w) {
        return right(Integer.toString(x), w);
    }
    static String right(String s, int w) {
        int n = s.length();
        //if (w < n) return s.substring(n - w);
        StringBuffer b = new StringBuffer(w);
        for (int i = n; i < w; ++i) {
            b.append(' ');
        }
        b.append(s);
        return b.toString();
    }

    int bdd_addvarblock(int[] v, boolean fixed) {
        BddTree t;
        int first, last;

        if (v.length < 1)
            return bdd_error(BDD_VARBLK);

        first = last = v[0];

        for (int n = 0; n < v.length; n++) {
            if (v[n] < first)
                first = v[n];
            if (v[n] > last)
                last = v[n];
        }

        if ((t = bddtree_addrange(vartree, first, last, fixed, blockid))
            == null)
            return bdd_error(BDD_VARBLK);

        vartree = t;
        return blockid++;
    }

    int bdd_intaddvarblock(int first, int last, boolean fixed) {
        BddTree t;

        if (first < 0 || first >= bddvarnum || last < 0 || last >= bddvarnum)
            return bdd_error(BDD_VAR);

        if ((t = bddtree_addrange(vartree, first, last, fixed, blockid))
            == null)
            return bdd_error(BDD_VARBLK);

        vartree = t;
        return blockid++;
    }

    BddTree bddtree_addrange_rec(
        BddTree t,
        BddTree prev,
        int first,
        int last,
        boolean fixed,
        int id) {
        if (first < 0 || last < 0 || last < first)
            return null;

        /* Empty tree -> build one */
        if (t == null) {
            if ((t = bddtree_new(id)) == null)
                return null;
            t.first = first;
            t.fixed = fixed;
            t.seq = new int[last - first + 1];
            t.last = last;
            update_seq(t);
            t.prev = prev;
            return t;
        }

        /* Check for identity */
        if (first == t.first && last == t.last)
            return t;

        /* Before this section -> insert */
        if (last < t.first) {
            BddTree tnew = bddtree_new(id);
            if (tnew == null)
                return null;
            tnew.first = first;
            tnew.last = last;
            tnew.fixed = fixed;
            tnew.seq = new int[last - first + 1];
            update_seq(tnew);
            tnew.next = t;
            tnew.prev = t.prev;
            t.prev = tnew;
            return tnew;
        }

        /* After this this section -> go to next */
        if (first > t.last) {
            t.next = bddtree_addrange_rec(t.next, t, first, last, fixed, id);
            return t;
        }

        /* Inside this section -> insert in next level */
        if (first >= t.first && last <= t.last) {
            t.nextlevel =
                bddtree_addrange_rec(t.nextlevel, null, first, last, fixed, id);
            return t;
        }

        /* Covering this section -> insert above this level */
        if (first <= t.first) {
            BddTree tnew;
            BddTree dis = t;

            while (true) {
                /* Partial cover ->error */
                if (last >= dis.first && last < dis.last)
                    return null;

                if (dis.next == null || last < dis.next.first) {
                    tnew = bddtree_new(id);
                    if (tnew == null)
                        return null;
                    tnew.first = first;
                    tnew.last = last;
                    tnew.fixed = fixed;
                    tnew.seq = new int[last - first + 1];
                    update_seq(tnew);
                    tnew.nextlevel = t;
                    tnew.next = dis.next;
                    tnew.prev = t.prev;
                    if (dis.next != null)
                        dis.next.prev = tnew;
                    dis.next = null;
                    t.prev = null;
                    return tnew;
                }

                dis = dis.next;
            }

        }

        return null;
    }

    void update_seq(BddTree t) {
        int n;
        int low = t.first;

        for (n = t.first; n <= t.last; n++)
            if (bddvar2level[n] < bddvar2level[low])
                low = n;

        for (n = t.first; n <= t.last; n++)
            t.seq[bddvar2level[n] - bddvar2level[low]] = n;
    }

    BddTree bddtree_addrange(
        BddTree t,
        int first,
        int last,
        boolean fixed,
        int id) {
        return bddtree_addrange_rec(t, null, first, last, fixed, id);
    }

    void bdd_varblockall() {
        int n;

        for (n = 0; n < bddvarnum; n++)
            bdd_intaddvarblock(n, n, true);
    }

    void print_order_rec(PrintStream o, BddTree t, int level) {
        if (t == null)
            return;

        if (t.nextlevel != null) {
            for (int i = 0; i < level; ++i)
                o.print("   ");
            // todo: better reorder id printout
            o.print(right(t.id, 3));
            o.println("{\n");

            print_order_rec(o, t.nextlevel, level + 1);

            for (int i = 0; i < level; ++i)
                o.print("   ");
            // todo: better reorder id printout
            o.print(right(t.id, 3));
            o.println("}\n");

            print_order_rec(o, t.next, level);
        } else {
            for (int i = 0; i < level; ++i)
                o.print("   ");
            // todo: better reorder id printout
            o.println(right(t.id, 3));

            print_order_rec(o, t.next, level);
        }
    }

    void bdd_fprintorder(PrintStream ofile) {
        print_order_rec(ofile, vartree, 0);
    }

    void bdd_fprintstat(PrintStream out) {
        bddCacheStat s = bddcachestats;

        out.println();
        out.println("Cache statistics");
        out.println("----------------");

        out.println("Unique Access:  " + s.uniqueAccess);
        out.println("Unique Chain:   " + s.uniqueChain);
        out.println("Unique Hit:     " + s.uniqueHit);
        out.println("Unique Miss:    " + s.uniqueMiss);
        out.println(
            "=> Hit rate =   "
                + ((s.uniqueHit + s.uniqueMiss > 0)
                    ? ((float) s.uniqueHit)
                        / ((float) s.uniqueHit + s.uniqueMiss)
                    : 0));
        out.println("Operator Hits:  " + s.opHit);
        out.println("Operator Miss:  " + s.opMiss);
        out.println(
            "=> Hit rate =   "
                + ((s.opHit + s.opMiss > 0)
                    ? ((float) s.opHit) / ((float) s.opHit + s.opMiss)
                    : 0));
        out.println("Swap count =    " + s.swapCount);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createDomain(int, long)
     */
    protected BDDDomain createDomain(int a, long b) {
        return new bddDomain(a, b);
    }

    class bddDomain extends BDDDomain {

        bddDomain(int a, long b) {
            super(a, b);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#getFactory()
         */
        public BDDFactory getFactory() {
            return JavaFactory.this;
        }

    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createBitVector(int)
     */
    protected BDDBitVector createBitVector(int a) {
        return new bvec(a);
    }

    class bvec extends BDDBitVector {

        /**
         * @param bitnum
         */
        protected bvec(int bitnum) {
            super(bitnum);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDBitVector#getFactory()
         */
        public BDDFactory getFactory() {
            return JavaFactory.this;
        }

    }

    //// Prime stuff below.

    static Random rng = new Random();

    static final int Random(int i) {
        return rng.nextInt(i) + 1;
    }

    static boolean isEven(int src) {
        return (src & 0x1) == 0;
    }

    static boolean hasFactor(int src, int n) {
        return (src != n) && (src % n == 0);
    }

    static boolean BitIsSet(int src, int b) {
        return (src & (1 << b)) != 0;
    }

    static final int CHECKTIMES = 20;

    static final int u64_mulmod(int a, int b, int c) {
        return (int) (((long) a * (long) b) % (long) c);
    }

    /*************************************************************************
      Miller Rabin check
    *************************************************************************/

    static int numberOfBits(int src) {
        int b;

        if (src == 0)
            return 0;

        for (b = 31; b > 0; --b)
            if (BitIsSet(src, b))
                return b + 1;

        return 1;
    }

    static boolean isWitness(int witness, int src) {
        int bitNum = numberOfBits(src - 1) - 1;
        int d = 1;
        int i;

        for (i = bitNum; i >= 0; --i) {
            int x = d;

            d = u64_mulmod(d, d, src);

            if (d == 1 && x != 1 && x != src - 1)
                return true;

            if (BitIsSet(src - 1, i))
                d = u64_mulmod(d, witness, src);
        }

        return d != 1;
    }

    static boolean isMillerRabinPrime(int src) {
        int n;

        for (n = 0; n < CHECKTIMES; ++n) {
            int witness = Random(src - 1);

            if (isWitness(witness, src))
                return false;
        }

        return true;
    }

    /*************************************************************************
      Basic prime searching stuff
    *************************************************************************/

    static boolean hasEasyFactors(int src) {
        return hasFactor(src, 3)
            || hasFactor(src, 5)
            || hasFactor(src, 7)
            || hasFactor(src, 11)
            || hasFactor(src, 13);
    }

    static boolean isPrime(int src) {
        if (hasEasyFactors(src))
            return false;

        return isMillerRabinPrime(src);
    }

    /*************************************************************************
      External interface
    *************************************************************************/

    int bdd_prime_gte(int src) {
        if (isEven(src))
            ++src;

        while (!isPrime(src))
            src += 2;

        return src;
    }

    int bdd_prime_lte(int src) {
        if (isEven(src))
            --src;

        while (!isPrime(src))
            src -= 2;

        return src;
    }

    public static void main(String[] a) throws IOException {
        BDDFactory bdd = init(1000, 1000);
        BDD b = bdd.load(a[0]);
        b.printSet();

        bdd.printTable(b);

        bdd.save("testfile2", b);
        
        bdd.printStat();
    }
}