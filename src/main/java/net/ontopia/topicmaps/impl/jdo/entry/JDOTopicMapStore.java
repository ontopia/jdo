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

package net.ontopia.topicmaps.impl.jdo.entry;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.NotRemovableException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.utils.OntopiaRuntimeException;

public class JDOTopicMapStore implements TopicMapStoreIF {
	public static final int JDO_IMPLEMENTATION = 3;
	
	private final boolean readOnly;
	protected final PersistenceManager persistenceManager;
	private final TopicMap topicmap;

	private JDOTopicMapReference reference;

	JDOTopicMapStore(long id, boolean readonly, PersistenceManagerFactory factory) {
		this.readOnly = readonly;
		persistenceManager = factory.getPersistenceManager();
		
		topicmap = persistenceManager.getObjectById(TopicMap.class, id);
		if (topicmap == null) {
			throw new OntopiaRuntimeException("Could not find topicmap with id " + id);
		}
		topicmap.setStore(this);
	}

	// for creating a new topicmap
	JDOTopicMapStore(PersistenceManagerFactory factory) {
		this.readOnly = false;
		this.persistenceManager = factory.getPersistenceManager();
		topicmap = new TopicMap();
		topicmap.setStore(this);
	}
	
	public int getImplementation() {
		return JDO_IMPLEMENTATION;
	}

	public boolean isTransactional() {
		return true;
	}

	public boolean isOpen() {
		return persistenceManager.currentTransaction().isActive();
	}

	public void open() {
		if (isOpen()) throw new OntopiaRuntimeException("Cannot open store: already open");
		persistenceManager.currentTransaction().begin();
	}

	public void close() {
		if (persistenceManager.currentTransaction().isActive()) {
			persistenceManager.currentTransaction().rollback();
		}
	}

	public LocatorIF getBaseAddress() {
		return null; // todo
	}

	public void setBaseAddress(LocatorIF base_address) {
		// todo
	}

	public TopicMapIF getTopicMap() {
		return topicmap;
	}

	public void commit() {
		if (persistenceManager.currentTransaction().isActive()) {
			persistenceManager.currentTransaction().commit();
		}
	}

	public void abort() {
		if (persistenceManager.currentTransaction().isActive()) {
			persistenceManager.currentTransaction().rollback();
		}
	}

	public void delete(boolean force) throws NotRemovableException {
		// todo
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public String getProperty(String propertyName) {
		return null; // todo: needed?
	}

	public TopicMapReferenceIF getReference() {
		return reference;
	}

	public void setReference(TopicMapReferenceIF reference) {
		// todo: secure?
		this.reference = (JDOTopicMapReference) reference;
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}
}
