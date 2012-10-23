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
#include "StdAfx.h"
#include "EventLog.h"

EventLog::EventLog()
	: m_hLog(NULL)
{
}

EventLog::~EventLog(void)
{
	if (m_hLog)
	{
		::DeregisterEventSource(m_hLog);
	}
}

HRESULT EventLog::Init(const WCHAR* sourceName)
{
	if (this->IsInitialized())
	{
		return S_OK;
	}
	
	HRESULT hr = E_FAIL;
	
	m_hLog = ::RegisterEventSource(NULL, sourceName);
	if (m_hLog == NULL)
	{
		hr = HRESULT_FROM_WIN32(::GetLastError());
		goto LBail;
	}
	
	hr = S_OK;
	
LBail:	
	return hr;
}

HRESULT EventLog::Write(const WORD type, const WCHAR *format, ...) const
{	
	HRESULT hr = E_FAIL;

	va_list argList;
	va_start(argList, format);
	hr = this->WriteV(type, format, argList);
	va_end(argList);    

	return hr;	
}

HRESULT EventLog::WriteError(const HRESULT hr, const WCHAR* message) const throw(...)
{
	return this->Write(EVENTLOG_ERROR_TYPE, L"ERROR=%x; %s", hr, message);
}

HRESULT EventLog::WriteInfo(const WCHAR* message) const throw(...)
{
	return this->Write(EVENTLOG_INFORMATION_TYPE, message);	
}

HRESULT EventLog::WriteV(const WORD type, const WCHAR *format, va_list argList) const
{	
	HRESULT hr = E_FAIL;
	CString message;

	if (!this->IsInitialized())
	{
		goto LBail;
	}

	message.AppendFormatV(format, argList);	
	
	LPCWSTR eventMsg = message;
	// ReportEvent(hEventLog, wType, wCategory, dwEventID, lpUserSid, wNumStrings, dwDataSize, lpStrings, lpRawaData)
	if (!::ReportEvent(m_hLog, type, 0, 1, NULL, 1, 0, &eventMsg, NULL))
	{
		hr = HRESULT_FROM_WIN32(::GetLastError());
		goto LBail;
	}
	
	hr = S_OK;
	
LBail:
	return hr;	
}

const WCHAR* DEFAULT_LOG = L"Health.Direct.MessageSink";

void Log(const WCHAR* message)
{
	EventLog log;
	if (SUCCEEDED(log.Init(DEFAULT_LOG)))
	{
		log.WriteInfo(message);
	}
}

void LogError(const HRESULT hr, const WCHAR* message)
{
	EventLog log;
	if (SUCCEEDED(log.Init(DEFAULT_LOG)))
	{
		log.WriteError(hr, message);
	}
}
