/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Arien Malec     arien.malec@nhindirect.org
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Health.Direct.Xdm
{
    public static class XDMStandard
    {
        public static string MainDirectory = "IHE_XDM";

        public static string ReadmeFilename = "README.TXT";

        public static string MetadataFilename = "METADATA.XML";

        public static string IndexHtmFile = "INDEX.HTM";

        public static string DefaultSubmissionSet = "Default";

        public static string DocPrefix = "DOC";

        public static string DefaultMetadataFilePath = String.Format("{0}/{1}/{2}", XDMStandard.MainDirectory, XDMStandard.DefaultSubmissionSet, XDMStandard.MetadataFilename);

        public static string ReadmeFileString = @"This XDM Zip file was created by an automated process.

To view the included files, use a zip file extraction tool; all health related content is in the IHE_XDM directory.

You may also extract the entire package to a directory and view the included INDEX.HTM file in a web browser
(e.g.,  Internet Explorer, Firefox, Chrome, Safari), either by clicking on the file, or opening it using the
Open File command in the File menu of your browser.

(this file created by the Direct reference information library)";
    }
}
