/**
 * Depending on security and operational requirements, the use of "active" asymmetric keys may not be allowed in process memmory.  Generally
 * this is supported by default in the BouncyCastle libraries by directing the cryptographic operations to a specific JCE provider, however
 * it is not always necessary (or optimal) to direct all operations to the same provider.  For example, if a JCE provider is configured to perform asymmetric
 * decryption operations in a PCKS11 token, the BouncyCastle libraries would also direct the symmetric decryption to the token.  For large messages, this
 * could create a performance bottle neck by transferring the entire message content to the token.  In this situation, the only sensitive key
 * material that needs extra protection is the asymmetric private key; the decryption operations using the message symetric key could be done
 * in process.  Similar statements could be said for digital signature operations.
 * <p>
 * The classes and interfaces in this package enable the ability the delegate asymmetric key operations and non-ssasymmetric key operations to 
 * different JCE providers.  This allows for optimized operations that may otherwise lead to performance bottlenecks.
 */
package org.nhindirect.stagent.cryptography.activekeyops;