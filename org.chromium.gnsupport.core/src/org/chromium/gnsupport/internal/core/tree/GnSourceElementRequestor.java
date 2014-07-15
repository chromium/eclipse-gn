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

import org.chromium.gnsupport.core.GnCodeUtil;
import org.chromium.gnsupport.core.ast.GnAssignmentExpression;
import org.chromium.gnsupport.core.ast.GnFieldDeclaration;
import org.chromium.gnsupport.core.ast.GnFunctionCall;
import org.chromium.gnsupport.core.ast.GnMethodDeclaration;
import org.chromium.gnsupport.core.ast.GnMethodDeclaration.Type;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.IElementRequestor;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;

import java.util.List;

public class GnSourceElementRequestor extends SourceElementRequestVisitor {

  public GnSourceElementRequestor(IElementRequestor requestor) {
    super(requestor);
  }

  @Override
  public boolean visit(ASTNode node) throws Exception {
    super.visit(node);
    if (node instanceof GnAssignmentExpression) {
      GnAssignmentExpression assignment = (GnAssignmentExpression) node;
      Statement left = assignment.getLeft();
      if (left instanceof FieldDeclaration) {
        addFieldDeclaration(left, assignment.getRight());
      } else {
        addVariableReference(assignment.getLeft());
      }
    } else if (node instanceof GnFunctionCall) {
      GnFunctionCall call = (GnFunctionCall) node;
      String functionName = call.getName();
      if (GnCodeUtil.isImport(functionName)) {
        addImport(call);
      } else {
        addFunctionCall(call);
      }
    } else if (node instanceof VariableReference) {
      addVariableReference(node);
    }

    return true;
  }

  private void handleMethodDeclaration(MethodDeclaration decl) {
    GnMethodDeclaration declaration = (GnMethodDeclaration) decl;
    if (declaration.getType() == Type.TEMPLATE_CALL) {
      addTemplateCallIfNeeded(declaration);
    } else {
      reportMethod(declaration);
    }
  }

  private String reportMethod(GnMethodDeclaration declaration) {
    ASTNode nameNode = declaration.getRef();
    if (declaration.getType() == Type.TEMPLATE_CALL) {
      SimpleReference originalRef = declaration.getOriginalRef();
      fRequestor.acceptMethodReference(originalRef.getName(), declaration.getArguments().size(),
          originalRef.sourceStart(), originalRef.sourceEnd());
      nameNode = declaration.getFirstArgument();
    } else {
      nameNode = declaration.getRef();
    }
    String name = nameNode.toString();
    IElementRequestor.MethodInfo info = new IElementRequestor.MethodInfo();
    info.declarationStart = nameNode.start();
    info.name = name;
    info.nameSourceStart = nameNode.start();
    info.nameSourceEnd = nameNode.end() - 1;
    info.modifiers =
        declaration.getType() == Type.TEMPLATE ? Modifiers.AccPublic : Modifiers.AccPrivate;

    fRequestor.enterMethod(info);
    return name;
  }

  @Override
  public boolean endvisit(ASTNode node) throws Exception {
    return true;
  }

  private void addImport(GnFunctionCall call) {
    List<ASTNode> args = call.getArgs().getChilds();
    if (args.size() != 1) {
      return;
    }
    ASTNode name = args.get(0);
    if (false == name instanceof StringLiteral) {
      return;
    }
    IElementRequestor.ImportInfo info = new IElementRequestor.ImportInfo();
    info.containerName = "import"; //$NON-NLS-1$
    info.name = ((StringLiteral) name).getValue();
    info.sourceStart = call.sourceStart();
    info.sourceEnd = call.sourceEnd() - 1;
    fRequestor.acceptImport(info);
  }

  private void addFunctionCall(GnFunctionCall call) {
    fRequestor.acceptMethodReference(call.getName(), call.getArgs().getChilds().size(),
        call.sourceStart(), call.sourceEnd());
  }

  @SuppressWarnings("unchecked")
  private boolean addTemplateCallIfNeeded(GnMethodDeclaration declaration) {
    List<ASTNode> arguments = declaration.getArguments();
    int argCount = arguments.size();
    if (argCount != 1) {
      // FIXME: Is this check necessary?
      return false;
    }

    String name = declaration.getName();
    reportMethod(declaration);
    fRequestor.acceptMethodReference(name, argCount, declaration.sourceStart(),
        declaration.sourceEnd());
    return true;
  }

  private void addFieldDeclaration(ASTNode left, ASTNode right) {
    if (false == left instanceof GnFieldDeclaration) {
      return;
    }
    GnFieldDeclaration variable = (GnFieldDeclaration) left;
    IElementRequestor.FieldInfo info = new IElementRequestor.FieldInfo();
    info.modifiers =
        variable.getKind() == GnFieldDeclaration.GLOBAL ? Modifiers.AccGlobal
            : Modifiers.AccDefault;
    info.name = variable.getName();
    info.nameSourceEnd = variable.sourceEnd() - 1;
    info.nameSourceStart = variable.sourceStart();
    info.declarationStart = variable.sourceStart();
    fRequestor.enterField(info);
    if (right != null) {
      fRequestor.exitField(right.sourceEnd() - 1);
    } else {
      fRequestor.exitField(left.sourceEnd() - 1);
    }
  }

  private void addVariableReference(ASTNode left) {
    if (false == left instanceof VariableReference) {
      return;
    }
    VariableReference variable = (VariableReference) left;
    fRequestor.acceptFieldReference(variable.getName(), variable.sourceStart());
  }

  @Override
  public boolean visit(Expression expression) throws Exception {
    super.visit(expression);
    return visit((ASTNode) expression);
  }

  @Override
  public boolean endvisit(Expression expression) throws Exception {
    super.endvisit(expression);
    return endvisit((ASTNode) expression);
  }

  @Override
  public boolean visit(MethodDeclaration declaration) throws Exception {
    handleMethodDeclaration(declaration);
    return true;
  }

  @Override
  public boolean endvisit(MethodDeclaration declaration) throws Exception {
    fRequestor.exitMethod(declaration.sourceEnd() - 1);
    return true;
  }

  @Override
  public boolean visit(Statement statement) throws Exception {
    super.visit(statement);
    return visit((ASTNode) statement);
  }

  @Override
  public boolean endvisit(Statement statement) throws Exception {
    super.endvisit(statement);
    return endvisit((ASTNode) statement);
  }
}
