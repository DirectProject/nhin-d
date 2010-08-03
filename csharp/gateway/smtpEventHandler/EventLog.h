#pragma once

class EventLog
{
public:
	EventLog();
	~EventLog(void);

	HRESULT Init(const WCHAR* sourceName);
	
    HRESULT Write(const WORD type, const WCHAR* format, ...) const throw(...);
	HRESULT WriteError(const HRESULT hr, const WCHAR* message) const throw(...);
    HRESULT WriteInfo(const WCHAR* message) const throw(...);
    
private:
    HRESULT WriteV(const WORD type, const WCHAR* format, va_list argList) const throw(...);
	
private:
	bool IsInitialized(void) const
	{
		return (m_hLog != NULL);
	}	
	
private:
	HANDLE m_hLog;
};

void Log(const WCHAR* message);
void LogError(HRESULT hr, const WCHAR* message);
