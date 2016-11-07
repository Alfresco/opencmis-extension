/*
 * Copyright 2014-2014 Alfresco Software Limited.
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
 */package org.alfresco.cmis.client.impl;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.runtime.ItemImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionImpl;
import org.apache.chemistry.opencmis.commons.data.ObjectData;

public class AlfrescoItemImpl extends ItemImpl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -564801526480912392L;

	public AlfrescoItemImpl(SessionImpl session, ObjectType objectType,
			ObjectData objectData, OperationContext context) 
	{
		super(session, objectType, objectData, context);
	}
}
