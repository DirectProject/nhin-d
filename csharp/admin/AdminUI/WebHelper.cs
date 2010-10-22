using System;
using System.Web.UI.WebControls;

namespace Health.Direct.AdminUI
{
    public static class WebHelper
    {
        public static string DateTimeFormatString = "{0:d}";
      
        public static string FormatDateTime(DateTime date)
        {
            return string.Format(DateTimeFormatString, date);
        }
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
                    throw new Exception("Could not find the key specified:" + dataKeyName, ex);
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