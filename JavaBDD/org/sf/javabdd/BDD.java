package org.sf.javabdd;

import java.util.Iterator;

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
     * Gets the variable labeling the BDD.
     * 
     * Compare to bdd_var.
     * 
     * @return the index of the variable labeling the BDD
     */
    public abstract int var();

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
    public abstract Iterator allsat();

    /**
     * Scans this BDD to find all occurrences of FDD variables and returns an
     * array that contains the indices of the possible found FDD variables.
     * 
     * Compare to fdd_scanset.
     * 
     * @return int[]
     */
    public abstract int[] scanSet();

    /**
     * Finds one satisfying assignment of the FDD variable var in this BDD and
     * returns that value.
     * 
     * Compare to fdd_scanvar.
     * 
     * @param var
     * @return int
     */
    public abstract int scanVar(int var);
    
    /**
     * Finds one satisfying assignment in this BDD of all the defined FDD
     * variables.  Each value is stored in an array which is returned.  The size
     * of this array is exactly the number of FDD variables defined.
     * 
     * Compare to fdd_scanallvar.
     * 
     * @return int[]
     */
    public abstract int[] scanAllVar();

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

    /**
     * Prints the set of truth assignments specified by this BDD.
     * 
     * Compare to bdd_printset.
     */
    public abstract void printSet();
    
    /**
     * Prints this BDD using a set notation as in printSet() but with the index
     * of the finite domain blocks included instead of the BDD variables.
     * 
     * Compare to fdd_printset.
     */
    public abstract void printSetWithDomains();
    
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
    public abstract double satCount(BDD varset);
    
    /**
     * Calculates the log. number of satisfying variable assignments.
     * 
     * Compare to bdd_satcount.
     * 
     * @return the log. number of satisfying variable assignments
     */
    public abstract double logSatCount();
    
    /**
     * Calculates the log. number of satisfying variable assignments to the
     * variables in the given varset.
     * 
     * Compare to bdd_satcountset.
     * 
     * @return the log. number of satisfying variable assignments
     */
    public abstract double logSatCount(BDD varset);
    
    public abstract boolean equals(BDD that);
    
    public boolean equals(Object o) {
        if (!(o instanceof BDD)) return false;
        return this.equals((BDD) o);
    }
    
    public abstract int hashCode();
    
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
