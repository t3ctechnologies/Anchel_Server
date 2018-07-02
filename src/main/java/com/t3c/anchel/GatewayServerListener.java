package com.t3c.anchel;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.waarp.gateway.ftp.ExecGatewayFtpServer;
import org.waarp.gateway.ftp.ServerInitDatabase;

public class GatewayServerListener extends ContextLoaderListener {

	private static final Logger logger = LoggerFactory.getLogger(GatewayServerListener.class);

	public void contextInitialized(ServletContextEvent arg0) {

		logger.debug("Waarp gateway server is starting");
		File waarpFile = new File(this.getClass().getClassLoader().getResource("config-serverA.xml").getFile());
		File gatewayFile = new File(this.getClass().getClassLoader().getResource("Gg-FTP.xml").getFile());
		String waarppath = null;
		String gatewayppath = null;
		if (waarpFile.exists() && gatewayFile.exists()) {
			waarppath = waarpFile.toString();
			gatewayppath = gatewayFile.toString();
		}
		String[] configFiles = { gatewayppath, waarppath };
		logger.debug("Waarp gateway server is loading properties file");
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
			logger.debug("Waarp gateway server databse is initiating");
			ServerInitDatabase.initGatewayDB(gatearray);
			logger.debug("Waarp gateway server databse is initiated");
		}
		logger.debug("Waarp gateway server is starting");
		ExecGatewayFtpServer.initGatewayServer(configFiles);
		logger.debug("Waarp gateway server is started");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (driver.getClass().getClassLoader() == cl) {
				try {
					logger.info("Deregistering JDBC driver {}", driver);
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e) {
					e.printStackTrace();
					logger.error("Error deregistering JDBC driver {}", driver, e);
				}
			} else {
				logger.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader",
						driver);
			}
		}
		logger.debug("Waarp gateway server terminated");
	}
}
