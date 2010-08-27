<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="DomainDetailsControl.ascx.cs"
    Inherits="AdminUI.Logic.Views.DomainDetailsControl" %>
<style type="text/css">
    .style1
    {
        color: #CCCCCC;
    }
</style>
<div>
    <h3>
        Domain
        Details for
            <asp:Label ID="EntityName" runat="server" Text="<%#DomainName%>"/>
        </h3>
</div>
<div class="DetailsPanel">
    <fieldset style="overflow: auto; width: 561px;">
        <legend>Details
        </legend>

        <div>Name:<asp:TextBox ID="DomainNameTextBox" runat="server" />
        &nbsp;<span class="style1">(e.g.:domain.com)</span></div>
        <div>Postmaster:<asp:Label ID="PostmasterTextBox" runat="server"></asp:Label>
        </div>
        <div> Status:
            <asp:DropDownList ID="StatusDropDownList" runat="server">
                <asp:ListItem Value="0">New</asp:ListItem>
                <asp:ListItem Value="1">Enabled</asp:ListItem>
                <asp:ListItem Value="2">Disabled</asp:ListItem>
                <asp:ListItem Value="3">Deleted</asp:ListItem>
            </asp:DropDownList>
        </div>
        <div>
            <div style="text-align: center">
                &nbsp;<asp:LinkButton ID="Save" runat="server" onclick="Save_Click">Save</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="Cancel" runat="server" onclick="Cancel_Click">Cancel</asp:LinkButton>
            </div>
        </div>
  
      
</fieldset>     
</div>
