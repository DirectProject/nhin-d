package org.nhindirect.dns.tools;

import java.util.Collection;

import org.nhind.config.DnsRecord;

/**
 * Interface for printing DNS records to an output Stream.
 * @author Greg Meyer
 *
 * @since 1.0
 */
public interface DNSRecordPrinter 
{
    /**
     * Prints the contents of a collection of DNS records.
     * @param records A collection of DNS records to print.
     * 
     * @since 1.0
     */
    public void print(Collection<DnsRecord> records);
    
    /**
     * Prints the contents of an array of DNS records.
     * @param records An array of DNS records to print.
     * 
     * @since 1.0
     */  
    public void print(DnsRecord[] records);
    
    /**
     * Prints the contents of a single DNS records.
     * @param record DNS records to print.
     * 
     * @since 1.0
     */    
    public void print(DnsRecord record);
}
