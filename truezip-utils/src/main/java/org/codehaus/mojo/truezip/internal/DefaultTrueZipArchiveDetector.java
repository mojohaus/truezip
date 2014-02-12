package org.codehaus.mojo.truezip.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

        TConfig.get().setArchiveDetector( new TArchiveDetector( TArchiveDetector.NULL, new Object[][] {
                                              { "tar|ova", new TarDriver( IOPoolLocator.SINGLETON ) },
                                              { "tgz|tar.gz", new TarGZipDriver( IOPoolLocator.SINGLETON ) },
                                              { "tbz2|tar.bz2", new TarBZip2Driver( IOPoolLocator.SINGLETON ) },
                                              { "zip|kar", new ZipDriver( IOPoolLocator.SINGLETON ) },
                                              { "jar|war|ear|sar|swc|nar|esb|par",
                                                  new JarDriver( IOPoolLocator.SINGLETON ) }, } ) );
    }
}
