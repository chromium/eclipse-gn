/*
 * Copyright (c) 2014, The Chromium Authors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chromium.gnsupport.internal.core.tree;

import java_cup.runtime.Symbol;

import org.chromium.gnsupport.core.GnCorePlugin;
import org.chromium.gnsupport.core.ast.GnSymbolFactory;
import org.chromium.gnsupport.internal.core.parser.GnParser;
import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

import java.io.StringReader;

public class GnSourceParser extends AbstractSourceParser {

  @Override
  public IModuleDeclaration parse(IModuleSource source, IProblemReporter reporter) {
    GnParser parser = new GnParser(
        new StringReader(source.getSourceContents()), new GnSymbolFactory(),
        source.getFileName(), reporter);
    try {
      Symbol module;
      if (GnCorePlugin.getDefault().isDebugging()) {
        module = parser.debug_parse();
      } else {
        module = parser.parse();
      }
      ModuleDeclaration file = new ModuleDeclaration(source.getSourceContents().length());
      if (module.value != null) {
        file.setStatements(((ASTListNode) module.value).getChilds());
      }
      return file;
    } catch (Exception e) {
      GnCorePlugin.log(e);
    }
    return new ModuleDeclaration(0);
  }
}
