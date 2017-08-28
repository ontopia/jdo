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
	public static final String ASSOCIATION_ROLE_TYPES = "Association.getRoleTypes";
	public static final String ASSOCIATION_ROLES_BY_TYPE = "Association.getRolesByType";
	public static final String CLASSINSTANCEINDEX_UNTYPED_TOPICS = "ClassInstanceIndex.getTopics(null)";
	public static final String CLASSINSTANCEINDEX_TOPICS_BY_TYPE = "ClassInstanceIndex.getTopics";
	public static final String CLASSINSTANCEINDEX_NAMES_BY_TYPE = "ClassInstanceIndex.getTopicNames";
	public static final String CLASSINSTANCEINDEX_OCCURRENCES_BY_TYPE = "ClassInstanceIndex.getOccurrences";
	public static final String CLASSINSTANCEINDEX_ASSOCIATIONS_BY_TYPE = "ClassInstanceIndex.getAssociations";
	public static final String CLASSINSTANCEINDEX_ROLES_BY_TYPE = "ClassInstanceIndex.getAssociationRoles";
	public static final String CLASSINSTANCEINDEX_ROLES_BY_TYPE_AND_ROLE_TYPE = "ClassInstanceIndex.getAssociationRolesByATRT";
	public static final String CLASSINSTANCEINDEX_TOPIC_TYPES = "ClassInstanceIndex.getTopicTypes";
	public static final String CLASSINSTANCEINDEX_TOPIC_NAME_TYPES = "ClassInstanceIndex.getTopicNameTypes";
	public static final String CLASSINSTANCEINDEX_OCCURRENCE_TYPES = "ClassInstanceIndex.getOccurrenceTypes";
	public static final String CLASSINSTANCEINDEX_ASSOCIATION_TYPES = "ClassInstanceIndex.getAssociationTypes";
	public static final String CLASSINSTANCEINDEX_ASSOCIATION_ROLE_TYPES = "ClassInstanceIndex.getAssociationRoleTypes";
	public static final String CLASSINSTANCEINDEX_USED_AS_TOPIC_TYPE = "ClassInstanceIndex.usedAsTopicType";
	public static final String CLASSINSTANCEINDEX_USED_AS_TOPIC_NAME_TYPE = "ClassInstanceIndex.usedAsTopicNameType";
	public static final String CLASSINSTANCEINDEX_USED_AS_OCCURRENCE_TYPE = "ClassInstanceIndex.usedAsOccurrenceType";
	public static final String CLASSINSTANCEINDEX_USED_AS_ASSOCIATION_TYPE = "ClassInstanceIndex.usedAsAssociationType";
	public static final String CLASSINSTANCEINDEX_USED_AS_ASSOCIATION_ROLE_TYPE = "ClassInstanceIndex.usedAsAssociationRoleType";
	public static final String CLASSINSTANCEINDEX_All_NAMES = "ClassInstanceIndex.AllNames";
	public static final String CLASSINSTANCEINDEX_All_OCCURRENCES = "ClassInstanceIndex.AllOccurrences";
	public static final String CLASSINSTANCEINDEX_All_VARIANTS = "ClassInstanceIndex.AllVariants";
	
	public static final String SCOPEINDEX_SCOPED_TOPIC_NAMES = "ScopeIndex.getTopicNames";
	public static final String SCOPEINDEX_SCOPED_VARIANT_NAMES = "ScopeIndex.getVariants";
	public static final String SCOPEINDEX_SCOPED_OCCURRENCES = "ScopeIndex.getOccurrences";
	public static final String SCOPEINDEX_SCOPED_ASSOCIATIONS = "ScopeIndex.getAssociations";
	public static final String SCOPEINDEX_TOPIC_NAME_SCOPES = "ScopeIndex.getTopicNameThemes";
	public static final String SCOPEINDEX_VARIANT_NAME_SCOPES = "ScopeIndex.getVariantThemes";
	public static final String SCOPEINDEX_OCCURRENCE_SCOPES = "ScopeIndex.getOccurrenceThemes";
	public static final String SCOPEINDEX_ASSOCIATION_SCOPES = "ScopeIndex.getAssociationThemes";
	public static final String SCOPEINDEX_USED_AS_TOPIC_NAME_SCOPE = "ScopeIndex.usedAsTopicNameTheme";
	public static final String SCOPEINDEX_USED_AS_VARIANT_NAME_SCOPE = "ScopeIndex.usedAsVariantTheme";
	public static final String SCOPEINDEX_USED_AS_OCCURRENCE_SCOPE = "ScopeIndex.usedAsOccurrenceTheme";
	public static final String SCOPEINDEX_USED_AS_ASSOCIATION_SCOPE = "ScopeIndex.usedAsAssociationTheme";
	public static final String SCOPEINDEX_USED_AS_SCOPE = "ScopeIndex.usedAsTheme";

	public static final String NAMEINDEX_TOPIC_NAMES = "NameIndex.getTopicNames";
	public static final String NAMEINDEX_VARIANT_NAMES = "NameIndex.getVariants";
	public static final String NAMEINDEX_VARIANT_NAMES_DATATYPE = "NameIndex.getVariants_datatype";

	public static final String OCCURRENCEINDEX_OCCURRENCES = "OccurrenceIndex.getOccurrences";
	public static final String OCCURRENCEINDEX_OCCURRENCES_DATATYPE = "OccurrenceIndex.getOccurrences(datatype)";
	public static final String OCCURRENCEINDEX_OCCURRENCES_PREFIX = "OccurrenceIndex.getOccurrencesByPrefix";
	public static final String OCCURRENCEINDEX_OCCURRENCES_PREFIX_DATATYPE = "OccurrenceIndex.getOccurrencesByPrefix(datatype)";
	public static final String OCCURRENCEINDEX_VALUES_SMALLER_EQUAL = "OccurrenceIndex.getValuesGreaterThanOrEqual";
	public static final String OCCURRENCEINDEX_VALUES_GREATER_EQUAL = "OccurrenceIndex.getValuesSmallerThanOrEqual";
	
	public static final String STATISTICSINDEX_TOPICS = "StatisticsIndex.getTopicCount";
	public static final String STATISTICSINDEX_TYPED_TOPICS = "StatisticsIndex.getTypedTopicCount";
	public static final String STATISTICSINDEX_UNTYPED_TOPICS = "StatisticsIndex.getUntypedTopicCount";
	public static final String STATISTICSINDEX_TOPIC_TYPES = "StatisticsIndex.getTopicTypeCount";
	public static final String STATISTICSINDEX_ASSOCIATIONS = "StatisticsIndex.getAssociationCount";
	public static final String STATISTICSINDEX_ASSOCIATION_TYPES = "StatisticsIndex.getAssociationTypeCount";
	public static final String STATISTICSINDEX_ROLES = "StatisticsIndex.getRoleCount";
	public static final String STATISTICSINDEX_ROLE_TYPES = "StatisticsIndex.getRoleTypeCount";
	public static final String STATISTICSINDEX_OCCURRENCES = "StatisticsIndex.getOccurrenceCount";
	public static final String STATISTICSINDEX_OCCURRENCE_TYPES = "StatisticsIndex.getOccurrenceTypeCount";
	public static final String STATISTICSINDEX_TOPIC_NAMES = "StatisticsIndex.getTopicNameCount";
	public static final String STATISTICSINDEX_NO_NAMES = "StatisticsIndex.getNoNameTopicCount";
	public static final String STATISTICSINDEX_TOPIC_NAME_TYPES = "StatisticsIndex.getTopicNameTypeCount";
	public static final String STATISTICSINDEX_VARIANTS = "StatisticsIndex.getVariantCount";
	public static final String STATISTICSINDEX_SUBJECT_IDENTIFIERS = "StatisticsIndex.getSubjectIdentifierCount";
	public static final String STATISTICSINDEX_SUBJECT_LOCATORS = "StatisticsIndex.getSubjectLocatorCount";
	public static final String STATISTICSINDEX_ITEM_IDENTIFIERS = "StatisticsIndex.getItemIdentifierCount";
	
	public static final String BASICSEARCHER_SEARCH = "BasicSearcher.search";

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
		if (pq.isAbstract) throw new OntopiaRuntimeException("Cannot instantiate abstract query " + name);
		Query query = queryCache.get(pq);
		if (query == null) {
			query = pq.create(persistenceManager, loadedQueries);
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
		
		@XmlAttribute
		private String inherit;
		
		@XmlAttribute(name = "abstract")
		private boolean isAbstract = false;
		
		@XmlAttribute(name = "unique")
		private String isUnique;
		
		@XmlElement(name = "class")
		private String klass;
		
		@XmlElement
		private String filter;
		
		@XmlElement
		private String parameters;
		
		@XmlElement
		private String result;
		
		@XmlElement
		private String order;
		
		@XmlElement(name = "result-class")
		private String resultClass;
		
		@XmlElement(name = "range-min")
		private int min = -1;
		
		@XmlElement(name = "range-max")
		private int max = -1;

		private Query create(PersistenceManager pm, Map<String, PersistedQuery> loadedQueries) {
			
			try {
				Query q;
				if (inherit != null) {
					PersistedQuery pq = loadedQueries.get(inherit);
					if (pq == null) throw new OntopiaRuntimeException("Unknown query " + inherit + " specified as inheritance");
					q = pq.create(pm, loadedQueries);
				} else {
					q = pm.newQuery();
				}

				if (klass != null) {
					q.setClass(Class.forName(klass));
				}
				if (filter != null) q.setFilter(filter);
				if (result != null) q.setResult(result);
				if (parameters != null) q.declareParameters(parameters);
				if (resultClass != null) {
					Class<?> rClass = Class.forName(resultClass);
					q.setResultClass(rClass);
				}
				if ((min > -1) && (max > -1)) {
					q.setRange(min, max);
				}
				if (order != null) {
					q.setOrdering(order);
				}
				
				if (isUnique != null) q.setUnique(isUnique.equalsIgnoreCase("true"));
				
				if (!isAbstract) {
					q.setUnmodifiable();
					q.compile();
				}
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
