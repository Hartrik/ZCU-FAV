#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=MinGW-Windows
CND_DLIB_EXT=dll
CND_CONF=Debug_repl
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/built-in.o \
	${OBJECTDIR}/src/built-in_list.o \
	${OBJECTDIR}/src/built-in_math.o \
	${OBJECTDIR}/src/built-in_system.o \
	${OBJECTDIR}/src/commons.o \
	${OBJECTDIR}/src/console.o \
	${OBJECTDIR}/src/heap.o \
	${OBJECTDIR}/src/interpreter.o \
	${OBJECTDIR}/src/lexer.o \
	${OBJECTDIR}/src/main.o \
	${OBJECTDIR}/src/reader.o \
	${OBJECTDIR}/src/repl.o \
	${OBJECTDIR}/src/types.o

# Test Directory
TESTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}/tests

# Test Files
TESTFILES= \
	${TESTDIR}/TestFiles/f1 \
	${TESTDIR}/TestFiles/f2

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/lisp.exe

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/lisp.exe: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.c} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/lisp ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/src/built-in.o: src/built-in.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in.o src/built-in.c

${OBJECTDIR}/src/built-in_list.o: src/built-in_list.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_list.o src/built-in_list.c

${OBJECTDIR}/src/built-in_math.o: src/built-in_math.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_math.o src/built-in_math.c

${OBJECTDIR}/src/built-in_system.o: src/built-in_system.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_system.o src/built-in_system.c

${OBJECTDIR}/src/commons.o: src/commons.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/commons.o src/commons.c

${OBJECTDIR}/src/console.o: src/console.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/console.o src/console.c

${OBJECTDIR}/src/heap.o: src/heap.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/heap.o src/heap.c

${OBJECTDIR}/src/interpreter.o: src/interpreter.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/interpreter.o src/interpreter.c

${OBJECTDIR}/src/lexer.o: src/lexer.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/lexer.o src/lexer.c

${OBJECTDIR}/src/main.o: src/main.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/main.o src/main.c

${OBJECTDIR}/src/reader.o: src/reader.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/reader.o src/reader.c

${OBJECTDIR}/src/repl.o: src/repl.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/repl.o src/repl.c

${OBJECTDIR}/src/types.o: src/types.c 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -std=c89 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/types.o src/types.c

# Subprojects
.build-subprojects:

# Build Test Targets
.build-tests-conf: .build-conf ${TESTFILES}
${TESTDIR}/TestFiles/f1: ${TESTDIR}/tests/built-in-test.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.c}   -o ${TESTDIR}/TestFiles/f1 $^ ${LDLIBSOPTIONS} 

${TESTDIR}/TestFiles/f2: ${TESTDIR}/tests/eval-test.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.c}   -o ${TESTDIR}/TestFiles/f2 $^ ${LDLIBSOPTIONS} 


${TESTDIR}/tests/built-in-test.o: tests/built-in-test.c 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -I. -std=c89 -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/built-in-test.o tests/built-in-test.c


${TESTDIR}/tests/eval-test.o: tests/eval-test.c 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.c) -g -Wall -I. -I. -std=c89 -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/eval-test.o tests/eval-test.c


${OBJECTDIR}/src/built-in_nomain.o: ${OBJECTDIR}/src/built-in.o src/built-in.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/built-in.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_nomain.o src/built-in.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/built-in.o ${OBJECTDIR}/src/built-in_nomain.o;\
	fi

${OBJECTDIR}/src/built-in_list_nomain.o: ${OBJECTDIR}/src/built-in_list.o src/built-in_list.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/built-in_list.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_list_nomain.o src/built-in_list.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/built-in_list.o ${OBJECTDIR}/src/built-in_list_nomain.o;\
	fi

${OBJECTDIR}/src/built-in_math_nomain.o: ${OBJECTDIR}/src/built-in_math.o src/built-in_math.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/built-in_math.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_math_nomain.o src/built-in_math.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/built-in_math.o ${OBJECTDIR}/src/built-in_math_nomain.o;\
	fi

${OBJECTDIR}/src/built-in_system_nomain.o: ${OBJECTDIR}/src/built-in_system.o src/built-in_system.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/built-in_system.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/built-in_system_nomain.o src/built-in_system.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/built-in_system.o ${OBJECTDIR}/src/built-in_system_nomain.o;\
	fi

${OBJECTDIR}/src/commons_nomain.o: ${OBJECTDIR}/src/commons.o src/commons.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/commons.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/commons_nomain.o src/commons.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/commons.o ${OBJECTDIR}/src/commons_nomain.o;\
	fi

${OBJECTDIR}/src/console_nomain.o: ${OBJECTDIR}/src/console.o src/console.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/console.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/console_nomain.o src/console.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/console.o ${OBJECTDIR}/src/console_nomain.o;\
	fi

${OBJECTDIR}/src/heap_nomain.o: ${OBJECTDIR}/src/heap.o src/heap.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/heap.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/heap_nomain.o src/heap.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/heap.o ${OBJECTDIR}/src/heap_nomain.o;\
	fi

${OBJECTDIR}/src/interpreter_nomain.o: ${OBJECTDIR}/src/interpreter.o src/interpreter.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/interpreter.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/interpreter_nomain.o src/interpreter.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/interpreter.o ${OBJECTDIR}/src/interpreter_nomain.o;\
	fi

${OBJECTDIR}/src/lexer_nomain.o: ${OBJECTDIR}/src/lexer.o src/lexer.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/lexer.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/lexer_nomain.o src/lexer.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/lexer.o ${OBJECTDIR}/src/lexer_nomain.o;\
	fi

${OBJECTDIR}/src/main_nomain.o: ${OBJECTDIR}/src/main.o src/main.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/main.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/main_nomain.o src/main.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/main.o ${OBJECTDIR}/src/main_nomain.o;\
	fi

${OBJECTDIR}/src/reader_nomain.o: ${OBJECTDIR}/src/reader.o src/reader.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/reader.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/reader_nomain.o src/reader.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/reader.o ${OBJECTDIR}/src/reader_nomain.o;\
	fi

${OBJECTDIR}/src/repl_nomain.o: ${OBJECTDIR}/src/repl.o src/repl.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/repl.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/repl_nomain.o src/repl.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/repl.o ${OBJECTDIR}/src/repl_nomain.o;\
	fi

${OBJECTDIR}/src/types_nomain.o: ${OBJECTDIR}/src/types.o src/types.c 
	${MKDIR} -p ${OBJECTDIR}/src
	@NMOUTPUT=`${NM} ${OBJECTDIR}/src/types.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.c) -g -Wall -std=c89 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/src/types_nomain.o src/types.c;\
	else  \
	    ${CP} ${OBJECTDIR}/src/types.o ${OBJECTDIR}/src/types_nomain.o;\
	fi

# Run Test Targets
.test-conf:
	@if [ "${TEST}" = "" ]; \
	then  \
	    ${TESTDIR}/TestFiles/f1 || true; \
	    ${TESTDIR}/TestFiles/f2 || true; \
	else  \
	    ./${TEST} || true; \
	fi

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/lisp.exe

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
