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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoAspects;
import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;

public class AlfrescoAspectsImpl implements AlfrescoAspects {
	private Session session;
	private CmisObject object;
	private Map<String, ObjectType> aspectTypes;

	public AlfrescoAspectsImpl(Session session, CmisObject object) {
		this.session = session;
		this.object = object;

		Property<?> secondaryTypesProp = object.getProperty(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
		if(secondaryTypesProp != null)
		{
			// cmis 1.1
			List<String> secondaryTypes = secondaryTypesProp.getValue();
			aspectTypes = new HashMap<String, ObjectType>();
			for(String secondaryType : secondaryTypes)
			{
	            ObjectType aspectType = session.getTypeDefinition(secondaryType);
	            aspectTypes.put(aspectType.getId(), aspectType);
			}
		}
		else
		{
			// cmis 1.0
			List<CmisExtensionElement> alfrescoExtensions = AlfrescoUtils
					.findAlfrescoExtensions(object
							.getExtensions(ExtensionLevel.PROPERTIES));
	
			if (alfrescoExtensions == null) {
				aspectTypes = Collections.emptyMap();
			} else {
				aspectTypes = new HashMap<String, ObjectType>();
				for (ObjectType type : AlfrescoUtils.getAspectTypes(session,
						alfrescoExtensions)) {
					if (type != null) {
						aspectTypes.put(type.getId(), type);
					}
				}
			}
		}
	}

    public ObjectType getTypeWithAspects() {
		if (object instanceof AlfrescoDocument) {
			return new AlfrescoDocumentTypeImpl((AlfrescoDocument) object);
		} else if (object instanceof AlfrescoFolder) {
			return new AlfrescoFolderTypeImpl((AlfrescoFolder) object);
		} else {
			return object.getType();
		}
	}

	public boolean hasAspect(String id) {
		return aspectTypes.containsKey(id);
	}

	public boolean hasAspect(ObjectType type) {
		return type == null ? false : hasAspect(type.getId());
	}

	public Collection<ObjectType> getAspects() {
		return aspectTypes.values();
	}

	public ObjectType findAspect(String propertyId) {
		return AlfrescoUtils
				.findAspect(aspectTypes.values(), propertyId);
	}

	public CmisObject addAspect(String... id) {
		if (id == null || id.length == 0) {
			throw new IllegalArgumentException("Id must be set!");
		}

		ObjectType[] types = new ObjectType[id.length];
		for (int i = 0; i < id.length; i++) {
			types[i] = session.getTypeDefinition(id[i]);
		}

		return addAspect(types);
	}

	public CmisObject addAspect(ObjectType... type) {
		CmisObject ret = object;

		if (type == null || type.length == 0) {
			throw new IllegalArgumentException("Type must be set!");
		}

		String objectId = AlfrescoUtils.updateAspects(session, object, type, null,
				null);
        if (!object.getId().equals(objectId)) {
            ret = session.getObject(objectId);
        }
		return ret;
	}

	public CmisObject addAspect(ObjectType type, Map<String, ?> properties) {
		return addAspect(new ObjectType[] { type }, properties);
	}

	public CmisObject addAspect(ObjectType[] type, Map<String, ?> properties) {
		CmisObject ret = object;

		if (type == null || type.length == 0) {
			throw new IllegalArgumentException("Type must be set!");
		}

		String objectId = AlfrescoUtils.updateAspects(session, object, type, null,
				properties);
        if (!object.getId().equals(objectId)) {
            ret = session.getObject(objectId);
        }
		return ret;
	}

	public CmisObject addAspect(String id, Map<String, ?> properties) {
		return addAspect(new String[] { id }, properties);
	}

	public CmisObject addAspect(String[] id, Map<String, ?> properties) {
		if (id == null || id.length == 0) {
			throw new IllegalArgumentException("Id must be set!");
		}

		ObjectType[] types = new ObjectType[id.length];
		for (int i = 0; i < id.length; i++) {
			types[i] = session.getTypeDefinition(id[i]);
		}

		return addAspect(types, properties);
	}

	public CmisObject removeAspect(String... id) {
		if (id == null || id.length == 0) {
			throw new IllegalArgumentException("Id must be set!");
		}

		ObjectType[] types = new ObjectType[id.length];
		for (int i = 0; i < id.length; i++) {
			types[i] = session.getTypeDefinition(id[i]);
		}

		return removeAspect(types);
	}

	public CmisObject removeAspect(ObjectType... type) {
		CmisObject ret = object;
		if (type == null || type.length == 0) {
			throw new IllegalArgumentException("Type must be set!");
		}

		String objectId = AlfrescoUtils.updateAspects(session, object, null, type,
				null);
        if (!object.getId().equals(objectId)) {
            ret = session.getObject(objectId);
        }
        return ret;
	}
}
