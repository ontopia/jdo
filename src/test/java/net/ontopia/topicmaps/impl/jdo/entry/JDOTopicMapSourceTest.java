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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.utils.StreamUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JDOTopicMapSourceTest {
	
	public static final String PROPERTIES = "classpath:net/ontopia/topicmaps/impl/jdo/h2.props";
	public static final String INCORRECT_PROPERTIES1 = "classpath:net/ontopia/topicmaps/impl/jdo/h2.broken1.props";
	public static final String INCORRECT_PROPERTIES2 = "classpath:net/ontopia/topicmaps/impl/jdo/h2.broken2.props";
	
	@Before
	public void setUp() {
		File db = new File("target/ontopia.h2.db");
		if (db.exists()) db.delete();
	}
	
	@Test
	public void testOpenFromClasspath() throws IOException {
		JDOTopicMapSource source = new JDOTopicMapSource(PROPERTIES);
		source.refresh();
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
	}
	
	@Test
	public void testOpenFromProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(StreamUtils.getInputStream(PROPERTIES));
		JDOTopicMapSource source = new JDOTopicMapSource(properties);
		source.refresh();
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
	}
	
	@Test
	public void testOpenIncorrectProperties1() throws IOException {
		JDOTopicMapSource source = new JDOTopicMapSource(INCORRECT_PROPERTIES1);
		source.refresh();
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
	}
	
	@Test
	public void testCreateTopicMap() throws IOException {
		JDOTopicMapSource source = new JDOTopicMapSource(PROPERTIES);
		source.setId("ontopia-test-jdo");
		source.setSupportsCreate(true);
		source.refresh();
		
		Assert.assertTrue("Reference set not empty", source.getReferences().isEmpty());
		
		TopicMapReferenceIF ref = source.createTopicMap("foo", "foo:bar");
		
		Assert.assertEquals("Title changed after save", "foo", ref.getTitle());
		Assert.assertEquals("Unexpected id", "ontopia-test-jdo-1", ref.getId());
		Assert.assertEquals("Incorrect number of references after create", 1, source.getReferences().size());
	}
}
