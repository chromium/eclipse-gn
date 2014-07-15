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
package org.chromium.gnsupport.internal.ui.text;

import org.chromium.gnsupport.core.GnCodeUtil;
import org.chromium.gnsupport.core.GnNature;
import org.chromium.gnsupport.core.ast.GnFieldDeclaration;
import org.chromium.gnsupport.core.ast.GnVariableKind;
import org.chromium.gnsupport.internal.ui.GnPreferenceConstants;
import org.chromium.gnsupport.internal.ui.prefs.Messages;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.ui.editor.highlighting.AbortSemanticHighlightingException;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighterExtension;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlightingRequestor;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GnSemanticUpdateWorker extends ASTVisitor
    implements ISemanticHighlighter, ISemanticHighlighterExtension {

  @Override
  public SemanticHighlighting[] getSemanticHighlightings() {
    return new SemanticHighlighting[] {
        new GnSemanticHighlighting(GnPreferenceConstants.EDITOR_STRING_COLOR, null),
        new GnSemanticHighlighting(GnPreferenceConstants.EDITOR_VARIABLE_COLOR,
            Messages.GnLocalVariable),
        new GnSemanticHighlighting(GnPreferenceConstants.EDITOR_GLOBAL_VARIABLE_COLOR,
            Messages.GnGlobalVariable),
        new GnSemanticHighlighting(GnPreferenceConstants.EDITOR_NUMBER_COLOR, null)};
  }

  private static final String HL_STRING = GnPreferenceConstants.EDITOR_STRING_COLOR;
  private static final String HL_LOCAL_VARIABLE = GnPreferenceConstants.EDITOR_VARIABLE_COLOR;
  private static final String HL_GLOBAL_VARIABLE =
      GnPreferenceConstants.EDITOR_GLOBAL_VARIABLE_COLOR;
  private static final String HL_NUMBER = GnPreferenceConstants.EDITOR_NUMBER_COLOR;

  private final Deque<ASTNode> stack = new LinkedList<ASTNode>();
  private ISemanticHighlightingRequestor requestor;

  @Override
  public boolean visitGeneral(ASTNode node) throws Exception {
    if (node instanceof VariableReference) {
      handleVariableReference((VariableReference) node);
    } else if (node instanceof GnFieldDeclaration) {
      handleFieldDeclaration((GnFieldDeclaration) node);
    } else if (node instanceof StringLiteral) {
      requestor.addPosition(node.sourceStart(), node.sourceEnd(), HL_STRING);
    } else if (node instanceof NumericLiteral) {
      requestor.addPosition(node.sourceStart(), node.sourceEnd(), HL_NUMBER);
    } else if (node instanceof SimpleReference) {
      handleSimpleReference((SimpleReference) node);
    }
    stack.push(node);
    return true;
  }

  private void handleSimpleReference(SimpleReference node) {
    if (GnCodeUtil.isExternVariable(node.getName())) {
      requestor.addPosition(node.sourceStart(), node.sourceEnd(), HL_GLOBAL_VARIABLE);
    }
  }

  private void handleFieldDeclaration(GnFieldDeclaration node) {
    String highlight =
        node.getKind() == GnFieldDeclaration.GLOBAL ? HL_GLOBAL_VARIABLE : HL_LOCAL_VARIABLE;
    requestor.addPosition(node.sourceStart(), node.sourceEnd(), highlight);
  }

  @Override
  public void endvisitGeneral(ASTNode node) throws Exception {
    stack.pop();
  }

  private void handleVariableReference(VariableReference ref) {
    boolean isGlobal = ref.getVariableKind() == GnVariableKind.GLOBAL;

    // FIXME: Should this NOT highlight extern (is_linux et al.) var references?
    // if (isGlobal && isDeclared) {
    //   return;
    // }
    String highlight = isGlobal ? HL_GLOBAL_VARIABLE : HL_LOCAL_VARIABLE;
    requestor.addPosition(ref.sourceStart(), ref.sourceEnd(), highlight);
  }

  @Override
  public String[] getHighlightingKeys() {
    final Set<String> result = new HashSet<String>();
    for (SemanticHighlighting highlighting : getSemanticHighlightings()) {
      result.add(highlighting.getPreferenceKey());
    }
    return result.toArray(new String[result.size()]);
  }

  @Override
  public void process(IModuleSource code, ISemanticHighlightingRequestor requestor) {
    this.requestor = requestor;
    try {
      ((ModuleDeclaration) parseCode(code)).traverse(this);
    } catch (ModelException e) {
      throw new AbortSemanticHighlightingException();
    } catch (Exception e) {
      throw new AbortSemanticHighlightingException();
    }
  }

  protected IModuleDeclaration parseCode(IModuleSource code) {
    if (code instanceof ISourceModule) {
      return parseSourceModule((ISourceModule) code);
    } else {
      return parseSourceCode(code);
    }
  }

  private IModuleDeclaration parseSourceCode(IModuleSource code) {
    return SourceParserUtil.parse(code, GnNature.ID, null);
  }

  private IModuleDeclaration parseSourceModule(final ISourceModule sourceModule) {
    return SourceParserUtil.parse(sourceModule, null);
  }
}
