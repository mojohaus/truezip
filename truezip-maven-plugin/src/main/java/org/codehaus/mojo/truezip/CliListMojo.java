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

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.truezip.file.TFile;

/**
 * Display an archive's list to console. Note: inner archive file length always show 0 byte long. See TrueZip javadoc
 * for details.
 * <p>
 * Example:
 * <ul>
 * <li>mvn truezip:ls -Dfrom=a.zip</li>
 * </ul>
 * </p>
 * 
 * @goal ls
 * @requiresProject false
 * @version $Id: $
 */
public class CliListMojo
    extends AbstractArchiveMojo
{
    /**
     * Path to an archive file to display.
     * 
     * @parameter property="from"
     * @required
     * @since 1.0 beta-4
     */
    private File from;

    /**
     * Drill beyond sub archive.
     * 
     * @parameter property="followSubArchive" default-value="false"
     * @required
     * @since 1.0 beta-4
     */
    private boolean followSubArchive;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        if ( skip )
        {
            this.getLog().info( "Skip this execution" );
            return;
        }

        super.execute();

        intitializeArchiveDectector();

        Fileset fileset = new Fileset();
        fileset.setDirectory( from.getAbsolutePath() );
        fileset.setFollowArchive( followSubArchive );

        List<TFile> fileList = truezip.list( fileset );

        Iterator<TFile> iter = fileList.iterator();

        while ( iter.hasNext() )
        {
            TFile archiveFile = new TFile( iter.next().toString() );

            String relativePath = archiveFile.getPath();
            if ( relativePath.startsWith( from.getPath() ) )
            {
                relativePath = relativePath.substring( from.getPath().length() + 1 );
            }

            long fileLen = archiveFile.length();

            System.out.println( fileLen + "\t" + new Date( archiveFile.lastModified() ) + "\t" + relativePath );
        }

        this.tryImmediateUpdate();

    }

}
