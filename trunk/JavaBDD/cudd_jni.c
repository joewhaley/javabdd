#include <jni.h>
#include <stdlib.h>
#include "util.h"
#include "cudd.h"
#include "cuddInt.h"

#include "cudd_jni.h"

/*
** When casting from `int' to a pointer type, you should
** first cast to `intptr_cast_type'.  This is a type
** that is (a) the same size as a pointer, on most platforms,
** to avoid compiler warnings about casts from pointer to int of
** different size; and (b) guaranteed to be at least as big as
** `int'.
*/
#if __STDC_VERSION__ >= 199901
  #include <inttypes.h>
  #if INTPTR_MAX >= INT_MAX
    typedef intptr_t intptr_cast_type;
  #else /* no intptr_t, or intptr_t smaller than `int' */
    typedef intmax_t intptr_cast_type;
  #endif
#else
  #include <stddef.h>
  #include <limits.h>
  #if PTRDIFF_MAX >= INT_MAX
    typedef ptrdiff_t intptr_cast_type;
  #else
    typedef int intptr_cast_type;
  #endif
#endif

static jclass bdd_cls;
static jfieldID bdd_fid;
static jmethodID bdd_mid;
static jfieldID reorder_fid;
static jfieldID op_fid;
//static jfieldID pair_fid;
//static jfieldID domain_fid;

static DdManager *manager;
static jlong bdd_one, bdd_zero;
static int varcount, varnum;

#define TRACE_CUDDLIB

#define INVALID_BDD 0L

static DdNode *BDD_JavaToC(JNIEnv *env, jobject var)
{
  DdNode *bdd;
  bdd = (DdNode*) (intptr_cast_type) (*env)->GetLongField(env, var, bdd_fid);
  return bdd;
}

static jobject BDD_CToJava(JNIEnv *env, DdNode *var)
{
  jobject result = (*env)->NewObject(env, bdd_cls, bdd_mid, (int)var);
  return result;
}

static int BDDOp_JavaToC(JNIEnv *env, jobject method)
{
  jint m;
  m = (*env)->GetIntField(env, method, op_fid);
  return m;
}

/*
static DdManager *Manager_JavaToC(JNIEnv *env, jobject var)
{
	DdManager* manager;
	manager = (DdManager*) (*env)->GetIntField(env, var, manager_fid);
	return manager;
}

static DdManager *Manager_BDDJavaToC(JNIEnv *env, jobject var)
{
	DdManager* manager;
	jobject o = (*env)->GetObjectField(env, var, bddmanager_fid);
	return Manager_JavaToC(env, o);
}
*/

static void die(JNIEnv *env, char* msg)
{
    jclass cls;
    cls = (*env)->FindClass(env, "java/lang/InternalError");
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, msg);
    }
    (*env)->DeleteLocalRef(env, cls);
}

/**** START OF NATIVE METHOD IMPLEMENTATIONS ****/

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    registerNatives
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_CUDDFactory_registerNatives
  (JNIEnv *env, jclass c)
{
  jclass cls;

  cls = (*env)->FindClass(env, "org/sf/javabdd/CUDDFactory$CUDDBDD");
  if (cls != NULL) {
    bdd_cls = (*env)->NewWeakGlobalRef(env, cls);
    bdd_fid = (*env)->GetFieldID(env, cls, "_ddnode_ptr", "J");
    bdd_mid = (*env)->GetMethodID(env, cls, "<init>", "(J)V");
  }

  cls = (*env)->FindClass(env, "org/sf/javabdd/BDDFactory$ReorderMethod");
  if (cls != NULL) {
    reorder_fid = (*env)->GetFieldID(env, cls, "id", "I");
  }
  (*env)->DeleteLocalRef(env, cls);

  cls = (*env)->FindClass(env, "org/sf/javabdd/BDDFactory$BDDOp");
  if (cls != NULL) {
    op_fid = (*env)->GetFieldID(env, cls, "id", "I");
  }
  (*env)->DeleteLocalRef(env, cls);

	/*
  cls = (*env)->FindClass(env, "org/sf/javabdd/CUDDFactory$CUDDBDDPairing");
  if (cls != NULL) {
    pair_fid = (*env)->GetFieldID(env, cls, "_ptr", "J");
  }
  (*env)->DeleteLocalRef(env, cls);

  cls = (*env)->FindClass(env, "org/sf/javabdd/CUDDFactory$CUDDBDDDomain");
  if (cls != NULL) {
    domain_fid = (*env)->GetFieldID(env, cls, "_id", "I");
  }
  (*env)->DeleteLocalRef(env, cls);
     */

  if (!bdd_cls || !bdd_fid || !bdd_mid || !reorder_fid || !op_fid
      /*|| !pair_fid || !domain_fid*/
      ) {
     die(env, "cannot find members: version mismatch?");
     return;
  }
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    makeNode
 * Signature: (ILorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_makeNode
  (JNIEnv *env, jobject o, jint level, jobject low, jobject high)
{
  DdNode* b = BDD_JavaToC(env, low);
  DdNode* c = BDD_JavaToC(env, high);
  DdNode* r = cuddUniqueInter(manager, (int)level, c, b);
  jobject result;
  if (r == NULL) {
    return NULL;
  }
  Cudd_Ref(r);
  result = BDD_CToJava(env, r);
  return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    initialize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_CUDDFactory_initialize
  (JNIEnv *env, jobject o, jint numSlots, jint cacheSize)
{
    jclass cls;
    jfieldID one_fid;
    jfieldID zero_fid;
    
    if (manager != NULL) {
        die(env, "init called twice!");
        return;
    }
    
    //manager = Cudd_Init(nodenum, 0, numSlots, cachesize, 0);
    manager = Cudd_Init(0, 0, numSlots, cacheSize, 0);
    if (manager == NULL) {
        die(env, "unable to initialize CUDD");
        return;
    }

    // we cannot use ReadZero because it returns the arithmetic zero,
    // which is different than logical zero.
    bdd_one  = (jlong) (intptr_cast_type) Cudd_ReadOne(manager);
    bdd_zero = (jlong) (intptr_cast_type) Cudd_Not(Cudd_ReadOne(manager));
    
    Cudd_Ref((DdNode *)(intptr_cast_type) bdd_one);
    Cudd_Ref((DdNode *)(intptr_cast_type) bdd_zero);
    
    cls = (*env)->FindClass(env, "org/sf/javabdd/CUDDFactory");
    one_fid = (*env)->GetFieldID(env, cls, "one", "J");
    zero_fid = (*env)->GetFieldID(env, cls, "zero", "J");
    
    if (!one_fid || !zero_fid) {
        die(env, "cannot find members: version mismatch?");
        return;
    }
    (*env)->SetLongField(env, o, one_fid, bdd_one);
    (*env)->SetLongField(env, o, zero_fid, bdd_zero);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    isInitialized
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_sf_javabdd_CUDDFactory_isInitialized
  (JNIEnv *env, jobject o)
{
    return manager != NULL;
}
  
/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    done0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_CUDDFactory_done0
  (JNIEnv *env, jobject o)
{
    int bdds;
    DdManager* m;

    Cudd_Deref((DdNode *)(intptr_cast_type) bdd_one);
    Cudd_Deref((DdNode *)(intptr_cast_type) bdd_zero);
    
    fprintf(stderr, "Garbage collections: %d  Time spent: %ldms\n",
    	Cudd_ReadGarbageCollections(manager), Cudd_ReadGarbageCollectionTime(manager));
    
    bdds = Cudd_CheckZeroRef(manager);
    if (bdds > 0) fprintf(stderr, "Note: %d BDDs still in memory when terminating\n", bdds);
    m = manager;
    manager = NULL; // race condition with delRef
	Cudd_Quit(m);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    varNum
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_CUDDFactory_varNum
  (JNIEnv *env, jobject o)
{
    return varnum;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    setVarNum
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_CUDDFactory_setVarNum
  (JNIEnv *env, jobject o, jint x)
{
    jint old = varnum;
    varnum = varcount = x;
    return old;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    ithVar
 * Signature: (I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_ithVar
  (JNIEnv *env, jobject o, jint i)
{
  DdNode* d;
  jobject result;
  if (i >= CUDD_MAXINDEX - 1) return NULL;
  d = Cudd_bddIthVar(manager, i);
  result = BDD_CToJava(env, d);
  return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    level2Var
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_CUDDFactory_level2Var
  (JNIEnv *env, jobject o, jint level)
{
	return manager->invperm[level];
}

/*
 * Class:     org_sf_javabdd_CUDDFactory
 * Method:    var2Level
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_CUDDFactory_var2Level
  (JNIEnv *env, jobject o, jint v)
{
	return (jint) cuddI(manager, v);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    isZero
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_isZero
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    d = BDD_JavaToC(env, o);
    //return d == Cudd_Not(DD_ONE(manager));
    return d == (DdNode*)(intptr_cast_type) bdd_zero;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    isOne
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_isOne
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    d = BDD_JavaToC(env, o);
    //return d == DD_ONE(manager);
    return d == (DdNode*)(intptr_cast_type) bdd_one;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    var
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_var
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    d = BDD_JavaToC(env, o);
    return Cudd_Regular(d)->index;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    high
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_high
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    DdNode* res;
    jobject result;
    d = BDD_JavaToC(env, o);
    
    // TODO: check if d is a constant.
    res = Cudd_T(d);
    res = Cudd_NotCond(res, Cudd_IsComplement(d));
    
    result = BDD_CToJava(env, res);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    low
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_low
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    DdNode* res;
    jobject result;
    d = BDD_JavaToC(env, o);
    
    // TODO: check if d is a constant.
    res = Cudd_E(d);
    res = Cudd_NotCond(res, Cudd_IsComplement(d));
    
    result = BDD_CToJava(env, res);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    not
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_not
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    jobject result;
    d = BDD_JavaToC(env, o);
    d = Cudd_Not(d);
    result = BDD_CToJava(env, d);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    ite
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_ite
  (JNIEnv *env, jobject a, jobject b, jobject c)
{
    DdNode* d;
    DdNode* e;
    DdNode* f;
    DdNode* g;
    jobject result;
    d = BDD_JavaToC(env, a);
    e = BDD_JavaToC(env, b);
    f = BDD_JavaToC(env, c);
    g = Cudd_bddIte(manager, d, e, f);
    result = BDD_CToJava(env, g);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    relprod
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_relprod
  (JNIEnv *env, jobject a, jobject b, jobject c)
{
    DdNode* d;
    DdNode* e;
    DdNode* f;
    DdNode* g;
    jobject result;
    d = BDD_JavaToC(env, a);
    e = BDD_JavaToC(env, b);
    f = BDD_JavaToC(env, c);
    g = Cudd_bddAndAbstract(manager, d, e, f);
    result = BDD_CToJava(env, g);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    compose
 * Signature: (Lorg/sf/javabdd/BDD;I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_compose
  (JNIEnv *env, jobject a, jobject b, jint i)
{
    DdNode* d;
    DdNode* e;
    DdNode* f;
    jobject result;
    d = BDD_JavaToC(env, a);
    e = BDD_JavaToC(env, b);
    f = Cudd_bddCompose(manager, d, e, i);
    result = BDD_CToJava(env, f);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    restrict
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_restrict
  (JNIEnv *env, jobject o, jobject p)
{
    DdNode* d;
    DdNode* e;
    DdNode* f;
    jobject result;
    d = BDD_JavaToC(env, o);
    e = BDD_JavaToC(env, p);
    f = Cudd_bddRestrict(manager, d, e);
    result = BDD_CToJava(env, f);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    support
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_support
  (JNIEnv *env, jobject o)
{
    DdNode *f = BDD_JavaToC(env, o);
    DdNode *support;
    jobject result;

    support = Cudd_Support(manager, f);
    if (support == NULL) return NULL;

    result = BDD_CToJava(env, f);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    apply
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_apply
  (JNIEnv *env, jobject o, jobject p, jobject op)
{
    DdNode* d;
    DdNode* e;
    DdNode* f;
    int oper;
    jobject result;
    d = BDD_JavaToC(env, o);
    e = BDD_JavaToC(env, p);
    oper = BDDOp_JavaToC(env, op);
    switch (oper) {
    case 0: /* and */
        f = Cudd_bddAnd(manager, d, e);
        break;
    case 1: /* xor */
        f = Cudd_bddXor(manager, d, e);
        break;
    case 2: /* or */
        f = Cudd_bddOr(manager, d, e);
        break;
    case 3: /* nand */
        f = Cudd_bddNand(manager, d, e);
        break;
    case 4: /* nor */
        f = Cudd_bddNor(manager, d, e);
        break;
    case 5: /* imp */
        d = Cudd_Not(d);
        Cudd_Ref(d);
        f = Cudd_bddOr(manager, d, e);
        Cudd_RecursiveDeref(manager, d);
        break;
    case 6: /* biimp */
        f = Cudd_bddXnor(manager, d, e);
        break;
    case 7: /* diff */
        e = Cudd_Not(e);
        Cudd_Ref(e);
        f = Cudd_bddAnd(manager, d, e);
        Cudd_RecursiveDeref(manager, e);
        break;
    case 8: /* less */
        d = Cudd_Not(d);
        Cudd_Ref(d);
        f = Cudd_bddAnd(manager, d, e);
        Cudd_RecursiveDeref(manager, d);
        break;
    case 9: /* invimp */
        e = Cudd_Not(e);
        Cudd_Ref(e);
        f = Cudd_bddOr(manager, d, e);
        Cudd_RecursiveDeref(manager, e);
        break;
    default:
        die(env, "operation not supported");
        return NULL;
    }
    result = BDD_CToJava(env, f);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    applyWith
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_applyWith
  (JNIEnv *env, jobject o, jobject p, jobject op)
{
    DdNode* d;
    DdNode* e;
    DdNode* f;
    DdNode* g;
    int oper;
    d = BDD_JavaToC(env, o);
    e = BDD_JavaToC(env, p);
    oper = BDDOp_JavaToC(env, op);
    switch (oper) {
    case 0: /* and */
        f = Cudd_bddAnd(manager, d, e);
        break;
    case 1: /* xor */
        f = Cudd_bddXor(manager, d, e);
        break;
    case 2: /* or */
        f = Cudd_bddOr(manager, d, e);
        break;
    case 3: /* nand */
        f = Cudd_bddNand(manager, d, e);
        break;
    case 4: /* nor */
        f = Cudd_bddNor(manager, d, e);
        break;
    case 5: /* imp */
        g = Cudd_Not(d);
        Cudd_Ref(g);
        f = Cudd_bddOr(manager, g, e);
        Cudd_RecursiveDeref(manager, g);
        break;
    case 6: /* biimp */
        f = Cudd_bddXnor(manager, d, e);
        break;
    case 7: /* diff */
        g = Cudd_Not(e);
        Cudd_Ref(g);
        f = Cudd_bddAnd(manager, d, g);
        Cudd_RecursiveDeref(manager, g);
        break;
    case 8: /* less */
        g = Cudd_Not(d);
        Cudd_Ref(g);
        f = Cudd_bddAnd(manager, g, e);
        Cudd_RecursiveDeref(manager, g);
        break;
    case 9: /* invimp */
        g = Cudd_Not(e);
        Cudd_Ref(g);
        f = Cudd_bddOr(manager, d, g);
        Cudd_RecursiveDeref(manager, g);
        break;
    default:
        die(env, "operation not supported");
        return;
    }
    Cudd_Ref(f);
    (*env)->SetLongField(env, p, bdd_fid, INVALID_BDD);
    (*env)->SetLongField(env, o, bdd_fid, (jlong)(intptr_cast_type) f);
    Cudd_RecursiveDeref(manager, e);
    if ((*env)->IsSameObject(env, o, p) == JNI_FALSE) {
        Cudd_RecursiveDeref(manager, d);
    }
}

static DdNode* satone_rec(DdNode* f)
{
    DdNode* zero = (DdNode*)(intptr_cast_type)bdd_zero;
    DdNode* one = (DdNode*)(intptr_cast_type)bdd_one;
    DdNode* F = Cudd_Regular(f);
    DdNode* high;
    DdNode* low;
    DdNode* r;
    unsigned int index;
    
    if (F == zero ||
    	F == one) {
    	return f;
    }
  	
    index = F->index;
    high = cuddT(F);
    low = cuddE(F);
    if (Cudd_IsComplement(f)) {
	high = Cudd_Not(high);
	low = Cudd_Not(low);
    }
    if (low == (DdNode*)(intptr_cast_type)bdd_zero) {
        DdNode* res = satone_rec(high);
        if (res == NULL) {
            return NULL;
  	}
        cuddRef(res);
        if (Cudd_IsComplement(res)) {
            r = cuddUniqueInter(manager, (int)index, Cudd_Not(res), one);
            if (r == NULL) {
                Cudd_IterDerefBdd(manager, res);
                return NULL;
            }
            r = Cudd_Not(r);
        } else {
            r = cuddUniqueInter(manager, (int)index, res, zero);
            if (r == NULL) {
                Cudd_IterDerefBdd(manager, res);
                return NULL;
            }
        }
        cuddDeref(res);
    } else {
        DdNode* res = satone_rec(low);
        if (res == NULL) return NULL;
        cuddRef(res);
        r = cuddUniqueInter(manager, (int)index, one, Cudd_Not(res));
        if (r == NULL) {
            Cudd_IterDerefBdd(manager, res);
            return NULL;
        }
        r = Cudd_Not(r);
        cuddDeref(res);
    }
    
    return r;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    satOne
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_satOne
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    DdNode* res;
    jobject result;
    
    d = BDD_JavaToC(env, o);
    
    do {
        manager->reordered = 0;
        res = satone_rec(d);
    } while (manager->reordered == 1);
    
    result = BDD_CToJava(env, res);
    return result;
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    nodeCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_nodeCount
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    d = BDD_JavaToC(env, o);
    return Cudd_DagSize(d);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    pathCount
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_pathCount
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    d = BDD_JavaToC(env, o);
    return Cudd_CountPathsToNonZero(d);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    satCount
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_satCount
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    d = BDD_JavaToC(env, o);
    return Cudd_CountMinterm(manager, d, varcount);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    addRef
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_addRef
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    //printf("Add: Java object %p\n", o);
    d = BDD_JavaToC(env, o);
    //printf("Add: BDD node %p\n", d);
    Cudd_Ref(d);
}

/*
 * Class:     org_sf_javabdd_CUDDFactory_CUDDBDD
 * Method:    delRef
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_CUDDFactory_00024CUDDBDD_delRef
  (JNIEnv *env, jobject o)
{
    DdNode* d;
    if (manager == NULL) return;
    //printf("Del: Java object %p\n", o);
    d = BDD_JavaToC(env, o);
    //printf("Del: BDD node %p\n", d);
    if (d != INVALID_BDD)
        Cudd_IterDerefBdd(manager, d);
}
