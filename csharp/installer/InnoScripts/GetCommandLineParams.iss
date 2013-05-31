//Get the command line param value
//Get the command line param in the format of key/value

function GetCommandlineParamValue (inParam: String):String;
var
  i : Integer;                     
begin  
  
  i :=0;
  Result := '';
  
  // Loop through cmd args. ParamCount and PramStr are part of the pascal language. 
  while  (i < ParamCount)  do
  begin
    // match on key part
    if ( (ParamStr(i) = inParam) and
         ( (i+1) < ParamCount )) then
    begin
      //set Result to value part
      Result := ParamStr(i+1);   
      exit;            
    end; 
    i := i + 1;
  end;
end;

function CommandlineParamExists (inParam: String):Boolean;
var
  i : Integer;
  key: String;                     
begin  
  
  i :=0;
  
  
  // Loop through cmd args. ParamCount and PramStr are part of the pascal language. 
  while  (i < ParamCount + 1)  do
  begin
    // match on key part
    key := ParamStr(i);       
    if ( CompareText(key, inParam) = 0 ) then
    begin
      Result := true;   
      exit;            
    end;
    i := i + 1;
  end;
  Result := false;
end;