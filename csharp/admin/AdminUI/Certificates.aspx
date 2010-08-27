<%@ Page Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true" CodeBehind="Certificates.aspx.cs"
    Inherits="AdminUI.Certificates" Title="Untitled Page" %>

<%@ Register src="Logic/Views/CertificateListControl.ascx" tagname="CertificateListControl" tagprefix="uc1" %>

<%@ Register src="Logic/Views/CertificateDetailsControl.ascx" tagname="CertificateDetailsControl" tagprefix="uc2" %>

<asp:Content ID="Content3" ContentPlaceHolderID="ContentPlaceHolder" runat="server">
    <h2>
        Certificates</h2>
   
    <div>
        <asp:HyperLink ID="DomainsHyperLink" runat="server" 
            NavigateUrl="~/Domains.aspx">Back to Domains</asp:HyperLink>
</div>

    <asp:MultiView ID="CertificatesMultiView" runat="server" 
    ActiveViewIndex="0">
        <asp:View ID="MasterView" runat="server">
            <uc1:CertificateListControl ID="CertificateListControl1" runat="server" />
        </asp:View>
        <asp:View ID="DetailsView" runat="server">
            <uc2:CertificateDetailsControl ID="CertificateDetailsControl1" runat="server" />
        </asp:View>
    </asp:MultiView>
     <p style="color: #FF0000">
        <asp:Literal ID="ErrorLiteral" runat="server" EnableViewState="False"></asp:Literal>
    </p>
</asp:Content>
