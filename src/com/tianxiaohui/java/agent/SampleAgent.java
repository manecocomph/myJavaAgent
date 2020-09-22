package com.tianxiaohui.java.agent;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ReflectionException;

import com.sun.management.HotSpotDiagnosticMXBean;

public class SampleAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("in premain with Instrumentation parameter");
		doWork(agentArgs);
	}

	public static void premain(String agentArgs) {
		System.out.println("in premain without Instrumentation parameter");
		doWork(agentArgs);
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		System.out.println("in agentmain with Instrumentation parameter");
		doWork(agentArgs);
	}

	public static void agentmain(String agentArgs) {
		System.out.println("in agentmain without Instrumentation parameter");
		doWork(agentArgs);
	}

	private static void doWork(String cmd) {
		if (null == cmd || "".equals(cmd.trim())) {
			printUsage();
		}

		switch (cmd.toLowerCase()) {
		case "listmbean":
			listMBean();
			break;

		case "threaddump":
			captureThreadDump();
			break;

		case "heapdump":
			captureHeapDump();
			break;
			
		case "printdnscache":
			printDNSCache();
			break;

		default:
			printUsage();
		}
		
		System.out.println("Work done!");
	}

	private static void printUsage() {
		System.out.println("Which command do you want to run: listMBean, heapDump, threadDump, printDnsCache");
	}

	private static void listMBean() {
		System.out.println("listing MBean");
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		Set<ObjectInstance> instances = server.queryMBeans(null, null);

		Iterator<ObjectInstance> iterator = instances.iterator();

		while (iterator.hasNext()) {
			ObjectInstance instance = iterator.next();
			try {
				System.out.print("MBean:");
				System.out.print("\tClass Name: " + instance.getClassName());
				System.out.println("\tObject Name: " + instance.getObjectName());
				System.out.println();
				MBeanInfo beanInfo = server.getMBeanInfo(instance.getObjectName());

				MBeanAttributeInfo[] attrs = beanInfo.getAttributes();
				for (MBeanAttributeInfo attr : attrs) {
					System.out.println("\tattribute: (" + attr.getType() + ") " + attr.getName() + "\t: " + attr.getDescription());
				}
				System.out.println("");
				MBeanOperationInfo[] opers = beanInfo.getOperations();
				for (MBeanOperationInfo oper : opers) {
					System.out.print("\toperation: " + oper.getReturnType() + " " + oper.getName() + "(");
					
					MBeanParameterInfo[] params = oper.getSignature();
					for (int i = 0; i < params.length; i++) {
						MBeanParameterInfo param = params[i];
						System.out.print(param.getType() + " " + param.getName());
						if (i != (params.length - 1)) {
							System.out.print(", ");
						}
					}
					System.out.println(") :" + oper.getDescription());
				}

				System.out.println("");
			} catch (IntrospectionException e) {
				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * this code copy from: https://blogs.oracle.com/sundararajan/programmatically-dumping-heap-from-java-applications
	 */
	private static void captureHeapDump() {
		System.out.println("capture heap dump");
		try {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			HotSpotDiagnosticMXBean hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
			String fileName = "heap_" + System.currentTimeMillis() + ".hprof";
			hotspotMBean.dumpHeap(fileName, false);
			System.out.println("Heap file is generated: " + fileName);
		} catch (RuntimeException re) {
			re.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}
	
	/**
	 * this code copy from: https://www.baeldung.com/java-thread-dump
	 */
	private static void captureThreadDump() {
		System.out.println("capture thread dump");
		StringBuffer threadDump = new StringBuffer(System.lineSeparator());
	    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	    for(ThreadInfo threadInfo : threadMXBean.dumpAllThreads(true, true)) {
	        threadDump.append(threadInfo.toString());
	    }
	    System.out.println("Thread dump start");
	    System.out.println();
	    System.out.println(threadDump.toString());
	    System.out.println();
	    System.out.println("Thread dump end");
	}
	
	/**
	 * this code copy from: http://www.tianxiaohui.com/index.php/Java%E7%9B%B8%E5%85%B3/%E8%AF%BB%E5%8F%96-Java-%E5%BA%94%E7%94%A8%E7%9A%84-DNS-%E6%9F%A5%E8%AF%A2%E7%BC%93%E5%AD%98.html
	 */
	private static void printDNSCache() {
		System.out.println("print DNS cache");
		// dump the good DNS entries
	      String addressCache = "addressCache";
	      System.out.println("\t---------" + addressCache + "---------");
	      try {
			printDNSCacheByCacheName(addressCache);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	      // dump the bad DNS entries
	      String negativeCache = "negativeCache";
	      System.out.println("\t---------" + negativeCache + "---------");
	      try {
			printDNSCacheByCacheName(negativeCache);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void printDNSCacheByCacheName(String cacheName) throws Exception {
	      Class<InetAddress> iaclass = InetAddress.class;
	      Field acf = iaclass.getDeclaredField(cacheName);
	      acf.setAccessible(true);
	      Object addressCache = acf.get(null);
	      Class cacheClass = addressCache.getClass();
	      Field cf = cacheClass.getDeclaredField("cache");
	      cf.setAccessible(true);
	      Map<String, Object> cache = (Map<String, Object>) cf.get(addressCache);
	      for (Map.Entry<String, Object> hi : cache.entrySet()) {
	         Object cacheEntry = hi.getValue();
	         Class cacheEntryClass = cacheEntry.getClass();
	         Field expf = cacheEntryClass.getDeclaredField("expiration");
	         expf.setAccessible(true);
	         long expires = (Long) expf.get(cacheEntry);

	         Field af = cacheEntryClass.getDeclaredField("addresses"); // JDK 1.7, older version maybe "address"
	         af.setAccessible(true);
	         InetAddress[] addresses = (InetAddress[]) af.get(cacheEntry);
	         List<String> ads = new ArrayList<String>(addresses.length);

	         for (InetAddress address : addresses) {
	            ads.add(address.getHostAddress());
	         }

	         System.out.println("\t" + hi.getKey() + " " + new Date(expires) +" " + ads);
	      }
	   }
}
