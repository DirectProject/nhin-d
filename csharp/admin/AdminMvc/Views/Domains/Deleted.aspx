<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Domain Deleted
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Domain Deleted</h2>

    <div>
        <p>Your domain was successfully deleted.</p>
    </div>
    
    <div>
        <p><%= Html.ActionLink("Return to domains", "Index") %></p>
    </div>

</asp:Content>
