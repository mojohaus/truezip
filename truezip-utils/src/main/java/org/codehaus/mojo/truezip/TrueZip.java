package org.codehaus.mojo.truezip;

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

import java.io.IOException;
import java.util.List;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.fs.FsSyncException;

/**
 * Generic interface to manipulate recognizable archives
 */
public interface TrueZip
{
    /**
     * List the file from FileSet's configuration
     *
     * @param fileSet
     * @return List of TFile
     */
    List<TFile> list( TrueZipFileSet fileSet );

    /**
     * Copy a set of file to another archive using FileSet configuration
     *
     * @param oneFileSet
     * @throws IOException
     */
    void copy( TrueZipFileSet oneFileSet )
        throws IOException;

    /**
     * Copy a file or archive to another destination. Hash values of source's sub-archive(s) are not kept intact during
     * copy. Use copy() interface instead of if needed
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    void copyFile( TFile source, TFile dest )
        throws IOException;

    /**
     * Copy a file for MOJO
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    void moveFile( TFile source, TFile dest )
        throws IOException;

    /**
     * Move a set of files from one archive to another
     *
     * @param oneFileSet - The archive setup
     * @throws IOException
     */
    void move( TrueZipFileSet oneFileSet )
        throws IOException;

    /**
     * Remove a set of files from the archive setup
     *
     * @param oneFileSet - the archive setup
     * @throws IOException
     */
    void remove( TrueZipFileSet oneFileSet )
        throws IOException;

    /**
     * Global sync
     *
     * @throws FsSyncException
     */
    void sync()
        throws FsSyncException;

    /**
     * Selectively sync
     *
     * @param file
     * @throws FsSyncException
     */
    void sync( TFile file )
        throws FsSyncException;

}
