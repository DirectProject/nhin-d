<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<DomainModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Domains
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <div class="action-bar">
        <%= Html.ActionLink("Add Domain", "Add", null, new { @class = "action ui-priority-primary"})%>
    </div>

    <%= Html.Partial("DomainList", Model) %>
    <%= Html.Partial("DomainDetailsDialog") %>
    
</asp:Content>
