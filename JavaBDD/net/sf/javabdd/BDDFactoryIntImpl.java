// BDDFactoryIntImpl.java, created Jul 16, 2006 2:59:55 PM by jwhaley
// Copyright (C) 2004-2006 John Whaley <jwhaley@alum.mit.edu>
// Licensed under the terms of the GNU LGPL; see COPYING for details.
package net.sf.javabdd;

import java.util.Collection;
import java.util.Iterator;

/**
 * A shared superclass for BDD factories that refer to BDDs as ints.
 * 
 * @author jwhaley
 * @version $Id$
 */
public abstract class BDDFactoryIntImpl extends BDDFactory {
    
    static final boolean USE_FINALIZER = false;
    
    protected abstract void addref_impl(/*bdd*/int v);
    protected abstract void delref_impl(/*bdd*/int v);
    protected abstract /*bdd*/int zero_impl();
    protected abstract /*bdd*/int one_impl();
    protected abstract /*bdd*/int invalid_bdd_impl();
    protected abstract int var_impl(/*bdd*/int v);
    protected abstract int level_impl(/*bdd*/int v);
    protected abstract /*bdd*/int low_impl(/*bdd*/int v);
    protected abstract /*bdd*/int high_impl(/*bdd*/int v);
    protected abstract /*bdd*/int ithVar_impl(int var);
    protected abstract /*bdd*/int nithVar_impl(int var);
    
    protected abstract /*bdd*/int ite_impl(/*bdd*/int v1, /*bdd*/int v2, /*bdd*/int v3);
    protected abstract /*bdd*/int apply_impl(/*bdd*/int v1, /*bdd*/int v2, BDDOp opr);
    protected abstract /*bdd*/int not_impl(/*bdd*/int v1);
    protected abstract /*bdd*/int applyAll_impl(/*bdd*/int v1, /*bdd*/int v2, BDDOp opr, /*bdd*/int v3);
    protected abstract /*bdd*/int applyEx_impl(/*bdd*/int v1, /*bdd*/int v2, BDDOp opr, /*bdd*/int v3);
    protected abstract /*bdd*/int applyUni_impl(/*bdd*/int v1, /*bdd*/int v2, BDDOp opr, /*bdd*/int v3);
    protected abstract /*bdd*/int compose_impl(/*bdd*/int v1, /*bdd*/int v2, int var);
    protected abstract /*bdd*/int constrain_impl(/*bdd*/int v1, /*bdd*/int v2);
    protected abstract /*bdd*/int restrict_impl(/*bdd*/int v1, /*bdd*/int v2);
    protected abstract /*bdd*/int simplify_impl(/*bdd*/int v1, /*bdd*/int v2);
    protected abstract /*bdd*/int support_impl(/*bdd*/int v);
    protected abstract /*bdd*/int exist_impl(/*bdd*/int v1, /*bdd*/int v2);
    protected abstract /*bdd*/int forAll_impl(/*bdd*/int v1, /*bdd*/int v2);
    protected abstract /*bdd*/int unique_impl(/*bdd*/int v1, /*bdd*/int v2);
    protected abstract /*bdd*/int fullSatOne_impl(/*bdd*/int v);
    
    protected abstract /*bdd*/int replace_impl(/*bdd*/int v, BDDPairing p);
    protected abstract /*bdd*/int veccompose_impl(/*bdd*/int v, BDDPairing p);
    
    protected abstract int nodeCount_impl(/*bdd*/int v);
    protected abstract double pathCount_impl(/*bdd*/int v);
    protected abstract double satCount_impl(/*bdd*/int v);
    protected abstract /*bdd*/int satOne_impl(/*bdd*/int v);
    protected abstract /*bdd*/int satOne_impl2(/*bdd*/int v1, /*bdd*/int v2, boolean pol);
    protected abstract int nodeCount_impl2(/*bdd*/int[] v);
    protected abstract int[] varProfile_impl(/*bdd*/int v);
    protected abstract void printTable_impl(/*bdd*/int v);
    
    public class IntBDD extends BDD {
        protected /*bdd*/int v;
        protected IntBDD(/*bdd*/int v) {
            this.v = v;
            addref_impl(v);
        }
        public BDD apply(BDD that, BDDOp opr) {
            return makeBDD(apply_impl(v, unwrap(that), opr));
        }
        public BDD applyAll(BDD that, BDDOp opr, BDDVarSet var) {
            return makeBDD(applyAll_impl(v, unwrap(that), opr, unwrap(var)));
        }
        public BDD applyEx(BDD that, BDDOp opr, BDDVarSet var) {
            return makeBDD(applyEx_impl(v, unwrap(that), opr, unwrap(var)));
        }
        public BDD applyUni(BDD that, BDDOp opr, BDDVarSet var) {
            return makeBDD(applyUni_impl(v, unwrap(that), opr, unwrap(var)));
        }
        public BDD applyWith(BDD that, BDDOp opr) {
            /*bdd*/int v2 = unwrap(that);
            /*bdd*/int v3 = apply_impl(v, v2, opr);
            addref_impl(v3);
            delref_impl(v);
            if (this != that)
                that.free();
            v = v3;
            return this;
        }
        public BDD compose(BDD g, int var) {
            return makeBDD(compose_impl(v, unwrap(g), var));
        }
        public BDD constrain(BDD that) {
            return makeBDD(constrain_impl(v, unwrap(that)));
        }
        public boolean equals(BDD that) {
            return v == unwrap(that);
        }
        public BDD exist(BDDVarSet var) {
            return makeBDD(exist_impl(v, unwrap(var)));
        }
        public BDD forAll(BDDVarSet var) {
            return makeBDD(forAll_impl(v, unwrap(var)));
        }
        public void free() {
            delref_impl(v);
            v = invalid_bdd_impl();
        }
        public BDD fullSatOne() {
            return makeBDD(fullSatOne_impl(v));
        }
        public BDDFactory getFactory() {
            return BDDFactoryIntImpl.this;
        }
        public int hashCode() {
            return v;
        }
        public BDD high() {
            return makeBDD(high_impl(v));
        }
        public BDD id() {
            return makeBDD(v);
        }
        public boolean isOne() {
            return v == one_impl();
        }
        public boolean isZero() {
            return v == zero_impl();
        }
        public BDD ite(BDD thenBDD, BDD elseBDD) {
            return makeBDD(ite_impl(v, unwrap(thenBDD), unwrap(elseBDD)));
        }
        public BDD low() {
            return makeBDD(low_impl(v));
        }
        public int level() {
            return level_impl(v);
        }
        public int nodeCount() {
            return nodeCount_impl(v);
        }
        public BDD not() {
            return makeBDD(not_impl(v));
        }
        public double pathCount() {
            return pathCount_impl(v);
        }
        public BDD replace(BDDPairing pair) {
            return makeBDD(replace_impl(v, pair));
        }
        public BDD replaceWith(BDDPairing pair) {
            /*bdd*/int v3 = replace_impl(v, pair);
            addref_impl(v3);
            delref_impl(v);
            v = v3;
            return this;
        }
        public BDD restrict(BDD var) {
            return makeBDD(restrict_impl(v, unwrap(var)));
        }
        public BDD restrictWith(BDD that) {
            /*bdd*/int v2 = unwrap(that);
            /*bdd*/int v3 = restrict_impl(v, v2);
            addref_impl(v3);
            delref_impl(v);
            if (this != that)
                that.free();
            v = v3;
            return this;
        }
        public double satCount() {
            return satCount_impl(v);
        }
        public BDD satOne() {
            return makeBDD(satOne_impl(v));
        }
        public BDD satOne(BDDVarSet var, boolean pol) {
            return makeBDD(satOne_impl2(v, unwrap(var), pol));
        }
        public BDD simplify(BDDVarSet d) {
            return makeBDD(simplify_impl(v, unwrap(d)));
        }
        public BDDVarSet support() {
            return makeBDDVarSet(support_impl(v));
        }
        public BDD unique(BDDVarSet var) {
            return makeBDD(unique_impl(v, unwrap(var)));
        }
        public int var() {
            return var_impl(v);
        }
        public int[] varProfile() {
            return varProfile_impl(v);
        }
        public BDD veccompose(BDDPairing pair) {
            return makeBDD(veccompose_impl(v, pair));
        }
        public BDDVarSet toVarSet() {
            return makeBDDVarSet(v);
        }
    }
    
    public class IntBDDWithFinalizer extends IntBDD {
        protected IntBDDWithFinalizer(/*bdd*/int v) {
            super(v);
        }
        
        protected void finalize() throws Throwable {
            super.finalize();
            if (USE_FINALIZER) {
                if (false && v != invalid_bdd_impl()) {
                    System.out.println("BDD not freed! "+System.identityHashCode(this));
                }
                deferredFree(v);
            }
        }
        
    }
    
    protected IntBDD makeBDD(/*bdd*/int v) {
        if (USE_FINALIZER)
            return new IntBDDWithFinalizer(v);
        else
            return new IntBDD(v);
    }
    
    protected static final /*bdd*/int unwrap(BDD b) {
        return ((IntBDD) b).v;
    }
    
    protected static final /*bdd*/int[] unwrap(Collection/*<BDD>*/ c) {
        /*bdd*/int[] result = new /*bdd*/int[c.size()];
        int k = -1;
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            result[++k] = ((IntBDD) i.next()).v;
        }
        return result;
    }
    
    public class IntBDDVarSet extends BDDVarSet {
        /*bdd*/int v;
        private IntBDDVarSet(/*bdd*/int v) {
            this.v = v;
            addref_impl(v);
        }
        public boolean equals(BDDVarSet that) {
            return v == unwrap(that);
        }
        public void free() {
            delref_impl(v);
            v = invalid_bdd_impl();
        }
        public BDDFactory getFactory() {
            return BDDFactoryIntImpl.this;
        }
        public int hashCode() {
            return v;
        }
        public BDDVarSet id() {
            return makeBDDVarSet(v);
        }
        public BDDVarSet intersect(BDDVarSet b) {
            return makeBDDVarSet(apply_impl(v, unwrap(b), or));
        }
        public BDDVarSet intersectWith(BDDVarSet b) {
            /*bdd*/int v2 = unwrap(b);
            /*bdd*/int v3 = apply_impl(v, v2, or);
            addref_impl(v3);
            delref_impl(v);
            if (this != b)
                b.free();
            v = v3;
            return this;
        }
        public boolean isEmpty() {
            return v == one_impl();
        }
        public int size() {
            int result = 0;
            for (/*bdd*/int p = v; p != one_impl(); p = high_impl(p)) {
                if (p == zero_impl())
                    throw new BDDException("varset contains zero");
                ++result;
            }
            return result;
        }
        public int[] toArray() {
            int[] result = new int[size()];
            int k = -1;
            for (/*bdd*/int p = v; p != one_impl(); p = high_impl(p)) {
                result[++k] = var_impl(p);
            }
            return result;
        }
        public BDD toBDD() {
            return makeBDD(v);
        }
        public int[] toLevelArray() {
            int[] result = new int[size()];
            int k = -1;
            for (int p = v; p != one_impl(); p = high_impl(p)) {
                result[++k] = level_impl(p);
            }
            return result;
        }
        public BDDVarSet union(BDDVarSet b) {
            return makeBDDVarSet(apply_impl(v, unwrap(b), and));
        }
        public BDDVarSet union(int var) {
            /*bdd*/int v2 = ithVar_impl(var);
            /*bdd*/int v3 = apply_impl(v, v2, and);
            delref_impl(v2);
            return makeBDDVarSet(v3);
        }
        public BDDVarSet unionWith(BDDVarSet b) {
            /*bdd*/int v2 = unwrap(b);
            /*bdd*/int v3 = apply_impl(v, v2, and);
            addref_impl(v3);
            delref_impl(v);
            if (this != b)
                b.free();
            v = v3;
            return this;
        }
        public BDDVarSet unionWith(int var) {
            /*bdd*/int v2 = ithVar_impl(var);
            /*bdd*/int v3 = apply_impl(v, v2, and);
            addref_impl(v3);
            delref_impl(v);
            delref_impl(v2);
            v = v3;
            return this;
        }
    }
    
    public class IntBDDVarSetWithFinalizer extends IntBDDVarSet {
        
        protected IntBDDVarSetWithFinalizer(int v) {
            super(v);
        }
        
        protected void finalize() throws Throwable {
            super.finalize();
            if (USE_FINALIZER) {
                if (false && v != invalid_bdd_impl()) {
                    System.out.println("BDD not freed! "+System.identityHashCode(this));
                }
                deferredFree(v);
            }
        }
        
    }
    
    protected IntBDDVarSet makeBDDVarSet(/*bdd*/int v) {
        if (USE_FINALIZER)
            return new IntBDDVarSetWithFinalizer(v);
        else
            return new IntBDDVarSet(v);
    }
    
    protected static final /*bdd*/int unwrap(BDDVarSet b) {
        return ((IntBDDVarSet) b).v;
    }
    
    public class IntBDDBitVector extends BDDBitVector {
        
        protected IntBDDBitVector(int bitnum) {
            super(bitnum);
        }

        public BDDFactory getFactory() {
            return BDDFactoryIntImpl.this;
        }
        
    }
    
    public BDD ithVar(/*bdd*/int var) {
        return makeBDD(ithVar_impl(var));
    }

    public BDD nithVar(/*bdd*/int var) {
        return makeBDD(nithVar_impl(var));
    }

    public int nodeCount(Collection/*<BDD>*/ r) {
        return nodeCount_impl2(unwrap(r));
    }

    public BDD one() {
        return makeBDD(one_impl());
    }

    public BDDVarSet emptySet() {
        return makeBDDVarSet(one_impl());
    }
    
    public void printTable(BDD b) {
        printTable_impl(unwrap(b));
    }

    public BDD zero() {
        return makeBDD(zero_impl());
    }
    
    public void done() {
        if (USE_FINALIZER) {
            System.gc();
            System.runFinalization();
            handleDeferredFree();
        }
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        this.done();
    }
    
    protected /*bdd*/int[] to_free = new /*bdd*/int[8];
    protected /*bdd*/int to_free_length = 0;
    public void deferredFree(int v) {
        synchronized(to_free) {
            if (to_free_length == to_free.length) {
                /*bdd*/int[] t = new /*bdd*/int[to_free.length * 2];
                System.arraycopy(to_free, 0, t, 0, to_free.length);
                to_free = t;
            }
            to_free[to_free_length++] = v;
        }
    }
    public void handleDeferredFree() {
        synchronized(to_free) {
            while (to_free_length > 0) {
                delref_impl(to_free[--to_free_length]);
            }
        }
    }
}
