/*******************************************************************************
 * 
 * Copyright (c) 2014 GigaSpaces Technologies, Inc. All rights reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 ******************************************************************************/

package org.openspaces.security.ldap;

import java.util.Arrays;
import java.util.Properties;

import com.gigaspaces.security.directory.User;
import com.gigaspaces.security.directory.UserDetails;

/**
 * A simple test utility to verify GigaSpaces XAP security settings against 
 * a configured provider. 
 * 
 * @author Ali Hodroj
 * @since 9.7.1
 */

public class Main {
	public static void main(String[] args) {
		
		if(args.length < 3) {
			System.out.println("usage: <spring config> <username> <password>");
			return;
		}
		
		String configLocation = args[0];
		String username = args[1];
		String password = args[2];
		
		Properties props = new Properties();
		props.setProperty("spring-security-config-location", configLocation);
		ActiveDirectorySpringSecurityManager securityManager = new ActiveDirectorySpringSecurityManager();
		
		securityManager.init(props);
				
		com.gigaspaces.security.Authentication authenticate = securityManager.authenticate(new User(username, password));
		
		UserDetails userDetails = authenticate.getUserDetails();
		System.out.println("user: " + userDetails.getUsername() + " password: "	 + userDetails.getPassword() + " authorities: " + Arrays.toString(userDetails.getAuthorities()));
	}
}