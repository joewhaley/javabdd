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
    
    protected abstract void addref_impl(int v);
    protected abstract void delref_impl(int v);
    protected abstract int zero_impl();
    protected abstract int one_impl();
    protected abstract int invalid_bdd_impl();
    protected abstract int var_impl(int v);
    protected abstract int level_impl(int v);
    protected abstract int low_impl(int v);
    protected abstract int high_impl(int v);
    protected abstract int ithVar_impl(int var);
    protected abstract int nithVar_impl(int var);
    
    protected abstract int ite_impl(int v1, int v2, int v3);
    protected abstract int apply_impl(int v1, int v2, BDDOp opr);
    protected abstract int not_impl(int v1);
    protected abstract int applyAll_impl(int v1, int v2, BDDOp opr, int v3);
    protected abstract int applyEx_impl(int v1, int v2, BDDOp opr, int v3);
    protected abstract int applyUni_impl(int v1, int v2, BDDOp opr, int v3);
    protected abstract int compose_impl(int v1, int v2, int var);
    protected abstract int constrain_impl(int v1, int v2);
    protected abstract int restrict_impl(int v1, int v2);
    protected abstract int simplify_impl(int v1, int v2);
    protected abstract int support_impl(int v);
    protected abstract int exist_impl(int v1, int v2);
    protected abstract int forAll_impl(int v1, int v2);
    protected abstract int unique_impl(int v1, int v2);
    protected abstract int fullSatOne_impl(int v);
    
    protected abstract int replace_impl(int v, BDDPairing p);
    protected abstract int veccompose_impl(int v, BDDPairing p);
    
    protected abstract int nodeCount_impl(int v);
    protected abstract double pathCount_impl(int v);
    protected abstract double satCount_impl(int v);
    protected abstract int satOne_impl(int v);
    protected abstract int satOne_impl2(int v1, int v2, boolean pol);
    protected abstract int nodeCount_impl2(int[] v);
    protected abstract int[] varProfile_impl(int v);
    protected abstract void printTable_impl(int v);
    
    public class IntBDD extends BDD {
        protected int v;
        protected IntBDD(int v) {
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
            int v2 = unwrap(that);
            int v3 = apply_impl(v, v2, opr);
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
            int v3 = replace_impl(v, pair);
            addref_impl(v3);
            delref_impl(v);
            v = v3;
            return this;
        }
        public BDD restrict(BDD var) {
            return makeBDD(restrict_impl(v, unwrap(var)));
        }
        public BDD restrictWith(BDD that) {
            int v2 = unwrap(that);
            int v3 = restrict_impl(v, v2);
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
        protected IntBDDWithFinalizer(int v) {
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
    
    protected IntBDD makeBDD(int v) {
        if (USE_FINALIZER)
            return new IntBDDWithFinalizer(v);
        else
            return new IntBDD(v);
    }
    
    protected static final int unwrap(BDD b) {
        return ((IntBDD) b).v;
    }
    
    protected static final int[] unwrap(Collection/*<BDD>*/ c) {
        int[] result = new int[c.size()];
        int k = -1;
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            result[++k] = ((IntBDD) i.next()).v;
        }
        return result;
    }
    
    public class IntBDDVarSet extends BDDVarSet {
        int v;
        private IntBDDVarSet(int v) {
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
            int v2 = unwrap(b);
            int v3 = apply_impl(v, v2, or);
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
            for (int p = v; p != one_impl(); p = high_impl(p)) {
                if (p == zero_impl())
                    throw new BDDException("varset contains zero");
                ++result;
            }
            return result;
        }
        public int[] toArray() {
            int[] result = new int[size()];
            int k = -1;
            for (int p = v; p != one_impl(); p = high_impl(p)) {
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
            int v2 = ithVar_impl(var);
            int v3 = apply_impl(v, v2, and);
            delref_impl(v2);
            return makeBDDVarSet(v3);
        }
        public BDDVarSet unionWith(BDDVarSet b) {
            int v2 = unwrap(b);
            int v3 = apply_impl(v, v2, and);
            addref_impl(v3);
            delref_impl(v);
            if (this != b)
                b.free();
            v = v3;
            return this;
        }
        public BDDVarSet unionWith(int var) {
            int v2 = ithVar_impl(var);
            int v3 = apply_impl(v, v2, and);
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
    
    protected IntBDDVarSet makeBDDVarSet(int v) {
        if (USE_FINALIZER)
            return new IntBDDVarSetWithFinalizer(v);
        else
            return new IntBDDVarSet(v);
    }
    
    protected static final int unwrap(BDDVarSet b) {
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
    
    public BDD ithVar(int var) {
        return makeBDD(ithVar_impl(var));
    }

    public BDD nithVar(int var) {
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
    
    protected int[] to_free = new int[8];
    protected int to_free_length = 0;
    public void deferredFree(int v) {
        synchronized(to_free) {
            if (to_free_length == to_free.length) {
                int[] t = new int[to_free.length * 2];
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
