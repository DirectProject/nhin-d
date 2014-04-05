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

package org.nhindirect.config.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.nhindirect.config.service.impl.CertificateGetOptions;
import org.nhindirect.config.service.impl.ConfigurationServiceImpl;
import org.nhindirect.config.store.Address;
import org.nhindirect.config.store.Anchor;
import org.nhindirect.config.store.Certificate;
import org.nhindirect.config.store.Domain;
import org.nhindirect.config.store.EntityStatus;

/**
 * Unit tests for the ConfigurationService class.
 * 
 * @author beau
 */
public class ConfigurationServiceTest extends MockObjectTestCase
{

    private Mockery context = new JUnit3Mockery();

    /**
     * Default constructor.
     * 
     * @param testName
     *            The test name.
     */
    public ConfigurationServiceTest(String testName)
    {
        super(testName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test the addAddress method.
     */
    public void testAddAddress() throws Exception
    {
        final AddressService addressService = context.mock(AddressService.class);

        final Collection<Address> collection = Arrays.asList(new Address(new Domain("domain"), "address"));

        context.checking(new Expectations()
        {
            {
                oneOf(addressService).addAddress(collection);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAddressSvc(addressService);

        try
        {
            service.addAddress(collection);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the updateAddress method.
     */
    public void testUpdateAddress() throws Exception
    {
        final AddressService addressService = context.mock(AddressService.class);

        final Address address = new Address(new Domain("domain"), "address");

        context.checking(new Expectations()
        {
            {
                oneOf(addressService).updateAddress(address);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAddressSvc(addressService);

        try
        {
            service.updateAddress(address);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAddressCount method.
     */
    public void testGetAddressCount() throws Exception
    {
        final AddressService addressService = context.mock(AddressService.class);

        context.checking(new Expectations()
        {
            {
                oneOf(addressService).getAddressCount();
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAddressSvc(addressService);

        try
        {
            service.getAddressCount();
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAddresses method.
     */
    public void testGetAddresses() throws Exception
    {
        final AddressService addressService = context.mock(AddressService.class);

        final Collection<String> collection = Arrays.asList("address1", "address2");
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(addressService).getAddress(collection, status);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAddressSvc(addressService);

        try
        {
            service.getAddress(collection, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeAddress method.
     */
    public void testRemoveAddress() throws Exception
    {
        final AddressService addressService = context.mock(AddressService.class);

        final String addressName = "address";

        context.checking(new Expectations()
        {
            {
                oneOf(addressService).removeAddress(addressName);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAddressSvc(addressService);

        try
        {
            service.removeAddress(addressName);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listAddress method.
     */
    public void testListAddress() throws Exception
    {
        final AddressService addressService = context.mock(AddressService.class);

        final String lastAddressName = "address";
        final int maxResults = 1;

        context.checking(new Expectations()
        {
            {
                oneOf(addressService).listAddresss(lastAddressName, maxResults);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAddressSvc(addressService);

        try
        {
            service.listAddresss(lastAddressName, maxResults);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the addDomain method.
     */
    public void testAddDomain() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final Domain domain = new Domain("domain");

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).addDomain(domain);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.addDomain(domain);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the updateDomain method.
     */
    public void testUpdateDomain() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final Domain domain = new Domain("domain");

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).updateDomain(domain);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.updateDomain(domain);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getDomainCount method.
     */
    public void testGetDomainCount() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).getDomainCount();
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.getDomainCount();
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getDomains method.
     */
    public void testGetDomains() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final Collection<String> collection = Arrays.asList("domain1", "domain2");
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).getDomains(collection, status);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.getDomains(collection, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeDomain method.
     */
    public void testRemoveDomain() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final String domain = "domain";

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).removeDomain(domain);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.removeDomain(domain);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the removeDomainById method.
     */
    public void testRemoveDomainById() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final long domainId = 1;

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).removeDomainById(domainId);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.removeDomainById(domainId);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listDomains method.
     */
    public void testListDomains() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final String lastDomainName = "domain";
        final int maxResults = 2;

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).listDomains(lastDomainName, maxResults);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.listDomains(lastDomainName, maxResults);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getDomain method.
     */
    public void testGetDomain() throws Exception
    {
        final DomainService domainService = context.mock(DomainService.class);

        final long domainId = 4L;

        context.checking(new Expectations()
        {
            {
                oneOf(domainService).getDomain(domainId);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setDomainSvc(domainService);

        try
        {
            service.getDomain(domainId);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the addCertificate method.
     */
    public void testAddCertificate() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final Collection<Certificate> collection = Arrays.asList(new Certificate());

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).addCertificates(collection);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.addCertificates(collection);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the setCertificateStatus method.
     */
    public void testSetCertficateStatus() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final Collection<Long> collection = Arrays.asList(2L, 3L);
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).setCertificateStatus(collection, status);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.setCertificateStatus(collection, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the setCertificateStatusForOwner method.
     */
    public void testSetCertificateStatusForOwner() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final String owner = "owner";
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).setCertificateStatusForOwner(owner, status);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.setCertificateStatusForOwner(owner, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeCertificates method.
     */
    public void testRemoveCertificates() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final Collection<Long> collection = Arrays.asList(3L, 4L);

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).removeCertificates(collection);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.removeCertificates(collection);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeCertificatesForOwner method.
     */
    public void testRemoveCertificatesForOwner() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final String owner = "owner";

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).removeCertificatesForOwner(owner);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.removeCertificatesForOwner(owner);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the contains method.
     */
    public void testContains() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final Certificate certificate = new Certificate();

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).contains(certificate);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.contains(certificate);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the addAnchors method.
     */
    public void testAddAnchors() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final Collection<Anchor> collection = Arrays.asList(new Anchor());

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).addAnchors(collection);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.addAnchors(collection);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the setAnchorStatusForOwner method.
     */
    public void testSetAnchorStatusForOwner() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final String owner = "owner";
        final EntityStatus status = EntityStatus.ENABLED;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).setAnchorStatusForOwner(owner, status);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.setAnchorStatusForOwner(owner, status);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeAnchors method.
     */
    public void testRemoveAnchors() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final Collection<Long> collection = Arrays.asList(3L, 4L);

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).removeAnchors(collection);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.removeAnchors(collection);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the removeAnchorsForOwner method.
     */
    public void testRemoveAnchorsForOwner() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final String owner = "owner";

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).removeAnchorsForOwner(owner);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.removeAnchorsForOwner(owner);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getCertificate method.
     */
    public void testGetCertificate() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final String owner = "owner";
        final String thumbprint = "thumbprint";
        final CertificateGetOptions certificateGetOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).getCertificate(owner, thumbprint, certificateGetOptions);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.getCertificate(owner, thumbprint, certificateGetOptions);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getCertificates method.
     */
    public void testGetCertificates() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final Collection<Long> certIds = Arrays.asList(4L, 5L);
        final CertificateGetOptions certificateGetOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).getCertificates(certIds, certificateGetOptions);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.getCertificates(certIds, certificateGetOptions);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getCertificatesForOwner method.
     */
    public void testGetCertificatesForOwner() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final String owner = "owner";
        final CertificateGetOptions certificateGetOptions = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).getCertificatesForOwner(owner, certificateGetOptions);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.getCertificatesForOwner(owner, certificateGetOptions);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listCertificates method.
     */
    public void testListCertificates() throws Exception
    {
        final CertificateService certificateService = context.mock(CertificateService.class);

        final long lastCertificateId = 3L;
        final int maxResults = 3;
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(certificateService).listCertificates(lastCertificateId, maxResults, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setCertSvc(certificateService);

        try
        {
            service.listCertificates(lastCertificateId, maxResults, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAnchor method.
     */
    public void testGetAnchor() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final String owner = "owner";
        final String thumbprint = "thumbprint";
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).getAnchor(owner, thumbprint, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.getAnchor(owner, thumbprint, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAnchors method.
     */
    public void testGetAnchors() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final Collection<Long> anchorIds = Arrays.asList(3L, 4L);
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).getAnchors(anchorIds, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.getAnchors(anchorIds, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getAnchorsForOwner method.
     */
    public void testGetAnchorsForOwner() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final String owner = "owner";
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).getAnchorsForOwner(owner, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.getAnchorsForOwner(owner, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getIncomingAnchors method.
     */
    public void testGetIncomingAnchors() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final String owner = "owner";
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).getIncomingAnchors(owner, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.getIncomingAnchors(owner, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the getOutgoingAnchors method.
     */
    public void testGetOutgoingAnchors() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final String owner = "owner";
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).getOutgoingAnchors(owner, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.getOutgoingAnchors(owner, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }

    /**
     * Test the listAnchors method.
     */
    public void testListAnchors() throws Exception
    {
        final AnchorService anchorService = context.mock(AnchorService.class);

        final long lastAnchorId = 3L;
        final int maxResults = 3;
        final CertificateGetOptions options = CertificateGetOptions.DEFAULT;

        context.checking(new Expectations()
        {
            {
                oneOf(anchorService).listAnchors(lastAnchorId, maxResults, options);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setAnchorSvc(anchorService);

        try
        {
            service.listAnchors(lastAnchorId, maxResults, options);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the addSetting method.
     */
    public void testAddSetting() throws Exception
    {
        final SettingService settingService = context.mock(SettingService.class);

        final String name = UUID.randomUUID().toString();
        final String value = UUID.randomUUID().toString();

        context.checking(new Expectations()
        {
            {
                oneOf(settingService).addSetting(name, value);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setSettingSvc(settingService);

        try
        {
            service.addSetting(name, value);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the getAllSettings method.
     */
    public void testGetAllSettings() throws Exception
    {
        final SettingService settingService = context.mock(SettingService.class);

        context.checking(new Expectations()
        {
            {
                oneOf(settingService).getAllSettings();
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setSettingSvc(settingService);

        try
        {
            service.getAllSettings();
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the getSettingByName method.
     */
    public void testGetSettingByName() throws Exception
    {
        final SettingService settingService = context.mock(SettingService.class);

        final String name = UUID.randomUUID().toString();

        context.checking(new Expectations()
        {
            {
                oneOf(settingService).getSettingByName(name);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setSettingSvc(settingService);

        try
        {
            service.getSettingByName(name);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the getSettingsByNames method.
     */
    public void testGetSettingsByNames() throws Exception
    {
        final SettingService settingService = context.mock(SettingService.class);

        final Collection<String> names = Arrays.asList(UUID.randomUUID().toString());

        context.checking(new Expectations()
        {
            {
                oneOf(settingService).getSettingsByNames(names);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setSettingSvc(settingService);

        try
        {
            service.getSettingsByNames(names);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the updateSetting method.
     */
    public void testUpdateSetting() throws Exception
    {
        final SettingService settingService = context.mock(SettingService.class);

        final String name = UUID.randomUUID().toString();
        final String value = UUID.randomUUID().toString();

        context.checking(new Expectations()
        {
            {
                oneOf(settingService).updateSetting(name, value);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setSettingSvc(settingService);

        try
        {
            service.updateSetting(name, value);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
    
    /**
     * Test the deleteSetting method.
     */
    public void testDeleteSetting() throws Exception
    {
        final SettingService settingService = context.mock(SettingService.class);

        final Collection<String> names = Arrays.asList(UUID.randomUUID().toString());

        context.checking(new Expectations()
        {
            {
                oneOf(settingService).deleteSetting(names);
            }
        });

        ConfigurationServiceImpl service = new ConfigurationServiceImpl();
        service.setSettingSvc(settingService);

        try
        {
            service.deleteSetting(names);
        }
        catch (Exception e)
        {
            fail("Exception thrown");
        }
    }
}
