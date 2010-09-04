/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nhind.mail.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

/**
 *
 * @author Vincent Lewis
 */
public class RepositoryHandlerResolver implements HandlerResolver{

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.ws.handler.HandlerResolver#getHandlerChain(javax.xml.ws.handler.PortInfo)
     */
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerList = new ArrayList<Handler>();
        handlerList.add(new RepositorySOAPHandler());
        return handlerList;
    }
}
