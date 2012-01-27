[code]

procedure CreateIISVirtualDir(virtualdir: String; path: String; description: String);
var
  IIS, WebSite, WebServer, WebRoot, VDir: Variant;
begin
  
  try
    IIS := CreateOleObject('IISNamespace');
  except
    RaiseException('Please install Microsoft IIS first.'#13#13'(Error ''' + GetExceptionMessage + ''' occurred)');
  end;
 
  WebSite := IIS.GetObject('IIsWebService', 'Localhost/w3svc');
  WebServer := WebSite.GetObject('IIsWebServer', '1');
  WebRoot := WebServer.GetObject('IIsWebVirtualDir', 'Root');

  { update virdir if it exists }
  try
    WebRoot.Delete('IIsWebVirtualDir', virtualdir);
    WebRoot.SetInfo();
  except
  end;

  VDir := WebRoot.Create('IIsWebVirtualDir', virtualdir);
  VDir.AccessRead := True;
  VDir.AccessScript := TRUE;
  VDir.AppFriendlyName := description;
  VDir.Path := path;
  try
    VDir.AppPoolId := 'DefaultAppPool';
  except
  end;

  VDir.AppCreate(True);
  VDir.SetInfo();
end;



procedure DeleteIISVirtualDir(virtualdir: String);
var
  IIS, WebSite, WebServer, WebRoot: Variant;
begin

  try
    IIS := CreateOleObject('IISNamespace');
  except
    RaiseException('Cannot find Microsoft IIS.'#13#13'(Error ''' + GetExceptionMessage + ''' occurred)');
  end;

  { Connect to the IIS server }
  WebSite := IIS.GetObject('IIsWebService', 'Localhost/w3svc');
  WebServer := WebSite.GetObject('IIsWebServer', '1');
  WebRoot := WebServer.GetObject('IIsWebVirtualDir', 'Root');
  try
    WebRoot.Delete('IIsWebVirtualDir', virtualdir);
    WebRoot.SetInfo();
  except
  end;
end;