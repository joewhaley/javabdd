package org.sf.javabdd;

/**
 * @author John Whaley
 * @version $Id$
 */
public abstract class BDDPairing {

    /**
     * Adds the pair (oldvar, newvar) to this table of pairs. This results in
     * oldvar being substituted with newvar in a call to BDD.replace().
     * 
     * Compare to bdd_setpair.
     */
    public abstract void set(int oldvar, int newvar);

    /**
     * Like set(), but with a whole list of pairs.
     * 
     * Compare to bdd_setpairs.
     */
    public abstract void set(int[] oldvar, int[] newvar);
    
    /**
     * Adds the pair (oldvar, newvar) to this table of pairs. This results in
     * oldvar being substituted with newvar in a call to bdd.replace().  The
     * variable oldvar is substituted with the BDD newvar.  The possibility to
     * substitute with any BDD as newvar is utilized in BDD.compose(), whereas
     * only the topmost variable in the BDD is used in BDD.replace().
     * 
     * Compare to bdd_setbddpair.
     */
    public abstract void set(BDD oldvar, BDD newvar);

    /**
     * Like set(), but with a whole list of pairs.
     * 
     * Compare to bdd_setbddpairs.
     */
    public abstract void set(BDD[] oldvar, BDD[] newvar);
    
    /**
     * Defines each variable in the finite domain block p1 to be paired with the
     * corresponding variable in p2.
     * 
     * Compare to fdd_setpair.
     */
    public abstract void set(BDDDomain p1, BDDDomain p2);

    /**
     * Like set(), but with a whole list of pairs.
     * 
     * Compare to fdd_setpairs.
     */
    public abstract void set(BDDDomain[] p1, BDDDomain[] p2);

    /**
     * Resets this table of pairs by setting all substitutions to their default
     * values (that is, no change).
     * 
     * Compare to bdd_resetpair.
     */
    public abstract void reset();

}
