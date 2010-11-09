<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Config.Store.Address>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Address Deleted
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Address Deleted</h2>

    <div>
        <p>The address was successfully deleted.</p>
    </div>
    
    <div>
        <p><%= Html.ActionLink("Return to addresses", "Show", new { domainID = Model.DomainID })%></p>
    </div>

</asp:Content>
