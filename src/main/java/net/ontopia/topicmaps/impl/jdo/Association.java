/*
 * #!
 * Ontopia JDO
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.impl.jdo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;

@PersistenceCapable(table = "TM_ASSOCIATION")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Index(name = "TM_ASSOCIATION_IX_ID_TM_TYPE", members = {"id", "topicmap", "type"})
public class Association extends Scoped implements AssociationIF {
	
	@Persistent(mappedBy = "association")
	private Set<AssociationRole> roles = new HashSet<AssociationRole>(2);

	Association(Topic type) {
		super(type);
	}

	@Override
	protected String getClassIndicator() {
		return "A";
	}

	@Override
	public Collection<TopicIF> getRoleTypes() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Collection<AssociationRoleIF> getRoles() {
		return new HashSet<AssociationRoleIF>(roles);
	}

	@Override
	protected void beforeRemove() {
		
		// remove roles
		for (AssociationRole role : new HashSet<AssociationRole>(roles)) {
			role.remove();
		}
		
		roles.clear();
		
		// let super cleanup
		super.beforeRemove();
	}
	
	public AssociationRole makeAssociationRole(TopicIF role_type, TopicIF player) {
		AssociationRole role = new AssociationRole(this, 
				JDOTopicMapBuilder.checkAndCast(role_type, "Role type", Topic.class), 
				JDOTopicMapBuilder.checkAndCast(player, "Player", Topic.class));
		getPersistenceManager().makePersistent(role);
		roles.add(role);
		((Topic) player).associationRoleCreated(role);
		return role;
	}
}
