<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="TestService.aspx.cs" Inherits="Health.Direct.Config.Service.TestService" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >
<head runat="server">
    <title></title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
    Simple web page that verifies if the database is accessible:
    </div>
    <asp:Label runat="server" id="TestStatus"></asp:Label>
    </form>
</body>
</html>
