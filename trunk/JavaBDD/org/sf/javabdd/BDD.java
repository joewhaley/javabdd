package org.sf.javabdd;

import java.util.List;

/**
 * Binary Decision Diagrams (BDDs) are used for efficient computation of many
 * common problems. This is done by giving a compact representation and a set
 * efficient operations on boolean functions f: {0,1}^n --> {0,1}.
 * 
 * Use an implementation of BDDFactory to create BDD objects.
 * 
 * @see org.sf.javabdd.BDDFactory
 * 
 * @author John Whaley
 * @version $Id$
 */
public abstract class BDD {

    /**
     * Returns the factory that created this BDD.
     * 
     * @return factory that created this BDD
     */
    public abstract BDDFactory getFactory();

    /**
     * Returns true if this BDD is the zero (false) BDD.
     * 
     * @return true if this BDD is the zero (false) BDD
     */
    public abstract boolean isZero();
    
    /**
     * Returns true if this BDD is the one (true) BDD.
     * 
     * @return true if this BDD is the one (true) BDD
     */
    public abstract boolean isOne();
    
    /**
     * Gets the variable labeling the BDD.
     * 
     * Compare to bdd_var.
     * 
     * @return the index of the variable labeling the BDD
     */
    public abstract int var();

    /**
     * Gets the level of this BDD.
     * 
     * Compare to LEVEL() macro.
     */
    public int level() {
        return getFactory().var2Level(var());
    }

    /**
     * Gets the true branch of this BDD.
     * 
     * Compare to bdd_high.
     * 
     * @return true branch of this BDD
     */
    public abstract BDD high();

    /**
     * Gets the false branch of this BDD.
     * 
     * Compare to bdd_low.
     * 
     * @return false branch of this BDD
     */
    public abstract BDD low();

    /**
     * Identity function.  Returns a copy of this BDD.  Use as the argument to
     * the "xxxWith" style operators when you do not want to have the argument
     * consumed.
     * 
     * Compare to bdd_addref.
     * 
     * @return copy of this BDD
     */
    public abstract BDD id();

    /**
     * Negates this BDD by exchanging all references to the zero-terminal with
     * references to the one-terminal and vice-versa.
     * 
     * Compare to bdd_not.
     * 
     * @return the negated BDD
     */
    public abstract BDD not();

    /**
     * Returns the logical 'and' of two BDDs.  This is a shortcut for calling
     * "apply" with the "and" operator.
     * 
     * Compare to bdd_and.
     * 
     * @return the logical 'and' of two BDDs
     */
    public BDD and(BDD that) {
        return this.apply(that, BDDFactory.and);
    }

    /**
     * Makes this BDD be the logical 'and' of two BDDs.  The "that" BDD is
     * consumed, and can no longer be used.  This is a shortcut for calling
     * "applyWith" with the "and" operator.
     * 
     * Compare to bdd_and and bdd_delref.
     */
    public void andWith(BDD that) {
        this.applyWith(that, BDDFactory.and);
    }

    /**
     * Returns the logical 'or' of two BDDs.  This is a shortcut for calling
     * "apply" with the "or" operator.
     * 
     * Compare to bdd_or.
     * 
     * @return the logical 'or' of two BDDs
     */
    public BDD or(BDD that) {
        return this.apply(that, BDDFactory.or);
    }

    /**
     * Makes this BDD be the logical 'or' of two BDDs.  The "that" BDD is
     * consumed, and can no longer be used.  This is a shortcut for calling
     * "applyWith" with the "or" operator.
     * 
     * Compare to bdd_or and bdd_delref.
     */
    public void orWith(BDD that) {
        this.applyWith(that, BDDFactory.or);
    }

    /**
     * Returns the logical 'xor' of two BDDs.  This is a shortcut for calling
     * "apply" with the "xor" operator.
     * 
     * Compare to bdd_xor.
     * 
     * @return the logical 'xor' of two BDDs
     */
    public BDD xor(BDD that) {
        return this.apply(that, BDDFactory.xor);
    }
    
    /**
     * Makes this BDD be the logical 'xor' of two BDDs.  The "that" BDD is
     * consumed, and can no longer be used.  This is a shortcut for calling
     * "applyWith" with the "xor" operator.
     * 
     * Compare to bdd_xor and bdd_delref.
     */
    public void xorWith(BDD that) {
        this.applyWith(that, BDDFactory.xor);
    }

    /**
     * Returns the logical 'implication' of two BDDs.  This is a shortcut for
     * calling "apply" with the "imp" operator.
     * 
     * Compare to bdd_imp.
     * 
     * @return the logical 'implication' of two BDDs
     */
    public BDD imp(BDD that) {
        return this.apply(that, BDDFactory.imp);
    }
    
    /**
     * Makes this BDD be the logical 'implication' of two BDDs.  The "that" BDD
     * is consumed, and can no longer be used.  This is a shortcut for calling
     * "applyWith" with the "imp" operator.
     * 
     * Compare to bdd_imp and bdd_delref.
     */
    public void impWith(BDD that) {
        this.applyWith(that, BDDFactory.imp);
    }

    /**
     * Returns the logical 'bi-implication' of two BDDs.  This is a shortcut for
     * calling "apply" with the "biimp" operator.
     * 
     * Compare to bdd_biimp.
     * 
     * @return the logical 'bi-implication' of two BDDs
     */
    public BDD biimp(BDD that) {
        return this.apply(that, BDDFactory.biimp);
    }
    
    /**
     * Makes this BDD be the logical 'bi-implication' of two BDDs.  The "that"
     * BDD is consumed, and can no longer be used.  This is a shortcut for
     * calling "applyWith" with the "biimp" operator.
     * 
     * Compare to bdd_biimp and bdd_delref.
     */
    public void biimpWith(BDD that) {
        this.applyWith(that, BDDFactory.biimp);
    }

    /**
     * if-then-else operator.
     * 
     * Compare to bdd_ite.
     * 
     * @return the result of the if-then-else operator on the three BDDs
     */
    public abstract BDD ite(BDD thenBDD, BDD elseBDD);
    
    /**
     * Relational product.  Calculates the relational product of the two BDDs as
     * this AND that with the variables in var quantified out afterwards.
     * Identical to applyEx(that, and, var).
     * 
     * Compare to bdd_relprod.
     * 
     * @return the result of the relational product
     */
    public abstract BDD relprod(BDD that, BDD var);
    
    /**
     * Functional composition.  Substitutes the variable var with the BDD that
     * in this BDD: result = f[g/var].
     * 
     * Compare to bdd_compose.
     * 
     * @return the result of the functional composition
     */
    public abstract BDD compose(BDD that, int var);

    /**
     * Simultaneous functional composition.  Uses the pairs of variables and
     * BDDs in pair to make the simultaneous substitution: f [g1/V1, ... gn/Vn].
     * In this way one or more BDDs may be substituted in one step. The BDDs in
     * pair may depend on the variables they are substituting.  BDD.compose()
     * may be used instead of BDD.replace() but is not as efficient when gi is a
     * single variable, the same applies to BDD.restrict().  Note that
     * simultaneous substitution is not necessarily the same as repeated
     * substitution.
     * 
     * Compare to bdd_veccompose.
     * 
     * @param pair
     * @return BDD
     */
    public abstract BDD veccompose(BDDPairing pair);

    /**
     * Generalized cofactor.  Computes the generalized cofactor of this BDD with
     * respect to the given BDD.
     * 
     * Compare to bdd_constrain.
     * 
     * @return the result of the generalized cofactor
     */
    public abstract BDD constrain(BDD that);

    /**
     * Existential quantification of variables.  Removes all occurrences of this
     * BDD in variables in the set var by existential quantification.
     * 
     * Compare to bdd_exist.
     * 
     * @return the result of the existential quantification
     */
    public abstract BDD exist(BDD var);

    /**
     * Universal quantification of variables.  Removes all occurrences of this
     * BDD in variables in the set var by universal quantification.
     * 
     * Compare to bdd_forall.
     * 
     * @return the result of the universal quantification
     */
    public abstract BDD forAll(BDD var);

    /**
     * Unique quantification of variables.  This type of quantification uses a
     * XOR operator instead of an OR operator as in the existential
     * quantification.
     * 
     * Compare to bdd_unique.
     * 
     * @return the result of the unique quantification
     */
    public abstract BDD unique(BDD var);
    
    /**
     * Restrict a set of variables to constant values.  Restricts the variables
     * in this BDD to constant true if they are included in their positive form
     * in var, and constant false if they are included in their negative form.
     * 
     * Compare to bdd_restrict.
     * 
     * @return the result of the restrict operation
     */
    public abstract BDD restrict(BDD var);

    /**
     * Coudert and Madre's restrict function.  Tries to simplify the BDD f by
     * restricting it to the domain covered by d.  No checks are done to see if
     * the result is actually smaller than the input.  This can be done by the
     * user with a call to nodeCount().
     * 
     * Compare to bdd_simplify.
     * 
     * @return the result of the simplify operation
     */
    public abstract BDD simplify(BDD d);

    /**
     * Returns the variable support of this BDD.  The support is all the
     * variables that this BDD depends on.
     * 
     * Compare to bdd_support.
     * 
     * @return the variable support of this BDD
     */
    public abstract BDD support();

    /**
     * Returns the result of applying the binary operator opr to the two BDDs.
     * 
     * Compare to bdd_apply.
     * 
     * @return the result of applying the operator
     */
    public abstract BDD apply(BDD that, BDDFactory.BDDOp opr);

    /**
     * Makes this BDD be the result of the binary operator opr of two BDDs.  The
     * "that" BDD is consumed, and can no longer be used.  Attempting to use the
     * passed in BDD again will result in an exception being thrown.
     * 
     * Compare to bdd_apply and bdd_delref.
     */
    public abstract void applyWith(BDD that, BDDFactory.BDDOp opr);
    
    /**
     * Applies the binary operator opr to two BDDs and then performs a universal
     * quantification of the variables from the variable set var.
     * 
     * Compare to bdd_appall.
     * 
     * @return the result
     */
    public abstract BDD applyAll(BDD that, BDDFactory.BDDOp opr, BDD var);

    /**
     * Applies the binary operator opr to two BDDs and then performs an
     * existential quantification of the variables from the variable set var.
     * 
     * Compare to bdd_appex.
     * 
     * @return the result
     */
    public abstract BDD applyEx(BDD that, BDDFactory.BDDOp opr, BDD var);

    /**
     * Applies the binary operator opr to two BDDs and then performs a unique
     * quantification of the variables from the variable set var.
     * 
     * Compare to bdd_appuni.
     * 
     * @return the result
     */
    public abstract BDD applyUni(BDD that, BDDFactory.BDDOp opr, BDD var);

    /**
     * Finds one satisfying variable assignment.  Finds a BDD with at most one
     * variable at each levels.  The new BDD implies this BDD and is not false
     * unless this BDD is false.
     * 
     * Compare to bdd_satone.
     * 
     * @return one satisfying variable assignment
     */
    public abstract BDD satOne();

    /**
     * Finds one satisfying variable assignment.  Finds a BDD with exactly one
     * variable at all levels.  The new BDD implies this BDD and is not false
     * unless this BDD is false.
     * 
     * Compare to bdd_fullsatone.
     * 
     * @return one satisfying variable assignment
     */
    public abstract BDD fullSatOne();

    /**
     * Finds one satisfying variable assignment.  Finds a minterm in this BDD.
     * The var argument is a set of variables that must be mentioned in the
     * result.  The polarity of these variables in the result -- in case they
     * are undefined in this BDD, are defined by the pol parameter.  If pol is
     * the false BDD then all variables will be in negative form, and otherwise
     * they will be in positive form.
     * 
     * Compare to bdd_satoneset.
     * 
     * @return one satisfying variable assignment
     */
    public abstract BDD satOneSet(BDD var, BDD pol);

    /**
     * Finds all satisfying variable assignments.
     * 
     * Compare to bdd_allsat.
     * 
     * @return all satisfying variable assignments
     */
    public abstract List allsat();

    /**
     * Scans this BDD to find all occurrences of FDD variables and returns an
     * array that contains the indices of the possible found FDD variables.
     * 
     * Compare to bdd_scanset.
     * 
     * @return int[]
     */
    public int[] scanSet() {
        if (isOne() || isZero()) {
            return null;
        }
        
        int num = 0;
        for (BDD n = this; !n.isZero() && !n.isOne() ; n = n.high())
            num++;

        int[] varset = new int[num];
   
        num = 0;
        for (BDD n = this; !n.isZero() && !n.isOne() ; n = n.high())
            varset[num++] = n.var();
        
        return varset;
    }

    /**
     * Scans this BDD and copies the stored variables into an integer array of
     * variable numbers.  The numbers returned are guaranteed to be in
     * ascending order.
     * 
     * Compare to fdd_scanset.
     * 
     * @return int[]
     */
    public int[] scanSetDomains() {
        int[] fv;
        int[] varset;
        int fn;
        int num, n, m, i;

        fv = this.scanSet();
        if (fv == null)
            return null;
        fn = fv.length;

        BDDFactory factory = getFactory();

        for (n = 0, num = 0; n < factory.numberOfDomains(); n++) {
            BDDDomain dom = factory.getDomain(n);
            int[] ivar = dom.vars();
            boolean found = false;
            for (m = 0; m < dom.varNum() && !found; m++) {
                for (i = 0; i < fn && !found; i++) {
                    if (ivar[m] == fv[i]) {
                        num++;
                        found = true;
                    }
                }
            }
        }

        varset = new int[num];

        for (n = 0, num = 0; n < factory.numberOfDomains(); n++) {
            BDDDomain dom = factory.getDomain(n);
            int[] ivar = dom.vars();
            boolean found = false;
            for (m = 0; m < dom.varNum() && !found; m++) {
                for (i = 0; i < fn && !found; i++) {
                    if (ivar[m] == fv[i]) {
                        varset[num++] = n;
                        found = true;
                    }
                }
            }
        }

        return varset;
    }
    
    /**
     * Finds one satisfying assignment of the domain d in this BDD and returns
     * that value.
     * 
     * Compare to fdd_scanvar.
     * 
     * @param d
     * @return int
     */
    public int scanVar(BDDDomain d) {
        if (this.isZero())
           return -1;
        int[] allvar = this.scanAllVar();
        int res = allvar[d.getIndex()];
        return res;
    }
    
    /**
     * Finds one satisfying assignment in this BDD of all the defined FDD
     * variables.  Each value is stored in an array which is returned.  The size
     * of this array is exactly the number of FDD variables defined.
     * 
     * Compare to fdd_scanallvar.
     * 
     * @return int[]
     */
    public int[] scanAllVar() {
        int n;
        boolean[] store;
        int[] res;
        BDD p = this;

        if (this.isZero())
            return null;

        BDDFactory factory = getFactory();

        int bddvarnum = factory.varNum();
        store = new boolean[bddvarnum];

        while (!p.isOne() && !p.isZero()) {
            if (!p.low().isZero()) {
                store[p.var()] = false;
                p = p.low();
            } else {
                store[p.var()] = true;
                p = p.high();
            }
        }

        int fdvarnum = factory.numberOfDomains();
        res = new int[fdvarnum];

        for (n = 0; n < fdvarnum; n++) {
            BDDDomain dom = factory.getDomain(n);
            int[] ivar = dom.vars();

            int val = 0;
            for (int m = dom.varNum() - 1; m >= 0; m--)
                if (store[ivar[m]])
                    val = val * 2 + 1;
                else
                    val = val * 2;

            res[n] = val;
        }

        return res;
    }

    /**
     * Replaces all variables in this BDD with the variables defined by pair.
     * Each entry in pair consists of a old and a new variable.  Whenever the
     * old variable is found in this BDD then a new node with the new variable
     * is inserted instead.
     * 
     * Compare to bdd_replace.
     * 
     * @param pair
     * @return BDD
     */
    public abstract BDD replace(BDDPairing pair);
    public abstract void replaceWith(BDDPairing pair);

    /**
     * Prints the set of truth assignments specified by this BDD.
     * 
     * Compare to bdd_printset.
     */
    public void printSet() {
        System.out.println(this.toString());
    }

    /**
     * Prints this BDD using a set notation as in printSet() but with the index
     * of the finite domain blocks included instead of the BDD variables.
     * 
     * Compare to fdd_printset.
     */
    public void printSetWithDomains() {
        System.out.println(toStringWithDomains());
    }
    
    /**
     * Compare to bdd_printdot.
     */
    public abstract void printDot();

    
    /**
     * Counts the number of distinct nodes used for this BDD.  
     * 
     * Compare to bdd_nodecount.
     * 
     * @return the number of distinct nodes used for this BDD
     */
    public abstract int nodeCount();
    
    /**
     * Counts the number of paths leading to the true terminal.
     * 
     * Compare to bdd_pathcount.
     * 
     * @return the number of paths leading to the true terminal
     */
    public abstract double pathCount();
    
    /**
     * Calculates the number of satisfying variable assignments.
     * 
     * Compare to bdd_satcount.
     * 
     * @return the number of satisfying variable assignments
     */
    public abstract double satCount();
    
    /**
     * Calculates the number of satisfying variable assignments to the variables
     * in the given varset.
     * 
     * Compare to bdd_satcountset.
     * 
     * @return the number of satisfying variable assignments
     */
    public double satCount(BDD varset) {
        BDDFactory factory = getFactory();
        double unused = factory.varNum();

        if (varset.isZero() || varset.isOne() || isZero()) /* empty set */
            return 0.;

        for (BDD n = varset; !n.isOne() && !n.isZero(); n = n.high())
            unused--;

        unused = satCount() / Math.pow(2.0, unused);

        return unused >= 1.0 ? unused : 1.0;
    }
    
    /**
     * Calculates the log. number of satisfying variable assignments.
     * 
     * Compare to bdd_satcount.
     * 
     * @return the log. number of satisfying variable assignments
     */
    public double logSatCount() {
        return Math.log(satCount());
    }
    
    /**
     * Calculates the log. number of satisfying variable assignments to the
     * variables in the given varset.
     * 
     * Compare to bdd_satcountset.
     * 
     * @return the log. number of satisfying variable assignments
     */
    public double logSatCount(BDD varset) {
        return Math.log(satCount(varset));
    }
    
    /**
     * Counts the number of times each variable occurs in this BDD.  The
     * result is stored and returned in an integer array where the i'th
     * position stores the number of times the i'th printing variable
     * occurred in the BDD.
     * 
     * Compare to bdd_varprofile.
     */
    public abstract int[] varProfile();
    
    public abstract boolean equals(BDD that);
    
    public boolean equals(Object o) {
        if (!(o instanceof BDD)) return false;
        return this.equals((BDD) o);
    }
    
    public abstract int hashCode();
    
    public String toString() {
        BDDFactory f = this.getFactory();
        int[] set = new int[f.varNum()];
        StringBuffer sb = new StringBuffer();
        bdd_printset_rec(f, sb, this, set);
        return sb.toString();
    }
    
    private static void bdd_printset_rec(BDDFactory f, StringBuffer sb, BDD r, int[] set) {
        int n;
        boolean first;

        if (r.isZero())
            return;
        else if (r.isOne()) {
            sb.append('<');
            first = true;

            for (n = 0; n < set.length; n++) {
                if (set[n] > 0) {
                    if (!first)
                        sb.append(", ");
                    first = false;
                    sb.append(f.level2Var(n));
                    sb.append(':');
                    sb.append((set[n] == 2 ? 1 : 0));
                }
            }
            sb.append('>');
        } else {
            set[f.var2Level(r.var())] = 1;
            BDD rl = r.low();
            bdd_printset_rec(f, sb, rl, set);
            rl.free();

            set[f.var2Level(r.var())] = 2;
            BDD rh = r.high();
            bdd_printset_rec(f, sb, rh, set);
            rh.free();

            set[f.var2Level(r.var())] = 0;
        }
    }
    
    public String toStringWithDomains() {
        return toStringWithDomains(BDDToString.INSTANCE);
    }
    
    public String toStringWithDomains(BDDToString ts) {
        if (this.isZero()) return "F";
        if (this.isOne()) return "T";
        
        BDDFactory bdd = getFactory();
        StringBuffer sb = new StringBuffer();
        int[] set = new int[bdd.varNum()];
        fdd_printset_rec(bdd, sb, ts, this, set);
        return sb.toString();
    }
    
    static void fdd_printset_rec(BDDFactory bdd, StringBuffer sb, BDDToString ts, BDD r, int[] set) {
        int fdvarnum = bdd.numberOfDomains();
        
        int n, m, i;
        boolean used = false;
        int[] var;
        boolean[] binval;
        boolean ok, first;
        
        if (r.isZero())
            return;
        else if (r.isOne()) {
            sb.append('<');
            first = true;
            
            for (n=0 ; n<fdvarnum ; n++) {
                boolean firstval = true;
                used = false;
                
                BDDDomain domain_n = bdd.getDomain(n);
                
                int[] domain_n_ivar = domain_n.vars();
                int domain_n_varnum = domain_n_ivar.length;
                for (m=0 ; m<domain_n_varnum ; m++)
                    if (set[domain_n_ivar[m]] != 0)
                        used = true;
                
                if (used) {
                    if (!first)
                        sb.append(", ");
                    first = false;
                    sb.append(ts.domainName(n));
                    sb.append(':');
                    
                    var = domain_n_ivar;
                    
                    for (m=0 ; m<(1<<domain_n_varnum) ; m++) {
                        binval = fdddec2bin(bdd, n, m);
                        ok = true;
                        
                        for (i=0 ; i<domain_n_varnum && ok ; i++)
                            if (set[var[i]] == 1  &&  binval[i] != false)
                                ok = false;
                            else if (set[var[i]] == 2  &&  binval[i] != true)
                                ok = false;
                        
                        if (ok) {
                            if (!firstval)
                                sb.append('/');
                            sb.append(ts.elementName(n, m));
                            firstval = false;
                        }
                        
                        //free(binval);
                    }
                }
            }
            
            sb.append('>');
        } else {
            set[r.var()] = 1;
            fdd_printset_rec(bdd, sb, ts, r.low(), set);
            
            set[r.var()] = 2;
            fdd_printset_rec(bdd, sb, ts, r.high(), set);
            
            set[r.var()] = 0;
        }
    }
    
    static boolean[] fdddec2bin(BDDFactory bdd, int var, int val) {
        boolean[] res;
        int n = 0;
        
        res = new boolean[bdd.getDomain(var).varNum()];
        
        while (val > 0) {
            if ((val & 0x1) != 0)
                res[n] = true;
            val >>= 1;
            n++;
        }
        
        return res;
    }
    
    public static class BDDToString {
        public static final BDDToString INSTANCE = new BDDToString();
        protected BDDToString() { }
        public String domainName(int i) { return Integer.toString(i); }
        public String elementName(int i, int j) { return Integer.toString(j); }
    }
    
    /**
     * Increases the reference count on a node.  Reference counting is done on
     * externally-referenced nodes only.
     * 
     * Compare to bdd_addref.
     */
    protected abstract void addRef();
    
    /**
     * Decreases the reference count on a node.  Reference counting is done on
     * externally-referenced nodes only.
     * 
     * Compare to bdd_delref.
     */
    protected abstract void delRef();
    
    public void free() {
        this.delRef();
    }
    
    protected BDD() { }
    
}
