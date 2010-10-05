package org.nhindirect.transform;

import java.io.File;
import java.util.Collection;

public interface DocumentXdmTransformer
{
    public File transform(Collection<String> docs, String suffix, byte[] meta, String messageId);

    public File transform(Collection<String> docs, String suffix, byte[] meta);
}
