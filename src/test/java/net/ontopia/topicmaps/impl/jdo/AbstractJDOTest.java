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
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSource;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSourceTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJDOTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected static TopicMapReferenceIF reference;
	protected static JDOTopicMapSource source;
	protected TopicMapStoreIF store;
	protected TopicMapIF topicmap;
	protected TopicMapBuilderIF builder;
	
	@BeforeClass
	public static void init() throws IOException {
		File db = new File("target/ontopia.h2.db");
		if (db.exists()) db.delete();
		
		source = new JDOTopicMapSource(JDOTopicMapSourceTest.PROPERTIES);
		source.setSupportsCreate(true);
		reference = source.createTopicMap("foo", "foo:bar");
	}
	
	@Before
	public void setUp() throws IOException {
		store = reference.createStore(false);
		store.open();  // check ontopia.rdbms if open is called in constructor / createStore / getTopicmap
		topicmap = store.getTopicMap();
		builder = topicmap.getBuilder();
	}
	
	@After
	public void tearDown() {
		store.abort();
		store.close();
	}
	
	@AfterClass
	public static void destroy() {
		reference.close();
		source.close();
	}	
	
	
}
