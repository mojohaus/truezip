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
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.schlichtherle.truezip.file.TFile;

/**
 * Copy an archive/directory to another archive/directory. Mainly used from command line to unpack/pack any known
 * archive type.
 * <p>
 * Example:
 * <ul>
 * <li>mvn truezip:cp -Dfrom=a.zip -Dto=b</li>
 * <li>mvn truezip:cp -Dfrom=b -Dto=b.zip</li>
 * </ul>
 * </p>
 * 
 * @goal cp
 * @requiresProject false
 * @version $Id: $
 */
public class CliCopyMojo
    extends AbstractArchiveMojo
{
    /**
     * Path to an archive to be unpacked.
     * 
     * @parameter property="from"
     * @required
     * @since 1.0 beta-4
     */
    private File from;

    /**
     * Path to an archive or directory to unpack to.
     * 
     * @parameter property="to"
     * @required
     * @since 1.0 beta-4
     */
    private File to;

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

        try
        {
            truezip.copyFile( new TFile( from ), new TFile( to ) );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to copy: " + from + " to " + to, e );
        }

        this.tryImmediateUpdate();

    }

}
