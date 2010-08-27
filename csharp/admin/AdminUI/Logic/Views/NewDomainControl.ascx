<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="NewDomainControl.ascx.cs" Inherits="AdminUI.Logic.Views.NewDomainControl" %>
<div class="DetailsPanel">
    <fieldset style="overflow: auto; width: 561px;">
        <legend>New Domain
        </legend>

        <div>Name:<asp:TextBox ID="DomainNameTextBox" runat="server" 
                CausesValidation="True" ValidationGroup="NewDomainGroup" />
        &nbsp;<span class="style1">(e.g.:domain.com)             <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" 
                ControlToValidate="DomainNameTextBox" ErrorMessage="RequiredFieldValidator" 
                ValidationGroup="NewDomainGroup">Domain name required.</asp:RequiredFieldValidator>
            </span></div>
        <div> Status:
            <asp:DropDownList ID="StatusDropDownList" runat="server">
                <asp:ListItem Value="0">New</asp:ListItem>
                <asp:ListItem Value="1">Enabled</asp:ListItem>
                <asp:ListItem Value="2">Disabled</asp:ListItem>
              
            </asp:DropDownList>
        </div>
        <div>
            <div class="ButtonRow" style="color: #FF0000">
         <asp:LinkButton ID="Add" runat="server" 
                    ValidationGroup="NewDomainGroup" onclick="Add_Click">Add</asp:LinkButton>
            &nbsp;<asp:Literal ID="ErrorLiteral" runat="server" EnableViewState="False"></asp:Literal>
            </div>
        </div>
  
      
</fieldset>     
</div>