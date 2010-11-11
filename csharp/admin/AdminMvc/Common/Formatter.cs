using System;

namespace AdminMvc.Common
{
    public static class Formatter
    {
        private const string DateFormat = "MM/dd/yyyy";

        public static string Format(DateTime dateTime)
        {
            return dateTime.ToString(DateFormat);
        }
    }
}