package org.openspaces.security.ldap;

import com.gigaspaces.security.authorities.*;
import com.gigaspaces.security.directory.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:/ldap-security-config-test.xml")
public class SecurityManagerValidCasesTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActiveDirectorySpringSecurityManager manager;

    private Privilege[] grantedPrivileges;

    private Privilege[] forbiddenPrivileges;

    private String role;

    public SecurityManagerValidCasesTest(String role, Privilege[] grantedPrivileges, Privilege[] forbiddenPrivileges){
        applicationContext = new ClassPathXmlApplicationContext("ldap-security-config-test.xml");
        manager = applicationContext.getBean("activeDirectorySpringSecurityManager", ActiveDirectorySpringSecurityManager.class);
        authenticationManager = applicationContext.getBean("authenticationManager", AuthenticationManager.class);
        this.grantedPrivileges = grantedPrivileges;
        this.forbiddenPrivileges = forbiddenPrivileges;
        this.role = role;
    }

    @Parameterized.Parameters(name = "{index}: add({0}+{1})={2}")
    public static Iterable<Object[]> data1() {
        List<Object[]> parameters = new ArrayList<Object[]>();
        Privilege[] administratorGrantedPrivileges = {GridAuthority.GridPrivilege.MANAGE_GRID, GridAuthority.GridPrivilege.MANAGE_PU, GridAuthority.GridPrivilege.PROVISION_PU,
                MonitorAuthority.MonitorPrivilege.MONITOR_JVM, MonitorAuthority.MonitorPrivilege.MONITOR_PU, SpaceAuthority.SpacePrivilege.WRITE,
                SpaceAuthority.SpacePrivilege.READ, SpaceAuthority.SpacePrivilege.TAKE, SpaceAuthority.SpacePrivilege.ALTER, SpaceAuthority.SpacePrivilege.EXECUTE};
        Privilege[] administratorForbiddenPrivileges = {SystemAuthority.SystemPrivilege.MANAGE_ROLES};
        Privilege[] deployerGrantedPrivileges = {GridAuthority.GridPrivilege.MANAGE_PU, MonitorAuthority.MonitorPrivilege.MONITOR_JVM, MonitorAuthority.MonitorPrivilege.MONITOR_PU};
        Privilege[] deployerForbiddenPrivileges = {SystemAuthority.SystemPrivilege.MANAGE_ROLES, SpaceAuthority.SpacePrivilege.EXECUTE, SpaceAuthority.SpacePrivilege.READ};
        Privilege[] cacheUserGrantedPrivileges = {MonitorAuthority.MonitorPrivilege.MONITOR_PU, SpaceAuthority.SpacePrivilege.WRITE, SpaceAuthority.SpacePrivilege.READ, SpaceAuthority.SpacePrivilege.TAKE, SpaceAuthority.SpacePrivilege.ALTER, SpaceAuthority.SpacePrivilege.EXECUTE};
        Privilege[] cacheUserForbiddenPrivileges = {GridAuthority.GridPrivilege.MANAGE_GRID, MonitorAuthority.MonitorPrivilege.MONITOR_JVM};
        Privilege[] cacheReadOnlyGrantedPrivileges = {MonitorAuthority.MonitorPrivilege.MONITOR_PU, SpaceAuthority.SpacePrivilege.READ};
        Privilege[] cacheReadOnlyForbiddenPrivileges = {SystemAuthority.SystemPrivilege.MANAGE_ROLES, SpaceAuthority.SpacePrivilege.EXECUTE, GridAuthority.GridPrivilege.MANAGE_PU, MonitorAuthority.MonitorPrivilege.MONITOR_JVM};
        parameters.add(new Object[]{"Administrators", administratorGrantedPrivileges, administratorForbiddenPrivileges});
        parameters.add(new Object[]{"Deployers", deployerGrantedPrivileges, deployerForbiddenPrivileges});
        parameters.add(new Object[]{"Cache_Users", cacheUserGrantedPrivileges, cacheUserForbiddenPrivileges});
        parameters.add(new Object[]{"Cache_ReadOnly", cacheReadOnlyGrantedPrivileges, cacheReadOnlyForbiddenPrivileges});
        return parameters;
    }

    @Test
    public void testAuthorities(){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role));
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken("username", "password", authorities);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticationToken);
        com.gigaspaces.security.Authentication authentication = manager.authenticate(new User("username", "password"));
        GrantedAuthorities grantedAuthorities = authentication.getGrantedAuthorities();
        for (Privilege privilege : grantedPrivileges){
            assertTrue(grantedAuthorities.isGranted(privilege));
        }
        for (Privilege privilege : forbiddenPrivileges){
            assertFalse(grantedAuthorities.isGranted(privilege));
        }
    }

}
