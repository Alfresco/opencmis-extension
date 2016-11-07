package org.alfresco.cmis.client.type;

import java.util.List;

import org.alfresco.cmis.client.impl.AlfrescoUtils;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.objecttype.FolderTypeImpl;
import org.apache.chemistry.opencmis.commons.definitions.FolderTypeDefinition;

/**
 * Represents an Alfresco folder type, including mandatory aspects.
 *
 */
public class AlfrescoFolderType extends FolderTypeImpl implements AlfrescoType
{
	private static final long serialVersionUID = -7236093421190237728L;

    public AlfrescoFolderType(Session session, FolderTypeDefinition typeDefinition)
    {
		super(session, typeDefinition);
		setExtensions(typeDefinition.getExtensions());
	}

	public List<String> getMandatoryAspects()
	{
		return AlfrescoUtils.getMandatoryAspects(this);
	}
}
