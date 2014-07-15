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

import org.chromium.gnsupport.core.GnCorePlugin;
import org.chromium.gnsupport.core.ast.GnArgument;
import org.chromium.gnsupport.core.ast.GnFieldDeclaration;
import org.chromium.gnsupport.internal.core.tree.AstUtil;
import org.chromium.gnsupport.internal.core.tree.GnSourceParser;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.AbstractSourceModule;

public class GnSelectionEngine extends ScriptSelectionEngine {
  @Override
  public IModelElement[] select(IModuleSource module, int selectionStart, int selectionEnd) {
    selectionEnd += 1;
    String target = null;
    if (selectionStart != selectionEnd) {
      String source = module.getSourceContents();
      if (selectionStart >= source.length()) {
        return null;
      }
      target = source.substring(selectionStart, selectionEnd);
      return selectElement((ISourceModule) module.getModelElement(), target, selectionEnd);
    } else {
      IModuleDeclaration moduleDeclaration = new GnSourceParser().parse(module, null);
      ASTNode node = AstUtil.findMinimalNode(
          (ModuleDeclaration) moduleDeclaration, selectionStart, selectionEnd);
      if (node instanceof GnArgument) {
        node = ((GnArgument) node).getActualNode();
      }
      if (node instanceof StringLiteral) {
        return selectGnFileOrAction(module, ((StringLiteral) node).getValue(), selectionEnd);
      } else if (node instanceof VariableReference) {
        target = ((VariableReference) node).getName();
      } else if (node instanceof GnFieldDeclaration) {
        target = ((GnFieldDeclaration) node).getName();
      } else if (node instanceof MethodDeclaration) {
        target = ((MethodDeclaration) node).getName();
      }
    }
    return selectElement((ISourceModule) module.getModelElement(), target, selectionEnd);
  }

  private IModelElement[] selectElement(ISourceModule sourceModule, String target, int position) {
    if (sourceModule == null) {
      return null;
    }

    IModelElement candidate = target != null ? sourceModule.getMethod(target) : sourceModule;
    try {
      if (target != null && !candidate.exists()) {
        IMethod currentFunction = findFunctionForPosition(sourceModule, position);

        functionChainLoop:
        while (currentFunction != null) {
          for (IModelElement child : currentFunction.getChildren()) {
            if (child.getElementType() != IModelElement.FIELD) {
              continue;
            }
            IField field = (IField) child;
            if (target.equals(field.getElementName())) {
              candidate = field;
              break functionChainLoop;
            }
          }
          IModelElement parent = currentFunction.getParent();
          if (parent != null && parent.getElementType() == IModelElement.METHOD) {
            currentFunction = (IMethod) parent;
          } else {
            currentFunction = null;
          }
        }
        if (!candidate.exists()) {
          candidate = sourceModule.getField(target);
        }
      }
      return candidate.exists() ? new IModelElement[] {candidate} : null;
    } catch (ModelException e) {
      GnCorePlugin.log(e);
      return null;
    }
  }

  private IModelElement[] selectGnFileOrAction(IModuleSource module, String target, int position) {
    if (target == null) {
      return null;
    }

    String action = null;
    int colonIndex = target.lastIndexOf(':');
    if (colonIndex != -1) {
      action = target.substring(colonIndex + 1);
      target = target.substring(0, colonIndex);
    }

    ISourceModule currentModule = (ISourceModule) module.getModelElement();

    try {
      IModelElement[] result = selectElement(currentModule, target, position);
      if (result == null) {
        ISourceModule sourceModule = getSourceModule(target, currentModule);
        if (sourceModule != null) {
          result = selectElement(sourceModule, action, position);
        }
      }
      return result;
    } catch (ModelException e) {
      GnCorePlugin.log(e);
      return null;
    }
  }

  private IMethod findFunctionForPosition(ISourceModule currentModule, int position)
      throws ModelException {
    IMethod[] methods = ((AbstractSourceModule) currentModule).getMethods();
    for (IMethod method : methods) {
      IMethod candidate = findFunctionForPosition(method, position);
      if (candidate != null) {
        return candidate;
      }
    }
    return null;
  }

  private IMethod findFunctionForPosition(IMethod parentMethod, int position) throws ModelException {
    if (!isPositionInside(parentMethod, position)) {
      return null;
    }
    IModelElement[] children = parentMethod.getChildren();
    for (IModelElement child : children) {
      if (child.getElementType() != IModelElement.METHOD) {
        continue;
      }
      IMethod candidate = findFunctionForPosition((IMethod) child, position);
      if (candidate != null) {
        return candidate;
      }
    }
    return parentMethod;
  }

  private boolean isPositionInside(ISourceReference child, int position) throws ModelException {
    ISourceRange sourceRange = child.getSourceRange();
    if (sourceRange.getOffset() <= position
        && position <= sourceRange.getOffset() + sourceRange.getLength()) {
      return true;
    }
    return false;
  }

  private ISourceModule getSourceModule(String target, ISourceModule currentModule)
      throws ModelException {
    IPath elementPath;
    if (target.startsWith("//")) { //$NON-NLS-1$
      elementPath =
          currentModule.getScriptProject().getPath().append(new Path(target.substring(2)));
    } else {
      if (target.isEmpty()) {
        elementPath = currentModule.getPath();
      } else {
        elementPath = currentModule.getParent().getPath().append(new Path(target));
      }
    }

    if (elementPath.segmentCount() > 1) {
      IScriptProject scriptProject = currentModule.getScriptProject();
      IScriptFolder folder = scriptProject.findScriptFolder(elementPath.removeLastSegments(1));
      if (folder != null) {
        return folder.getSourceModule(elementPath.lastSegment());
      }
    } else {
      return currentModule;
    }
    return null;
  }
}
