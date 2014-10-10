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
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class TMObject implements TMObjectIF {
	
	@PrimaryKey
	@Persistent(name = "id", valueStrategy=IdGeneratorStrategy.NATIVE)
	protected long id;

	@Persistent(name = "topicmap", column = "topicmap")
	protected TopicMap topicmap;
	
	@Persistent(mappedBy = "object")
	protected Set<IdentityLocator> itemIdentifiers = new HashSet<IdentityLocator>();

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
		if (item_identifier == null) throw new NullPointerException("Item identifier cannot be null");
		itemIdentifiers.add(new IdentityLocator(item_identifier, this, IdentityLocator.ITEM_IDENTIFIER));
	}

	public void removeItemIdentifier(LocatorIF item_identifier) {
		removeLocator(itemIdentifiers, item_identifier);
	}

	public void remove() {
		if (isReadOnly()) throw new ReadOnlyException();
		beforeRemove();
		getPersistenceManager().deletePersistent(this);
		
		// disconnect from topicmap, ontopia uses this as check
		topicmap = null;
	}

	// implementation for TMObject: remove item identifiers on object remove
	protected void beforeRemove() {
		PersistenceManager pm = getPersistenceManager();
		for (IdentityLocator idLocator : itemIdentifiers) {
			pm.deletePersistent(idLocator);
		}
	}

	@Override
	public String toString() {
		String name = "jdo." + getClass().getSimpleName();
		if (this instanceof TopicIF) return ObjectStrings.toString(name, (TopicIF) this);
		if (this instanceof AssociationIF) return ObjectStrings.toString(name, (AssociationIF) this);
		if (this instanceof AssociationRoleIF) return ObjectStrings.toString(name, (AssociationRoleIF) this);
		if (this instanceof OccurrenceIF) return ObjectStrings.toString(name, (OccurrenceIF) this);
		if (this instanceof TopicNameIF) return ObjectStrings.toString(name, (TopicNameIF) this);
		if (this instanceof VariantNameIF) return ObjectStrings.toString(name, (VariantNameIF) this);
		if (this instanceof TopicMapIF) return ObjectStrings.toString(name, (TopicMapIF) this);
		return super.toString();
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
	
	/**
	 * INTERNAL: Finds and removes a persisted locator in a set of locators.
	 * @param set
	 * @param remove 
	 */
	protected void removeLocator(Set<? extends JDOLocator> set, LocatorIF remove) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (remove == null) throw new NullPointerException("Locator cannot be null");
		JDOLocator toRemove = null;
		for (JDOLocator locator : set) {
			if (locator.equals(remove)) {
				toRemove = locator;
				break;
			}
		}
		if (toRemove != null) {
			set.remove(toRemove);
		}
	}
}
