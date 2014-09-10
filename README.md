openspaces-ldap
===============

OpenSpaces Security Manager for Active Directory integration. Once built, you'll have two jars:


##### manager (openspaces-ldap-manager.jar)
Spring security bridge for Active Directory. Should be configured via a spring-security.properties file

    com.gs.security.security-manager.class=org.openspaces.security.ldap.ActiveDirectorySpringSecurityManager
    spring-security-config-location = ../config/security/ldap-security-config.xml


##### test (openspaces-ldap-test.jar)
Integration test command line utility to verify correct configuration without having to start and XAP grid. 
    java -jar openspaces-ldap-test.jar <GS_HOME>/config/security/ldap-security-config.xml <username> <password>

## Directions
1. Place the openspaces-ldap-manager.jar into <GS_HOME>/lib/optional/security.
1. Verify the rights on openspaces-ldap-manager.jar
1. Update the appropriate <GS_HOME>/config/security/security.properites file (see manager section above).
1. Ensure <GS_HOME>/config/security/ldap-security-config.xml has the appropriate group mappings, and ldap connection information.