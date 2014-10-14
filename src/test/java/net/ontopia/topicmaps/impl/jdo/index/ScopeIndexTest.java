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
import junit.framework.Assert;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import org.junit.Before;
import org.junit.Test;

public class ScopeIndexTest extends AbstractJDOTest {

	private ScopeIndex index;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();
		index = new ScopeIndex((TopicMap) topicmap);
	}
	
	@Test
	public void testGetTopicNames() {
		TopicIF s = builder.makeTopic();
		TopicNameIF n = builder.makeTopicName(builder.makeTopic(), "foo");
		n.addTheme(s);
		builder.makeTopicName(builder.makeTopic(), "foo").addTheme(builder.makeTopic());

		Collection<TopicNameIF> names = index.getTopicNames(s);
		Collection<TopicIF> scopes = index.getTopicNameThemes();
		
		Assert.assertEquals(1, names.size());
		Assert.assertEquals(n, names.iterator().next());
		Assert.assertEquals(2, scopes.size());
		Assert.assertTrue(scopes.contains(s));
		Assert.assertTrue(index.usedAsTheme(s));
	}

	@Test
	public void testGetVariants() {
		TopicIF s = builder.makeTopic();
		VariantNameIF v = builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"), "bar", Collections.singleton(s));
		builder.makeVariantName(builder.makeTopicName(builder.makeTopic(), "foo"), "bar", Collections.singleton(builder.makeTopic()));
		
		Collection<VariantNameIF> variants = index.getVariants(s);
		Collection<TopicIF> scopes = index.getVariantThemes();
		
		Assert.assertEquals(1, variants.size());
		Assert.assertEquals(v, variants.iterator().next());
		Assert.assertEquals(2, scopes.size());
		Assert.assertTrue(scopes.contains(s));
		Assert.assertTrue(index.usedAsTheme(s));
	}

	@Test
	public void testGetOccurrences() {
		TopicIF s = builder.makeTopic();
		OccurrenceIF o = builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo");
		o.addTheme(s);
		builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "foo").addTheme(builder.makeTopic());
		
		Collection<OccurrenceIF> occurrences = index.getOccurrences(s);
		Collection<TopicIF> scopes = index.getOccurrenceThemes();
		
		Assert.assertEquals(1, occurrences.size());
		Assert.assertEquals(o, occurrences.iterator().next());
		Assert.assertEquals(2, scopes.size());
		Assert.assertTrue(scopes.contains(s));
		Assert.assertTrue(index.usedAsTheme(s));
	}

	@Test
	public void testGetAssociations() {
		TopicIF s = builder.makeTopic();
		AssociationIF a = builder.makeAssociation(builder.makeTopic());
		a.addTheme(s);
		builder.makeAssociation(builder.makeTopic()).addTheme(builder.makeTopic());
		
		Collection<AssociationIF> assocs = index.getAssociations(s);
		Collection<TopicIF> scopes = index.getAssociationThemes();
		
		Assert.assertEquals(1, assocs.size());
		Assert.assertEquals(a, assocs.iterator().next());
		Assert.assertEquals(2, scopes.size());
		Assert.assertTrue(scopes.contains(s));
		Assert.assertTrue(index.usedAsTheme(s));
	}
}
