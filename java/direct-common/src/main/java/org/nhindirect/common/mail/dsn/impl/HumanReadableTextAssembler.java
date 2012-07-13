/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.common.mail.dsn.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * Assembles the readable portion by injecting a small set of configurable
 * pieces into a set html structure
 * 
 * @author BF3174
 * 
 */
public class HumanReadableTextAssembler
{
  private String bounceHeader;
  private String bounceFooter;
  private String recipientsTitle;
  private String errorMessageTitle;
  private String errorMessageDefault;

  /**
   * @param log
   * @param bounceHeader
   * @param bounceFooter
   * @param rejectedRecipientsTitle
   * @param errorMessageTitle
   * @param errorMessageDefault
   */
  public HumanReadableTextAssembler(String bounceHeader,
      String bounceFooter, String recipientsTitle,
      String errorMessageTitle, String errorMessageDefault) 
  {

    this.bounceHeader = bounceHeader;
    this.bounceFooter = bounceFooter;
    this.recipientsTitle = recipientsTitle;
    this.errorMessageTitle = errorMessageTitle;
    this.errorMessageDefault = errorMessageDefault;
  }

  public MimeBodyPart assemble(List<Address> rejectedRecipients)
      throws MessagingException 
  {
    final String errorMessage = null;
    return makeBodyPart(rejectedRecipients, errorMessage);
  }

  public MimeBodyPart assemble(List<Address> rejectedRecipients,
      Throwable throwable) throws MessagingException 
  {
    return makeBodyPart(rejectedRecipients, throwable.getMessage());
  }

  public MimeBodyPart assemble(List<Address> rejectedRecipients,
      String errorMessage) throws MessagingException 
  {
    return makeBodyPart(rejectedRecipients, errorMessage);
  }

  protected MimeBodyPart makeBodyPart(List<Address> rejectedRecipients,
      String errorMessage) throws MessagingException 
  {
    String assembleHtmlBody;
    try 
    {
      assembleHtmlBody = assembleHtmlBody(rejectedRecipients, errorMessage);
    } 
    catch (IOException e) 
    {
      throw new MessagingException("", e);
    }
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(assembleHtmlBody, "text/html");

    return mimeBodyPart;
  }

  /**
   * This method will assemble html bounce message
   * 
   * @return bounce html message
   * @throws IOException
   */
  protected String assembleHtmlBody(List<Address> rejectedRecipients,
      String errorMessage) throws IOException 
  {

    List<UnescapedText> lstToUnescape = new ArrayList<UnescapedText>();
    Element html = new Element("html");
    Element body = new Element("body");
    html.addContent(body);
    {
      Element p = new Element("p");
      body.addContent(p);
      UnescapedText text = new UnescapedText(bounceHeader);
      lstToUnescape.add(text);
      p.addContent(text);
    }
    {
      Element p = new Element("p");
      body.addContent(p);
      p.setText(this.recipientsTitle);
      Element ul = new Element("ul");
      p.addContent(ul);
      for (Address address : rejectedRecipients) 
      {
        Element li = new Element("li");
        ul.addContent(li);
        li.addContent(address.toString());
      }
    }
    {
      Element p = new Element("p");
      body.addContent(p);
      p.setText(this.errorMessageTitle);
      Element br = new Element("br");
      p.addContent(br);
      if ((errorMessage != null) && errorMessage.length() > 0) {
        p.addContent(errorMessage);
      } else {          
        UnescapedText text = new UnescapedText(this.errorMessageDefault);
        lstToUnescape.add(text);
        p.addContent(text);
      }
    }
    {
      Element p = new Element("p");
      body.addContent(p);
      UnescapedText text = new UnescapedText(bounceFooter);
      lstToUnescape.add(text);
      p.addContent(text);
    }
    Document document = new Document(html);
    String randomStr;
    {
      // Determine which string indicator can be used to indicate that a
      // string should not be escaped.
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      XMLOutputter outputter = new NoEscapeXMLOutputter(Format
          .getPrettyFormat());
      outputter.output(document, byteArrayOutputStream);
      String htmlString = new String(byteArrayOutputStream.toByteArray());
      randomStr = getUniqueString();
      while (htmlString.indexOf(randomStr) > -1) 
      {
        randomStr = getUniqueString();
      }
    }
	    String htmlString;
    {
      for (UnescapedText unescapedText : lstToUnescape) 
      {
        unescapedText.setUnescapedIndicator(randomStr);
      }
      XMLOutputter outputter = new UnescapedAwareXMLOutputter(Format
          .getPrettyFormat(), randomStr);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      outputter.output(document, byteArrayOutputStream);
      htmlString = new String(byteArrayOutputStream.toByteArray());
    }

    return htmlString;
  }

  // /CLOVER:OFF
  protected String getUniqueString() 
  {
    UUID randomUUID = UUID.randomUUID();
    return randomUUID.toString();
  }
  // /CLOVER:ON
}



