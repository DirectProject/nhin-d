/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
// MessageArrivalSink.h : Declaration of the CMessageArrivalSink

#pragma once
#include "resource.h"       // main symbols

#include "smtpEventHandler_i.h"
#include <cdosyserr.h>
#include <cdosysstr.h>
#import <Health.Direct.SmtpAgent.tlb> no_namespace raw_interfaces_only

#include "EventLog.h"

// CMessageArrivalSink

class ATL_NO_VTABLE CMessageArrivalSink :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CMessageArrivalSink, &CLSID_MessageArrivalSink>,
	public IDispatchImpl<ISMTPOnArrival, &IID_ISMTPOnArrival, &LIBID_smtpEventHandlerLib>,
	public IEventIsCacheable,
	public IMessageArrivalSink,
	public IPersistPropertyBag
{
public:
	CMessageArrivalSink()
	{
		m_pUnkMarshaler = NULL;
		m_initialized = FALSE;
	}

DECLARE_REGISTRY_RESOURCEID(IDR_MESSAGEARRIVALSINK)

DECLARE_NOT_AGGREGATABLE(CMessageArrivalSink)

BEGIN_COM_MAP(CMessageArrivalSink)
	COM_INTERFACE_ENTRY(IMessageArrivalSink)
	COM_INTERFACE_ENTRY(ISMTPOnArrival)
	COM_INTERFACE_ENTRY(IEventIsCacheable)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(IPersistPropertyBag)
	COM_INTERFACE_ENTRY_AGGREGATE(IID_IMarshal, m_pUnkMarshaler.p)
END_COM_MAP()


	DECLARE_PROTECT_FINAL_CONSTRUCT()
	DECLARE_GET_CONTROLLING_UNKNOWN()

	HRESULT FinalConstruct();

	void FinalRelease()
	{
		m_pUnkMarshaler.Release();
	}

	CComPtr<IUnknown> m_pUnkMarshaler;

public:
   STDMETHOD(OnArrival)(IMessage *pMsg, CdoEventStatus *pEvStat);
   STDMETHOD(IsCacheable)() { return S_OK;} // can cache the object

    //
    // IPersistPropertyBag : IPersist
    //
    STDMETHOD(GetClassID)(CLSID *pClassID);
    STDMETHOD(InitNew)(void);
    STDMETHOD(Load)(IPropertyBag* pBag,IErrorLog *pErrorLog);
    STDMETHOD(Save)(IPropertyBag *pPropBag, BOOL fClearDirty, BOOL fSaveAllProperties);

private:
	HRESULT LoadFromConfigFile(CComVariant& filePath) throw(...);
	
private:
	CComPtr<IMessageArrivalEventHandler> m_managedHandler;
	BOOL m_initialized;
};

OBJECT_ENTRY_AUTO(__uuidof(MessageArrivalSink), CMessageArrivalSink)
