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
import javax.jdo.PersistenceManagerFactory;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.AbstractTopicMapReference;
import net.ontopia.utils.OntopiaRuntimeException;

public class JDOTopicMapReference extends AbstractTopicMapReference {

	private final long identity;

	public JDOTopicMapReference(JDOTopicMapSource source, String id, String title, long identity) {
		super(id, title);
		this.identity = identity;
		setSource(source);
	}

	@Override
	public TopicMapStoreIF createStore(boolean readonly) throws IOException {
		if (deleted) throw new IOException("Cannot open a store on a deleted topicmap");
		final JDOTopicMapStore store = new JDOTopicMapStore(identity, readonly,
				getPersistenceManagerFactory());
		store.setReference(this);
		store.setProperties(getPersistenceManagerFactory().getProperties());
		return store;
	}

	@Override
	public synchronized void delete() {
		if (source == null) {
			throw new UnsupportedOperationException("This reference cannot be deleted as it does not belong to a source.");
		}
		if (!source.supportsDelete()) {
			throw new UnsupportedOperationException("This reference cannot be deleted as the source does not allow deleting.");
		}
		// ignore if store already deleted
		if (isDeleted()) {
			return;
		}

		// close reference
		close();

		TopicMapStoreIF store = null;
		try {
			store = createStore(false);
			store.delete(true);
			store.commit();
			deleted = true;
			((JDOTopicMapSource) source).referenceRemoved(this);
		} catch (IOException ioe) {
			throw new OntopiaRuntimeException("Could not delete topicmap: " + ioe.getMessage(), ioe);
		} finally {
			if (store != null) store.close();
		}
	}

	PersistenceManagerFactory getPersistenceManagerFactory() {
		return ((JDOTopicMapSource) getSource()).getPersistenceManagerFactory();
	}

	public long getLongId() {
		return identity;
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + " " + getId() + "]";
	}
}
