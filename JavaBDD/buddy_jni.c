#include <jni.h>
#include <bdd.h>
#include <fdd.h>
#include <stdlib.h>
#include "buddy_jni.h"

static jclass bdd_cls;
static jfieldID bdd_fid;
static jmethodID bdd_mid;
static jfieldID reorder_fid;
static jfieldID op_fid;
static jfieldID pair_fid;
static jfieldID domain_fid;

#define INVALID_BDD -1

static int bdd_error;

static void bdd_errhandler(int errcode)
{
  printf("BuDDy error: %s\n", bdd_errstring(errcode));
  bdd_error = errcode;
  bdd_clear_error();
}

static int check_error(JNIEnv *env)
{
  int err = bdd_error;
  char* clsname;
  if (!err) return 0; // fast path
  clsname = NULL;
  switch (err) {
  case BDD_MEMORY:   /* Out of memory */
    clsname = "java/lang/OutOfMemoryError";
    break;
  case BDD_VAR:      /* Unknown variable */
  case BDD_RANGE:    /* Variable value out of range (not in domain) */
  case BDD_DEREF:    /* Removing external reference to unknown node */
  case BDD_RUNNING:  /* Called bdd_init() twice whithout bdd_done() */
  case BDD_ORDER:    /* Vars. not in order for vector based functions */
  case BDD_BREAK:    /* User called break */
  case BDD_VARNUM:  /* Different number of vars. for vector pair */
  case BDD_OP:      /* Unknown operator */
  case BDD_VARSET:  /* Illegal variable set */
  case BDD_VARBLK:  /* Bad variable block operation */
  case BDD_DECVNUM: /* Trying to decrease the number of variables */
  case BDD_REPLACE: /* Replacing to already existing variables */
  case BDD_NODENUM: /* Number of nodes reached user defined maximum */
  case BVEC_SIZE:    /* Mismatch in bitvector size */
  case BVEC_DIVZERO: /* Division by zero */
    clsname = "org/sf/javabdd/BDDException";
    break;
  case BDD_FILE:     /* Some file operation failed */
  case BDD_FORMAT:   /* Incorrect file format */
    clsname = "java/io/IOException";
    break;
  case BDD_NODES:   /* Tried to set max. number of nodes to be fewer */
                    /* than there already has been allocated */
  case BDD_ILLBDD:  /* Illegal bdd argument */
  case BDD_SIZE:    /* Illegal size argument */
  case BVEC_SHIFT:   /* Illegal shift-left/right parameter */
    clsname = "java/lang/IllegalArgumentException";
    break;
  default:
    clsname = "java/lang/InternalError";
    break;
  }
  if (clsname != NULL) {
    jclass cls = (*env)->FindClass(env, clsname);
    (*env)->ThrowNew(env, cls, bdd_errstring(err));
    (*env)->DeleteLocalRef(env, cls);
  }
  bdd_error = 0;
  return err;
}

static BDD BDD_JavaToC(JNIEnv *env, jobject var)
{
  BDD bdd;
  bdd = (*env)->GetIntField(env, var, bdd_fid);
  return bdd;
}

static jobject BDD_CToJava(JNIEnv *env, BDD var)
{
  jobject result = (*env)->NewObject(env, bdd_cls, bdd_mid, var);
  return result;
}

static int ReorderMethod_JavaToC(JNIEnv *env, jobject method)
{
  jint m;
  m = (*env)->GetIntField(env, method, reorder_fid);
  return m;
}

static jobject ReorderMethod_CToJava(JNIEnv *env, int m)
{
  char* fieldName;
  jfieldID fid = NULL;
  jclass cls = (*env)->FindClass(env, "org/sf/javabdd/BDDFactory");
  jobject result = NULL;

  switch (m) {
  case BDD_REORDER_WIN2:
    fieldName = "REORDER_WIN2";
    break;
  case BDD_REORDER_WIN2ITE:
    fieldName = "REORDER_WIN2ITE";
    break;
  case BDD_REORDER_WIN3:
    fieldName = "REORDER_WIN3";
    break;
  case BDD_REORDER_WIN3ITE:
    fieldName = "REORDER_WIN3ITE";
    break;
  case BDD_REORDER_SIFT:
    fieldName = "REORDER_SIFT";
    break;
  case BDD_REORDER_SIFTITE:
    fieldName = "REORDER_SIFTITE";
    break;
  case BDD_REORDER_RANDOM:
    fieldName = "REORDER_RANDOM";
    break;
  case BDD_REORDER_NONE:
  default:
    fieldName = "REORDER_NONE";
    break;
  }
  if (cls != NULL) {
    fid = (*env)->GetStaticFieldID(env, cls, fieldName, "Lorg/sf/javabdd/BDDFactory$ReorderMethod;");
  }
  if (fid != NULL) {
    result = (*env)->GetStaticObjectField(env, cls, fid);
  }
  (*env)->DeleteLocalRef(env, cls);
  return result;
}

static int BDDOp_JavaToC(JNIEnv *env, jobject method)
{
  jint m;
  m = (*env)->GetIntField(env, method, op_fid);
  return m;
}

static bddPair* Pair_JavaToC(JNIEnv *env, jobject pair)
{
  jlong m;
  bddPair* result;
  m = (*env)->GetLongField(env, pair, pair_fid);
  result = (bddPair*) m;
  return result;
}

static int Domain_JavaToC(JNIEnv *env, jobject domain)
{
  jint m;
  m = (*env)->GetIntField(env, domain, domain_fid);
  return m;
}

/**** START OF NATIVE METHOD IMPLEMENTATIONS ****/

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    registerNatives
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_registerNatives
  (JNIEnv *env, jclass c)
{

  jclass cls;

  cls = (*env)->FindClass(env, "org/sf/javabdd/BuDDyFactory$BuDDyBDD");
  if (cls != NULL) {
    bdd_cls = (*env)->NewWeakGlobalRef(env, cls);
    bdd_fid = (*env)->GetFieldID(env, cls, "_id", "I");
    bdd_mid = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
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

  cls = (*env)->FindClass(env, "org/sf/javabdd/BuDDyFactory$BuDDyBDDPairing");
  if (cls != NULL) {
    pair_fid = (*env)->GetFieldID(env, cls, "_ptr", "J");
  }
  (*env)->DeleteLocalRef(env, cls);

  cls = (*env)->FindClass(env, "org/sf/javabdd/BuDDyFactory$BuDDyBDDDomain");
  if (cls != NULL) {
    domain_fid = (*env)->GetFieldID(env, cls, "_id", "I");
  }
  (*env)->DeleteLocalRef(env, cls);

  if (!bdd_cls || !bdd_fid || !bdd_mid || !reorder_fid || !op_fid || !pair_fid || !domain_fid) {
    cls = (*env)->FindClass(env, "java/lang/InternalError");
    if (cls != NULL) {
      (*env)->ThrowNew(env, cls, "cannot find members: version mismatch?");
    }
    (*env)->DeleteLocalRef(env, cls);
  }
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    _getZero
 * Signature: ()Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory__1getZero
  (JNIEnv *env, jobject o)
{
  BDD b = bdd_false();
  jobject result = BDD_CToJava(env, b);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    _getOne
 * Signature: ()Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory__1getOne
  (JNIEnv *env, jobject o)
{
  BDD b = bdd_true();
  jobject result = BDD_CToJava(env, b);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    buildCube
 * Signature: (II[Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_buildCube__II_3Lorg_sf_javabdd_BDD_2
  (JNIEnv *env, jobject o, jint value, jint width, jobjectArray arr)
{
  int i;
  BDD* a;
  BDD r;
  a = (BDD*) malloc(sizeof(BDD) * width);
  if (a == NULL) return NULL;
  
  for (i=0; i<width; ++i) {
    jobject r = (*env)->GetObjectArrayElement(env, arr, i);
    a[i] = BDD_JavaToC(env, r);
  }
  r = bdd_buildcube(value, width, a);
  free(a);
  if (check_error(env)) return NULL;
  return BDD_CToJava(env, r);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    buildCube
 * Signature: (II[I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_buildCube__II_3I
  (JNIEnv *env, jobject o, jint value, jint width, jintArray var)
{
  int* arr;
  BDD b;

  arr = (int*) (*env)->GetPrimitiveArrayCritical(env, var, NULL);
  if (arr == NULL) return NULL;
  b = bdd_ibuildcube(value, width, arr);
  (*env)->ReleasePrimitiveArrayCritical(env, var, arr, 0);

  if (check_error(env)) return NULL;
  return BDD_CToJava(env, b);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    makeSet
 * Signature: ([I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_makeSet
  (JNIEnv *env, jobject o, jintArray v)
{
  int* arr;
  jint n;
  BDD b;

  n = (*env)->GetArrayLength(env, v);
  arr = (int*) (*env)->GetPrimitiveArrayCritical(env, v, NULL);
  if (arr == NULL) return NULL;
  b = bdd_makeset(arr, n);
  (*env)->ReleasePrimitiveArrayCritical(env, v, arr, 0);

  if (check_error(env)) return NULL;
  return BDD_CToJava(env, b);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    initialize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_initialize
  (JNIEnv *env, jobject o, jint nodesize, jint cachesize)
{
  bdd_init(nodesize, cachesize);
  bdd_error_hook(bdd_errhandler);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    isInitialized
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_sf_javabdd_BuDDyFactory_isInitialized
  (JNIEnv *env, jobject o)
{
  return bdd_isrunning();
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    _done
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory__1done
  (JNIEnv *env, jobject o)
{
  bdd_done();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    setMaxNodeNum
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_setMaxNodeNum
  (JNIEnv *env, jobject o, jint size)
{
  int result = bdd_setmaxnodenum(size);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    setMinFreeNodes
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_setMinFreeNodes
  (JNIEnv *env, jobject o, jint n)
{
  bdd_setminfreenodes(n);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    setMaxIncrease
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_setMaxIncrease
  (JNIEnv *env, jobject o, jint size)
{
  int result = bdd_setmaxincrease(size);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    setCacheRatio
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_setCacheRatio
  (JNIEnv *env, jobject o, jint r)
{
  int result = bdd_setcacheratio(r);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    varNum
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_varNum
  (JNIEnv *env, jobject o)
{
  int result = bdd_varnum();
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    setVarNum
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_setVarNum
  (JNIEnv *env, jobject o, jint num)
{
  int result = bdd_setvarnum(num);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    extVarNum
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_extVarNum
  (JNIEnv *env, jobject o, jint num)
{
  int result = bdd_extvarnum(num);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    ithVar
 * Signature: (I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_ithVar
  (JNIEnv *env, jobject o, jint var)
{
  BDD b = bdd_ithvar(var);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    nithVar
 * Signature: (I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_nithVar
  (JNIEnv *env, jobject o, jint var)
{
  BDD b = bdd_nithvar(var);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    swapVar
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_swapVar
  (JNIEnv *env, jobject o, jint v1, jint v2)
{
  bdd_swapvar(v1, v2);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    makePair
 * Signature: ()Lorg/sf/javabdd/BDDPairing;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_makePair
  (JNIEnv *env, jobject o)
{
  jclass cls;
  jobject result = NULL;

  cls = (*env)->FindClass(env, "org/sf/javabdd/BuDDyFactory$BuDDyBDDPairing");
  if (cls != NULL) {
    jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
    if (mid != NULL) {
      bddPair* pair = bdd_newpair();
      jlong param = (jlong) pair;
      result = (*env)->NewObject(env, cls, mid, param);
    }
  }
  (*env)->DeleteLocalRef(env, cls);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    printAll
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_printAll
  (JNIEnv *env, jobject o)
{
  bdd_printall();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    printTable
 * Signature: (Lorg/sf/javabdd/BDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_printTable
  (JNIEnv *env, jobject o, jobject r)
{
  BDD bdd = BDD_JavaToC(env, r);
  bdd_printtable(bdd);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    load
 * Signature: (Ljava/lang/String;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_load
  (JNIEnv *env, jobject o, jstring fname)
{
  BDD r;
  int rc;
  jbyte *str;
  jobject result;
  str = (jbyte*) (*env)->GetStringUTFChars(env, fname, NULL);
  if (str == NULL) return NULL;
  rc = bdd_fnload(str, &r);
  (*env)->ReleaseStringUTFChars(env, fname, str);
  result = BDD_CToJava(env, r);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    save
 * Signature: (Ljava/lang/String;Lorg/sf/javabdd/BDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_save
  (JNIEnv *env, jobject o, jstring fname, jobject bdd)
{
  BDD r;
  int rc;
  jbyte *str;
  r = BDD_JavaToC(env, bdd);
  str = (jbyte*) (*env)->GetStringUTFChars(env, fname, NULL);
  if (str == NULL) return;
  rc = bdd_fnsave(str, r);
  (*env)->ReleaseStringUTFChars(env, fname, str);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    level2Var
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_level2Var
  (JNIEnv *env, jobject o, jint level)
{
  int result = bdd_level2var(level);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    var2level
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_var2level
  (JNIEnv *env, jobject o, jint var)
{
  int result = bdd_var2level(var);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    reorder
 * Signature: (Lorg/sf/javabdd/BDDFactory$ReorderMethod;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_reorder
  (JNIEnv *env, jobject o, jobject method)
{
  jint m = ReorderMethod_JavaToC(env, method);
  bdd_reorder(m);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    autoReorder
 * Signature: (Lorg/sf/javabdd/BDDFactory$ReorderMethod;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_autoReorder__Lorg_sf_javabdd_BDDFactory_00024ReorderMethod_2
  (JNIEnv *env, jobject o, jobject method)
{
  jint m = ReorderMethod_JavaToC(env, method);
  bdd_autoreorder(m);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    autoReorder
 * Signature: (Lorg/sf/javabdd/BDDFactory$ReorderMethod;I)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_autoReorder__Lorg_sf_javabdd_BDDFactory_00024ReorderMethod_2I
  (JNIEnv *env, jobject o, jobject method, jint n)
{
  jint m = ReorderMethod_JavaToC(env, method);
  bdd_autoreorder_times(m, n);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    getReorderMethod
 * Signature: ()Lorg/sf/javabdd/BDDFactory$ReorderMethod;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_getReorderMethod
  (JNIEnv *env, jobject o)
{
  int method = bdd_getreorder_method();
  jobject result = ReorderMethod_CToJava(env, method);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    getReorderTimes
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_getReorderTimes
  (JNIEnv *env, jobject o)
{
  int result = bdd_getreorder_times();
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    disableReorder
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_disableReorder
  (JNIEnv *env, jobject o)
{
  bdd_disable_reorder();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    enableReorder
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_enableReorder
  (JNIEnv *env, jobject o)
{
  bdd_enable_reorder();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    reorderVerbose
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_reorderVerbose
  (JNIEnv *env, jobject o, jint level)
{
  int result = bdd_reorder_verbose(level);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    addVarBlock
 * Signature: (Lorg/sf/javabdd/BDD;Z)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_addVarBlock__Lorg_sf_javabdd_BDD_2Z
  (JNIEnv *env, jobject o, jobject var, jboolean fixed)
{
  BDD b = BDD_JavaToC(env, var);
  bdd_addvarblock(b, fixed);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    addVarBlock
 * Signature: (IIZ)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_addVarBlock__IIZ
  (JNIEnv *env, jobject o, jint first, jint last, jboolean fixed)
{
  bdd_intaddvarblock(first, last, fixed);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    varBlockAll
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_varBlockAll
  (JNIEnv *env, jobject o)
{
  bdd_varblockall();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    clearVarBlocks
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_clearVarBlocks
  (JNIEnv *env, jobject o)
{
  bdd_clrvarblocks();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    printOrder
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_printOrder
  (JNIEnv *env, jobject o)
{
  bdd_printorder();
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    nodeCount
 * Signature: (Ljava/util/Collection;)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_nodeCount
  (JNIEnv *env, jobject o, jobject r)
{
  BDD b = BDD_JavaToC(env, r);
  int result = bdd_nodecount(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    getAllocNum
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_getAllocNum
  (JNIEnv *env, jobject o)
{
  int result = bdd_getallocnum();
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    getNodeNum
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_getNodeNum
  (JNIEnv *env, jobject o)
{
  int result = bdd_getnodenum();
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    reorderGain
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_reorderGain
  (JNIEnv *env, jobject o)
{
  int result = bdd_reorder_gain();
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    printStat
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_printStat
  (JNIEnv *env, jobject o)
{
  bdd_printstat();
  check_error(env);
}

/* class org_sf_javabdd_BuDDyFactory_BuDDyBDD */

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    var
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_var
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  int result = bdd_var(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    high
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_high
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, bdd_high(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    low
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_low
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, bdd_low(b));
  check_error(env);
  return result;}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    id
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_id
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    not
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_not
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, bdd_not(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    ite
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_ite
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  BDD d = BDD_JavaToC(env, that2);
  jobject result = BDD_CToJava(env, bdd_ite(b, c, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    relprod
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_relprod
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  BDD d = BDD_JavaToC(env, that2);
  jobject result = BDD_CToJava(env, bdd_ite(b, c, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    compose
 * Signature: (Lorg/sf/javabdd/BDD;I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_compose
  (JNIEnv *env, jobject o, jobject that, jint v)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_compose(b, c, v));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    constrain
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_constrain
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_constrain(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    exist
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_exist
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_exist(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    forAll
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_forAll
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_forall(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    unique
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_unique
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_unique(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    restrict
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_restrict
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_restrict(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    simplify
 * Signature: (Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_simplify
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  jobject result = BDD_CToJava(env, bdd_simplify(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    support
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_support
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, bdd_support(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    apply
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_apply
  (JNIEnv *env, jobject o, jobject that, jobject op)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  int operation = BDDOp_JavaToC(env, op);
  jobject result = BDD_CToJava(env, bdd_apply(b, c, operation));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyWith
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyWith
  (JNIEnv *env, jobject o, jobject that, jobject op)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = bdd_apply(b, c, operation);
  if (check_error(env)) return;
  bdd_addref(d);
  (*env)->SetIntField(env, that, bdd_fid, INVALID_BDD);
  (*env)->SetIntField(env, o, bdd_fid, d);
  bdd_delref(b);
  if ((*env)->IsSameObject(env, o, that) == JNI_FALSE) {
    bdd_delref(c);
  }
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyAll
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyAll
  (JNIEnv *env, jobject o, jobject that1, jobject op, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = BDD_JavaToC(env, that2);
  jobject result = BDD_CToJava(env, bdd_appall(b, c, operation, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyEx
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyEx
  (JNIEnv *env, jobject o, jobject that1, jobject op, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = BDD_JavaToC(env, that2);
  jobject result = BDD_CToJava(env, bdd_appex(b, c, operation, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyUni
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDDFactory$BDDOp;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyUni
  (JNIEnv *env, jobject o, jobject that1, jobject op, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = BDD_JavaToC(env, that2);
  jobject result = BDD_CToJava(env, bdd_appuni(b, c, operation, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satOne
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satOne
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, bdd_satone(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    fullSatOne
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_fullSatOne
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  jobject result = BDD_CToJava(env, bdd_fullsatone(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satOneSet
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satOneSet
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  BDD d = BDD_JavaToC(env, that2);
  jobject result = BDD_CToJava(env, bdd_satoneset(b, c, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    printSet
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_printSet
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  bdd_printset(b);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    printDot
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_printDot
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  bdd_printdot(b);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    nodeCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_nodeCount
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  int result = bdd_nodecount(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    pathCount
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_pathCount
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  double result = bdd_pathcount(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satCount
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satCount__
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  double result = bdd_satcount(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satCount
 * Signature: (Lorg/sf/javabdd/BDD;)D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satCount__Lorg_sf_javabdd_BDD_2
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  double result = bdd_satcountset(b, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    logSatCount
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_logSatCount__
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  double result = bdd_satcountln(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    logSatCount
 * Signature: (Lorg/sf/javabdd/BDD;)D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_logSatCount__Lorg_sf_javabdd_BDD_2
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  double result = bdd_satcountlnset(b, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    addRef
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_addRef
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  bdd_addref(b);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    delRef
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_delRef
  (JNIEnv *env, jobject o)
{
  BDD bdd;
  bdd = (*env)->GetIntField(env, o, bdd_fid);
  (*env)->SetIntField(env, o, bdd_fid, INVALID_BDD);
  if (bdd != INVALID_BDD)
    bdd_delref(bdd);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    veccompose
 * Signature: (Lorg/sf/javabdd/BDDPairing;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_veccompose
  (JNIEnv *env, jobject o, jobject pair)
{
  BDD b = BDD_JavaToC(env, o);
  bddPair* p = Pair_JavaToC(env, pair);
  BDD c = bdd_veccompose(b, p);
  jobject result = BDD_CToJava(env, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    scanSet
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_scanSet
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  int size;
  int *arr;
  jintArray result;

  bdd_scanset(b, &arr, &size);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;
  result = (*env)->NewIntArray(env, size);
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*) arr);
  free(arr);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    scanVar
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_scanVar
  (JNIEnv *env, jobject o, jint i)
{
  BDD b = BDD_JavaToC(env, o);
  jint result = fdd_scanvar(b, i);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    scanAllVar
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_scanAllVar
  (JNIEnv *env, jobject o)
{
  jintArray result;
  BDD b = BDD_JavaToC(env, o);
  jint size = fdd_domainnum();
  int* arr = fdd_scanallvar(b);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;

  result = (*env)->NewIntArray(env, size);
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*) arr);
  free(arr);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    replace
 * Signature: (Lorg/sf/javabdd/BDDPairing;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_replace
  (JNIEnv *env, jobject o, jobject pair)
{
  BDD b = BDD_JavaToC(env, o);
  bddPair* p = Pair_JavaToC(env, pair);
  BDD c = bdd_replace(b, p);
  jobject result = BDD_CToJava(env, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    printSetWithDomains
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_printSetWithDomains
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  fdd_printset(b);
  check_error(env);
}

/* class org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain */

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    domain
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_domain
  (JNIEnv *env, jobject o)
{
  int domain = Domain_JavaToC(env, o);
  BDD b = fdd_domain(domain);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_size
  (JNIEnv *env, jobject o)
{
  int domain = Domain_JavaToC(env, o);
  jint result = fdd_domainsize(domain);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    buildEquals
 * Signature: (Lorg/sf/javabdd/BDDDomain;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_buildEquals
  (JNIEnv *env, jobject o, jobject that)
{
  int d1 = Domain_JavaToC(env, o);
  int d2 = Domain_JavaToC(env, that);
  BDD b = fdd_equals(d1, d2);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    set
 * Signature: ()Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_set
  (JNIEnv *env, jobject o)
{
  int domain = Domain_JavaToC(env, o);
  BDD b = fdd_ithset(domain);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    ithVar
 * Signature: (I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_ithVar
  (JNIEnv *env, jobject o, jint i)
{
  int domain = Domain_JavaToC(env, o);
  BDD b = fdd_ithvar(domain, i);
  jobject result = BDD_CToJava(env, b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    varNum
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_varNum
  (JNIEnv *env, jobject o)
{
  int domain = Domain_JavaToC(env, o);
  jint result = fdd_varnum(domain);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    vars
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_vars
  (JNIEnv *env, jobject o)
{
  jintArray result;
  int domain = Domain_JavaToC(env, o);
  jint size = fdd_domainsize(domain);
  int* arr = fdd_vars(domain);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;

  result = (*env)->NewIntArray(env, size);
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*)arr);
  free(arr);
  return result;
}

/* class org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing */

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set__II
  (JNIEnv *env, jobject o, jint i, jint j)
{
  bddPair* p = Pair_JavaToC(env, o);
  bdd_setpair(p, i, j);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set
 * Signature: ([I[I)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set___3I_3I
  (JNIEnv *env, jobject o, jintArray arr1, jintArray arr2)
{
  bddPair* p = Pair_JavaToC(env, o);
  jint *a1;
  jint *a2;
  jint size1 = (*env)->GetArrayLength(env, arr1);
  jint size2 = (*env)->GetArrayLength(env, arr2);
  if (size1 != size2) {
    jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    (*env)->ThrowNew(env, cls, "array sizes do not match");
    (*env)->DeleteLocalRef(env, cls);
    return;
  }
  a1 = (*env)->GetPrimitiveArrayCritical(env, arr1, NULL);
  if (a1 == NULL) return;
  a2 = (*env)->GetPrimitiveArrayCritical(env, arr2, NULL);
  if (a2 == NULL) return;
  bdd_setpairs(p, (int*)a1, (int*)a2, size1);
  (*env)->ReleasePrimitiveArrayCritical(env, arr1, a1, 0);
  (*env)->ReleasePrimitiveArrayCritical(env, arr2, a2, 0);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set
 * Signature: (Lorg/sf/javabdd/BDD;Lorg/sf/javabdd/BDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set__Lorg_sf_javabdd_BDD_2Lorg_sf_javabdd_BDD_2
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  bddPair* p = Pair_JavaToC(env, o);
  BDD b = BDD_JavaToC(env, that1);
  BDD c = BDD_JavaToC(env, that2);
  bdd_setbddpair(p, b, c);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set
 * Signature: ([Lorg/sf/javabdd/BDD;[Lorg/sf/javabdd/BDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set___3Lorg_sf_javabdd_BDD_2_3Lorg_sf_javabdd_BDD_2
  (JNIEnv *env, jobject o, jobjectArray arr1, jobjectArray arr2)
{
  bddPair* p = Pair_JavaToC(env, o);
  bdd *a1;
  bdd *a2;
  jint size1 = (*env)->GetArrayLength(env, arr1);
  jint size2 = (*env)->GetArrayLength(env, arr2);
  int i;
  if (size1 != size2) {
    jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    (*env)->ThrowNew(env, cls, "array sizes do not match");
    (*env)->DeleteLocalRef(env, cls);
    return;
  }
  a1 = (bdd*) malloc(size1 * sizeof(bdd*));
  a2 = (bdd*) malloc(size1 * sizeof(bdd*));
  for (i=0; i<size1; ++i) {
    jobject obj1 = (*env)->GetObjectArrayElement(env, arr1, i);
    jobject obj2 = (*env)->GetObjectArrayElement(env, arr2, i);
    a1[i] = BDD_JavaToC(env, obj1);
    a2[i] = BDD_JavaToC(env, obj2);
    (*env)->DeleteLocalRef(env, obj1);
    (*env)->DeleteLocalRef(env, obj2);
  }
  bdd_setbddpairs(p, a1, a2, size1);
  free(a1);
  free(a2);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set
 * Signature: (Lorg/sf/javabdd/BDDDomain;Lorg/sf/javabdd/BDDDomain;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set__Lorg_sf_javabdd_BDDDomain_2Lorg_sf_javabdd_BDDDomain_2
  (JNIEnv *env, jobject o, jobject dom1, jobject dom2)
{
  bddPair* p = Pair_JavaToC(env, o);
  int b = Domain_JavaToC(env, dom1);
  int c = Domain_JavaToC(env, dom2);
  fdd_setpair(p, b, c);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set
 * Signature: ([Lorg/sf/javabdd/BDDDomain;[Lorg/sf/javabdd/BDDDomain;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set___3Lorg_sf_javabdd_BDDDomain_2_3Lorg_sf_javabdd_BDDDomain_2
  (JNIEnv *env, jobject o, jobjectArray arr1, jobjectArray arr2)
{
  bddPair* p = Pair_JavaToC(env, o);
  int *a1;
  int *a2;
  jint size1 = (*env)->GetArrayLength(env, arr1);
  jint size2 = (*env)->GetArrayLength(env, arr2);
  int i;
  if (size1 != size2) {
    jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    (*env)->ThrowNew(env, cls, "array sizes do not match");
    (*env)->DeleteLocalRef(env, cls);
    return;
  }
  a1 = (int*) malloc(size1 * sizeof(int*));
  a2 = (int*) malloc(size1 * sizeof(int*));
  for (i=0; i<size1; ++i) {
    jobject obj1 = (*env)->GetObjectArrayElement(env, arr1, i);
    jobject obj2 = (*env)->GetObjectArrayElement(env, arr2, i);
    a1[i] = Domain_JavaToC(env, obj1);
    a2[i] = Domain_JavaToC(env, obj2);
    (*env)->DeleteLocalRef(env, obj1);
    (*env)->DeleteLocalRef(env, obj2);
  }
  fdd_setpairs(p, a1, a2, size1);
  free(a1);
  free(a2);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_reset
  (JNIEnv *env, jobject o)
{
  bddPair* p = Pair_JavaToC(env, o);
  bdd_resetpair(p);
  check_error(env);
}
