package org.alfresco.cmis.client.type;

import java.util.List;

import org.alfresco.cmis.client.impl.AlfrescoUtils;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.objecttype.RelationshipTypeImpl;
import org.apache.chemistry.opencmis.commons.definitions.RelationshipTypeDefinition;

/**
 * Represents an Alfresco relationship type, including mandatory aspects.
 *
 */
public class AlfrescoRelationshipType extends RelationshipTypeImpl implements AlfrescoType
{
	private static final long serialVersionUID = -7236093421190237728L;

    public AlfrescoRelationshipType(Session session, RelationshipTypeDefinition typeDefinition)
    {
		super(session, typeDefinition);
		setExtensions(typeDefinition.getExtensions());
	}

	public List<String> getMandatoryAspects()
	{
		return AlfrescoUtils.getMandatoryAspects(this);
	}

}
