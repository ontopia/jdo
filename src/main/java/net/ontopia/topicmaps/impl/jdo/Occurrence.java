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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;

@PersistenceCapable(table = "TM_OCCURRENCE")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Indices({
	///@Index(name = "TM_OCCURRENCE_IX_ID_TOPICMAP_HASH") // hashcode is not implemented here
	@Index(name = "TM_OCCURRENCE_IX_ID_TOPICMAP_TYPE", members = {"id", "topicmap", "type"}),
	@Index(name = "TM_OCCURRENCE_IX_TOPIC", members = {"topic"})
})
public class Occurrence extends Scoped implements OccurrenceIF {
	
	@Persistent(name = "topic", column = "topic")
	private Topic topic;

	@Persistent(name = "datatype", column = "datatype")
	@Column(jdbcType = "LONGVARCHAR")
	private String datatype = DataTypes.TYPE_STRING.getAddress();

	@Persistent(name = "value", column = "value")
	@Column(jdbcType = "LONGVARCHAR")
	private String value;

	@Persistent(name = "length", column = "length")
	private long length;
	@Persistent(name = "type", column = "type")
	private Topic type;

	Occurrence(Topic topic, Topic type) {
		super((TopicMap) topic.getTopicMap());
		this.topic = topic;
		this.type = type;
	}

	@Override
	protected String getClassIndicator() {
		return "O";
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
		if (isReadOnly())throw new ReadOnlyException();
		if (type == null) throw new NullPointerException("Type cannot be null");
		this.type = (Topic) type;
	}

	@Override
	public LocatorIF getDataType() {
		return URILocator.create(datatype);
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
	public LocatorIF getLocator() {
		if (value == null) return null;
		if (DataTypes.TYPE_URI.getAddress().equals(datatype)) {
			return URILocator.create(value);
		}
		return null;
	}

	@Override
	public void setLocator(LocatorIF locator) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		if (!"URI".equalsIgnoreCase(locator.getNotation())) throw new ConstraintViolationException("Only URI Locators are allowed");
		setValue(locator.getAddress(), DataTypes.TYPE_URI);
	}

	@Override
	public void setValue(String value, LocatorIF datatype) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (datatype == null) throw new NullPointerException("Datatype cannot be null");
		if (!"URI".equalsIgnoreCase(datatype.getNotation())) throw new ConstraintViolationException("Only URI Locators are allowed for datatype");
		setValue(value);
		this.datatype = datatype.getAddress();
	}

	@Override
	public Reader getReader() {
		if (value == null) return null;
		return new StringReader(value);
	}

	@Override
	public void setReader(Reader value, long length, LocatorIF datatype) {
		if (value == null) throw new NullPointerException("Reader cannot be null");
		try {
			setValue(StreamUtils.readString(value, length), datatype);
		} catch (IOException ioe) {
			throw new OntopiaRuntimeException(ioe);
		}
	}

	@Override
	public long getLength() {
		return (value == null ? 0 : value.length());
	}
}
