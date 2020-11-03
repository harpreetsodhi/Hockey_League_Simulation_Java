package com.persistencemodel;
import com.inputoutputmodel.DisplayToUser;
import com.inputoutputmodel.IDisplayToUser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Properties;

public class ConnectionDB {
	private static final HashMap<String, String> propsMap = new HashMap<>();
	protected Connection con = null;

	private IDisplayToUser displayToUser;

	public ConnectionDB(){
		displayToUser = new DisplayToUser();
	}

	static {
		InputStream inputStream = null;
		boolean isFileNotOnServer = true;
		IDisplayToUser displayToUser = new DisplayToUser();
		try {
			Path currentRelativePath = Paths.get("");
			String dirPath = currentRelativePath.toAbsolutePath().toString();
			try {
				dirPath = dirPath.substring(0, dirPath.lastIndexOf("target"));
			} catch (StringIndexOutOfBoundsException se) {
			}
			Properties property = new Properties();
			String fileName = "application.properties";
			try {
				inputStream = new FileInputStream(dirPath + fileName);
			} catch (FileNotFoundException f) {
				inputStream = ConnectionDB.class.getClassLoader().getResourceAsStream(fileName);
			}
			if (inputStream != null) {
				property.load(inputStream);
				isFileNotOnServer = false;
			} else {
				throw new FileNotFoundException("File '" + fileName + "' not found in the classpath");
			}
			if (isFileNotOnServer) {
				inputStream = ConnectionDB.class.getClassLoader().getResourceAsStream(fileName);
				if (inputStream != null) {
					property.load(inputStream);
				} else {
					throw new FileNotFoundException("File '" + fileName + "' not found in the classpath");
				}
			}
			propsMap.put("driver", property.getProperty("datasource.driver-class-name"));
			propsMap.put("url", property.getProperty("datasource.url"));
			propsMap.put("username", property.getProperty("datasource.username"));
			propsMap.put("password", property.getProperty("datasource.password"));

		} catch (Exception e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	private void connect() {
		try {
			System.setProperty("Jdbc.drivers", propsMap.get("driver"));
			Properties properties = new Properties();
			properties.put("user", propsMap.get("username"));
			properties.put("password", propsMap.get("password"));
			String url = propsMap.get("url");
			this.con = DriverManager.getConnection(url, properties);
		} catch (Exception err) {
			displayToUser.displayMsgToUser("Oops! unable to connect to datasource: " + err);
			try {
				this.con.close();
			} catch (Exception ignored) {
			}
		}
	}

	private void disconnect() {
		try {
			this.con.close();
		} catch (Exception e) {
			displayToUser.displayMsgToUser("Oops! unable to close the db connection: " + e);
		}
	}

	void getConnection() {
		connect();
	}

	void closeConnection() {
		disconnect();
	}
}