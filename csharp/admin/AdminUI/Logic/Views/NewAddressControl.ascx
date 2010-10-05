<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="NewAddressControl.ascx.cs" Inherits="AdminUI.Logic.Views.NewAddressControl" %>
<style type="text/css">

    .style1
    {
        color: #CCCCCC;
    }
    .ButtonRow
    {
        color: #FF0000;
    }
</style>
<div class="AddressDetails">
    <fieldset>
        <legend><b>New Address</b></legend>EmailAddress:
       <asp:TextBox runat="server" ID="EmailAddressTextBox" Width="234px" 
            CausesValidation="True" ValidationGroup="NewAddressGroup" />
        &nbsp;<br />
        DisplayName:
        <asp:TextBox runat="server" ID="DisplayNameTextBox" Width="382px" />
        <br />
        Type:         <asp:TextBox runat="server" ID="TypeTextBox" />
        <span class="style1">(e.g.:SMTP, XDR)</span><br />
        <div class="ButtonRow">
            <asp:LinkButton ID="AddButton" runat="server" CommandName="Add" CssClass="SaveButton"
                OnClick="AddButton_Click" ValidationGroup="NewAddressGroup">Add</asp:LinkButton>
            &nbsp;
            <asp:Literal ID="ErrorLiteral" runat="server" EnableViewState="False"></asp:Literal>
        </div>
    </fieldset>
</div>
