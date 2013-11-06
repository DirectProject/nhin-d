/* 
 Copyright (c) 2010, Direct Project
 All rights reserved.

 Authors:
    Umesh Madan     umeshma@microsoft.com
    Joe Shook       jshook@kryptiq.com

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/
using System;
using System.Runtime.Serialization;
using System.Data.Common;
using System.Data.Linq;
using System.Data.SqlClient;

using Health.Direct.Common;

namespace Health.Direct.Config.Store
{
    public enum ConfigStoreError
    {
        None = 0,
        Unknown,
        Unexpected,
        Conflict,
        DatabaseError,
        UniqueConstraint,
        ForeignKeyConstraint,
        InvalidIDs,
        InvalidDomain,
        InvalidDomainName,
        DomainNameLength,
        InvalidDomainID,
        InvalidAddress,
        AddressLength,
        DisplayNameLength,
        InvalidEmailAddress,
        InvalidCertificate,
        InvalidX509Certificate,
        MissingCertificateData,
        InvalidOwnerName,
        OwnerLength,
        InvalidThumbprint,
        InvalidAnchor,
        AccountNameLength,
        InvalidMXSMTPName,
        MXSMTPNameLength,
        InvalidMX,
        InvalidDnsRecord,
        NotesLength,
        InvalidAdministrator,
        InvalidUsername,
        InvalidPassword,
        InvalidPropertyName,
        InvalidPropertyNameLength,
        InvalidTextBlobName,
        InvalidTextBlobNameLength,
        InvalidBlob,
        AgentNameLength,
        InvalidAgentName,
        InvalidMdnIdentifier,
        InvalidMdn,
        DuplicateProcessedMdn,
        DuplicateDispatchedMdn,
        DuplicateFailedMdn,
        MdnPreviouslyProcessed,
        MdnUncorrelated,
        MdnPreviouslyFailed,
        InvalidUrl,
        UrlLength,
        InvalidBundle,
        DuplicateMdnStart
    }

    public class ConfigStoreException : DirectException<ConfigStoreError>
    {
        public ConfigStoreException()
            : base(ConfigStoreError.Unknown)
        {
        }
        
        public ConfigStoreException(ConfigStoreError error)
            : base(error)
        {
        }
        
        public ConfigStoreFault ToFault()
        {
            return new ConfigStoreFault(this.Error);
        }

        public static ConfigStoreError ToError(Exception ex)
        {
            ConfigStoreException ce = ex as ConfigStoreException;
            if (ce != null)
            {
                return ce.Error;
            }

            ChangeConflictException conflict = ex as ChangeConflictException;
            if (conflict != null)
            {
                return ConfigStoreError.Conflict;
            }

            SqlException sqlex = ex as SqlException;
            if (sqlex != null)
            {
                ConfigStoreError errorCode = ConfigStoreError.DatabaseError;
                switch (sqlex.Number)
                {
                    default:
                        break;

                    case (int)SqlErrorCodes.DuplicatePrimaryKey:
                    case (int)SqlErrorCodes.UniqueConstraintViolation:
                        errorCode = ConfigStoreError.UniqueConstraint;
                        break;

                    case (int)SqlErrorCodes.ForeignKeyViolation:
                        errorCode = ConfigStoreError.ForeignKeyConstraint;
                        break;
                }

                return errorCode;
            }

            DbException dbex = ex as DbException;
            if (dbex != null)
            {
                return ConfigStoreError.DatabaseError;
            }

            return ConfigStoreError.Unknown;
        }
        
        public static bool IsPrimaryKeyViolation(Exception ex)
        {
            ConfigStoreError error = ConfigStoreException.ToError(ex);
            return (error == ConfigStoreError.UniqueConstraint || error == ConfigStoreError.Conflict);
        }
    }

    /// <summary>
    /// Serializable - used for web services
    /// </summary>
    [DataContract(Namespace = ConfigStore.Namespace)]
    public class ConfigStoreFault
    {
        string m_message;
        
        public ConfigStoreFault()
            : this(ConfigStoreError.Unknown)
        {
        }
        
        public ConfigStoreFault(ConfigStoreError error)
            : this(error, null)
        {
        }

        public ConfigStoreFault(ConfigStoreError error, string message)
        {
            this.Error = error;
            this.Message = message;
        }

        public ConfigStoreFault(ConfigStoreException ex)
            : this(ex.Error, ex.Message)
        {
        }
        
        [DataMember]
        public ConfigStoreError Error
        {
            get;
            set;
        }
        
        //
        // Optional
        //
        [DataMember]
        public string Message
        {
            get
            {
                return m_message ?? string.Empty;
            }
            set
            {
                m_message = value;
            }
        }
        
        public static ConfigStoreFault ToFault(Exception ex)
        {
            ConfigStoreException ce = ex as ConfigStoreException;
            if (ce != null)
            {
                return ce.ToFault();
            }
            
            ChangeConflictException conflict = ex as ChangeConflictException;
            if (conflict != null)
            {
                return new ConfigStoreFault(ConfigStoreError.Conflict, conflict.Message);
            }
            
            SqlException sqlex = ex as SqlException;
            if (sqlex != null)
            {
                ConfigStoreError errorCode = ConfigStoreError.DatabaseError;
                switch (sqlex.Number)
                {
                    default:
                        break;
                    
                    case (int) SqlErrorCodes.DuplicatePrimaryKey:
                    case (int) SqlErrorCodes.UniqueConstraintViolation:
                        errorCode = ConfigStoreError.UniqueConstraint;
                        break;    
                    
                    case (int) SqlErrorCodes.ForeignKeyViolation:
                        errorCode = ConfigStoreError.ForeignKeyConstraint;
                        break;                                                               
                }
                
                return new ConfigStoreFault(errorCode, sqlex.Message);
            }
            
            DbException dbex = ex as DbException;
            if (dbex != null)
            {
                return new ConfigStoreFault(ConfigStoreError.DatabaseError, dbex.Message);
            }
            
            return new ConfigStoreFault(ConfigStoreError.Unknown, ex.Message);
        }
        
        public override string ToString()
        {
            if (string.IsNullOrEmpty(m_message))
            {
                return this.Error.ToString();
            }
            
            return string.Format("ERROR={0};{1}", this.Error, m_message);
        }
    }

    /// <summary>
    /// Can't believe we have to do this...
    /// </summary>
    internal enum SqlErrorCodes : int
    {
        ForeignKeyViolation = 547,
        UniqueConstraintViolation = 2601,
        DuplicatePrimaryKey = 2627,
        Deadlock = 1205
    }
}