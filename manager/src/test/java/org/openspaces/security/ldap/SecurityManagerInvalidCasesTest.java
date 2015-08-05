package org.openspaces.security.ldap;

import com.gigaspaces.security.AuthenticationException;
import com.gigaspaces.security.directory.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
        locations = "classpath:/ldap-security-config-test.xml")
public class SecurityManagerInvalidCasesTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActiveDirectorySpringSecurityManager manager;

    @Test(expected = AuthenticationException.class)
    public void testNotAuthenticated(){
        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        manager.authenticate(new User("username", "password"));
    }

    @Test(expected = AuthenticationException.class)
    public void testEmptyAuthorities(){
        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password", new ArrayList<GrantedAuthority>());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        manager.authenticate(new User("username", "password"));
    }

}
