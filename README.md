openspaces-ldap
===============

OpenSpaces Security Manager for Active Directory integration. Once built, you'll have two jars:


##### manager (openspaces-ldap-manager.jar)
Spring security bridge for Active Directory. Should be configured via a spring-security.properties file

     com.gs.security.security-manager.class=org.openspaces.security.spring.ActiveDirectorySpringSecurityManager
     spring-security-config-location = ../config/security/ldap-security-config.xml


##### test (openspaces-ldap-test.jar)
Integration test command line utility to verify correct configuration without having to start and XAP grid. 


