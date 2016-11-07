package org.alfresco.cmis.client.type;

import java.util.List;

import org.alfresco.cmis.client.impl.AlfrescoUtils;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.objecttype.PolicyTypeImpl;
import org.apache.chemistry.opencmis.commons.definitions.PolicyTypeDefinition;

/**
 * Represents an Alfresco policy type, including mandatory aspects.
 *
 */
public class AlfrescoPolicyType extends PolicyTypeImpl implements AlfrescoType
{
	private static final long serialVersionUID = -7236093421190237728L;

    public AlfrescoPolicyType(Session session, PolicyTypeDefinition typeDefinition)
    {
		super(session, typeDefinition);
		setExtensions(typeDefinition.getExtensions());
	}

	public List<String> getMandatoryAspects()
	{
		return AlfrescoUtils.getMandatoryAspects(this);
	}

}
