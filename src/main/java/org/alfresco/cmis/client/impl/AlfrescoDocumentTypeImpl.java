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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoDocumentType;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.definitions.DocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeMutability;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeMutabilityImpl;

public class AlfrescoDocumentTypeImpl implements AlfrescoDocumentType {
	private static final long serialVersionUID = 1L;

	private AlfrescoDocument doc;

	public AlfrescoDocumentTypeImpl(AlfrescoDocument doc) {
		this.doc = doc;
	}

	public Boolean isVersionable() {
		return ((DocumentTypeDefinition) doc.getType()).isVersionable();
	}

	public ContentStreamAllowed getContentStreamAllowed() {
		return ((DocumentTypeDefinition) doc.getType())
				.getContentStreamAllowed();
	}

    public List<CmisExtensionElement> getExtensions()
    {
        return doc.getType().getExtensions();
    }

    public void setExtensions(List<CmisExtensionElement> extensions)
    {
        doc.getType().setExtensions(extensions);
    }

	public String getId() {
		return doc.getType().getId();
	}

	public String getLocalName() {
		return doc.getType().getLocalName();
	}

	public String getLocalNamespace() {
		return doc.getType().getLocalNamespace();
	}

	public String getDisplayName() {
		return doc.getType().getDisplayName();
	}

	public String getQueryName() {
		return doc.getType().getQueryName();
	}

	public String getDescription() {
		return doc.getType().getDescription();
	}

	public BaseTypeId getBaseTypeId() {
		return doc.getType().getBaseTypeId();
	}

	public String getParentTypeId() {
		return doc.getType().getParentTypeId();
	}

	public Boolean isCreatable() {
		return doc.getType().isCreatable();
	}

	public Boolean isFileable() {
		return doc.getType().isFileable();
	}

	public Boolean isQueryable() {
		return doc.getType().isQueryable();
	}

	public Boolean isFulltextIndexed() {
		return doc.getType().isFulltextIndexed();
	}

	public Boolean isIncludedInSupertypeQuery() {
		return doc.getType().isIncludedInSupertypeQuery();
	}

	public Boolean isControllablePolicy() {
		return doc.getType().isControllablePolicy();
	}

	public Boolean isControllableAcl() {
		return doc.getType().isControllableAcl();
	}

	public Map<String, PropertyDefinition<?>> getPropertyDefinitions() {

		Map<String, PropertyDefinition<?>> result = new HashMap<String, PropertyDefinition<?>>(
				doc.getType().getPropertyDefinitions());

		for (ObjectType aspect : doc.getAspects()) {
			result.putAll(aspect.getPropertyDefinitions());
		}

		return result;
	}

	public Collection<ObjectType> getAspects() {
		return doc.getAspects();
	}

	public boolean isBaseType() {
		return doc.getType().isBaseType();
	}

	public ObjectType getBaseType() {
		return doc.getType().getBaseType();
	}

	public ObjectType getParentType() {
		return doc.getType().getParentType();
	}

	public ItemIterable<ObjectType> getChildren() {
		return doc.getType().getChildren();
	}

	public List<Tree<ObjectType>> getDescendants(int depth) {
		return doc.getType().getDescendants(depth);
	}

	public TypeMutability getTypeMutability()
	{
		TypeMutabilityImpl typeMutability = new TypeMutabilityImpl();
		typeMutability.setCanCreate(Boolean.FALSE);
		typeMutability.setCanDelete(Boolean.FALSE);
		typeMutability.setCanUpdate(Boolean.FALSE);
		return typeMutability;
	}
}
