/*
 * Created on Oct 20, 2003
 */
package org.sf.javabdd;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <p>This BDD factory keeps track of what domains each BDD uses, and complains
 * if you try to do an operation where the domains do not match.</p>
 * 
 * @see org.sf.javabdd.BDDFactory
 * 
 * @author John Whaley
 * @version $Id$
 */
public class TypedBDDFactory extends BDDFactory {

    static PrintStream out = System.out;
    static boolean STACK_TRACES = true;
    
    BDDFactory factory;
    
    TypedBDDFactory(BDDFactory f) {
        this.factory = f;
    }
    
    public static BDDFactory init(int nodenum, int cachesize) {
        BDDFactory a = BuDDyFactory.init(nodenum, cachesize);
        return new TypedBDDFactory(a);
    }
    
    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#zero()
     */
    public BDD zero() {
        return new TypedBDD(factory.zero(), makeSet());
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#one()
     */
    public BDD one() {
        Set s = makeSet();
        //Set s = allDomains();
        return new TypedBDD(factory.one(), s);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#initialize(int, int)
     */
    protected void initialize(int nodenum, int cachesize) {
        factory.initialize(nodenum, cachesize);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#isInitialized()
     */
    public boolean isInitialized() {
        return factory.isInitialized();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#done()
     */
    public void done() {
        factory.done();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxNodeNum(int)
     */
    public int setMaxNodeNum(int size) {
        return factory.setMaxNodeNum(size);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMinFreeNodes(int)
     */
    public void setMinFreeNodes(int x) {
        factory.setMinFreeNodes(x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setMaxIncrease(int)
     */
    public int setMaxIncrease(int x) {
        return factory.setMaxIncrease(x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setCacheRatio(int)
     */
    public int setCacheRatio(int x) {
        return factory.setCacheRatio(x);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varNum()
     */
    public int varNum() {
        return factory.varNum();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarNum(int)
     */
    public int setVarNum(int num) {
        return factory.setVarNum(num);
    }

    public BDDDomain whichDomain(int var) {
        for (int i = 0; i < numberOfDomains(); ++i) {
            int[] vars = getDomain(i).vars();
            for (int j = 0; j < vars.length; ++j) {
                if (var == vars[j])
                    return getDomain(i);
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#ithVar(int)
     */
    public BDD ithVar(int var) {
        Set s = makeSet();
        //BDDDomain d = whichDomain(var);
        //if (d != null) s.add(d);
        return new TypedBDD(factory.ithVar(var), s);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nithVar(int)
     */
    public BDD nithVar(int var) {
        Set s = makeSet();
        //BDDDomain d = whichDomain(var);
        //if (d != null) s.add(d);
        return new TypedBDD(factory.nithVar(var), s);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printAll()
     */
    public void printAll() {
        factory.printAll();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printTable(org.sf.javabdd.BDD)
     */
    public void printTable(BDD b) {
        TypedBDD bdd1 = (TypedBDD) b;
        factory.printTable(bdd1.bdd);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#load(java.lang.String)
     */
    public BDD load(String filename) throws IOException {
        // TODO domains?
        Set d = makeSet();
        return new TypedBDD(factory.load(filename), d);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD var) throws IOException {
        TypedBDD bdd1 = (TypedBDD) var;
        factory.save(filename, bdd1.bdd);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#level2Var(int)
     */
    public int level2Var(int level) {
        return factory.level2Var(level);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#var2Level(int)
     */
    public int var2Level(int var) {
        return factory.var2Level(var);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void reorder(ReorderMethod m) {
        factory.reorder(m);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod)
     */
    public void autoReorder(ReorderMethod method) {
        factory.autoReorder(method);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#autoReorder(org.sf.javabdd.BDDFactory.ReorderMethod, int)
     */
    public void autoReorder(ReorderMethod method, int max) {
        factory.autoReorder(method, max);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderMethod()
     */
    public ReorderMethod getReorderMethod() {
        return factory.getReorderMethod();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getReorderTimes()
     */
    public int getReorderTimes() {
        return factory.getReorderTimes();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#disableReorder()
     */
    public void disableReorder() {
        factory.disableReorder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#enableReorder()
     */
    public void enableReorder() {
        factory.enableReorder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderVerbose(int)
     */
    public int reorderVerbose(int v) {
        return factory.reorderVerbose(v);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#setVarOrder(int[])
     */
    public void setVarOrder(int[] neworder) {
        factory.setVarOrder(neworder);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(org.sf.javabdd.BDD, boolean)
     */
    public void addVarBlock(BDD var, boolean fixed) {
        TypedBDD bdd1 = (TypedBDD) var;
        factory.addVarBlock(bdd1.bdd, fixed);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#addVarBlock(int, int, boolean)
     */
    public void addVarBlock(int first, int last, boolean fixed) {
        factory.addVarBlock(first, last, fixed);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#varBlockAll()
     */
    public void varBlockAll() {
        factory.varBlockAll();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#clearVarBlocks()
     */
    public void clearVarBlocks() {
        factory.clearVarBlocks();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printOrder()
     */
    public void printOrder() {
        factory.printOrder();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nodeCount(java.util.Collection)
     */
    public int nodeCount(Collection r) {
        Collection s = new LinkedList();
        for (Iterator i = r.iterator(); i.hasNext(); ) {
            TypedBDD bdd1 = (TypedBDD) i.next();
            s.add(bdd1.bdd);
        }
        return factory.nodeCount(s);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getAllocNum()
     */
    public int getAllocNum() {
        return factory.getAllocNum();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#getNodeNum()
     */
    public int getNodeNum() {
        return factory.getNodeNum();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#reorderGain()
     */
    public int reorderGain() {
        return factory.reorderGain();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#printStat()
     */
    public void printStat() {
        factory.printStat();
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#makePair()
     */
    public BDDPairing makePair() {
        return new TypedBDDPairing(factory.makePair());
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#swapVar(int, int)
     */
    public void swapVar(int v1, int v2) {
        factory.swapVar(v1, v2);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createDomain(int, long)
     */
    protected BDDDomain createDomain(int a, long b) {
        return new TypedBDDDomain(factory.getDomain(a), a, b);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createBitVector(int)
     */
    protected BDDBitVector createBitVector(int a) {
        return factory.createBitVector(a);
    }

    public BDDDomain[] extDomain(long[] domainSizes) {
        factory.extDomain(domainSizes);
        return super.extDomain(domainSizes);
    }
    
    static Set makeSet() {
        //return SortedArraySet.FACTORY.makeSet(domain_comparator);
        return new TreeSet(domain_comparator);
    }
    
    static Set makeSet(Set s) {
        //Set r = SortedArraySet.FACTORY.makeSet(domain_comparator);
        Set r = new TreeSet(domain_comparator);
        r.addAll(s);
        return r;
    }
    
    Set allDomains() {
        Set r = makeSet();
        for (int i = 0; i < factory.numberOfDomains(); ++i) {
            r.add(factory.getDomain(i));
        }
        return r;
    }
    
    static Map makeMap() {
        return new TreeMap(domain_comparator);
    }
    
    static String domainNames(Set dom) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = dom.iterator(); i.hasNext(); ) {
            BDDDomain d = (BDDDomain) i.next();
            sb.append(d.getName());
            if (i.hasNext()) sb.append(',');
        }
        return sb.toString();
    }
    
    public static final Comparator domain_comparator = new Comparator() {

        public int compare(Object arg0, Object arg1) {
            BDDDomain d1 = (BDDDomain) arg0;
            BDDDomain d2 = (BDDDomain) arg1;
            if (d1.getIndex() < d2.getIndex()) return -1;
            else if (d1.getIndex() > d2.getIndex()) return 1;
            else return 0;
        }
        
    };
    
    public class TypedBDD extends BDD {
        
        final BDD bdd;
        final Set dom;
        
        public TypedBDD(BDD bdd, Set dom) {
            this.bdd = bdd;
            this.dom = dom;
        }
        
        /**
         * Returns the set of domains that this BDD uses.
         */
        public Set getDomainSet() {
            return dom;
        }
        
        /**
         * Changes this BDD's domains to be the given set.
         */
        public void setDomains(Set d) {
            dom.clear();
            dom.addAll(d);
        }
        
        /**
         * Changes this BDD's domain to be the given domain.
         */
        public void setDomains(BDDDomain d) {
            dom.clear();
            dom.add(d);
        }
        
        /**
         * Changes this BDD's domains to be the given domains.
         */
        public void setDomains(BDDDomain d1, BDDDomain d2) {
            dom.clear();
            dom.add(d1); dom.add(d2);
        }
        
        /**
         * Changes this BDD's domains to be the given domains.
         */
        public void setDomains(BDDDomain d1, BDDDomain d2, BDDDomain d3) {
            dom.clear();
            dom.add(d1); dom.add(d2); dom.add(d3);
        }
        
        /**
         * Changes this BDD's domains to be the given domains.
         */
        public void setDomains(BDDDomain d1, BDDDomain d2, BDDDomain d3, BDDDomain d4) {
            dom.clear();
            dom.add(d1); dom.add(d2); dom.add(d3); dom.add(d4);
        }
        
        /**
         * Changes this BDD's domains to be the given domains.
         */
        public void setDomains(BDDDomain d1, BDDDomain d2, BDDDomain d3, BDDDomain d4, BDDDomain d5) {
            dom.clear();
            dom.add(d1); dom.add(d2); dom.add(d3); dom.add(d4); dom.add(d5);
        }
        
        /**
         * Returns the set of domains in BDD format.
         */
        BDD getDomains() {
            BDD b = factory.one();
            for (Iterator i = dom.iterator(); i.hasNext(); ) {
                TypedBDDDomain d = (TypedBDDDomain) i.next();
                b.andWith(d.domain.set());
            }
            return b;
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#getFactory()
         */
        public BDDFactory getFactory() {
            return TypedBDDFactory.this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isZero()
         */
        public boolean isZero() {
            return bdd.isZero();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#isOne()
         */
        public boolean isOne() {
            return bdd.isOne();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#var()
         */
        public int var() {
            return bdd.var();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#high()
         */
        public BDD high() {
            return new TypedBDD(bdd.high(), makeSet(dom));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#low()
         */
        public BDD low() {
            return new TypedBDD(bdd.low(), makeSet(dom));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#id()
         */
        public BDD id() {
            return new TypedBDD(bdd.id(), makeSet(dom));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#not()
         */
        public BDD not() {
            return new TypedBDD(bdd.not(), makeSet(dom));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#ite(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD ite(BDD thenBDD, BDD elseBDD) {
            TypedBDD bdd1 = (TypedBDD) thenBDD;
            TypedBDD bdd2 = (TypedBDD) elseBDD;
            Set newDom = makeSet();
            newDom.addAll(dom);
            newDom.addAll(bdd1.dom);
            newDom.addAll(bdd2.dom);
            return new TypedBDD(bdd.ite(bdd1.bdd, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#relprod(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD relprod(BDD that, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            TypedBDD bdd2 = (TypedBDD) var;
            Set newDom = makeSet();
            newDom.addAll(dom);
            newDom.addAll(bdd1.dom);
            if (!newDom.containsAll(bdd2.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd2.dom);
            return new TypedBDD(bdd.relprod(bdd1.bdd, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#compose(org.sf.javabdd.BDD, int)
         */
        public BDD compose(BDD g, int var) {
            TypedBDD bdd1 = (TypedBDD) g;
            // TODO How does this change the domains?
            Set newDom = makeSet();
            newDom.addAll(dom);
            return new TypedBDD(bdd.compose(bdd1.bdd, var), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#veccompose(org.sf.javabdd.BDDPairing)
         */
        public BDD veccompose(BDDPairing pair) {
            TypedBDDPairing p = (TypedBDDPairing) pair;
            // TODO How does this change the domains?
            Set newDom = makeSet();
            newDom.addAll(dom);
            return new TypedBDD(bdd.veccompose(p.pairing), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            TypedBDD bdd1 = (TypedBDD) that;
            // TODO How does this change the domains?
            Set newDom = makeSet();
            newDom.addAll(dom);
            return new TypedBDD(bdd.constrain(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd1.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd1.dom);
            return new TypedBDD(bdd.exist(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#forAll(org.sf.javabdd.BDD)
         */
        public BDD forAll(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd1.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd1.dom);
            return new TypedBDD(bdd.forAll(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#unique(org.sf.javabdd.BDD)
         */
        public BDD unique(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd1.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd1.dom);
            return new TypedBDD(bdd.unique(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrict(org.sf.javabdd.BDD)
         */
        public BDD restrict(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                out.println("Warning! Restricting domain that doesn't exist: "+domainNames(bdd1.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            if (bdd1.bdd.satCount(bdd1.getDomains()) > 1.0) {
                out.println("Warning! Using restrict with more than one value");
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd1.dom);
            return new TypedBDD(bdd.restrict(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrictWith(org.sf.javabdd.BDD)
         */
        public BDD restrictWith(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            if (!dom.containsAll(bdd1.dom)) {
                out.println("Warning! Restricting domain that doesn't exist: "+domainNames(bdd1.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            if (bdd1.bdd.satCount(bdd1.getDomains()) > 1.0) {
                out.println("Warning! Using restrict with more than one value");
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            dom.removeAll(bdd1.dom);
            bdd.restrictWith(bdd1.bdd);
            return this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#simplify(org.sf.javabdd.BDD)
         */
        public BDD simplify(BDD d) {
            TypedBDD bdd1 = (TypedBDD) d;
            // TODO How does this change the domains?
            Set newDom = makeSet();
            newDom.addAll(dom);
            return new TypedBDD(bdd.simplify(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#support()
         */
        public BDD support() {
            // TODO How does this change the domains?
            Set newDom = makeSet();
            newDom.addAll(dom);
            return new TypedBDD(bdd.support(), newDom);
        }

        void applyHelper(Set newDom, TypedBDD bdd0, TypedBDD bdd1, BDDOp opr) {
            switch (opr.id) {
                case 1: // xor
                case 2: // or
                case 4: // nor
                case 5: // imp
                case 6: // biimp
                case 7: // diff
                case 8: // less
                case 9: // invimp
                    if (!bdd0.isZero() && !bdd1.isZero() && !newDom.equals(bdd1.dom)) {
                        out.println("Warning! Or'ing BDD with different domains: "+domainNames(newDom)+" != "+domainNames(bdd1.dom));
                        if (STACK_TRACES)
                            new Exception().printStackTrace(out);
                    }
                    // fallthrough
                case 0: // and
                case 3: // nand
                    newDom.addAll(bdd1.dom);
                    break;
                default:
                    throw new BDDException();
            }
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#apply(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public BDD apply(BDD that, BDDOp opr) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, this, bdd1, opr);
            return new TypedBDD(bdd.apply(bdd1.bdd, opr), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyWith(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public BDD applyWith(BDD that, BDDOp opr) {
            TypedBDD bdd1 = (TypedBDD) that;
            applyHelper(dom, this, bdd1, opr);
            bdd.applyWith(bdd1.bdd, opr);
            return this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyAll(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyAll(BDD that, BDDOp opr, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, this, bdd1, opr);
            TypedBDD bdd2 = (TypedBDD) var;
            if (!newDom.containsAll(bdd2.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd2.dom);
            out.println(domainNames(dom)+" "+opr+" "+domainNames(bdd1.dom)+" / "+domainNames(bdd2.dom)+" = "+domainNames(newDom));
            return new TypedBDD(bdd.applyAll(bdd1.bdd, opr, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDOp opr, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, this, bdd1, opr);
            TypedBDD bdd2 = (TypedBDD) var;
            if (!newDom.containsAll(bdd2.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd2.dom);
            out.println(domainNames(dom)+" "+opr+" "+domainNames(bdd1.dom)+" / "+domainNames(bdd2.dom)+" = "+domainNames(newDom));
            return new TypedBDD(bdd.applyEx(bdd1.bdd, opr, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDOp opr, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, this, bdd1, opr);
            TypedBDD bdd2 = (TypedBDD) var;
            if (!newDom.containsAll(bdd2.dom)) {
                out.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.removeAll(bdd2.dom);
            out.println(domainNames(dom)+" "+opr+" "+domainNames(bdd1.dom)+" / "+domainNames(bdd2.dom)+" = "+domainNames(newDom));
            return new TypedBDD(bdd.applyUni(bdd1.bdd, opr, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOne()
         */
        public BDD satOne() {
            return new TypedBDD(bdd.satOne(), makeSet(dom));
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#fullSatOne()
         */
        public BDD fullSatOne() {
            return new TypedBDD(bdd.fullSatOne(), allDomains());
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satOne(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOne(BDD var, BDD pol) {
            TypedBDD bdd1 = (TypedBDD) var;
            TypedBDD bdd2 = (TypedBDD) pol;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                out.println("Warning! Selecting domain that doesn't exist: "+domainNames(bdd1.dom));
                if (STACK_TRACES)
                    new Exception().printStackTrace(out);
            }
            newDom.addAll(bdd1.dom);
            return new TypedBDD(bdd.satOne(bdd1.bdd, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            return bdd.allsat();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replace(org.sf.javabdd.BDDPairing)
         */
        public BDD replace(BDDPairing pair) {
            TypedBDDPairing tpair = (TypedBDDPairing) pair;
            Set newDom = makeSet();
            newDom.addAll(dom);
            for (Iterator i = tpair.domMap.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();
                BDDDomain d_from = (BDDDomain) e.getKey();
                BDDDomain d_to = (BDDDomain) e.getValue();
                //System.out.println("Replace "+domainNames(dom)+" ("+d_from+"->"+d_to+")");
                if (!dom.contains(d_from)) {
                    out.println("Warning! Replacing domain that doesn't exist: "+d_from.getName());
                    new Exception().printStackTrace();
                }
                if (dom.contains(d_to) && !tpair.domMap.containsKey(d_to)) {
                    out.println("Warning! Overwriting domain that exists: "+d_to.getName());
                    new Exception().printStackTrace();
                }
            }
            newDom.removeAll(tpair.domMap.keySet());
            newDom.addAll(tpair.domMap.values());
            //System.out.println("Result = "+domainNames(newDom));
            return new TypedBDD(bdd.replace(tpair.pairing), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public BDD replaceWith(BDDPairing pair) {
            TypedBDDPairing tpair = (TypedBDDPairing) pair;
            for (Iterator i = tpair.domMap.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();
                BDDDomain d_from = (BDDDomain) e.getKey();
                BDDDomain d_to = (BDDDomain) e.getValue();
                if (!dom.contains(d_from)) {
                    out.println("Warning! Replacing domain that doesn't exist: "+d_from.getName());
                }
                if (dom.contains(d_to) && !tpair.domMap.containsKey(d_to)) {
                    out.println("Warning! Overwriting domain that exists: "+d_to.getName());
                }
            }
            dom.removeAll(tpair.domMap.keySet());
            dom.addAll(tpair.domMap.values());
            bdd.replaceWith(tpair.pairing);
            return this;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#nodeCount()
         */
        public int nodeCount() {
            return bdd.nodeCount();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#pathCount()
         */
        public double pathCount() {
            return bdd.pathCount();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#satCount()
         */
        public double satCount() {
            return bdd.satCount();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#varProfile()
         */
        public int[] varProfile() {
            return bdd.varProfile();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#equals(org.sf.javabdd.BDD)
         */
        public boolean equals(BDD that) {
            TypedBDD bdd1 = (TypedBDD) that;
            if (!dom.containsAll(bdd1.dom)) {
                out.println("Warning! Comparing domain that doesn't exist: "+domainNames(bdd1.dom));
            }
            return bdd.equals(bdd1.bdd);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#hashCode()
         */
        public int hashCode() {
            return bdd.hashCode();
        }

        public Iterator iterator() {
            final BDD b = bdd.id();
            final BDD domains = getDomains();
            final BDD zero = bdd.getFactory().zero();
            return new Iterator() {

                BDD last;
                
                public void remove() {
                    if (last != null) {
                        bdd.applyWith(last.id(), diff);
                        last = null;
                    } else {
                        throw new IllegalStateException();
                    }
                }

                public boolean hasNext() {
                    return !b.isZero();
                }

                public Object next() {
                    BDD c = b.satOne(domains, zero);
                    b.applyWith(c.id(), diff);
                    last = c;
                    Set newDom = makeSet();
                    newDom.addAll(dom);
                    return new TypedBDD(c, newDom);
                }
                
            };
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#free()
         */
        public void free() {
            bdd.free();
            dom.clear();
        }
        
    }
    
    class TypedBDDDomain extends BDDDomain {

        BDDDomain domain;
        
        /**
         * @param index
         * @param range
         */
        protected TypedBDDDomain(BDDDomain domain, int index, long range) {
            super(index, range);
            this.domain = domain;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#getFactory()
         */
        public BDDFactory getFactory() {
            return TypedBDDFactory.this;
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#ithVar(long)
         */
        public BDD ithVar(long val) {
            BDD v = domain.ithVar(val);
            Set s = makeSet();
            s.add(this);
            return new TypedBDD(v, s);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#domain()
         */
        public BDD domain() {
            BDD v = domain.domain();
            Set s = makeSet();
            s.add(this);
            return new TypedBDD(v, s);
        }

        public BDD buildAdd(BDDDomain that, int bits, long value) {
            TypedBDDDomain d = (TypedBDDDomain) that;
            BDD v = domain.buildAdd(d.domain, bits, value);
            Set s = makeSet();
            s.add(this);
            s.add(that);
            return new TypedBDD(v, s);
        }
        
        public BDD buildEquals(BDDDomain that) {
            TypedBDDDomain d = (TypedBDDDomain) that;
            BDD v = domain.buildEquals(d.domain);
            Set s = makeSet();
            s.add(this);
            s.add(that);
            return new TypedBDD(v, s);
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#set()
         */
        public BDD set() {
            BDD v = domain.set();
            Set s = makeSet();
            s.add(this);
            return new TypedBDD(v, s);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDDomain#varRange(long, long)
         */
        public BDD varRange(long lo, long hi) {
            BDD v = domain.varRange(lo, hi);
            Set s = makeSet();
            s.add(this);
            return new TypedBDD(v, s);
        }

    }
    
    class TypedBDDPairing extends BDDPairing {

        final Map domMap;
        final BDDPairing pairing;
        
        TypedBDDPairing(BDDPairing pairing) {
            this.domMap = makeMap();
            this.pairing = pairing;
        }
        
        public void set(BDDDomain p1, BDDDomain p2) {
            if (domMap.containsValue(p2)) {
                out.println("Warning! Set domain that already exists: "+p2.getName());
            }
            domMap.put(p1, p2);
            pairing.set(p1, p2);
        }
        
        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, int)
         */
        public void set(int oldvar, int newvar) {
            throw new BDDException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#set(int, org.sf.javabdd.BDD)
         */
        public void set(int oldvar, BDD newvar) {
            throw new BDDException();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDDPairing#reset()
         */
        public void reset() {
            domMap.clear();
            pairing.reset();
        }
        
    }
}
