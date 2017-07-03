#!/bin/zsh
####################################################################################################
#   >>> READ ME <<<
#   The original compile file "./linux-compile.sh" comes with this package doesn't work.
#   This file works well.
#   I haven't found the reason why "./linux-compile.sh" refuse to work.
#   I made only 3 changes in this file:
#       1. Separate the CLASSPATH parameter($CP) from the command line.
#       2. Add "." to the end of CLASSPATH
#       3. Add "-source 1.4" option.
####################################################################################################

### CLASSPATH PARAMETER
CP="./lib/commons-beanutils.jar:./lib/commons-collections.jar:./lib/commons-daemon.jar:./lib/commons-digester.jar:./lib/commons-logging.jar:./lib/commons-modeler.jar:./lib/jakarta-regexp-1.2.jar:./lib/mx4j.jar:./lib/naming-common.jar:./lib/naming-factory.jar:./lib/naming-resources.jar:./lib/servlet.jar:./lib/tomcat-util.jar:."

### COMMAND
javac -source 1.4 -d . -classpath ${CP} ./src/com/brainysoftware/pyrmont/util/*.java ./src/ex01/pyrmont/*.java ./src/ex02/pyrmont/*.java ./src/ex03/pyrmont/*.java ./src/ex03/pyrmont/connector/*.java ./src/ex03/pyrmont/connector/http/*.java ./src/ex03/pyrmont/startup/*.java ./src/ex04/pyrmont/core/*.java ./src/ex04/pyrmont/startup/*.java ./src/ex05/pyrmont/core/*.java ./src/ex05/pyrmont/startup/*.java ./src/ex05/pyrmont/valves/*.java ./src/ex06/pyrmont/core/*.java ./src/ex06/pyrmont/startup/*.java ./src/ex07/pyrmont/core/*.java ./src/ex07/pyrmont/startup/*.java ./src/ex08/pyrmont/core/*.java ./src/ex08/pyrmont/startup/*.java ./src/ex09/pyrmont/core/*.java ./src/ex09/pyrmont/startup/*.java ./src/ex10/pyrmont/core/*.java ./src/ex10/pyrmont/realm/*.java ./src/ex10/pyrmont/startup/*.java ./src/ex11/pyrmont/core/*.java ./src/ex11/pyrmont/startup/*.java ./src/ex13/pyrmont/core/*.java ./src/ex13/pyrmont/startup/*.java ./src/ex14/pyrmont/core/*.java ./src/ex14/pyrmont/startup/*.java ./src/ex15/pyrmont/digestertest/*.java ./src/ex15/pyrmont/startup/*.java ./src/ex16/pyrmont/shutdownhook/*.java ./src/ex20/pyrmont/modelmbeantest1/*.java ./src/ex20/pyrmont/modelmbeantest2/*.java ./src/ex20/pyrmont/standardmbeantest/*.java ./src/org/apache/catalina/*.java ./src/org/apache/catalina/authenticator/*.java ./src/org/apache/catalina/cluster/*.java ./src/org/apache/catalina/connector/*.java ./src/org/apache/catalina/connector/http/*.java ./src/org/apache/catalina/connector/http10/*.java ./src/org/apache/catalina/core/*.java ./src/org/apache/catalina/deploy/*.java ./src/org/apache/catalina/loader/*.java ./src/org/apache/catalina/logger/*.java ./src/org/apache/catalina/mbeans/*.java ./src/org/apache/catalina/net/*.java ./src/org/apache/catalina/realm/*.java ./src/org/apache/catalina/servlets/*.java ./src/org/apache/catalina/session/*.java ./src/org/apache/catalina/ssi/*.java ./src/org/apache/catalina/startup/*.java ./src/org/apache/catalina/users/*.java ./src/org/apache/catalina/util/*.java ./src/org/apache/catalina/valves/*.java ./src/org/apache/naming/*.java ./src/org/apache/naming/java/*.java ./src/org/apache/naming/resources/*.java ./src/org/apache/naming/resources/jndi/*.java

java PropertiesFilesCopier
