<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="CertificateDetailsControl.ascx.cs"
    Inherits="AdminUI.Logic.Views.CertificateDetailsControl" %>
   <h3>
   Certificate for  
<asp:Label ID="OwnerTitleLabel" runat="server"></asp:Label>
</h3>
<div class="CertificateDetails">
    <fieldset>
        <legend><b>Details</b></legend>Owner:
        <asp:Label runat="server" ID="OwnerLabel" Style="font-weight: 700" /><br />
        Thumbprint:
        <asp:Label runat="server" ID="ThumbprintLabel" Style="font-weight: 700" /><br />
        CreateDate:
        <asp:Label runat="server" ID="CreateDateLabel" Style="font-weight: 700" /><br />
        ValidStartDate:
        <asp:Label runat="server" ID="ValidStartDateLabel" Style="font-weight: 700" /><br />
        ValidEndDate:
        <asp:Label runat="server" ID="ValidEndDateLabel" Style="font-weight: 700" /><br />
        Status:
        <asp:DropDownList ID="StatusDropDownList" runat="server">
            <asp:ListItem Value="0">New</asp:ListItem>
            <asp:ListItem Value="1">Enabled</asp:ListItem>
            <asp:ListItem Value="2">Disabled</asp:ListItem>
            <asp:ListItem Value="3">Deleted</asp:ListItem>
        </asp:DropDownList>
        <br />
        <asp:CheckBox ID="HasDataCheckBox" runat="server" Enabled="False" Text="Has Data"
            TextAlign="Left" />
        <br />
        <div class="ButtonRow" >
            <asp:LinkButton ID="SaveButton" runat="server" CommandName="Save" 
                CssClass="SaveButton" onclick="SaveButton_Click">Save</asp:LinkButton>
&nbsp;|
            <asp:LinkButton ID="CancelButton" runat="server" CommandName="Cancel" 
                CssClass="CancelButton" onclick="CancelButton_Click">Cancel</asp:LinkButton>
        </div>
    </fieldset>
</div>
