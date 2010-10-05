<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="CertificateUploadControl.ascx.cs" Inherits="AdminUI.Logic.Views.CertificateUploadControl" %>

<asp:MultiView ID="CertificateUploadMultiView" runat="server" 
    ActiveViewIndex="0">
    <asp:View ID="CertificateUploadView" runat="server">
        <div class="FileUploadContainer">
           
    
            <div class="FieldContainer">
                <label>
                Certificate File:
                <asp:FileUpload ID="CertificateUpload" runat="server" />
                </label>
            </div>
            <div class="FieldContainer">
                <label>
                Description:
                </label>
                <asp:TextBox ID="DescriptionTextBox" runat="server" Height="95px" 
                    TextMode="MultiLine" Width="235px"></asp:TextBox>
            </div>
            <div class="FieldContainer">
                <asp:Button ID="UploadButton" runat="server" onclick="UploadButton_Click" 
                    Text="Upload" />
            </div>
        </div>
    </asp:View>
    <asp:View ID="CertificatePasswordView" runat="server">
        <div class="FieldContainer">
            <label>
            Certificate Password</label>
            <asp:TextBox ID="CertificatePasswordTextBox" runat="server" 
                ValidationGroup="CertificatePasswordGroup"></asp:TextBox>
            &nbsp;<asp:RequiredFieldValidator ID="requiredPasswordVaidator" runat="server" 
                ControlToValidate="CertificatePasswordTextBox" 
                ErrorMessage="RequiredFieldValidator" SetFocusOnError="True" 
                ValidationGroup="CertificatePasswordGroup">A Certificate Password is 
            required.</asp:RequiredFieldValidator>
        </div>
        <div class="ButtonRow">
            <asp:LinkButton ID="Process" runat="server" onclick="Process_Click" 
                ValidationGroup="CertificatePasswordGroup">Process</asp:LinkButton>
            &nbsp;|
            <asp:LinkButton ID="Cancel" runat="server" CausesValidation="False" 
                onclick="Cancel_Click">Cancel</asp:LinkButton>
        </div>
    </asp:View>
</asp:MultiView>
