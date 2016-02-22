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

public class IndexCache {
	public final static String DEFAULT_SEARCHER = BasicSearcher.class.getName();

	private static final Map<String, Class<? extends IndexIF>> knownIndexes 
			= new HashMap<String, Class<? extends IndexIF>>(5);
	
	static {
		knownIndexes.put(ClassInstanceIndexIF.class.getName(), ClassInstanceIndex.class);
		knownIndexes.put(NameIndexIF.class.getName(), NameIndex.class);
		knownIndexes.put(OccurrenceIndexIF.class.getName(), OccurrenceIndex.class);
		knownIndexes.put(ScopeIndexIF.class.getName(), ScopeIndex.class);
		knownIndexes.put(StatisticsIndexIF.class.getName(), StatisticsIndex.class);
		knownIndexes.put(SearcherIF.class.getName(), BasicSearcher.class);
	}
	
	private final TopicMap topicmap;
	private final Map<String, IndexIF> indexes = new HashMap<String, IndexIF>(5);

	public IndexCache(TopicMap topicmap) {
		this.topicmap = topicmap;
	}
	
	public IndexIF getIndex(String name) {
		IndexIF index = indexes.get(name);
		if (index == null) {
			
			if (SearcherIF.class.getName().equals(name)) {
				index = getSearcher(name);
			} else {

				Class<? extends IndexIF> indexClass = knownIndexes.get(name);
				if (indexClass != null) {
					index = instantiate(indexClass, name);
				}
			}
			indexes.put(name, index);
		}
		return index;
	}
	
	public void addIndex(String name, IndexIF index) {
		indexes.put(name, index);
	}
	
	private IndexIF instantiate(Class<? extends IndexIF> indexClass, String name) throws IllegalArgumentException, OntopiaRuntimeException {
		try {
			return indexClass.getConstructor(TopicMap.class).newInstance(topicmap);
		} catch (NoSuchMethodException ex) {
			throw new OntopiaRuntimeException("Could not initialize index " + name + ": " + ex.getMessage(), ex);
		} catch (SecurityException ex) {
			throw new OntopiaRuntimeException("Could not initialize index " + name + ": " + ex.getMessage(), ex);
		} catch (InstantiationException ex) {
			throw new OntopiaRuntimeException("Could not initialize index " + name + ": " + ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			throw new OntopiaRuntimeException("Could not initialize index " + name + ": " + ex.getMessage(), ex);
		} catch (InvocationTargetException ex) {
			throw new OntopiaRuntimeException("Could not initialize index " + name + ": " + ex.getMessage(), ex);
		}
	}	

	@SuppressWarnings("unchecked")
	private IndexIF getSearcher(String name) {
		String searcher = topicmap.getStore().getProperty(name);
		
		if (searcher == null) {
			searcher = DEFAULT_SEARCHER;
		}

		try {
			Class<?> indexClass = Class.forName(searcher);
			if (SearcherIF.class.isAssignableFrom(indexClass)) {
				return instantiate((Class<? extends SearcherIF>) indexClass, name);
			} else {
				throw new OntopiaRuntimeException("Could not initialize specified SearcherIF index '" + searcher + "': not a subclass of SearcherIF");
			}
		} catch (ClassNotFoundException cnfe) {
			throw new OntopiaRuntimeException("Could not initialize specified SearcherIF index '" + searcher + "': " + cnfe.getClass().getName() + ": " + cnfe.getMessage(), cnfe);
		}
	}
}
