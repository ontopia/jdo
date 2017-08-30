/*
 * #!
 * Ontopia JDO
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicSearcherTest extends AbstractJDOTest {
	
	private BasicSearcher searcher;

	@Override
	@Before
	public void setUp() throws IOException {
		super.setUp();
		searcher = new BasicSearcher((TopicMap) topicmap);
	}

	@Override
	@After
	public void tearDown() {
		try {
			searcher.close();
		} catch (IOException ioe) {
			// ignore
		}
		searcher = null;
		super.tearDown();
	}

	@Test
	public void testSearchNull() throws IOException {
		SearchResultIF result = searcher.search(null);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.hits());
	}

	@Test
	public void testSearchEmpty() throws IOException {
		SearchResultIF result = searcher.search("foo");
		
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.hits());
		
		Assert.assertNull(result.getDocument(0));
		Assert.assertNull(result.getDocument(-1));
		Assert.assertNull(result.getDocument(100));
		
		// score not supported
		Assert.assertEquals(0, result.getScore(0), 0);
		Assert.assertEquals(0, result.getScore(-1), 0);
		Assert.assertEquals(0, result.getScore(100), 0);
	}

	@Test
	public void testSearch() throws IOException {
		builder.makeTopicName(builder.makeTopic(), "foo");
		
		SearchResultIF result = searcher.search("foo");
		
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.hits());
		Assert.assertNull(result.getDocument(-1));
		Assert.assertNull(result.getDocument(100));
		
		// score not supported
		Assert.assertEquals(0, result.getScore(0), 0);

		DocumentIF document = result.getDocument(0);
		Assert.assertNotNull(document);
		
		Collection<FieldIF> fields = document.getFields();
		Assert.assertNotNull(fields);
		
		for (FieldIF field : fields) {
			Assert.assertNotNull(field);
			Assert.assertNotNull(field.getName());
			Assert.assertNotNull(field.getValue());
			Assert.assertNull(field.getReader());
			Assert.assertTrue(field.isIndexed());
			Assert.assertTrue(field.isStored());
			Assert.assertFalse(field.isTokenized());
		}
	}
}
