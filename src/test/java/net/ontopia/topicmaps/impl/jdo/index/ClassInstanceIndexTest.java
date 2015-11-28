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

package net.ontopia.topicmaps.impl.jdo.index;

import java.io.IOException;
import java.util.Collection;
import junit.framework.Assert;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.utils.PSI;
import org.junit.Before;
import org.junit.Test;

public class ClassInstanceIndexTest extends AbstractJDOTest {
	
	private ClassInstanceIndex index;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();
		index = new ClassInstanceIndex((TopicMap) topicmap);
	}
	
	@Test
	public void testGetTopics() {
		// one with, one without type
		TopicIF topic = builder.makeTopic();
		TopicIF topic2 = builder.makeTopic(topic);
		
		Collection<TopicIF> topics = index.getTopics(null);
		
		Assert.assertEquals(1, topics.size());
		Assert.assertEquals(topic, topics.iterator().next());
		
		topics = index.getTopics(topic);

		Assert.assertEquals(1, topics.size());
		Assert.assertEquals(topic2, topics.iterator().next());
	}
	
	@Test
	public void testGetTopicNames() {
		
		// with default type
		TopicNameIF tn1 = builder.makeTopicName(builder.makeTopic(), "foo");
		TopicIF t = builder.makeTopic();
		TopicNameIF tn2 = builder.makeTopicName(builder.makeTopic(), t, "foo");
		
		Collection<TopicNameIF> names = index.getTopicNames(null);
		
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(tn1, names.iterator().next());
		
		names = index.getTopicNames(topicmap.getTopicBySubjectIdentifier(PSI.getSAMNameType()));
		
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(tn1, names.iterator().next());
		
		names = index.getTopicNames(t);
		
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(tn2, names.iterator().next());
	}

	@Test
	public void testGetOccurrences() {
		TopicIF topic = builder.makeTopic();
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), topic, "foo");
		OccurrenceIF o2 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		
		Collection<OccurrenceIF> occurrences = index.getOccurrences(topic);
		
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
	}
	
	@Test
	public void testGetAssociations() {
		TopicIF topic = builder.makeTopic();
		AssociationIF a = builder.makeAssociation(topic, builder.makeTopic(), builder.makeTopic());
		builder.makeAssociation(builder.makeTopic(), builder.makeTopic(), builder.makeTopic());
		
		Collection<AssociationIF> associations = index.getAssociations(topic);
		
		Assert.assertEquals(1, associations.size());
		Assert.assertEquals(a, associations.iterator().next());
	}
	
	@Test
	public void testGetAssociationRoles() {
		TopicIF topic = builder.makeTopic();
		AssociationIF a = builder.makeAssociation(builder.makeTopic());
		AssociationRoleIF ar = builder.makeAssociationRole(a, topic, builder.makeTopic());
		builder.makeAssociationRole(a, builder.makeTopic(), builder.makeTopic());
		
		Collection<AssociationRoleIF> roles = index.getAssociationRoles(topic);
		
		Assert.assertEquals(1, roles.size());
		Assert.assertEquals(ar, roles.iterator().next());
	}
	
	@Test
	public void testTopicTypes() {
		TopicIF t1 = builder.makeTopic();
		TopicIF t2 = builder.makeTopic();		
		builder.makeTopic(t1);
		builder.makeTopic(t2);
		
		Collection<TopicIF> tt = index.getTopicTypes();
		
		Assert.assertEquals(2, tt.size());
		Assert.assertTrue(tt.contains(t1));
		Assert.assertTrue(tt.contains(t2));
		Assert.assertTrue(index.usedAsTopicType(t1));
		Assert.assertTrue(index.usedAsTopicType(t2));
		Assert.assertTrue(index.usedAsType(t1));
		Assert.assertTrue(index.usedAsType(t2));
	}
	
	@Test
	public void testTopicNameTypes() {
		TopicIF nt1 = builder.makeTopic();
		TopicIF nt2 = builder.makeTopic();
		
		builder.makeTopicName(builder.makeTopic(), "foo");
		builder.makeTopicName(builder.makeTopic(), nt1, "foo");
		builder.makeTopicName(builder.makeTopic(), nt2, "foo");
		
		Collection<TopicIF> nts = index.getTopicNameTypes();

		Assert.assertEquals(3, nts.size());
		Assert.assertTrue(nts.contains(nt1));
		Assert.assertTrue(nts.contains(nt2));
		Assert.assertTrue(nts.contains(topicmap.getTopicBySubjectIdentifier(PSI.getSAMNameType())));
		Assert.assertTrue(index.usedAsTopicNameType(nt1));
		Assert.assertTrue(index.usedAsTopicNameType(nt2));
		Assert.assertTrue(index.usedAsType(nt1));
		Assert.assertTrue(index.usedAsType(nt2));
	}

	@Test
	public void testOcurrenceTypes() {
		TopicIF ot1 = builder.makeTopic();
		TopicIF ot2 = builder.makeTopic();
		
		builder.makeOccurrence(builder.makeTopic(), ot1, "foo");
		builder.makeOccurrence(builder.makeTopic(), ot2, "foo");
		
		Collection<TopicIF> ots = index.getOccurrenceTypes();
		
		Assert.assertEquals(2, ots.size());
		Assert.assertTrue(ots.contains(ot1));
		Assert.assertTrue(ots.contains(ot2));
		Assert.assertTrue(index.usedAsOccurrenceType(ot1));
		Assert.assertTrue(index.usedAsOccurrenceType(ot2));
		Assert.assertTrue(index.usedAsType(ot1));
		Assert.assertTrue(index.usedAsType(ot2));
	}

	@Test
	public void testAssociationTypes() {
		TopicIF at1 = builder.makeTopic();
		TopicIF at2 = builder.makeTopic();
		
		builder.makeAssociation(at1);
		builder.makeAssociation(at2);
		
		Collection<TopicIF> ats = index.getAssociationTypes();
		
		Assert.assertEquals(2, ats.size());
		Assert.assertTrue(ats.contains(at1));
		Assert.assertTrue(ats.contains(at2));
		Assert.assertTrue(index.usedAsAssociationType(at1));
		Assert.assertTrue(index.usedAsAssociationType(at2));
		Assert.assertTrue(index.usedAsType(at1));
		Assert.assertTrue(index.usedAsType(at2));
	}

	@Test
	public void testAssociationRoleTypes() {
		TopicIF rt1 = builder.makeTopic();
		TopicIF rt2 = builder.makeTopic();
		
		AssociationIF a = builder.makeAssociation(builder.makeTopic());
		builder.makeAssociationRole(a, rt1, builder.makeTopic());
		builder.makeAssociationRole(a, rt2, builder.makeTopic());
		
		Collection<TopicIF> rts = index.getAssociationRoleTypes();
		
		Assert.assertEquals(2, rts.size());
		Assert.assertTrue(rts.contains(rt1));
		Assert.assertTrue(rts.contains(rt2));
		Assert.assertTrue(index.usedAsAssociationRoleType(rt1));
		Assert.assertTrue(index.usedAsAssociationRoleType(rt2));
		Assert.assertTrue(index.usedAsType(rt1));
		Assert.assertTrue(index.usedAsType(rt2));
	}
}
