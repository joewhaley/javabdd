/*========================================================================
               Copyright (C) 1996-2002 by Jorn Lind-Nielsen
                            All rights reserved

    Permission is hereby granted, without written agreement and without
    license or royalty fees, to use, reproduce, prepare derivative
    works, distribute, and display this software and its documentation
    for any purpose, provided that (1) the above copyright notice and
    the following two paragraphs appear in all copies of the source code
    and (2) redistributions, including without limitation binaries,
    reproduce these notices in the supporting documentation. Substantial
    modifications to this software may be copyrighted by their authors
    and need not follow the licensing terms described here, provided
    that the new terms are clearly indicated in all files where they apply.

    IN NO EVENT SHALL JORN LIND-NIELSEN, OR DISTRIBUTORS OF THIS
    SOFTWARE BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL,
    INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
    SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE AUTHORS OR ANY OF THE
    ABOVE PARTIES HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

    JORN LIND-NIELSEN SPECIFICALLY DISCLAIM ANY WARRANTIES, INCLUDING,
    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
    FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS
    ON AN "AS IS" BASIS, AND THE AUTHORS AND DISTRIBUTORS HAVE NO
    OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
    MODIFICATIONS.
========================================================================*/

/*************************************************************************
  $Header: /cvsroot/buddy/buddy/src/kernel.h,v 1.3 2004/08/03 16:49:54 haimcohen Exp $
  FILE:  kernel.h
  DESCR: Kernel specific definitions for BDD package
  AUTH:  Jorn Lind
  DATE:  (C) june 1997
*************************************************************************/

#ifndef _KERNEL_H
#define _KERNEL_H

/*=== Includes =========================================================*/

#include <limits.h>
#include <setjmp.h>
#include "bdd.h"

/*=== SANITY CHECKS ====================================================*/

   /* Make sure we use at least 32 bit integers */
#if (INT_MAX < 0x7FFFFFFF)
#error The compiler does not support 4 byte integers!
#endif


   /* Sanity check argument and return eventual error code */
#define CHECK(r)\
   if (!bddrunning) return bdd_error(BDD_RUNNING);\
   else if ((r) < 0  ||  (r) >= bddnodesize) return bdd_error(BDD_ILLBDD);\
   else if (r >= 2 && LOW(r) == -1) return bdd_error(BDD_ILLBDD)\

   /* Sanity check argument and return eventually the argument 'a' */
#define CHECKa(r,a)\
   if (!bddrunning) { bdd_error(BDD_RUNNING); return (a); }\
   else if ((r) < 0  ||  (r) >= bddnodesize)\
     { bdd_error(BDD_ILLBDD); return (a); }\
   else if (r >= 2 && LOW(r) == -1)\
     { bdd_error(BDD_ILLBDD); return (a); }

#define CHECKn(r)\
   if (!bddrunning) { bdd_error(BDD_RUNNING); return; }\
   else if ((r) < 0  ||  (r) >= bddnodesize)\
     { bdd_error(BDD_ILLBDD); return; }\
   else if (r >= 2 && LOW(r) == -1)\
     { bdd_error(BDD_ILLBDD); return; }


/*=== SEMI-INTERNAL TYPES ==============================================*/

typedef struct s_BddNode /* Node table entry */
{
#if defined(USE_BITFIELDS)
   unsigned int refcou : 10;
   unsigned int level  : 22;
#else
   unsigned int refcou_and_level;
#endif
   int low;
   int high;
   int hash;
   int next;
} BddNode;


/*=== KERNEL VARIABLES =================================================*/

#ifdef CPLUSPLUS
extern "C" {
#endif

extern int       bddrunning;         /* Flag - package initialized */
extern int       bdderrorcond;       /* Some error condition was met */
extern int       bddnodesize;        /* Number of allocated nodes */
extern int       bddmaxnodesize;     /* Maximum allowed number of nodes */
extern int       bddmaxnodeincrease; /* Max. # of nodes used to inc. table */
extern BddNode*  bddnodes;           /* All of the bdd nodes */
extern int       bddvarnum;          /* Number of defined BDD variables */
extern int*      bddrefstack;        /* Internal node reference stack */
extern int*      bddrefstacktop;     /* Internal node reference stack top */
extern int*      bddvar2level;
extern int*      bddlevel2var;
extern jmp_buf   bddexception;
extern int       bddreorderdisabled;
extern int       bddresized;
extern bddCacheStat bddcachestats;

#ifdef CPLUSPLUS
}
#endif


/*=== KERNEL DEFINITIONS ===============================================*/

#define MAXVAR 0x1FFFFF
#define MAXREF 0x3FF
#define SRAND48SEED 0xbeef

   /* Reference counting */
#if defined(USE_BITFIELDS)
#define DECREF(n) if (REF(n)!=MAXREF && REF(n)>0) bddnodes[n].refcou--
#define INCREF(n) if (REF(n)<MAXREF) bddnodes[n].refcou++
#define DECREFp(n) if (REFp(n)!=MAXREF && REFp(n)>0) n->refcou--
#define INCREFp(n) if (REFp(n)<MAXREF) n->refcou++
#define HASREF(n) (REF(n) > 0)
#else
#define DECREF(n) if (REF(n)!=MAXREF && REF(n)>0) bddnodes[n].refcou_and_level--
#define INCREF(n) if (REF(n)<MAXREF) bddnodes[n].refcou_and_level++
#define DECREFp(n) if (REFp(n)!=MAXREF && REFp(n)>0) n->refcou_and_level--
#define INCREFp(n) if (REFp(n)<MAXREF) n->refcou_and_level++
#define HASREF(n) (REF(n) > 0)
#endif

   /* Marking BDD nodes */

#if defined(USE_BITFIELDS)
#define MARKON1   0x200000    /* Bit used to mark a node (1) */
#define MARKOFF1  0x1FFFFF    /* - unmark */
#define MARKHIDE 0x1FFFFF
#define SETMARK(n)  (LEVEL(n) |= MARKON1)
#define UNMARK(n)   (LEVEL(n) &= MARKOFF1)
#define MARKED(n)   (LEVEL(n) & MARKON1)
#define SETMARKp(p) (LEVELp(p) |= MARKON1)
#define UNMARKp(p)  (LEVELp(p) &= MARKOFF1)
#define MARKEDp(p)  (LEVELp(p) & MARKON1)
#else
#define MARKON2   0x80000000    /* Bit used to mark a node (1) */
#define MARKOFF2  0x7FFFFFFF    /* - unmark */
#define MARKHIDE  0x1FFFFF
#define SETMARK(n)  (bddnodes[n].refcou_and_level |= MARKON2)
#define UNMARK(n)   (bddnodes[n].refcou_and_level &= MARKOFF2)
#define MARKED(n)   (bddnodes[n].refcou_and_level & MARKON2)
#define SETMARKp(p) ((p)->refcou_and_level |= MARKON2)
#define UNMARKp(p)  ((p)->refcou_and_level &= MARKOFF2)
#define MARKEDp(p)  ((p)->refcou_and_level & MARKON2)
#endif


   /* Hashfunctions */

#define PAIR(a,b)      ((unsigned int)((((unsigned int)a)+((unsigned int)b))*(((unsigned int)a)+((unsigned int)b)+((unsigned int)1))/((unsigned int)2)+((unsigned int)a)))
#define TRIPLE(a,b,c)  ((unsigned int)(PAIR((unsigned int)c,PAIR(a,b))))


   /* Inspection of BDD nodes */
#define ISCONST(a) ((a) < 2)
#define ISNONCONST(a) ((a) >= 2)
#define ISONE(a)   ((a) == 1)
#define ISZERO(a)  ((a) == 0)
#define LOW(a)     (bddnodes[a].low)
#define HIGH(a)    (bddnodes[a].high)
#define LOWp(p)     ((p)->low)
#define HIGHp(p)    ((p)->high)
#if defined(USE_BITFIELDS)
#define REF(n) (bddnodes[n].refcou)
#define REFp(n) ((n)->refcou)
#define SETREF(n,v) (bddnodes[n].refcou = (v))
#define SETREFp(n,v) ((n)->refcou = (v))
#define LEVEL(n) (bddnodes[n].level)
#define LEVELp(p)   ((p)->level)
#define SETLEVEL(n,v) (LEVEL(n) = (v))
#define SETLEVELp(p,v) (LEVELp(p) = (v))
#define CLRLEVELREF(n) (bddnodes[n].refcou = bddnodes[n].level = 0)
#else
#define REF(n) (bddnodes[n].refcou_and_level & MAXREF)
#define REFp(n) ((n)->refcou_and_level & MAXREF)
#define SETREF(n,v) (bddnodes[n].refcou_and_level = (bddnodes[n].refcou_and_level & ~MAXREF) | (v))
#define SETREFp(p,v) ((p)->refcou_and_level = ((p)->refcou_and_level & ~MAXREF) | (v))
#define LEVEL(n) (bddnodes[n].refcou_and_level >> 10)
#define LEVELp(p) ((p)->refcou_and_level >> 10)
#define SETLEVEL(n,v) (bddnodes[n].refcou_and_level = (bddnodes[n].refcou_and_level & MAXREF) | (v << 10))
#define SETLEVELp(p,v) ((p)->refcou_and_level = ((p)->refcou_and_level & MAXREF) | (v << 10))
#define CLRLEVELREF(n) (bddnodes[n].refcou_and_level = 0)
#endif

   /* Stacking for garbage collector */
#define INITREF    bddrefstacktop = bddrefstack
#define PUSHREF(a) *(bddrefstacktop++) = (a)
#define READREF(a) *(bddrefstacktop-(a))
#define POPREF(a)  bddrefstacktop -= (a)

#define BDDONE 1
#define BDDZERO 0

#ifndef CLOCKS_PER_SEC
  /* Pass `CPPFLAGS=-DDEFAULT_CLOCK=1000' as an argument to ./configure
     to override this setting.  */
# ifndef DEFAULT_CLOCK
#  define DEFAULT_CLOCK 60
# endif
# define CLOCKS_PER_SEC DEFAULT_CLOCK
#endif

#define DEFAULTMAXNODEINC 50000

#define MIN(a,b) ((a) < (b) ? (a) : (b))
#define MAX(a,b) ((a) > (b) ? (a) : (b))
#define NEW(t,n) ( (t*)malloc(sizeof(t)*(n)) )

  /* Compatibility with Windows */
#if defined(_MSC_VER) || defined(WIN32)
#define srand48(x) srand(x)
#define lrand48(x) rand(x)
#define alloca(x) _alloca(x)
#endif


/*=== KERNEL PROTOTYPES ================================================*/

#ifdef CPLUSPLUS
extern "C" {
#endif

extern int    bdd_error(int);
extern int    bdd_makenode(unsigned int, int, int);
extern int    bdd_noderesize(int);
extern void   bdd_checkreorder(void);
extern void   bdd_mark(int);
extern void   bdd_mark_upto(int, int);
extern void   bdd_markcount(int, int*);
extern void   bdd_unmark(int);
extern void   bdd_unmark_upto(int, int);
extern void   bdd_register_pair(bddPair*);
extern int   *fdddec2bin(int, int);

extern int    bdd_operator_init(int);
extern void   bdd_operator_done(void);
extern void   bdd_operator_varresize(void);
extern void   bdd_operator_reset(void);

extern void   bdd_pairs_init(void);
extern void   bdd_pairs_done(void);
extern int    bdd_pairs_resize(int,int);
extern void   bdd_pairs_vardown(int);

extern void   bdd_fdd_init(void);
extern void   bdd_fdd_done(void);

extern void   bdd_reorder_init(void);
extern void   bdd_reorder_done(void);
extern int    bdd_reorder_ready(void);
extern void   bdd_reorder_auto(void);
extern int    bdd_reorder_vardown(int);
extern int    bdd_reorder_varup(int);

extern void   bdd_cpp_init(void);

#ifdef CPLUSPLUS
}
#endif

#endif /* _KERNEL_H */


/* EOF */