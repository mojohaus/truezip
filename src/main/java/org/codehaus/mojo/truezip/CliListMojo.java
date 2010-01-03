package org.codehaus.mojo.truezip;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.io.File;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Display an archive's list to console. Note: inner archive file length always show 0 byte long. See TrueZip java doc for details.
 * 
 * <p>Example: 
 *   <ul>
 *     <li>mvn truezip:ls -Dfrom=a.zip </li>
 *   </ul>
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
     * Path to an archive file to display
     * 
     * @parameter expression="${from}"
     * @required
     * @since beta-4
     */
    private java.io.File from;

    /**
     * Drill beyond sub archive
     * 
     * @parameter expression="${followSubArchive}" default-value="false"
     * @required
     * @since beta-4
     */
    private boolean followSubArchive;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        Fileset fileset = new Fileset();
        fileset.setDirectory( from.getAbsolutePath() );
        fileset.setFollowArchive( followSubArchive );

        List fileList = truezip.list( fileset );

        Iterator iter = fileList.iterator();

        while ( iter.hasNext() )
        {
            File archiveFile = new File( iter.next().toString() );

            String relativePath = archiveFile.getPath();
            if ( relativePath.startsWith( from.getPath() ) )
            {
                relativePath = relativePath.substring( from.getPath().length() + 1 );
            }

            long fileLen = archiveFile.length();

            System.out.println( fileLen + "\t" + new Date( archiveFile.lastModified() ) + "\t" + relativePath );
        }
    }

}
