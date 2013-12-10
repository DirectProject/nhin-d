//Orginally from Kevin Provance
//http://www.vincenzo.net/isxkb/index.php?title=Is_VC_Runtime_Installed
[Code]
// API call to msi.dll to determine is a MSI installed packed has been installed.
// It does this by checking the state of the installed package.
// Anything other than a return value of INSTALLSTATE_DEFAULT (5) indicates a broken or
// no installation.
function MsiQueryProductState(szProduct: String): Integer; external 'MsiQueryProductStateA@msi.dll stdcall';

// String constants to pass to the routine.
const
   // These constants check for any install of the version specific package
   // regardless the service pack or security update.  There are specific
   // constants for x86, x64 and i64 installations.
   VC2005_ANY_x86        = 'vc2005x86';
   VC2005_ANY_x64        = 'vc2005x64';
   VC2005_ANY_i64        = 'vc2005i64';
   VC2008_ANY_x86        = 'vc2008x86';
   VC2008_ANY_x64        = 'vc2008x64';
   VC2008_ANY_i64        = 'vc2008i64';
   VC2010_ANY_x86        = 'vc2010x86';
   VC2010_ANY_x64        = 'vc2010x64';
   VC2010_ANY_i64        = 'vc2010i64';

   // All available VC 2005 (version 8.0) installations.
   VC2005_x86            = '{A49F249F-0C91-497F-86DF-B2585E8E76B7}';
   VC2005_x64            = '{6E8E85E8-CE4B-4FF5-91F7-04999C9FAE6A}';
   VC2005_i64            = '{03ED71EA-F531-4927-AABD-1C31BCE8E187}';
   VC2005_SP1_x86        = '{7299052B-02A4-4627-81F2-1818DA5D550D}';
   VC2005_SP1_x64        = '{071C9B48-7C32-4621-A0AC-3F809523288F}';
   VC2005_SP1_i64        = '{0F8FB34E-675E-42ED-850B-29D98C2ECE08}';
   VC2005_SP1_SECUP_x86  = '{837B34E3-7C30-493C-8F6A-2B0F04E2912C}';
   VC2005_SP1_SECUP_x64  = '{6CE5BAE9-D3CA-4B99-891A-1DC6C118A5FC}';
   VC2005_SP1_SECUP_i64  = '{85025851-A784-46D8-950D-05CB3CA43A13}';

   // All available VC 2008 (version 9.0) installations.
   VC2008_x86            = '{FF66E9F6-83E7-3A3E-AF14-8DE9A809A6A4}';
   VC2008_x64            = '{350AA351-21FA-3270-8B7A-835434E766AD}';
   VC2008_i64            = '{2B547B43-DB50-3139-9EBE-37D419E0F5FA}';
   VC2008_SP1_x86        = '{9A25302D-30C0-39D9-BD6F-21E6EC160475}';
   VC2008_SP1_x64        = '{8220EEFE-38CD-377E-8595-13398D740ACE}';
   VC2008_SP1_i64        = '{5827ECE1-AEB0-328E-B813-6FC68622C1F9}';
   VC2008_SP1_SECUP_x86  = '{1F1C2DFC-2D24-3E06-BCB8-725134ADF989}';
   VC2008_SP1_SECUP_x64  = '{4B6C7001-C7D6-3710-913E-5BC23FCE91E6}';
   VC2008_SP1_SECUP_i64  = '{977AD349-C2A8-39DD-9273-285C08987C7B}';

   // All available VC 2010 (version 10.0) installations.
   VC2010_x86            = '{196BB40D-1578-3D01-B289-BEFC77A11A1E}';
   VC2010_x64            = '{DA5E371C-6333-3D8A-93A4-6FD5B20BCC6E}';
   VC2010_i64            = '{C1A35166-4301-38E9-BA67-02823AD72A1B}';

   // Return values for MsiQueryProductState. I added INSTALLSTATE_MSIABSENT (-8) in the
   // event MSI is not installed on the target system.
   INSTALLSTATE_MSIABSENT     = -8;
   INSTALLSTATE_NOTUSED       = -7;
   INSTALLSTATE_BADCONFIG     = -6;
   INSTALLSTATE_INCOMPLETE    = -5;
   INSTALLSTATE_SOURCEABSENT  = -4;
   INSTALLSTATE_MOREDATA      = -3;
   INSTALLSTATE_INVALIDARG    = -2;
   INSTALLSTATE_UNKNOWN       = -1;
   INSTALLSTATE_BROKEN        = 0;
   INSTALLSTATE_ADVERTISED    = 1;
   INSTALLSTATE_ABSENT        = 2;
   INSTALLSTATE_LOCAL         = 3;
   INSTALLSTATE_SOURCE        = 4;
   INSTALLSTATE_DEFAULT       = 5; 

function VCRT_IsInstalled(sPackage: String): Integer;


begin
   // Check for the existence of msi.dll.  The poor man's way of checking for the
   // installation of the Windows Installation Engine.
   If FileExists(ExpandConstant('{sys}\msi.dll')) then begin

      // Default return value of -1, which indicated no installation.  This is only 
      // necessary for the VC*_ANY_* flags as individual return values are not captured.
      Result := -1;

      // Check for the package passed ia the sPackage parameter.
      case sPackage of
         VC2005_ANY_x86:
            begin
               If (MsiQueryProductState(VC2005_x86) = 5) or
               (MsiQueryProductState(VC2005_SP1_x86) = 5) or 
               (MsiQueryProductState(VC2005_SP1_SECUP_x86) = 5) then begin
                  Result := 5;
               end;
            end;
         VC2008_ANY_x86:
            begin
               If (MsiQueryProductState(VC2008_x86) = 5) or 
               (MsiQueryProductState(VC2008_SP1_x86) = 5) or 
               (MsiQueryProductState(VC2008_SP1_SECUP_x86) = 5) then begin
                  Result := 5;
               end;
            end;
         VC2010_ANY_x86:
            begin
               If MsiQueryProductState(VC2010_x86) = 5 then begin
                  Result := 5;
               end;
            end;
         VC2005_ANY_x64:
            begin
               If (MsiQueryProductState(VC2005_x64) = 5) or 
               (MsiQueryProductState(VC2005_SP1_x64) = 5) or 
               (MsiQueryProductState(VC2005_SP1_SECUP_x64) = 5) then begin
                  Result := 5;
               end;
            end;
         VC2008_ANY_x64:
            begin
               If (MsiQueryProductState(VC2008_x64) = 5) or 
              (MsiQueryProductState(VC2008_SP1_x64) = 5) or 
              (MsiQueryProductState(VC2008_SP1_SECUP_x64) = 5) then begin
                  Result := 5;
               end;
            end;
         VC2010_ANY_x64:
            begin
               If MsiQueryProductState(VC2010_x64) = 5 then begin
                  Result := 5;
               end;
            end;			
         VC2005_ANY_i64:
            begin
               If (MsiQueryProductState(VC2005_i64) = 5) or 
               (MsiQueryProductState(VC2005_SP1_i64) = 5) or 
               (MsiQueryProductState(VC2005_SP1_SECUP_i64) = 5) then begin
                  Result := 5;
               end;
            end;			
         VC2008_ANY_i64:
            begin
               If (MsiQueryProductState(VC2008_i64) = 5) or 
               (MsiQueryProductState(VC2008_SP1_i64) = 5) or 
               (MsiQueryProductState(VC2008_SP1_SECUP_i64) = 5) then begin
                  Result := 5;
               end;
            end;
         VC2010_ANY_i64:
            begin
               If MsiQueryProductState(VC2010_i64) = 5 then begin
                  Result := 5;
               end;
            end;

         // All speific versions are checked here.  The return value is passed back
         // as the functions return value.
         else 
            begin
               Result := MsiQueryProductState(sPackage);
            end;
      end;
   end else begin

      // MSI not installed.  Pass the specific error number for it.
      Result := INSTALLSTATE_MSIABSENT;
   end;
end;