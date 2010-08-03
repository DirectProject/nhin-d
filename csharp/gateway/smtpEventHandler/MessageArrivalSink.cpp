// MessageArrivalSink.cpp : Implementation of CMessageArrivalSink

#include "stdafx.h"
#include "MessageArrivalSink.h"
#include "dllmain.h"

// CMessageArrivalSink
HRESULT CMessageArrivalSink::FinalConstruct(void)
{
	HRESULT hr = E_FAIL;
	
	if (FAILED(hr = m_managedHandler.CoCreateInstance(__uuidof(MessageArrivalEventHandler), NULL, CLSCTX_INPROC_SERVER)))
	{
		LogError(hr, L"Could not create MessageArrivalEventHandler");
		goto LBail;
	}
	
	if (FAILED(hr = CoCreateFreeThreadedMarshaler(GetControllingUnknown(), &m_pUnkMarshaler.p)))
	{
		LogError(hr, L"FreeThreadedMarshaler");
		goto LBail;
	}
		
	hr = S_OK;
	
LBail:
	return hr;
}

STDMETHODIMP CMessageArrivalSink::GetClassID(CLSID* pClassID)
{
	return S_OK;
}

STDMETHODIMP CMessageArrivalSink::InitNew()
{
	return S_OK;
}

STDMETHODIMP CMessageArrivalSink::Load(IPropertyBag* pBag, IErrorLog* pErrorLog)
{
	if (!pBag)
	{
		return E_POINTER;
	}
	
	HRESULT hr = E_FAIL;
	CComVariant varVal;
    
    if (FAILED(hr = pBag->Read(L"ConfigFilePath", &varVal,pErrorLog)))
    {
		LogError(hr, L"Could not read config file path");
		goto LBail;
    }
	
	if (varVal.vt != VT_BSTR)
	{
		hr = E_INVALIDARG;
		LogError(hr, L"Invalid config file path");
		goto LBail;
	}
	
	if (FAILED(hr = m_managedHandler->Init(varVal.bstrVal)))
	{
		LogError(hr, L"Could not Init managed handler");
		goto LBail;
	}
	
	m_initialized = TRUE;
	
	hr = S_OK;
LBail:
	return hr;	
}

STDMETHODIMP CMessageArrivalSink::Save(IPropertyBag* pPropBag, BOOL fClearDirty, BOOL fSaveAllProperties)
{
	return S_OK;
}

STDMETHODIMP CMessageArrivalSink::OnArrival(IMessage *pMsg, CdoEventStatus *pEvStat)
{
	if (!pMsg || !pEvStat)
	{
		return E_POINTER;
	}
	
	HRESULT hr = E_FAIL;

	if (!m_initialized)	
	{
		hr = E_UNEXPECTED;
		LogError(hr, L"Not initialized");
		goto LBail;
	}

	*pEvStat = cdoSkipRemainingSinks;	
	if (FAILED(hr = m_managedHandler->ProcessCDOMessage(pMsg)))
	{
		LogError(hr, L"ProcessCDOMessage");
		goto LBail;
	}
	
	hr = S_OK;
	*pEvStat = cdoRunNextSink;
	
LBail:	
	return hr;
}
