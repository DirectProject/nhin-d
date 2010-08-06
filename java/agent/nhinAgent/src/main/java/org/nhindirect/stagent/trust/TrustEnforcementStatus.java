package org.nhindirect.stagent.trust;

/**
 * Enumeration of statuses of a trust operation.
 * @author Greg Meyer
 * @author Umesh Madan
 *
 */
public enum TrustEnforcementStatus 
{
    Failed,
    Unknown,
    Success_Offline,            // Signature valid, signing cert is trusted, but could not retrieve cert directly from source
    Success_ThumbprintMismatch, // Signature valid, signing cert is trusted, but the signing cert and the source cert did not match
    Success                     // Signature valid, siging cert trusted, and certs match perfectly
}
