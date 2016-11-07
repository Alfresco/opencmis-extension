package org.alfresco.cmis.client.type;

import java.util.List;

import org.alfresco.cmis.client.impl.AlfrescoUtils;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.objecttype.ItemTypeImpl;
import org.apache.chemistry.opencmis.commons.definitions.ItemTypeDefinition;

public class AlfrescoItemType extends ItemTypeImpl implements AlfrescoType
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8384158771339321883L;

	public AlfrescoItemType(Session session, ItemTypeDefinition typeDefinition) {
		super(session, typeDefinition);
	}

	public List<String> getMandatoryAspects() {
		return AlfrescoUtils.getMandatoryAspects(this);
	}



}
