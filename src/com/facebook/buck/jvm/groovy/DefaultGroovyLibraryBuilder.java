/*
 * Copyright 2017-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.jvm.groovy;

import com.facebook.buck.jvm.java.CompileToJarStepFactory;
import com.facebook.buck.jvm.java.DefaultJavaLibraryBuilder;
import com.facebook.buck.jvm.java.JavaLibraryDescription;
import com.facebook.buck.jvm.java.JavacOptions;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Optional;


class DefaultGroovyLibraryBuilder extends DefaultJavaLibraryBuilder {
  private final GroovyBuckConfig groovyBuckConfig;
  private ImmutableList<String> extraGroovycArguments = ImmutableList.of();

  protected DefaultGroovyLibraryBuilder(
      BuildRuleParams params,
      BuildRuleResolver buildRuleResolver,
      JavacOptions javacOptions,
      GroovyBuckConfig groovyBuckConfig) {
    super(params, buildRuleResolver);
    this.groovyBuckConfig = groovyBuckConfig;
    setJavacOptions(javacOptions);
    setSuggestDependencies(groovyBuckConfig.shouldSuggestDependencies());
  }

  @Override
  public DefaultGroovyLibraryBuilder setArgs(JavaLibraryDescription.Arg args) {
    super.setArgs(args);
    GroovyLibraryDescription.Arg groovyArgs = (GroovyLibraryDescription.Arg) args;
    extraGroovycArguments = groovyArgs.extraGroovycArguments;
    return this;
  }

  @Override
  protected BuilderHelper newHelper() {
    return new BuilderHelper();
  }

  protected class BuilderHelper extends DefaultJavaLibraryBuilder.BuilderHelper {
    @Override
    protected CompileToJarStepFactory buildCompileStepFactory() {
      return new GroovycToJarStepFactory(
          Preconditions.checkNotNull(groovyBuckConfig).getGroovyCompiler().get(),
          Optional.of(extraGroovycArguments),
          Preconditions.checkNotNull(javacOptions));
    }
  }
}
