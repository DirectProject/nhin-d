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
    
    if (SUCCEEDED(hr = pBag->Read(L"ConfigFilePath", &varVal,pErrorLog)))
    {
		hr = this->LoadFromConfigFile(varVal);
		goto LBail;
    }
	
	LogError(hr, L"No configuration associated with sink");	
	
LBail:
	if (SUCCEEDED(hr))
	{
		m_initialized = TRUE;
	}
	
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

HRESULT CMessageArrivalSink::LoadFromConfigFile(CComVariant &filePath) throw(...)
{
	HRESULT hr = E_FAIL;
	
	Log(L"Initializing sink using config file");		
	if (filePath.vt != VT_BSTR)
	{
		hr = E_INVALIDARG;
		LogError(hr, L"Invalid config file path");
		goto LBail;
	}
	
	if (FAILED(hr = m_managedHandler->InitFromConfigFile(filePath.bstrVal)))
	{
		LogError(hr, L"Could not Init managed handler");
		goto LBail;
	}
	
	hr = S_OK;
	
LBail:
	return hr;	
}

