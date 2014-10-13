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

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TypedIF;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Typed extends Reifiable implements TypedIF {
	
	@Persistent(name = "type", column = "type")
	private Topic type;

	Typed(Topic type) {
		super((TopicMap) type.getTopicMap());
		this.type = type;
	}

	@Override
	public TopicIF getType() {
		return type;
	}

	@Override
	public void setType(TopicIF type) {
		if (isReadOnly()) throw new ReadOnlyException();
		// todo: class check
		this.type = (Topic) type;
	}
}
