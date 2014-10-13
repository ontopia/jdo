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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import javax.jdo.Extent;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;

public class JDOTopicMapSource implements TopicMapSourceIF {

	private String id;
	private String title;
	private boolean supportsDelete;
	private boolean supportsCreate;
	private String propertyFile = null;
	private Map<?, ?> properties = null;

	private PersistenceManagerFactory persistenceManagerFactory = null;
	private final Collection<JDOTopicMapReference> references = new HashSet<JDOTopicMapReference>();
	private final Queries queries;

	public JDOTopicMapSource() {
		queries = new Queries();
	}

	public JDOTopicMapSource(String propertyFile) {
		this();
		this.propertyFile = propertyFile;
	}

	public JDOTopicMapSource(Map<?, ?> properties) {
		this();
		this.properties = properties;
	}

	@Override
	public Collection<TopicMapReferenceIF> getReferences() {
		if (references == null) {
			refresh();
		}

		return new HashSet<TopicMapReferenceIF>(references);
	}

	@Override
	public synchronized void refresh() {
		createPersistenceManagerFactory();
		
		references.clear();
		
		PersistenceManager persistenceManager = null;
		Transaction tx = null;
		try {
			persistenceManager = persistenceManagerFactory.getPersistenceManager();
			tx = persistenceManager.currentTransaction();
			tx.begin();
			Extent<TopicMap> extent = persistenceManager.getExtent(TopicMap.class, false);
			for (TopicMap tm : extent) {
				references.add(new JDOTopicMapReference(this, makeId(tm), tm.getTitle(), tm.getLongId()));
			}
			extent.closeAll();
		} catch (JDOException jdoe) {
			throw new OntopiaRuntimeException("Could not refresh source: " + jdoe.getMessage(), jdoe);
		} finally {
			if (tx != null) {
				tx.rollback();
			}
			if (persistenceManager != null) {
				persistenceManager.close();
			}
		}
	}

	private String makeId(TopicMap tm) {
		if (id == null) {
			return "JDO-" + tm.getLongId();
		} else {
			return id + "-" + tm.getLongId();
		}
	}
	
	protected void createPersistenceManagerFactory() {
		
		if (persistenceManagerFactory == null) {
			try {
				if (propertyFile != null) {
					persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(StreamUtils.getInputStream(propertyFile));
				} else if (properties != null) {
					persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(properties);
				} else {
					throw new OntopiaRuntimeException("Either propertyFile or properties fields have to be set!");
				}
			} catch (IOException ioe) {
				throw new OntopiaRuntimeException("Could not load JDO persistence manager factory: " + ioe.getMessage(), ioe);
			} catch (JDOException jdoe) {
				throw new OntopiaRuntimeException("Error encountered during loading of JDO persistence manager factory: " + jdoe.getMessage(), jdoe);
			}
			
			try {
				// load default queries
				queries.load(Queries.DEFAULT_QUERIES);
			} catch (IOException ioe) {
				throw new OntopiaRuntimeException(ioe);
			}
		}
	}

	@Override
	public void close() {
		if (persistenceManagerFactory != null) {
			persistenceManagerFactory.close();
		}
	}

	@Override
	public TopicMapReferenceIF createTopicMap(String name, String baseAddressURI) {

		if (!supportsCreate) {
			throw new UnsupportedOperationException("This source does not support creating new topic maps.");
		}
		
		createPersistenceManagerFactory();
		
		JDOTopicMapStore store = null;
		try {
			store = new JDOTopicMapStore(persistenceManagerFactory);
			store.open();
			
			TopicMap tm = (TopicMap) store.getTopicMap();
			tm.setTitle(name);
			tm.setBaseAddress(URILocator.create(baseAddressURI));
			store.getPersistenceManager().makePersistent(tm);
			store.commit();

			JDOTopicMapReference newReference = new JDOTopicMapReference(this, makeId(tm), name, tm.getLongId());
			references.add(newReference);
			return newReference;
			
		} finally {
			if (store != null) {
				store.close();
			}
		}
	}

	PersistenceManagerFactory getPersistenceManagerFactory() {
		createPersistenceManagerFactory();
		return persistenceManagerFactory;
	}

	public Queries getQueries() {
		return queries;
	}

	/* --- getter/setter for sources xml --- */
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	public void setPropertyFile(String propertyFile) {
		this.propertyFile = propertyFile;
	}

	public void setSupportsCreate(boolean supportsCreate) {
		this.supportsCreate = supportsCreate;
	}

	public void setSupportsDelete(boolean supportsDelete) {
		this.supportsDelete = supportsDelete;
	}

	public boolean getSupportsDelete() {
		return supportsDelete;
	}

	public boolean getSupportsCreate() {
		return supportsCreate;
	}

	@Override
	public boolean supportsCreate() {
		return getSupportsCreate();
	}

	@Override
	public boolean supportsDelete() {
		return getSupportsDelete();
	}

}
