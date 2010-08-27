<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="AddressListControl.ascx.cs" Inherits="AdminUI.Logic.Views.AddressListControl" %>
        <div class="FilterTitleContainer" id="OwnerTitleContainer" runat="server" visible="false">
        <h3>For:<asp:Label ID="OwnerTitleLabel" runat="server"></asp:Label></h3>
        </div>
            <asp:GridView ID="AddressesGridView" runat="server" AutoGenerateColumns="False" 
                CellPadding="4" ForeColor="#333333" 
                GridLines="None" DataKeyNames="ID, DomainID, EmailAddress" 
                onrowcommand="AddressesGridView_RowCommand">
                <RowStyle BackColor="#F7F6F3" ForeColor="#333333" />
                <Columns>
                    <asp:BoundField DataField="DisplayName" HeaderText="DisplayName" 
                        SortExpression="DisplayName" />
                    <asp:BoundField DataField="EmailAddress" HeaderText="EmailAddress" 
                        SortExpression="EmailAddress" />
                    <asp:BoundField DataField="Status" HeaderText="Status" ReadOnly="True" 
                        SortExpression="Status" />
                    <asp:BoundField DataField="CreateDate" DataFormatString="{0:d}" 
                        HeaderText="CreateDate" SortExpression="CreateDate" />
                    <asp:BoundField DataField="UpdateDate" DataFormatString="{0:d}" 
                        HeaderText="UpdateDate" SortExpression="UpdateDate" />
                    <asp:BoundField DataField="Type" HeaderText="Type" SortExpression="Type" />
                    <asp:CheckBoxField DataField="HasType" HeaderText="HasType" ReadOnly="True" 
                        SortExpression="HasType" />
                    <asp:TemplateField>
                        <ItemTemplate>
                            <asp:LinkButton ID="DetailsButton" runat="server" 
                                CommandArgument="<%# Container.DataItemIndex %>" CommandName="Details">Details</asp:LinkButton>
                            &nbsp;|
                            <asp:LinkButton ID="NHINDCardButton" runat="server" 
                                CommandArgument="<%# Container.DataItemIndex %>" CommandName="Card">NHIND 
                            Card</asp:LinkButton>
                        </ItemTemplate>
                    </asp:TemplateField>
                </Columns>
                <FooterStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
                <PagerStyle BackColor="#284775" ForeColor="White" HorizontalAlign="Center" />
                <EmptyDataTemplate>
                    <div>
                        &nbsp;<div>
                            There are no addresses defined. Please add one.</div>
                        <div style="position: relative; top: 0px; left: 0px; width: 43px;">
                            <img alt="" src="../../Assets/images/exclamation.png" 
                                style="width: 32px; height: 32px" /></div>
                    </div>
                </EmptyDataTemplate>
                <SelectedRowStyle BackColor="#E2DED6" Font-Bold="True" ForeColor="#333333" />
                <HeaderStyle BackColor="#5D7B9D" Font-Bold="True" ForeColor="White" />
                <EditRowStyle BackColor="#999999" />
                <AlternatingRowStyle BackColor="White" ForeColor="#284775" />
            </asp:GridView>
     
      

    
