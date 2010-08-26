using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;
using System.Web.UI.WebControls;

namespace AdminUI
{
    public static class WebHelper
    {
       public static  int GetDataKeyFromGridView(object sender, object commandArgument, string dataKeyName)
        {
            int index = -1;
            Int32.TryParse(commandArgument.ToString(), out index);
            if (index >= 0)
            {
                try
                {
                    var dataKeys = (sender as GridView).DataKeys[index];
                    Int32.TryParse(dataKeys[dataKeyName].ToString(), out index);
                }
                catch (Exception ex)
                {
                    //TODO: Something went wrong. Do something about it
                }
            }
            return index;


        }
      
       public static object GetDataKeyAsObjectFromGridView(object sender, object commandArgument, string dataKeyName)
        {

            int index = -1;
            Int32.TryParse(commandArgument.ToString(), out index);
           if (index >= 0)
           {
               try
               {
                   var dataKeys = (sender as GridView).DataKeys[index];
                   return dataKeys[dataKeyName].ToString();
               }
               catch (Exception ex)
               {
                   //TODO: Something went wrong. Do something about it
               }
           }
           return null;


       }
    
    }
}
