/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    John Theisen
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Linq.Expressions;
using System.Web;
using System.Web.Mvc;
using System.Web.Mvc.Html;
using System.Web.Routing;

namespace Health.Direct.Admin.Console.Common
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

        public static MvcHtmlString P(this HtmlHelper html, string text)
        {
            return P(html, text, null);
        }

        public static MvcHtmlString P(this HtmlHelper html, string text, object htmlAttributes)
        {
            var builder = new TagBuilder("p");
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

        public static MvcHtmlString TextBoxWithMaxLengthFor<TModel, TProperty>(
            this HtmlHelper<TModel> htmlHelper,
            Expression<Func<TModel, TProperty>> expression
            )
        {
            return TextBoxWithMaxLengthFor(htmlHelper, expression, null);
        }

        public static MvcHtmlString TextBoxWithMaxLengthFor<TModel, TProperty>(
                this HtmlHelper<TModel> htmlHelper,
                Expression<Func<TModel, TProperty>> expression,
                object htmlAttributes
            )
        {
            var member = expression.Body as MemberExpression;
            if (member == null)
            {
                return htmlHelper.TextBoxFor(expression);
            }

            var metadataTypeAttr = member.Member.ReflectedType
              .GetCustomAttributes(typeof(MetadataTypeAttribute), true)
              .FirstOrDefault() as MetadataTypeAttribute;

            IDictionary<string, object> attributes = null;

            if (metadataTypeAttr != null)
            {
                var stringLength = metadataTypeAttr.MetadataClassType
                  .GetProperty(member.Member.Name)
                  .GetCustomAttributes(typeof(StringLengthAttribute), true)
                  .FirstOrDefault() as StringLengthAttribute;

                if (stringLength != null)
                {
                    attributes = new RouteValueDictionary(htmlAttributes) {{"maxlength", stringLength.MaximumLength}};
                }
            }

            return htmlHelper.TextBoxFor(expression, attributes);
        }
    }
}