/*
 * Created on Oct 20, 2003
 */
package org.sf.javabdd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author jwhaley
 */
public class TypedBDDFactory extends BDDFactory {

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
        return new TypedBDD(factory.zero(), allDomains());
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

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#ithVar(int)
     */
    public BDD ithVar(int var) {
        // TODO domains?
        return new TypedBDD(factory.ithVar(var), makeSet());
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#nithVar(int)
     */
    public BDD nithVar(int var) {
        // TODO domains?
        return new TypedBDD(factory.nithVar(var), makeSet());
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
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#save(java.lang.String, org.sf.javabdd.BDD)
     */
    public void save(String filename, BDD var) throws IOException {
        // TODO Auto-generated method stub
        factory.save(filename, var);
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
        // TODO Auto-generated method stub
        return 0;
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
        return new TypedBDDDomain(factory.createDomain(a, b), a, b);
    }

    /* (non-Javadoc)
     * @see org.sf.javabdd.BDDFactory#createBitVector(int)
     */
    protected BDDBitVector createBitVector(int a) {
        return factory.createBitVector(a);
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
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
            }
            newDom.removeAll(bdd2.dom);
            return new TypedBDD(bdd.relprod(bdd1.bdd, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#compose(org.sf.javabdd.BDD, int)
         */
        public BDD compose(BDD g, int var) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#veccompose(org.sf.javabdd.BDDPairing)
         */
        public BDD veccompose(BDDPairing pair) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#constrain(org.sf.javabdd.BDD)
         */
        public BDD constrain(BDD that) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#exist(org.sf.javabdd.BDD)
         */
        public BDD exist(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd1.dom));
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
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd1.dom));
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
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd1.dom));
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
                System.err.println("Warning! Restricting domain that doesn't exist: "+domainNames(bdd1.dom));
            }
            if (bdd1.satCount() > 1.0) {
                System.err.println("Warning! Using restrict with more than one value");
            }
            newDom.removeAll(bdd1.dom);
            return new TypedBDD(bdd.restrict(bdd1.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#restrictWith(org.sf.javabdd.BDD)
         */
        public void restrictWith(BDD var) {
            TypedBDD bdd1 = (TypedBDD) var;
            if (!dom.containsAll(bdd1.dom)) {
                System.err.println("Warning! Restricting domain that doesn't exist: "+domainNames(bdd1.dom));
            }
            if (bdd1.satCount() > 1.0) {
                System.err.println("Warning! Using restrict with more than one value");
            }
            dom.removeAll(bdd1.dom);
            bdd.restrictWith(bdd1.bdd);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#simplify(org.sf.javabdd.BDD)
         */
        public BDD simplify(BDD d) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#support()
         */
        public BDD support() {
            // TODO Auto-generated method stub
            return null;
        }

        void applyHelper(Set newDom, Set dom1, BDDOp opr) {
            switch (opr.id) {
                case 1: // xor
                case 2: // or
                case 4: // nor
                case 5: // imp
                case 6: // biimp
                case 7: // diff
                case 8: // less
                case 9: // invimp
                    if (!newDom.equals(dom1)) {
                        System.err.println("Warning! Or'ing BDD with different domains: "+domainNames(dom1));
                    }
                    // fallthrough
                case 0: // and
                case 3: // nand
                    newDom.addAll(dom1);
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
            applyHelper(newDom, bdd1.dom, opr);
            return new TypedBDD(bdd.apply(bdd1.bdd, opr), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyWith(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp)
         */
        public void applyWith(BDD that, BDDOp opr) {
            TypedBDD bdd1 = (TypedBDD) that;
            applyHelper(dom, bdd1.dom, opr);
            bdd.applyWith(bdd1.bdd, opr);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyAll(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyAll(BDD that, BDDOp opr, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, bdd1.dom, opr);
            TypedBDD bdd2 = (TypedBDD) var;
            if (!newDom.containsAll(bdd2.dom)) {
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
            }
            newDom.removeAll(bdd2.dom);
            return new TypedBDD(bdd.applyAll(bdd1.bdd, opr, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyEx(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyEx(BDD that, BDDOp opr, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, bdd1.dom, opr);
            TypedBDD bdd2 = (TypedBDD) var;
            if (!newDom.containsAll(bdd2.dom)) {
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
            }
            newDom.removeAll(bdd2.dom);
            return new TypedBDD(bdd.applyEx(bdd1.bdd, opr, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#applyUni(org.sf.javabdd.BDD, org.sf.javabdd.BDDFactory.BDDOp, org.sf.javabdd.BDD)
         */
        public BDD applyUni(BDD that, BDDOp opr, BDD var) {
            TypedBDD bdd1 = (TypedBDD) that;
            Set newDom = makeSet();
            newDom.addAll(dom);
            applyHelper(newDom, bdd1.dom, opr);
            TypedBDD bdd2 = (TypedBDD) var;
            if (!newDom.containsAll(bdd2.dom)) {
                System.err.println("Warning! Quantifying domain that doesn't exist: "+domainNames(bdd2.dom));
            }
            newDom.removeAll(bdd2.dom);
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
         * @see org.sf.javabdd.BDD#satOneSet(org.sf.javabdd.BDD, org.sf.javabdd.BDD)
         */
        public BDD satOneSet(BDD var, BDD pol) {
            TypedBDD bdd1 = (TypedBDD) var;
            TypedBDD bdd2 = (TypedBDD) pol;
            Set newDom = makeSet();
            newDom.addAll(dom);
            if (!newDom.containsAll(bdd1.dom)) {
                System.err.println("Warning! Selecting domain that doesn't exist: "+domainNames(bdd1.dom));
            }
            newDom.addAll(bdd1.dom);
            return new TypedBDD(bdd.satOneSet(bdd1.bdd, bdd2.bdd), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#allsat()
         */
        public List allsat() {
            // TODO Auto-generated method stub
            return null;
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
                if (!dom.contains(d_from)) {
                    System.err.println("Warning! Replacing domain that doesn't exist: "+d_from.getName());
                }
                if (dom.contains(d_to) && !tpair.domMap.containsKey(d_to)) {
                    System.err.println("Warning! Overwriting domain that exists: "+d_to.getName());
                }
            }
            newDom.removeAll(tpair.domMap.keySet());
            newDom.addAll(tpair.domMap.values());
            return new TypedBDD(bdd.replace(tpair.pairing), newDom);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#replaceWith(org.sf.javabdd.BDDPairing)
         */
        public void replaceWith(BDDPairing pair) {
            TypedBDDPairing tpair = (TypedBDDPairing) pair;
            for (Iterator i = tpair.domMap.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();
                BDDDomain d_from = (BDDDomain) e.getKey();
                BDDDomain d_to = (BDDDomain) e.getValue();
                if (!dom.contains(d_from)) {
                    System.err.println("Warning! Replacing domain that doesn't exist: "+d_from.getName());
                }
                if (dom.contains(d_to) && !tpair.domMap.containsKey(d_to)) {
                    System.err.println("Warning! Overwriting domain that exists: "+d_to.getName());
                }
            }
            dom.removeAll(tpair.domMap.keySet());
            dom.addAll(tpair.domMap.values());
            bdd.replaceWith(tpair.pairing);
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
                System.err.println("Warning! Comparing domain that doesn't exist: "+domainNames(bdd1.dom));
            }
            return bdd.equals(bdd1.bdd);
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#hashCode()
         */
        public int hashCode() {
            return bdd.hashCode();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#addRef()
         */
        protected void addRef() {
            bdd.addRef();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#delRef()
         */
        protected void delRef() {
            bdd.delRef();
        }

        /* (non-Javadoc)
         * @see org.sf.javabdd.BDD#free()
         */
        public void free() {
            bdd.free();
            dom.clear();
        }
        
    }
    
    public class TypedBDDDomain extends BDDDomain {

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
        
    }
    
    public class TypedBDDPairing extends BDDPairing {

        final Map domMap;
        final BDDPairing pairing;
        
        TypedBDDPairing(BDDPairing pairing) {
            this.domMap = makeMap();
            this.pairing = pairing;
        }
        
        public void set(BDDDomain p1, BDDDomain p2) {
            if (domMap.containsValue(p2)) {
                System.err.println("Warning! Set domain that already exists: "+p2.getName());
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
