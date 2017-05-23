/* 
 Copyright (c) 2016, Direct Project
 All rights reserved.

 Authors:
    Joe Shook      Joseph.Shook@Surescripts.com
  
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
Neither the name of The Direct Project (directproject.org) nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/


using System;
using System.Linq;
using System.Security.Cryptography.Pkcs;
using System.Security.Cryptography.X509Certificates;
using Health.Direct.Common.Container;
using Health.Direct.Common.Cryptography;
using Health.Direct.Common.Extensions;
using Health.Direct.Common.Mail;
using Health.Direct.Common.Mime;
using Health.Direct.Config.Client.SettingsManager;
using Health.Direct.Config.Store;

namespace Health.Direct.Hsm
{

    /// <summary>
    /// This plugin resolver actually loads a <see cref="HsmCryptographer"/>... and proxies calls to it (See Init method)
    /// </summary>
    public class HsmCryptographerProxy : ISmimeCryptographer, IPlugin, IDisposable
    {
        HsmCryptographer m_innerHsmCryptographer;
        ISmimeCryptographer m_innerSoftwareCryptographer;

        /// <summary>
        /// Flag indicating whether instance has been disposed
        /// </summary>
        private bool m_disposed;

        /// <inheritdoc />
        public event Action<ISmimeCryptographer, Exception> Error
        {
            add
            {
                m_innerHsmCryptographer.Error += value;
            }
            remove
            {
                m_innerHsmCryptographer.Error -= value;
            }
        }

        /// <inheritdoc />
        public event Action<ISmimeCryptographer, string> Warning
        {
            add
            {
                m_innerHsmCryptographer.Warning += value;
                ProxyWarning += value;
            }
            remove
            {
                m_innerHsmCryptographer.Warning -= value;
                ProxyWarning -= value;
            }
        }

        /// <summary>
        /// Specific to this proxy.  
        /// Used for testing before the <see cref="HsmCryptographer"/> is created.
        /// </summary>
        public event Action<ISmimeCryptographer, Exception> ProxyError;

        /// <summary>
        /// Used for testing and for cut over notification.
        /// Notifies when a decrypting with a cut over soft cert during the transition time from soft to hard certs.
        /// </summary>
        public event Action<ISmimeCryptographer, string> ProxyWarning;


        /// <summary>
        /// Required default constructor to be activated as a plugin.
        /// </summary>
        public HsmCryptographerProxy()
        {
        }

        /// <summary>
        /// Do not use this accessor accept for testing.
        /// </summary>
        public TokenSettings TokenSettings
        {
            get { return m_innerHsmCryptographer.TokenSettings; }
            set { m_innerHsmCryptographer.TokenSettings = value; }
        }

        #region IPlugin Members

        public ISmimeCryptographer DefaultCryptographer
        {
            get { return m_innerSoftwareCryptographer; }
            set { m_innerSoftwareCryptographer = value; }
        }

        /// <summary>
        /// Initialize cryptographer from <see cref="TokenSettings"/>
        /// <remarks>
        /// Setting the <see cref="DefaultCryptographer"/> to the Direct Project's <see cref="SMIMECryptographer"/>
        /// </remarks>
        /// </summary>
        /// <param name="pluginDef"></param>
        public void Init(PluginDefinition pluginDef)
        {
            try
            {
                var settings = pluginDef.DeserializeSettings<TokenResolverSettings>();
                Init(settings);
            }
            catch (Exception ex)
            {
                ProxyError.NotifyEvent(this, ex);
                // Do not remove. Exceptions here can cause the Direct Project to not bind to SMTP. 
            }
        }

        public void Init(TokenResolverSettings resolverSettings)
        {
            try
            {
                IPropertyManager client = resolverSettings.ClientSettings.CreatePropertyManagerClient();
                Property[] properties = client.GetProperties(new [] {"TokenSettings"});
                string tokenSettingsXml = properties.SingleOrDefault().Value;
                var tokenSettings = tokenSettingsXml.FromXml<TokenSettings>();

                m_innerSoftwareCryptographer = new SMIMECryptographer(
                    tokenSettings.DefaultEncryption,
                    tokenSettings.DefaultDigest);

                tokenSettings.Error += ProxyError;
                m_innerHsmCryptographer = tokenSettings.Create();
            }
            catch (Exception ex)
            {
                ProxyError.NotifyEvent(this, ex);
                // Do not remove. Exceptions here can cause the Direct Project to not bind to SMTP.  
            }
        }

        #endregion
        
        /// <inheritdoc />
        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2 encryptingCertificate)
        {
            return m_innerSoftwareCryptographer.Encrypt(entity, encryptingCertificate);
        }

        /// <inheritdoc />
        public MimeEntity Encrypt(MimeEntity entity, X509Certificate2Collection encryptingCertificates)
        {
            return m_innerSoftwareCryptographer.Encrypt(entity, encryptingCertificates);
        }

        /// <inheritdoc />
        public MimeEntity DecryptEntity(byte[] encryptedBytes, X509Certificate2 decryptingCertificate)
        {
            MimeEntity mimeEntity = null;
            
            if (m_innerHsmCryptographer == null)
            {
                throw new NullReferenceException("Missing HSMCryptographer");
            }

            mimeEntity = m_innerHsmCryptographer.DecryptEntity(encryptedBytes, decryptingCertificate);

            if (mimeEntity == null && 
                decryptingCertificate != null && 
                decryptingCertificate.HasPrivateKey)
            {
                mimeEntity = CutOverDecryption(encryptedBytes, decryptingCertificate);
            }

            return mimeEntity;
        }

        /// <summary>
        /// If the HSM cannot decrypt it may be because an existing software based certificate 
        /// cached in DNS was used for the original encryption.
        /// </summary>
        /// <param name="encryptedBytes"></param>
        /// <param name="decryptingCertificate"></param>
        /// <returns></returns>
        private MimeEntity CutOverDecryption(byte[] encryptedBytes, 
            X509Certificate2 decryptingCertificate)
        {
            ProxyWarning.NotifyEvent(this, "Cutover to Soft SMIMECryptographer started...");

            var mimeEntity = m_innerSoftwareCryptographer
                .DecryptEntity(encryptedBytes, decryptingCertificate);

            if (mimeEntity != null)
            {
                ProxyWarning.NotifyEvent(this, "Cutover succeeded.");
            }
            
            return mimeEntity;
        }

        /// <inheritdoc />
        public SignedEntity Sign(MimeEntity entity, X509Certificate2 signingCertificate)
        {
            if (m_innerHsmCryptographer == null)
            {
                throw new NullReferenceException("Missing HSMCryptographer");
            }

            return m_innerHsmCryptographer.Sign(entity, signingCertificate);
        }

        /// <inheritdoc />
        public SignedEntity Sign(Message message, X509Certificate2Collection signingCertificates)
        {
            if (m_innerHsmCryptographer == null)
            {
                throw new NullReferenceException("Missing HSMCryptographer");
            }
 
            return m_innerHsmCryptographer.Sign(message, signingCertificates);
        }

        /// <inheritdoc />
        public SignedEntity Sign(MimeEntity entity, X509Certificate2Collection signingCertificates)
        {
            if (m_innerHsmCryptographer == null)
            {
                throw new NullReferenceException("Missing HSMCryptographer");
            }

            return m_innerHsmCryptographer.Sign(entity, signingCertificates);
        }

        /// <summary>
        /// Transforms a <see cref="SignedEntity"/> to the associated <see cref="SignedCms"/> instance.
        /// SignedCms is in PKCS#7 format.
        /// </summary>
        /// <param name="entity">The <see cref="SignedEntity"/> to deserialize</param>
        /// <returns>The corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeDetachedSignature(SignedEntity entity)
        {
            return m_innerSoftwareCryptographer.DeserializeDetachedSignature(entity);
        }

        /// <summary>
        /// Tranforms an enveloped signature to the corresponding <see cref="SignedCms"/>.
        /// SignedCms is in PKCS#7 format.
        /// </summary>
        /// <param name="envelopeEntity">The entity containing the enveloped signature</param>
        /// <returns>the corresponding <see cref="SignedCms"/></returns>
        public SignedCms DeserializeEnvelopedSignature(MimeEntity envelopeEntity)
        {
            return m_innerSoftwareCryptographer.DeserializeEnvelopedSignature(envelopeEntity);
        }

        /// <inheritdoc />
        public byte[] GetEncryptedBytes(MimeEntity encryptedEntity)
        {
            return m_innerSoftwareCryptographer.GetEncryptedBytes(encryptedEntity);
        }

        /// <inheritdoc />
        public EncryptionAlgorithm EncryptionAlgorithm
        {
            get { return m_innerHsmCryptographer.EncryptionAlgorithm; }
            set { m_innerHsmCryptographer.EncryptionAlgorithm = value; }
        }

        /// <inheritdoc />
        public DigestAlgorithm DigestAlgorithm
        {
            get { return m_innerHsmCryptographer.DigestAlgorithm; }
            set { m_innerHsmCryptographer.DigestAlgorithm = value; }
        }

        /// <inheritdoc />
        public bool IncludeMultipartEpilogueInSignature
        {
            get { return m_innerHsmCryptographer.IncludeMultipartEpilogueInSignature; }
            set { m_innerHsmCryptographer.IncludeMultipartEpilogueInSignature = value; }
        }

        /// <inheritdoc />
        public X509IncludeOption IncludeCertChainInSignature
        {
            get { return m_innerHsmCryptographer.IncludeCertChainInSignature; }
            set { m_innerHsmCryptographer.IncludeCertChainInSignature = value; }
        }

        /// <inheritdoc />
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!m_disposed)
            {
                if (disposing && m_innerHsmCryptographer != null)
                {
                    (m_innerHsmCryptographer).Dispose();
                }
            }

            // Dispose unmanaged objects
            m_disposed = true;
        }

        /// <summary>
        /// Class destructor that disposes object if caller forgot to do so
        /// </summary>
        ~HsmCryptographerProxy()
        {
            Dispose(false);
        }
    }
}
