package org.sf.javabdd;

import java.util.Iterator;

/**
 * @author John Whaley
 * @version $Id$
 */
public abstract class BDD {

    /**
     * Gets the variable labeling the BDD.
     * 
     * Compare to bdd_var.
     */
    public abstract int var();

    /**
     * Gets the true branch of this BDD.
     * 
     * Compare to bdd_high.
     */
    public abstract BDD high();

    /**
     * Gets the false branch of this BDD.
     * 
     * Compare to bdd_low.
     */
    public abstract BDD low();

    /**
     * Identity function.  Returns a copy of this BDD.  Use as the argument to
     * the "xxxWith" style operators when you do not want to have the argument
     * consumed.
     * 
     * Compare to bdd_addref.
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
     * Returns the logical 'and' of two BDDs.
     * 
     * Compare to bdd_and.
     */
    public BDD and(BDD that) {
        return this.apply(that, BDDFactory.and);
    }

    /**
     * Makes this BDD be the logical 'and' of two BDDs.  The "that" BDD is
     * consumed, and can no longer be used.
     * 
     * Compare to bdd_and.
     */
    public void andWith(BDD that) {
        this.applyWith(that, BDDFactory.and);
    }

    /**
     * Returns the logical 'or' of two BDDs.
     * 
     * Compare to bdd_or.
     */
    public BDD or(BDD that) {
        return this.apply(that, BDDFactory.or);
    }

    /**
     * Makes this BDD be the logical 'or' of two BDDs.  The "that" BDD is
     * consumed, and can no longer be used.
     * 
     * Compare to bdd_or.
     */
    public void orWith(BDD that) {
        this.applyWith(that, BDDFactory.or);
    }

    /**
     * Returns the logical 'xor' of two BDDs.
     * 
     * Compare to bdd_xor.
     */
    public BDD xor(BDD that) {
        return this.apply(that, BDDFactory.xor);
    }
    
    /**
     * Makes this BDD be the logical 'xor' of two BDDs.  The "that" BDD is
     * consumed, and can no longer be used.
     * 
     * Compare to bdd_or.
     */
    public void xorWith(BDD that) {
        this.applyWith(that, BDDFactory.xor);
    }

    /**
     * Returns the logical 'implication' of two BDDs.
     * 
     * Compare to bdd_imp.
     */
    public BDD imp(BDD that) {
        return this.apply(that, BDDFactory.imp);
    }
    
    /**
     * Makes this BDD be the logical 'implication' of two BDDs.  The "that" BDD
     * is consumed, and can no longer be used.
     * 
     * Compare to bdd_or.
     */
    public void impWith(BDD that) {
        this.applyWith(that, BDDFactory.imp);
    }

    /**
     * Returns the logical 'bi-implication' of two BDDs.
     * 
     * Compare to bdd_biimp.
     */
    public BDD biimp(BDD that) {
        return this.apply(that, BDDFactory.biimp);
    }
    
    /**
     * Makes this BDD be the logical 'bi-implication' of two BDDs.  The "that"
     * BDD is consumed, and can no longer be used.
     * 
     * Compare to bdd_or.
     */
    public void biimpWith(BDD that) {
        this.applyWith(that, BDDFactory.biimp);
    }

    /**
     * if-then-else operator.
     * 
     * Compare to bdd_ite.
     */
    public abstract BDD ite(BDD thenBDD, BDD elseBDD);
    
    /**
     * Relational product.  Calculates the relational product of the two BDDs as
     * this AND that with the variables in var quantified out afterwards.
     * Identical to applyEx(that, and, var).
     * 
     * Compare to bdd_relprod.
     */
    public abstract BDD relprod(BDD that, BDD var);
    
    /**
     * Functional composition.  Substitutes the variable var with the BDD that
     * in this BDD: result = f[g/var].
     * 
     * Compare to bdd_compose.
     */
    public abstract BDD compose(BDD that, int var);

    /**
     * Generalized cofactor.  Computes the generalized cofactor of this BDD with
     * respect to the given BDD.
     * 
     * Compare to bdd_constrain.
     */
    public abstract BDD constrain(BDD that);

    /**
     * Existential quantification of variables.  Removes all occurrences of this
     * BDD in variables in the set var by existential quantification.
     * 
     * Compare to bdd_exist.
     */
    public abstract BDD exist(BDD var);

    /**
     * Universal quantification of variables.  Removes all occurrences of this
     * BDD in variables in the set var by universal quantification.
     * 
     * Compare to bdd_forall.
     */
    public abstract BDD forAll(BDD var);

    /**
     * Unique quantification of variables.  This type of quantification uses a
     * XOR operator instead of an OR operator as in the existential
     * quantification.
     * 
     * Compare to bdd_unique.
     */
    public abstract BDD unique(BDD var);
    
    /**
     * Restrict a set of variables to constant values.  Restricts the variables
     * in this BDD to constant true if they are included in their positive form
     * in var, and constant false if they are included in their negative form.
     * 
     * Compare to bdd_restrict.
     */
    public abstract BDD restrict(BDD var);

    /**
     * Coudert and Madre's restrict function.  Tries to simplify the BDD f by
     * restricting it to the domain covered by d.  No checks are done to see if
     * the result is actually smaller than the input.  This can be done by the
     * user with a call to nodeCount().
     * 
     * Compare to bdd_simplify.
     */
    public abstract BDD simplify(BDD d);

    /**
     * Returns the variable support of this BDD.  The support is all the
     * variables that this BDD depends on.
     */
    public abstract BDD support();

    /**
     * Returns the result of applying the binary operator opr to the two BDDs.
     * 
     * Compare to bdd_apply.
     */
    public abstract BDD apply(BDD that, BDDFactory.BDDOp opr);

    /**
     * Makes this BDD be the result of the binary operator opr of two BDDs.  The
     * "that" BDD is consumed, and can no longer be used.
     * 
     * Compare to bdd_apply.
     */
    public abstract void applyWith(BDD that, BDDFactory.BDDOp opr);
    
    /**
     * Applies the binary operator opr to two BDDs and then performs a universal
     * quantification of the variables from the variable set var.
     * 
     * Compare to bdd_appall.
     */
    public abstract BDD applyAll(BDD that, BDDFactory.BDDOp opr, BDD var);

    /**
     * Applies the binary operator opr to two BDDs and then performs an
     * existential quantification of the variables from the variable set var.
     * 
     * Compare to bdd_appex.
     */
    public abstract BDD applyEx(BDD that, BDDFactory.BDDOp opr, BDD var);

    /**
     * Applies the binary operator opr to two BDDs and then performs a unique
     * quantification of the variables from the variable set var.
     * 
     * Compare to bdd_appuni.
     */
    public abstract BDD applyUni(BDD that, BDDFactory.BDDOp opr, BDD var);

    /**
     * Finds one satisfying variable assignment.  Finds a BDD with at most one
     * variable at each levels.  The new BDD implies this BDD and is not false
     * unless this BDD is false.
     * 
     * Compare to bdd_satone.
     */
    public abstract BDD satOne();

    /**
     * Finds one satisfying variable assignment.  Finds a BDD with exactly one
     * variable at all levels.  The new BDD implies this BDD and is not false
     * unless this BDD is false.
     * 
     * Compare to bdd_fullsatone.
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
     */
    public abstract BDD satOneSet(BDD var, BDD pol);

    /**
     * Finds all satisfying variable assignments.
     * 
     * Compare to bdd_allsat.
     */
    public abstract Iterator allsat();

    /**
     * Prints the set of truth assignments specified by this BDD.
     * 
     * Compare to bdd_printset.
     */
    public abstract void printSet();
    
    /**
     * Compare to bdd_printdot.
     */
    public abstract void printDot();

    
    /**
     * Counts the number of distinct nodes used for this BDD.  
     * 
     * Compare to bdd_nodecount.
     */
    public abstract int nodeCount();
    
    /**
     * Counts the number of paths leading to the true terminal.
     * 
     * Compare to bdd_pathcount.
     */
    public abstract double pathCount();
    
    /**
     * Calculates the number of satisfying variable assignments.
     * 
     * Compare to bdd_satcount.
     */
    public abstract double satCount();
    
    /**
     * Calculates the number of satisfying variable assignments to the variables
     * in the given varset.
     * 
     * Compare to bdd_satcountset.
     */
    public abstract double satCount(BDD varset);
    
    /**
     * Calculates the log. number of satisfying variable assignments.
     * 
     * Compare to bdd_satcount.
     */
    public abstract double logSatCount();
    
    /**
     * Calculates the log. number of satisfying variable assignments to the
     * variables in the given varset.
     * 
     * Compare to bdd_satcountset.
     */
    public abstract double logSatCount(BDD varset);
    
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
    
    protected BDD() { }
    
    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        this.delRef();
    }

}
