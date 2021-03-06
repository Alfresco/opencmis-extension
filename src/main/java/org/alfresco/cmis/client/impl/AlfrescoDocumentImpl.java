/*
 * Copyright 2005-2011 Alfresco Software Limited.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.cmis.client.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoDocumentType;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.runtime.DocumentImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionImpl;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;

public class AlfrescoDocumentImpl extends DocumentImpl implements
		AlfrescoDocument {
	private static final long serialVersionUID = 1L;

	protected AlfrescoAspectsImpl aspects;

	public AlfrescoDocumentImpl(SessionImpl session, ObjectType objectType,
			ObjectData objectData, OperationContext context) {
		super(session, objectType, objectData, context);
	}

	@Override
	protected void initialize(SessionImpl session, ObjectType objectType,
			ObjectData objectData, OperationContext context) {
		super.initialize(session, objectType, objectData, context);
		aspects = new AlfrescoAspectsImpl(session, this);
	}

	public AlfrescoDocumentType getTypeWithAspects() {
		readLock();
		try {
            return new AlfrescoDocumentTypeImpl(this);
		} finally {
			readUnlock();
		}
	}

	@Override
	public ObjectId updateProperties(Map<String, ?> properties, boolean refresh) {
		return super.updateProperties(
				AlfrescoUtils.preparePropertiesForUpdate(properties,
						getType(), getAspects()), refresh);
	}

	@Override
	public ObjectId checkIn(boolean major, Map<String, ?> properties,
			ContentStream contentStream, String checkinComment,
			List<Policy> policies, List<Ace> addAces, List<Ace> removeAces) {
		return super.checkIn(major,
				AlfrescoUtils.preparePropertiesForUpdate(properties,
						getType(), getAspects()), contentStream,
				checkinComment, policies, addAces, addAces);
	}

	public boolean hasAspect(String id) {
		readLock();
		try {
			return aspects.hasAspect(id);
		} finally {
			readUnlock();
		}
	}

	public boolean hasAspect(ObjectType type) {
		readLock();
		try {
			return aspects.hasAspect(type);
		} finally {
			readUnlock();
		}
	}

	public Collection<ObjectType> getAspects() {
		readLock();
		try {
			return aspects.getAspects();
		} finally {
			readUnlock();
		}
	}

	public ObjectType findAspect(String propertyId) {
		readLock();
		try {
			return aspects.findAspect(propertyId);
		} finally {
			readUnlock();
		}
	}

	public CmisObject addAspect(String... id) {
		CmisObject ret = this;

		readLock();
		try {
			ret = aspects.addAspect(id);
		} finally {
			readUnlock();
		}
		refresh();

		return ret;
	}

	public CmisObject addAspect(ObjectType... type) {
		CmisObject ret = this;

		readLock();
		try {
			ret = aspects.addAspect(type);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}

	public CmisObject addAspect(ObjectType type, Map<String, ?> properties) {
		CmisObject ret = this;
		
		readLock();
		try {
			ret = aspects.addAspect(type, properties);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}

	public CmisObject addAspect(ObjectType[] type, Map<String, ?> properties) {
		CmisObject ret = this;

		readLock();
		try {
			ret = aspects.addAspect(type, properties);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}

	public CmisObject addAspect(String id, Map<String, ?> properties) {
		CmisObject ret = this;
		
		readLock();
		try {
			ret = aspects.addAspect(id, properties);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}

	public CmisObject addAspect(String[] id, Map<String, ?> properties) {
		CmisObject ret = this;
		
		readLock();
		try {
			ret = aspects.addAspect(id, properties);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}

	public CmisObject removeAspect(String... id) {
		CmisObject ret = this;

		readLock();
		try {
			aspects.removeAspect(id);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}

	public CmisObject removeAspect(ObjectType... type) {
		CmisObject ret = this;

		readLock();
		try {
			ret = aspects.removeAspect(type);
		} finally {
			readUnlock();
		}
		refresh();
		
		return ret;
	}
}
