#include <jni.h>
#include <bdd.h>
#include <fdd.h>
#include <stdlib.h>
#include "buddy_jni.h"

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
static jfieldID pair_fid;
static jfieldID domain_fid;

#define INVALID_BDD -1

//#define TRACE_BUDDYLIB

static int bdd_error;

static void bdd_errhandler(int errcode)
{
#if defined(TRACE_BUDDYLIB)
  printf("bdd_errstring(%d)\n", errcode);
#endif
  printf("BuDDy error: %s\n", bdd_errstring(errcode));
  bdd_error = errcode;
#if defined(TRACE_BUDDYLIB)
  printf("bdd_clear_error()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
    printf("bdd_errstring(%d)\n", err);
#endif
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
  result = (bddPair*) (intptr_cast_type) m;
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

extern int    bdd_makenode(unsigned int, int, int);
/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    makeNode0
 * Signature: (ILorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_makeNode0
  (JNIEnv *env, jobject o, jint level, jobject low, jobject high)
{
  BDD b = BDD_JavaToC(env, low);
  BDD c = BDD_JavaToC(env, high);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_makenode(%d, %d, %d)\n", level, b, c);
#endif
  jobject result = BDD_CToJava(env, bdd_makenode(level, b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    buildCube0
 * Signature: (I[Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_buildCube0
  (JNIEnv *env, jobject o, jint value, jobjectArray arr)
{
  int i;
  jint width;
  BDD* a;
  BDD r;
  width = (*env)->GetArrayLength(env, arr);
  
  a = (BDD*) malloc(sizeof(BDD) * width);
  if (a == NULL) return NULL;
  
  for (i=0; i<width; ++i) {
    jobject r = (*env)->GetObjectArrayElement(env, arr, i);
    a[i] = BDD_JavaToC(env, r);
  }
#if defined(TRACE_BUDDYLIB)
  printf("bdd_buildcube(%d, %d, %p)\n", value, width, a);
#endif
  r = bdd_buildcube(value, width, a);
  free(a);
  if (check_error(env)) return NULL;
  return BDD_CToJava(env, r);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    buildCube1
 * Signature: (I[I)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_buildCube1
  (JNIEnv *env, jobject o, jint value, jintArray var)
{
  int* arr;
  BDD b;
  jint width;

  width = (*env)->GetArrayLength(env, var);
  arr = (int*) (*env)->GetPrimitiveArrayCritical(env, var, NULL);
  if (arr == NULL) return NULL;
#if defined(TRACE_BUDDYLIB)
  printf("bdd_ibuildcube(%d, %d, %p)\n", value, width, arr);
#endif
  b = bdd_ibuildcube(value, width, arr);
  (*env)->ReleasePrimitiveArrayCritical(env, var, arr, JNI_ABORT);

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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_makeset(%p, %d)\n", arr, n);
#endif
  b = bdd_makeset(arr, n);
  (*env)->ReleasePrimitiveArrayCritical(env, v, arr, JNI_ABORT);

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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_init(%d, %d)\n", nodesize, cachesize);
#endif
  bdd_init(nodesize, cachesize);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_error_hook(%p)\n", bdd_errhandler);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_isrunning()\n");
#endif
  return bdd_isrunning();
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    done0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_done0
  (JNIEnv *env, jobject o)
{
#if defined(TRACE_BUDDYLIB)
  printf("bdd_done()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setmaxnodenum(%d)\n", size);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setminfreenodes(%d)\n", n);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setmaxincrease(%d)\n", size);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setcacheratio(%d)\n", r);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_varnum()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setvarnum(%d)\n", num);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_extvarnum(%d)\n", num);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_ithvar(%d)\n", var);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_nithvar(%d)\n", var);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_swapvar(%d, %d)\n", v1, v2);
#endif
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
    jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "(J)V");
    if (mid != NULL) {
#if defined(TRACE_BUDDYLIB)
      printf("bdd_newpair()\n");
#endif
      bddPair* pair = bdd_newpair();
      jlong param = (jlong) (intptr_cast_type) pair;
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_printall()\n");
#endif
  bdd_printall();
  fflush(stdout);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    printTable0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_printTable0
  (JNIEnv *env, jobject o, jobject r)
{
  BDD bdd = BDD_JavaToC(env, r);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_printtable(%d)\n", bdd);
#endif
  bdd_printtable(bdd);
  fflush(stdout);
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_fnload(%s, %p)\n", str, &r);
#endif
  rc = bdd_fnload(str, &r);
  (*env)->ReleaseStringUTFChars(env, fname, str);
  result = BDD_CToJava(env, r);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    save0
 * Signature: (Ljava/lang/String;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_save0
  (JNIEnv *env, jobject o, jstring fname, jobject bdd)
{
  BDD r;
  int rc;
  jbyte *str;
  r = BDD_JavaToC(env, bdd);
  str = (jbyte*) (*env)->GetStringUTFChars(env, fname, NULL);
  if (str == NULL) return;
#if defined(TRACE_BUDDYLIB)
  printf("bdd_fnsave(%s, %d)\n", str, r);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_level2var(%d)\n", level);
#endif
  int result = bdd_level2var(level);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    var2Level
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_var2Level
  (JNIEnv *env, jobject o, jint var)
{
#if defined(TRACE_BUDDYLIB)
  printf("bdd_var2level(%d)\n", var);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_reorder(%d)\n", m);
#endif
  bdd_reorder(m);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    autoReorder0
 * Signature: (Lorg/sf/javabdd/BDDFactory$ReorderMethod;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_autoReorder0
  (JNIEnv *env, jobject o, jobject method)
{
  jint m = ReorderMethod_JavaToC(env, method);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_autoreorder(%d)\n", m);
#endif
  bdd_autoreorder(m);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    autoReorder1
 * Signature: (Lorg/sf/javabdd/BDDFactory$ReorderMethod;I)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_autoReorder1
  (JNIEnv *env, jobject o, jobject method, jint n)
{
  jint m = ReorderMethod_JavaToC(env, method);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_autoreorder_times(%d, %d)\n", m, n);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_getreorder_method()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_getreorder_times()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_disable_reorder()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_enable_reorder()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_reorder_verbose(%d)\n", level);
#endif
  int result = bdd_reorder_verbose(level);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    setVarOrder
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_setVarOrder
  (JNIEnv *env, jobject o, jintArray arr)
{
  jint *a;
  jint size = (*env)->GetArrayLength(env, arr);
  jint varnum = bdd_varnum();
  if (size != varnum) {
    jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    (*env)->ThrowNew(env, cls, "array size != number of vars");
    (*env)->DeleteLocalRef(env, cls);
    return;
  }
  a = (*env)->GetPrimitiveArrayCritical(env, arr, NULL);
  if (a == NULL) return;
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setvarorder(%p)\n", a);
#endif
  bdd_setvarorder((int*)a);
  (*env)->ReleasePrimitiveArrayCritical(env, arr, a, JNI_ABORT);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    addVarBlock0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Z)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_addVarBlock0
  (JNIEnv *env, jobject o, jobject var, jboolean fixed)
{
  BDD b = BDD_JavaToC(env, var);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_addvarblock(%d, %d)\n", b, fixed);
#endif
  bdd_addvarblock(b, fixed);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    addVarBlock1
 * Signature: (IIZ)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_addVarBlock1
  (JNIEnv *env, jobject o, jint first, jint last, jboolean fixed)
{
#if defined(TRACE_BUDDYLIB)
  printf("bdd_intaddvarblock(%d, %d, %d)\n", first, last, fixed);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_varblockall()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_clrvarblocks()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_printorder()\n");
#endif
  bdd_printorder();
  fflush(stdout);
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_nodecount(%d)\n", b);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_getallocnum()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_getnodenum()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_reorder_gain()\n");
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_printstat()\n");
#endif
  bdd_printstat();
  fflush(stdout);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    extDomain
 * Signature: ([I)[Lorg/sf/javabdd/BDDDomain;
 */
JNIEXPORT jobjectArray JNICALL Java_org_sf_javabdd_BuDDyFactory_extDomain
  (JNIEnv *env, jobject o, jintArray arr)
{
  int i, domnum;
  jint *a;
  jobjectArray result;
  jclass cls;
  jmethodID mid;
  jint size = (*env)->GetArrayLength(env, arr);
  a = (*env)->GetPrimitiveArrayCritical(env, arr, NULL);
#if defined(TRACE_BUDDYLIB)
  printf("fdd_extdomain(%p, %d)\n", a, size);
#endif
  domnum = fdd_extdomain((int*)a, size);
  (*env)->ReleasePrimitiveArrayCritical(env, arr, a, JNI_ABORT);
  if (check_error(env)) return NULL;

  cls = (*env)->FindClass(env, "org/sf/javabdd/BuDDyFactory$BuDDyBDDDomain");
  if (cls == NULL) return NULL;
  mid = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
  if (mid == NULL) return NULL;
  result = (*env)->NewObjectArray(env, size, cls, NULL);
  if (result == NULL) return NULL;
  for (i=0; i<size; ++i) {
    jobject obj = (*env)->NewObject(env, cls, mid, domnum+i);
    if (obj == NULL) return NULL;
    (*env)->SetObjectArrayElement(env, result, i, obj);
    (*env)->DeleteLocalRef(env, obj);
  }
  (*env)->DeleteLocalRef(env, cls);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    overlapDomain
 * Signature: (Lorg/sf/javabdd/BDDDomain;Lorg/sf/javabdd/BDDDomain;)Lorg/sf/javabdd/BDDDomain;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_overlapDomain
  (JNIEnv *env, jobject o, jobject dom1, jobject dom2)
{
  jclass cls;
  jmethodID mid;
  jobject result;
  int d1 = Domain_JavaToC(env, dom1);
  int d2 = Domain_JavaToC(env, dom2);
#if defined(TRACE_BUDDYLIB)
  printf("fdd_overlapdomain(%d, %d)\n", d1, d2);
#endif
  int d3 = fdd_overlapdomain(d1, d2);
  if (check_error(env)) return NULL;
  cls = (*env)->FindClass(env, "org/sf/javabdd/BuDDyFactory$BuDDyBDDDomain");
  if (cls == NULL) return NULL;
  mid = (*env)->GetMethodID(env, cls, "<init>", "(I)V");
  if (mid == NULL) return NULL;
  result = (*env)->NewObject(env, cls, mid, d3);
  (*env)->DeleteLocalRef(env, cls);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    makeSet
 * Signature: ([Lorg/sf/javabdd/BDDDomain;)Lorg/sf/javabdd/BDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_makeSet___3Lorg_sf_javabdd_BDDDomain_2
  (JNIEnv *env, jobject o, jobjectArray arr)
{
  int *a;
  jint size = (*env)->GetArrayLength(env, arr);
  int i;
  bdd b;
  jobject result;
  a = (int*) malloc(size * sizeof(int*));
  for (i=0; i<size; ++i) {
    jobject obj = (*env)->GetObjectArrayElement(env, arr, i);
    a[i] = Domain_JavaToC(env, obj);
    (*env)->DeleteLocalRef(env, obj);
  }
#if defined(TRACE_BUDDYLIB)
  printf("fdd_makeset(%p, %d)\n", a, size);
#endif
  b = fdd_makeset(a, size);
  free(a);
  if (check_error(env)) return NULL;
  result = BDD_CToJava(env, b);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    clearAllDomains
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_clearAllDomains
  (JNIEnv *env, jobject o)
{
#if defined(TRACE_BUDDYLIB)
  printf("fdd_clearall()\n");
#endif
  fdd_clearall();
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory
 * Method:    numberOfDomains
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_numberOfDomains
  (JNIEnv *env, jobject o)
{
#if defined(TRACE_BUDDYLIB)
  printf("fdd_domainnum()\n");
#endif
  jint result = fdd_domainnum();
  return result;
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_var(%d)\n", b);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_high(%d)\n", b);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_low(%d)\n", b);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_not(%d)\n", b);
#endif
  jobject result = BDD_CToJava(env, bdd_not(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    ite0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_ite0
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  BDD d = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_ite(%d, %d, %d)\n", b, c, d);
#endif
  jobject result = BDD_CToJava(env, bdd_ite(b, c, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    relprod0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_relprod0
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  BDD d = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_relprod(%d, %d, %d)\n", b, c, d);
#endif
  jobject result = BDD_CToJava(env, bdd_relprod(b, c, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    compose0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;I)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_compose0
  (JNIEnv *env, jobject o, jobject that, jint v)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_compose(%d, %d, %d)\n", b, c, v);
#endif
  jobject result = BDD_CToJava(env, bdd_compose(b, c, v));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    constrain0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_constrain0
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_constrain(%d, %d)\n", b, c);
#endif
  jobject result = BDD_CToJava(env, bdd_constrain(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    exist0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_exist0
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_exist(%d, %d)\n", b, c);
#endif
  jobject result = BDD_CToJava(env, bdd_exist(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    forAll0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_forAll0
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_forall(%d, %d)\n", b, c);
#endif
  jobject result = BDD_CToJava(env, bdd_forall(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    unique0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_unique0
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_unique(%d, %d)\n", b, c);
#endif
  jobject result = BDD_CToJava(env, bdd_unique(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    restrict0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_restrict0
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_restrict(%d, %d)\n", b, c);
#endif
  jobject result = BDD_CToJava(env, bdd_restrict(b, c));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    simplify0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_simplify0
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_simplify(%d, %d)\n", b, c);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_support(%d)\n", b);
#endif
  jobject result = BDD_CToJava(env, bdd_support(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    apply0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BDDFactory$BDDOp;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_apply0
  (JNIEnv *env, jobject o, jobject that, jobject op)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  int operation = BDDOp_JavaToC(env, op);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_apply(%d, %d, %d)\n", b, c, operation);
#endif
  jobject result = BDD_CToJava(env, bdd_apply(b, c, operation));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyWith0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BDDFactory$BDDOp;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyWith0
  (JNIEnv *env, jobject o, jobject that, jobject op)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
  int operation = BDDOp_JavaToC(env, op);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_apply(%d, %d, %d)\n", b, c, operation);
#endif
  BDD d = bdd_apply(b, c, operation);
  if (check_error(env)) return;
#if defined(TRACE_BUDDYLIB)
  printf("bdd_addref(%d)\n", d);
#endif
  bdd_addref(d);
  (*env)->SetIntField(env, that, bdd_fid, INVALID_BDD);
  (*env)->SetIntField(env, o, bdd_fid, d);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_delref(%d)\n", b);
#endif
  bdd_delref(b);
  if ((*env)->IsSameObject(env, o, that) == JNI_FALSE) {
#if defined(TRACE_BUDDYLIB)
    printf("bdd_delref(%d)\n", c);
#endif
    bdd_delref(c);
  }
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyAll0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BDDFactory$BDDOp;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyAll0
  (JNIEnv *env, jobject o, jobject that1, jobject op, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_appall(%d, %d, %d, %d)\n", b, c, operation, d);
#endif
  jobject result = BDD_CToJava(env, bdd_appall(b, c, operation, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyEx0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BDDFactory$BDDOp;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyEx0
  (JNIEnv *env, jobject o, jobject that1, jobject op, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_appex(%d, %d, %d, %d)\n", b, c, operation, d);
#endif
  jobject result = BDD_CToJava(env, bdd_appex(b, c, operation, d));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    applyUni0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BDDFactory$BDDOp;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_applyUni0
  (JNIEnv *env, jobject o, jobject that1, jobject op, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  int operation = BDDOp_JavaToC(env, op);
  BDD d = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_appuni(%d, %d, %d, %d)\n", b, c, operation, d);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_satone(%d)\n", b);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_fullsatone(%d)\n", b);
#endif
  jobject result = BDD_CToJava(env, bdd_fullsatone(b));
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satOneSet0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satOneSet0
  (JNIEnv *env, jobject o, jobject that1, jobject that2)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that1);
  BDD d = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_satoneset(%d, %d, %d)\n", b, c, d);
#endif
  jobject result = BDD_CToJava(env, bdd_satoneset(b, c, d));
  check_error(env);
  return result;
}

static JNIEnv *allsat_env;
static jobjectArray allsat_result;
static int allsat_index;
static void allsatHandler(char* varset, int size)
{
  jbyteArray result = (*allsat_env)->NewByteArray(allsat_env, size);
  (*allsat_env)->SetByteArrayRegion(allsat_env, result, 0, size, (jbyte*) varset);
  (*allsat_env)->SetObjectArrayElement(allsat_env, allsat_result, allsat_index, result);
  (*allsat_env)->DeleteLocalRef(allsat_env, result);
  allsat_index++;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    allsat0
 * Signature: ()[[B
 */
JNIEXPORT jobjectArray JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_allsat0
  (JNIEnv *env, jobject o)
{
  jobjectArray result;
  BDD b = BDD_JavaToC(env, o);
  jclass c = (*env)->FindClass(env, "[B");
#if defined(TRACE_BUDDYLIB)
  printf("bdd_varnum()\n");
#endif
  int size = bdd_varnum();
  check_error(env);
  allsat_result = (*env)->NewObjectArray(env, size, c, NULL);
  if (allsat_result == NULL) return NULL;
  allsat_env = env;
  allsat_index = 0;
#if defined(TRACE_BUDDYLIB)
  printf("bdd_allsat(%d, %p)\n", b, allsatHandler);
#endif
  bdd_allsat(b, allsatHandler);
  allsat_env = NULL;
  result = allsat_result;
  allsat_result = NULL;
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_printset(%d)\n", b);
#endif
  bdd_printset(b);
  fflush(stdout);
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_printdot(%d)\n", b);
#endif
  bdd_printdot(b);
  fflush(stdout);
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_nodecount(%d)\n", b);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_pathcount(%d)\n", b);
#endif
  double result = bdd_pathcount(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satCount0
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satCount0
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_satcount(%d)\n", b);
#endif
  double result = bdd_satcount(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    satCount1
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_satCount1
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_satcountset(%d, %d)\n", b, c);
#endif
  double result = bdd_satcountset(b, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    logSatCount0
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_logSatCount0
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_satcountln(%d)\n", b);
#endif
  double result = bdd_satcountln(b);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    logSatCount1
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)D
 */
JNIEXPORT jdouble JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_logSatCount1
  (JNIEnv *env, jobject o, jobject that)
{
  BDD b = BDD_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_satcountlnset(%d, %d)\n", b, c);
#endif
  double result = bdd_satcountlnset(b, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    varProfile
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_varProfile
  (JNIEnv *env, jobject o)
{
  jintArray result;
  BDD b = BDD_JavaToC(env, o);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_varnum()\n");
#endif
  int size = bdd_varnum();
#if defined(TRACE_BUDDYLIB)
  printf("bdd_varprofile(%d)\n", b);
#endif
  int* arr = bdd_varprofile(b);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;
  result = (*env)->NewIntArray(env, size);
  if (result == NULL) return NULL;
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*) arr);
  free(arr);
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_addref(%d)\n", b);
#endif
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
  if (bdd != INVALID_BDD) {
#if defined(TRACE_BUDDYLIB)
    printf("bdd_delref(%d)\n", bdd);
#endif
    bdd_delref(bdd);
  }
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    veccompose0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDPairing;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_veccompose0
  (JNIEnv *env, jobject o, jobject pair)
{
  BDD b = BDD_JavaToC(env, o);
  bddPair* p = Pair_JavaToC(env, pair);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_veccompose(%d, %p)\n", b, p);
#endif
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

#if defined(TRACE_BUDDYLIB)
  printf("bdd_scanset(%d, %p, %p)\n", b, &arr, &size);
#endif
  bdd_scanset(b, &arr, &size);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;
  result = (*env)->NewIntArray(env, size);
  if (result == NULL) return NULL;
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*) arr);
  free(arr);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    scanSetDomains
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_scanSetDomains
  (JNIEnv *env, jobject o)
{
  BDD b = BDD_JavaToC(env, o);
  int size;
  int *arr;
  jintArray result;

#if defined(TRACE_BUDDYLIB)
  printf("fdd_scanset(%d, %p, %p)\n", b, &arr, &size);
#endif
  fdd_scanset(b, &arr, &size);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;
  result = (*env)->NewIntArray(env, size);
  if (result == NULL) return NULL;
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*) arr);
  free(arr);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    scanVar0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDDomain;)I
 */
JNIEXPORT jint JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_scanVar0
  (JNIEnv *env, jobject o, jobject p)
{
  BDD b = BDD_JavaToC(env, o);
  int domain = Domain_JavaToC(env, p);
#if defined(TRACE_BUDDYLIB)
  printf("fdd_scanvar(%d, %d)\n", b, domain);
#endif
  jint result = fdd_scanvar(b, domain);
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_domainnum()\n");
#endif
  jint size = fdd_domainnum();
#if defined(TRACE_BUDDYLIB)
  printf("fdd_scanallvar(%d)\n", b);
#endif
  int* arr = fdd_scanallvar(b);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;

  result = (*env)->NewIntArray(env, size);
  if (result == NULL) return NULL;
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*) arr);
  free(arr);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    replace0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDPairing;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_replace0
  (JNIEnv *env, jobject o, jobject pair)
{
  BDD b = BDD_JavaToC(env, o);
  bddPair* p = Pair_JavaToC(env, pair);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_replace(%d, %p)\n", b, p);
#endif
  BDD c = bdd_replace(b, p);
  jobject result = BDD_CToJava(env, c);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDD
 * Method:    replaceWith0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDPairing;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDD_replaceWith0
  (JNIEnv *env, jobject o, jobject pair)
{
  BDD b = BDD_JavaToC(env, o);
  bddPair* p = Pair_JavaToC(env, pair);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_replace(%d, %p)\n", b, p);
#endif
  BDD c = bdd_replace(b, p);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_addref(%d)\n", c);
#endif
  bdd_addref(c);
  (*env)->SetIntField(env, o, bdd_fid, c);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_delref(%d)\n", b);
#endif
  bdd_delref(b);
  check_error(env);
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_printset(%d)\n", b);
#endif
  fdd_printset(b);
  fflush(stdout);
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_domain(%d)\n", domain);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_domainsize(%d)\n", domain);
#endif
  jint result = fdd_domainsize(domain);
  check_error(env);
  return result;
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDDomain
 * Method:    buildEquals0
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDDomain;)Lorg/sf/javabdd/BuDDyFactory$BuDDyBDD;
 */
JNIEXPORT jobject JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDDomain_buildEquals0
  (JNIEnv *env, jobject o, jobject that)
{
  int d1 = Domain_JavaToC(env, o);
  int d2 = Domain_JavaToC(env, that);
#if defined(TRACE_BUDDYLIB)
  printf("fdd_equals(%d, %d)\n", d1, d2);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_ithset(%d)\n", domain);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_ithvar(%d, %d)\n", domain, i);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_varnum(%d)\n", domain);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_varnum(%d)\n", domain);
#endif
  jint size = fdd_varnum(domain);
#if defined(TRACE_BUDDYLIB)
  printf("fdd_vars(%d)\n", domain);
#endif
  int* arr = fdd_vars(domain);
  if (check_error(env)) return NULL;
  if (arr == NULL) return NULL;

  result = (*env)->NewIntArray(env, size);
  if (result == NULL) return NULL;
  (*env)->SetIntArrayRegion(env, result, 0, size, (jint*)arr);
  return result;
}

/* class org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing */

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set0
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set0
  (JNIEnv *env, jobject o, jint i, jint j)
{
  bddPair* p = Pair_JavaToC(env, o);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setpair(%p, %d, %d)\n", p, i, j);
#endif
  bdd_setpair(p, i, j);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set1
 * Signature: ([I[I)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set1
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setpairs(%p, %p, %p, %d)\n", p, a1, a2, size1);
#endif
  bdd_setpairs(p, (int*)a1, (int*)a2, size1);
  (*env)->ReleasePrimitiveArrayCritical(env, arr1, a1, JNI_ABORT);
  (*env)->ReleasePrimitiveArrayCritical(env, arr2, a2, JNI_ABORT);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set2
 * Signature: (ILorg/sf/javabdd/BuDDyFactory$BuDDyBDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set2
  (JNIEnv *env, jobject o, jint b, jobject that2)
{
  bddPair* p = Pair_JavaToC(env, o);
  BDD c = BDD_JavaToC(env, that2);
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setbddpair(%p, %d, %d)\n", p, b, c);
#endif
  bdd_setbddpair(p, b, c);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set3
 * Signature: ([I[Lorg/sf/javabdd/BDD;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set3
  (JNIEnv *env, jobject o, jintArray arr1, jobjectArray arr2)
{
  bddPair* p = Pair_JavaToC(env, o);
  bdd *a1;
  bdd *a2;
  jint size1 = (*env)->GetArrayLength(env, arr1);
  jint size2 = (*env)->GetArrayLength(env, arr2);
  jint* arr;
  int i;
  if (size1 != size2) {
    jclass cls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
    (*env)->ThrowNew(env, cls, "array sizes do not match");
    (*env)->DeleteLocalRef(env, cls);
    return;
  }
  a1 = (bdd*) malloc(size1 * sizeof(bdd*));
  a2 = (bdd*) malloc(size1 * sizeof(bdd*));
  arr = (*env)->GetIntArrayElements(env, arr1, 0);
  for (i=0; i<size1; ++i) {
    int foo = arr[i];
    jobject obj2 = (*env)->GetObjectArrayElement(env, arr2, i);
    a1[i] = foo;
    a2[i] = BDD_JavaToC(env, obj2);
    (*env)->DeleteLocalRef(env, obj2);
  }
#if defined(TRACE_BUDDYLIB)
  printf("bdd_setbddpairs(%p, %p, %p, %d)\n", p, a1, a2, size1);
#endif
  bdd_setbddpairs(p, a1, a2, size1);
  free(a1);
  free(a2);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set4
 * Signature: (Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDDomain;Lorg/sf/javabdd/BuDDyFactory$BuDDyBDDDomain;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set4
  (JNIEnv *env, jobject o, jobject dom1, jobject dom2)
{
  bddPair* p = Pair_JavaToC(env, o);
  int b = Domain_JavaToC(env, dom1);
  int c = Domain_JavaToC(env, dom2);
#if defined(TRACE_BUDDYLIB)
  printf("fdd_setpair(%p, %d, %d);\n", p, b, c);
#endif
  fdd_setpair(p, b, c);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    set5
 * Signature: ([Lorg/sf/javabdd/BDDDomain;[Lorg/sf/javabdd/BDDDomain;)V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_set5
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
#if defined(TRACE_BUDDYLIB)
  printf("fdd_setpairs(%p, %p, %p, %d);\n", p, a1, a2, size1);
#endif
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
#if defined(TRACE_BUDDYLIB)
  printf("bdd_resetpair(%p);\n", p);
#endif
  bdd_resetpair(p);
  check_error(env);
}

/*
 * Class:     org_sf_javabdd_BuDDyFactory_BuDDyBDDPairing
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_sf_javabdd_BuDDyFactory_00024BuDDyBDDPairing_free
  (JNIEnv *env, jobject o)
{
  bddPair* p = Pair_JavaToC(env, o);
  (*env)->SetLongField(env, o, pair_fid, 0);
  if (p) {
#if defined(TRACE_BUDDYLIB)
    printf("bdd_freepair(%p);\n", p);
#endif
    bdd_freepair(p);
  }
}
