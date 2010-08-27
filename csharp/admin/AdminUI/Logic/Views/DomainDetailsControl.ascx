<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="DomainDetailsControl.ascx.cs"
    Inherits="AdminUI.Logic.Views.DomainDetailsControl" %>
<style type="text/css">
    .Hint
    {
        color: #CCCCCC;
    }
</style>
<div>
    <h3>
        Domain Details for
        <asp:Label ID="EntityName" runat="server" Text="<%#DomainName%>" />
    </h3>
</div>
<div class="DetailsPanel">
    <fieldset style="overflow: auto; width: 561px;">
        <legend>Details </legend>
        <div class="FieldContainer">
            <label>
                Name:</label><asp:Label runat="server" ID="DomainNameLabel" />
            </div>
            
            <div class="FieldContainer">
            <label> Create Date:
            </label> 
            <asp:Label runat="server" ID="CreateDateLabel" Style="font-weight: 700" />
            </div>
          
             <div class="FieldContainer">
            <label>Update Date:
            </label> <asp:Label runat="server" ID="UpdateDateLabel" Style="font-weight: 700" />
            </div>
        <div class="FieldContainer">
            Status:<label>Status</label>
            <asp:DropDownList ID="StatusDropDownList" runat="server">
                <asp:ListItem Value="0">New</asp:ListItem>
                <asp:ListItem Value="1">Enabled</asp:ListItem>
                <asp:ListItem Value="2">Disabled</asp:ListItem>
            </asp:DropDownList>
        </div>
        <div>
            <div class=""ButtonRow">
                &nbsp;<asp:LinkButton ID="Save" runat="server" OnClick="Save_Click">Save</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="Cancel" runat="server" OnClick="Cancel_Click">Cancel</asp:LinkButton>
            </div>
        </div>
    </fieldset>
</div>
