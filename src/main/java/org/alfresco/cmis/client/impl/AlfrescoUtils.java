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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CmisExtensionElementImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.spi.Holder;

public class AlfrescoUtils
{
    private AlfrescoUtils()
    {
    }

    public static final String ALFRESCO_NAMESPACE = "http://www.alfresco.org";
    public static final String CMIS_NAMESPACE = "http://docs.oasis-open.org/ns/cmis/core/200908/";
    public static final String APPLIED_ASPECTS = "appliedAspects";
    public static final String SET_ASPECTS = "setAspects";
    public static final String ASPECTS_TO_ADD = "aspectsToAdd";
    public static final String ASPECTS_TO_REMOVE = "aspectsToRemove";
    public static final String PROPERTIES = "properties";
    public static final String MANDATORY_ASPECTS = "mandatoryAspects";
    public static final String MANDATORY_ASPECT = "mandatoryAspect";

    /**
     * Finds the Alfresco extensions within the given extensions.
     */
    public static List<CmisExtensionElement> findAlfrescoExtensions(List<CmisExtensionElement> extensions)
    {
        if (extensions == null || extensions.isEmpty())
        {
            return null;
        }

        for (CmisExtensionElement ext : extensions)
        {
            if (ALFRESCO_NAMESPACE.equals(ext.getNamespace()))
            {
                return ext.getChildren();
            }
        }

        return null;
    }

    /**
     * Returns the aspect types from the Alfresco extensions.
     */
    public static Collection<ObjectType> getAspectTypes(Session session, List<CmisExtensionElement> alfrescoExtensions)
    {
        Collection<ObjectType> aspectTypes = new ArrayList<ObjectType>();

        for (CmisExtensionElement extension : alfrescoExtensions)
        {
            if (extension.getName().equals(APPLIED_ASPECTS))
            {
                ObjectType aspectType = session.getTypeDefinition(extension.getValue());
                aspectTypes.add(aspectType);
            }
        }

        return aspectTypes;
    }

    /**
     * Finds the aspect type that contains the given property id.
     */
    public static ObjectType findAspect(Collection<ObjectType> aspectTypes, String propertyId)
    {
        if (aspectTypes == null || propertyId == null)
        {
            return null;
        }

        for (ObjectType type : aspectTypes)
        {
            if (type != null && type.getPropertyDefinitions() != null
                    && type.getPropertyDefinitions().containsKey(propertyId))
            {
                return type;
            }
        }

        return null;
    }

    /**
     * Creates a setAspects extension element.
     */
    public static CmisExtensionElement createSetAspectsExtension(List<CmisExtensionElement> setAspectsChildren)
    {
        return new CmisExtensionElementImpl(ALFRESCO_NAMESPACE, SET_ASPECTS, null, setAspectsChildren);
    }

    /**
     * Creates an aspectsToAdd extension element.
     */
    public static CmisExtensionElement createAspectsToAddExtension(ObjectType aspectType)
    {
        return new CmisExtensionElementImpl(ALFRESCO_NAMESPACE, ASPECTS_TO_ADD, null, aspectType.getId());
    }

    /**
     * Creates an aspectsToRemove extension element.
     */
    public static CmisExtensionElement createAspectsToRemoveExtension(ObjectType aspectType)
    {
        return new CmisExtensionElementImpl(ALFRESCO_NAMESPACE, ASPECTS_TO_REMOVE, null, aspectType.getId());
    }

    /**
     * Creates an aspectsToAdd extension element.
     */
    public static CmisExtensionElement createAspectPropertiesExtension(List<CmisExtensionElement> propertiesChildren)
    {
        return new CmisExtensionElementImpl(ALFRESCO_NAMESPACE, PROPERTIES, null, propertiesChildren);
    }

    /**
     * Creates a property extension element.
     */
    @SuppressWarnings("rawtypes")
    public static CmisExtensionElement createAspectPropertyExtension(PropertyDefinition<?> propertyDefintion,
            Object value)
    {
        String name;
        switch (propertyDefintion.getPropertyType())
        {
        case BOOLEAN:
            name = "propertyBoolean";
            break;
        case DATETIME:
            name = "propertyDateTime";
            break;
        case DECIMAL:
            name = "propertyDecimal";
            break;
        case INTEGER:
            name = "propertyInteger";
            break;
        case ID:
            name = "propertyId";
            break;
        case HTML:
            name = "propertyHtml";
            break;
        case URI:
            name = "propertyUri";
            break;
        default:
            name = "propertyString";
        }

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("propertyDefinitionId", propertyDefintion.getId());

        List<CmisExtensionElement> propertyValues = new ArrayList<CmisExtensionElement>();
        if (value != null)
        {
            if (value instanceof Property<?>)
            {
                value = ((Property<?>) value).getValues();
            }

            if (value instanceof List)
            {
                for (Object o : ((List) value))
                {
                    propertyValues.add(new CmisExtensionElementImpl(CMIS_NAMESPACE, "value", null,
                            convertAspectPropertyValue(o)));
                }
            } else
            {
                propertyValues.add(new CmisExtensionElementImpl(CMIS_NAMESPACE, "value", null,
                        convertAspectPropertyValue(value)));
            }
        }

        return new CmisExtensionElementImpl(CMIS_NAMESPACE, name, attributes, propertyValues);
    }

    private static String convertAspectPropertyValue(Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Aspect property value must not be null!");
        }

        if (value instanceof GregorianCalendar)
        {
            DatatypeFactory df;
            try
            {
                df = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e)
            {
                throw new IllegalArgumentException("Aspect conversation exception: " + e.getMessage(), e);
            }
            return df.newXMLGregorianCalendar((GregorianCalendar) value).toXMLFormat();
        } else if (!(value instanceof String) && !(value instanceof Number) && !(value instanceof Boolean))
        {
            throw new IllegalArgumentException("Invalid aspect value!");
        }

        return value.toString();
    }

    /**
     * Checks a property value.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> checkProperty(PropertyDefinition<T> propertyDefinition, Object value)
    {
        // null values are ok for updates
        if (value == null)
        {
            return null;
        }

        // single and multi value check
        List<T> values = null;
        if (value instanceof List<?>)
        {
            if (propertyDefinition.getCardinality() != Cardinality.MULTI)
            {
                throw new IllegalArgumentException("Property '" + propertyDefinition.getId()
                        + "' is not a multi value property!");
            }

            values = (List<T>) value;
            if (values.isEmpty())
            {
                return values;
            }
        } else
        {
            if (propertyDefinition.getCardinality() != Cardinality.SINGLE)
            {
                throw new IllegalArgumentException("Property '" + propertyDefinition.getId()
                        + "' is not a single value property!");
            }

            values = Collections.singletonList((T) value);
        }

        // check if list contains null values
        for (Object o : values)
        {
            if (o == null)
            {
                throw new IllegalArgumentException("Property '" + propertyDefinition.getId()
                        + "' contains null values!");
            }
        }

        // take a sample and test the data type
        boolean typeMatch = false;
        Object firstValue = values.get(0);

        switch (propertyDefinition.getPropertyType())
        {
        case STRING:
        case ID:
        case URI:
        case HTML:
            typeMatch = (firstValue instanceof String);
            break;
        case INTEGER:
            typeMatch = (firstValue instanceof BigInteger) || (firstValue instanceof Byte)
                    || (firstValue instanceof Short) || (firstValue instanceof Integer) || (firstValue instanceof Long);
            break;
        case DECIMAL:
            typeMatch = (firstValue instanceof BigDecimal);
            break;
        case BOOLEAN:
            typeMatch = (firstValue instanceof Boolean);
            break;
        case DATETIME:
            typeMatch = (firstValue instanceof GregorianCalendar);
            break;
        }

        if (!typeMatch)
        {
            throw new IllegalArgumentException("Value of property '" + propertyDefinition.getId()
                    + "' does not match property type!");
        }

        return values;
    }

    /**
     * Adds object type and aspect types to properties.
     */
    public static Map<String, ?> preparePropertiesForUpdate(Map<String, ?> properties, ObjectType type,
            Collection<ObjectType> aspectTypes)
    {
        Map<String, Object> newProperties = (properties == null ? null : new LinkedHashMap<String, Object>(properties));
        if (newProperties != null)
        {
            newProperties.put(PropertyIds.OBJECT_TYPE_ID, createObjectTypeIdValue(type, aspectTypes));
        }

        return newProperties;
    }

    public static String createObjectTypeIdValue(ObjectType type, Collection<ObjectType> aspectTypes)
    {
        StringBuilder sb = new StringBuilder(type.getId());

        for (ObjectType aspect : aspectTypes)
        {
            sb.append(',');
            sb.append(aspect.getId());
        }

        return sb.toString();
    }

    /**
     * Adds and removes aspects.
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String updateAspects(Session session, CmisObject object, ObjectType[] addAspectIds,
            ObjectType[] removeAspectIds, Map<String, ?> properties)
    {
        String objectId = object.getId();
        String repId = session.getRepositoryInfo().getId();
        Holder<String> objectIdHolder = new Holder<String>(objectId);
        Map<String, PropertyDefinition<?>> aspectPropertyDefinition = null;
        // create properties object
        Properties cmisProperties = new PropertiesImpl();
        
        CmisVersion cmisVersion = session.getRepositoryInfo().getCmisVersion();

        if(cmisVersion.equals(CmisVersion.CMIS_1_1))
        {
        	// cmis 1.1
        	Property<?> secondaryTypesProp = object.getProperty(PropertyIds.SECONDARY_OBJECT_TYPE_IDS);
        	List currentSecondaryTypes = (List)secondaryTypesProp.getValue();
        	Set<String> currentSecondaryTypesSet = new HashSet<String>(currentSecondaryTypes);

	        if (addAspectIds != null)
	        {
	        	Set<String> addAspects = new HashSet<String>();
	        	for(ObjectType type : addAspectIds)
	        	{
	        		addAspects.add(type.getId());
	        	}
	        	currentSecondaryTypesSet.addAll(addAspects);
	        }

	        if (removeAspectIds != null)
	        {
	        	Set<String> removeAspects = new HashSet<String>();
	        	for(ObjectType type : removeAspectIds)
	        	{
	        		removeAspects.add(type.getId());
	        	}
	        	currentSecondaryTypesSet.removeAll(removeAspects);
	        }
	        
	        List<String> secondaryTypesToSet = new ArrayList<String>();
	        for(String secondaryType : currentSecondaryTypesSet)
	        {
	        	secondaryTypesToSet.add(secondaryType);
	        }
	        
	        if(secondaryTypesToSet.isEmpty())
	        {
	        	// nothing to do
	        	return objectId;
	        }

	        Collection<PropertyData<?>> props = new ArrayList<PropertyData<?>>(1);
	        PropertyData<?> updatedSecondaryTypesProp = new PropertyIdImpl(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypesToSet);
	        props.add(updatedSecondaryTypesProp);
	        cmisProperties = new PropertiesImpl(props);
	        session.getBinding().getObjectService().updateProperties(repId, objectIdHolder, null, cmisProperties, null);
        }
        else if(cmisVersion.equals(CmisVersion.CMIS_1_0))
        {
        	// cmis 1.0
	        List<CmisExtensionElement> alfrescoExtensionList = new ArrayList<CmisExtensionElement>();
	
	        if (addAspectIds != null)
	        {
	            aspectPropertyDefinition = new HashMap<String, PropertyDefinition<?>>();
	            for (ObjectType type : addAspectIds)
	            {
	                if (type != null)
	                {
	                    alfrescoExtensionList.add(AlfrescoUtils.createAspectsToAddExtension(type));
	
	                    if (type.getPropertyDefinitions() != null)
	                    {
	                        aspectPropertyDefinition.putAll(type.getPropertyDefinitions());
	                    }
	                }
	            }
	        }
	
	        if (removeAspectIds != null)
	        {
	            for (ObjectType type : removeAspectIds)
	            {
	                if (type != null)
	                {
	                    alfrescoExtensionList.add(AlfrescoUtils.createAspectsToRemoveExtension(type));
	                }
	            }
	        }
	
	        if (alfrescoExtensionList.isEmpty())
	        {
	            return objectId;
	        }
	
	        // add property values
	        if (addAspectIds != null && properties != null && !properties.isEmpty())
	        {
	            List<CmisExtensionElement> aspectProperties = new ArrayList<CmisExtensionElement>(properties.size());
	
	            for (Map.Entry<String, ?> property : properties.entrySet())
	            {
	                if ((property == null) || (property.getKey() == null))
	                {
	                    continue;
	                }
	
	                String id = property.getKey();
	                Object value = property.getValue();
	
	                if (!aspectPropertyDefinition.containsKey(id))
	                {
	                    throw new IllegalArgumentException("Property '" + id + "' is not an aspect property!");
	                }
	
	                aspectProperties.add(createAspectPropertyExtension(aspectPropertyDefinition.get(id), value));
	            }
	
	            alfrescoExtensionList.add(AlfrescoUtils.createAspectPropertiesExtension(aspectProperties));
	        }

	        cmisProperties = new PropertiesImpl();
	        cmisProperties.setExtensions(Collections.singletonList(AlfrescoUtils
	                .createSetAspectsExtension(alfrescoExtensionList)));
        }

        session.getBinding().getObjectService().updateProperties(repId, objectIdHolder, null, cmisProperties, null);

        return objectIdHolder.getValue();
    }
    
	private static CmisExtensionElement getExtension(List<CmisExtensionElement> extensions, String namespace, String name)
    {
    	CmisExtensionElement ret = null;

    	if (extensions != null)
    	{
    	    for(CmisExtensionElement elem : extensions)
    	    {
    	    	/* MNT-10430 : browser binding does not deliver any namespace */
    	    	if((elem.getNamespace() == null || elem.getNamespace().equals(namespace)) && elem.getName().equals(name))
    	        {
    	            ret = elem;
    	        }
    	    }
    	}

    	return ret;
    }
	
    public static List<String> getMandatoryAspects(ExtensionsData object)
    {
		List<String> mandatoryAspects = null;

		List<CmisExtensionElement> extensions = object.getExtensions();
		CmisExtensionElement mandatoryAspectsExtension = getExtension(extensions, ALFRESCO_NAMESPACE, MANDATORY_ASPECTS);
		if(mandatoryAspectsExtension != null)
		{
			List<CmisExtensionElement> mandatoryAspectsExtensions = mandatoryAspectsExtension.getChildren();
			mandatoryAspects = new ArrayList<String>(mandatoryAspectsExtensions.size());
			for(CmisExtensionElement elem : mandatoryAspectsExtensions)
			{
				mandatoryAspects.add(elem.getValue());
			}
		}
		else
		{
			mandatoryAspects = Collections.emptyList();
		}

		return mandatoryAspects;
    }
}
