package org.sf.javabdd;

import java.util.Collection;

/**
 * Interface for the creation and manipulation of BDDs.
 * 
 * @see org.sf.javabdd.BDD
 * 
 * @author John Whaley
 * @version $Id$
 */
public abstract class BDDFactory {

    /**
     * Logical 'and'.
     */
    public static final BDDOp and   = new BDDOp(0, "and");
    /**
     * Logical 'xor'.
     */
    public static final BDDOp xor   = new BDDOp(1, "xor");
    /**
     * Logical 'or'.
     */
    public static final BDDOp or    = new BDDOp(2, "or");
    /**
     * Logical 'nand'.
     */
    public static final BDDOp nand  = new BDDOp(3, "nand");
    /**
     * Logical 'nor'.
     */
    public static final BDDOp nor   = new BDDOp(4, "nor");
    /**
     * Logical 'implication'.
     */
    public static final BDDOp imp   = new BDDOp(5, "imp");
    /**
     * Logical 'bi-implication'.
     */
    public static final BDDOp biimp = new BDDOp(6, "biimp");
    /**
     * Set difference.
     */
    public static final BDDOp diff  = new BDDOp(7, "diff");
    /**
     * Less than.
     */
    public static final BDDOp less  = new BDDOp(8, "less");
    /**
     * Inverse implication.
     */
    public static final BDDOp invimp = new BDDOp(9, "invimp");

    /**
     * Enumeration class for binary operations on BDDs.  Use the static
     * fields in BDDFactory to access the different binary operations.
     */
    public static class BDDOp {
        final int id; final String name;
        private BDDOp(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public String toString() {
            return name;
        }
    }
    
    /**
     * Get the constant false BDD.
     * 
     * Compare to bdd_false.
     */
    public abstract BDD zero();
    
    /**
     * Get the constant true BDD.
     * 
     * Compare to bdd_true.
     */
    public abstract BDD one();
    
    /**
     * Build a cube from an array of variables.
     * 
     * Compare to bdd_buildcube.
     */
    public abstract BDD buildCube(int value, int width, Collection/*BDD*/ var);
    
    /**
     * Build a cube from an array of variables.
     * 
     * Compare to bdd_ibuildcube.
     */
    public abstract BDD buildCube(int value, int width, int[] var);
    
    /**
     * Builds a BDD variable set from an integer array.  The integer array v
     * holds the variable numbers.  The BDD variable set is represented by a
     * conjunction of all the variables in their positive form.
     * 
     * Compare to bdd_makeset.
     */
    public abstract BDD makeSet(int[] v);
    

    
    /**** STARTUP / SHUTDOWN ****/
    
    /**
     * Compare to bdd_init.
     * 
     * @param nodenum the initial number of BDD nodes
     * @param cachesize the size of caches used by the BDD operators
     */
    protected abstract void initialize(int nodenum, int cachesize);

    /**
     * Compare to bdd_isrunning.
     * 
     * @return boolean
     */
    public abstract boolean isInitialized();

    /**
     * Resets the BDD package.  This function frees all memory used by the BDD
     * package and resets the package to its initial state.
     * 
     * Compare to bdd_done.
     */
    public abstract void done();

    
    
    /**** CACHE/TABLE PARAMETERS ****/
    
    /**
     * Set the maximum available number of BDD nodes.
     * 
     * Compare to bdd_setmaxnodenum.
     */
    public abstract int setMaxNodeNum(int size);

    /**
     * Set minimum number of nodes to be reclaimed after a garbage collection.
     * The range of x is 0..100.  The default is 20.
     * 
     * Compare to bdd_setminfreenodes.
     * @param x
     */
    public abstract void setMinFreeNodes(int x);
    
    /**
     * Set maximum number of nodes used to increase node table.
     * 
     * Compare to bdd_setmaxincrease.
     * @param x
     */
    public abstract int setMaxIncrease(int x);
    
    /**
     * Sets the cache ratio for the operator caches.
     * 
     * Compare to bdd_setcacheratio.
     * @param x
     */
    public abstract int setCacheRatio(int x);
    
    
    
    /**** VARIABLE NUMBERS ****/
    
    /**
     * Returns the number of defined variables.
     * 
     * Compare to bdd_varnum.
     */
    public abstract int varNum();
    
    /**
     * Set the number of used BDD variables.  It can be called more than one
     * time, but only to increase the number of variables.
     * 
     * Compare to bdd_setvarnum.
     * 
     * @param num
     */
    public abstract int setVarNum(int num);
    
    /**
     * Add extra BDD variables.  Extends the current number of allocated BDD
     * variables with num extra variables.
     * 
     * Compare to bdd_extvarnum.
     * 
     * @param num
     */
    public abstract int extVarNum(int num);
    // TODO: handle error code for extvarnum (?)
    
    /**
     * Returns a BDD representing the I'th variable.  (One node with the
     * children true and false).  The requested variable must be in the
     * (zero-indexed) range defined by setVarNum.
     * 
     * Compare to bdd_ithvar.
     * 
     * @return the I'th variable on success, otherwise the constant false BDD
     */
    public abstract BDD ithVar(int var);
    
    /**
     * Returns a BDD representing the negation of the I'th variable.  (One node
     * with the children false and true).  The requested variable must be in the
     * (zero- indexed) range defined by setVarNum.
     * 
     * Compare to bdd_nithvar.
     * 
     * @return the negated I'th variable on success, otherwise the constant
     * false BDD
     */
    public abstract BDD nithVar(int var);
    
    
    
    /**** INPUT / OUTPUT ****/
    
    /**
     * Prints all used entries in the node table.
     * 
     * Compare to bdd_printall.
     */
    public abstract void printAll();
    
    /**
     * Prints the node table entries used by a BDD.
     * 
     * Compare to bdd_printtable.
     */
    public abstract void printTable(BDD b);
    
    /**
     * Loads a BDD from a file.
     * 
     * Compare to bdd_load.
     */
    public abstract BDD load(String filename);
    // TODO: error code from bdd_load (?)
    
    /**
     * Saves a BDD to a file.
     * 
     * Compare to bdd_save.
     */
    public abstract void save(String filename, BDD var);
    // TODO: error code from bdd_save (?)
    
    // TODO: bdd_strm_hook, bdd_file_hook, bdd_blockfile_hook
    // TODO: bdd_versionnum, bdd_versionstr
    
    
    
    /**
     * Compare to bdd_level2var.
     */
    public abstract int level2Var(int level);
    
    /**
     * Compare to bdd_var2level.
     */
    public abstract int var2level(int var);
    
    
    /**** REORDERING ****/
    
    /**
     * Compare to bdd_reorder.
     */
    public abstract void reorder(ReorderMethod m);
    
    /**
     * Enables automatic reordering.  If method is REORDER_NONE then automatic
     * reordering is disabled.
     * 
     * Compare to bdd_autoreorder.
     */
    public abstract void autoReorder(ReorderMethod method);
    
    /**
     * Enables automatic reordering with the given (maximum) number of
     * reorderings. If method is REORDER_NONE then automatic reordering is
     * disabled.
     * 
     * Compare to bdd_autoreorder_times.
     */
    public abstract void autoReorder(ReorderMethod method, int max);

    /**
     * Returns the current reorder method as defined by autoReorder.
     * 
     * Compare to bdd_getreorder_method.
     * 
     * @return ReorderMethod
     */
    public abstract ReorderMethod getReorderMethod();
    
    /**
     * Returns the number of allowed reorderings left.  This value can be
     * defined by autoReorder.
     * 
     * Compare to bdd_getreorder_times.
     */
    public abstract int getReorderTimes();
    
    /**
     * Disable automatic reordering until enableReorder is called.  Reordering
     * is enabled by default as soon as any variable blocks have been defined.
     * 
     * Compare to bdd_disable_reorder.
     */
    public abstract void disableReorder();
    
    /**
     * Enable automatic reordering after a call to disableReorder.
     * 
     * Compare to bdd_enable_reorder
     */
    public abstract void enableReorder();

    /**
     * Enables verbose information about reordering.  A value of zero means no
     * information, one means some information and greater than one means lots
     * of information.
     * 
     * @param v the new verbose level
     * @return the old verbose level
     */
    public abstract int reorderVerbose(int v);
    
    /**
     * This function sets the current variable order to be the one defined by
     * neworder.  The variable parameter neworder is interpreted as a sequence
     * of variable indices and the new variable order is exactly this sequence.
     * The array must contain all the variables defined so far. If, for
     * instance the current number of variables is 3 and neworder contains
     * [1; 0; 2] then the new variable order is v1<v0<v2.
     * 
     * @param neworder
     */
    public abstract void setVarOrder(int[] neworder);


    
    /**** VARIABLE BLOCKS ****/
    
    /**
     * Adds a new variable block for reordering.
     * 
     * Creates a new variable block with the variables in the variable set var.
     * The variables in var must be contiguous.
     * 
     * The fixed parameter sets the block to be fixed (no reordering of its
     * child blocks is allowed) or free,
     * 
     * Compare to bdd_addvarblock.
     */
    public abstract void addVarBlock(BDD var, boolean fixed);
    // TODO: handle error code for addVarBlock.
    
    /**
     * Adds a new variable block for reordering.
     * 
     * Creates a new variable block with the variables numbered first through
     * last, inclusive.
     * 
     * The fixed parameter sets the block to be fixed (no reordering of its
     * child blocks is allowed) or free,
     * 
     * Compare to bdd_intaddvarblock.
     */
    public abstract void addVarBlock(int first, int last, boolean fixed);
    // TODO: handle error code for addVarBlock.

    /**
     * Add a variable block for all variables.
     * 
     * Adds a variable block for all BDD variables declared so far.  Each block
     * contains one variable only.  More variable blocks can be added later with
     * the use of addVarBlock -- in this case the tree of variable blocks will
     * have the blocks of single variables as the leafs.
     * 
     * Compare to bdd_varblockall.
     */
    public abstract void varBlockAll();

    /**
     * Clears all the variable blocks that have been defined by calls to
     * addVarBlock.
     * 
     * Compare to bdd_clrvarblocks.
     */
    public abstract void clearVarBlocks();

    /**
     * Prints an indented list of the variable blocks.
     * 
     * Compare to bdd_printorder.
     */
    public abstract void printOrder();



    /**** BDD STATS ****/
    
    /**
     * Counts the number of shared nodes in a collection of BDDs.  Counts all
     * distinct nodes that are used in the BDDs -- if a node is used in more
     * than one BDD then it only counts once.
     * 
     * Compare to bdd_anodecount.
     */
    public abstract int nodeCount(Collection/*BDD*/ r);

    /**
     * Get the number of allocated nodes.  This includes both dead and active
     * nodes.
     * 
     * Compare to bdd_getallocnum.
     */
    public abstract int getAllocNum();

    /**
     * Get the number of active nodes in use.  Note that dead nodes that have
     * not been reclaimed yet by a garbage collection are counted as active.
     * 
     * Compare to bdd_getnodenum
     */
    public abstract int getNodeNum();

    /**
     * Calculate the gain in size after a reordering.  The value returned is
     * (100*(A-B))/A, where A is previous number of used nodes and B is current
     * number of used nodes.
     * 
     * Compare to bdd_reorder_gain.
     */
    public abstract int reorderGain();

    /**
     * Print cache statistics.
     * 
     * Compare to bdd_printstat.
     */
    public abstract void printStat();

    // TODO: bdd_cachestats, bdd_stats
    
    
    /**
     * Compare to bdd_newpair.
     */
    public abstract BDDPairing makePair();

    public BDDPairing makePair(int oldvar, int newvar) {
        BDDPairing p = makePair();
        p.set(oldvar, newvar);
        return p;
    }

    public BDDPairing makePair(BDD oldvar, BDD newvar) {
        BDDPairing p = makePair();
        p.set(oldvar, newvar);
        return p;
    }
    
    public BDDPairing makePair(BDDDomain oldvar, BDDDomain newvar) {
        BDDPairing p = makePair();
        p.set(oldvar, newvar);
        return p;
    }
    
    /**
     * Compare to bdd_swapvar.
     */
    public abstract void swapVar(int v1, int v2);
    
    // TODO: bdd_sizeprobe_hook, bdd_reorder_hook, bdd_resize_hook, bdd_gbc_hook
    // TODO: bdd_reorder_probe
    
    // TODO: bvec functions


    /**** FINITE DOMAINS ****/
    
    /**
     * Extends the set of finite domain blocks with domains of the given sizes.
     * Each entry in domainSizes is the size of a new finite domain which later
     * on can be used for finite state machine traversal and other operations on
     * finite domains.  Each domain allocates log 2 (|domainSizes[i]|) BDD
     * variables to be used later.  The ordering is interleaved for the domains
     * defined in each call to extDomain. This means that assuming domain D0
     * needs 2 BDD variables x1 and x2 , and another domain D1 needs 4 BDD
     * variables y1, y2, y3 and y4, then the order then will be x1, y1, x2, y2,
     * y3, y4.  The new domains are returned in order.  The BDD variables needed
     * to encode the domain are created for the purpose and do not interfere
     * with the BDD variables already in use.
     * 
     * Compare to fdd_extdomain.
     */
    public abstract BDDDomain[] extDomain(int[] domainSizes);
    
    /**
     * This function takes two finit blocks and merges them into a new one, such
     * that the new one is encoded using both sets of BDD variables.
     * 
     * Compare to fdd_overlapdomain.
     */
    public abstract BDDDomain overlapDomain(BDDDomain d1, BDDDomain d2);
    
    /**
     * Returns a BDD defining all the variable sets used to define the variable
     * blocks in the given array.
     * 
     * Compare to fdd_makeset.
     */
    public abstract BDD makeSet(BDDDomain[] v);
    
    /**
     * Clear all allocated finite domain blocks that were defined by extDomain()
     * or overlapDomain().
     * 
     * Compare to fdd_clearall.
     */
    public abstract void clearAllDomains();
    
    /**
     * Returns the number of finite domain blocks defined by calls to
     * extDomain().
     * 
     * Compare to fdd_domainnum.
     */
    public abstract int numberOfDomains();
    
    // TODO: fdd_file_hook, fdd_strm_hook
    
    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        this.done();
    }

    /**
     * No reordering.
     */
    public static final ReorderMethod REORDER_NONE    = new ReorderMethod(0, "NONE");
    /**
     * Reordering using a sliding window of 2.
     */
    public static final ReorderMethod REORDER_WIN2    = new ReorderMethod(1, "WIN2");
    /**
     * Reordering using a sliding window of 2, iterating until no further
     * progress.
     */
    public static final ReorderMethod REORDER_WIN2ITE = new ReorderMethod(2, "WIN2ITE");
    /**
     * Reordering using a sliding window of 3.
     */
    public static final ReorderMethod REORDER_WIN3    = new ReorderMethod(5, "WIN3");
    /**
     * Reordering using a sliding window of 3, iterating until no further
     * progress.
     */
    public static final ReorderMethod REORDER_WIN3ITE = new ReorderMethod(6, "WIN3ITE");
    /**
     * Reordering where each block is moved through all possible positions.  The
     * best of these is then used as the new position.  Potentially a very slow
     * but good method.
     */
    public static final ReorderMethod REORDER_SIFT    = new ReorderMethod(3, "SIFT");
    /**
     * Same as REORDER_SIFT, but the process is repeated until no further
     * progress is done.  Can be extremely slow.
     */
    public static final ReorderMethod REORDER_SIFTITE = new ReorderMethod(4, "SIFTITE");
    /**
     * Selects a random position for each variable.  Mostly used for debugging
     * purposes.
     */
    public static final ReorderMethod REORDER_RANDOM  = new ReorderMethod(7, "RANDOM");
    
    /**
     * Enumeration class for method reordering techniques.  Use the static fields
     * in BDDFactory to access the different reordering techniques.
     */
    public static class ReorderMethod {
        final int id; final String name;
        private ReorderMethod(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public String toString() {
            return name;
        }
    }
}
