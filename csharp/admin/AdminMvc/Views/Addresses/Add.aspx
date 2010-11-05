<%@ Page Title="" Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<AdminMvc.Models.AddressModel>" %>

<asp:Content ID="Content1" ContentPlaceHolderID="TitleContent" runat="server">
	Add Address
</asp:Content>

<asp:Content ID="Content2" ContentPlaceHolderID="MainContent" runat="server">

    <h2>Add Address</h2>

    <%= Html.ValidationSummary("Please correct the errors and try again.") %>

    <% using (Html.BeginForm()) { %> 
    
        <fieldset>
            <p>
                <%= Html.LabelFor(m => m.EmailAddress) %>
                <%= Html.TextBoxFor(m => m.EmailAddress) %>
                <%= Html.ValidationMessageFor(m => m.EmailAddress, "*") %>
            </p>
            <p>
                <%= Html.LabelFor(m => m.DisplayName) %>
                <%= Html.TextBoxFor(m => m.DisplayName) %>
                <%= Html.ValidationMessageFor(m => m.DisplayName, "*") %>
            </p>
            <p>
                <%= Html.LabelFor(m => m.Type) %>
                <%= Html.TextBoxFor(m => m.Type) %>
                <%= Html.ValidationMessageFor(m => m.Type, "*") %>
            </p>
            <p>
                <%= Html.HiddenFor(m => m.DomainID) %>
                <input type="submit" value="Save" />
                <%= Html.ActionLink("Cancel", "Show", new {domainID = Model.DomainID}) %>
            </p>
        </fieldset>
    
    <% } %>

</asp:Content>
