<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="DomainListControl.ascx.cs"
    Inherits="Health.Direct.AdminUI.Logic.Views.DomainListControl" %>
<div class="GridContainer">
<asp:GridView ID="DomainGridView" runat="server" AutoGenerateColumns="False" CellPadding="4"
    ForeColor="#333333" GridLines="None" DataKeyNames="Id,Name" 
        OnRowCommand="DomainGridView_RowCommand">
    <RowStyle BackColor="#F7F6F3" ForeColor="#333333" />
    <Columns>
        <asp:BoundField DataField="Name" HeaderText="Name" SortExpression="Name" />
        <asp:BoundField DataField="Status" HeaderText="Status" ReadOnly="True" 
            SortExpression="Status" />
        <asp:BoundField DataField="CreateDate" HeaderText="CreateDate" SortExpression="CreateDate"
            DataFormatString="{0:d}" />
        <asp:BoundField DataField="UpdateDate" HeaderText="UpdateDate" SortExpression="UpdateDate"
            DataFormatString="{0:d}" />
        <asp:TemplateField>
            <ItemTemplate>
                <asp:LinkButton ID="Details" runat="server" CommandArgument="<%# Container.DataItemIndex %>"
                    CommandName="Details">Details</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="Addresses" runat="server" CommandArgument="<%# Container.DataItemIndex %>"
                    CommandName="Addresses">Addresses</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="Certificates" runat="server" CommandArgument="<%# Container.DataItemIndex %>"
                    CommandName="Certificates">Certificates</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="Anchors" runat="server" CommandArgument="<%# Container.DataItemIndex %>"
                    CommandName="Anchors">Anchors</asp:LinkButton>
                &nbsp;|
                <asp:LinkButton ID="RemoveButton" runat="server" 
                    CommandArgument="<%# Container.DataItemIndex %>" CommandName="Remove" 
                    CssClass="RemoveLink">Remove</asp:LinkButton>
            </ItemTemplate>
        </asp:TemplateField>
    </Columns>
    <FooterStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
    <PagerStyle BackColor="#284775" ForeColor="White" HorizontalAlign="Center" />
    <EmptyDataTemplate>
        No domains have been defined. Please add a new one.&nbsp;
    </EmptyDataTemplate>
    <SelectedRowStyle BackColor="#E2DED6" Font-Bold="True" ForeColor="#333333" />
    <HeaderStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
    <EditRowStyle BackColor="#999999" />
    <AlternatingRowStyle BackColor="White" ForeColor="#284775" />
</asp:GridView>
</div>


