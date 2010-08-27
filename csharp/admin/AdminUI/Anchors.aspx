<%@ Page Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true" CodeBehind="Anchors.aspx.cs" Inherits="AdminUI.Anchors" Title="Untitled Page" %>

<asp:Content ID="Content3" ContentPlaceHolderID="ContentPlaceHolder" runat="server">
    <h2>
    Anchors</h2>

    <div>
        <asp:HyperLink ID="DomainsHyperLink" runat="server" 
            NavigateUrl="~/Domains.aspx">Back to Domains</asp:HyperLink>
</div>

<asp:MultiView ID="AnchorsMultiView" runat="server" ActiveViewIndex="0">
    <asp:View ID="MasterView" runat="server">
        <asp:ObjectDataSource ID="ObjectDataSource1" runat="server" 
            SelectMethod="EnumerateAnchors" 
            TypeName="NHINDirect.Config.Client.CertificateService.AnchorStoreClient">
            <SelectParameters>
                <asp:Parameter DefaultValue="-1" Name="lastAnchorID" Type="Int64" />
                <asp:Parameter DefaultValue="20" Name="maxResults" Type="Int32" />
                <asp:Parameter DefaultValue="" Name="options" Type="Object" />
            </SelectParameters>
        </asp:ObjectDataSource>
        <asp:GridView ID="GridView1" runat="server" AutoGenerateColumns="False" 
            CellPadding="4" DataSourceID="ObjectDataSource1" ForeColor="#333333" 
            GridLines="None">
            <RowStyle BackColor="#F7F6F3" ForeColor="#333333" />
            <Columns>
                <asp:BoundField DataField="Owner" HeaderText="Owner" SortExpression="Owner" />
                <asp:BoundField DataField="Thumbprint" HeaderText="Thumbprint" 
                    SortExpression="Thumbprint" />
                <asp:BoundField DataField="CreateDate" HeaderText="CreateDate" 
                    SortExpression="CreateDate" />
                <asp:BoundField DataField="ValidStartDate" HeaderText="ValidStartDate" 
                    SortExpression="ValidStartDate" />
                <asp:BoundField DataField="ValidEndDate" HeaderText="ValidEndDate" 
                    SortExpression="ValidEndDate" />
                <asp:CheckBoxField DataField="ForIncoming" HeaderText="ForIncoming" 
                    SortExpression="ForIncoming" />
                <asp:CheckBoxField DataField="ForOutgoing" HeaderText="ForOutgoing" 
                    SortExpression="ForOutgoing" />
                <asp:CheckBoxField DataField="HasData" HeaderText="HasData" ReadOnly="True" 
                    SortExpression="HasData" />
            </Columns>
            <FooterStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
            <PagerStyle BackColor="#284775" ForeColor="White" HorizontalAlign="Center" />
            <EmptyDataTemplate>
                <div>
                    &nbsp;<div>
                        There are no anchors defined. Please upload a certificate file.</div>
                    <div style="position: relative; top: 0px; left: 0px; width: 43px;">
                        <img alt="" src="Assets/images/exclamation.png" 
                            style="width: 32px; height: 32px" /></div>
                </div>
            </EmptyDataTemplate>
            <SelectedRowStyle BackColor="#E2DED6" Font-Bold="True" ForeColor="#333333" />
            <HeaderStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
            <EditRowStyle BackColor="#999999" />
            <AlternatingRowStyle BackColor="White" ForeColor="#284775" />
        </asp:GridView>
    </asp:View>
    <asp:View ID="DetailsView" runat="server">
    </asp:View>
</asp:MultiView>
</asp:Content>
