package org.nhindirect.stagent.trust;

import java.util.Collection;

import javax.mail.internet.InternetAddress;
import java.security.cert.X509Certificate;

/**
 * A trust setting store contains certificate anchors that assert the trust policy of a recicient's or sender's certificate.  A certificate
 * must have a anchor in its certificate chain that is contained in this store to be considered trusted.  Certificate anchors can be specific
 * to an InternetAddress.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public interface ITrustSettingsStore 
{
	/**
	 * Gets the collection of trust anchor certificates for an InternetAddress.
	 * @param address The address to get the specific list of anchors for.
	 * @return A collection of certificate anchors.
	 */
    Collection<X509Certificate> getTrustAnchorsIncoming(InternetAddress address);

	/**
	 * Gets the collection of trust anchor certificates for an InternetAddress.
	 * @param address The address to get the specific list of anchors for.
	 * @return A collection of certificate anchors.
	 */    
    Collection<X509Certificate> getTrustAnchorsOutgoing(InternetAddress address);
}
