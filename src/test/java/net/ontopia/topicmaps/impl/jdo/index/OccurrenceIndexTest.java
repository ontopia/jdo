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
import java.util.Iterator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OccurrenceIndexTest extends AbstractJDOTest {

	private OccurrenceIndex index;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();
		index = new OccurrenceIndex((TopicMap) topicmap);
	}
	
	@Test
	public void testGetOccurrences() {
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		OccurrenceIF o2 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "bar");
		
		Collection<OccurrenceIF> occurrences = index.getOccurrences("foo");
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
		occurrences = index.getOccurrences("bar");
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o2, occurrences.iterator().next());
	}

	@Test
	public void testGetOccurrences_datatype() {
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		OccurrenceIF o2 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		
		o1.setValue("foo", URILocator.create("foo:bar"));
		
		Collection<OccurrenceIF> occurrences = index.getOccurrences("foo", URILocator.create("foo:bar"));
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
	}

	@Test
	public void testGetOccurrencesByPrefix() {
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foobar");
		
		Collection<OccurrenceIF> occurrences = index.getOccurrencesByPrefix("foo");
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
	}

	@Test
	public void testGetOccurrencesByPrefix_datatyoe() {
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foobar");
		OccurrenceIF o2 = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foobar");
		o1.setValue("foobar", URILocator.create("foo:bar"));
		
		Collection<OccurrenceIF> occurrences = index.getOccurrencesByPrefix("foo", URILocator.create("foo:bar"));
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
	}

	@Test
	public void testGetValuesGreaterThanOrEqual() {
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "a");
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "c");
		
		Iterator<String> values = index.getValuesGreaterThanOrEqual("b");
		Assert.assertEquals("c", values.next());
		Assert.assertFalse(values.hasNext());
	}

	@Test
	public void testGetValuesSmallerThanOrEqual() {
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "a");
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "c");
		
		Iterator<String> values = index.getValuesSmallerThanOrEqual("b");
		Assert.assertEquals("a", values.next());
		Assert.assertFalse(values.hasNext());
	}
	
	@Test
	public void testGetOccurrences_type() {
		TopicIF type = builder.makeTopic();
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), type, "foo");
		OccurrenceIF o2 = builder.makeOccurrence(builder.makeTopic(), type, "bar");
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "bar");
		
		Collection<OccurrenceIF> occurrences = index.getOccurrences("foo", type);
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
		occurrences = index.getOccurrences("bar", type);
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o2, occurrences.iterator().next());
	}

	@Test
	public void testGetOccurrences_datatype_type() {
		TopicIF type = builder.makeTopic();
		OccurrenceIF o1 = builder.makeOccurrence(builder.makeTopic(), type, "foo");
		builder.makeOccurrence(builder.makeTopic(), type, "foo");
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "bar");
		
		o1.setValue("foo", URILocator.create("foo:bar"));
		
		Collection<OccurrenceIF> occurrences = index.getOccurrences("foo", URILocator.create("foo:bar"), type);
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o1, occurrences.iterator().next());
	}
}
