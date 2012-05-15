package org.codehaus.mojo.truezip.internal;

import javax.annotation.PostConstruct;

import org.codehaus.mojo.truezip.TrueZipArchiveDetector;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.fs.archive.tar.TarBZip2Driver;
import de.schlichtherle.truezip.fs.archive.tar.TarDriver;
import de.schlichtherle.truezip.fs.archive.tar.TarGZipDriver;
import de.schlichtherle.truezip.fs.archive.zip.JarDriver;
import de.schlichtherle.truezip.fs.archive.zip.ZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;

public class DefaultTrueZipArchiveDetector
    implements TrueZipArchiveDetector
{

    @PostConstruct
    public void init()
    {

        TConfig.get()
            .setArchiveDetector( new TArchiveDetector( TArchiveDetector.NULL, new Object[][] {
                                     { "tar|ova", new TarDriver( IOPoolLocator.SINGLETON ) },
                                     { "tgz|tar.gz", new TarGZipDriver( IOPoolLocator.SINGLETON ) },
                                     { "tbz2|tar.bz2", new TarBZip2Driver( IOPoolLocator.SINGLETON ) },
                                     { "zip", new ZipDriver( IOPoolLocator.SINGLETON ) },
                                     { "jar|war|ear|sar|swc|nar|esb|par", new JarDriver( IOPoolLocator.SINGLETON ) }, } ) );
    }
}
