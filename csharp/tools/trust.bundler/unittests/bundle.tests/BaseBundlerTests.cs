using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace Health.Direct.Trust.Tests
{
    public  class BaseBundlerTests
    {
        public BaseBundlerTests()
        {
            IEnumerable<FileInfo> files = GetFiles(Directory.GetCurrentDirectory(), ".p7b", ".p7m");
            foreach (FileInfo fileInfo in files)
            {
                File.Delete(fileInfo.FullName); 
            }
        }

        public static IEnumerable<FileInfo> GetFiles(string path, params string[] extensions)
        {
            List<FileInfo> list = new List<FileInfo>();
            foreach (string ext in extensions)
                list.AddRange(new DirectoryInfo(path).GetFiles("*" + ext).Where(p =>
                                                                                p.Extension.Equals(ext, StringComparison.CurrentCultureIgnoreCase))
                                                     .ToArray());
            return list;
        }
    }
}
