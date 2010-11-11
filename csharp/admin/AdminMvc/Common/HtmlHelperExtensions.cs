using System.Web;
using System.Web.Mvc;
using System.Web.Routing;

namespace AdminMvc.Common
{
    public static class HtmlHelperExtensions
    {
        public static MvcHtmlString JQueryUITheme(this HtmlHelper html, string defaultThemeName)
        {
            string themeName = GetThemeName(html, defaultThemeName);
            var href = string.Format("https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.6/themes/{0}/jquery-ui.css", themeName);

            var builder = new TagBuilder("link");
            builder.MergeAttribute("href", href);
            builder.MergeAttribute("rel", "stylesheet");
            builder.MergeAttribute("type", "text/css");

            return MvcHtmlString.Create(builder.ToString(TagRenderMode.SelfClosing));
        }

        private static string GetThemeName(HtmlHelper html, string defaultThemeName)
        {
            var cookie = html.ViewContext.HttpContext.Request.Cookies["jquery-ui-theme"];
            return cookie != null
                       ? (HttpUtility.UrlDecode(cookie.Value).ToLower().Replace(' ', '-'))
                       : defaultThemeName;
        }

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

        public static MvcHtmlString File(this HtmlHelper html, string name)
        {
            return File(html, name, null);
        }

        public static MvcHtmlString File(this HtmlHelper html, string name, object htmlAttributes)
        {
            var builder = new TagBuilder("input");
            builder.MergeAttributes(new RouteValueDictionary(htmlAttributes));
            builder.MergeAttribute("name", name, true);
            builder.MergeAttribute("type", "file");
            builder.MergeAttribute("id", name, false);
            return MvcHtmlString.Create(builder.ToString(TagRenderMode.SelfClosing));
        }
    }
}