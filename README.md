This project is not a Maven project, just a Java project, which depends on 2 jars from JDK (JDK 8 or lower):
1) JDK_DIR/lib/tools.jar
2) JRE_DIR/lib/rt.jar


# how to build the agent jar
1) go to the project folder: myJavaAgent/bin 
2) run command: jar -cmf ../MANIFEST.MF myJavaAgent.jar com/tianxiaohui/java/agent/*
3) 