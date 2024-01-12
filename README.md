# old-tomcat-virtual-threads

This is a small POC to illustrate a discussion on StackOverflow question [Unable to Execute Platform Threads in Spring Boot Application - Only NIO Threads Working](https://stackoverflow.com/questions/77756813/unable-to-execute-platform-threads-in-spring-boot-application-only-nio-threads).

The standalone Java application configures embedded Tomcat 8.0.5, which does not support virtual threads, to use them. Optionally, a BIO Tomcat Protocol `org.apache.coyote.http11.Http11Protocol` is configured instead of default for this version of Tomcat NIO Protocol.

An example of embedded Tomcat basic configuration has been adapted from <a href="https://devcenter.heroku.com/articles/create-a-java-web-application-using-embedded-tomcat">Create a Java Web Application Using Embedded Tomcat</a>.

To test run `com.github.tomcat.virtualthreads.TomcatMain` and point a browser to <a href="http://localhost:8080/hello">http://localhost:8080/hello</a>.

Java 21 or later is required.