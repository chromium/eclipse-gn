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
package org.chromium.gnsupport.internal.core.search;

import org.chromium.gnsupport.core.ast.GnFieldDeclaration;
import org.chromium.gnsupport.core.ast.GnFunctionCall;
import org.chromium.gnsupport.core.ast.GnMethodDeclaration;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.references.Reference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocatorParser;
import org.eclipse.dltk.core.search.matching.PatternLocator;

import java.util.List;

public class GnMatchLocatorParser extends MatchLocatorParser {

  protected GnMatchLocatorParser(MatchLocator locator) {
    super(locator);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void processStatement(ASTNode node, PatternLocator locator) {
    if (node == null) {
      return;
    }

    if (node instanceof GnFunctionCall) {
      locator.match((CallExpression) node, this.getNodeSet());
    } else if (node instanceof GnMethodDeclaration) {
      GnMethodDeclaration decl = (GnMethodDeclaration) node;
      switch (decl.getType()) {
        case TEMPLATE_CALL:
          SimpleReference ref = decl.getRef();
          List<ASTNode> args = decl.getArguments();
          CallArgumentsList argList;
          String targetName = null;
          if (args.isEmpty()) {
            argList = new CallArgumentsList();
          } else {
            argList = new CallArgumentsList(args.get(0).start(), args.get(args.size() - 1).end());
            targetName = args.get(0).toString();
          }
          for (ASTNode arg : args) {
            argList.addNode(arg);
          }
          CallExpression call =
              new CallExpression(ref.sourceStart(), ref.sourceEnd(), null, ref, argList);
          locator.match(call, this.getNodeSet());

          if (targetName != null) {
            addMethodDeclarationByArgument(locator, decl);
          }
          break;
        case TEMPLATE:
        case TARGET:
          boolean added = addMethodDeclarationByArgument(locator, decl);
          if (!added) {
            locator.match((MethodDeclaration) node, this.getNodeSet());
          }
        default:
          locator.match((MethodDeclaration) node, this.getNodeSet());
      }
    } else if (node instanceof GnFieldDeclaration) {
      locator.match((FieldDeclaration) node, this.getNodeSet());
    } else if (node instanceof VariableReference) {
      locator.match((Reference) node, this.getNodeSet());
    }
  }

  private boolean addMethodDeclarationByArgument(PatternLocator locator, GnMethodDeclaration decl) {
    ASTNode argNode = decl.getFirstArgument();
    if (argNode != null && argNode instanceof StringLiteral) {
      MethodDeclaration fakeDecl = new MethodDeclaration(
          ((StringLiteral) argNode).getValue(), argNode.start(), argNode.end(),
          argNode.start(), argNode.end());
      locator.match(fakeDecl, this.getNodeSet());
      return true;
    }
    return false;
  }
}
