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
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
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

	public TopicName(Topic topic, Topic type, String value) {
		super(type);
		this.topic = topic;
		this.value = value;
	}

	@Override
	protected String getClassIndicator() {
		return "N";
	}

	public Collection<VariantNameIF> getVariants() {
		return new HashSet<VariantNameIF>(variants);
	}

	public TopicIF getTopic() {
		return topic;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (isReadOnly()) throw new ReadOnlyException();
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
}
