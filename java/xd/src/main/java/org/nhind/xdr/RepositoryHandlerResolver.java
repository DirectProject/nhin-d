/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nhind.xdr;


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
  
public List<Handler> getHandlerChain(PortInfo portInfo) {
        List<Handler> handlerList = new ArrayList<Handler>();
        handlerList.add(new RepositorySOAPHandler());
        return handlerList;
    }
}
