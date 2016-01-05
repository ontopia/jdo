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
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
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
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSource;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class TMObject implements TMObjectIF {
	
	@NotPersistent
	protected final Logger logger;
	
	@PrimaryKey
	@Persistent(name = "id", valueStrategy=IdGeneratorStrategy.NATIVE)
	protected long id;

	@Persistent(name = "topicmap", column = "topicmap")
	protected TopicMap topicmap;
	
	@Persistent(mappedBy = "object", dependentElement = "true")
	protected Set<ItemIdentifier> itemIdentifiers = new HashSet<ItemIdentifier>();
	
	@NotPersistent
	protected final boolean readOnly;

	TMObject(TopicMap topicmap) {
		this.topicmap = topicmap;
		logger = LoggerFactory.getLogger(getClass());
		readOnly = ((topicmap == null) || topicmap.getStore().isReadOnly());
	}
	
	protected abstract String getClassIndicator();
	
	@Override
	public String getObjectId() {
		return getClassIndicator() + id;
	}

	// todo: actually implement on setters
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public TopicMapIF getTopicMap() {
		if (isDeleted()) return null;
		return topicmap;
	}

	@Override
	public Collection<LocatorIF> getItemIdentifiers() {
		return new HashSet<LocatorIF>(itemIdentifiers);
	}

	@Override
	public void addItemIdentifier(LocatorIF item_identifier) throws ConstraintViolationException {
		if (isDeleted()) throw new ConstraintViolationException("Cannot modify item identifiers when object isn't attached to a topic map.");
		if (isReadOnly()) throw new ReadOnlyException();
		if (item_identifier == null) throw new NullPointerException("Item identifier cannot be null");
		
		// TMDM constraint: II cannot be SI of a topic
		TMObjectIF existing = topicmap.getTopicBySubjectIdentifier(item_identifier);
		if (existing != null && existing != this && (this instanceof TopicIF)) {
			throw new UniquenessViolationException("Another topic " + existing + " already has this item identifier as its subject identifier: " + item_identifier + " (" + this + ")");
		}
		
		try {
			ItemIdentifier itemIdentifier = new ItemIdentifier(item_identifier, this);
			if (!itemIdentifiers.contains(itemIdentifier)) {
				getPersistenceManager().makePersistent(itemIdentifier);
				itemIdentifiers.add(itemIdentifier);
			}
		} catch (JDOException re) {
			throw new UniquenessViolationException("Item identifier " + item_identifier + " is already identifying another object: " 
					+ topicmap.getObjectByIdentifier(item_identifier));
		}
	}

	@Override
	public void removeItemIdentifier(LocatorIF item_identifier) {
		removeLocator(itemIdentifiers, item_identifier);
	}

	@Override
	public void remove() {
		if (isDeleted()) return;
		if (isReadOnly()) throw new ReadOnlyException();
		beforeRemove();
		getPersistenceManager().deletePersistent(this);
	}
	
	protected boolean isDeleted() {
		ObjectState state = JDOHelper.getObjectState(this);
		return ((state == ObjectState.PERSISTENT_DELETED) || (state == ObjectState.PERSISTENT_NEW_DELETED) || (topicmap == null));
	}

	protected void beforeRemove() {
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
	protected void removeLocator(Set<? extends LocatorIF> set, LocatorIF remove) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (remove == null) throw new NullPointerException("Locator cannot be null");
		LocatorIF toRemove = null;
		for (LocatorIF locator : set) {
			if (locator.equals(remove)) {
				toRemove = locator;
				break;
			}
		}
		if (toRemove != null) {
			set.remove(toRemove);
			getPersistenceManager().deletePersistent(toRemove);
		}
	}
	
	public Query getQuery(String name) {
		JDOTopicMapSource source = (JDOTopicMapSource) getTopicMap().getStore().getReference().getSource();
		return source.getQueries().get(name, getPersistenceManager());
	}
}
