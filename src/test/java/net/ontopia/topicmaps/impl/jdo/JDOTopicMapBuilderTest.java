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
import junit.framework.Assert;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSource;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapSourceTest;
import net.ontopia.topicmaps.utils.PSI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDOTopicMapBuilderTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JDOTopicMapBuilderTest.class);
	
	private TopicMapStoreIF store;
	private TopicMapReferenceIF reference;
	private JDOTopicMapSource source;
	private TopicMapIF topicmap;
	private TopicMapBuilderIF builder;
	
	@Before
	public void setUp() throws IOException {
		source = new JDOTopicMapSource(JDOTopicMapSourceTest.PROPERTIES);
		source.setSupportsCreate(true);
		reference = source.createTopicMap("foo", "foo:bar");
		store = reference.createStore(false);
		topicmap = store.getTopicMap();
		builder = topicmap.getBuilder();
	}
	
	@After
	public void tearDown() {
		store.close();
		reference.close();
		source.close();
		
		File db = new File("target/ontopia.h2.db");
		if (db.exists()) db.delete();
	}
	
	@Test
	public void testCreateTopic() {
		TopicIF topic = builder.makeTopic();
		store.commit();
		
		Assert.assertNotNull(topic);
		Assert.assertEquals(topicmap, topic.getTopicMap());
		Assert.assertEquals(1, topicmap.getTopics().size());
	}
	
	@Test
	public void testCreateName() {
		TopicIF topic = builder.makeTopic();
		TopicNameIF name = builder.makeTopicName(topic, "Foo");
		TopicNameIF name2 = builder.makeTopicName(topic, "Foo");
		
		store.commit();
		
		TopicNameIF name3 = builder.makeTopicName(topic, "Foo");
		
		store.commit();
		
		Assert.assertNotNull(name);
		Assert.assertEquals(topicmap, name.getTopicMap());
		Assert.assertEquals(3, topic.getTopicNames().size());
		Assert.assertNotNull(name.getType());
		Assert.assertNotNull(topicmap.getTopicBySubjectIdentifier(PSI.getSAMNameType()));
	}

	@Test
	public void testAddSI() {
		TopicIF topic = builder.makeTopic();
		topic.addSubjectIdentifier(URILocator.create("foo:bar"));
		Assert.assertEquals(1, topic.getSubjectIdentifiers().size());
		Assert.assertEquals("foo:bar", topic.getSubjectIdentifiers().iterator().next().getAddress());
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void testDuplicateSI() {
		builder.makeTopic().addSubjectIdentifier(URILocator.create("foo:bar"));
		Assert.assertNotNull(topicmap.getTopicBySubjectIdentifier(URILocator.create("foo:bar")));
		builder.makeTopic().addSubjectIdentifier(URILocator.create("foo:bar"));
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void testDuplicateSI_II() {
		builder.makeTopic().addSubjectIdentifier(URILocator.create("foo:bar"));
		Assert.assertNotNull(topicmap.getTopicBySubjectIdentifier(URILocator.create("foo:bar")));
		builder.makeTopic().addItemIdentifier(URILocator.create("foo:bar"));
	}
	
	@Test(expected = ConstraintViolationException.class)
	public void testDuplicateII() {
		builder.makeTopic().addItemIdentifier(URILocator.create("foo:bar"));
		Assert.assertNotNull(topicmap.getObjectByItemIdentifier(URILocator.create("foo:bar")));
		builder.makeTopic().addItemIdentifier(URILocator.create("foo:bar"));
	}
	
	@Test
	public void testAddSameII() {
		TopicIF topic = builder.makeTopic();
		topic.addItemIdentifier(URILocator.create("foo:bar"));
		topic.addItemIdentifier(URILocator.create("foo:bar"));
		Assert.assertNotNull(topicmap.getObjectByItemIdentifier(URILocator.create("foo:bar")));
		Assert.assertEquals(1, topic.getItemIdentifiers().size());
	}
	
	@Test
	public void testAddSameSI() {
		TopicIF topic = builder.makeTopic();
		topic.addSubjectIdentifier(URILocator.create("foo:bar"));
		topic.addSubjectIdentifier(URILocator.create("foo:bar"));
		Assert.assertNotNull(topicmap.getTopicBySubjectIdentifier(URILocator.create("foo:bar")));
		Assert.assertEquals(1, topic.getSubjectIdentifiers().size());
	}
	
	@Test
	public void testAddSameSL() {
		TopicIF topic = builder.makeTopic();
		topic.addSubjectLocator(URILocator.create("foo:bar"));
		topic.addSubjectLocator(URILocator.create("foo:bar"));
		Assert.assertNotNull(topicmap.getTopicBySubjectLocator(URILocator.create("foo:bar")));
		Assert.assertEquals(1, topic.getSubjectLocators().size());
	}
	
	@Test
	public void testScopes() {
		TopicNameIF name = builder.makeTopicName(builder.makeTopic(), "foo");
		name.addTheme(builder.makeTopic());
		name.addTheme(builder.makeTopic());
		store.commit();
	}
	
}
