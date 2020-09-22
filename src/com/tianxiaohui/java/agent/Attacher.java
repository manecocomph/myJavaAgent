package com.tianxiaohui.java.agent;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class Attacher {

	public static void main(String[] args) {
		if (null == args || args.length < 1 || "".equals(args[0])) {
			System.out.println("Java pid is a mandatory argument.");
			System.out.println("like: ");
			System.out.println("\tjava -jar myJavaAgent.jar <pid> listMBean ");
			System.out.println("\tjava -jar myJavaAgent.jar <pid> heapDump ");
			System.out.println("\tjava -jar myJavaAgent.jar <pid> threadDump ");
			System.out.println("\tjava -jar myJavaAgent.jar <pid> printDnsCache ");
			
			System.out.println("on *unx platform, you can get Java pid by command: pgrep java or jps");
			return;
		}
		
		String action = args.length > 1 ? args[1] : "listMBean";
		Set<String> actions = new HashSet<String>(4);
		if (!actions.contains(action)) {
			action = "listMBean";//Default value
		}
		AttachAction.doAttach(args[0], action);
	}

	/**
	 *  copied from https://github.com/gridkit/jvm-attach/blob/jvm-attach-api/src/main/java/org/gridkit/lab/jvm/attach/AttachAPI.java
	 *  @author Alexey Ragozin (alexey.ragozin@gmail.com)
	 */
	static {
		try {
			System.out.println("Try to load attach API from JDK");
			if (ClassLoader.getSystemClassLoader() instanceof URLClassLoader) {
				// Try to add tools.jar into classpath
				String javaHome = System.getProperty("java.home");
				String toolsJarURL = "file:" + javaHome + "/../lib/tools.jar";

				// Make addURL public
				Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);

				URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
				if (sysloader.getResourceAsStream("/com/sun/tools/attach/VirtualMachine.class") == null) {
					method.invoke(sysloader, (Object) new URL(toolsJarURL));
					Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
					Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.AttachNotSupportedException");
				}
			} else {
				// is it Java 9 or above?
				// let's hope tools classes are already on classpath
				Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
				Thread.currentThread().getContextClassLoader().loadClass("com.sun.tools.attach.AttachNotSupportedException");
			}
			System.out.println("Attach API is loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

