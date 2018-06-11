package com.t3c.anchel;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.t3c.anchel.gateway.ftp.ExecGatewayFtpServer;
import com.t3c.anchel.gateway.ftp.ServerInitDatabase;


public class GatewayServerListener extends ContextLoaderListener {

	public void contextInitialized(ServletContextEvent arg0) {

		File waarpFile = new File(this.getClass().getClassLoader().getResource("config-serverA.xml").getFile());
		File gatewayFile = new File(this.getClass().getClassLoader().getResource("Gg-FTP.xml").getFile());
		String waarppath = null;
		String gatewayppath = null;
		if (waarpFile.exists() && gatewayFile.exists()) {
			waarppath = waarpFile.toString();
			gatewayppath = gatewayFile.toString();
		}
		String[] configFiles = { gatewayppath, waarppath };
		Properties properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("waarpdb.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String propcondition = properties.getProperty("com.sgs.waarpdb.auto");
		String mycondition1 = new String("create");
		if (propcondition.equals(mycondition1)) {
			String[] gatearray = { gatewayppath, "-initdb" };
			ServerInitDatabase.initGatewayDB(gatearray);
		}
		ExecGatewayFtpServer.initGatewayServer(configFiles);
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		AbandonedConnectionCleanupThread.checkedShutdown();
		System.out.println("GatewayServer terminated");
	}
}
