<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<Health.Direct.Config.Store.Domain>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Delete Confirmation: <%= Model.Name %>
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Delete Confirmation</h2>

    <div>
    
        <p>Please confirm you want to delete the domain named: 
        <i><%= Model.Name %>?</i></p>
        
        <% using (Html.BeginForm()) { %> 
            <input name="confirmButton" type="submit" value="Delete" />
        <% }%>
        
    </div>

</asp:Content>
