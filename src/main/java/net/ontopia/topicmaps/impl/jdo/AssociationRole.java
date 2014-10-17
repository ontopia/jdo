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

import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;

@PersistenceCapable(table = "TM_ASSOCIATION_ROLE")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Indices({
		@Index(name = "TM_ASSOCIATION_ROLE_IX_ID_ASSOCIATION", members = {"id", "association"}),
		@Index(name = "TM_ASSOCIATION_ROLE_IX_ID_PLAYER", members = {"id", "player"}),
		@Index(name = "TM_ASSOCIATION_ROLE_IX_ID_TOPICMAP_PLAYER_TYPE_ASSOCIATION", members = {"id", "topicmap", "player", "type", "association"}),
		@Index(name = "TM_ASSOCIATION_ROLE_IX_ID_TOPICMAP_TYPE", members = {"id", "topicmap", "type"}),
		@Index(name = "TM_ASSOCIATION_ROLE_IX_ASSOCIATION", members = {"association"}),
		@Index(name = "TM_ASSOCIATION_ROLE_IX_PLAYER", members = {"player"})
})
public class AssociationRole extends Reifiable implements AssociationRoleIF {
	
	@Persistent(name = "player", column = "player")
	private Topic player;
	
	@Persistent(name = "association", column = "association")
	private Association association;

	@Persistent(name = "type", column = "type")
	private Topic type;

	AssociationRole(Association association, Topic type, Topic player) {
		super((TopicMap) association.getTopicMap());
		this.association = association;
		this.player = player;
		this.type = type;
	}

	@Override
	protected String getClassIndicator() {
		return "R";
	}

	@Override
	public AssociationIF getAssociation() {
		return association;
	}

	@Override
	public TopicIF getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(TopicIF player) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (player == null) throw new NullPointerException("Player cannot be null");
		this.player = (Topic) player;
	}
	
	@Override
	public TopicIF getType() {
		return type;
	}

	@Override
	public void setType(TopicIF type) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (type == null) throw new NullPointerException("Type cannot be null");
		this.type = (Topic) type;
	}

	@Override
	protected void beforeRemove() {
		super.beforeRemove();

		// consistency check: 0-role associations are not allowed
		// todo: on commit?
//		if (association.getRoles().isEmpty()) {
//			association.remove();
//		}

		// unregister at the association
		association.removeRole(this);
	}
}
