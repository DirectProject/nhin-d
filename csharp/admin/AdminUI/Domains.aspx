<%@ Page Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true" CodeBehind="Domains.aspx.cs" Inherits="AdminUI.DomainsPage" Title="Domain Administration" %>
<%@ Register src="~/Logic/Views/DomainListControl.ascx" tagname="DomainListControl" tagprefix="uc1" %>
<%@ Register src="Logic/Views/DomainDetailsControl.ascx" tagname="DomainDetailsControl" tagprefix="uc2" %>
<%@ Register src="Logic/Views/NewDomainControl.ascx" tagname="NewDomainControl" tagprefix="uc3" %>
<asp:Content ID="Content3" ContentPlaceHolderID="ContentPlaceHolder" runat="server">
    <h2>Domains</h2>
    
    <asp:MultiView ID="DomainsMultiView" runat="server" ActiveViewIndex="0">
         <asp:View ID="MasterView" runat="server">
            <uc1:DomainListControl ID="DomainListControl1" runat="server" />
         <uc3:NewDomainControl ID="NewDomainControl1" runat="server" />
        </asp:View>
        
        
        <asp:View ID="DetailsView" runat="server">
            <uc2:DomainDetailsControl ID="DomainDetailsControl1" runat="server" />
        </asp:View>
       
    </asp:MultiView>
    <br />
</asp:Content>
