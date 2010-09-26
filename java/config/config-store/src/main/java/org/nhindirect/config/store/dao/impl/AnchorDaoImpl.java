/* 
 Copyright (c) 2010, NHIN Direct Project
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
 in the documentation and/or other materials provided with the distribution.  
 3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
 BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nhindirect.config.store.dao.impl;

import java.util.List;

import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.EntityStatus;
import org.nhindirect.config.store.dao.AnchorDao;

/**
 * Implementing class for Anchor DAO methods.
 * 
 * @author ppyette
 */
public class AnchorDaoImpl implements AnchorDao {

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#load(java.lang.String)
     */
    public Anchor load(String owner) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#listAll()
     */
    public List<Anchor> listAll() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#list(java.util.List)
     */
    public List<Anchor> list(List<String> owners) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#save(org.nhindirect.config.store.Anchor)
     */
    public void save(Anchor anchor) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#save(java.util.List)
     */
    public void save(List<Anchor> anchorList) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#setStatus(java.util.List, org.nhindirect.config.store.EntityStatus)
     */
    public void setStatus(List<Long> anchorIDs, EntityStatus status) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#setStatus(java.lang.String, org.nhindirect.config.store.EntityStatus)
     */
    public void setStatus(String owner, EntityStatus status) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#delete(java.util.List)
     */
    public void delete(List<Long> idList) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nhindirect.config.store.dao.AnchorDao#delete(java.lang.String)
     */
    public void delete(String owner) {
        // TODO Auto-generated method stub

    }

}
