/* 
 Copyright (c) 2010-2017, Direct Project
 All rights reserved.

 Authors:
    Joseph Shook    Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

namespace Health.Direct.Context
{

    /// <summary>
    /// Declare well known Mail headers
    /// </summary>
    public class MailStandard
    {
        /// <summary>
        /// Organize by Mail headers
        /// </summary>
        public class Headers
        {
            /// <summary>
            /// Header indicating conformance to the "Implementation Guide for Expressing Context in Direct Messaging".
            /// </summary>
            public static string DirectContext = "X-Direct-Context";
        }
    }

    /// <summary>
    /// Declare well known Mime headers
    /// </summary>
    public class MimeStandard
    {
        /// <summary>
        /// 
        /// </summary>
        public class DispositionType
        {
            /// <summary>
            /// The <c>Attachment Disposition Type</c>
            /// </summary>
            public const string Attachment = "attachment";
        }
    }

    /// <summary>
    /// Provides constants and utility functions for working with <see cref="Context"/>
    /// </summary>
    /// <remarks>
    /// "Implementation Guide for Expressing Context in Direct Messaging"
    /// Reference doc...
    /// </remarks>
    public class ContextStandard
    {
        /// <summary>
        /// The standard string representation of the <c>Content-ID</c> MIME header
        /// </summary>
        public const string ContentIdHeader = "Content-ID";

        /// <summary>
        /// Default filename for a <see cref="MimeStandard.DispositionType"/> of <c>attachment</c>
        /// </summary>
        public const string AttachmentFileName = "metadata.txt";

        /// <summary>
        /// Name for the <c>version</c> header
        /// </summary>
        public const string Version = "version";

        /// <summary>
        /// Name for the <c>id</c> header
        /// </summary>
        public const string Id = "id";

        /// <summary>
        /// Name for the <c>patient-id</c> header
        /// </summary>
        public const string PatientId = "patient-id";


        /// <summary>
        /// Transaction type 
        /// </summary>
        /// <remarks>
        /// See <a href="http://wiki.directproject.org/file/detail/Implementation+Guide+for+Expressing+Context+in+Direct+Messaging+v1.0-DRAFT-2016122901.docx"> Context in Direct</a>.
        /// </remarks>
        public class Type
        {
            /// <summary>
            /// Name for the <c>type</c> header
            /// </summary>
            public const string Label = "type";

            /// <summary>
            /// Transaction <c>Type</c>'s <c>category</c> value of "laboratory"
            /// </summary>
            public const string CategoryLaboratory = "laboratory";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>category</c> value of "radiology"
            /// </summary>
            public const string CategoryRadiology = "radiology";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>category</c> value of "pharmacy"
            /// </summary>
            public const string CategoryPharmacy = "pharmacy";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>category</c> value of "referral"
            /// </summary>
            public const string CategoryReferral = "referral";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>category</c> value of "general"
            /// </summary>
            public const string CategoryGeneral = "general";

            /// <summary>
            /// Normative categories.  i.e., other values are not permitted.
            /// </summary>
            public enum Category
            {
                /// <summary>
                /// Unknown.
                /// </summary>
                Unknown = 0,

                /// <summary>
                /// The <c>laboratory category</c>
                /// </summary>
                Laboratory,

                /// <summary>
                /// The <c>radiology category</c>
                /// </summary>
                Radiology,

                /// <summary>
                /// The <c>pharmacy category</c>
                /// </summary>
                Pharmacy,

                /// <summary>
                /// The <c>referral category</c>
                /// </summary>
                Referral,

                /// <summary>
                /// The <c>general category</c>
                /// </summary>
                General
            }

            /// <summary>
            /// Transaction <c>Type</c>'s <c>action</c> value of "order"
            /// </summary>
            public const string ActionOrder = "order";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>action</c> value of "report"
            /// </summary>
            public const string ActionReport = "report";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>action</c> value of "result"
            /// </summary>
            public const string ActionResult = "result";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>action</c> value of "query"
            /// </summary>
            public const string ActionQuery = "query";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>action</c> value of "response"
            /// </summary>
            public const string ActionResponse = "response";
            /// <summary>
            /// Transaction <c>Type</c>'s <c>action</c> value of "notification"
            /// </summary>
            public const string ActionNotification = "notification";

            /// <summary>
            /// Intended to identify the role of the  message sender in the transaction sequence, i.e., a query
            /// action would generally be followed by a response action.
            /// </summary>
            public enum Action
            {
                /// <summary>
                /// Unknown.
                /// </summary>
                Unknown = 0,

                /// <summary>
                /// The <c>order action</c>
                /// </summary>
                Order,

                /// <summary>
                /// The <c>report action</c>
                /// </summary>
                Report,

                /// <summary>
                /// The <c>result action</c>
                /// </summary>
                Result,

                /// <summary>
                /// The <c>query action</c>
                /// </summary>
                Query,

                /// <summary>
                /// The <c>response action</c>
                /// </summary>
                Response,

                /// <summary>
                /// The <c>notification action</c>
                /// </summary>
                Notification
            }
        }


        

        
        /// <summary>
        /// Context: Purpose types
        /// </summary>
        public class Purpose
        {
            /// <summary>
            /// Name for the <c>purpose</c> header
            /// </summary>
            public const string Label = "purpose";

            /// <summary>
            /// <c>purpose-name</c> value of "treatment"
            /// </summary>
            public const string PurposeTreatment = "treatment";
            /// <summary>
            /// <c>purpose-name</c> value of "payment"
            /// </summary>
            public const string PurposePayment = "payment";
            /// <summary>
            /// <c>purpose-name</c> value of "operations"
            /// </summary>
            public const string PurposeOperations = "operations";
            /// <summary>
            /// <c>purpose-name</c> value of "emergency"
            /// </summary>
            public const string PurposeEmergency = "emergency";
            /// <summary>
            /// <c>purpose-name</c> value of "research"
            /// </summary>
            public const string PurposeResearch = "research";


            /// <summary>
            /// List of purpose names
            /// </summary>
            public enum PurposeName
            {
                /// <summary>
                /// Unknown.
                /// </summary>
                Unknown = 0,

                /// <summary>
                /// The <c>treatment purpose-name</c>
                /// </summary>
                Treatment,

                /// <summary>
                /// The <c>payment purpose-name</c>
                /// </summary>
                Payment,

                /// <summary>
                /// The <c>operations purpose-name</c>
                /// </summary>
                Operations,

                /// <summary>
                /// The <c>emergency purpose-name</c>
                /// </summary>
                Emergency,

                /// <summary>
                /// The <c>research purpose-name</c>
                /// </summary>
                Research
            }
        }

        /// <summary>
        /// 3.6 Patient Matching Paramters defined. 
        /// </summary>
        public class Patient
        {
            /// <summary>
            /// Name for the <c>patient</c> header
            /// </summary>
            public const string Label = "patient";

            /// <summary>
            /// <c>patient-parameter</c> value of "givenName"
            /// </summary>
            public const string PatientGivenName = "givenName";
            /// <summary>
            /// <c>patient-parameter</c> value of "surname"
            /// </summary>
            public const string PatientSurName = "surname";
            /// <summary>
            /// <c>patient-parameter</c> value of "middleName"
            /// </summary>
            public const string PatientMiddleName = "middleName";
            /// <summary>
            /// <c>patient-parameter</c> value of "dateOfBirth"
            /// </summary>
            public const string PatientDateOfBirth = "dateOfBirth";
            /// <summary>
            /// <c>patient-parameter</c> value of "gender"
            /// </summary>
            public const string PatientGender = "gender";
            /// <summary>
            /// <c>patient-parameter</c> value of "socialSecurityNumber"
            /// </summary>
            public const string PatientSocialSecurityNumber = "socialSecurityNumber";
            /// <summary>
            /// <c>patient-parameter</c> value of "telephoneNumber"
            /// </summary>
            public const string PatientTelephoneNumber = "telephoneNumber";
            /// <summary>
            /// <c>patient-parameter</c> value of "streetAddress"
            /// </summary>
            public const string PatientStreetAddress = "streetAddress";
            /// <summary>
            /// <c>patient-parameter</c> value of "postalCode"
            /// </summary>
            public const string PatientPostalCode = "postalCode";

            /// <summary>
            /// patient-parameter-value 
            /// </summary>
            public enum PatientParameter
            {
                /// <summary>
                /// Unknown.
                /// </summary>
                Unknown = 0,

                /// <summary>
                /// The <c>givenName patient-parameter</c>
                /// </summary>
                GivenName,

                /// <summary>
                /// The <c>surname patient-parameter</c>
                /// </summary>
                Surname,

                /// <summary>
                /// The <c>middleName patient-parameter</c>
                /// </summary>
                MiddleName,

                /// <summary>
                /// The <c>dateOfBirth patient-parameter</c>
                /// </summary>
                DateOfBirth,

                /// <summary>
                /// The <c>gender patient-parameter</c>
                /// </summary>
                Gender,

                /// <summary>
                /// The <c>socialSecurityNumber patient-parameter</c>
                /// </summary>
                SocialSecurityNumber,

                /// <summary>
                /// The <c>telephoneNumber patient-parameter</c>
                /// </summary>
                TelephoneNumber,

                /// <summary>
                /// The <c>streetAddress patient-parameter</c>
                /// </summary>
                StreetAddress,

                /// <summary>
                /// The <c>postalCode patient-parameter</c>
                /// </summary>
                PostalCode
            }
        }
        
        
        /// <summary>
        /// List of possible <c>encapsulated-message-type</c>s
        /// </summary>
        public class Encapsulation
        {
            /// <summary>
            /// Name for the <c>encapsulation</c> header
            /// </summary>
            public const string Label = "encapsulation";


            /// <summary>
            /// The <c>http encapsulation-message-type</c>
            /// </summary>
            public const string Http = "http";

            /// <summary>
            /// The <c>hl7v2 encapsulation-message-type</c>
            /// </summary>
            public const string Hl7V2 = "hl7v2";

            /// <summary>
            /// encapsulated-message-type
            /// </summary>
            public enum Type
            {
                /// <summary>
                /// The <c>http encapsulated-message-type </c>
                /// </summary>
                Http,
                /// <summary>
                /// The <c>hl7v2 encapsulated-message-type </c>
                /// </summary>
                Hl7V2
            }
        }
    }
}
