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

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.jdo.entry.JDOTopicMapStore;
import net.ontopia.utils.OntopiaRuntimeException;

@PersistenceCapable(table = "TM_TOPIC_MAP")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Index(name = "TM_TOPIC_MAP_IX_BASE_ID", members = {"base", "id"})
public class TopicMap extends Reifiable implements TopicMapIF {
	
	@Persistent(name = "title", column = "title", defaultFetchGroup = "true")
	private String title;

	@Persistent(name = "base", column = "base", defaultFetchGroup = "true")
	@Column(jdbcType = "LONGVARCHAR")
	private String base;

	@Persistent(mappedBy = "topicmap")
	private Set<Association> associations = new HashSet<Association>();

	@Persistent(mappedBy = "topicmap")
	private Set<Topic> topics = new HashSet<Topic>();
	
	@NotPersistent
	private JDOTopicMapStore store;
	
	/* -- not persistent -- */
	private transient JDOTopicMapBuilder builder = null;
	
	private static enum TopicMapQuery { 
		TOPIC_BY_SUBJECT_IDENTIFIER(IdentityLocator.class, 
			"address == locator && topicmap == tm && type == " + IdentityLocator.SUBJECT_IDENTIFIER,
			"String locator, TopicMap tm"),
		TOPIC_BY_SUBJECT_LOCATOR(SubjectLocator.class, 
			"address == locator && topicmap == tm",
			"String locator, TopicMap tm"),
		OBJECT_BY_ITEM_IDENTIFIER(IdentityLocator.class,
			"address == locator && topicmap == tm && type == " + IdentityLocator.ITEM_IDENTIFIER,
			"String locator, TopicMap tm"),
		OBJECT_BY_IDENTIFIER(JDOLocator.class,
			"address == locator && topicmap == tm",
			"String locator, TopicMap tm");
		
		private final String filter;
		private final String parameters;
		private final Class<?> klass;
		private static final EnumMap<TopicMapQuery, Query> queryCache = 
				new EnumMap<TopicMapQuery, Query>(TopicMapQuery.class);

		private TopicMapQuery(Class<?> klass, String filter, String parameters) {
			this.klass = klass;
			this.filter = filter;
			this.parameters = parameters;
		}
		
		Query get(PersistenceManager pm) {
			Query q = queryCache.get(this);
			if (q == null) {
				q = pm.newQuery(klass, filter);
				q.declareParameters(parameters);
				q.compile();
				queryCache.put(this, q);
				return q;
			} else {
				return pm.newQuery(q);
			}
		}
	}
	
	public TopicMap() {
		super();
	}
	
	public TopicMap(long id) {
		super(null);
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.title = title;
	}

	public LocatorIF getBaseAddress() {
		return URILocator.create(base);
	}

	public void setBaseAddress(LocatorIF base) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.base = base.getAddress();
	}

	public TopicMapStoreIF getStore() {
		return store;
	}
	
	public void setStore(JDOTopicMapStore store) {
		this.store = store;
	}

	public synchronized TopicMapBuilderIF getBuilder() {
		if (builder == null) {
			builder = new JDOTopicMapBuilder(this);
		}
		return builder;
	}

	public Object getIndex(String name) {
		return null; // todo
	}

	public Collection<TopicIF> getTopics() {
		return new HashSet<TopicIF>(topics);
	}

	public Collection<AssociationIF> getAssociations() {
		return new HashSet<AssociationIF>(associations);
	}

	public TMObjectIF getObjectById(String object_id) {
		if (object_id == null) throw new NullPointerException("Object id cannot be null");
		return getPersistenceManager().getObjectById(TMObject.class, object_id);
	}

	public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		IdentityLocator itemIdentifier = singularResultQuery(TopicMapQuery.OBJECT_BY_ITEM_IDENTIFIER, IdentityLocator.class, locator.getAddress(), this);
		if (itemIdentifier == null) return null;
		return itemIdentifier.getObject();
	}

	public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		SubjectLocator subjectLocator = singularResultQuery(TopicMapQuery.TOPIC_BY_SUBJECT_LOCATOR, SubjectLocator.class, locator.getAddress(), this);
		if (subjectLocator == null) return null;
		TMObject object = subjectLocator.getObject();
		if (!(object instanceof TopicIF)) throw new OntopiaRuntimeException("Data inconsistency: the subject locator " + locator + " was added to the non-topic " + object);
		return (TopicIF) object;
	}

	public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		IdentityLocator subjectIdentifier = singularResultQuery(TopicMapQuery.TOPIC_BY_SUBJECT_IDENTIFIER, IdentityLocator.class, locator.getAddress(), this);
		if (subjectIdentifier == null) return null;
		TMObject object = subjectIdentifier.getObject();
		if (!(object instanceof TopicIF)) throw new OntopiaRuntimeException("Data inconsistency: the subject identifier " + locator + " was added to the non-topic " + object);
		return (TopicIF) object;
	}

	/**
	 * INTERNAL: Find the object use specified locator as item/subject identifier. For ConstraintViolationException
	 * purposes.
	 * @param locator
	 * @return 
	 */
	TMObject getObjectByIdentifier(LocatorIF locator) {
		IdentityLocator identifier = singularResultQuery(TopicMapQuery.OBJECT_BY_IDENTIFIER, IdentityLocator.class, locator.getAddress());
		if (identifier == null) return null;
		return identifier.getObject();
	}

	public void clear() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected String getClassIndicator() {
		return "M";
	}

	@Override
	public TopicMapIF getTopicMap() {
		return this;
	}
	
	// builder methods

	public TopicIF makeTopic() {
		Topic topic = new Topic(this);
		getPersistenceManager().makePersistent(topic);
		topics.add(topic);
		return topic;
	}

	public TopicIF makeTopic(TopicIF topic_type) {
		if (topic_type == null) throw new NullPointerException("Topic type cannot be null");
		CrossTopicMapException.check(topic_type, this);
		TopicIF topic = makeTopic();
		topic.addType(topic_type);
		return topic;
	}

	public TopicIF makeTopic(Collection<TopicIF> topic_types) {
		if (topic_types == null) throw new NullPointerException("Topic types cannot be null");
		TopicIF topic = makeTopic();
		for (TopicIF type : topic_types) {
			CrossTopicMapException.check(type, this);
			topic.addType(type);
		}
		return topic;
	}

	public AssociationIF makeAssociation(TopicIF assoc_type) {
		Association association = new Association(
				JDOTopicMapBuilder.checkAndCast(assoc_type, "Type", Topic.class));
		getPersistenceManager().makePersistent(association);
		associations.add(association);
		return association;
	}
		
	@SuppressWarnings("unchecked")
	private <T> T singularResultQuery(TopicMapQuery query, Class<T> expected, Object... variables) {
		Query q = query.get(getPersistenceManager());
		try {
			Collection<?> result = (Collection) q.executeWithArray(variables);
			if ((result == null) || (result.isEmpty())) return null;

			Object o = result.iterator().next();
			if (o == null) return null;
			if (o.getClass().isAssignableFrom(expected)) {
				return (T) o;
			}

			return null;
		} finally {
			q.closeAll();
		}
	}
}
