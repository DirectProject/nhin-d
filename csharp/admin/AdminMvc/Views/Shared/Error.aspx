<%@ Page Language="C#" MasterPageFile="~/Views/Shared/UnAuthSite.Master" Inherits="System.Web.Mvc.ViewPage<HandleErrorInfo>" %>

<asp:Content ID="errorTitle" ContentPlaceHolderID="TitleContent" runat="server">
    Error
</asp:Content>

<asp:Content ID="errorContent" ContentPlaceHolderID="MainContent" runat="server">
    <h2>
        Sorry, an error occurred while processing your request.
    </h2>
    <h3>Exception details:</h3>
    <h4><%= Model.Exception %></h4>
</asp:Content>
