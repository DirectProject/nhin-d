// eventHandlerTest.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <smtpEventHandler_i.h>

int _tmain(int argc, _TCHAR* argv[])
{
	::CoInitializeEx(NULL, COINIT_MULTITHREADED);

	HRESULT hr = E_FAIL;	
	CComPtr<IDispatch> sink;
	CLSID clsid;
	
	if (FAILED(hr = ::CLSIDFromProgID(L"NHINDirectGateway.MessageArrivalSink", &clsid)))
	{
		printf("%x", hr);
		return 0;
	}

	if (FAILED(hr = ::CoCreateInstance(clsid, NULL, CLSCTX_INPROC_SERVER, IID_IDispatch, reinterpret_cast<PVOID*>(&sink))))
	{
		printf("%x", hr);
	}		

	::CoUninitialize();
	return 0;
}

