using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NHINDirect.Tests
{
    public class TestingBase
    {

        #region protected void DumpError(string msg)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 7:10:34 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// 
        /// </summary>
        /// <param name="msg"></param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected void DumpError(string msg)
        {
            Console.WriteLine(new String('!', 50));
            Dump(msg);
        }
        #endregion

        #region protected void DumpSuccess(string msg)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 7:48:27 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// 
        /// </summary>
        /// <param name="msg"></param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected void DumpSuccess(string msg)
        {
            Console.WriteLine(new String('-', 50));
            Dump(msg);
        }
        #endregion

        #region protected void Dump(string msg)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// <Author>Chris Lomonico (mailto:chris.lomonico@surescripts.com)</Author>
        /// <DateCreated>08.31.2010 7:48:26 AM MST</DateCreated>
        /// <TFSItem></TFSItem>
        /// <ReleaseCandidate></ReleaseCandidate>
        /// <summary>
        /// 
        /// </summary>
        /// <param name="msg"></param>
        /// <remarks></remarks>
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        protected void Dump(string msg)
        {
            Console.WriteLine("{0} - {1}", DateTime.UtcNow.ToString("mm:ss:ff"), msg);
        }
        #endregion
    }


}
