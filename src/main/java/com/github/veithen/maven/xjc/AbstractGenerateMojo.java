/*-
 * #%L
 * xjc-maven-plugin
 * %%
 * Copyright (C) 2018 - 2020 Andreas Veithen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.github.veithen.maven.xjc;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;

public abstract class AbstractGenerateMojo extends AbstractMojo {
    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    @Parameter private Language schemaLanguage = Language.XMLSCHEMA;

    @Parameter(required = true)
    private File[] files;

    /** Corresponds to the {@code -p} option. */
    @Parameter private String packageName;

    @Parameter(required = true, defaultValue = "${project.build.sourceEncoding}")
    private String outputEncoding;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        final Log log = getLog();
        File outputDirectory = getOutputDirectory();
        Options options = new Options();
        options.setSchemaLanguage(schemaLanguage);
        options.defaultPackage = packageName;
        for (File file : files) {
            options.addGrammar(file);
        }
        ErrorReceiver errorReceiver =
                new ErrorReceiver() {
                    @Override
                    public void fatalError(SAXParseException exception) {
                        log.error(exception.getMessage());
                        log.debug(exception);
                    }

                    @Override
                    public void error(SAXParseException exception) {
                        log.error(exception.getMessage());
                        log.debug(exception);
                    }

                    @Override
                    public void warning(SAXParseException exception) {
                        log.warn(exception.getMessage());
                        log.debug(exception);
                    }

                    @Override
                    public void info(SAXParseException exception) {
                        log.info(exception.getMessage());
                        log.debug(exception);
                    }
                };
        Model model = ModelLoader.load(options, new JCodeModel(), errorReceiver);
        if (model == null) {
            throw new MojoExecutionException("Code generation failed");
        }
        Outline outline = model.generateCode(options, errorReceiver);
        if (outline == null) {
            throw new MojoExecutionException("Code generation failed");
        }
        outputDirectory.mkdirs();
        try {
            CodeWriter codeWriter = new FileCodeWriter(outputDirectory, outputEncoding);
            model.codeModel.build(codeWriter);
        } catch (IOException ex) {
            throw new MojoFailureException("Failed to write code", ex);
        }
        addSourceRoot(project, outputDirectory.getAbsolutePath());
    }

    protected abstract File getOutputDirectory();

    protected abstract void addSourceRoot(MavenProject project, String path);
}
