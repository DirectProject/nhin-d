<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="AddressDetailsControl.ascx.cs"
    Inherits="AdminUI.Logic.Views.AddressDetailsControl" %>
<h3>
    Address in
    <asp:Label ID="OwnerTitleLabel" runat="server"></asp:Label>
</h3>

<div class="AddressDetails">
    <fieldset>
        <legend><b>Details</b></legend>EmailAddress:
        <asp:TextBox  runat="server" ID="EmailAddressTextBox" />
        <br />
        DisplayName:
        <asp:TextBox  runat="server" ID="DisplayNameTextBox" />
        <br />
        Status:
        <asp:DropDownList ID="StatusDropDownList" runat="server">
            <asp:ListItem Value="0">New</asp:ListItem>
            <asp:ListItem Value="1">Enabled</asp:ListItem>
            <asp:ListItem Value="2">Disabled</asp:ListItem>
            <asp:ListItem Value="3">Deleted</asp:ListItem>
        </asp:DropDownList>
        <br />
        Type:
        <asp:TextBox runat="server" ID="TypeTextBox" />
        <br />
        <div class="ButtonRow">
            <asp:LinkButton ID="SaveButton" runat="server" CommandName="Save" 
                CssClass="SaveButton">Save</asp:LinkButton>
            &nbsp;|
            <asp:LinkButton ID="CancelButton" runat="server" CommandName="Cancel" 
                CssClass="CancelButton">Cancel</asp:LinkButton>
        </div>
    </fieldset>
</div>
