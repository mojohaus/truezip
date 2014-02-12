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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.truezip.file.TFile;

/**
 * Copy a set of files in and out of an existing archive.
 * 
 * @goal copy
 * @phase process-resources
 * @version $Id: $
 */
public class CopyMojo
    extends AbstractManipulateArchiveMojo
{

    /**
     * The list of FileItem to manipulate the archive with. Use this configuration when you have a need to do copying
     * with the option to change the file name.
     * 
     * @parameter
     * @since 1.0 beta-1
     */
    private FileItem[] files;

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

        this.processFileItems();

        this.processFileSets();

        this.tryImmediateUpdate();
    }

    private void processFileSets()
        throws MojoExecutionException, MojoFailureException
    {
        if ( this.fileset != null )
        {
            this.filesets.add( this.fileset );
            this.fileset = null;
        }

        for ( int i = 0; i < filesets.size(); ++i )
        {
            Fileset fileSet = (Fileset) this.filesets.get( i );

            if ( StringUtils.isBlank( fileSet.getDirectory() ) )
            {
                fileSet.setDirectory( this.project.getBasedir().getAbsolutePath() );
            }

            try
            {
                this.resolveRelativePath( fileSet );
                this.truezip.copy( fileSet );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Copy fileset fails", e );
            }
        }
    }

    private void processFileItems()
        throws MojoExecutionException, MojoFailureException
    {
        for ( int i = 0; files != null && i < files.length; ++i )
        {
            FileItem copyInfo = files[i];

            this.resolveRelativePath( copyInfo );

            TFile source = new TFile( copyInfo.getSource() );

            TFile dest = new TFile( copyInfo.getDestinationPath() );

            try
            {
                this.truezip.copyFile( source, dest );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Copy fileset fails", e );
            }
        }

    }

}
