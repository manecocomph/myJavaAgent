This project is not a Maven project, just a Java project, which depends on 2 jars from JDK (JDK 8 or lower):
1) JDK_DIR/lib/tools.jar
2) JRE_DIR/lib/rt.jar


# how to build the agent jar
1) go to the project folder: myJavaAgent/bin 
2) run command: jar -cmf ../MANIFEST.MF myJavaAgent.jar com/tianxiaohui/java/agent/*

# how to use the agent
1) get target Java PID by: pgrep java or jps
2) run command:
	java -jar myJavaAgent.jar <pid> listMBean
	java -jar myJavaAgent.jar <pid> heapDump
	java -jar myJavaAgent.jar <pid> threadDump
	java -jar myJavaAgent.jar 69286 printDnsCache