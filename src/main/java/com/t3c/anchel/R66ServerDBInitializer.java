package com.t3c.anchel;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import com.t3c.anchel.openr66.server.ServerInitDatabase;


public class R66ServerDBInitializer {

	public void initdb() {
		Properties properties = new Properties();
		Connection conn = null;
		Statement stmt = null;
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream("waarpdb.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String propcondition = properties.getProperty("com.sgs.waarpdb.auto");
		String mycondition1 = new String("create");
		String mycondition2 = new String("update");
		File waarpconfigFile = new File(this.getClass().getClassLoader().getResource("config-serverA.xml").getFile());
		File waarpauthFile = new File(this.getClass().getClassLoader().getResource("OpenR66-authent-A.xml").getFile());
		File waarplimitFile = new File(this.getClass().getClassLoader().getResource("limitConfiga.xml").getFile());
		String waarpconfig = waarpconfigFile.toString();
		String directory = waarpconfig.substring(0, waarpconfig.lastIndexOf(File.separator));
		String[] update = { waarpconfig, "-upgradeDb" };
		if (propcondition.equals(mycondition1)) {
			String[] waarpdbinit = { waarpconfig, "-initdb" };
			String[] loadBusiness = { waarpconfig, "-loadBusiness", waarpconfig };
			String[] loadAlias = { waarpconfig, "-loadAlias", waarpconfig };
			String[] loadRoles = { waarpconfig, "-loadRoles", waarpconfig };
			String[] loadRules = { waarpconfig, "-dir", directory };
			String[] loadAuths = { waarpconfig, "-auth", waarpauthFile.toString() };
			String[] loadLimit = { waarpconfig, "-limit", waarplimitFile.toString() };
			ServerInitDatabase.initR66database(waarpdbinit);

			ServerInitDatabase.initR66database(loadBusiness);
			ServerInitDatabase.initR66database(loadAlias);
			ServerInitDatabase.initR66database(loadRoles);
			ServerInitDatabase.initR66database(loadRules);
			ServerInitDatabase.initR66database(loadAuths);
			ServerInitDatabase.initR66database(loadLimit);
			ServerInitDatabase.initR66database(update);

			try {
				String dbDriver = properties.getProperty("com.sgs.waarpdb.driver");
				String dbServer = properties.getProperty("com.sgs.waarpdb.server");
				String dbuser = properties.getProperty("com.sgs.waarpdb.user");
				String dbpass = properties.getProperty("com.sgs.waarpdb.pass");

				Class.forName(dbDriver);
				conn = DriverManager.getConnection(dbServer, dbuser, dbpass);
				stmt = conn.createStatement();

				String s3mappingtab = "CREATE TABLE IF NOT EXISTS S3BUCKETMAPPING" + "(id int(11) NOT NULL AUTO_INCREMENT,"
						+ "filename varchar(500) NOT NULL," +"uuid varchar(500) NOT NULL,"
						+ "specialKey varchar(450) NULL,"
						+ "s3fileurl varchar(450) NULL," + "processedOn DATETIME,"+ "deleted char(1) NOT NULL,"
						+ "PRIMARY KEY (id))";

				stmt.executeUpdate(s3mappingtab);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (propcondition.equals(mycondition2)) {
			ServerInitDatabase.initR66database(update);
		}
	}
}
