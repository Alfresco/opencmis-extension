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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;

import org.alfresco.cmis.client.type.AlfrescoDocumentType;
import org.alfresco.cmis.client.type.AlfrescoFolderType;
import org.alfresco.cmis.client.type.AlfrescoItemType;
import org.alfresco.cmis.client.type.AlfrescoPolicyType;
import org.alfresco.cmis.client.type.AlfrescoRelationshipType;
import org.alfresco.cmis.client.type.AlfrescoSecondaryType;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ItemImpl;
import org.apache.chemistry.opencmis.client.runtime.PolicyImpl;
import org.apache.chemistry.opencmis.client.runtime.RelationshipImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionImpl;
import org.apache.chemistry.opencmis.client.runtime.repository.ObjectFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.definitions.DocumentTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.FolderTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.ItemTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PolicyTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.RelationshipTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.SecondaryTypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

public class AlfrescoObjectFactoryImpl extends ObjectFactoryImpl
{
    private static final long serialVersionUID = 1L;

    private Session session = null;

    /**
     * Default constructor.
     */
    public AlfrescoObjectFactoryImpl()
    {
    }

    @Override
    public ObjectType convertTypeDefinition(TypeDefinition typeDefinition)
    {
    	ObjectType ret = null;

        if (typeDefinition instanceof DocumentTypeDefinition) {
        	ret = new AlfrescoDocumentType(this.session, (DocumentTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof FolderTypeDefinition) {
        	ret = new AlfrescoFolderType(this.session, (FolderTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof RelationshipTypeDefinition) {
        	ret = new AlfrescoRelationshipType(this.session, (RelationshipTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof PolicyTypeDefinition) {
        	ret = new AlfrescoPolicyType(this.session, (PolicyTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof SecondaryTypeDefinition) {
        	ret = new AlfrescoSecondaryType(this.session, (SecondaryTypeDefinition) typeDefinition);
        } else if (typeDefinition instanceof ItemTypeDefinition) {
        	ret = new AlfrescoItemType(this.session, (ItemTypeDefinition) typeDefinition);
        } else {
            throw new CmisRuntimeException("Unknown base type!");
        }

    	return ret;
    }
    
    public void initialize(Session session, Map<String, String> parameters)
    {
        super.initialize(session, parameters);
        this.session = session;
    }

    public Properties convertProperties(Map<String, ?> properties, ObjectType type, Collection<SecondaryType> secondaryTypes, Set<Updatability> updatabilityFilter)
    {
        // check input
        if (properties == null)
        {
            return null;
        }

        // get the object and aspect types
        Object typeId = properties.get(PropertyIds.OBJECT_TYPE_ID);
        String typeIdStr = null;

        if (typeId instanceof String)
        {
            typeIdStr = typeId.toString();
        } else if (typeId instanceof Property<?>)
        {
            Object propValue = ((Property<?>) typeId).getFirstValue();
            typeIdStr = (propValue == null ? null : propValue.toString());
        }

        if (typeIdStr == null)
        {
            throw new IllegalArgumentException("Type property must be set!");
        }

        // CMIS 1.0, secondary types/aspects are in the objectTypeId
        ObjectType objectType = null;
        List<ObjectType> aspectTypes = new ArrayList<ObjectType>();
        List<String> secondaryTypesPropValue = new ArrayList<String>();
        if (typeIdStr.indexOf(',') == -1)
        {
            objectType = session.getTypeDefinition(typeIdStr);
        } else
        {
            String[] typeIds = typeIdStr.split(",");
            objectType = session.getTypeDefinition(typeIds[0].trim());

            for (int i = 1; i < typeIds.length; i++)
            {
            	String secondaryTypeId = typeIds[i].trim();
            	secondaryTypesPropValue.add(secondaryTypeId);
                aspectTypes.add(session.getTypeDefinition(secondaryTypeId));
            }
        }

        // split type properties from aspect properties
        Map<String, Object> typeProperties = new HashMap<String, Object>();
        Map<String, Object> aspectProperties = new HashMap<String, Object>();
        Map<String, PropertyDefinition<?>> aspectPropertyDefinition = new HashMap<String, PropertyDefinition<?>>();
        for (Map.Entry<String, ?> property : properties.entrySet())
        {
            if ((property == null) || (property.getKey() == null))
            {
                continue;
            }

            String id = property.getKey();
            Object value = property.getValue();

            if (PropertyIds.OBJECT_TYPE_ID.equals(id))
            {
                if (type == null)
                {
                    typeProperties.put(id, objectType.getId());
                }
            } else if (objectType.getPropertyDefinitions().containsKey(id))
            {
                typeProperties.put(id, value);
            } else
            {
                aspectProperties.put(id, value);

                for (ObjectType aspectType : aspectTypes)
                {
                    if (aspectType.getPropertyDefinitions() == null)
                    {
                        continue;
                    }

                    PropertyDefinition<?> propDef = aspectType.getPropertyDefinitions().get(id);
                    if (propDef != null)
                    {
                        aspectPropertyDefinition.put(id, propDef);
                        break;
                    }
                }

                if (!aspectPropertyDefinition.containsKey(id))
                {
                    throw new IllegalArgumentException("Property '" + id
                            + "' is neither an object type property nor an aspect property!");
                }
            }
        }

        if(session.getRepositoryInfo().getCmisVersion().equals(CmisVersion.CMIS_1_1))
        {
	        // cmis 1.1 - secondary types
	        typeProperties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypesPropValue);
        }

        // prepare type properties
        Properties result = super.convertProperties(typeProperties, type, secondaryTypes, updatabilityFilter);

        if(session.getRepositoryInfo().getCmisVersion().equals(CmisVersion.CMIS_1_0))
        {
	        // prepare extensions
	        List<CmisExtensionElement> alfrescoExtensionList = new ArrayList<CmisExtensionElement>();
	
	        // prepare aspects
	        for (ObjectType aspectType : aspectTypes)
	        {
	            alfrescoExtensionList.add(AlfrescoUtils.createAspectsToAddExtension(aspectType));
	        }
	
	        // prepare aspect properties
	        if (!aspectProperties.isEmpty())
	        {
	            List<CmisExtensionElement> propertrtyExtensionList = new ArrayList<CmisExtensionElement>();
	
	            for (Map.Entry<String, Object> property : aspectProperties.entrySet())
	            {
	                PropertyDefinition<?> propDef = aspectPropertyDefinition.get(property.getKey());
	                if (propDef == null)
	                {
	                    throw new IllegalArgumentException("Unknown aspect property: " + property.getKey());
	                }
	
	                CmisExtensionElement element = AlfrescoUtils.createAspectPropertyExtension(propDef,
	                        property.getValue());
	                if (element != null)
	                {
	                    propertrtyExtensionList.add(element);
	                }
	            }
	
	            alfrescoExtensionList.add(AlfrescoUtils.createAspectPropertiesExtension(propertrtyExtensionList));
	        }
	
	        if (!alfrescoExtensionList.isEmpty())
	        {
	            result.setExtensions(Collections.singletonList(AlfrescoUtils
	                    .createSetAspectsExtension(alfrescoExtensionList)));
	        }
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Property<?>> convertProperties(ObjectType objectType, Collection<SecondaryType> secondaryTypes, Properties properties)
    {
        Map<String, Property<?>> result = super.convertProperties(objectType, secondaryTypes, properties);

        if(session.getRepositoryInfo().getCmisVersion().equals(CmisVersion.CMIS_1_0))
        {
	        // find the Alfresco extensions
	        List<CmisExtensionElement> alfrescoExtensions = AlfrescoUtils.findAlfrescoExtensions(properties
	                .getExtensions());
	
	        if (alfrescoExtensions == null)
	        {
	            // no Alfresco extensions found
	            return result;
	        }
	
	        // get the aspect types
	        Collection<ObjectType> aspectTypes = AlfrescoUtils.getAspectTypes(session, alfrescoExtensions);
	
	        for (CmisExtensionElement extension : alfrescoExtensions)
	        {
	            if (!extension.getName().equals("properties"))
	            {
	                continue;
	            }
	
	            for (CmisExtensionElement property : extension.getChildren())
	            {
	                String id = property.getAttributes().get("propertyDefinitionId");
	
	                // find the aspect type
	                ObjectType aspectType = AlfrescoUtils.findAspect(aspectTypes, id);
	                if (aspectType == null)
	                {
	                    throw new IllegalArgumentException("Unknown aspect property: " + id);
	                }
	
	                // convert values
	                PropertyDefinition propDef = aspectType.getPropertyDefinitions().get(id);
	                List values = new ArrayList();
	                DatatypeFactory df = null;
	                try
	                {
	                    for (CmisExtensionElement propertyValues : property.getChildren())
	                    {
	                        switch (propDef.getPropertyType())
	                        {
	                        case BOOLEAN:
	                            values.add(Boolean.parseBoolean(propertyValues.getValue()));
	                            break;
	                        case DATETIME:
	                            if (df == null)
	                            {
	                                df = DatatypeFactory.newInstance();
	                            }
	                            values.add(df.newXMLGregorianCalendar(propertyValues.getValue()).toGregorianCalendar());
	                            break;
	                        case DECIMAL:
	                            values.add(new BigDecimal(propertyValues.getValue()));
	                            break;
	                        case INTEGER:
	                            values.add(new BigInteger(propertyValues.getValue()));
	                            break;
	                        default:
	                            values.add(propertyValues.getValue());
	                        }
	                    }
	                } catch (Exception e)
	                {
	                    throw new IllegalArgumentException("Aspect conversation exception: " + e.getMessage(), e);
	                }
	
	                // add property
	                result.put(id, createProperty(propDef, values));
	            }
	        }
        }

        return result;
    }

    public CmisObject convertObject(ObjectData objectData, OperationContext context)
    {
        if (objectData == null)
        {
            throw new IllegalArgumentException("Object data is null!");
        }

        ObjectType type = getTypeFromObjectData(objectData);

        /* determine type */
        switch (objectData.getBaseTypeId())
        {
        case CMIS_DOCUMENT:
            return new AlfrescoDocumentImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_FOLDER:
            return new AlfrescoFolderImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_POLICY:
            return new PolicyImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_RELATIONSHIP:
            return new RelationshipImpl((SessionImpl) this.session, type, objectData, context);
        case CMIS_ITEM:
            return new ItemImpl((SessionImpl) this.session, type, objectData, context);

        default:
            throw new CmisRuntimeException("unsupported type: " + objectData.getBaseTypeId());
        }
    }
}
