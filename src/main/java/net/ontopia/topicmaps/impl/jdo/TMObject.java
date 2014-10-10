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
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class TMObject implements TMObjectIF {
	
	@PrimaryKey
	@Persistent(name = "id", valueStrategy=IdGeneratorStrategy.NATIVE)
	protected long id;

	@Persistent(name = "topicmap", column = "topicmap")
	protected TopicMap topicmap;
	
	@Persistent(mappedBy = "object")
	protected Collection<IdentityLocator> itemIdentifiers;

	TMObject() {
	}

	TMObject(TopicMap topicmap) {
		this.topicmap = topicmap;
	}
	
	protected abstract String getClassIndicator();
	
	public String getObjectId() {
		return getClassIndicator() + id;
	}

	// todo: actually implement on setters
	public boolean isReadOnly() {
		return getTopicMap().getStore().isReadOnly();
	}

	public TopicMapIF getTopicMap() {
		return topicmap;
	}

	public Collection<LocatorIF> getItemIdentifiers() {
		return new HashSet<LocatorIF>(itemIdentifiers);
	}

	public void addItemIdentifier(LocatorIF item_identifier) throws ConstraintViolationException {
		if (isReadOnly()) throw new ReadOnlyException();
		
		if (item_identifier instanceof IdentityLocator) {
			IdentityLocator idLocator = (IdentityLocator) item_identifier;
			if (idLocator.isItemIdentifier()) {
				itemIdentifiers.add(idLocator);
				return;
			}
		}
		
		itemIdentifiers.add(new IdentityLocator(item_identifier, this, IdentityLocator.ITEM_IDENTIFIER));
	}

	public void removeItemIdentifier(LocatorIF item_identifier) {
		if (isReadOnly()) throw new ReadOnlyException();
		
		if (item_identifier instanceof IdentityLocator) {
			itemIdentifiers.remove((IdentityLocator) item_identifier);
		} else {
			IdentityLocator toRemove = null;
			for (IdentityLocator idLocator : itemIdentifiers) {
				if (idLocator.getAddress().equals(item_identifier.getAddress())) {
					toRemove = idLocator;
					break;
				}
			}
			if (toRemove != null) {
				itemIdentifiers.remove(toRemove);
			}
		}
	}

	public void remove() {
		if (isReadOnly()) throw new ReadOnlyException();
		beforeRemove();
		getPersistenceManager().deletePersistent(this);
	}

	// implementation for TMObject: remove item identifiers on object remove
	protected void beforeRemove() {
		PersistenceManager pm = getPersistenceManager();
		for (IdentityLocator idLocator : itemIdentifiers) {
			pm.deletePersistent(idLocator);
		}
	}
	
	/* ---
		JDO utilities
	--- */
	
	public long getLongId() {
		return id;
	}

	protected PersistenceManager getPersistenceManager() {
		return JDOHelper.getPersistenceManager(this);
	}
}
