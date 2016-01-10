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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.AbstractLocator;
import net.ontopia.infoset.impl.basic.URILocator;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Unique(members = {"address", "topicmap"})
public abstract class AbstractJDOLocator extends AbstractLocator implements Externalizable {
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(name = "id", valueStrategy=IdGeneratorStrategy.NATIVE)
	private long id;
	
	@Persistent(name = "address", column = "address")
	@Column(jdbcType = "LONGVARCHAR")
	protected String address;
	
	@NotPersistent
	protected String _address;
	
	@Persistent(name = "topicmap", column = "topicmap")
	protected TopicMap topicmap;
	
	public AbstractJDOLocator() {
	}

	public AbstractJDOLocator(LocatorIF locator, TMObject object) {
		this(locator.getAddress(), object);
	}

	private AbstractJDOLocator(String address, TMObject object) {
		if (address == null) {
			throw new NullPointerException("The locator address cannot be null.");
		}
		this.address = address;
		this._address = address;
		this.topicmap = (TopicMap) object.getTopicMap();
	}

	@Override
	public String getNotation() {
		return "URI";
	}

	@Override
	public String getAddress() {
		ObjectState objectState = JDOHelper.getObjectState(this);
		if ((objectState == ObjectState.PERSISTENT_DELETED) || (objectState == ObjectState.PERSISTENT_NEW_DELETED)) {
			return _address;
		}
		
		return address;
	}
	
	void preRemove() {
		// internal detach copy on delete, allows re-use
		// see LocatorTest
		_address = address;
	}

	@Override
	public LocatorIF resolveAbsolute(String address) {
		// FIXME: should use static method instead of creating URILocator instance
		try {
			return new URILocator(this.address).resolveAbsolute(address);
		} catch (java.net.MalformedURLException e) {
			// use RDBMS locator
		}
	    // Since this locator is general we cannot make the address
		// absolute, so we'll just return a new locator with the same
		// address instead.
		return this;
	}

	@Override
	public String getExternalForm() {
		// FIXME: should use static method instead of creating URILocator instance
		try {
			return new URILocator(this.address).getExternalForm();
		} catch (java.net.MalformedURLException e) {
			// use existing address
		}
		// this locator is general so we don't know how to do this
		return address;
	}

	@Override
	public int hashCode() {
		return getAddress().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LocatorIF)) return false;
		try {
			LocatorIF locator = (LocatorIF) object;
			return getAddress().equals(locator.getAddress());
		} catch (ClassCastException e) {
			return false; // In case the object is not a locator
		} catch (NullPointerException e) {
			return false; // In case the object is null
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(address);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		address = in.readUTF();
	}
}
