using System.Web.Mvc;
using System.Web.Routing;

namespace AdminMvc.Common
{
    public static class HtmlHelperExtensions
    {
        public static MvcHtmlString Span(this HtmlHelper html, string text)
        {
            return Span(html, text, null);
        }

        public static MvcHtmlString Span(this HtmlHelper html, string text, object htmlAttributes)
        {
            var builder = new TagBuilder("span");
            builder.SetInnerText(text);
            builder.MergeAttributes(new RouteValueDictionary(htmlAttributes));
            return MvcHtmlString.Create(builder.ToString(TagRenderMode.Normal));
        }
    }
}