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

package net.ontopia.topicmaps.impl.jdo.entry;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JDOTopicMapSourceTest {
	
	public static final String PROPERTIES = "classpath:net/ontopia/topicmaps/impl/jdo/h2.props";
	public static final String INCORRECT_PROPERTIES1 = "classpath:net/ontopia/topicmaps/impl/jdo/h2.broken1.props";
	
	private JDOTopicMapSource source;
	
	@Before
	public void setUp() {
		File db = new File("target/ontopia.h2.db");
		if (db.exists()) db.delete();
	}

	@After
	public void tearDown() {
		if (source != null) source.close();
	}
	
	@Test
	public void testOpenFromClasspath() throws IOException {
		source = new JDOTopicMapSource(PROPERTIES);
		source.refresh();
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
	}
	
	@Test
	public void testOpenFromProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(StreamUtils.getInputStream(PROPERTIES));
		source = new JDOTopicMapSource(properties);
		source.refresh();
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
	}
	
	@Test(expected = OntopiaRuntimeException.class)
	public void testOpenIncorrectProperties1() throws IOException {
		source = new JDOTopicMapSource(INCORRECT_PROPERTIES1);
		source.refresh();
		Assert.fail("Missing tables not detected");
	}
	
	@Test
	public void testCreateTopicMap() throws IOException {
		source = new JDOTopicMapSource(PROPERTIES);
		source.setId("ontopia-test-jdo");
		source.setSupportsCreate(true);
		source.setSupportsDelete(true);
		source.refresh();
		
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
		
		JDOTopicMapReference ref = (JDOTopicMapReference) source.createTopicMap("foo", "foo:bar");
		
		Assert.assertEquals("Title changed after save", "foo", ref.getTitle());
		String id = ref.getId();
		Assert.assertNotNull("Unexpected id", id);
		Assert.assertTrue("Unexpected id", id.startsWith("ontopia-test-jdo-1"));
		Assert.assertEquals("Incorrect number of references after create", 1, source.getReferences().size());
		
		TopicMapStoreIF store = null;
		try {
			store = ref.createStore(true);
			TopicMap tm = (TopicMap) store.getTopicMap();
			Assert.assertEquals("Unexpected base", "foo:bar", tm.getBaseAddress().getAddress());
			store.close();

			// refresh
			source.refresh();

			Assert.assertEquals("Incorrect number of references after refresh", 1, source.getReferences().size());
			ref = (JDOTopicMapReference) source.getReferences().iterator().next();
			Assert.assertEquals("Title changed after refresh", "foo", ref.getTitle());
			Assert.assertEquals("Id changed after refresh", id, ref.getId());

			store = ref.createStore(true);
			tm = (TopicMap) store.getTopicMap();
			Assert.assertEquals("Changed base after refresh", "foo:bar", tm.getBaseAddress().getAddress());
		} finally {
			if (store != null) store.close();
		}
		
		ref.delete();
		Assert.assertTrue("Reference not set to deleted after delete", ref.isDeleted());
		Assert.assertTrue("Reference set not empty after delete", source.getReferences().isEmpty());

		try {
			ref.createStore(true).close();
			Assert.fail("Could open store after reference delete");
		} catch (IOException ioe) {
			// expected
		}
	}
}
