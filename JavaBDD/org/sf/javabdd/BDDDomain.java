package org.sf.javabdd;

/**
 * Represents a domain of BDD variables.  This is useful for finite state
 * machines, among other things.
 * 
 * @author John Whaley
 * @version $Id$
 */
public abstract class BDDDomain {

    public abstract BDDFactory getFactory();

    /**
     * Returns the index of this domain.
     */ 
    public abstract int getIndex();

    /**
     * Returns what corresponds to a disjunction of all possible values of this
     * domain. This is more efficient than doing ithVar(0) OR ithVar(1) ...
     * explicitly for all values in the domain.
     * 
     * Compare to fdd_domain.
     */ 
    public BDD domain() {
        BDDFactory factory = getFactory();
        
        /* Encode V<=X-1. V is the variables in 'var' and X is the domain size */
        int val = size() - 1;
        BDD d = factory.one();
        int[] ivar = vars();
        for (int n = 0; n < this.varNum(); n++) {
            if ((val & 0x1) != 0)
                d.orWith(factory.nithVar(ivar[n]));
            else
                d.andWith(factory.nithVar(ivar[n]));
            val >>= 1;
        }
        return d;
    }

    /**
     * Returns the size of the domain for this finite domain block.
     * 
     * Compare to fdd_domainsize.
     */
    public abstract int size();
    
    public abstract BDD buildAdd(BDDDomain that, int value);
    
    /**
     * Builds a BDD which is true for all the possible assignments to the
     * variable blocks that makes the blocks equal.
     * 
     * Compare to fdd_equals/fdd_equ.
     * 
     * @param that
     * @return BDD
     */
    public BDD buildEquals(BDDDomain that) {
        if (this.size() != that.size()) {
            throw new BDDException();
        }
   
        BDDFactory factory = getFactory();
        BDD e = factory.one();
        
        int[] this_ivar = this.vars();
        int[] that_ivar = that.vars();
        
        for (int n=0 ; n<this.varNum() ; n++)
        {
            BDD a = factory.ithVar(this_ivar[n]);
            BDD b = factory.ithVar(that_ivar[n]);
            a.applyWith(b, BDDFactory.biimp);
            e.andWith(a);
        }

        return e;
    }
    
    /**
     * Returns the variable set that contains the variables used to define this
     * finite domain block.
     * 
     * Compare to fdd_ithset.
     * 
     * @return BDD
     */
    public abstract BDD set();
    
    /**
     * Returns the BDD that defines the given value for this finite domain
     * block.
     * 
     * Compare to fdd_ithvar.
     * 
     * @return BDD
     */
    public BDD ithVar(int val) {
        if (val < 0 || val >= this.size()) {
            throw new BDDException();
        }

        BDDFactory factory = getFactory();
        BDD v = factory.one();
        int[] ivar = this.vars();
        for (int n = 0; n < ivar.length; n++) {
            if ((val & 0x1) != 0)
                v.andWith(factory.ithVar(ivar[n]));
            else
                v.andWith(factory.nithVar(ivar[n]));
            val >>= 1;
        }

        return v;
    }
    
    /**
     * Returns the BDD that defines the given range of values, inclusive,
     * for this finite domain block.
     * 
     * @return BDD
     */
    public BDD varRange(int lo, int hi) {
        if (lo < 0 || hi >= this.size() || lo > hi) {
            throw new BDDException("range <"+lo+", "+hi+"> is invalid");
        }

        BDDFactory factory = getFactory();
        BDD result = factory.zero();
        int[] ivar = this.vars();
        while (lo <= hi) {
            int bitmask = 1 << (ivar.length - 1);
            BDD v = factory.one();
            for (int n = ivar.length - 1; ; n--) {
                int bit = lo & bitmask;
                if (bit != 0) {
                    v.andWith(factory.ithVar(ivar[n]));
                } else {
                    v.andWith(factory.nithVar(ivar[n]));
                }
                int mask = bitmask - 1;
                if ((lo & mask) == 0 && (lo | mask) <= hi) {
                    lo = (lo | mask) + 1;
                    break;
                }
                bitmask >>= 1;
            }
            result.orWith(v);
        }
        return result;
    }
    
    /**
     * Returns the number of BDD variables used for this finite domain block.
     * 
     * Compare to fdd_varnum.
     * 
     * @return int
     */
    public abstract int varNum();
    
    /**
     * Returns an integer array containing the indices of the BDD variables used
     * to define this finite domain.
     * 
     * Compare to fdd_vars.
     * 
     * @return int[]
     */
    public abstract int[] vars();
    
}
