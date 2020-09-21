package com.tianxiaohui.java.agent;

import java.io.IOException;
import java.util.Properties;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class Attacher {

	public static void main(String[] args) {
		if (null == args || args.length < 1 || "".equals(args[0])) {
			System.out.println("Java pid is a mandatory argument.");
			System.out.println("like: java -jar myAgent.jar 32156");
			System.out.println("on *unx platform, you can get Java pid by command: pgrep java");
			return;
		}
		
		try {
			VirtualMachine vm = VirtualMachine.attach(args[0]);
			Properties props = vm.getSystemProperties();
			System.out.println("target JVM process System Properties:");
			System.out.println(props);
			System.out.println();
			
			System.out.println(Attacher.class.getProtectionDomain().getCodeSource().getLocation());
			// agent file generated with command: jar -cmf ../MANIFEST.MF myJavaAgent.jar com/tianxiaohui/java/agent/*
			String agent = "./bin/myJavaAgent.jar";

			// load agent into target VM
			vm.loadAgent(agent, "printdnscache");

			// detach
			vm.detach();
		} catch (AttachNotSupportedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AgentLoadException e) {
			e.printStackTrace();
		} catch (AgentInitializationException e) {
			e.printStackTrace();
		}
	}

}
