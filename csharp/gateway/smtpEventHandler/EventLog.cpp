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
	if (!::ReportEvent(m_hLog, type, 0, 0, NULL, 1, 0, &eventMsg, NULL))
	{
		hr = HRESULT_FROM_WIN32(::GetLastError());
		goto LBail;
	}
	
	hr = S_OK;
	
LBail:
	return hr;	
}

const WCHAR* DEFAULT_LOG = L"nhinMessageSink";

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
