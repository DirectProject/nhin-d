package org.nhindirect.xd.transform.impl;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;

import org.nhindirect.xd.common.DirectDocument2;
import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.exception.MetadataException;
import org.nhindirect.xd.transform.XdsDirectDocumentsTransformer;
import org.nhindirect.xd.transform.exception.TransformationException;

public class DefaultXdsDirectDocumentsTransformer implements XdsDirectDocumentsTransformer
{

    @Override
    public DirectDocuments transform(ProvideAndRegisterDocumentSetRequestType provideAndRegisterDocumentSetRequestType)
            throws TransformationException
    {
        DirectDocuments documents = new DirectDocuments();

        try
        {
            documents.setValues(provideAndRegisterDocumentSetRequestType.getSubmitObjectsRequest());
        }
        catch (MetadataException e)
        {
            throw new TransformationException("Unable to complete transformation due to metadata error", e);
        }

        for (ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document document : provideAndRegisterDocumentSetRequestType.getDocument())
        {
            byte[] data = null;

            try
            {
                DataHandler dataHandler = document.getValue();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                dataHandler.writeTo(outputStream);
                data = outputStream.toByteArray();
            }
            catch (IOException e)
            {
                throw new TransformationException("Unable to complete transformation due to document IO error", e);
            }

            DirectDocument2 doc = documents.getDocumentByUniqueId(document.getId());

            if (doc != null)
            {
                doc.setData(data);
            }
            else
            {
                documents.getDocumentById(document.getId()).setData(data);
            }
        }
        
        return documents;
    }

}
