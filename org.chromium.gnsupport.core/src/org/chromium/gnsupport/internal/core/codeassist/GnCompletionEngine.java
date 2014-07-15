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
package org.chromium.gnsupport.internal.core.codeassist;

import org.chromium.gnsupport.core.GnCodeUtil;
import org.chromium.gnsupport.core.GnCorePlugin;
import org.chromium.gnsupport.core.ast.GnAssignmentExpression;
import org.chromium.gnsupport.internal.core.tree.AstUtil;
import org.chromium.gnsupport.internal.core.tree.GnSourceParser;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.codeassist.RelevanceConstants;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.ModelException;

import java.util.HashSet;
import java.util.Set;

public class GnCompletionEngine extends ScriptCompletionEngine {
  private static final IProblemReporter NULL_PROBLEM_REPORTER = new IProblemReporter() {
    @Override
    public void reportProblem(IProblem problem) {}
  };
  private static final int METHOD_RELEVANCE = 10;
  private static final int FIELD_RELEVANCE = 20;

  private int targetElementType;
  private Set<String> knownCompletions;

  @Override
  public void complete(IModuleSource module, int position, int pos) {
    this.actualCompletionPosition = position;
    this.knownCompletions = new HashSet<String>();

    this.requestor.beginReporting();
    final String content = module.getSourceContents();
    final String wordStart = getWordStarting(content, position, 16);
    if (!wordStart.isEmpty()) {
      this.setSourceRange(position - wordStart.length(), position);
    } else {
      this.setSourceRange(position, position);
    }

    ModuleDeclaration moduleDeclaration =
        (ModuleDeclaration) new GnSourceParser().parse(module, NULL_PROBLEM_REPORTER);
    ASTNode minimalNode = AstUtil.findMinimalNode(moduleDeclaration, position, position);
    if (minimalNode instanceof SimpleReference || minimalNode instanceof GnAssignmentExpression) {
      targetElementType = IModelElement.FIELD;
    }

    if (targetElementType == 0) {
      findKeywords(wordStart.toCharArray(), GnCodeUtil.KEYWORDS, true);
    }

    try {
      module.getModelElement().accept(new IModelElementVisitor() {
        @Override
        public boolean visit(IModelElement element) {
          int type = element.getElementType();
          try {
            if (type > IModelElement.SOURCE_MODULE
                && (type != IModelElement.METHOD || isProposal(element))) {
              createProposal(wordStart, element);
            }
          } catch (ModelException e) {
            return true;
          }
          return true;
        }

        private boolean isProposal(IModelElement element) throws ModelException {
          return (((IMethod) element).getFlags() & Modifiers.AccPrivate) == 0;
        }
      });
    } catch (ModelException e) {
      if (DLTKCore.DEBUG) {
        e.printStackTrace();
      }
    } finally {
      this.requestor.endReporting();
    }
  }

  private String getWordStarting(String content, int position, int maxLen) {
    if (position <= 0 || position > content.length()) {
      return Util.EMPTY_STRING;
    }
    final int original = position;
    while (position > 0 && maxLen > 0) {
      char previous = content.charAt(position - 1);
      if ((previous == '_') || Character.isLetter(previous)) {
        --position;
        --maxLen;
      } else {
        break;
      }
    }
    return content.substring(position, original);
  }

  private void createProposal(String wordStart, IModelElement element) {
    CompletionProposal proposal = null;
    try {
      String name = element.getElementName();
      if (!wordStart.isEmpty() && (name == null || !name.startsWith(wordStart))) {
        return;
      }
      if (knownCompletions.contains(name)) {
        return;
      }
      knownCompletions.add(name);

      int relevance = RelevanceConstants.R_DEFAULT;
      relevance += computeRelevanceForCaseMatching(wordStart.toCharArray(), name);
      int elementType = element.getElementType();
      if (targetElementType != 0 && elementType != targetElementType) {
        return;
      }

      switch (elementType) {
        case IModelElement.METHOD:
          noProposal = false;
          proposal = this.createProposal(
              CompletionProposal.METHOD_DECLARATION, this.actualCompletionPosition);
          proposal.setFlags(((IMethod) element).getFlags());
          relevance += METHOD_RELEVANCE;
          break;
        case IModelElement.FIELD:
          noProposal = false;
          proposal = this.createProposal(
              CompletionProposal.FIELD_REF, this.actualCompletionPosition);
          proposal.setFlags(((IField) element).getFlags());
          relevance += FIELD_RELEVANCE;
          break;
        default:
          return;
      }
      proposal.setName(name);
      proposal.setCompletion(name);
      proposal.setModelElement(element);
      proposal.setRelevance(relevance);
      accept(proposal);
    } catch (ModelException e) {
      GnCorePlugin.log(e);
    }
  }
}
