// dllmain.h : Declaration of module class.

class CsmtpEventHandlerModule : public CAtlDllModuleT< CsmtpEventHandlerModule >
{
public :
	DECLARE_LIBID(LIBID_smtpEventHandlerLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_SMTPEVENTHANDLER, "{FF148F7E-C41D-470B-BCB9-75AE74F94C29}")	
private:
};

extern class CsmtpEventHandlerModule _AtlModule;
