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

import java.util.Properties;
import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.NotRemovableException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.utils.OntopiaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDOTopicMapStore implements TopicMapStoreIF {
	private static final Logger logger = LoggerFactory.getLogger(JDOTopicMapStore.class);
	public static final int JDO_IMPLEMENTATION = 3;
	
	protected final boolean readOnly;
	protected final PersistenceManagerFactory factory;
	protected long id;

	protected PersistenceManager persistenceManager = null;
	protected Transaction transaction = null;
	protected TopicMap topicmap = null;

	private JDOTopicMapReference reference;
	private Properties properties;

	JDOTopicMapStore(long id, boolean readonly, PersistenceManagerFactory factory) {
		this.readOnly = readonly;
		this.factory = factory;
		this.id = id;
	}

	// for creating a new topicmap
	JDOTopicMapStore(PersistenceManagerFactory factory) {
		this.readOnly = false;
		this.factory = factory;
		this.id = -1;
	}
	
	@Override
	public int getImplementation() {
		return JDO_IMPLEMENTATION;
	}

	@Override
	public boolean isTransactional() {
		return true;
	}

	@Override
	public boolean isOpen() {
		return (persistenceManager != null) && (!persistenceManager.isClosed());
	}

	@Override
	public void open() {
		if (isOpen()) throw new OntopiaRuntimeException("Cannot open store: already open");
		
		persistenceManager = factory.getPersistenceManager();
		transaction = persistenceManager.currentTransaction();
		
		if (id == -1) {
			// create new
			topicmap = new TopicMap();
			id = topicmap.getLongId();
		} else {
			topicmap = persistenceManager.getObjectById(TopicMap.class, id);
			if (topicmap == null) {
				throw new OntopiaRuntimeException("Could not find topicmap with id " + id);
			}
		}
		topicmap.setStore(this);
		
		transaction.begin();
		logger.trace("{} open", this);
	}

	@Override
	public void close() {
		if (isOpen()) {
			if (transaction.isActive()) {
				transaction.rollback();
				logger.trace("{} rollback", this);
			}
			persistenceManager.close();
			transaction = null;
			persistenceManager = null;
			logger.trace("{} close", this);
		}
	}

	@Override
	public LocatorIF getBaseAddress() {
		return null; // todo
	}

	@Override
	public void setBaseAddress(LocatorIF base_address) {
		// todo
	}

	@Override
	public TopicMapIF getTopicMap() {
		if (!isOpen()) open();
		return topicmap;
	}

	@Override
	public void commit() {
		logger.trace("{} commit", this);
		if (transaction.isActive()) {
			transaction.commit();
		}
	}

	@Override
	public void abort() {		
		logger.trace("{} rollback", this);
		if ((transaction != null) && (transaction.isActive())) {
			transaction.rollback();
		}
	}

	@Override
	public void delete(boolean force) throws NotRemovableException {
		if (readOnly) throw new ReadOnlyException();

		if (!isOpen()) open();
		
		logger.trace("{} delete", this);

		if (!force) {

			StatisticsIndexIF index = (StatisticsIndexIF) topicmap.getIndex(StatisticsIndexIF.class.getName());
			int objects = index.getTopicCount();
			objects += index.getAssociationCount();

			if (objects > 0) {
				throw new NotRemovableException("Topicmap is not empty");
			}
		}
		try {
			topicmap.clear();
			persistenceManager.deletePersistent(topicmap);
		} catch (JDOException e) {
			throw new NotRemovableException("Could not delete topicmap", e);
		}
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

	void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public TopicMapReferenceIF getReference() {
		return reference;
	}

	@Override
	public void setReference(TopicMapReferenceIF reference) {
		// todo: secure?
		this.reference = (JDOTopicMapReference) reference;
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	@Override
	protected void finalize() throws Throwable {
		if (isOpen()) {
			logger.warn("!! Finalize called on JDO store {}. Store was not closed properly, closing now!", Integer.toHexString(hashCode()));
			close();
		}
		super.finalize();
	}
}
