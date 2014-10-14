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

package net.ontopia.topicmaps.impl.jdo.index;

import javax.jdo.Query;
import net.ontopia.topicmaps.impl.jdo.TopicMap;

public class AbstractIndex {

	protected final TopicMap topicmap;

	protected AbstractIndex(TopicMap topicmap) {
		this.topicmap = topicmap;
	}
	
	protected Query getQuery(String name) {
		return topicmap.getQuery(name);
	}
}
