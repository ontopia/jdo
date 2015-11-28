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
import java.util.Collections;
import junit.framework.Assert;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import org.junit.Before;
import org.junit.Test;

public class StatisticsIndexTest extends AbstractJDOTest {
	
	private StatisticsIndex index;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();
		index = new StatisticsIndex((TopicMap) topicmap);
	}
	
	@Test
	public void testGetTopicCount() {
		Assert.assertEquals(0, index.getTopicCount());
		builder.makeTopic();
		Assert.assertEquals(1, index.getTopicCount());
	}

	@Test
	public void testGetTypedTopicCount() {
		Assert.assertEquals(0, index.getTypedTopicCount());
		builder.makeTopic(builder.makeTopic());
		Assert.assertEquals(1, index.getTypedTopicCount());
	}

	@Test
	public void testGetUntypedTopicCount() {
		Assert.assertEquals(0, index.getUntypedTopicCount());
		builder.makeTopic(builder.makeTopic());
		Assert.assertEquals(1, index.getUntypedTopicCount());
	}

	@Test
	public void testGetTopicTypeCount() {
		Assert.assertEquals(0, index.getTopicTypeCount());
		builder.makeTopic(builder.makeTopic());
		Assert.assertEquals(1, index.getTopicTypeCount());
	}

	@Test
	public void testGetAssociationCount() {
		Assert.assertEquals(0, index.getAssociationCount());
		builder.makeAssociation(builder.makeTopic());
		Assert.assertEquals(1, index.getAssociationCount());
	}

	@Test
	public void testGetAssociationTypeCount() {
		Assert.assertEquals(0, index.getAssociationTypeCount());
		builder.makeAssociation(builder.makeTopic());
		Assert.assertEquals(1, index.getAssociationTypeCount());
	}

	@Test
	public void testGetRoleCount() {
		Assert.assertEquals(0, index.getRoleCount());
		builder.makeAssociation(builder.makeTopic(), builder.makeTopic(), builder.makeTopic());
		Assert.assertEquals(1, index.getRoleCount());
	}

	@Test
	public void testGetRoleTypeCount() {
		Assert.assertEquals(0, index.getRoleTypeCount());
		builder.makeAssociation(builder.makeTopic(), builder.makeTopic(), builder.makeTopic());
		Assert.assertEquals(1, index.getRoleTypeCount());
	}

	@Test
	public void testGetOccurrenceCount() {
		Assert.assertEquals(0, index.getOccurrenceCount());
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		Assert.assertEquals(1, index.getOccurrenceCount());
	}

	@Test
	public void testGetOccurrenceTypeCount() {
		Assert.assertEquals(0, index.getOccurrenceTypeCount());
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		Assert.assertEquals(1, index.getOccurrenceTypeCount());
	}

	@Test
	public void testGetTopicNameCount() {
		Assert.assertEquals(0, index.getTopicNameCount());
		builder.makeTopicName(builder.makeTopic(), "foo");
		Assert.assertEquals(1, index.getTopicNameCount());
	}

	@Test
	public void testGetTopicNameTypeCount() {
		Assert.assertEquals(0, index.getTopicNameTypeCount());
		builder.makeTopicName(builder.makeTopic(), "foo");
		Assert.assertEquals(1, index.getTopicNameTypeCount());
	}

	@Test
	public void testGetNoNameTopicCount() {
		Assert.assertEquals(0, index.getNoNameTopicCount());
		builder.makeTopic();
		builder.makeTopicName(builder.makeTopic(), "foo");
		
		// no names: the dummy topic and default-name-type
		Assert.assertEquals(2, index.getNoNameTopicCount());
	}

	@Test
	public void testGetVariantCount() {
		Assert.assertEquals(0, index.getVariantCount());
		builder.makeVariantName(
				builder.makeTopicName(builder.makeTopic(), "foo"),
				"foo", Collections.singleton(builder.makeTopic()));
		Assert.assertEquals(1, index.getVariantCount());
	}

	@Test
	public void testGetSubjectIdentifierCount() {
		Assert.assertEquals(0, index.getSubjectIdentifierCount());
		builder.makeTopic().addSubjectIdentifier(URILocator.create("foo:bar"));
		Assert.assertEquals(1, index.getSubjectIdentifierCount());
	}

	@Test
	public void testGetSubjectLocatorCount() {
		Assert.assertEquals(0, index.getSubjectLocatorCount());
		builder.makeTopic().addSubjectLocator(URILocator.create("foo:bar"));
		Assert.assertEquals(1, index.getSubjectLocatorCount());
	}

	@Test
	public void testGetItemIdentifierCount() {
		Assert.assertEquals(0, index.getItemIdentifierCount());
		builder.makeTopic().addItemIdentifier(URILocator.create("foo:bar"));
		Assert.assertEquals(1, index.getItemIdentifierCount());
	}
}
