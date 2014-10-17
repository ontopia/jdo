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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import net.ontopia.topicmaps.impl.jdo.index.IndexCache;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;

@PersistenceCapable(table = "TM_TOPIC_MAP")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Index(name = "TM_TOPIC_MAP_IX_BASE_ID", members = {"base", "id"})
public class TopicMap extends Reifiable implements TopicMapIF {
	
	static final Map<Character, Class<? extends TMObject>> classmap = new HashMap<Character, Class<? extends TMObject>>();
	static {
		classmap.put('M', TopicMap.class);
		classmap.put('A', Association.class);
		classmap.put('T', Topic.class);
		classmap.put('R', AssociationRole.class);
		classmap.put('O', Occurrence.class);
		classmap.put('V', VariantName.class);
		classmap.put('N', TopicName.class);
	}
	
	@Persistent(name = "title", column = "title", defaultFetchGroup = "true")
	private String title;

	@Persistent(name = "base", column = "base", defaultFetchGroup = "true")
	@Column(jdbcType = "LONGVARCHAR")
	private String base;

	@Persistent(mappedBy = "topicmap", dependentElement = "true")
	private Set<Association> associations = new HashSet<Association>();

	@Persistent(mappedBy = "topicmap", dependentElement = "true")
	private Set<Topic> topics = new HashSet<Topic>();
	
	@NotPersistent
	private JDOTopicMapStore store;
	
	/* -- not persistent -- */
	private transient JDOTopicMapBuilder builder = null;
	private transient final IndexCache indexes;
	
	public TopicMap() {
		super(null);
		indexes = new IndexCache(this);
	}
	
	public TopicMap(long id) {
		this();
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

	@Override
	public TopicMapStoreIF getStore() {
		return store;
	}
	
	public void setStore(JDOTopicMapStore store) {
		this.store = store;
	}

	@Override
	public synchronized TopicMapBuilderIF getBuilder() {
		if (builder == null) {
			builder = new JDOTopicMapBuilder(this);
		}
		return builder;
	}

	@Override
	public Object getIndex(String name) {
		return indexes.getIndex(name);
	}

	@Override
	public Collection<TopicIF> getTopics() {
		return new HashSet<TopicIF>(topics);
	}

	@Override
	public Collection<AssociationIF> getAssociations() {
		return new HashSet<AssociationIF>(associations);
	}

	@Override
	public TMObjectIF getObjectById(String object_id) {
		if ((object_id == null) || (object_id.isEmpty())) throw new NullPointerException("Object id cannot be null");
		Class<? extends TMObject> cls = classmap.get(object_id.charAt(0));
		if (cls == null) return null; // unknown indicator
		return getPersistenceManager().getObjectById(cls, object_id.substring(1));
	}

	@Override
	public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		ItemIdentifier itemIdentifier = JDOQueryUtils.singularResultQuery(
				getQuery(Queries.TOPICMAP_TOPIC_BY_ITEM_IDENTIFIER),
				this, locator.getAddress());
		if (itemIdentifier == null) return null;
		return itemIdentifier.getObject();
	}

	@Override
	public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		SubjectLocator subjectLocator = JDOQueryUtils.singularResultQuery(
				getQuery(Queries.TOPICMAP_TOPIC_BY_SUBJECT_LOCATOR),
				this, locator.getAddress());
		if (subjectLocator == null) return null;
		return subjectLocator.getTopic();
	}

	@Override
	public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		SubjectIdentifier subjectIdentifier = JDOQueryUtils.singularResultQuery(
				getQuery(Queries.TOPICMAP_TOPIC_BY_SUBJECT_IDENTIFIER),
				this, locator.getAddress());
		if (subjectIdentifier == null) return null;
		return subjectIdentifier.getTopic();
	}

	/**
	 * INTERNAL: Find the object use specified locator as item/subject identifier. For ConstraintViolationException
	 * purposes.
	 * @param locator
	 * @return 
	 */
	TMObject getObjectByIdentifier(LocatorIF locator) {
		TMObject o = (TMObject) getTopicBySubjectIdentifier(locator);
		if (o == null) {
			o = (TMObject) getObjectByItemIdentifier(locator);
		}
		return o;
	}

	@Override
	public void clear() {
		if (isReadOnly()) throw new ReadOnlyException();
		if (isDeleted()) return;
		
		// remove all topics, cascades to all other objects
		for (TopicIF t : getTopics()) {
			t.remove();
		}
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
	
	// remove methods
	void removeTopic(Topic topic) {
		topics.remove(topic);
	}

	void removeAssociation(Association association) {
		associations.remove(association);
	}
}
