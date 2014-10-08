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

public class JDOTopicMapReference extends AbstractTopicMapReference {
	
	private final long identity;

	public JDOTopicMapReference(String id, String title, long identity) {
		super(id, title);
		this.identity = identity;
	}

	@Override
	public TopicMapStoreIF createStore(boolean readonly) throws IOException {
		final JDOTopicMapStore store = new JDOTopicMapStore(identity, readonly, 
				getPersistenceManagerFactory());
		store.setReference(this);
		return store;
	}
	
	PersistenceManagerFactory getPersistenceManagerFactory() {
		return ((JDOTopicMapSource)getSource()).getPersistenceManagerFactory();
	}

	public long getLongId() {
		return identity;
	}
}
