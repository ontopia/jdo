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
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.jdo.AbstractJDOTest;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.utils.OntopiaRuntimeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexCacheTest extends AbstractJDOTest {
	
	private IndexCache cache;
	
	@Before
	@Override
	public void setUp() throws IOException {
		super.setUp();
		cache = new IndexCache((TopicMap) topicmap);
	}
	
	@After
	@Override
	public void tearDown() {
		cache = null;
		super.tearDown();
	}
	
	@Test
	public void addIndex() {
		Assert.assertNull(cache.getIndex("foo"));
		cache.addIndex("foo", new IndexIF() { });
		Assert.assertNotNull(cache.getIndex("foo"));
	}

	@Test
	public void getSearcher() {
		IndexIF index = cache.getIndex(SearcherIF.class.getName());
		Assert.assertNotNull(index);
	}

	@Test
	public void getDifferentSearcher() {
		IndexIF searcher = cache.getSearcher(OtherSearcher.class.getName());
		Assert.assertNotNull(searcher);
		Assert.assertTrue(searcher instanceof OtherSearcher);
	}

	@Test(expected = OntopiaRuntimeException.class)
	public void getInvalidSearcher1() {
		cache.getSearcher(InvalidSearcher.class.getName());
	}

	@Test(expected = OntopiaRuntimeException.class)
	public void getInvalidSearcher2() {
		cache.getSearcher(InvalidSearcher2.class.getName());
	}

	public static class OtherSearcher implements IndexIF {
		public OtherSearcher(TopicMap tm) {
			// valid, but no-op
		}
	}
	public static class InvalidSearcher implements IndexIF {
		// invalid constructor
	}
	public static class InvalidSearcher2 {
		// invalid class
	}
}
