package org.alfresco.cmis.client.type;

import java.util.List;

import org.alfresco.cmis.client.impl.AlfrescoUtils;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.objecttype.SecondaryTypeImpl;
import org.apache.chemistry.opencmis.commons.definitions.SecondaryTypeDefinition;

public class AlfrescoSecondaryType extends SecondaryTypeImpl implements AlfrescoType
{
	private static final long serialVersionUID = 3985314017717890607L;

	public AlfrescoSecondaryType(Session session, SecondaryTypeDefinition typeDefinition)
    {
		super(session, typeDefinition);
		setExtensions(typeDefinition.getExtensions());
	}

	public List<String> getMandatoryAspects()
	{
		return AlfrescoUtils.getMandatoryAspects(this);
	}

}
