<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<DnsRecordModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	DNS Record
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <div class="action-bar">
        <%= Html.ActionLink("Add DNS Record", "Add", null, new { @class = "action ui-priority-primary"})%>
    </div>

    <%= Html.Partial("DnsRecordList", Model) %>
    
</asp:Content>
