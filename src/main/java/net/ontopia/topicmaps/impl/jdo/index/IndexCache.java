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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

public class IndexCache {
	public static final String DEFAULT_SEARCHER = BasicSearcher.class.getName();
	public static final String SEARCHERIF = SearcherIF.class.getName();

	private final TopicMap topicmap;
	private final Map<String, IndexIF> indexes = new HashMap<>(6);

	public IndexCache(TopicMap topicmap) {
		this.topicmap = topicmap;
		
		indexes.put(ClassInstanceIndexIF.class.getName(), new ClassInstanceIndex(topicmap));
		indexes.put(NameIndexIF.class.getName(), new NameIndex(topicmap));
		indexes.put(OccurrenceIndexIF.class.getName(), new OccurrenceIndex(topicmap));
		indexes.put(ScopeIndexIF.class.getName(), new ScopeIndex(topicmap));
		indexes.put(StatisticsIndexIF.class.getName(), new StatisticsIndex(topicmap));
		indexes.put(SEARCHERIF, new BasicSearcher(topicmap));
	}
	
	public IndexIF getIndex(String name) {
		if (SEARCHERIF.equals(name)) {
			return getSearcher();
		}
		return indexes.get(name);
	}
	
	public void addIndex(String name, IndexIF index) {
		indexes.put(name, index);
	}
	
	private IndexIF getSearcher() {
		return getSearcher(topicmap.getStore().getProperty(SEARCHERIF));
	}

	// for testing
	protected IndexIF getSearcher(String searcher) {
		if (searcher == null) {
			return indexes.get(SEARCHERIF);
		} else {
			try {
				return (IndexIF) ConstructorUtils.invokeConstructor(ClassUtils.getClass(searcher), topicmap);
			} catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
				throw new OntopiaRuntimeException("Could not initialize index " + searcher + ": " + ex.getMessage(), ex);
			}
		}
	}
}
