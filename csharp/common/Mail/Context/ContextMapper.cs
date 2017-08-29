using Health.Direct.Common.Mime;
using MimeKit;

namespace Health.Direct.Common.Mail.Context
{
    internal static class ContextMapper
    {
        public static ContentEncoding MapContentEncoding(string encoding)
        {
            switch (encoding)
            {
                case MimeStandard.TransferEncodingBase64:
                    return ContentEncoding.Base64;
                case MimeStandard.TransferEncoding7Bit:
                    return ContentEncoding.SevenBit;
                case MimeStandard.TransferEncodingQuoted:
                    return ContentEncoding.QuotedPrintable;
                default:
                    return ContentEncoding.Default;

            }
        }
    }
}
