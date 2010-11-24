<%@ Page Language="C#" MasterPageFile="~/Views/Shared/UnAuthSite.Master" Inherits="System.Web.Mvc.ViewPage<HandleErrorInfo>" %>

<asp:Content ID="errorTitle" ContentPlaceHolderID="TitleContent" runat="server">
    Configuration Service Unavailable
</asp:Content>

<asp:Content ID="errorContent" ContentPlaceHolderID="MainContent" runat="server">
    <h2>
        The configuration service is unavailable.
    </h2>
    <p>Message = '<%= Model.Exception.GetBaseException().Message %>'</p>
</asp:Content>
