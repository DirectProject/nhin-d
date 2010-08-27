<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="CertificateListControl.ascx.cs"
    Inherits="AdminUI.Logic.Views.CertificateListControl" %>
<%@ Register src="CertificateUploadControl.ascx" tagname="CertificateUploadControl" tagprefix="uc1" %>

        <div class="FilterTitleContainer" id="OwnerTitleContainer" runat="server" visible="false">
        <h3>For:<asp:Label ID="OwnerTitleLabel" runat="server"></asp:Label></h3>
        </div>

<asp:GridView ID="CertificateGridView" runat="server" AutoGenerateColumns="False"
    CellPadding="4" ForeColor="#333333" GridLines="None" 
    OnRowCommand="CertificateGridView_RowCommand" DataKeyNames="ID">
    <RowStyle BackColor="#F7F6F3" ForeColor="#333333" />
    <Columns>
        <asp:BoundField DataField="Owner" HeaderText="Owner" />
        <asp:BoundField DataField="Thumbprint" HeaderText="Thumbprint" SortExpression="Thumbprint" />
        <asp:BoundField DataField="Status" HeaderText="Status" ReadOnly="True" 
            SortExpression="Status" />
        <asp:BoundField DataField="CreateDate" DataFormatString="{0:d}" HeaderText="CreateDate"
            SortExpression="CreateDate" />
        <asp:BoundField DataField="ValidStartDate" DataFormatString="{0:d}" HeaderText="ValidStartDate"
            SortExpression="ValidStartDate" />
        <asp:BoundField DataField="ValidEndDate" DataFormatString="{0:d}" HeaderText="ValidEndDate"
            SortExpression="ValidEndDate" />
        <asp:TemplateField>
            <ItemTemplate>
                <asp:LinkButton ID="Details" runat="server" CommandArgument="<%# Container.DataItemIndex %>"
                    CommandName="Details">Details</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="Export" runat="server" CommandArgument="<%# Container.DataItemIndex %>"
                    CommandName="Export">Export</asp:LinkButton>
            </ItemTemplate>
        </asp:TemplateField>
    </Columns>
    <FooterStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
    <PagerStyle BackColor="#284775" ForeColor="White" HorizontalAlign="Center" />
    <EmptyDataTemplate>
        <div>
            &nbsp;<div>
                There are no certificates defined. Please upload a certificate file.</div>
            <div style="position: relative; top: 0px; left: 0px; width: 43px;">
                <img alt="" src="../../Assets/images/exclamation.png" 
                    style="width: 32px; height: 32px" /></div>
        </div>
        <br />
    </EmptyDataTemplate>
    <SelectedRowStyle BackColor="#E2DED6" Font-Bold="True" ForeColor="#333333" />
    <HeaderStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
    <EditRowStyle BackColor="#999999" />
    <AlternatingRowStyle BackColor="White" ForeColor="#284775" />
</asp:GridView>

<uc1:CertificateUploadControl ID="CertificateUploadControl1" runat="server" 
    Visible="False" />
<p style="color: #FF0000">
<asp:Literal ID="ErrorLiteral" runat="server" EnableViewState="False"></asp:Literal>
</p>
