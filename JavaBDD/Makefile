#
# Makefile to create Java JNI library.
# On Windows, uses Cygwin b20.1 tools with Mingw runtime. 
# 
# Things you may need to change, or redefine on the command line:
#   BUDDY_SRC    -- location of BuDDy source code
#   JDK_ROOT     -- location where you installed JDK
#

BUDDY_SRC = buddy22/src

ifeq (${OS},Windows_NT)
  JDK_ROOT = $(firstword $(wildcard c:/j2sdk*))
  CLASSPATH = .\;jdd.jar
  CC = gcc
  CFLAGS = -Wall -O3 -mno-cygwin
  OBJECT_OUTPUT_OPTION = -o$(space)
  LINK = dllwrap
  LINKFLAGS = -s -mno-cygwin -mwindows --target=i386-mingw32 \
              --add-stdcall-alias --driver-name gcc
  DLL_OUTPUT_OPTION = -o$(space)
  INCLUDES = -I. -I$(JDK_ROOT)/include -I$(BUDDY_SRC) \
             -I$(JDK_ROOT)/include/win32
  DLL_NAME = buddy.dll
  ifeq (${CC},icl)    # Intel Windows compiler
    CFLAGS = -O3 -QaxW
    OBJECT_OUTPUT_OPTION = -Fo
    LINK = xilink
    LINKFLAGS = /dll /libpath:$(JDK_ROOT)/lib user32.lib gdi32.lib
    DLL_OUTPUT_OPTION = /out:
  endif
  ifeq (${CC},cl)     # Microsoft Visual C++ compiler
    CFLAGS = -O2
    OBJECT_OUTPUT_OPTION = -Fo
    LINK = cl
    LINKFLAGS = -MLd -LDd -Zi /link /libpath:$(JDK_ROOT)/lib user32.lib gdi32.lib
    DLL_OUTPUT_OPTION = -Fe
  endif
else
  JDK_ROOT = $(firstword $(wildcard /usr/java/j2sdk*))
  CLASSPATH = .:jdd.jar
  CFLAGS = -D_REENTRANT -D_GNU_SOURCE -O3
  OBJECT_OUTPUT_OPTION = -o$(space)
  LINK = $(CC)
  LINKFLAGS = -shared
  DLL_OUTPUT_OPTION = -o$(space)
  INCLUDES = -I. -I$(JDK_ROOT)/include -I$(BUDDY_SRC) \
             -I$(JDK_ROOT)/include/linux
  DLL_NAME = libbuddy.so
  ifeq (${CC},icc)    # Intel Linux compiler
    LINKFLAGS = -shared -static-libcxa
  endif
endif

# The java tools:
JAVAC = $(JDK_ROOT)/bin/javac
JAVA = $(JDK_ROOT)/bin/java
JAVAH = $(JDK_ROOT)/bin/javah
JAVADOC = $(JDK_ROOT)/bin/javadoc
JAR = $(JDK_ROOT)/bin/jar

# The java source code
JAVA_SOURCES = org/sf/javabdd/BDD.java \
	org/sf/javabdd/BDDException.java \
	org/sf/javabdd/BDDFactory.java \
	org/sf/javabdd/BDDDomain.java \
	org/sf/javabdd/BDDPairing.java \
	org/sf/javabdd/BuDDyFactory.java
JAVA_CLASSFILES = org/sf/javabdd/*.class
JAVA_PACKAGES = org.sf.javabdd
JNI_CLASSFILE = org/sf/javabdd/BuDDyFactory.class
JNI_CLASSNAMES = org.sf.javabdd.BuDDyFactory \
	org.sf.javabdd.BuDDyFactory\$$BuDDyBDD \
	org.sf.javabdd.BuDDyFactory\$$BuDDyBDDDomain \
	org.sf.javabdd.BuDDyFactory\$$BuDDyBDDPairing
JNI_INCLUDE = buddy_jni.h
EXAMPLE_SOURCES = NQueens.java
EXAMPLE_CLASSFILES = $(EXAMPLE_SOURCES:%.java=%.class)
JAR_NAME = javabdd.jar

DLL_SRCS  = buddy_jni.c \
	$(BUDDY_SRC)/bddio.c $(BUDDY_SRC)/bddop.c $(BUDDY_SRC)/bvec.c \
	$(BUDDY_SRC)/cache.c $(BUDDY_SRC)/fdd.c $(BUDDY_SRC)/imatrix.c \
	$(BUDDY_SRC)/kernel.c $(BUDDY_SRC)/pairs.c $(BUDDY_SRC)/prime.c \
	$(BUDDY_SRC)/reorder.c $(BUDDY_SRC)/tree.c
DLL_OBJS  = $(DLL_SRCS:.c=.o)

all: $(DLL_NAME)

dll: $(DLL_NAME)

$(DLL_NAME): $(DLL_OBJS)
	$(LINK) $(DLL_OUTPUT_OPTION)$@ $(DLL_OBJS) $(LINKFLAGS)
#	$(LINK) $(INCLUDES) $(CFLAGS) $(DLL_OUTPUT_OPTION)$@ $(DLL_SRCS) -MLd -LDd -Zi /link /libpath:$(JDK_ROOT)/lib 

buddy_jni.o: buddy_jni.c $(JNI_INCLUDE)
	$(CC) $(CFLAGS) $(INCLUDES) -c $(OBJECT_OUTPUT_OPTION)$@ $<

.c.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c $(OBJECT_OUTPUT_OPTION)$@ $<

$(JNI_INCLUDE): $(JNI_CLASSFILE)
	$(JAVAH) -jni -o $(JNI_INCLUDE) $(JNI_CLASSNAMES)

$(JNI_CLASSFILE): $(JAVA_SOURCES)
	$(JAVAC) -classpath $(CLASSPATH) $(JAVA_SOURCES)

$(EXAMPLE_CLASSFILES): $(EXAMPLE_SOURCES)
	$(JAVAC) -classpath $(CLASSPATH) $(EXAMPLE_SOURCES)

examples: $(EXAMPLE_CLASSFILES)

javadoc: $(JAVA_SOURCES)
	$(JAVADOC) -d javadoc -breakiterator $(JAVA_PACKAGES)

jar: $(JAR_NAME)

$(JAR_NAME): $(JNI_CLASSFILE) $(EXAMPLE_CLASSFILES)
	$(JAR) cvfm $(JAR_NAME) javabddManifest $(JAVA_CLASSFILES) $(EXAMPLE_CLASSFILES)

pdo:
	icl -Qprof_gen $(INCLUDES) $(CFLAGS) $(DLL_OUTPUT_OPTION)buddy.dll $(DLL_SRCS) -LD /link /libpath:$(JDK_ROOT)/lib 
	$(JAVA) NQueens 12
	icl -Qprof_use $(INCLUDES) $(CFLAGS) $(DLL_OUTPUT_OPTION)buddy.dll $(DLL_SRCS) -LD /link /libpath:$(JDK_ROOT)/lib 
	$(JAVA) NQueens 12

opt_report:
	icl -Qopt_report -Qopt_report_phase all $(INCLUDES) $(CFLAGS) $(DLL_OUTPUT_OPTION)buddy.dll $(DLL_SRCS) -LD /link /libpath:$(JDK_ROOT)/lib 

test:	$(DLL_NAME) $(EXAMPLE_CLASSFILES)
	$(JAVA) NQueens 8

clean:
	$(RM) -f $(JAVA_CLASSFILES) $(JNI_INCLUDE) $(DLL_OBJS) $(DLL_NAME) $(EXAMPLE_CLASSFILES) $(JAR_NAME)
	$(RM) -rf javadoc

empty := 
space := $(empty) $(empty)
