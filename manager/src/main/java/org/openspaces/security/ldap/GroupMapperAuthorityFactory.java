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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import com.gigaspaces.security.Authority;
import com.gigaspaces.security.AuthorityFactory;

/**
 * A factory for creating an {@link Authority} instance list back from authority map representation.
 * This requires {@link ActiveDirectoryGroupMapper} to be configuration in the spring security configuraiton
 * at runtime. 
 * 
 * @author Ali Hodroj
 * @since 9.7.1
 */
public final class GroupMapperAuthorityFactory {
	
	private static final String AUTHORITY_MAP_DELIM = ",";

	public static ArrayList<Authority> create(Collection<? extends GrantedAuthority> grantedAuthorities, Map authorityMap) {
		ArrayList<Authority> authoritiesList = new ArrayList<Authority>();
				
		for (GrantedAuthority grantedAuthority : grantedAuthorities) {
			String memberOf = grantedAuthority.getAuthority().trim();
			if(authorityMap.containsKey(memberOf)) {
				String gsAuthorityRules = (String)authorityMap.get(memberOf);
				String[] split = gsAuthorityRules.split(AUTHORITY_MAP_DELIM);
				for(String authority : split) {
					Authority gsAuthority = AuthorityFactory.create(authority);
					authoritiesList.add(gsAuthority);
				}
			}
		}
		
		return authoritiesList;
	}
}