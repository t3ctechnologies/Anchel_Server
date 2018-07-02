package com.t3c.anchel;

import java.io.File;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.waarp.openr66.protocol.exception.OpenR66ProtocolPacketException;
import org.waarp.openr66.server.R66Server;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

public class R66ServerListener extends ContextLoaderListener {

	private static final Logger logger = LoggerFactory.getLogger(GatewayServerListener.class);

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			new R66ServerDBInitializer().initdb();
			File configFile = new File(this.getClass().getClassLoader().getResource("config-serverA.xml").getFile());
			String path = null;
			if (configFile.exists()) {
				path = configFile.toString();
			}
			String[] waarpconfig = { path };
			logger.debug("Waarp R66 server databse is initiating");
			R66Server.initR66Server(waarpconfig);
		} catch (OpenR66ProtocolPacketException e) {
			e.printStackTrace();
			logger.error("Waarp R66 server is not started " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Waarp R66 database is not initiated properly " + e.getMessage());
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		AbandonedConnectionCleanupThread.checkedShutdown();
		logger.debug("Waarp R66 server is terminated");
	}

}
