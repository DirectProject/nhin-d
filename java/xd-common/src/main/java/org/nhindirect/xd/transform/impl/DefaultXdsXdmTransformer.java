/* 
 * Copyright (c) 2010, NHIN Direct Project
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution.  
 * 3. Neither the name of the the NHIN Direct Project (nhindirect.org)
 *    nor the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.xd.transform.impl;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.activation.DataHandler;

import org.nhindirect.xd.common.DirectDocuments;
import org.nhindirect.xd.common.XdmPackage;
import org.nhindirect.xd.common.exception.MetadataException;
import org.nhindirect.xd.transform.XdsXdmTransformer;
import org.nhindirect.xd.transform.exception.TransformationException;

/**
 * Interface for handling the transformation of a
 * ProvideAndRegisterDocumentSetRequestType object to an XDM File.
 * 
 * @author beau
 */
public class DefaultXdsXdmTransformer implements XdsXdmTransformer
{

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.transform.XdsXdmTransformer#transform(ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType)
     */
    @Override
    public File transform(ProvideAndRegisterDocumentSetRequestType provideAndRegisterDocumentSetRequestType)
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

            documents.getDocumentByUniqueId(document.getId()).setData(new String(data));
        }

        XdmPackage xdmPackage = new XdmPackage();
        xdmPackage.setDocuments(documents);

        return xdmPackage.toFile();
    }

}
