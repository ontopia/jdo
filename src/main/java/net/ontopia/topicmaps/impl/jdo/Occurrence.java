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
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;

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

	//@Persistent(name = "datatype", column = "datatype")
	@NotPersistent
	private LocatorIF dataType;

	@Persistent(name = "value", column = "value")
	@Column(jdbcType = "LONGVARCHAR")
	private String value;

	@Persistent(name = "length", column = "length")
	private long length;

	Occurrence(Topic topic, Topic type) {
		super(type);
		this.topic = topic;
	}

	@Override
	protected String getClassIndicator() {
		return "O";
	}

	public TopicIF getTopic() {
		return topic;
	}

	public LocatorIF getDataType() {
		return dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.value = value;
	}

	public LocatorIF getLocator() {
		if (dataType.getAddress().equals("")) { // todo
			return URILocator.create(getValue());
		}
		return null;
	}

	public void setLocator(LocatorIF locator) {
		if (isReadOnly()) throw new ReadOnlyException();
		setValue(locator.getAddress(), null); // todo: datatype uri
	}

	public void setValue(String value, LocatorIF datatype) {
		if (isReadOnly()) throw new ReadOnlyException();
		setValue(value);
		this.dataType = datatype;
	}

	public Reader getReader() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void setReader(Reader value, long length, LocatorIF datatype) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public long getLength() {
		return length;
	}
}
