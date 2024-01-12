package com.github.tomcat.virtualthreads;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.AbstractProtocol;

/**
 * Configures embedded Tomcat to use virtual threads and starts it.
 * 
 * Adopted from 
 * <a href="https://devcenter.heroku.com/articles/create-a-java-web-application-using-embedded-tomcat">
 * Create a Java Web Application Using Embedded Tomcat</a>.
 * 
 * <br/> Environment variables PORT and USE_BIO used to define a Tomcat port 
 * and whether a BIO protocol should be used instead of default NIO protocol.  
 */
public class TomcatMain {

	public static void main(String[] args) throws Exception {
		
		if (Runtime.version().feature() < 21)
			throw new IllegalStateException("Java version must be 21 or greater");

		String webappDirLocation = "src/main/webapp/";
		Tomcat tomcat = new Tomcat();

		// The port that we should run on can be set into an environment variable
		// Look for that variable and default to 8080 if it isn't there.
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}

		tomcat.setPort(Integer.valueOf(webPort));

		StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
		System.out.println("configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		// Servlet 3.0 annotation will work
		File additionWebInfClasses = new File("target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
		ctx.setResources(resources);

		Connector connector = null;
		String useBioConnector = System.getenv("USE_BIO");
		if (useBioConnector == null || useBioConnector.isEmpty()) {
		// reuse existing Connector
			connector = tomcat.getConnector();
		} else {
			// redefine Connector
			connector = new Connector("org.apache.coyote.http11.Http11Protocol");
			connector.setPort(Integer.valueOf(webPort));
			tomcat.getService().addConnector(connector);
			tomcat.setConnector(connector);
		}
		
		@SuppressWarnings("rawtypes")
		AbstractProtocol protocol = (AbstractProtocol) connector.getProtocolHandler();
		protocol.setExecutor((r) -> {
			Thread.ofVirtual().start(r);
		});

		tomcat.start();
		tomcat.getServer().await();
	}


}
