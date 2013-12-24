namespace Health.Direct.Policy.X509.Standard
{
    /// <summary>
    /// General name types as describe in section 4.2.1.6 of RFC5280
    /// <remarks>
    /// <![CDATA[
    /// SubjectAltName ::= GeneralNames<br/>
    ///  
    /// GeneralNames ::= SEQUENCE SIZE (1..MAX) OF GeneralName<br/>
    ///  
    /// GeneralName ::= CHOICE {<br/>
    ///      otherName                       [0]     OtherName,<br/>
    ///      rfc822Name                      [1]     IA5String,<br/>
    ///      dNSName                         [2]     IA5String,<br/>
    ///      x400Address                     [3]     ORAddress,<br/>
    ///      directoryName                   [4]     Name,<br/>
    ///      ediPartyName                    [5]     EDIPartyName,<br/>
    ///      uniformResourceIdentifier       [6]     IA5String,<br/>
    ///      iPAddress                       [7]     OCTET STRING,<br/>
    ///      registeredID                    [8]     OBJECT IDENTIFIER }<br/>
    /// ]]>
    /// </remarks>
    /// </summary>
    public enum GeneralNameType
    {
        OtherName = 0,
        RFC822Name = 1,
        DNSName = 2,
        X400Address = 3,
        DirectoryName = 4,
        EdiPartyName = 5,
        UniformResourceIdentifier = 6,
        IPAddress = 7,
        RegisteredId = 8

    }

    

}