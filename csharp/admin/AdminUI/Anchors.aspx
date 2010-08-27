<%@ Page Language="C#" MasterPageFile="~/Site.master" AutoEventWireup="true" CodeBehind="Anchors.aspx.cs" Inherits="AdminUI.Anchors" Title="Untitled Page" %>

<%@ Register src="Logic/Views/CertificateUploadControl.ascx" tagname="CertificateUploadControl" tagprefix="uc1" %>

<asp:Content ID="Content3" ContentPlaceHolderID="ContentPlaceHolder" runat="server">
    <h2>
    Anchors</h2>

    <div __designer:mapid="71">
</div>

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
                <asp:Parameter DefaultValue="" Name="lastAnchorID" Type="Int64" />
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
                <asp:TemplateField>
                    <ItemTemplate>
                        <asp:LinkButton ID="DetailsButton" runat="server" CommandName="Details">Details</asp:LinkButton>
                        &nbsp;|
                        <asp:LinkButton ID="RemoveButton" runat="server" CommandName="Remove">Remove</asp:LinkButton>
                    </ItemTemplate>
                </asp:TemplateField>
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
        <uc1:CertificateUploadControl ID="CertificateUploadControl1" runat="server" />
    </asp:View>
    <asp:View ID="DetailsView" runat="server">
        <div>
            Owner:
            <asp:Label ID="OwnerLabel" runat="server" Text='<%# Bind("Owner") %>' />
            <br />
            Thumbprint:
            <asp:Label ID="ThumbprintLabel" runat="server" 
                Text='<%# Bind("Thumbprint") %>' />
            <br />
            CreateDate:
            <asp:Label ID="CreateDateLabel" runat="server" 
                Text='<%# Bind("CreateDate") %>' />
            <br />
            ValidStartDate:
            <asp:Label ID="ValidStartDateLabel" runat="server" 
                Text='<%# Bind("ValidStartDate") %>' />
            <br />
            ValidEndDate:
            <asp:Label ID="ValidEndDateLabel" runat="server" 
                Text='<%# Bind("ValidEndDate") %>' />
            <br />
            ForIncoming:
            <asp:CheckBox ID="ForIncomingCheckBox" runat="server" 
                Checked='<%# Bind("ForIncoming") %>' Enabled="false" />
            <br />
            ForOutgoing:
            <asp:CheckBox ID="ForOutgoingCheckBox" runat="server" 
                Checked='<%# Bind("ForOutgoing") %>' Enabled="false" />
            <br />
            Status:
            <asp:DropDownList ID="StatusDropDownList" runat="server">
                <asp:ListItem Value="0">New</asp:ListItem>
                <asp:ListItem Value="1">Enabled</asp:ListItem>
                <asp:ListItem Value="2">Disabled</asp:ListItem>
            </asp:DropDownList>
            <br />
            HasData:
            <asp:CheckBox ID="HasDataCheckBox" runat="server" 
                Checked='<%# Bind("HasData") %>' Enabled="false" />
            <br />
        </div>
    </asp:View>
</asp:MultiView>
</asp:Content>
