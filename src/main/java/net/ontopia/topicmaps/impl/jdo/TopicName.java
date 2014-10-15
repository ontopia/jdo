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

import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

@PersistenceCapable(table = "TM_TOPIC_NAME")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Indices({
	@Index(name = "TM_TOPIC_NAME_IX_ID_TOPICMAP_VALUE", members = {"id", "topicmap", "value"}),
	@Index(name = "TM_TOPIC_NAME_IX_ID_TOPICMAP_TYPE", members = {"id", "topicmap", "type"}),
	@Index(name = "TM_TOPIC_NAME_IX_TOPIC", members = {"topic"})
})
public class TopicName extends Scoped implements TopicNameIF {
	
	@Persistent(name = "topic", column = "topic")
	private Topic topic;
	
	@Persistent(mappedBy = "topicname")
	private Set<VariantName> variants = new HashSet<VariantName>();

	@Persistent(name = "value", column = "value")
	@Column(jdbcType = "LONGVARCHAR")
	private String value;
	@Persistent(name = "type", column = "type")
	private Topic type;

	public TopicName(Topic topic, Topic type, String value) {
		super((TopicMap) topic.getTopicMap());
		this.topic = topic;
		this.type = type;
		this.value = value;
	}

	@Override
	protected String getClassIndicator() {
		return "N";
	}

	@Override
	public Collection<VariantNameIF> getVariants() {
		return new HashSet<VariantNameIF>(variants);
	}

	@Override
	public TopicIF getTopic() {
		return topic;
	}

	@Override
	public TopicIF getType() {
		return type;
	}

	@Override
	public void setType(TopicIF type) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (type == null) {
			type = ((JDOTopicMapBuilder)topicmap.getBuilder()).getDefaultNameType();
		}
		this.type = (Topic) type;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (value == null) throw new NullPointerException("Value cannot be null");
		this.value = value;
	}

	@Override
	protected void beforeRemove() {
		
		// remove variants
		for (VariantName variant : new HashSet<VariantName>(variants)) {
			variant.remove();
		}
		
		super.beforeRemove();
	}

	public VariantName makeVariantName(Collection<TopicIF> scope) {
		if (scope == null) throw new NullPointerException("Scope cannot be null");
		VariantName variant = new VariantName(this);
		for (TopicIF scopeTopic : scope) {
			CrossTopicMapException.check(scopeTopic, getTopicMap());
			variant.addTheme(scopeTopic);
		}
		getPersistenceManager().makePersistent(variant);
		variants.add(variant);
		return variant;
	}
	
	public VariantNameIF makeVariantName(String value, Collection<TopicIF> scope) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		VariantName variant = makeVariantName(scope);
		variant.setValue(value);
		return variant;
	}

	public VariantNameIF makeVariantName(LocatorIF locator, Collection<TopicIF> scope) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		VariantName variant = makeVariantName(scope);
		variant.setLocator(locator);
		return variant;
	}

	public VariantNameIF makeVariantName(String value, LocatorIF datatype, Collection<TopicIF> scope) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		if (datatype == null) throw new NullPointerException("Datatype cannot be null");
		VariantName variant = makeVariantName(scope);
		variant.setValue(value, datatype);
		return variant;
	}

	public VariantNameIF makeVariantName(Reader value, long length, LocatorIF datatype, Collection<TopicIF> scope) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		if (datatype == null) throw new NullPointerException("Datatype cannot be null");
		VariantName variant = makeVariantName(scope);
		variant.setReader(value, length, datatype);
		return variant;
	}
}
