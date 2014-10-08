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
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import net.ontopia.utils.StreamUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JDOTest {
	
	private static PersistenceManagerFactory factory;
	private PersistenceManager pm;

	@BeforeClass
	public static void init() throws IOException {
		
		File file = new File("/Users/qs/ontopia.h2.db");
		if (file.exists()) file.delete();
		
		factory = JDOHelper.getPersistenceManagerFactory(StreamUtils.getInputStream("classpath:net/ontopia/topicmaps/impl/jdo/h2.props"));
	}
	
	@Before
	public void setUp() {
		pm = factory.getPersistenceManager();
	}
	
	@After
	public void tearDown() {
		if (pm != null) {
			pm.close();
		}
	}
	
	@AfterClass
	public static void destroy() {
		if (factory != null) {
			factory.close();
		}
	}
	
	@Test
	public void test() throws IOException {
		
		Transaction transaction = pm.currentTransaction();
		transaction.begin();
		
		TopicMap tm = new TopicMap(1);
		
		pm.makePersistent(tm);
		
		transaction.commit();
	}
	
	
}
