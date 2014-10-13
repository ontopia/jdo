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
import java.util.Collection;
import junit.framework.Assert;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDOTopicMapBuilderTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JDOTopicMapBuilderTest.class);
	
	private static TopicMapReferenceIF reference;
	private static JDOTopicMapSource source;
	private TopicMapStoreIF store;
	private TopicMapIF topicmap;
	private TopicMapBuilderIF builder;
	
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
	
	@Test
	public void testCreateTopic() {
		TopicIF topic = builder.makeTopic();
		Assert.assertNotNull(topic);
		Assert.assertEquals(topicmap, topic.getTopicMap());
		Assert.assertEquals(1, topicmap.getTopics().size());
	}
	
	@Test
	public void testCreateName() {
		TopicIF topic = builder.makeTopic();
		TopicNameIF name = builder.makeTopicName(topic, "Foo");
		TopicNameIF name2 = builder.makeTopicName(topic, "Foo");
		TopicNameIF name3 = builder.makeTopicName(topic, "Foo");
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
	}
	
	@Test
	public void testMakeAssociation() {
		TopicIF at = builder.makeTopic();
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		AssociationIF association = builder.makeAssociation(at, rt, t);
		
		Assert.assertEquals(1, topicmap.getAssociations().size());
		Assert.assertEquals(association, topicmap.getAssociations().iterator().next());
		
		TopicIF t2 = builder.makeTopic();
		TopicIF rt2 = builder.makeTopic();
		AssociationRoleIF ar2 = builder.makeAssociationRole(association, rt2, t2);
		
		Assert.assertEquals(1, topicmap.getAssociations().size());
		Assert.assertEquals(2, association.getRoles().size());
		Assert.assertEquals(rt2, ar2.getType());
		Assert.assertEquals(t2, ar2.getPlayer());
	}
	
	@Test
	public void testAssociationQuery_getRoleTypes() {
		TopicIF at = builder.makeTopic();
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		AssociationIF association = builder.makeAssociation(at, rt, t);
		
		Collection<TopicIF> roleTypes = association.getRoleTypes();
		
		Assert.assertEquals(1, roleTypes.size());
		Assert.assertEquals(rt, roleTypes.iterator().next());
	}
	
	@Test
	public void testAssociationQuery_getRolesByType() {
		TopicIF at = builder.makeTopic();
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		AssociationIF association = builder.makeAssociation(at, rt, t);
		builder.makeAssociationRole(association, builder.makeTopic(), builder.makeTopic());
		
		Collection<AssociationRoleIF> roles = association.getRolesByType(rt);
		
		Assert.assertEquals(1, roles.size());
		Assert.assertEquals(rt, roles.iterator().next().getType());
		Assert.assertEquals(t, roles.iterator().next().getPlayer());
	}
	
	@Test
	public void testTopicQuery_NamesByType() {
		TopicIF nt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		TopicNameIF tn = builder.makeTopicName(t, nt, "foo");
		
		Assert.assertEquals(1, t.getTopicNames().size());
		
		Collection<TopicNameIF> tnbt = t.getTopicNamesByType(nt);
		
		Assert.assertEquals(1, tnbt.size());
		Assert.assertEquals(tn, tnbt.iterator().next());
	}
	
	@Test
	public void testTopicQuery_OccurrencesByType() {
		TopicIF ot = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		OccurrenceIF o = builder.makeOccurrence(t, ot, "foo");
		
		Assert.assertEquals(1, t.getOccurrences().size());
		
		Collection<OccurrenceIF> obt = t.getOccurrencesByType(ot);
		
		Assert.assertEquals(1, obt.size());
		Assert.assertEquals(o, obt.iterator().next());
	}
	
	@Test
	public void testTopicQuery_RolesByType() {
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		AssociationRoleIF r = builder.makeAssociationRole(builder.makeAssociation(builder.makeTopic()), rt, t);
		
		Assert.assertEquals(1, t.getRoles().size());
		
		Collection<AssociationRoleIF> rbt = t.getRolesByType(rt);
		
		Assert.assertEquals(1, rbt.size());
		Assert.assertEquals(r, rbt.iterator().next());
	}
	
	@Test
	public void testTopicQuery_RolesByType2() {
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		TopicIF at = builder.makeTopic();
		AssociationRoleIF r = builder.makeAssociationRole(builder.makeAssociation(at), rt, t);
		
		Assert.assertEquals(1, t.getRoles().size());
		
		Collection<AssociationRoleIF> rbt = t.getRolesByType(rt, at);
		
		Assert.assertEquals(1, rbt.size());
		Assert.assertEquals(r, rbt.iterator().next());
	}
	
	@Test
	public void testTopicQuery_Associations() {
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		TopicIF at = builder.makeTopic();
		AssociationIF a = builder.makeAssociation(at, rt, t);
		
		Assert.assertEquals(1, topicmap.getAssociations().size());
		
		Collection<AssociationIF> assocs = t.getAssociations();
		
		Assert.assertEquals(1, assocs.size());
		Assert.assertEquals(a, assocs.iterator().next());
	}
	
	@Test
	public void testTopicQuery_AssociationsByType() {
		TopicIF rt = builder.makeTopic();
		TopicIF t = builder.makeTopic();
		TopicIF at = builder.makeTopic();
		AssociationIF a = builder.makeAssociation(at, rt, t);
		
		Assert.assertEquals(1, topicmap.getAssociations().size());
		
		Collection<AssociationIF> assocs = t.getAssociationsByType(at);
		
		Assert.assertEquals(1, assocs.size());
		Assert.assertEquals(a, assocs.iterator().next());
	}
	
}
