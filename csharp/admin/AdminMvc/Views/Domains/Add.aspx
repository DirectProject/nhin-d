<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<AdminMvc.Models.DomainModel>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add Domain
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add Domain</h2>

    <%= Html.ValidationSummary("Please correct the errors and try again.") %>

    <% using (Html.BeginForm()) { %> 
    
        <fieldset>
            <p>
                <%= Html.LabelFor(m => m.Name) %>
                <%= Html.TextBoxFor(m => m.Name) %>
                <%= Html.ValidationMessageFor(m => m.Name, "*") %>
            </p>
            <p>
                <input type="submit" value="Save" />
            </p>
        </fieldset>
    
    <% } %>

</asp:Content>
