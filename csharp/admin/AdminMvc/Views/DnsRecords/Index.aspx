<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<DnsRecordModel>>" %>
<%@ Import Namespace="Health.Direct.Admin.Console.Models"%>
<%@ Import Namespace="Health.Direct.Admin.Console.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	DNS Record
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <div class="action-bar">
        <%= Html.ActionLink("Add ANAME Record", "AddAname", null, new { @class = "action ui-priority-primary"})%>
        <%= Html.ActionLink("Add MX Record", "AddMx", null, new { @class = "action ui-priority-primary"})%>
        <%= Html.ActionLink("Add SOA Record", "AddSoa", null, new { @class = "action ui-priority-primary"})%>
    </div>

    <%= Html.Partial("DnsRecordList", Model) %>
    <%= Html.Partial("DnsRecordDetailsDialog", Model) %>
    
</asp:Content>
