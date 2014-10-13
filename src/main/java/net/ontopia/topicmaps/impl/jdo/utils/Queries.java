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

package net.ontopia.topicmaps.impl.jdo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Queries {
	private static final Logger logger = LoggerFactory.getLogger(Queries.class);
	public static final String DEFAULT_QUERIES = "classpath:net/ontopia/topicmaps/impl/jdo/queries.xml";
	
	// default query name constants
	public static final String TOPICMAP_TOPIC_BY_SUBJECT_IDENTIFIER = "TopicMap.getTopicBySubjectIdentifier";
	public static final String TOPICMAP_TOPIC_BY_ITEM_IDENTIFIER = "TopicMap.getObjectByItemIdentifier";
	public static final String TOPICMAP_TOPIC_BY_SUBJECT_LOCATOR = "TopicMap.getTopicBySubjectLocator";
	public static final String TOPICMAP_OBJECT_BY_IDENTIFIER = "TopicMap.getObjectByIdentifier";

	private final Map<String, PersistedQuery> loadedQueries;
	private final Map<PersistedQuery, Query> queryCache;
	
	public Queries() {
		loadedQueries = new HashMap<String, PersistedQuery>();
		queryCache = new HashMap<PersistedQuery, Query>();
	}
	
	public void load(String resource) throws IOException {
		try {
			InputStream in = StreamUtils.getInputStream(resource);
			if (in == null) throw new IOException("Resource " + resource + " was not found");
			
			JAXBContext jaxb = JAXBContext.newInstance(PersistedQuerySet.class);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			unmarshaller.setEventHandler(new JaxbValidationListener(logger));
			
			process((PersistedQuerySet) unmarshaller.unmarshal(in));
		} catch (JAXBException jbe) {
			throw new IOException("Could not parse query declarations in " + resource + ": " + jbe.getClass().getName() + " " + jbe.getMessage(), jbe);
		}
	}
	
	private void process(PersistedQuerySet set) {
		logger.debug("Loading query set '" + set.name + "' containing " + set.queries.size() + " queries");
		String pkg = set.defaultPackage;
		if (!pkg.endsWith(".")) pkg += ".";
		for (PersistedQuery pq : set.queries) {
			if ((pq.klass != null) && (!pq.klass.contains("."))) {
				pq.klass = pkg + pq.klass;
			}
			if ((pq.resultClass != null) && (!pq.resultClass.contains("."))) {
				pq.resultClass = pkg + pq.resultClass;
			}
			loadedQueries.put(pq.name, pq);
		}
	}
	
	public synchronized Query get(String name, PersistenceManager persistenceManager) {
		PersistedQuery pq = loadedQueries.get(name);
		if (pq == null) throw new OntopiaRuntimeException("Unknown query " + name);
		Query query = queryCache.get(pq);
		if (query == null) {
			query = pq.create(persistenceManager);
			queryCache.put(pq, query);
			return query;
		} else {
			return persistenceManager.newQuery(query);
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class PersistedQuery {
		
		@XmlAttribute
		private String name;
		
		@XmlElement(name = "class")
		private String klass;
		
		@XmlElement
		private String filter;
		
		@XmlElement
		private String parameters;
		
		@XmlElement
		private String result;
		
		@XmlElement(name = "result-class")
		private String resultClass;
		
		@XmlElement(name = "range-min")
		private int min = -1;
		
		@XmlElement(name = "range-max")
		private int max = -1;

		private Query create(PersistenceManager pm) {
			
			try {
				Class<?> klazz = Class.forName(klass);
				Query q = pm.newQuery(klazz, filter);
				if (result != null) q.setResult(result);
				if (parameters != null) q.declareParameters(parameters);
				if (resultClass != null) {
					Class<?> rClass = Class.forName(resultClass);
					q.setResultClass(rClass);
				}
				if ((min > -1) && (max > -1)) {
					q.setRange(min, max);
				}
				
				q.setUnmodifiable();
				q.compile();
				return q;
			} catch (ClassNotFoundException cnfe) {
				throw new OntopiaRuntimeException("Could not load query " + name + ", class " + klass + " not found", cnfe);
			}
		}
	}
	
	@XmlRootElement(name = "queries")
	@XmlAccessorType(XmlAccessType.NONE)
	private static class PersistedQuerySet {
		
		@XmlAttribute
		private String name;

		@XmlAttribute
		private String defaultPackage;
		
		@XmlElement(name = "query")
		private List<PersistedQuery> queries;
	}	
}
