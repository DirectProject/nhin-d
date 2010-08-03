// MessageArrivalSink.h : Declaration of the CMessageArrivalSink

#pragma once
#include "resource.h"       // main symbols

#include "smtpEventHandler_i.h"
#include <cdosyserr.h>
#include <cdosysstr.h>
#import <nhinSmtpAgent.tlb> no_namespace raw_interfaces_only

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
	CComPtr<IMessageArrivalEventHandler> m_managedHandler;
	BOOL m_initialized;
};

OBJECT_ENTRY_AUTO(__uuidof(MessageArrivalSink), CMessageArrivalSink)
