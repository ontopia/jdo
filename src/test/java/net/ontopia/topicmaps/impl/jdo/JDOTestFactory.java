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

import java.io.File;
import java.io.IOException;
import net.ontopia.topicmaps.core.TestFactoryIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSource;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSourceTest;

public class JDOTestFactory implements TestFactoryIF {

	protected TopicMapReferenceIF reference;
	protected static JDOTopicMapSource source;
	protected TopicMapStoreIF store;
	protected TopicMapIF topicmap;
	protected TopicMapBuilderIF builder;

	public JDOTestFactory() {
		File db = new File("target/ontopia.h2.db");
		if (db.exists()) db.delete();

		if (source == null) {
			source = new JDOTopicMapSource(JDOTopicMapSourceTest.PROPERTIES);
			source.setSupportsCreate(true);
			source.setSupportsDelete(true);
		}
	}
	
	@Override
	public TopicMapStoreIF makeStandaloneTopicMapStore() {
		TopicMapReferenceIF ref = source.createTopicMap("foo", "jdo:test");
		try {
			return ref.createStore(false);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	@Override
	public TopicMapReferenceIF makeTopicMapReference() {
		return source.createTopicMap("foo", "jdo:test");
	}

	@Override
	public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef) {
		// todo: actual delete to avoid having to delete the file
		topicmapRef.close();
	}
}
