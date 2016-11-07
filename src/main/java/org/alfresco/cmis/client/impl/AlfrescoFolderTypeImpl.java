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

import org.alfresco.cmis.client.AlfrescoFolder;
import org.alfresco.cmis.client.AlfrescoFolderType;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeMutability;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeMutabilityImpl;

public class AlfrescoFolderTypeImpl implements AlfrescoFolderType
{
    private static final long serialVersionUID = 1L;

    private AlfrescoFolder folder;

    public AlfrescoFolderTypeImpl(AlfrescoFolder folder)
    {
        this.folder = folder;
    }

    public List<CmisExtensionElement> getExtensions()
    {
        return folder.getType().getExtensions();
    }

    public void setExtensions(List<CmisExtensionElement> extensions)
    {
        folder.getType().setExtensions(extensions);
    }

    public String getId()
    {
        return folder.getType().getId();
    }

    public String getLocalName()
    {
        return folder.getType().getLocalName();
    }

    public String getLocalNamespace()
    {
        return folder.getType().getLocalNamespace();
    }

    public String getDisplayName()
    {
        return folder.getType().getDisplayName();
    }

    public String getQueryName()
    {
        return folder.getType().getQueryName();
    }

    public String getDescription()
    {
        return folder.getType().getDescription();
    }

    public BaseTypeId getBaseTypeId()
    {
        return folder.getType().getBaseTypeId();
    }

    public String getParentTypeId()
    {
        return folder.getType().getParentTypeId();
    }

    public Boolean isCreatable()
    {
        return folder.getType().isCreatable();
    }

    public Boolean isFileable()
    {
        return folder.getType().isFileable();
    }

    public Boolean isQueryable()
    {
        return folder.getType().isQueryable();
    }

    public Boolean isFulltextIndexed()
    {
        return folder.getType().isFulltextIndexed();
    }

    public Boolean isIncludedInSupertypeQuery()
    {
        return folder.getType().isIncludedInSupertypeQuery();
    }

    public Boolean isControllablePolicy()
    {
        return folder.getType().isControllablePolicy();
    }

    public Boolean isControllableAcl()
    {
        return folder.getType().isControllableAcl();
    }

    public Map<String, PropertyDefinition<?>> getPropertyDefinitions()
    {

        Map<String, PropertyDefinition<?>> result = new HashMap<String, PropertyDefinition<?>>(folder.getType()
                .getPropertyDefinitions());

        for (ObjectType aspect : folder.getAspects())
        {
            result.putAll(aspect.getPropertyDefinitions());
        }

        return result;
    }

    public Collection<ObjectType> getAspects()
    {
        return folder.getAspects();
    }

    public boolean isBaseType()
    {
        return folder.getType().isBaseType();
    }

    public ObjectType getBaseType()
    {
        return folder.getType().getBaseType();
    }

    public ObjectType getParentType()
    {
        return folder.getType().getParentType();
    }

    public ItemIterable<ObjectType> getChildren()
    {
        return folder.getType().getChildren();
    }

    public List<Tree<ObjectType>> getDescendants(int depth)
    {
        return folder.getType().getDescendants(depth);
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
