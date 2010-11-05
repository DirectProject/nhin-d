<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<IEnumerable<Health.Direct.Config.Store.Anchor>>" %>
<%@ Import Namespace="MvcContrib.UI.Pager"%>
<%@ Import Namespace="MvcContrib.Pagination"%>
<%@ Import Namespace="MvcContrib.UI.Grid"%>
<%@ Import Namespace="AdminMvc.Controllers"%>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Anchors
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Anchors</h2>

    <%= Html.Grid(Model)
        .Columns(
            column =>
                {
                    column.For(d => d.Owner);
                    column.For(d => d.Thumbprint);
                    column.For(d => d.CreateDate);
                    column.For(d => d.ValidStartDate);
                    column.For(d => d.ValidEndDate);
                    column.For(d => d.ForIncoming);
                    column.For(d => d.ForOutgoing);
                    column.For(d => d.HasData);
                    column.For(d => d.Status);
                })%>
    <%= Html.Pager((IPagination)Model) %>

</asp:Content>
