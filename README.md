openspaces-ldap
===============

OpenSpaces Security Manager for Active Directory integration. Once built, you'll have two jars:


##### manager (openspaces-ldap-manager.jar)
Spring security bridge for Active Directory. Should be configured via a spring-security.properties file

    com.gs.security.security-manager.class=org.openspaces.security.ldap.ActiveDirectorySpringSecurityManager
    spring-security-config-location = ../config/security/ldap-security-config.xml


##### test (openspaces-ldap-test.jar)
Integration test command line utility to verify correct configuration without having to start and XAP grid. 
    java -jar openspaces-ldap-test.jar /$GS_HOME/config/security/ldap-security-config.xml < username > < password >

## Directions
1. Place the openspaces-ldap-manager.jar into $GS_HOME/lib/optional/security.
1. Place also spring-ldap-{version}.jar, spring-ldap-core-{version}.jar and spring-security-ldap-{version}.jar into $GS_HOME/lib/optional/security. 
1. Verify the rights on openspaces-ldap-manager.jar
1. Update the appropriate $GS_HOME/config/security/security.properites file (see manager section above).
1. Ensure $GS_HOME/config/security/ldap-security-config.xml has the appropriate group mappings, and ldap connection information.

## Setting up a test environment

This instruction describes steps needed to create a VM, install and configure Active Directory.
Steps not strictly related to Active Directory installation/configuration (e.g. creating new VM)
are described shortly for the sake of simplicity. The following steps were performed on a virtual
machine launched in AWS cloud.

### Create VM
1. Launch Windows Server 2012 R2 Base (or similar) t2.micro type should be sufficient.
1. Configure security group so that ports TCP 3389 (RDP), TCP 389 (LDAP), TCP 53 and UDP 53 (DNS) are open for inbound connections.

### Install Active Directory

##### Set static IP:

1. Log into the machine.
1. Open command line and run command: 'ipconfig /all'.
1. Go to Network and Sharing Center, edit IPv4 properties of Ethernet connection, set checkbox to manually set values and copy
IP address, subnet mask, default gateway and preferred DNS server from 'Ethernet Adapter' section from output of ipconfig command.

##### Install Active Directory Domain Services

1. Open Server Manager, click on Add roles and features.
1. Set Role-based or feature-based installation type in 'Installation type' and set this server in 'Server selection'.
1. In server roles choose 'Active Directory Domain Services', click next in following steps and 'Install' in the last step.
1. When this feature is installed, in the Server Manager window, a notification will appear (a flag icon in the top part
of the window), click on it and then click on 'Promote this server to a domain controller' link.
1. In deployment configuration choose 'Add a new forest' and type a name of your domain.
In this tutorial name of the domain will be 'ldap-xap.gs.com'.
1. Set DSRM password (it will not be needed throughout this tutorial).
1. Click next until you reach Prerequisites Check window - there could be 2 warnings
(cryptography algorithms and missing authoritative parent zone), but they are not important. Click on install button
and wait until installation is finished. After installation the server will reboot automatically.
1. After the installation has finished, assign an elastic IP to the machine
(otherwise you won't be able to log into the machine again).

##### Add Active Directory user

1. Open Server Manager.
1. Click on Tools in the upper right corner and choose 'Active Directory Users and Computers' from the list.
1. Open ldap-xap.gs.com (the same as domain) node, click on Users container.
1. Choose 'Create a new user in the current container' from the taskbar.
1. Choose user logon name ('testuser' will be used for the purpose of this tutorial) and type the same value
in the first name field.
1. Click next and choose password for this user ('1234Pass' would be used for this user in tutorial),
uncheck all checkboxes below, click next and then finish.
You can verify that the user has been created by clicking on Users container. You should be able to find this user
in the list (name 'testuser' in case of this tutorial).

##### Add user
1. Go to Control Panel/User accounts and add a new user.
1. Choose the same name and domain used before for Active Directory user (testuser and ldap-xap.gs.com,
respectively, in case of this tutorial).
1. Choose this user to be administrator user.
1. You can verify the user was created correctly and will have access to Active Directory by running cmd.exe
and typing command: `ldifde -f all.txt -b testuser ldap-xap.gs.com *`
and then type password (1234Pass for this tutorial). You should see message similar to
'The command has completed successfully'. The command will export Active Directory data to all.txt file
(content is not important, only the fact that the command was successful).

##### Configure openspaces-ldap project

1. This step is not strictly related with the project, but is needed to access the correct machine using hostname.
Add a hostname mapping for the machine (for Unix-family systems you need to edit / etc/hosts,
for Windows-family C:\Windows\system32\drivers\etc\hosts). Hostname should be name of domain that was chosen for
Active Directory, IP should be the elastic IP of the machine.
1. Modify ldap-security-config.xml: in ldapAuthenticationProviderBean change both constructor-arg values:
the first one should contain domain name ('ldap-xap.gs.com') and the second one should be URL in
the following form: ldap://ldap-xap.gs.com (use different names if your domain is not the same as in tutorial).
1. Rebuild project by running mvn clean package.
1. Go to test/target directory.
1. Run command
`java -jar openspaces-ldap-test.jar ../../ldap-security-config.xml testuser 1234Pass` (replace username and password with values that you used earlier).
The command should return list of Authorities for the current user.