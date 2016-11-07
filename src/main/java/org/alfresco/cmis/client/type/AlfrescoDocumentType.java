package org.alfresco.cmis.client.type;

import java.util.List;

import org.alfresco.cmis.client.impl.AlfrescoUtils;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.objecttype.DocumentTypeImpl;
import org.apache.chemistry.opencmis.commons.definitions.DocumentTypeDefinition;

/**
 * Represents an Alfresco document type, including mandatory aspects.
 *
 */
public class AlfrescoDocumentType extends DocumentTypeImpl implements AlfrescoType
{
	private static final long serialVersionUID = -7236093421190237728L;

    public AlfrescoDocumentType(Session session, DocumentTypeDefinition typeDefinition)
    {
		super(session, typeDefinition);
		setExtensions(typeDefinition.getExtensions());
	}

	public List<String> getMandatoryAspects()
	{
		return AlfrescoUtils.getMandatoryAspects(this);
	}
}
