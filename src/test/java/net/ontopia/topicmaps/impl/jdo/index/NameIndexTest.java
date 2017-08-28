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
import java.util.Collections;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameIndexTest extends AbstractJDOTest {

	private NameIndex index;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();
		index = new NameIndex((TopicMap) topicmap);
	}
	
	@Test
	public void testGetTopicNames() {
		TopicNameIF n1 = builder.makeTopicName(builder.makeTopic(), "foo");
		TopicNameIF n2 = builder.makeTopicName(builder.makeTopic(), "foobar");
		
		Collection<TopicNameIF> names = index.getTopicNames("foo");
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n1, names.iterator().next());
		names = index.getTopicNames("foobar");
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n2, names.iterator().next());
	}
	
	@Test
	public void testGetTopicNamesType() {
		TopicIF type = builder.makeTopic();
		builder.makeTopicName(builder.makeTopic(), "foo");
		TopicNameIF n2 = builder.makeTopicName(builder.makeTopic(), type, "foobar");
		
		Collection<TopicNameIF> names = index.getTopicNames("foo", type);
		Assert.assertEquals(0, names.size());
		names = index.getTopicNames("foobar", type);
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n2, names.iterator().next());
	}
	
	@Test
	public void testGetVariants() {
		VariantNameIF n1 = builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"), "bar", Collections.singleton(builder.makeTopic()));
		VariantNameIF n2 = builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"), "baz", Collections.singleton(builder.makeTopic()));
		
		Collection<VariantNameIF> names = index.getVariants("bar");
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n1, names.iterator().next());
		names = index.getVariants("baz");
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n2, names.iterator().next());
	}
	
	@Test
	public void testGetVariants_datatype() {
		VariantNameIF n1 = builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"), "bar", URILocator.create("foo:bar"), Collections.singleton(builder.makeTopic()));
		VariantNameIF n2 = builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"), "bar", Collections.singleton(builder.makeTopic()));
		
		Collection<VariantNameIF> names = index.getVariants("bar", URILocator.create("foo:bar"));
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n1, names.iterator().next());
	}
}
