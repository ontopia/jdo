/*
 * #!
 * Ontopia JDO
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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
import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSource;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSourceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorReuseTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected TopicMapReferenceIF reference;
	protected JDOTopicMapSource source;
	protected TopicMapStoreIF store;
	protected TopicMapIF topicmap;
	protected TopicMapBuilderIF builder;
	private TopicIF t1;
	private TopicIF t2;
	private LocatorIF loc;
	
	@Before
	public void setUp() throws IOException {
		File db = new File("target/ontopia.h2.db");
		if (db.exists()) db.delete();
		
		source = new JDOTopicMapSource(JDOTopicMapSourceTest.PROPERTIES);
		source.setSupportsCreate(true);
		reference = source.createTopicMap("foo", "foo:bar");

		store = reference.createStore(false);
		store.open();  // check ontopia.rdbms if open is called in constructor / createStore / getTopicmap
		topicmap = store.getTopicMap();
		builder = topicmap.getBuilder();

		t1 = builder.makeTopic();
		t1.addSubjectIdentifier(new URILocator("foo:bar"));
		store.commit();
		store.close();
		store.open();
		topicmap = store.getTopicMap();
		builder = topicmap.getBuilder();
		t2 = builder.makeTopic();
		t1 = (TopicIF) topicmap.getObjectById(t1.getObjectId());
		
		// get the locator that we want to reuse
		loc = t1.getSubjectIdentifiers().iterator().next();
	}
	
	@After
	public void tearDown() {
		store.abort();
		store.close();
		reference.close();
		source.close();
	}	

	@Test
	public void testMoveSI() throws MalformedURLException {
		// move locator from t1 to t2
		// without AbstractJDOLocator.preRemove() this fails as locator is marked as deleted
		t1.removeSubjectIdentifier(loc);
		t2.addSubjectIdentifier(loc);
	}

	@Test
	public void testMoveII() throws MalformedURLException {
		t1.removeSubjectIdentifier(loc);
		t2.addItemIdentifier(loc);
	}

	@Test
	public void testMoveSL() throws MalformedURLException {
		t1.removeSubjectIdentifier(loc);
		t2.addSubjectLocator(loc);
	}
}
