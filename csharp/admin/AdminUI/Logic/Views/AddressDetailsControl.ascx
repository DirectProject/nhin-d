<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="AddressDetailsControl.ascx.cs"
    Inherits="AdminUI.Logic.Views.AddressDetailsControl" %>
<style type="text/css">
    .style1
    {
        color: #CCCCCC;
    }
</style>
<h3>
    Address in
    <asp:Label ID="OwnerTitleLabel" runat="server"></asp:Label>
</h3>

<div class="AddressDetails">
    <fieldset>
        <legend><b>Details</b></legend>EmailAddress:
        <asp:TextBox  runat="server" ID="EmailAddressTextBox" CausesValidation="True" 
            ValidationGroup="AddressGroup" Width="383px"/>
        &nbsp;<asp:RegularExpressionValidator ID="EmailValidator" runat="server" 
            ControlToValidate="EmailAddressTextBox" ErrorMessage="Invalid email address." 
            ValidationExpression="\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*" 
            ValidationGroup="AddressGroup"></asp:RegularExpressionValidator>
        <br />
        DisplayName:
        <asp:TextBox  runat="server" ID="DisplayNameTextBox" Width="382px" />
        <br />
        Create Date:
        <asp:Label runat="server" ID="CreateDateLabel" Style="font-weight: 700" />
        <br />
        Update Date:
        <asp:Label runat="server" ID="UpdateDateLabel" Style="font-weight: 700" />
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
        <span class="style1">(e.g.:SMTP, XDR)</span><br />
        <div class="ButtonRow">
            <asp:LinkButton ID="SaveButton" runat="server" CommandName="Save" 
                CssClass="SaveButton" onclick="SaveButton_Click" 
                ValidationGroup="AddressGroup">Save</asp:LinkButton>
            &nbsp;|
            <asp:LinkButton ID="CancelButton" runat="server" CommandName="Cancel" 
                CssClass="CancelButton">Cancel</asp:LinkButton>
        </div>
    </fieldset>
</div>
