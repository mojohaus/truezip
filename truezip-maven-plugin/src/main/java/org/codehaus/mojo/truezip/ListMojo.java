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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import de.schlichtherle.truezip.file.TFile;

/**
 * List all files in the archive.
 * 
 * @goal list
 * @phase process-resources
 * @version $Id: $
 */
public class ListMojo
    extends AbstractManipulateArchiveMojo
{

    /**
     * Write list output to a file if needed.
     * 
     * @parameter
     * @since 1.0 beta-1
     */
    private File outputFile;

    /**
     * Use full path to display the list. Useful to get a relative content per fileset.
     * 
     * @parameter default-value="true"
     * @since 1.1
     */
    private boolean printFullPath = true;

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

        PrintStream ps = System.out;

        OutputStream os = null;

        if ( this.outputFile != null )
        {
            try
            {
                outputFile.getParentFile().mkdirs();
                os = new FileOutputStream( outputFile );
                ps = new PrintStream( os );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( e.getMessage() );
            }
        }

        try
        {
            this.processFileSets( ps );
        }
        finally
        {
            IOUtil.close( os );
        }

        this.tryImmediateUpdate();

    }

    private void processFileSets( PrintStream ps )
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

            this.resolveRelativePath( fileSet );

            List<TFile> fileList = this.truezip.list( this.filesets.get( i ) );

            for ( int j = 0; j < fileList.size(); ++j )
            {
                TFile file = fileList.get( j );
                if ( printFullPath )
                {
                    ps.println( ( file.getPath() ) );
                }
                else
                {
                    int startPos = fileSet.getDirectory().length() + 1;
                    ps.println( ( file.getPath().substring( startPos ) ) );
                }

            }
        }
    }
}
