package org.codehaus.mojo.truezip;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.io.ArchiveException;
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
 * UMount an archive or all
 * 
 * @goal umount
 * @phase process-resources
 * @version $Id:  $
 * @author Dan T. Tran
 */
public class UMountMojo
    extends AbstractArchiveMojo
{

    /**
     * The archive file to be manipulated.
     * @parameter
     * @since 1.0-alpha-1
     */
    private java.io.File archiveFile;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        try
        {
            if ( archiveFile != null )
            {
                File.umount( new File( archiveFile ) );
            }
            else
            {
                File.umount();
            }
        }
        catch ( ArchiveException e )
        {
            throw new MojoFailureException( e.getMessage() );
        }
    }
}
