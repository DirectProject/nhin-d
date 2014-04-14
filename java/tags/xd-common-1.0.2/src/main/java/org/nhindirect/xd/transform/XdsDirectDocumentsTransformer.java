package org.nhindirect.xd.transform;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.transform.exception.TransformationException;

public interface XdsDirectDocumentsTransformer
{
    public DirectDocuments transform(ProvideAndRegisterDocumentSetRequestType provideAndRegisterDocumentSetRequestType)
            throws TransformationException;
}
