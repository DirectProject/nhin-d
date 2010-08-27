<%@ Page Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true" CodeBehind="Addresses.aspx.cs"
    Inherits="AdminUI.Addresses" Title="Untitled Page" %>

<%@ Register src="Logic/Views/AddressListControl.ascx" tagname="AddressListControl" tagprefix="uc1" %>

<%@ Register src="Logic/Views/AddressDetailsControl.ascx" tagname="AddressDetailsControl" tagprefix="uc1" %>

<asp:Content ID="Content3" ContentPlaceHolderID="ContentPlaceHolder" runat="server">
    <h2>Addresses
    </h2>

    <asp:MultiView ID="AddressesMultiView" runat="server" ActiveViewIndex="0">
        <asp:View ID="MasterView" runat="server">
            <uc1:AddressListControl ID="AddressListControl1" runat="server" />
        </asp:View>
         <asp:View ID="DetailsView" runat="server">
             <uc1:AddressDetailsControl ID="AddressDetailsControl1" runat="server" />
        </asp:View>
          <asp:View ID="CardView" runat="server"/>
        </asp:View>
    </asp:MultiView>
    </asp:Content>
