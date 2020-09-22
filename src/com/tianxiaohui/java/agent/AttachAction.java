package com.tianxiaohui.java.agent;

import java.io.IOException;
import java.util.Properties;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class AttachAction {
	public static void doAttach(String pid, String action) {
		try {
			VirtualMachine vm = VirtualMachine.attach(pid);
			System.out.println("Attached to target Java process: " + pid);
			Properties props = vm.getSystemProperties();
			System.out.println("Target JVM process system properties:");
			System.out.println("\t" + props);
			System.out.println();

			String agent = Attacher.class.getProtectionDomain().getCodeSource().getLocation().getFile();

			vm.loadAgent(agent, action);

			vm.detach();
			System.out.println("Detached to target Java process: " + pid);
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
